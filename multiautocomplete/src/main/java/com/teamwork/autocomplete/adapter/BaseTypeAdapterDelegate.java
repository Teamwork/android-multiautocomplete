/*
 * Copyright 2017-present Teamwork.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamwork.autocomplete.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import com.teamwork.autocomplete.filter.TokenFilter;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;
import com.teamwork.autocomplete.view.AutoCompleteViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A typed adapter for the auto complete view, used to provide a layout, data binding and filter options for a single data type.
 * It holds an {@link AutoCompleteViewBinder} and a {@link TokenFilter}.
 *
 * @author Marco Salis
 */
// @ThreadSafe
class BaseTypeAdapterDelegate<M>
        extends BaseAdapter
        implements TypeAdapterDelegate<M>, AutoCompleteTypeAdapter<M> {

    private final Executor computationExecutor;
    private final Handler mainThreadHandler;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /* LinkedHashMap preserves the insertion order so values can be iterated consistently */
    // GuardedBy("lock")
    private final LinkedHashMap<CharSequence, M> itemsMap;

    /* this is used to retain deleted items until we need them to call onTokenRemoved(CharSequence, M) */
    // Concurrent map
    private final ConcurrentMap<CharSequence, M> itemsScrapMap;

    /* this is a "snapshot" of the real data set, which might get off-sync for milliseconds after the items are set.
     * That is perfectly fine, since there is a very low chance of the user performing a search in that interval. */
    // GuardedBy("main thread")
    private final List<M> filteredItems;

    // Concurrent set
    private final Set<CharSequence> activeTokens;

    // GuardedBy("main thread")
    private final AutoCompleteViewBinder<M> viewBinder;
    // thread safe where needed
    private final TokenFilter<M> tokenFilter;

    // GuardedBy("main thread")
    private @Nullable OnTokensChangedListener<M> listener;
    // GuardedBy("main thread")
    private @Nullable CharSequence lastText;

    BaseTypeAdapterDelegate(@NonNull AutoCompleteViewBinder<M> viewBinder, @NonNull TokenFilter<M> tokenFilter) {
        this(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()), viewBinder, tokenFilter);
    }

    @VisibleForTesting BaseTypeAdapterDelegate(@NonNull Executor computationExecutor,
                                               @NonNull Handler mainThreadHandler,
                                               @NonNull AutoCompleteViewBinder<M> viewBinder,
                                               @NonNull TokenFilter<M> tokenFilter) {
        this.computationExecutor = computationExecutor;
        this.mainThreadHandler = mainThreadHandler;
        this.viewBinder = viewBinder;
        this.tokenFilter = tokenFilter;

        this.itemsMap = new LinkedHashMap<>();
        this.itemsScrapMap = new ConcurrentHashMap<>();
        this.filteredItems = new ArrayList<>();

        this.activeTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public @NonNull TokenFilter<M> getFilter() {
        return tokenFilter;
    }

    @Override
    @CallSuper
    public void setItems(@NonNull List<M> items) {
        //noinspection WrongThread
        computationExecutor.execute(() -> setItemsSync(items));
    }

    @WorkerThread
    private void setItemsSync(@NonNull List<M> items) {
        runLocked(lock.writeLock(), () -> mapItems(items));
        mainThreadHandler.post(() -> {
            filteredItems.clear();
            notifyDataSetChanged();

            // the data set has changed, we need to compute token changes even if the text is unchanged
            if (lastText != null) {
                computeTokenChangesAsync(lastText);
            }
        });
    }

    @WorkerThread
    private void mapItems(@NonNull List<M> items) {
        itemsScrapMap.clear();
        itemsScrapMap.putAll(itemsMap);
        itemsMap.clear();

        TokenFilter<M> filter = getFilter();
        for (M item : items) {
            itemsMap.put(filter.toTokenString(item), item);
        }
    }

    @Override
    public final void setFilteredItems(@NonNull List<M> filteredItems) {
        this.filteredItems.clear();
        this.filteredItems.addAll(filteredItems);
    }

    @Override
    public final int getCount() {
        return filteredItems.size();
    }

    @Override
    public final @NonNull M getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return viewBinder.getItemId(getItem(position));
    }

    @Override
    public @NonNull View getView(@NonNull LayoutInflater inflater,
                                 int position,
                                 @Nullable View convertView,
                                 @NonNull ViewGroup parent,
                                 @Nullable CharSequence constraint) {
        if (convertView == null) { // inflate view and create view holder
            convertView = inflater.inflate(viewBinder.getItemLayoutId(), parent, false);
            convertView.setTag(viewBinder.getViewHolder(convertView));
        }
        AutoCompleteViewHolder tag = (AutoCompleteViewHolder) convertView.getTag();
        viewBinder.bindData(tag, getItem(position), constraint);
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    @WorkerThread
    public final @NonNull List<M> performFiltering(@NonNull CharSequence constraint) {
        List<M> items = new ArrayList<>();
        // defensive copy of the items to avoid locking for the whole filtering process
        runLocked(lock.readLock(), () -> items.addAll(this.itemsMap.values()));

        return getFilter().performFiltering(constraint, items);
    }

    @Override
    public final void setOnTokensChangedListener(@Nullable OnTokensChangedListener<M> listener) {
        if (getFilter().getValidTokenPattern() == null) {
            throw new IllegalStateException("The token filter getValidTokenPattern() must return a valid Pattern!");
        }
        this.listener = listener;
    }

    @Override
    public final void onTextChanged(@NonNull CharSequence text) {
        lastText = text;
        computeTokenChangesAsync(text);
    }

    @MainThread
    private void computeTokenChangesAsync(@NonNull CharSequence text) {
        Pattern pattern = getFilter().getValidTokenPattern();
        if (pattern == null || listener == null) {
            return;
        }
        computationExecutor.execute(() -> computeTokenChanges(text, pattern));
    }

    @WorkerThread
    private void computeTokenChanges(CharSequence text, Pattern pattern) {
        Set<CharSequence> activeTokens = new HashSet<>();
        Matcher matcher = pattern.matcher(text);

        runLocked(lock.readLock(), () -> {
            while (matcher.find()) {
                String token = text.subSequence(matcher.start(), matcher.end()).toString().trim();
                if (itemsMap.containsKey(token)) { // the token matches an item identifier
                    activeTokens.add(token);
                }
            }
        });

        // compute difference and update active tokens
        Collection<CharSequence> addedTokens = getAddedTokens(this.activeTokens, activeTokens);
        Collection<CharSequence> removedTokens = getRemovedTokens(this.activeTokens, activeTokens);
        this.activeTokens.addAll(addedTokens);
        this.activeTokens.removeAll(removedTokens);

        // notify listeners for added/removed tokens on the main thread
        postNotifyTokenChanges(addedTokens, removedTokens);
    }

    private void postNotifyTokenChanges(Collection<CharSequence> addedTokens, Collection<CharSequence> removedTokens) {
        mainThreadHandler.post(() -> notifyTokenChanges(addedTokens, removedTokens));
    }

    @MainThread
    private void notifyTokenChanges(Collection<CharSequence> addedTokens, Collection<CharSequence> removedTokens) {
        if (listener == null) {
            return;
        }
        runLocked(lock.readLock(), () -> {
            notifyRemovedTokens(listener, removedTokens);
            notifyAddedTokens(listener, addedTokens);
        });
    }

    @MainThread
    private void notifyRemovedTokens(@NonNull OnTokensChangedListener<M> listener, @NonNull Collection<CharSequence> removedTokens) {
        for (CharSequence token : removedTokens) {
            M removedTokenItem = itemsMap.get(token);
            if (removedTokenItem != null) {
                listener.onTokenRemoved(token, removedTokenItem);
            } else {
                // retry from the scrap map: the item could have just been removed
                removedTokenItem = itemsScrapMap.get(token);
                if (removedTokenItem != null) {
                    listener.onTokenRemoved(token, removedTokenItem);
                }
            }
        }
    }

    @MainThread
    private void notifyAddedTokens(@NonNull OnTokensChangedListener<M> listener, @NonNull Collection<CharSequence> addedTokens) {
        for (CharSequence token : addedTokens) {
            M addedTokenItem = itemsMap.get(token);
            if (addedTokenItem != null) listener.onTokenAdded(token, addedTokenItem);
        }
    }

    @VisibleForTesting LinkedHashMap<CharSequence, M> getItemsMap() {
        return itemsMap;
    }

    @VisibleForTesting
    static Collection<CharSequence> getAddedTokens(Set<CharSequence> existingSet, Set<CharSequence> newSet) {
        Set<CharSequence> added = new HashSet<>(newSet);
        added.removeAll(existingSet);
        return added;
    }

    @VisibleForTesting
    static Collection<CharSequence> getRemovedTokens(Set<CharSequence> existingSet, Set<CharSequence> newSet) {
        Set<CharSequence> removed = new HashSet<>(existingSet);
        removed.removeAll(newSet);
        return removed;
    }

    private static void runLocked(@NonNull Lock lock, @NonNull Runnable action) {
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

}

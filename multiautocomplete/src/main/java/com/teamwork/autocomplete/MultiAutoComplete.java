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

package com.teamwork.autocomplete;

import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;
import com.teamwork.autocomplete.adapter.TypeAdapterDelegate;
import com.teamwork.autocomplete.filter.BaseTokenFilter;
import com.teamwork.autocomplete.filter.HandleTokenFilter;
import com.teamwork.autocomplete.filter.SimpleTokenFilter;
import com.teamwork.autocomplete.filter.TokenFilter;
import com.teamwork.autocomplete.model.SimpleItem;
import com.teamwork.autocomplete.tokenizer.PrefixTokenizer;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;
import com.teamwork.autocomplete.view.MultiAutoCompleteEditText;
import com.teamwork.autocomplete.view.SimpleItemViewBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * The main <b>MultiAutoComplete</b> component, it manages the type adapters and holds a reference to the managed {@link
 * MultiAutoCompleteTextView}. It also provides lifecycle methods to attach/detach the view itself.
 * <p>
 * The managed {@link MultiAutoCompleteTextView} can be styled and used as normal.
 * Once configured, the library will take care of calling {@link MultiAutoCompleteTextView#setTokenizer(MultiAutoCompleteTextView.Tokenizer)}
 * and {@link MultiAutoCompleteTextView#setAdapter(ListAdapter)} and present the data for you when there is a token match.
 * <p>
 * It holds an "adapter of adapters" internally, with which the component can handle multiple data types in the same instance and decide
 * which adapter to use for filtering depending on what kind of token is being typed in.
 * <p>
 * To create an instance use one of the static factory methods in {@link MultiAutoComplete.Build} or, for full customization, use the
 * {@link Builder} itself.
 *
 * @author Marco Salis
 */
public interface MultiAutoComplete {

    /**
     * Call this when the view gets attached to the component using {@link MultiAutoComplete}. This is usually done in
     * <code>Activity.onCreate(Bundle)</code> or <code>Fragment.onViewCreated(View, Bundle)</code>.
     *
     * @param view The {@link MultiAutoCompleteEditText} view that this {@link MultiAutoComplete} will be managing.
     */
    void onViewAttached(@NonNull MultiAutoCompleteEditText view);

    /**
     * Call this when the view gets detached to the component using {@link MultiAutoComplete}, to avoid memory leaks or usage of a view that
     * is no longer on screen. This is usually done in <code>Activity.onDestroy()</code> or <code>Fragment.onDestroyView()</code>.
     */
    void onViewDetached();


    /**
     * {@link MultiAutoComplete} concrete implementation builder component.
     */
    class Builder {

        private final List<TypeAdapterDelegate<?>> typeAdapters = new ArrayList<>();

        private MultiAutoCompleteTextView.Tokenizer tokenizer;
        private @Nullable
        Delayer delayer;

        /**
         * Set the {@link MultiAutoCompleteTextView.Tokenizer} for the {@link MultiAutoComplete} being built.
         *
         * @param tokenizer The tokenizer.
         * @return The builder for chaining calls.
         * @see MultiAutoCompleteTextView.Tokenizer
         */
        public Builder tokenizer(@NonNull MultiAutoCompleteTextView.Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
            return this;
        }

        /**
         * Set the {@link Delayer} to be used to delay filtering on this {@link MultiAutoComplete}.
         *
         * @param delayer The delayer to use.
         * @return The builder for chaining calls.
         * @see Delayer
         * @see android.widget.Filter
         */
        public Builder delayer(@NonNull Delayer delayer) {
            this.delayer = delayer;
            return this;
        }

        /**
         * Add a {@link AutoCompleteTypeAdapter} to the {@link MultiAutoComplete} being built.
         * The type adapter must be built with one of the {@link AutoCompleteTypeAdapter.Build} factory methods.
         * <p>
         * <b>Note:</b> the insertion order will determine which type adapter will handle a specific token.
         * If there are multiple adapters that support a specific token format, only the first matching will be used.
         *
         * @param typeAdapter The type adapter to add.
         * @return The builder for chaining calls.
         */
        public Builder addTypeAdapter(@NonNull AutoCompleteTypeAdapter<?> typeAdapter) {
            if (!(typeAdapter instanceof TypeAdapterDelegate)) {
                throw new IllegalArgumentException("Type adapter must implement TypeAdapterDelegate!");
            }
            typeAdapters.add((TypeAdapterDelegate<?>) typeAdapter);
            return this;
        }

        /**
         * Build the configured instance of this {@link MultiAutoComplete}.
         */
        public @NonNull MultiAutoComplete build() {
            return new MultiAutoCompleteImpl(tokenizer, typeAdapters, delayer);
        }
    }


    /**
     * Static factory helper methods to construct an instance of {@link MultiAutoComplete} with default parameters.
     * <p>
     * The {@link TokenFilter} implementations set by this methods will match the current constraint with the value returned by the {@link
     * #toString()} method of the passed items.
     *
     * @see BaseTokenFilter
     */
    class Build {

        /**
         * Build an instance of {@link MultiAutoComplete} from the passed list of {@link SimpleItem}s with the default UI.
         * The auto complete will be triggered at every space-prefixed character, on each comma-separated value.
         * <p>
         * Use this method to implement if no handle, customization of the layout, data type or filtering is required.
         *
         * @param items The List of items to display (only the text will be shown).
         * @return The built {@link MultiAutoComplete} instance.
         * @see SimpleItem
         * @see SimpleItemViewBinder
         * @see SimpleTokenFilter
         */
        @NonNull
        public static MultiAutoComplete from(@NonNull List<SimpleItem> items) {
            AutoCompleteTypeAdapter<SimpleItem> typeAdapter =
                    AutoCompleteTypeAdapter.Build.from(new SimpleItemViewBinder(), new SimpleTokenFilter<>());
            typeAdapter.setItems(items);
            return new Builder()
                    .tokenizer(new MultiAutoCompleteTextView.CommaTokenizer())
                    .addTypeAdapter(typeAdapter)
                    .build();
        }

        /**
         * Build an instance of {@link MultiAutoComplete} from the passed list of {@link SimpleItem}s with the default UI.
         * The auto complete will be triggered by the passed char handle.
         * <p>
         * Use this method to implement if no customization of the layout, data type or filtering is required.
         *
         * @param handle The character handle that will trigger the auto complete drop down.
         * @param items  The List of items to display (only the text will be shown).
         * @return The built {@link MultiAutoComplete} instance.
         * @see SimpleItem
         * @see SimpleItemViewBinder
         */
        @NonNull
        public static MultiAutoComplete from(char handle, @NonNull List<SimpleItem> items) {
            return from(handle, new SimpleItemViewBinder(), items);
        }

        /**
         * Build an instance of {@link MultiAutoComplete} from the passed list of item models and a layout provided by a custom view binder.
         * The auto complete will be triggered by the passed char handle.
         * <p>
         * Use this method to implement if no customization of the filtering and tokenizer is required.
         *
         * @param handle     The character handle that will trigger the auto complete drop down.
         * @param viewBinder An {@link AutoCompleteViewBinder} to provide a custom layout and data binding.
         * @param items      The List of items to use as data set.
         * @return The built {@link MultiAutoComplete} instance.
         */
        @NonNull
        public static <M> MultiAutoComplete from(char handle, @NonNull AutoCompleteViewBinder<M> viewBinder, @NonNull List<M> items) {
            AutoCompleteTypeAdapter<M> typeAdapter =
                    AutoCompleteTypeAdapter.Build.from(viewBinder, new HandleTokenFilter<>(handle));
            typeAdapter.setItems(items);
            return new Builder()
                    .tokenizer(new PrefixTokenizer(handle))
                    .addTypeAdapter(typeAdapter)
                    .build();
        }
    }


    /**
     * Delegate interface for the hidden {@link Filter}<b>$Delayer</b> interface, used to delay the filtering of the text constraint on the
     * {@link MultiAutoCompleteTextView}.
     * <p>
     * <b>Important note:</b> {@link MultiAutoComplete} uses reflection internally to access the state of the Android {@link Filter} and
     * set the hidden delayer. Its behaviour is not guaranteed for future Android versions, and it will fail silently in some scenarios (an
     * error message is logged into the LogCat console when that happens).
     * <p>
     * Implementations must be thread safe.
     */
    interface Delayer {

        /**
         * Get the delay with which the filtering will be performed after the constraint change from the user.
         *
         * @param constraint The constraint passed to {@link Filter#filter(CharSequence)}.
         * @return The delay with which the filtering operation on the {@link MultiAutoCompleteEditText} will occur in milliseconds.
         */
        long getPostingDelay(CharSequence constraint);
    }

}

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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teamwork.autocomplete.MultiAutoComplete;
import com.teamwork.autocomplete.filter.TokenFilter;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;

import java.util.List;

/**
 * Interface for a typed adapter for the auto complete view.
 * <p>
 * Its purpose is to allow client code to set the data set for the type it represents, and optionally listen to token changes within the
 * user-typed text with an {@link OnTokensChangedListener}.
 * <p>
 * Use the static methods in the {@link Build} class to construct an instance.
 *
 * @author Marco Salis
 */
public interface AutoCompleteTypeAdapter<Model> {

    /**
     * Set a list of items for this type adapter.
     * Note that the list will be processed and indexed asynchronously, and used for the next filtering.
     * <p>
     * This method can be called to replace an existing data set, even after the {@link MultiAutoComplete} has been created.
     * The data set change will be notified and the filter updated accordingly.
     *
     * @param items The List of items to set into the type adapter.
     */
    @MainThread
    void setItems(@NonNull List<Model> items);

    /**
     * Set a {@link OnTokensChangedListener} to listen for changes in the matched tokens in the user-typed text.
     * <p>
     * <b>Note:</b> this method can only be called if the token filter returned by {@link TokenFilter#getValidTokenPattern()} is not null,
     * since it's required to detect the token matches within the user typed string.
     *
     * @param listener The listener, or null to reset it.
     * @throws IllegalStateException when the adapter's {@link TokenFilter#getValidTokenPattern()} is null.
     * @see OnTokensChangedListener
     */
    @MainThread
    void setOnTokensChangedListener(@Nullable OnTokensChangedListener<Model> listener);


    /**
     * Static factory methods to construct an instance of {@link AutoCompleteTypeAdapter}.
     */
    class Build {

        @NonNull
        public static <M> AutoCompleteTypeAdapter<M> from(@NonNull AutoCompleteViewBinder<M> binder, @NonNull TokenFilter<M> filter) {
            if (binder == null || filter == null) {
                throw new IllegalArgumentException("View binder and token filter must not be null");
            }
            return new BaseTypeAdapterDelegate<>(binder, filter);
        }
    }

}

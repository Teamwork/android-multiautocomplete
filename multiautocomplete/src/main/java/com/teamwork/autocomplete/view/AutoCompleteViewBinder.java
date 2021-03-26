/*
 * Copyright 2017-present Teamwork.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamwork.autocomplete.view;

import android.view.View;
import android.widget.BaseAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;

/**
 * View binder interface that an {@link AutoCompleteTypeAdapter}s use to allow the auto complete component to render the desired UI into the
 * auto complete drop down.
 *
 * @author Marco Salis
 * @see AutoCompleteTypeAdapter
 */
@MainThread
public interface AutoCompleteViewBinder<Model> {

    /**
     * Return a numeric identifier for the passed model. The returned value must be unique and "stable" (see {@link
     * BaseAdapter#hasStableIds()}.
     *
     * @param item The item to return the identifier of.
     * @return A long identifier for the item.
     */
    long getItemId(@NonNull Model item);

    /**
     * Return the layout resource identifier to be used in the auto complete adapter. This will then be used by the type adapter {@link
     * AutoCompleteViewHolder} and data binder.
     */
    @LayoutRes int getItemLayoutId();

    /**
     * Return a layout-specific implementation of {@link AutoCompleteViewHolder} for the adapter to access sub-views faster, following
     * Android's suggested view binder pattern.
     *
     * @param view The view to create a view holder.
     * @return The {@link AutoCompleteViewHolder} instance.
     */
    @NonNull AutoCompleteViewHolder getViewHolder(@NonNull View view);

    /**
     * Bind the data for the passed model to the view held from the {@link AutoCompleteViewHolder} implementation, which must be cast to the
     * concrete type.
     *
     * @param viewHolder The {@link AutoCompleteViewHolder} returned by the adapter.
     * @param item       The item whose data must be bound to the view.
     * @param constraint The current text filter constraint, if any. This can be used to manipulate the UI for the current item if it
     *                   matches the constraint.
     */
    void bindData(@NonNull AutoCompleteViewHolder viewHolder, @NonNull Model item, @Nullable CharSequence constraint);

}

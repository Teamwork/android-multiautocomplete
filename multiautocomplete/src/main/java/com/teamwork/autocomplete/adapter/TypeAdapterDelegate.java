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

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.teamwork.autocomplete.filter.TokenFilter;

import java.util.List;

/**
 * Internal use interface for a type adapter.
 * <p>
 * Used by {@link com.teamwork.autocomplete.MultiAutoComplete}'s main adapter to delegate filtering to one of the registered type adapters.
 *
 * @author Marco Salis
 */
public interface TypeAdapterDelegate<M> {

    //region adapter items

    /**
     * @see android.widget.BaseAdapter#registerDataSetObserver(DataSetObserver)
     */
    void registerDataSetObserver(@NonNull DataSetObserver observer);

    /**
     * @see android.widget.BaseAdapter#unregisterDataSetObserver(DataSetObserver)
     */
    void unregisterDataSetObserver(@NonNull DataSetObserver observer);

    /**
     * Set the list of currently filtered items into the type adapter.
     * This will be called by the <code>Filter.performFiltering(CharSequence)</code> method.
     *
     * @param items A list of items to set in the adapter.
     */
    @MainThread
    void setFilteredItems(@NonNull List<M> items);

    /**
     * Get the current count of filtered items in the type adapter.
     */
    @MainThread
    int getCount();

    /**
     * Return the item at the passed position in the type adapter.
     *
     * @param position The position in the adapter.
     * @return The item.
     */
    @MainThread
    @NonNull M getItem(int position);

    /**
     * Return the item identifier as specified by {@link android.widget.BaseAdapter#getItemId(int)}.
     *
     * @param position The position of the item.
     * @return The numeric identifier of the item at the passed position.
     */
    @MainThread
    long getItemId(int position);

    //endregion


    //region layout and view binding

    /**
     * Retrieve the view for the given position using the inflater or the view holder associated to the view itself, and binds the data to
     * it.
     *
     * @param inflater    The {@link LayoutInflater} for inflating the view.
     * @param position    The adapter position of the view to retrieve.
     * @param convertView The convert view passed by the adapter.
     * @param parent      The view parent for view inflation.
     * @param constraint  The current text constraint to pass the view binder.
     * @return The view for the element at the passed position.
     * @see android.widget.BaseAdapter#getView(int, View, ViewGroup)
     */
    @NonNull View getView(@NonNull LayoutInflater inflater,
                          int position,
                          @Nullable View convertView,
                          @NonNull ViewGroup parent,
                          @Nullable CharSequence constraint);

    //endregion


    //region tokens, filters and constraints

    /**
     * Return the typed {@link TokenFilter} for this type adapter.
     */
    @NonNull TokenFilter<M> getFilter();

    /**
     * Called by <code>Filter#performFiltering(CharSequence)</code> off the main thread to filter the list in this type adapter based on the
     * passed constraint.
     *
     * @param constraint A text constraint to filter the adapter elements.
     * @return A List of filtered items from this adapter.
     */
    @WorkerThread
    @NonNull List<M> performFiltering(@NonNull CharSequence constraint);

    /**
     * Called by the {@link android.widget.MultiAutoCompleteTextView} when the text typed by the user has changed.
     *
     * @param text The text currently present in the editable text view.
     */
    @MainThread
    void onTextChanged(@NonNull CharSequence text);

    //endregion

}

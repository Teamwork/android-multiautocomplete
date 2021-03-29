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

package com.teamwork.autocomplete.filter;

import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;
import com.teamwork.autocomplete.util.ConstraintComparator;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Interface for components that implement the filtering feature of {@link AutoCompleteTypeAdapter}s.
 * <p>
 * Includes methods to detect if a token is supported, filter a list of items coming from the type adapter and provide a Comparator to sort
 * the results.
 * <p>
 * Implementations should be thread safe.
 *
 * @author Marco Salis
 */
public interface TokenFilter<M> {

    /**
     * Check whether the provided token is supported by the filter.
     * When true is returned, the {@link TokenFilter} can perform filtering and recognize the items that are valid for the other auto
     * completion features.
     *
     * @param token The token string.
     * @return true if the token text is supported, false otherwise.
     */
    boolean supportsToken(@NonNull CharSequence token);

    /**
     * Remove the token handle from the passed token if present.
     *
     * @param token The token string to remove the handle from.
     * @return The token without the handle if found.
     */
    @NonNull CharSequence stripHandle(@NonNull CharSequence token);

    /**
     * Return the token string representation of the passed item.
     * Note that this could be different from the {@link #toString()} representation, for example to add a handle when supported.
     * <p>
     * This is used by the {@link Filter#convertResultToString(Object)} method for user suggestions and auto complete
     * and by the {@link AutoCompleteTypeAdapter} to match token strings with the user-typed characters.
     *
     * @param item The item whose token string representation is needed.
     * @return The token string.
     */
    @NonNull CharSequence toTokenString(@NonNull M item);

    /**
     * Return an optional "valid token" pattern that matches full tokens which can be associated with the items data.
     * This can be used by the {@link AutoCompleteTypeAdapter} to detect tokens and notify when a token is added/removed by the user in the
     * auto complete edit text view.
     * <p>
     * For performance reasons, the same instance of {@link Pattern} should be returned at every method call.
     *
     * @return The "valid token" pattern for this filter.
     */
    @Nullable Pattern getValidTokenPattern();

    /**
     * Perform the actual items filtering, delegating to the filter the decision whether each items matches the passed text constraint.
     * <p>
     * This method must always be executed from a worker thread.
     *
     * @param constraint The current text constraint to be used for matching.
     * @param items      The list of items to iterate and filter (the list shouldn't be modified by implementations).
     * @return A new List containing the items that have matched the constraint.
     */
    @WorkerThread
    @NonNull List<M> performFiltering(@NonNull CharSequence constraint, @NonNull List<M> items);

    /**
     * Return an optional {@link ConstraintComparator} to sort the filtered items based on the current text constraint.
     * <p>
     * Note that it's more efficient, performance-wise, to just sort the initial list set and then pass it to
     * {@link AutoCompleteTypeAdapter#setItems(List)} and return null from this method, if the ordering never depends on the text
     * constraint.
     *
     * @return The {@link ConstraintComparator} instance, can be reused across calls if possible, or null if the filtered results can use
     * the original items list order.
     */
    @WorkerThread
    @Nullable ConstraintComparator<M> getConstraintComparator();

}

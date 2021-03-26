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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.teamwork.autocomplete.util.ConstraintComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Base implementation of a {@link TokenFilter} which:
 * <ul>
 * <li>Matches items by calling their {@link #toString()} method and checking if they contain the constraint</li>
 * <li>Does not provide a {@link ConstraintComparator}</li>
 * <li>Does not support pattern matching with {@link #getValidTokenPattern()}</li>
 * </ul>
 * <p>
 * Extend the class to alter the default behaviour or provide additional functionality within a custom type adapter.
 *
 * @author Marco Salis
 * @see HandleTokenFilter
 * @see SimpleTokenFilter
 */
public abstract class BaseTokenFilter<M> implements TokenFilter<M> {

    @Override
    public @NonNull CharSequence toTokenString(@NonNull M item) {
        return item.toString();
    }

    @Override
    @WorkerThread
    public final @NonNull List<M> performFiltering(@NonNull CharSequence constraint, @NonNull List<M> items) {
        List<M> filteredItems = new ArrayList<>();

        for (M item : items) {
            if (matchesConstraint(item, constraint)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    @WorkerThread
    protected boolean matchesConstraint(@NonNull M item, @NonNull CharSequence constraint) {
        return item.toString().toLowerCase().contains(constraint.toString().toLowerCase());
    }

    @Override
    public @Nullable ConstraintComparator<M> getConstraintComparator() {
        return null;
    }

    @Override
    public @Nullable Pattern getValidTokenPattern() {
        return null;
    }

}

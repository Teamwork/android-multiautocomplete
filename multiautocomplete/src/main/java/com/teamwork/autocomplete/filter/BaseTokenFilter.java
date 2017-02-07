package com.teamwork.autocomplete.filter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

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
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

package com.teamwork.autocomplete.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.util.Comparator;

/**
 * Abstract implementation of {@link Comparator} that supports an additional text constraint to implement custom ordering rules when
 * filtering objects based on a user-entered input.
 * <p>
 * Extend this class and implement your {@link #compare(CharSequence, Object, Object)} method as if you were using a standard Comparator.
 * <p>
 * This class is thread safe, and subclasses should maintain this property.
 *
 * @author Marco Salis
 */
public abstract class ConstraintComparator<T> implements Comparator<T> {

    // GuardedBy("this")
    private CharSequence constraint;

    public synchronized final @Nullable CharSequence getConstraint() {
        return constraint;
    }

    public synchronized final void setConstraint(@Nullable CharSequence constraint) {
        this.constraint = constraint;
    }

    /**
     * Return whether the currently set constraint requires sorting. The default implementation returns true when the constraint is not null
     * or empty.
     */
    public synchronized boolean shouldCompare() {
        return !TextUtils.isEmpty(constraint);
    }

    @Override
    @WorkerThread
    public synchronized final int compare(@NonNull T o1, @NonNull T o2) {
        return compare(constraint, o1, o2);
    }

    /**
     * Compares the two objects for order, providing the current text constraint for filtering and applying custom rules to the ordering.
     *
     * @param constraint The constraint text.
     * @param o1         the first object to be compared.
     * @param o2         the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     * @see Comparator#compare(Object, Object)
     */
    @WorkerThread
    public abstract int compare(@Nullable CharSequence constraint, @NonNull T o1, @NonNull T o2);

}

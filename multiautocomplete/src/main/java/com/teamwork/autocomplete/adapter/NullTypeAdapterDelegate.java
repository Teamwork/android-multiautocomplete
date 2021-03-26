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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teamwork.autocomplete.filter.BaseTokenFilter;
import com.teamwork.autocomplete.filter.TokenFilter;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;
import com.teamwork.autocomplete.view.AutoCompleteViewHolder;

/**
 * 'Null object' pattern implementation of an {@link TypeAdapterDelegate} that performs no operations.
 *
 * @author Marco Salis
 */
public class NullTypeAdapterDelegate extends BaseTypeAdapterDelegate<Object> {

    @SuppressWarnings("unused")
    private final NullViewBinder viewBinder = new NullViewBinder();
    private final NullTokenFilter filter = new NullTokenFilter();

    public NullTypeAdapterDelegate() {
        super(new NullViewBinder(), new NullTokenFilter());
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public @NonNull TokenFilter<Object> getFilter() {
        return filter;
    }


    private static class NullViewBinder implements AutoCompleteViewBinder<Object> {
        @Override public long getItemId(@NonNull Object item) {
            return 0;
        }

        @Override
        public int getItemLayoutId() {
            return 0;
        }

        @Override
        public @NonNull AutoCompleteViewHolder getViewHolder(@NonNull View view) {
            return null;
        }

        @Override
        public void bindData(@NonNull AutoCompleteViewHolder viewHolder, @NonNull Object item, @Nullable CharSequence constraint) {
        }
    }


    private static class NullTokenFilter extends BaseTokenFilter<Object> {

        @Override
        protected boolean matchesConstraint(@NonNull Object item, @NonNull CharSequence constraint) {
            return false;
        }

        @Override
        public boolean supportsToken(@NonNull CharSequence token) {
            return false;
        }

        @Override
        public @NonNull CharSequence stripHandle(@NonNull CharSequence token) {
            return token;
        }
    }

}

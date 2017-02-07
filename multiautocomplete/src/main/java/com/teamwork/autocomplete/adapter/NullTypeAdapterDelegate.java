package com.teamwork.autocomplete.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.teamwork.autocomplete.filter.BaseTokenFilter;
import com.teamwork.autocomplete.filter.TokenFilter;
import com.teamwork.autocomplete.view.AutoCompleteViewHolder;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;

/**
 * 'Null object' pattern implementation of an {@link TypeAdapterDelegate} that performs no operations.
 */
public class NullTypeAdapterDelegate extends BaseTypeAdapterDelegate<Object> {

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
            //noinspection ConstantConditions
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
package com.teamwork.autocomplete.filter;

import android.support.annotation.NonNull;
import android.widget.MultiAutoCompleteTextView;

/**
 * Simple implementation of a {@link BaseTokenFilter} which supports all tokens and does not use handles.
 * <p>
 * Can be used to trigger filtering on all strings in combination with a {@link MultiAutoCompleteTextView.CommaTokenizer}
 * or another custom tokenizer which uses a different separator.
 */
public class SimpleTokenFilter<M> extends BaseTokenFilter<M> {

    @Override
    public boolean supportsToken(@NonNull CharSequence token) {
        return true;
    }

    @Override
    public @NonNull CharSequence stripHandle(@NonNull CharSequence token) {
        return token;
    }

}
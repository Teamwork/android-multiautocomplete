package com.teamwork.autocomplete.filter;

import android.support.annotation.NonNull;

import com.teamwork.autocomplete.util.AutoCompleteUtils;

/**
 * {@link BaseTokenFilter} that supports tokens starting with an initial handle (like @token or #token).
 */
public class HandleTokenFilter<M> extends BaseTokenFilter<M> {

    protected final char handleChar;

    public HandleTokenFilter(char handleChar) {
        this.handleChar = handleChar;
    }

    @Override
    public @NonNull CharSequence stripHandle(@NonNull CharSequence token) {
        return AutoCompleteUtils.stripPrefixHandle(handleChar, token);
    }

    @Override
    public boolean supportsToken(@NonNull CharSequence token) {
        return AutoCompleteUtils.hasPrefixHandle(handleChar, token);
    }

}
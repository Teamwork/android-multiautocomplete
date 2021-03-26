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

import com.teamwork.autocomplete.util.AutoCompleteUtils;

import java.util.Locale;

/**
 * {@link BaseTokenFilter} that supports tokens starting with an initial handle (like @token or #token).
 *
 * @author Marco Salis
 */
public class HandleTokenFilter<M> extends BaseTokenFilter<M> {

    protected final char handleChar;

    public HandleTokenFilter(char handleChar) {
        this.handleChar = handleChar;
    }

    @Override
    public @NonNull CharSequence toTokenString(@NonNull M item) {
        return String.format(Locale.US, "%c%s", handleChar, item.toString());
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

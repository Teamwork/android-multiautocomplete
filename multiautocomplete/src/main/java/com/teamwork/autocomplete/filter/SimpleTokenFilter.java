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

import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;

/**
 * Simple implementation of a {@link BaseTokenFilter} which supports all tokens and does not use handles.
 * <p>
 * Can be used to trigger filtering on all strings in combination with a {@link MultiAutoCompleteTextView.CommaTokenizer} or another custom
 * tokenizer which uses a different separator.
 *
 * @author Marco Salis
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

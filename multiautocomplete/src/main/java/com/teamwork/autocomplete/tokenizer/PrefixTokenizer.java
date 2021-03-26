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

package com.teamwork.autocomplete.tokenizer;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * Implementation of {@link android.widget.MultiAutoCompleteTextView.Tokenizer} that finds tokens which are prefixed by one of the passed
 * chars. This is useful, for example, to manage autocomplete for user handles ('@' prefix).
 * <p>
 * Only one separator is supported per tokenizer. The default is the space character, to change override {@link #getDefaultSeparator}.
 * <p>
 * This implementation does not filter on the token characters (i.e. '@han:dle;' is a valid token), but only terminates it when a separator
 * is found.
 *
 * @author Marco Salis
 */
public class PrefixTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    private static final char NEW_LINE = '\n';

    private final char[] prefixes;

    public PrefixTokenizer(char prefix) {
        prefixes = new char[]{prefix};
    }

    public PrefixTokenizer(char... prefixes) {
        this.prefixes = Arrays.copyOf(prefixes, prefixes.length);
    }

    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        // base case: empty string
        if (text.length() == 0) {
            return 0;
        }

        // iterate back until we find the prefix
        char separator = getDefaultSeparator();
        for (int i = cursor - 1; i >= 0; i--) {
            if (matchesPrefix(text, i)) {
                char charAt; // consider separator or new line as a token start
                if (i == 0 || (charAt = text.charAt(i - 1)) == separator || charAt == NEW_LINE) {
                    return i;
                }
            }
        }

        return cursor;
    }

    private boolean matchesPrefix(@NonNull CharSequence text, int index) {
        for (char prefix : prefixes) {
            if (text.charAt(index) == prefix) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        // base case: empty string
        int length = text.length();
        if (length == 0) {
            return 0;
        }

        // iterate forward until we get a space, a new line or the end of the string
        char separator = getDefaultSeparator();
        for (int i = cursor; i < length; i++) {
            char charAt = text.charAt(i);
            if (charAt == separator || charAt == NEW_LINE) {
                return i == 0 ? 0 : i - 1;
            }
        }

        return length;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();
        char separator = getDefaultSeparator();

        // the text is empty or already has a trailing space
        if (i == 0 || (i > 0 && text.charAt(i - 1) == separator)) {
            return text;
        }

        // append a trailing space to the string
        if (text instanceof Spanned) {
            SpannableString sp = new SpannableString(text + String.valueOf(separator));
            TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
            return sp;
        } else {
            return text + String.valueOf(separator);
        }
    }

    protected char getDefaultSeparator() {
        return ' ';
    }

}

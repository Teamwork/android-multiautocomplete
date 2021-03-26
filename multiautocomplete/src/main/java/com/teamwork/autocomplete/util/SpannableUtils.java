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

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

/**
 * Utility methods to manipulate {@link Spannable}s.
 */
public class SpannableUtils {

    private SpannableUtils() { // no instantiation needed
    }

    /**
     * Find the first (case insensitive) match of the substring in the passed text and set it to bold.
     *
     * @param text    The string to find the passed substring into.
     * @param subText Substring to be set to bold.
     * @return a {@link Spannable}.
     */
    public static Spannable setBoldSubText(@NonNull CharSequence text, @NonNull CharSequence subText) {
        String textLowerCase = text.toString().toLowerCase();
        String subTextLowerCase = subText.toString().toLowerCase();

        int start = textLowerCase.indexOf(subTextLowerCase);
        int end = start + subText.length();

        SpannableStringBuilder span = new SpannableStringBuilder(text);
        if (start >= 0 && end > start) {
            span.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

}

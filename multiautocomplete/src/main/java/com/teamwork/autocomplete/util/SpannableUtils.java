package com.teamwork.autocomplete.util;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

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
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
import android.widget.EditText;

import androidx.annotation.Nullable;

/**
 * Helper static methods for AutoComplete features.
 *
 * @author Marco Salis
 */
public class AutoCompleteUtils {

    private AutoCompleteUtils() { // no instantiation needed
    }

    /**
     * Check whether a token starts with the passed handle.
     *
     * @param handle The handle char to detect at the first character.
     * @param token  A CharSequence to find the handle from.
     * @return true if the text token starts with the passed handle, false otherwise.
     */
    public static boolean hasPrefixHandle(char handle, @Nullable CharSequence token) {
        return !TextUtils.isEmpty(token) && token.charAt(0) == handle;
    }

    /**
     * Removes the first character of the passed text if it matches the specified handle.
     *
     * @param handle The handle char to strip from the text.
     * @param text   A CharSequence to find and remove the handle from.
     * @return The stripped string (might be empty).
     */
    public static String stripPrefixHandle(char handle, @Nullable CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return text.charAt(0) == handle ? text.toString().substring(1) : text.toString();
    }

    /**
     * Select all the text in the {@link EditText}.
     *
     * @param editText The {@link EditText}.
     */
    @SuppressWarnings("unused")
    public static void selectAllText(EditText editText) {
        editText.setSelection(0, editText.getText().length());
    }

}

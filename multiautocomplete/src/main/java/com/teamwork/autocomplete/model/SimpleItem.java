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

package com.teamwork.autocomplete.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Simple POJO for an item model to use with an {@link com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter}.
 * <p>
 * It consists of an (optional) image URL and a text to display.
 *
 * @author Marco Salis
 */
// @ThreadSafe
public class SimpleItem {

    private final @Nullable String imageUrl;
    private final @NonNull CharSequence text;

    public SimpleItem(@Nullable String imageUrl, @NonNull CharSequence text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    @SuppressWarnings("unused")
    public @Nullable String getImageUrl() {
        return imageUrl;
    }

    public @NonNull CharSequence getText() {
        return text;
    }

    @Override
    public @NonNull String toString() {
        return text.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleItem that = (SimpleItem) o;
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

}

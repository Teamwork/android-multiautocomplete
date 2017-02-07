package com.teamwork.autocomplete.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Simple POJO for an item model to use with an {@link com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter}.
 * <p>
 * It consists of an (optional) image URL and a text to display.
 */
// @ThreadSafe
public class SimpleItem {

    private final @Nullable String imageUrl;
    private final @NonNull CharSequence text;

    public SimpleItem(@Nullable String imageUrl, @NonNull CharSequence text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    public @Nullable String getImageUrl() {
        return imageUrl;
    }

    public @NonNull CharSequence getText() {
        return text;
    }

    @Override
    public String toString() {
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
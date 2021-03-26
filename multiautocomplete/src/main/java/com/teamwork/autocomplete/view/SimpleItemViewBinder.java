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

package com.teamwork.autocomplete.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teamwork.autocomplete.R;
import com.teamwork.autocomplete.model.SimpleItem;
import com.teamwork.autocomplete.util.SpannableUtils;

/**
 * Item view binder for a {@link SimpleItem} model.
 * <p>
 * It provides very basic behaviour for components that don't need any customization:
 * <ul>
 * <li>{@link #getItemId(SimpleItem)} returns the item's hash code</li>
 * <li>It binds the data to the R.layout.simple_item layout</li>
 * <li>It highlights the portion of text that matches the current text constraint</li>
 * </ul>
 * To prevent the library to have dependencies on any image loading/caching framework, images are not loaded into the
 * image view. To implement your own loading, override {@link #onImageViewBind(ImageView, SimpleItem)}.
 *
 * @author Marco Salis
 */
@SuppressWarnings("WeakerAccess")
public class SimpleItemViewBinder implements AutoCompleteViewBinder<SimpleItem> {

    @Override
    public long getItemId(@NonNull SimpleItem item) {
        return item.hashCode();
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.simple_item;
    }

    @Override
    public @NonNull AutoCompleteViewHolder getViewHolder(@NonNull View view) {
        return new SimpleItemViewHolder(view);
    }

    @Override
    public void bindData(@NonNull AutoCompleteViewHolder viewHolder, @NonNull SimpleItem item, @Nullable CharSequence constraint) {
        SimpleItemViewHolder itemViewHolder = (SimpleItemViewHolder) viewHolder;
        CharSequence text;
        if (constraint != null) { // highlight constraint text in bold if any
            text = SpannableUtils.setBoldSubText(item.getText(), constraint);
        } else {
            text = item.getText();
        }
        itemViewHolder.textView.setText(text);

        onImageViewBind(itemViewHolder.imageView, item);
    }

    /**
     * Override this to provide image loading to the {@link SimpleItemViewBinder} using your own image caching library.
     * <p>
     * The default implementation just sets the ImageView's visibility to {@link View#GONE}.
     */
    protected void onImageViewBind(@NonNull ImageView imageView, @SuppressWarnings("unused") @NonNull SimpleItem item) {
        imageView.setVisibility(View.GONE);
    }


    public static class SimpleItemViewHolder extends AutoCompleteViewHolder {

        public final ImageView imageView;
        public final TextView textView;

        public SimpleItemViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.textView);
        }
    }

}

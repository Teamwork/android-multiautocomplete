package com.teamwork.autocomplete.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    protected void onImageViewBind(@NonNull ImageView imageView, @NonNull SimpleItem item) {
        imageView.setVisibility(View.GONE);
    }

    public static class SimpleItemViewHolder extends AutoCompleteViewHolder {

        public final ImageView imageView;
        public final TextView textView;

        protected SimpleItemViewHolder(@NonNull View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }

}
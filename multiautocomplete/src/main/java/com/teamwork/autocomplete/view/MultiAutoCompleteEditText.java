package com.teamwork.autocomplete.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.lang.reflect.Field;

/**
 * Subclass of {@link AppCompatMultiAutoCompleteTextView} for multi auto complete use.
 * <p>
 * The main reason for which this is necessary is that Android (sigh) didn't provide a listener for
 * {@link #onSelectionChanged(int, int)} cursor callbacks.
 * <p>
 * We need that to start the filtering (and display the dropdown if necessary) when the user moves the cursor within
 * the {@link AppCompatMultiAutoCompleteTextView}.
 */
public class MultiAutoCompleteEditText extends AppCompatMultiAutoCompleteTextView {

    public interface OnSelectionChangedListener {

        /**
         * Called when the view's own {@link #onSelectionChanged(int, int)} is called.
         *
         * @return true if the view should start filtering when the cursor moves, false otherwise.
         */
        boolean onSelectionChanged(int selStart, int selEnd);
    }

    private OnSelectionChangedListener listener;

    public MultiAutoCompleteEditText(Context context) {
        super(context);
    }

    public MultiAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnSelectionChangedListener(@Nullable OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (listener != null) {
            if (listener.onSelectionChanged(selStart, selEnd)) {
                // Erm, a trick to trigger the filtering and make sure the view shows the drop down popup afterwards
                // calling performFiltering won't always trigger the popup here otherwise, because of mPopupCanBeUpdated
                try {
                    // AppCompatMultiAutoCompleteTextView -> MultiAutoCompleteTextView -> AutoCompleteTextView
                    Class<?> autoCompleteTextViewCls = getClass().getSuperclass().getSuperclass().getSuperclass();
                    Field field = autoCompleteTextViewCls.getDeclaredField("mPopupCanBeUpdated");
                    field.setAccessible(true);
                    field.set(this, true);
                } catch (Exception ignored) { // we failed.
                }
                // attempt filtering
                performFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
            }
        }
    }

}
package com.teamwork.autocomplete.adapter;

import android.support.annotation.NonNull;

/**
 * Listener interface for components tied to an {@link TypeAdapterDelegate} that need a callback when a full token
 * gets added or removed within the text contents of an editable TextView.
 * <p>
 * The detected text token will be matched to an item of type 'Model' by the type adapter and returned in the callback.
 * <p>
 * i.e. if the user types '@johndoe' and the handle matches one of the data items in the adapter, the {@link
 * #onTokenAdded(CharSequence, Object)} method from a subscribed listener will be called with that handle and the
 * associated 'person' model.
 *
 * @see AutoCompleteTypeAdapter#setOnTokensChangedListener(OnTokensChangedListener)
 */
public interface OnTokensChangedListener<Model> {

    /**
     * Called when a token associated to a data item gets fully typed by the user.
     *
     * @param token The text token that triggered the callback.
     * @param added The model bound to the token just added.
     */
    void onTokenAdded(@NonNull CharSequence token, @NonNull Model added);

    /**
     * Called when a token that was previously typed by the user gets deleted.
     *
     * @param token   The text token that got previously added.
     * @param removed The model bound to the token just removed.
     */
    void onTokenRemoved(@NonNull CharSequence token, @NonNull Model removed);

}
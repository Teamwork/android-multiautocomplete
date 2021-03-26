/*
 * Copyright 2017-present Teamwork.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamwork.autocomplete.adapter;

import androidx.annotation.NonNull;

/**
 * Listener interface for components tied to an {@link TypeAdapterDelegate} that need a callback when a full token gets added or removed
 * within the text contents of an editable TextView.
 * <p>
 * The detected text token will be matched to an item of type 'Model' by the type adapter and returned in the callback.
 * <p>
 * i.e. if the user types '@johndoe' and the handle matches one of the data items in the adapter, the {@link
 * #onTokenAdded(CharSequence, Object)} method from a subscribed listener will be called with that handle and the associated 'person' model.
 *
 * @author Marco Salis
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

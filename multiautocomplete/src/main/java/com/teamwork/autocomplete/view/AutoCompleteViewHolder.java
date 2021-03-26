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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

/**
 * Base class for View holders to use within an {@link com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter}.
 *
 * @author Marco Salis
 */
@MainThread
public class AutoCompleteViewHolder {

    protected final View view;

    protected AutoCompleteViewHolder(@NonNull View view) {
        this.view = view;
    }

}

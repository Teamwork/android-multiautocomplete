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

package com.teamwork.autocomplete;

import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.teamwork.autocomplete.adapter.TypeAdapterDelegate;
import com.teamwork.autocomplete.view.MultiAutoCompleteEditText;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link MultiAutoComplete} concrete implementation for a {@link MultiAutoCompleteEditText} auto complete feature with drop down.
 * It manages a list of {@link TypeAdapterDelegate}s through a single "adapter of adapters".
 *
 * @author Marco Salis
 */
class MultiAutoCompleteImpl
        implements MultiAutoComplete, TextWatcher, MultiAutoCompleteEditText.OnSelectionChangedListener {

    private final Tokenizer tokenizer;
    private final List<TypeAdapterDelegate<?>> typeAdapters;
    private final @Nullable Delayer delayer;

    private @Nullable MultiAutoCompleteEditText editText;
    private @Nullable AutoCompleteAdapter adapter;

    MultiAutoCompleteImpl(@NonNull Tokenizer tokenizer,
                          @NonNull List<TypeAdapterDelegate<?>> typeAdapters,
                          @Nullable Delayer delayer) {
        this.tokenizer = tokenizer;
        this.typeAdapters = Collections.unmodifiableList(new CopyOnWriteArrayList<>(typeAdapters));
        this.delayer = delayer;
    }

    @Override
    public void onViewAttached(@NonNull MultiAutoCompleteEditText view) {
        editText = view;

        adapter = new AutoCompleteAdapter(view.getContext(), typeAdapters, delayer);
        editText.setAdapter(adapter);
        editText.setTokenizer(tokenizer);
        editText.addTextChangedListener(this);
        editText.setOnSelectionChangedListener(this);

        adapter.registerDataSetObserver(dataSetObserver);

        for (TypeAdapterDelegate<?> adapter : typeAdapters) {
            adapter.registerDataSetObserver(delegateDataSetObserver);
        }
    }

    @Override
    public void onViewDetached() {
        for (TypeAdapterDelegate<?> adapter : typeAdapters) {
            adapter.unregisterDataSetObserver(delegateDataSetObserver);
        }

        if (adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
            adapter.notifyDataSetInvalidated();
            adapter = null;
        }
        if (editText != null) {
            editText.setAdapter(null);
            editText = null;
        }
    }

    @VisibleForTesting
    protected @Nullable MultiAutoCompleteEditText getEditText() {
        return editText;
    }

    @VisibleForTesting
    protected @Nullable AutoCompleteAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    @CallSuper
    public void afterTextChanged(Editable s) {
        for (TypeAdapterDelegate<?> typeAdapter : typeAdapters) {
            typeAdapter.onTextChanged(s.toString());
        }
    }

    @Override
    public boolean onSelectionChanged(int selStart, int selEnd) {
        return true; // filter on selection changed by default
    }

    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (editText != null && adapter != null && !adapter.isEmpty()) {
                // reset selection on first filtered item
                editText.setListSelection(0);
            }
        }
    };

    private final DataSetObserver delegateDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            if (adapter != null) {
                // notify main adapter that one of the type adapters data has changed
                adapter.notifyDataSetChanged();
            }
        }
    };

}

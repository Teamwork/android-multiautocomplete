package com.teamwork.autocomplete.view;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Base class for View holders to use within an {@link com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter}.
 */
@MainThread
public class AutoCompleteViewHolder {

    protected final View view;

    protected AutoCompleteViewHolder(@NonNull View view) {
        this.view = view;
    }

}
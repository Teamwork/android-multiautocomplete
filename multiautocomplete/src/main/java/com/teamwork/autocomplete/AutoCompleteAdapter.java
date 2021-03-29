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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.teamwork.autocomplete.adapter.NullTypeAdapterDelegate;
import com.teamwork.autocomplete.adapter.TypeAdapterDelegate;
import com.teamwork.autocomplete.util.ConstraintComparator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of an "adapter of adapters" that is set into the {@link android.widget.MultiAutoCompleteTextView} to manage and filter
 * multiple data sets.
 * <p>
 * The {@link Filter} component delegates returning the autocomplete filtered results to one of the registered adapters.
 *
 * @author Marco Salis
 */
class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    private final NullTypeAdapterDelegate nullTypeAdapter = new NullTypeAdapterDelegate();

    private final LayoutInflater layoutInflater;
    private final List<TypeAdapterDelegate<?>> typeAdapters;
    private final @Nullable MultiAutoComplete.Delayer delayer;

    @SuppressWarnings("rawtypes")
    private TypeAdapterDelegate currentTypeAdapter = nullTypeAdapter;
    private AutoCompleteFilter filter;

    private CharSequence currentConstraint;

    AutoCompleteAdapter(@NonNull Context context,
                        @NonNull List<TypeAdapterDelegate<?>> typeAdapters,
                        @Nullable MultiAutoComplete.Delayer delayer) {
        this.layoutInflater = LayoutInflater.from(context);
        this.typeAdapters = typeAdapters;
        this.delayer = delayer;
    }

    @Override
    public int getCount() {
        return currentTypeAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return currentTypeAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return currentTypeAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getViewTypeCount() {
        return typeAdapters.size() /* NullTypeAdapterDelegate */ + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (currentTypeAdapter instanceof NullTypeAdapterDelegate) {
            return typeAdapters.size(); // last index + 1
        }
        return typeAdapters.indexOf(currentTypeAdapter);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return currentTypeAdapter.getView(layoutInflater, position, convertView, parent, currentConstraint);
    }

    @Override
    public @NonNull Filter getFilter() {
        if (filter == null) {
            filter = new AutoCompleteFilter();
            if (delayer != null) {
                injectDelayer(filter, delayer);
            }
        }
        return filter;
    }

    /**
     * {@link Filter}s implementation of a delayer is unfortunately hidden in the Android SDK, despite it having been
     * there, used and unchanged, for ages. We use reflection and {@link java.lang.reflect.Proxy} to create an instance
     * of the Delayer class which delegates to our implementation of {@link MultiAutoComplete.Delayer}.
     */
    private void injectDelayer(@NonNull AutoCompleteFilter filter, @NonNull MultiAutoComplete.Delayer delayer) {
        try {
            @SuppressLint("PrivateApi") Class<?> delayerInterface = Class.forName("android.widget.Filter$Delayer");
            Object delayerInstance = java.lang.reflect.Proxy.newProxyInstance(
                    delayerInterface.getClassLoader(),
                    new Class[]{delayerInterface},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getPostingDelay")) {
                            CharSequence constraint = (CharSequence) args[0];
                            return delayer.getPostingDelay(constraint);
                        }
                        return null;
                    });

            Method method = filter.getClass().getMethod("setDelayer", delayerInterface);
            method.setAccessible(true);
            method.invoke(filter, delayerInstance);
        } catch (NoSuchMethodException e) {
            logReflectionException();
        } catch (InvocationTargetException e) {
            logReflectionException();
        } catch (IllegalAccessException e) {
            logReflectionException();
        } catch (ClassNotFoundException e) {
            logReflectionException();
        }
    }

    private void logReflectionException() {
        Log.e(MultiAutoComplete.class.getSimpleName(), "Reflection inject attempt of Delayer failed");
    }

    /**
     * {@link Filter} for the main adapter.
     * It checks which type adapter filter can handle the current token, and delegates the filtering to the chosen
     * adapter. The adapter data set is then set to the filtered items of the type adapter.
     *
     * @see com.teamwork.autocomplete.filter.TokenFilter
     */
    private class AutoCompleteFilter extends Filter {

        @Override
        @WorkerThread
        protected FilterResults performFiltering(CharSequence token) {
            FilterResults filterResults = new FilterResults();
            FilterResultsWrapper resultsWrapper = new FilterResultsWrapper();

            CharSequence constraint = null;
            List<Object> filteredData = new ArrayList<>();
            TypeAdapterDelegate<?> typeAdapter;

            if (token != null) {
                // retrieve the first type adapter that supports this token
                typeAdapter = getCurrentTypeAdapter(token);
                constraint = typeAdapter.getFilter().stripHandle(token);

                // filter data based on the constraint (stripped by any handle)
                List<?> filteredList = typeAdapter.performFiltering(constraint);
                filteredData.addAll(filteredList);

                // sort filtered results if there is a custom comparator
                @SuppressWarnings("rawtypes")
                ConstraintComparator comparator = typeAdapter.getFilter().getConstraintComparator();
                if (comparator != null) {
                    comparator.setConstraint(constraint);
                    if (comparator.shouldCompare()) {
                        //noinspection unchecked
                        Collections.sort(filteredData, comparator);
                    }
                }
            } else {
                // there is no original data without a type adapter: the adapter will be empty
                typeAdapter = nullTypeAdapter;
            }

            // we pass adapter and results in a wrapper to avoid accessing the class state from the worker thread
            resultsWrapper.constraint = constraint;
            resultsWrapper.typeAdapter = typeAdapter;
            resultsWrapper.results = filteredData;
            filterResults.values = resultsWrapper;
            filterResults.count = filteredData.size();
            return filterResults;
        }

        private @NonNull TypeAdapterDelegate<?> getCurrentTypeAdapter(@NonNull CharSequence token) {
            for (TypeAdapterDelegate<?> typeAdapter : typeAdapters) {
                if (typeAdapter.getFilter().supportsToken(token)) {
                    return typeAdapter;
                }
            }
            return nullTypeAdapter;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence token, FilterResults results) {
            FilterResultsWrapper resultsWrapper = (FilterResultsWrapper) results.values;
            currentConstraint = resultsWrapper.constraint;
            currentTypeAdapter = resultsWrapper.typeAdapter;

            currentTypeAdapter.setFilteredItems(resultsWrapper.results);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            //noinspection unchecked
            return currentTypeAdapter.getFilter().toTokenString(resultValue);
        }
    }

    /**
     * Simple wrapper for a <code>android.widget.Filter.FilterResults.values</code> to post the {@link TypeAdapterDelegate} to the main
     * thread from <code>Filter#performFiltering(CharSequence)</code>.
     */
    private static class FilterResultsWrapper {
        CharSequence constraint;
        TypeAdapterDelegate<?> typeAdapter;
        List<?> results;
    }

}

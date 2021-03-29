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

package com.teamwork.autocomplete.demo;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.teamwork.autocomplete.MultiAutoComplete;
import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;
import com.teamwork.autocomplete.adapter.OnTokensChangedListener;
import com.teamwork.autocomplete.filter.HandleTokenFilter;
import com.teamwork.autocomplete.model.SimpleItem;
import com.teamwork.autocomplete.tokenizer.PrefixTokenizer;
import com.teamwork.autocomplete.util.SpannableUtils;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;
import com.teamwork.autocomplete.view.AutoCompleteViewHolder;
import com.teamwork.autocomplete.view.MultiAutoCompleteEditText;
import com.teamwork.autocomplete.view.SimpleItemViewBinder.SimpleItemViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class SampleActivity extends AppCompatActivity implements OnTokensChangedListener<SampleActivity.Country> {

    private MultiAutoComplete simpleMultiAutoComplete;
    private MultiAutoComplete customMultiAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        MultiAutoCompleteEditText simpleEditText = (MultiAutoCompleteEditText) findViewById(R.id.simple_edit_text);
        MultiAutoCompleteEditText editText = (MultiAutoCompleteEditText) findViewById(R.id.edit_text);

        setupSimpleAutoComplete();
        setupCustomAutoComplete();

        /*
         * This call binds the MultiAutoCompleteEditText to the component, creating the adapter and indexing the data
         * within the adapters (asynchronously, from a worker thread). From this moment on, the MultiAutoComplete will
         * react to the user input on the passed editable text view.
         */
        simpleMultiAutoComplete.onViewAttached(simpleEditText);
        customMultiAutoComplete.onViewAttached(editText);
    }

    @Override
    protected void onDestroy() {
        /*
         * This would be particularly important in a Fragment (called from onDestroyView()), it would allow reuse of
         * the component across Fragment instance re-creation.
         * The AutoComplete can also be persisted as a static singleton in the application if needed, as long as it's
         * only used by the foreground Activity/Fragment and the lifecycle methods are correctly executed.
         */
        customMultiAutoComplete.onViewDetached();
        simpleMultiAutoComplete.onViewDetached();
        super.onDestroy();
    }

    /**
     * This is the simplest use possible of {@link MultiAutoComplete}.
     * It uses a default token filter, view binder and layout to match country names.
     */
    private void setupSimpleAutoComplete() {
        List<SimpleItem> countryList = getSimpleCountryList();
        simpleMultiAutoComplete = MultiAutoComplete.Build.from(countryList);
    }

    private void setupCustomAutoComplete() {
        List<Country> countryList = getCountryList();

        // type adapter to match country names prefixed with '#'
        AutoCompleteTypeAdapter<Country> nameTypeAdapter =
                AutoCompleteTypeAdapter.Build.from(new CountryViewBinder(), new CountryNameTokenFilter());
        // listener for tokens added/removed by the user (see onTokenAdded() and onTokenRemoved() below)
        nameTypeAdapter.setOnTokensChangedListener(this);

        // type adapter to match country codes prefixed with '@'
        // note: the data type here could be different (i.e. a Region object)
        AutoCompleteTypeAdapter<Country> codeTypeAdapter =
                AutoCompleteTypeAdapter.Build.from(new CountryViewBinder(), new CountryCodeTokenFilter());

        // setting items synchronously since we already have the list
        // this could be also done later on in the likely case the data set comes from network or disk
        nameTypeAdapter.setItems(countryList);
        codeTypeAdapter.setItems(countryList);

        // build the custom MultiAutoComplete by passing the required Tokenizer and type adapters
        customMultiAutoComplete = new MultiAutoComplete.Builder()
                .tokenizer(new PrefixTokenizer('@', '#'))
                .addTypeAdapter(nameTypeAdapter)
                .addTypeAdapter(codeTypeAdapter)
                .delayer(new TestDelayer())
                .build();
    }

    public List<SimpleItem> getSimpleCountryList() {
        List<SimpleItem> countryList = new ArrayList<>();
        String[] isoCountries = Locale.getISOCountries();
        for (String countryCode : isoCountries) {
            Locale country = new Locale("", countryCode);
            countryList.add(new SimpleItem(null, country.getDisplayCountry()));
        }
        return countryList;
    }

    public List<Country> getCountryList() {
        List<Country> countryList = new ArrayList<>();
        String[] isoCountries = Locale.getISOCountries();
        for (String countryCode : isoCountries) {
            Locale countryLocale = new Locale("", countryCode);
            Country country = new Country();
            country.countryCode = countryCode;
            country.countryName = countryLocale.getDisplayCountry();
            country.flagResource = getFlagFromResources(countryCode);
            countryList.add(country);
        }
        return countryList;
    }

    private int getFlagFromResources(String countryCode) {
        return getResources().getIdentifier(countryCode.toLowerCase(), "drawable", getPackageName());
    }

    /*
     * These two callback methods allow you to take action whenever a token matching an item is added or removed by the
     * user typing in the editable text view.
     */

    @Override
    public void onTokenAdded(@NonNull CharSequence token, @NonNull Country added) {
    }

    @Override
    public void onTokenRemoved(@NonNull CharSequence token, @NonNull Country removed) {
    }

    static class Country {
        String countryCode;
        String countryName;
        int flagResource;

        @Override public String toString() {
            return countryName;
        }
    }

    /**
     * This simple {@link AutoCompleteViewBinder} uses the default MultiAutoComplete layout to bind the country flag
     * and text. The current user-typed constraint is highlighted in bold in both country name and code.
     */
    private static class CountryViewBinder implements AutoCompleteViewBinder<Country> {

        @Override
        public long getItemId(@NonNull Country item) {
            return item.countryCode.hashCode();
        }

        @Override
        public int getItemLayoutId() {
            return com.teamwork.autocomplete.R.layout.simple_item;
        }

        @NonNull
        @Override
        public AutoCompleteViewHolder getViewHolder(@NonNull View view) {
            return new SimpleItemViewHolder(view);
        }

        @Override
        public void bindData(@NonNull AutoCompleteViewHolder viewHolder, @NonNull Country item, @Nullable CharSequence constraint) {
            SimpleItemViewHolder itemViewHolder = (SimpleItemViewHolder) viewHolder;
            CharSequence countryLabel;
            if (constraint == null) {
                countryLabel = item.countryName + " (" + item.countryCode + ")";
            } else {
                countryLabel = new SpannableStringBuilder()
                        .append(SpannableUtils.setBoldSubText(item.countryName, constraint))
                        .append(" (")
                        .append(SpannableUtils.setBoldSubText(item.countryCode, constraint))
                        .append(")");
            }
            itemViewHolder.textView.setText(countryLabel);
            itemViewHolder.imageView.setImageResource(item.flagResource);
        }
    }

    /**
     * Token filter to match country names prefixed with a '#' handle (i.e. '@Ireland').
     */
    private static class CountryNameTokenFilter extends HandleTokenFilter<Country> {

        private final Pattern namePattern = Pattern.compile("(^|\\s|>)#(\\w+)");

        CountryNameTokenFilter() {
            super('#');
        }

        @Override
        protected boolean matchesConstraint(@NonNull Country country, @NonNull CharSequence constraint) {
            return country.countryName.toLowerCase().contains(constraint.toString().toLowerCase());
        }

        @Override
        public @Nullable Pattern getValidTokenPattern() {
            /*
             * Providing a valid token Pattern allows MultiAutoComplete to check on text changes and notify when a token
             * matching an item in the adapter gets added or removed from the text.
             */
            return namePattern;
        }
    }

    /**
     * Token filter to match country codes prefixed with a '@' handle (i.e. '@IR').
     */
    private static class CountryCodeTokenFilter extends HandleTokenFilter<Country> {

        CountryCodeTokenFilter() {
            super('@');
        }

        @Override
        protected boolean matchesConstraint(@NonNull Country country, @NonNull CharSequence constraint) {
            return country.countryCode.toLowerCase().contains(constraint.toString().toLowerCase());
        }

        @Override
        public @NonNull CharSequence toTokenString(@NonNull Country item) {
            return String.format(Locale.US, "%c%s", handleChar, item.countryCode);
        }
    }

    private static class TestDelayer implements MultiAutoComplete.Delayer {
        @Override public long getPostingDelay(CharSequence constraint) {
            return 10;
        }
    }

}

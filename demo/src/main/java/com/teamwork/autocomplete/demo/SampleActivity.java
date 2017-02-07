package com.teamwork.autocomplete.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.teamwork.autocomplete.MultiAutoComplete;
import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;
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

public class SampleActivity extends AppCompatActivity {


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

        simpleMultiAutoComplete.onViewAttached(simpleEditText);
        customMultiAutoComplete.onViewAttached(editText);
    }

    @Override
    protected void onDestroy() {
        customMultiAutoComplete.onViewDetached();
        simpleMultiAutoComplete.onViewDetached();
        super.onDestroy();
    }

    private void setupSimpleAutoComplete() {
        List<SimpleItem> countryList = getSimpleCountryList();
        simpleMultiAutoComplete = MultiAutoComplete.Build.from(countryList);
    }

    private void setupCustomAutoComplete() {
        List<Country> countryList = getCountryList();

        AutoCompleteTypeAdapter<Country> nameTypeAdapter =
                AutoCompleteTypeAdapter.Build.from(new CountryViewBinder(), new CountryNameTokenFilter());
        AutoCompleteTypeAdapter<Country> codeTypeAdapter =
                AutoCompleteTypeAdapter.Build.from(new CountryViewBinder(), new CountryCodeTokenFilter());

        nameTypeAdapter.setItems(countryList);
        codeTypeAdapter.setItems(countryList);

        customMultiAutoComplete = new MultiAutoComplete.Builder()
                .tokenizer(new PrefixTokenizer('@', '#'))
                .addTypeAdapter(nameTypeAdapter)
                .addTypeAdapter(codeTypeAdapter)
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

    private static class Country {
        String countryCode;
        String countryName;
        int flagResource;
    }

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

    private static class CountryNameTokenFilter extends HandleTokenFilter<Country> {

        CountryNameTokenFilter() {
            super('#');
        }

        @Override
        protected boolean matchesConstraint(@NonNull Country country, @NonNull CharSequence constraint) {
            return country.countryName.toLowerCase().contains(constraint.toString().toLowerCase());
        }

        @NonNull
        @Override
        public CharSequence toTokenString(@NonNull Country item) {
            return handleChar + item.countryName;
        }
    }


    private static class CountryCodeTokenFilter extends HandleTokenFilter<Country> {

        CountryCodeTokenFilter() {
            super('@');
        }

        @Override
        protected boolean matchesConstraint(@NonNull Country country, @NonNull CharSequence constraint) {
            return country.countryCode.toLowerCase().contains(constraint.toString().toLowerCase());
        }

        @NonNull
        @Override
        public CharSequence toTokenString(@NonNull Country item) {
            return handleChar + item.countryCode;
        }
    }

}
package com.teamwork.autocomplete;

import android.text.SpannableStringBuilder;
import android.widget.MultiAutoCompleteTextView;

import com.teamwork.autocomplete.adapter.TypeAdapterDelegate;
import com.teamwork.autocomplete.view.MultiAutoCompleteEditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MultiAutoCompleteImplTest {

    @Mock MultiAutoCompleteTextView.Tokenizer tokenizer;
    @Mock TypeAdapterDelegate<String> typeAdapter1;
    @Mock TypeAdapterDelegate<Object> typeAdapter2;

    @Mock MultiAutoCompleteEditText editText;

    private MultiAutoCompleteImpl autoComplete;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(editText.getContext()).thenReturn(RuntimeEnvironment.application);
        autoComplete = new MultiAutoCompleteImpl(tokenizer, Arrays.asList(typeAdapter1, typeAdapter2), null);
    }

    @Test
    public void onViewAttached() throws Exception {
        autoComplete.onViewAttached(editText);

        assertThat(autoComplete.getAdapter(), notNullValue());
        assertThat(autoComplete.getEditText(), notNullValue());
    }

    @Test
    public void onViewDetached() throws Exception {
        autoComplete.onViewAttached(editText);
        autoComplete.onViewDetached();

        assertThat(autoComplete.getAdapter(), nullValue());
        assertThat(autoComplete.getEditText(), nullValue());
    }

    @Test
    public void afterTextChanged() throws Exception {
        CharSequence text = "text";
        autoComplete.afterTextChanged(new SpannableStringBuilder(text));

        verify(typeAdapter1).onTextChanged(eq(text));
        verify(typeAdapter2).onTextChanged(eq(text));
    }

}
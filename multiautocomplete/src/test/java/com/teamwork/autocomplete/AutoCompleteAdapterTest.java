package com.teamwork.autocomplete;

import com.teamwork.autocomplete.adapter.AutoCompleteTypeAdapter;
import com.teamwork.autocomplete.adapter.TypeAdapterDelegate;
import com.teamwork.autocomplete.filter.SimpleTokenFilter;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AutoCompleteAdapterTest {

    private static final int ASYNC_FILTER_WAIT_MS = 100;

    @Mock AutoCompleteViewBinder<String> viewBinder;
    @Mock MultiAutoComplete.Delayer delayer;

    private AutoCompleteTypeAdapter<String> typeAdapter;
    private List<String> dataset;
    private AutoCompleteAdapter autoCompleteAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private SimpleTokenFilter<String> spiedFilter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        dataset = Arrays.asList("Ireland", "Italy", "United Kingdom", "Spain");

        when(viewBinder.getItemId(anyObject())).then(invocation -> (long) invocation.getArguments()[0].hashCode());
        spiedFilter = spy(new SimpleTokenFilter<>());
        typeAdapter = AutoCompleteTypeAdapter.Build.from(viewBinder, spiedFilter);
        typeAdapter.setItems(dataset);
        autoCompleteAdapter = new AutoCompleteAdapter(RuntimeEnvironment.application,
                Collections.singletonList((TypeAdapterDelegate) typeAdapter), delayer);
    }

    @Test
    public void getCount() throws Exception {
        assertThat(autoCompleteAdapter.getCount(), is(0));

        autoCompleteAdapter.getFilter().filter("");
        waitOnFilter();
        assertThat(autoCompleteAdapter.getCount(), is(dataset.size()));

        autoCompleteAdapter.getFilter().filter("it");
        waitOnFilter();
        assertThat(autoCompleteAdapter.getCount(), is(2));
    }

    @Test
    public void getItem() throws Exception {
        autoCompleteAdapter.getFilter().filter("it");
        waitOnFilter();
        assertThat(autoCompleteAdapter.getItem(0), is("Italy"));
        assertThat(autoCompleteAdapter.getItem(1), is("United Kingdom"));
    }

    @Test
    public void getItemId() throws Exception {
        autoCompleteAdapter.getFilter().filter("it");
        waitOnFilter();
        assertThat(autoCompleteAdapter.getItemId(0), is((long) "Italy".hashCode()));
        assertThat(autoCompleteAdapter.getItemId(1), is((long) "United Kingdom".hashCode()));
    }

    @Test
    public void getViewTypeCount() throws Exception {
        assertThat(autoCompleteAdapter.getViewTypeCount(), is(2));

        autoCompleteAdapter.getFilter().filter("");
        waitOnFilter();
        assertThat(autoCompleteAdapter.getViewTypeCount(), is(2));
    }

    @Test
    public void getItemViewType() throws Exception {
        assertThat(autoCompleteAdapter.getItemViewType(0), is(1));

        autoCompleteAdapter.getFilter().filter("");
        waitOnFilter();
        assertThat(autoCompleteAdapter.getItemViewType(0), is(0));
    }

    @Test
    public void testFilter_performFiltering() throws Exception {
        assertThat(autoCompleteAdapter.getFilter(), notNullValue());

        autoCompleteAdapter.getFilter().filter("it");
        waitOnFilter();

        verify(((TypeAdapterDelegate) typeAdapter).getFilter()).stripHandle(eq("it"));
        verify(((TypeAdapterDelegate) typeAdapter).getFilter()).supportsToken("it");
        //noinspection unchecked
        verify(((TypeAdapterDelegate) typeAdapter).getFilter()).performFiltering("it", dataset);
    }

    private void waitOnFilter() throws InterruptedException {
        Thread.sleep(ASYNC_FILTER_WAIT_MS); // async filter
    }

}
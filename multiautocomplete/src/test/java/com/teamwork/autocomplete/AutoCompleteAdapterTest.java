package com.teamwork.autocomplete;

import android.widget.Filter;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AutoCompleteAdapterTest extends Filter {

    @Mock AutoCompleteViewBinder<String> viewBinder;
    @Mock MultiAutoComplete.Delayer delayer;

    private AutoCompleteTypeAdapter<String> typeAdapter;
    private List<String> dataset;
    private AutoCompleteAdapter autoCompleteAdapter;
    @SuppressWarnings("FieldCanBeLocal")
    private SimpleTokenFilter<String> spiedFilter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        dataset = Arrays.asList("Ireland", "Italy", "United Kingdom", "Spain");

        when(viewBinder.getItemId(any())).then(invocation -> (long) invocation.getArguments()[0].hashCode());
        spiedFilter = spy(new SimpleTokenFilter<>());
        typeAdapter = AutoCompleteTypeAdapter.Build.from(viewBinder, spiedFilter);
        typeAdapter.setItems(dataset);
        autoCompleteAdapter = new AutoCompleteAdapter(RuntimeEnvironment.application,
                Collections.singletonList((TypeAdapterDelegate<?>) typeAdapter), delayer);
    }

    @Test
    public void getCount() throws Exception {
        assertThat(autoCompleteAdapter.getCount(), is(0));

        Filter filter = autoCompleteAdapter.getFilter();
        performFiltering(filter, "it");

        assertThat(autoCompleteAdapter.getCount(), is(2));
    }

    @Test
    public void getItem() throws Exception {
        Filter filter = autoCompleteAdapter.getFilter();
        performFiltering(filter, "it");

        assertThat(autoCompleteAdapter.getItem(0), is("Italy"));
        assertThat(autoCompleteAdapter.getItem(1), is("United Kingdom"));
    }

    @Test
    public void getItemId() throws Exception {
        Filter filter = autoCompleteAdapter.getFilter();
        performFiltering(filter, "");

        assertThat(autoCompleteAdapter.getItemId(0), is((long) "Ireland".hashCode()));
        assertThat(autoCompleteAdapter.getItemId(3), is((long) "Spain".hashCode()));
    }

    @Test
    public void getViewTypeCount() throws Exception {
        assertThat(autoCompleteAdapter.getViewTypeCount(), is(2));

        Filter filter = autoCompleteAdapter.getFilter();
        performFiltering(filter, "it");

        assertThat(autoCompleteAdapter.getViewTypeCount(), is(2));
    }

    @Test
    public void getItemViewType() throws Exception {
        assertThat(autoCompleteAdapter.getItemViewType(0), is(1));

        Filter filter = autoCompleteAdapter.getFilter();
        performFiltering(filter, "");

        assertThat(autoCompleteAdapter.getItemViewType(0), is(0));
    }

    @Test
    public void testFilter_performFiltering() throws Exception {
        Filter filter = autoCompleteAdapter.getFilter();
        assertThat(filter, notNullValue());

        performFiltering(filter, "it");

        verify(((TypeAdapterDelegate<?>) typeAdapter).getFilter()).stripHandle(eq("it"));
        verify(((TypeAdapterDelegate<?>) typeAdapter).getFilter()).supportsToken("it");
        //noinspection unchecked
        verify(((TypeAdapterDelegate<String>) typeAdapter).getFilter()).performFiltering("it", dataset);
    }

    // running the asynchronous Filter.filter() yields to unstable tests which need to rely on Thread.sleep
    private static void performFiltering(Filter filter, String constraint)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends Filter> filterCls = filter.getClass();
        Method performFiltering = filterCls.getDeclaredMethod("performFiltering", CharSequence.class);
        Method publishResults = filterCls.getDeclaredMethod("publishResults", CharSequence.class, FilterResults.class);

        FilterResults results = (FilterResults) performFiltering.invoke(filter, constraint);
        publishResults.invoke(filter, constraint, results);
    }

    // extends Filter only for quicker access to FilterResults class

    @Override protected FilterResults performFiltering(CharSequence charSequence) {
        return null;
    }

    @Override protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

    }
}

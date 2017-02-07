package com.teamwork.autocomplete.adapter;

import android.os.Handler;
import android.os.Looper;

import com.teamwork.autocomplete.filter.SimpleTokenFilter;
import com.teamwork.autocomplete.test_util.ImmediateExecutor;
import com.teamwork.autocomplete.view.AutoCompleteViewBinder;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.teamwork.autocomplete.adapter.BaseTypeAdapterDelegate.getAddedTokens;
import static com.teamwork.autocomplete.adapter.BaseTypeAdapterDelegate.getRemovedTokens;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BaseTypeAdapterDelegateTest {

    @Mock AutoCompleteViewBinder<String> viewBinder;

    private List<String> dataset;
    private BaseTypeAdapterDelegate<String> adapterDelegate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ImmediateExecutor executor = new ImmediateExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        SimpleTokenFilter<String> tokenFilter = new SimpleTokenFilter<String>() {
            @Override public Pattern getValidTokenPattern() {
                return Pattern.compile("(^|\\s|>)(\\w+)");
            }
        };

        dataset = Arrays.asList("Ireland", "Italy", "United Kingdom", "Spain");

        adapterDelegate = new BaseTypeAdapterDelegate<>(executor, handler, viewBinder, tokenFilter);
    }

    @After
    public void tearDown() throws Exception {
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
    }

    @Test
    public void testGetFilter() throws Exception {
        assertThat(adapterDelegate.getFilter(), notNullValue());
    }

    @Test
    public void testSetItems() throws Exception {
        adapterDelegate.setItems(dataset);

        assertThat(adapterDelegate.getItemsMap(), notNullValue());
        assertThat(adapterDelegate.getItemsMap().size(), is(dataset.size()));
        assertThat(adapterDelegate.getItemsMap().values(), IsIterableContainingInOrder.contains(dataset.toArray()));

        assertThat(adapterDelegate.getCount(), is(0)); // no filterable items just yet
    }

    @Test
    public void testSetFilteredItems_getCount_getItem_ItemId() throws Exception {
        when(viewBinder.getItemId(any())).thenReturn(-666L);
        adapterDelegate.setFilteredItems(dataset);

        assertThat(adapterDelegate.getCount(), is(dataset.size()));

        for (int i = 0; i < dataset.size(); i++) {
            assertThat(adapterDelegate.getItem(i), is(dataset.get(i)));
            assertThat(adapterDelegate.getItemId(i), is(-666L));
        }
    }

    @Test
    public void testPerformFiltering() throws Exception {
        adapterDelegate.setItems(dataset);

        List<String> filtered = adapterDelegate.performFiltering("it");
        assertThat(filtered, hasItems("Italy", "United Kingdom"));
    }

    @Test
    public void testOnTextChanged() throws Exception {
        adapterDelegate.setItems(dataset);

        OnTokensChangedListener mockListener = mock(OnTokensChangedListener.class);
        //noinspection unchecked
        adapterDelegate.setOnTokensChangedListener(mockListener);

        adapterDelegate.onTextChanged(" Spain");
        ShadowLooper.runUiThreadTasks();

        //noinspection unchecked
        verify(mockListener).onTokenAdded(eq("Spain"), eq("Spain"));

        adapterDelegate.onTextChanged(" Spai");
        ShadowLooper.runUiThreadTasks();

        //noinspection unchecked
        verify(mockListener).onTokenRemoved(eq("Spain"), eq("Spain"));
    }

    @Test
    public void testGetAddedTokens() throws Exception {
        Collection<CharSequence> diff = getAddedTokens(Collections.emptySet(), Collections.emptySet());
        assertThat(diff.size(), is(0));

        diff = getAddedTokens(Collections.singleton("test"), (Collections.singleton("test")));
        assertThat(diff.size(), is(0));

        diff = getAddedTokens(Collections.singleton("test1"), Collections.singleton("test2"));
        assertThat(diff.size(), is(1));
        assertThat(diff, hasItem("test2"));

        Set<CharSequence> diff1 = new HashSet<>(Arrays.asList("existing1", "existing2", "existing3"));
        Set<CharSequence> diff2 = new HashSet<>(Arrays.asList("new1", "new2", "existing2"));
        diff = getAddedTokens(diff1, diff2);
        assertThat(diff, hasItems("new1", "new2"));
    }

    @Test
    public void testGetRemovedTokens() throws Exception {
        Collection<CharSequence> diff = getRemovedTokens(Collections.emptySet(), Collections.emptySet());
        assertThat(diff.size(), is(0));

        diff = getRemovedTokens(Collections.singleton("test"), Collections.singleton("test"));
        assertThat(diff.size(), is(0));

        diff = getRemovedTokens(Collections.singleton("test1"), Collections.singleton("test2"));
        assertThat(diff.size(), is(1));
        assertThat(diff, hasItem("test1"));

        Set<CharSequence> diff1 = new HashSet<>(Arrays.asList("existing1", "existing2", "existing3"));
        Set<CharSequence> diff2 = new HashSet<>(Arrays.asList("new1", "new2", "existing2"));
        diff = getRemovedTokens(diff1, diff2);
        assertThat(diff, hasItems("existing1", "existing3"));
    }

}
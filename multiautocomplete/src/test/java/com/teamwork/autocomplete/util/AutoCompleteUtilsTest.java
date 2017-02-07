package com.teamwork.autocomplete.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class AutoCompleteUtilsTest {

    @Test
    public void hasHandle() throws Exception {
        assertThat(AutoCompleteUtils.hasPrefixHandle('@', ""), is(false));
        assertThat(AutoCompleteUtils.hasPrefixHandle('@', "no_handle"), is(false));
        assertThat(AutoCompleteUtils.hasPrefixHandle('@', "no_handle@"), is(false));
        assertThat(AutoCompleteUtils.hasPrefixHandle('@', "@handle"), is(true));
    }

    @Test
    public void stripHandle() throws Exception {
        assertThat(AutoCompleteUtils.stripPrefixHandle('@', ""), is(""));
        assertThat(AutoCompleteUtils.stripPrefixHandle('@', "no_handle"), is("no_handle"));
        assertThat(AutoCompleteUtils.stripPrefixHandle('@', "no_handle@"), is("no_handle@"));
        assertThat(AutoCompleteUtils.stripPrefixHandle('@', "@handle"), is("handle"));
    }

}
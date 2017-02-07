package com.teamwork.autocomplete.tokenizer;

import android.widget.MultiAutoCompleteTextView;

import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class PrefixTokenizerTest {

    private MultiAutoCompleteTextView.Tokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        tokenizer = new PrefixTokenizer('@', ':');
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findTokenStart() throws Exception {
        assertThat(tokenizer.findTokenStart("", 0), is(0)); // empty string

        assertThat(tokenizer.findTokenStart("@", 1), is(0)); // match - first char
        assertThat(tokenizer.findTokenStart(" @", 1), is(1)); // match - space first
        assertThat(tokenizer.findTokenStart(" @te", 3), is(1)); // match - space first
        assertThat(tokenizer.findTokenStart(" @te @tests", 5), is(1)); // match - first handle
        assertThat(tokenizer.findTokenStart(" @te @tests", 11), is(5)); // match - second handle
        assertThat(tokenizer.findTokenStart(" @te@tests", 10), is(1)); // match - first handle

        assertThat(tokenizer.findTokenStart(" ", 1), is(1)); // no match - spaces
        assertThat(tokenizer.findTokenStart(" @", 0), is(0)); // no match - space before
        assertThat(tokenizer.findTokenStart("tests:", 6), is(6)); // no match - spaces
        assertThat(tokenizer.findTokenStart("test@s:", 6), is(6)); // no match - @ after text
    }

    @Test
    public void findTokenStart_multiple() throws Exception {
        assertThat(tokenizer.findTokenStart(" @:", 3), is(1));
        assertThat(tokenizer.findTokenStart(" :@", 3), is(1));
        assertThat(tokenizer.findTokenStart(" :test1@test2", 3), is(1));
        assertThat(tokenizer.findTokenStart(" :test1 @test2", 9), is(8));
    }

    @Test
    public void findTokenEnd() throws Exception {
        assertThat(tokenizer.findTokenEnd("", 0), is(0)); // empty string

        assertThat(tokenizer.findTokenEnd("@", 0), is(1)); // match - first char
        assertThat(tokenizer.findTokenEnd("@ ", 0), is(0)); // match - space after
        assertThat(tokenizer.findTokenEnd("@te ", 0), is(2)); // match - space after
        assertThat(tokenizer.findTokenEnd("@te @tests", 0), is(2)); // match - first handle
        assertThat(tokenizer.findTokenEnd("@te @tests ", 4), is(9)); // match - second handle
        assertThat(tokenizer.findTokenEnd("@te@tests ", 0), is(8)); // match - first handle

        assertThat(tokenizer.findTokenEnd(" ", 0), is(0)); // no match - spaces
        assertThat(tokenizer.findTokenEnd("@ ", 2), is(2)); // no match - space after
        assertThat(tokenizer.findTokenEnd("tests:", 0), is(6)); // no match - spaces
        assertThat(tokenizer.findTokenEnd("test@s:", 0), is(7)); // no match - @ after text
    }

    @Test
    public void terminateToken() throws Exception {
        MatcherAssert.assertThat(tokenizer.terminateToken(""), is("")); // empty string stays same
        MatcherAssert.assertThat(tokenizer.terminateToken(" "), is(" ")); // don't add spaces
        MatcherAssert.assertThat(tokenizer.terminateToken("test "), is("test ")); // don't add spaces
        MatcherAssert.assertThat(tokenizer.terminateToken("test"), is("test ")); // add spaces
    }

}
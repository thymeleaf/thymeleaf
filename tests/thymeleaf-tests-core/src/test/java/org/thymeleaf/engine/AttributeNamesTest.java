/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public final class AttributeNamesTest {



    @Test
    public void testHTMLBuffer() {
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertNull(AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()).getPrefix());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forHTMLName("something".toCharArray(), 0, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forHTMLName("absomethingba".toCharArray(), 2, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forHTMLName("abcdefghijkliklmnsomethingba".toCharArray(), 17, "something".length()).toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("abcdefghijkliklmnth:somethingba".toCharArray(), 17, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forHTMLName("SOMETHING".toCharArray(), 0, "SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{:something}", AttributeNames.forHTMLName(":something".toCharArray(), 0, ":something".length()).toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()).toString());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertEquals(
                "{xml:ns}", AttributeNames.forHTMLName("xml:ns".toCharArray(), 0, "xml:ns".length()).toString());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forHTMLName("xml:space".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forHTMLName("XML:SPACE".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{xmlns:th}", AttributeNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).toString());
        Assertions.assertFalse(
                AttributeNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).isPrefixed());

        Assertions.assertSame(
                AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()), AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()));
        Assertions.assertSame(
                AttributeNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()), AttributeNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()));
        Assertions.assertSame(
                AttributeNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), AttributeNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()));
        Assertions.assertSame(
                AttributeNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), AttributeNames.forHTMLName("DATA-TH-SOMETHING".toCharArray(), 0, "data-th-something".length()));

        try {
            AttributeNames.forHTMLName(null, 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forHTMLName("".toCharArray(), 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forHTMLName(" ".toCharArray(), 0, 1);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }



    @Test
    public void testHTMLString() {
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertNull(AttributeNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()).getPrefix());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName(null, "data-something").toString());
        Assertions.assertNull(AttributeNames.forHTMLName(null, "data-something").getPrefix());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName(null, "th:something").toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forHTMLName("something").toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("th:something").toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forHTMLName("SOMETHING").toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("TH:SOMETHING").toString());
        Assertions.assertEquals(
                "{:something}", AttributeNames.forHTMLName(":something").toString());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("data-th-something").toString());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName("data-something").toString());
        Assertions.assertEquals(
                "{xml:ns}", AttributeNames.forHTMLName("xml:ns").toString());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forHTMLName("xml:space").toString());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forHTMLName("XML:SPACE").toString());
        Assertions.assertEquals(
                "{xmlns:th}", AttributeNames.forHTMLName("xmlns:th").toString());
        Assertions.assertFalse(
                AttributeNames.forHTMLName("xmlns:th").isPrefixed());
        Assertions.assertEquals(
                "{th:something,data-th-something}", AttributeNames.forHTMLName("th","something").toString());

        Assertions.assertSame(
                AttributeNames.forHTMLName("data-something"), AttributeNames.forHTMLName("data-something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("xmlns:th"), AttributeNames.forHTMLName("xmlns:th"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("data-th-something"), AttributeNames.forHTMLName("data-th-something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("data-th-something"), AttributeNames.forHTMLName("th:something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("xmlns","th"), AttributeNames.forHTMLName("xmlns","th"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("th","something"), AttributeNames.forHTMLName("th","something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("","something"), AttributeNames.forHTMLName("something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName(null,"something"), AttributeNames.forHTMLName("something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("  ","something"), AttributeNames.forHTMLName("something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("  ","SOMETHING"), AttributeNames.forHTMLName("something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("data-TH-SOMETHING"), AttributeNames.forHTMLName("th:something"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("XMLNS","TH"), AttributeNames.forHTMLName("xmlns","th"));
        Assertions.assertSame(
                AttributeNames.forHTMLName("data-th-something"), AttributeNames.forHTMLName("TH:SOMETHING"));

        try {
            AttributeNames.forHTMLName(null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forHTMLName("");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forHTMLName("t", "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forHTMLName(" ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forHTMLName("t", " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }


    @Test
    public void testXMLBuffer() {
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forXMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forXMLName("something".toCharArray(), 0, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forXMLName("abcdefghijkliklmnsomethingba".toCharArray(), 17, "something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forXMLName("abcdefghijkliklmnth:somethingba".toCharArray(), 17, "th:something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forXMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "th", AttributeNames.forXMLName("th:something".toCharArray(), 0, "th:something".length()).getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", AttributeNames.forXMLName("SOMETHING".toCharArray(), 0, "SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", AttributeNames.forXMLName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).toString());
        Assertions.assertEquals(
                "TH", AttributeNames.forXMLName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).getPrefix());
        Assertions.assertEquals(
                "{:something}", AttributeNames.forXMLName(":something".toCharArray(), 0, ":something".length()).toString());
        Assertions.assertFalse(
                AttributeNames.forXMLName(":something".toCharArray(), 0, ":something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", AttributeNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()).toString());
        Assertions.assertFalse(
                AttributeNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forXMLName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertEquals(
                "{xml:ns}", AttributeNames.forXMLName("xml:ns".toCharArray(), 0, "xml:ns".length()).toString());
        Assertions.assertEquals(
                "xml", AttributeNames.forXMLName("xml:ns".toCharArray(), 0, "xml:ns".length()).getPrefix());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forXMLName("xml:space".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{XML:SPACE}", AttributeNames.forXMLName("XML:SPACE".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "XML", AttributeNames.forXMLName("XML:SPACE".toCharArray(), 0, "xml:space".length()).getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", AttributeNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).toString());
        Assertions.assertEquals(
                "xmlns", AttributeNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).getPrefix());

        Assertions.assertSame(
                AttributeNames.forXMLName("data-something".toCharArray(), 0, "data-something".length()), AttributeNames.forXMLName("data-something".toCharArray(), 0, "data-something".length()));
        Assertions.assertSame(
                AttributeNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()), AttributeNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()));
        Assertions.assertSame(
                AttributeNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), AttributeNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()));
        Assertions.assertNotSame(
                AttributeNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), AttributeNames.forXMLName("DATA-TH-SOMETHING".toCharArray(), 0, "data-th-something".length()));

        try {
            AttributeNames.forXMLName(null, 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forXMLName("".toCharArray(), 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forXMLName(" ".toCharArray(), 0, 1);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }


    @Test
    public void testXMLString() {
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName(null, "data-something").toString());
        Assertions.assertNull(AttributeNames.forHTMLName(null, "data-something").getPrefix());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forXMLName(null, "th:something").toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forXMLName("something").toString());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forXMLName("th:something").toString());
        Assertions.assertEquals(
                "th", AttributeNames.forXMLName("th:something").getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", AttributeNames.forXMLName("SOMETHING").toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", AttributeNames.forXMLName("TH:SOMETHING").toString());
        Assertions.assertEquals(
                "TH", AttributeNames.forXMLName("TH:SOMETHING").getPrefix());
        Assertions.assertEquals(
                "{:something}", AttributeNames.forXMLName(":something").toString());
        Assertions.assertFalse(
                AttributeNames.forXMLName(":something").isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", AttributeNames.forXMLName("data-th-something").toString());
        Assertions.assertFalse(
                AttributeNames.forXMLName("data-th-something").isPrefixed());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forXMLName("data-something").toString());
        Assertions.assertEquals(
                "{xml:ns}", AttributeNames.forXMLName("xml:ns").toString());
        Assertions.assertEquals(
                "xml", AttributeNames.forXMLName("xml:ns").getPrefix());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forXMLName("xml:space").toString());
        Assertions.assertEquals(
                "{XML:SPACE}", AttributeNames.forXMLName("XML:SPACE").toString());
        Assertions.assertEquals(
                "XML", AttributeNames.forXMLName("XML:SPACE").getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", AttributeNames.forHTMLName("xmlns:th").toString());
        Assertions.assertEquals(
                "xmlns", AttributeNames.forXMLName("xmlns:th").getPrefix());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forXMLName("th","something").toString());

        Assertions.assertSame(
                AttributeNames.forXMLName("data-something"), AttributeNames.forXMLName("data-something"));
        Assertions.assertSame(
                AttributeNames.forXMLName("xmlns:th"), AttributeNames.forXMLName("xmlns:th"));
        Assertions.assertSame(
                AttributeNames.forXMLName("data-th-something"), AttributeNames.forXMLName("data-th-something"));
        Assertions.assertNotSame(
                AttributeNames.forXMLName("data-th-something"), AttributeNames.forXMLName("th:something"));
        Assertions.assertSame(
                AttributeNames.forXMLName("xmlns","th"), AttributeNames.forXMLName("xmlns","th"));
        Assertions.assertSame(
                AttributeNames.forXMLName("th","something"), AttributeNames.forXMLName("th","something"));
        Assertions.assertSame(
                AttributeNames.forXMLName("","something"), AttributeNames.forXMLName("something"));
        Assertions.assertSame(
                AttributeNames.forXMLName(null,"something"), AttributeNames.forXMLName("something"));
        Assertions.assertSame(
                AttributeNames.forXMLName("  ","something"), AttributeNames.forXMLName("something"));

        try {
            AttributeNames.forXMLName(null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forXMLName("");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forXMLName("t", "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forXMLName(" ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forXMLName("t", " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }


    @Test
    public void testTextBuffer() {
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forTextName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forTextName("something".toCharArray(), 0, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forTextName("abcdefghijkliklmnsomethingba".toCharArray(), 17, "something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forTextName("abcdefghijkliklmnth:somethingba".toCharArray(), 17, "th:something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forTextName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "th", AttributeNames.forTextName("th:something".toCharArray(), 0, "th:something".length()).getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", AttributeNames.forTextName("SOMETHING".toCharArray(), 0, "SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", AttributeNames.forTextName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).toString());
        Assertions.assertEquals(
                "TH", AttributeNames.forTextName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).getPrefix());
        Assertions.assertEquals(
                "{:something}", AttributeNames.forTextName(":something".toCharArray(), 0, ":something".length()).toString());
        Assertions.assertFalse(
                AttributeNames.forTextName(":something".toCharArray(), 0, ":something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", AttributeNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()).toString());
        Assertions.assertFalse(
                AttributeNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forTextName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertEquals(
                "{xml:ns}", AttributeNames.forTextName("xml:ns".toCharArray(), 0, "xml:ns".length()).toString());
        Assertions.assertEquals(
                "xml", AttributeNames.forTextName("xml:ns".toCharArray(), 0, "xml:ns".length()).getPrefix());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forTextName("xml:space".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{XML:SPACE}", AttributeNames.forTextName("XML:SPACE".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "XML", AttributeNames.forTextName("XML:SPACE".toCharArray(), 0, "xml:space".length()).getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", AttributeNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).toString());
        Assertions.assertEquals(
                "xmlns", AttributeNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).getPrefix());

        Assertions.assertSame(
                AttributeNames.forTextName("data-something".toCharArray(), 0, "data-something".length()), AttributeNames.forTextName("data-something".toCharArray(), 0, "data-something".length()));
        Assertions.assertSame(
                AttributeNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()), AttributeNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()));
        Assertions.assertSame(
                AttributeNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()), AttributeNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()));

        try {
            AttributeNames.forTextName(null, 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forTextName("".toCharArray(), 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forTextName(" ".toCharArray(), 0, 1);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }


    @Test
    public void testTextString() {
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forHTMLName(null, "data-something").toString());
        Assertions.assertNull(AttributeNames.forHTMLName(null, "data-something").getPrefix());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forTextName(null, "th:something").toString());
        Assertions.assertEquals(
                "{something}", AttributeNames.forTextName("something").toString());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forTextName("th:something").toString());
        Assertions.assertEquals(
                "th", AttributeNames.forTextName("th:something").getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", AttributeNames.forTextName("SOMETHING").toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", AttributeNames.forTextName("TH:SOMETHING").toString());
        Assertions.assertEquals(
                "TH", AttributeNames.forTextName("TH:SOMETHING").getPrefix());
        Assertions.assertEquals(
                "{:something}", AttributeNames.forTextName(":something").toString());
        Assertions.assertFalse(
                AttributeNames.forTextName(":something").isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", AttributeNames.forTextName("data-th-something").toString());
        Assertions.assertFalse(
                AttributeNames.forTextName("data-th-something").isPrefixed());
        Assertions.assertEquals(
                "{data-something}", AttributeNames.forTextName("data-something").toString());
        Assertions.assertEquals(
                "{xml:ns}", AttributeNames.forTextName("xml:ns").toString());
        Assertions.assertEquals(
                "xml", AttributeNames.forTextName("xml:ns").getPrefix());
        Assertions.assertEquals(
                "{xml:space}", AttributeNames.forTextName("xml:space").toString());
        Assertions.assertEquals(
                "{XML:SPACE}", AttributeNames.forTextName("XML:SPACE").toString());
        Assertions.assertEquals(
                "XML", AttributeNames.forTextName("XML:SPACE").getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", AttributeNames.forHTMLName("xmlns:th").toString());
        Assertions.assertEquals(
                "xmlns", AttributeNames.forTextName("xmlns:th").getPrefix());
        Assertions.assertEquals(
                "{th:something}", AttributeNames.forTextName("th","something").toString());

        Assertions.assertSame(
                AttributeNames.forTextName("data-something"), AttributeNames.forTextName("data-something"));
        Assertions.assertSame(
                AttributeNames.forTextName("xmlns:th"), AttributeNames.forTextName("xmlns:th"));
        Assertions.assertSame(
                AttributeNames.forTextName("data-th-something"), AttributeNames.forTextName("data-th-something"));
        Assertions.assertNotSame(
                AttributeNames.forTextName("data-th-something"), AttributeNames.forTextName("th:something"));
        Assertions.assertSame(
                AttributeNames.forTextName("xmlns","th"), AttributeNames.forTextName("xmlns","th"));
        Assertions.assertSame(
                AttributeNames.forTextName("th","something"), AttributeNames.forTextName("th","something"));
        Assertions.assertSame(
                AttributeNames.forTextName("","something"), AttributeNames.forTextName("something"));
        Assertions.assertSame(
                AttributeNames.forTextName(null,"something"), AttributeNames.forTextName("something"));
        Assertions.assertSame(
                AttributeNames.forTextName("  ","something"), AttributeNames.forTextName("something"));

        try {
            AttributeNames.forTextName(null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forTextName("");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forTextName("t", "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forTextName(" ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            AttributeNames.forTextName("t", " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

    }


}

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


public final class ElementNamesTest {



    @Test
    public void testHTMLBuffer() {
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forHTMLName("something".toCharArray(), 0, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forHTMLName("abcdefghijkliklmnsomething".toCharArray(), 17, "something".length()).toString());
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("abcdefghijkliklmnth:something".toCharArray(), 17, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forHTMLName("SOMETHING".toCharArray(), 0, "SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{:something}", ElementNames.forHTMLName(":something".toCharArray(), 0, ":something".length()).toString());
        Assertions.assertEquals(
                "{data:th-something,data-th-something}", ElementNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()).toString());
        Assertions.assertEquals(
                "{data:something,data-something}", ElementNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertEquals(
                "{xml:ns}", ElementNames.forHTMLName("xml:ns".toCharArray(), 0, "xml:ns".length()).toString());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forHTMLName("xml:space".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forHTMLName("XML:SPACE".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{xmlns:th}", ElementNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).toString());

        Assertions.assertSame(
                ElementNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()), ElementNames.forHTMLName("data-something".toCharArray(), 0, "data-something".length()));
        Assertions.assertSame(
                ElementNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()), ElementNames.forHTMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()));
        Assertions.assertSame(
                ElementNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), ElementNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()));
        Assertions.assertSame(
                ElementNames.forHTMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), ElementNames.forHTMLName("DATA-TH-SOMETHING".toCharArray(), 0, "data-th-something".length()));

        try {
            ElementNames.forHTMLName(null, 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forHTMLName("".toCharArray(), 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forHTMLName(" ".toCharArray(), 0, 1);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }



    @Test
    public void testHTMLString() {
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName(null, "th:something").toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forHTMLName("something").toString());
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("th:something").toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forHTMLName("SOMETHING").toString());
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("TH:SOMETHING").toString());
        Assertions.assertEquals(
                "{:something}", ElementNames.forHTMLName(":something").toString());
        Assertions.assertEquals(
                "{data:th-something,data-th-something}", ElementNames.forHTMLName("data-th-something").toString());
        Assertions.assertEquals(
                "{data:something,data-something}", ElementNames.forHTMLName("data-something").toString());
        Assertions.assertEquals(
                "{xml:ns}", ElementNames.forHTMLName("xml:ns").toString());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forHTMLName("xml:space").toString());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forHTMLName("XML:SPACE").toString());
        Assertions.assertEquals(
                "{xmlns:th}", ElementNames.forHTMLName("xmlns:th").toString());
        Assertions.assertEquals(
                "{th:something,th-something}", ElementNames.forHTMLName("th","something").toString());

        Assertions.assertSame(
                ElementNames.forHTMLName("data-something"), ElementNames.forHTMLName("data-something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("xmlns:th"), ElementNames.forHTMLName("xmlns:th"));
        Assertions.assertSame(
                ElementNames.forHTMLName("data-th-something"), ElementNames.forHTMLName("data-th-something"));
        Assertions.assertNotSame(
                ElementNames.forHTMLName("data-th-something"), ElementNames.forHTMLName("th:something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("th-something"), ElementNames.forHTMLName("th:something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("xmlns","th"), ElementNames.forHTMLName("xmlns","th"));
        Assertions.assertSame(
                ElementNames.forHTMLName("th","something"), ElementNames.forHTMLName("th","something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("","something"), ElementNames.forHTMLName("something"));
        Assertions.assertSame(
                ElementNames.forHTMLName(null,"something"), ElementNames.forHTMLName("something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("  ","something"), ElementNames.forHTMLName("something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("  ","SOMETHING"), ElementNames.forHTMLName("something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("TH-SOMETHING"), ElementNames.forHTMLName("th:something"));
        Assertions.assertSame(
                ElementNames.forHTMLName("XMLNS","TH"), ElementNames.forHTMLName("xmlns","th"));
        Assertions.assertSame(
                ElementNames.forHTMLName("th-something"), ElementNames.forHTMLName("TH:SOMETHING"));

        try {
            ElementNames.forHTMLName(null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forHTMLName("");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forHTMLName("t", "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forHTMLName(" ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forHTMLName("t", " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }


    @Test
    public void testXMLBuffer() {
        Assertions.assertEquals(
                "{th:something}", ElementNames.forXMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forXMLName("something".toCharArray(), 0, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forXMLName("abcdefghijkliklmnsomething".toCharArray(), 17, "something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forXMLName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forXMLName("abcdefghijkliklmnth:something".toCharArray(), 17, "th:something".length()).toString());
        Assertions.assertEquals(
                "th", ElementNames.forXMLName("th:something".toCharArray(), 0, "th:something".length()).getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", ElementNames.forXMLName("SOMETHING".toCharArray(), 0, "SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", ElementNames.forXMLName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).toString());
        Assertions.assertEquals(
                "TH", ElementNames.forXMLName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).getPrefix());
        Assertions.assertEquals(
                "{:something}", ElementNames.forXMLName(":something".toCharArray(), 0, ":something".length()).toString());
        Assertions.assertFalse(
                ElementNames.forXMLName(":something".toCharArray(), 0, ":something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", ElementNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()).toString());
        Assertions.assertFalse(
                ElementNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-something}", ElementNames.forXMLName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertEquals(
                "{data:something}", ElementNames.forXMLName("data:something").toString());
        Assertions.assertEquals(
                "data", ElementNames.forXMLName("data:something").getPrefix());
        Assertions.assertEquals(
                "{xml:ns}", ElementNames.forXMLName("xml:ns".toCharArray(), 0, "xml:ns".length()).toString());
        Assertions.assertEquals(
                "xml", ElementNames.forXMLName("xml:ns".toCharArray(), 0, "xml:ns".length()).getPrefix());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forXMLName("xml:space".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{XML:SPACE}", ElementNames.forXMLName("XML:SPACE".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "XML", ElementNames.forXMLName("XML:SPACE".toCharArray(), 0, "xml:space".length()).getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", ElementNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).toString());
        Assertions.assertEquals(
                "xmlns", ElementNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).getPrefix());

        Assertions.assertSame(
                ElementNames.forXMLName("data-something".toCharArray(), 0, "data-something".length()), ElementNames.forXMLName("data-something".toCharArray(), 0, "data-something".length()));
        Assertions.assertSame(
                ElementNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()), ElementNames.forXMLName("xmlns:th".toCharArray(), 0, "xmlns:th".length()));
        Assertions.assertSame(
                ElementNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), ElementNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()));
        Assertions.assertNotSame(
                ElementNames.forXMLName("data-th-something".toCharArray(), 0, "data-th-something".length()), ElementNames.forXMLName("DATA-TH-SOMETHING".toCharArray(), 0, "data-th-something".length()));

        try {
            ElementNames.forXMLName(null, 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forXMLName("".toCharArray(), 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forXMLName(" ".toCharArray(), 0, 1);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }


    @Test
    public void testXMLString() {
        Assertions.assertEquals(
                "{th:something}", ElementNames.forXMLName(null, "th:something").toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forXMLName("something").toString());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forXMLName("th:something").toString());
        Assertions.assertEquals(
                "th", ElementNames.forXMLName("th:something").getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", ElementNames.forXMLName("SOMETHING").toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", ElementNames.forXMLName("TH:SOMETHING").toString());
        Assertions.assertEquals(
                "TH", ElementNames.forXMLName("TH:SOMETHING").getPrefix());
        Assertions.assertEquals(
                "{:something}", ElementNames.forXMLName(":something").toString());
        Assertions.assertFalse(
                ElementNames.forXMLName(":something").isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", ElementNames.forXMLName("data-th-something").toString());
        Assertions.assertFalse(
                ElementNames.forXMLName("data-th-something").isPrefixed());
        Assertions.assertEquals(
                "{data-something}", ElementNames.forXMLName("data-something").toString());
        Assertions.assertEquals(
                "{data:something}", ElementNames.forXMLName("data:something").toString());
        Assertions.assertEquals(
                "data", ElementNames.forXMLName("data:something").getPrefix());
        Assertions.assertEquals(
                "{xml:ns}", ElementNames.forXMLName("xml:ns").toString());
        Assertions.assertEquals(
                "xml", ElementNames.forXMLName("xml:ns").getPrefix());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forXMLName("xml:space").toString());
        Assertions.assertEquals(
                "{XML:SPACE}", ElementNames.forXMLName("XML:SPACE").toString());
        Assertions.assertEquals(
                "XML", ElementNames.forXMLName("XML:SPACE").getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", ElementNames.forXMLName("xmlns:th").toString());
        Assertions.assertEquals(
                "xmlns", ElementNames.forXMLName("xmlns:th").getPrefix());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forXMLName("th","something").toString());

        Assertions.assertSame(
                ElementNames.forXMLName("data-something"), ElementNames.forXMLName("data-something"));
        Assertions.assertSame(
                ElementNames.forXMLName("xmlns:th"), ElementNames.forXMLName("xmlns:th"));
        Assertions.assertSame(
                ElementNames.forXMLName("data-th-something"), ElementNames.forXMLName("data-th-something"));
        Assertions.assertNotSame(
                ElementNames.forXMLName("data-th-something"), ElementNames.forXMLName("th:something"));
        Assertions.assertSame(
                ElementNames.forXMLName("xmlns","th"), ElementNames.forXMLName("xmlns","th"));
        Assertions.assertSame(
                ElementNames.forXMLName("th","something"), ElementNames.forXMLName("th","something"));
        Assertions.assertSame(
                ElementNames.forXMLName("","something"), ElementNames.forXMLName("something"));
        Assertions.assertSame(
                ElementNames.forXMLName(null,"something"), ElementNames.forXMLName("something"));
        Assertions.assertSame(
                ElementNames.forXMLName("  ","something"), ElementNames.forXMLName("something"));

        try {
            ElementNames.forXMLName(null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forXMLName("");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forXMLName("t", "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forXMLName(" ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forXMLName("t", " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }


    @Test
    public void testTextBuffer() {
        Assertions.assertEquals(
                "{th:something}", ElementNames.forTextName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forTextName("something".toCharArray(), 0, "something".length()).toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forTextName("abcdefghijkliklmnsomething".toCharArray(), 17, "something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forTextName("th:something".toCharArray(), 0, "th:something".length()).toString());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forTextName("abcdefghijkliklmnth:something".toCharArray(), 17, "th:something".length()).toString());
        Assertions.assertEquals(
                "th", ElementNames.forTextName("th:something".toCharArray(), 0, "th:something".length()).getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", ElementNames.forTextName("SOMETHING".toCharArray(), 0, "SOMETHING".length()).toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", ElementNames.forTextName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).toString());
        Assertions.assertEquals(
                "TH", ElementNames.forTextName("TH:SOMETHING".toCharArray(), 0, "TH:SOMETHING".length()).getPrefix());
        Assertions.assertEquals(
                "{:something}", ElementNames.forTextName(":something".toCharArray(), 0, ":something".length()).toString());
        Assertions.assertFalse(
                ElementNames.forTextName(":something".toCharArray(), 0, ":something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", ElementNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()).toString());
        Assertions.assertFalse(
                ElementNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()).isPrefixed());
        Assertions.assertEquals(
                "{data-something}", ElementNames.forTextName("data-something".toCharArray(), 0, "data-something".length()).toString());
        Assertions.assertEquals(
                "{data:something}", ElementNames.forTextName("data:something").toString());
        Assertions.assertEquals(
                "data", ElementNames.forTextName("data:something").getPrefix());
        Assertions.assertEquals(
                "{xml:ns}", ElementNames.forTextName("xml:ns".toCharArray(), 0, "xml:ns".length()).toString());
        Assertions.assertEquals(
                "xml", ElementNames.forTextName("xml:ns".toCharArray(), 0, "xml:ns".length()).getPrefix());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forTextName("xml:space".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "{XML:SPACE}", ElementNames.forTextName("XML:SPACE".toCharArray(), 0, "xml:space".length()).toString());
        Assertions.assertEquals(
                "XML", ElementNames.forTextName("XML:SPACE".toCharArray(), 0, "xml:space".length()).getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", ElementNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).toString());
        Assertions.assertEquals(
                "xmlns", ElementNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()).getPrefix());

        Assertions.assertSame(
                ElementNames.forTextName("data-something".toCharArray(), 0, "data-something".length()), ElementNames.forTextName("data-something".toCharArray(), 0, "data-something".length()));
        Assertions.assertSame(
                ElementNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()), ElementNames.forTextName("xmlns:th".toCharArray(), 0, "xmlns:th".length()));
        Assertions.assertSame(
                ElementNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()), ElementNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()));
        Assertions.assertNotSame(
                ElementNames.forTextName("data-th-something".toCharArray(), 0, "data-th-something".length()), ElementNames.forTextName("DATA-TH-SOMETHING".toCharArray(), 0, "data-th-something".length()));

        try {
            ElementNames.forTextName(null, 0, 0);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forTextName("".toCharArray(), 0, 0);
            Assertions.assertTrue(true); // In text mode, element names CAN have empty names
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(false);
        }

        try {
            ElementNames.forTextName(" ".toCharArray(), 0, 1);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }


    @Test
    public void testTextString() {
        Assertions.assertEquals(
                "{th:something}", ElementNames.forTextName(null, "th:something").toString());
        Assertions.assertEquals(
                "{something}", ElementNames.forTextName("something").toString());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forTextName("th:something").toString());
        Assertions.assertEquals(
                "th", ElementNames.forTextName("th:something").getPrefix());
        Assertions.assertEquals(
                "{SOMETHING}", ElementNames.forTextName("SOMETHING").toString());
        Assertions.assertEquals(
                "{TH:SOMETHING}", ElementNames.forTextName("TH:SOMETHING").toString());
        Assertions.assertEquals(
                "TH", ElementNames.forTextName("TH:SOMETHING").getPrefix());
        Assertions.assertEquals(
                "{:something}", ElementNames.forTextName(":something").toString());
        Assertions.assertFalse(
                ElementNames.forTextName(":something").isPrefixed());
        Assertions.assertEquals(
                "{data-th-something}", ElementNames.forTextName("data-th-something").toString());
        Assertions.assertFalse(
                ElementNames.forTextName("data-th-something").isPrefixed());
        Assertions.assertEquals(
                "{data-something}", ElementNames.forTextName("data-something").toString());
        Assertions.assertEquals(
                "{data:something}", ElementNames.forTextName("data:something").toString());
        Assertions.assertEquals(
                "data", ElementNames.forTextName("data:something").getPrefix());
        Assertions.assertEquals(
                "{xml:ns}", ElementNames.forTextName("xml:ns").toString());
        Assertions.assertEquals(
                "xml", ElementNames.forTextName("xml:ns").getPrefix());
        Assertions.assertEquals(
                "{xml:space}", ElementNames.forTextName("xml:space").toString());
        Assertions.assertEquals(
                "{XML:SPACE}", ElementNames.forTextName("XML:SPACE").toString());
        Assertions.assertEquals(
                "XML", ElementNames.forTextName("XML:SPACE").getPrefix());
        Assertions.assertEquals(
                "{xmlns:th}", ElementNames.forTextName("xmlns:th").toString());
        Assertions.assertEquals(
                "xmlns", ElementNames.forTextName("xmlns:th").getPrefix());
        Assertions.assertEquals(
                "{th:something}", ElementNames.forTextName("th","something").toString());

        Assertions.assertSame(
                ElementNames.forTextName("data-something"), ElementNames.forTextName("data-something"));
        Assertions.assertSame(
                ElementNames.forTextName("xmlns:th"), ElementNames.forTextName("xmlns:th"));
        Assertions.assertSame(
                ElementNames.forTextName("data-th-something"), ElementNames.forTextName("data-th-something"));
        Assertions.assertNotSame(
                ElementNames.forTextName("data-th-something"), ElementNames.forTextName("th:something"));
        Assertions.assertSame(
                ElementNames.forTextName("xmlns","th"), ElementNames.forTextName("xmlns","th"));
        Assertions.assertSame(
                ElementNames.forTextName("th","something"), ElementNames.forTextName("th","something"));
        Assertions.assertSame(
                ElementNames.forTextName("","something"), ElementNames.forTextName("something"));
        Assertions.assertSame(
                ElementNames.forTextName(null,"something"), ElementNames.forTextName("something"));
        Assertions.assertSame(
                ElementNames.forTextName("  ","something"), ElementNames.forTextName("something"));

        try {
            ElementNames.forTextName(null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forTextName("");
            Assertions.assertTrue(true);
        } catch (final IllegalArgumentException e) {
            // Empty-name elements ARE allowed in TEXT modes
            Assertions.assertTrue(false);
        }

        try {
            ElementNames.forTextName("t", "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forTextName(" ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            ElementNames.forTextName("t", " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }
    }


}

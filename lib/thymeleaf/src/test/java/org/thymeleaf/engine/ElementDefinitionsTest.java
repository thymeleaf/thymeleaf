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

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public final class ElementDefinitionsTest {



    @Test
    public void test() {

        final ElementDefinitions elementDefinitions = new ElementDefinitions(Collections.EMPTY_MAP);

        final int standardSize = ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES.size();
        Assertions.assertEquals(standardSize, elementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES.size());

        for (final String name : ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES) {
            final ElementDefinition def1 = elementDefinitions.forHTMLName(name);
            final ElementDefinition def2 = elementDefinitions.forHTMLName(name);
            final ElementDefinition def3 = elementDefinitions.forHTMLName(name.toUpperCase());
            Assertions.assertSame(def1, def2);
            Assertions.assertSame(def2, def3);
        }
        for (final String name : ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES) {
            final ElementDefinition def1 = elementDefinitions.forXMLName(name);
            final ElementDefinition def2 = elementDefinitions.forXMLName(name);
            final ElementDefinition def3 = elementDefinitions.forXMLName(name.toUpperCase());
            Assertions.assertSame(def1, def2);
            Assertions.assertNotSame(def2, def3);
            Assertions.assertNotEquals(def2, def3);
        }

        final ElementDefinition new1 = elementDefinitions.forHTMLName("NEW");
        Assertions.assertNotNull(new1);
        Assertions.assertEquals("new", new1.getElementName().getElementName());
        final ElementDefinition new2 = elementDefinitions.forHTMLName("new");
        Assertions.assertSame(new1, new2);
        final ElementDefinition new3 = elementDefinitions.forHTMLName("NeW");
        Assertions.assertSame(new1, new3);
        final ElementDefinition new4 = elementDefinitions.forXMLName("NeW");
        Assertions.assertNotSame(new1, new4);
        final ElementDefinition new5 = elementDefinitions.forXMLName("new");
        Assertions.assertNotSame(new1, new5);
        final ElementDefinition new6 = elementDefinitions.forXMLName("new");
        Assertions.assertSame(new5, new6);
        final ElementDefinition new7 = elementDefinitions.forHTMLName("new");
        Assertions.assertSame(new1, new7);

        Assertions.assertEquals(standardSize, ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES.size());
        Assertions.assertEquals(standardSize, ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES.size());
        Assertions.assertFalse(ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES.contains("new"));
        Assertions.assertFalse(ElementDefinitions.ALL_STANDARD_HTML_ELEMENT_NAMES.contains(new1.getElementName().elementName));

        ElementDefinition thtextDefinition_3 = elementDefinitions.forHTMLName("t", "text");
        Assertions.assertEquals("{t:text,t-text}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName(null, "text");
        Assertions.assertEquals("{text}", thtextDefinition_3.getElementName().toString());
        ElementDefinition thtextDefinition_4 = elementDefinitions.forHTMLName("text");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{text}", thtextDefinition_4.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName("thhhh", "text");
        Assertions.assertEquals("{thhhh:text,thhhh-text}", thtextDefinition_3.getElementName().toString());

        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "t");
        Assertions.assertEquals("{t:t,t-t}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName(null, "t");
        Assertions.assertEquals("{t}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_4 = elementDefinitions.forHTMLName("t");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{t}", thtextDefinition_4.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName("thhhh", "teeee");
        Assertions.assertEquals("{thhhh:teeee,thhhh-teeee}", thtextDefinition_3.getElementName().toString());

        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "te");
        Assertions.assertEquals("{t:te,t-te}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName(null, "te");
        Assertions.assertEquals("{te}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_4 = elementDefinitions.forHTMLName("te");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{te}", thtextDefinition_4.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_3.getElementName().toString());

        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "ta");
        Assertions.assertEquals("{t:ta,t-ta}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName(null, "ta");
        Assertions.assertEquals("{ta}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_4 = elementDefinitions.forHTMLName("ta");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{ta}", thtextDefinition_4.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_3.getElementName().toString());

        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "ti");
        Assertions.assertEquals("{t:ti,t-ti}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName(null, "ti");
        Assertions.assertEquals("{ti}", thtextDefinition_3.getElementName().toString());
        thtextDefinition_4 = elementDefinitions.forHTMLName("ti");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{ti}", thtextDefinition_4.getElementName().toString());
        thtextDefinition_3 = elementDefinitions.forHTMLName("t", "teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_3.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName("t:teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_4.getElementName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "t:teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_4.getElementName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        thtextDefinition_4 = elementDefinitions.forHTMLName("t-teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_4.getElementName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "t-teeee");
        Assertions.assertEquals("{t:teeee,t-teeee}", thtextDefinition_4.getElementName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        try {
            thtextDefinition_4 = elementDefinitions.forHTMLName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = elementDefinitions.forHTMLName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = elementDefinitions.forHTMLName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = elementDefinitions.forXMLName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = elementDefinitions.forXMLName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = elementDefinitions.forXMLName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        thtextDefinition_4 = elementDefinitions.forTextName(null, "");
        Assertions.assertEquals("{}", thtextDefinition_4.getElementName().toString());

        try {
            thtextDefinition_4 = elementDefinitions.forTextName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "");
        Assertions.assertEquals("{}", thtextDefinition_4.getElementName().toString());

        try {
            thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "");
        Assertions.assertEquals("{}", thtextDefinition_4.getElementName().toString());

        try {
            thtextDefinition_4 = elementDefinitions.forCSSName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee,data-teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee,dataa-teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "data:data");
        Assertions.assertEquals("{data:data,data-data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "DATA:TEEEE");
        Assertions.assertEquals("{data:teeee,data-teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "DATA");
        Assertions.assertEquals("{data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{dataa:teeee,dataa-teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forHTMLName(null, "DATA:DATA");
        Assertions.assertEquals("{data:data,data-data}", thtextDefinition_4.getElementName().toString());


        thtextDefinition_4 = elementDefinitions.forXMLName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forXMLName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getElementName().toString());


        thtextDefinition_4 = elementDefinitions.forTextName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forTextName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getElementName().toString());


        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forJavaScriptName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getElementName().toString());


        thtextDefinition_4 = elementDefinitions.forCSSName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getElementName().toString());

        thtextDefinition_4 = elementDefinitions.forCSSName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getElementName().toString());

    }



    @Test
    public void testEmptyPrefix() {

        final ElementDefinitions elementDefinitions = new ElementDefinitions(Collections.EMPTY_MAP);

        final ElementDefinition ed01 = elementDefinitions.forHTMLName("", "one");
        final ElementDefinition ed02 = elementDefinitions.forXMLName("", "one");
        final ElementDefinition ed03 = elementDefinitions.forTextName("", "one");
        final ElementDefinition ed04 = elementDefinitions.forJavaScriptName("", "one");
        final ElementDefinition ed05 = elementDefinitions.forCSSName("", "one");


        Assertions.assertEquals("{one}", ed01.getElementName().toString());
        Assertions.assertEquals("{one}", ed02.getElementName().toString());
        Assertions.assertEquals("{one}", ed03.getElementName().toString());
        Assertions.assertEquals("{one}", ed04.getElementName().toString());
        Assertions.assertEquals("{one}", ed05.getElementName().toString());

    }



    @Test
    public void testNullPrefix() {

        final ElementDefinitions elementDefinitions = new ElementDefinitions(Collections.EMPTY_MAP);

        final ElementDefinition ed01 = elementDefinitions.forHTMLName(null, "one");
        final ElementDefinition ed02 = elementDefinitions.forXMLName(null, "one");
        final ElementDefinition ed03 = elementDefinitions.forTextName(null, "one");
        final ElementDefinition ed04 = elementDefinitions.forJavaScriptName(null, "one");
        final ElementDefinition ed05 = elementDefinitions.forCSSName(null, "one");


        Assertions.assertEquals("{one}", ed01.getElementName().toString());
        Assertions.assertEquals("{one}", ed02.getElementName().toString());
        Assertions.assertEquals("{one}", ed03.getElementName().toString());
        Assertions.assertEquals("{one}", ed04.getElementName().toString());
        Assertions.assertEquals("{one}", ed05.getElementName().toString());

    }



    @Test
    public void testWhitespacePrefix() {

        final ElementDefinitions elementDefinitions = new ElementDefinitions(Collections.EMPTY_MAP);

        final ElementDefinition ed01 = elementDefinitions.forHTMLName(" ", "one");
        final ElementDefinition ed02 = elementDefinitions.forXMLName(" ", "one");
        final ElementDefinition ed03 = elementDefinitions.forTextName(" ", "one");
        final ElementDefinition ed04 = elementDefinitions.forJavaScriptName(" ", "one");
        final ElementDefinition ed05 = elementDefinitions.forCSSName(" ", "one");


        Assertions.assertEquals("{one}", ed01.getElementName().toString());
        Assertions.assertEquals("{one}", ed02.getElementName().toString());
        Assertions.assertEquals("{one}", ed03.getElementName().toString());
        Assertions.assertEquals("{one}", ed04.getElementName().toString());
        Assertions.assertEquals("{one}", ed05.getElementName().toString());

    }

}

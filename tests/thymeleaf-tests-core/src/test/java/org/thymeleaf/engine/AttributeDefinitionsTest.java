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


public final class AttributeDefinitionsTest {



    @Test
    public void test() {

        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        final int standardSize = AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size();
        Assertions.assertEquals(standardSize, AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size());

        for (final String name : AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            final AttributeDefinition def1 = attributeDefinitions.forHTMLName(name);
            final AttributeDefinition def2 = attributeDefinitions.forHTMLName(name);
            final AttributeDefinition def3 = attributeDefinitions.forHTMLName(name.toUpperCase());
            Assertions.assertSame(def1, def2);
            Assertions.assertSame(def2, def3);
        }
        for (final String name : AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES) {
            final AttributeDefinition def1 = attributeDefinitions.forXMLName(name);
            final AttributeDefinition def2 = attributeDefinitions.forXMLName(name);
            final AttributeDefinition def3 = attributeDefinitions.forXMLName(name.toUpperCase());
            Assertions.assertSame(def1, def2);
            Assertions.assertNotSame(def2, def3);
            Assertions.assertNotEquals(def2, def3);
        }

        final AttributeDefinition new1 = attributeDefinitions.forHTMLName("NEW");
        Assertions.assertNotNull(new1);
        Assertions.assertEquals("new", new1.getAttributeName().getAttributeName());
        final AttributeDefinition new2 = attributeDefinitions.forHTMLName("new");
        Assertions.assertSame(new1, new2);
        final AttributeDefinition new3 = attributeDefinitions.forHTMLName("NeW");
        Assertions.assertSame(new1, new3);
        final AttributeDefinition new4 = attributeDefinitions.forXMLName("NeW");
        Assertions.assertNotSame(new1, new4);
        final AttributeDefinition new5 = attributeDefinitions.forXMLName("new");
        Assertions.assertNotSame(new4, new5);
        final AttributeDefinition new6 = attributeDefinitions.forXMLName("new");
        Assertions.assertSame(new5, new6);
        final AttributeDefinition new7 = attributeDefinitions.forHTMLName("new");
        Assertions.assertSame(new1, new7);

        Assertions.assertEquals(standardSize, AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size());
        Assertions.assertEquals(standardSize, AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES.size());
        Assertions.assertFalse(AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES.contains("new"));
        Assertions.assertFalse(AttributeDefinitions.ALL_STANDARD_HTML_ATTRIBUTE_NAMES.contains(new1.getAttributeName().attributeName));

        final HTMLAttributeDefinition htmlIdDefinition = attributeDefinitions.forHTMLName("id");
        final HTMLAttributeDefinition htmlDisabledDefinition = attributeDefinitions.forHTMLName("disabled");
        Assertions.assertEquals("{disabled}", htmlDisabledDefinition.getAttributeName().toString());

        Assertions.assertFalse(htmlIdDefinition.isBooleanAttribute());
        Assertions.assertTrue(htmlDisabledDefinition.isBooleanAttribute());

        final AttributeDefinition thtextDefinition = attributeDefinitions.forHTMLName("th:text");
        Assertions.assertEquals("{th:text,data-th-text}", thtextDefinition.getAttributeName().toString());
        final AttributeDefinition thtextDefinition2 = attributeDefinitions.forHTMLName("th:text");
        final AttributeDefinition thtextDefinition3 = attributeDefinitions.forHTMLName("th:TEXT");
        final AttributeDefinition thtextDefinition4 = attributeDefinitions.forHTMLName("data-th-TEXT");
        Assertions.assertSame(thtextDefinition, thtextDefinition2);
        Assertions.assertSame(thtextDefinition, thtextDefinition3);
        Assertions.assertSame(thtextDefinition, thtextDefinition4);

        final AttributeDefinition xmlthtextDefinition = attributeDefinitions.forXMLName("th:text");
        Assertions.assertEquals("{th:text}", xmlthtextDefinition.getAttributeName().toString());
        final AttributeDefinition xmlthtextDefinition2 = attributeDefinitions.forXMLName("th:text");
        final AttributeDefinition xmlthtextDefinition3 = attributeDefinitions.forXMLName("th:TEXT");
        Assertions.assertEquals("{th:TEXT}", xmlthtextDefinition3.getAttributeName().toString());
        final AttributeDefinition xmlthtextDefinition4 = attributeDefinitions.forXMLName("data-th-TEXT");
        Assertions.assertEquals("{data-th-TEXT}", xmlthtextDefinition4.getAttributeName().toString());
        Assertions.assertSame(xmlthtextDefinition, xmlthtextDefinition2);
        Assertions.assertNotSame(xmlthtextDefinition, xmlthtextDefinition3);
        Assertions.assertNotSame(xmlthtextDefinition, xmlthtextDefinition4);

        final AttributeDefinition thtextDefinition_2 = attributeDefinitions.forHTMLName("th", "text");
        Assertions.assertEquals("{th:text,data-th-text}", thtextDefinition_2.getAttributeName().toString());
        final AttributeDefinition thtextDefinition2_2 = attributeDefinitions.forHTMLName("th:text");
        final AttributeDefinition thtextDefinition3_2 = attributeDefinitions.forHTMLName("th:TEXT");
        final AttributeDefinition thtextDefinition4_2 = attributeDefinitions.forHTMLName("data-th-TEXT");
        Assertions.assertSame(thtextDefinition_2, thtextDefinition2_2);
        Assertions.assertSame(thtextDefinition_2, thtextDefinition3_2);
        Assertions.assertSame(thtextDefinition_2, thtextDefinition4_2);

        final AttributeDefinition xmlthtextDefinition_2 = attributeDefinitions.forXMLName("th", "text");
        Assertions.assertEquals("{th:text}", xmlthtextDefinition_2.getAttributeName().toString());
        final AttributeDefinition xmlthtextDefinition2_2 = attributeDefinitions.forXMLName("th:text");
        final AttributeDefinition xmlthtextDefinition3_2 = attributeDefinitions.forXMLName("th:TEXT");
        Assertions.assertEquals("{th:TEXT}", xmlthtextDefinition3_2.getAttributeName().toString());
        final AttributeDefinition xmlthtextDefinition4_2 = attributeDefinitions.forXMLName("data-th-TEXT");
        Assertions.assertEquals("{data-th-TEXT}", xmlthtextDefinition4_2.getAttributeName().toString());
        Assertions.assertSame(xmlthtextDefinition_2, xmlthtextDefinition2_2);
        Assertions.assertNotSame(xmlthtextDefinition_2, xmlthtextDefinition3_2);
        Assertions.assertNotSame(xmlthtextDefinition_2, xmlthtextDefinition4_2);

        AttributeDefinition thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "text");
        Assertions.assertEquals("{t:text,data-t-text}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName(null, "text");
        Assertions.assertEquals("{text}", thtextDefinition_3.getAttributeName().toString());
        AttributeDefinition thtextDefinition_4 = attributeDefinitions.forHTMLName("text");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{text}", thtextDefinition_4.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName("thhhh", "text");
        Assertions.assertEquals("{thhhh:text,data-thhhh-text}", thtextDefinition_3.getAttributeName().toString());

        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "t");
        Assertions.assertEquals("{t:t,data-t-t}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName(null, "t");
        Assertions.assertEquals("{t}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_4 = attributeDefinitions.forHTMLName("t");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{t}", thtextDefinition_4.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName("thhhh", "teeee");
        Assertions.assertEquals("{thhhh:teeee,data-thhhh-teeee}", thtextDefinition_3.getAttributeName().toString());

        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "te");
        Assertions.assertEquals("{t:te,data-t-te}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName(null, "te");
        Assertions.assertEquals("{te}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_4 = attributeDefinitions.forHTMLName("te");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{te}", thtextDefinition_4.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_3.getAttributeName().toString());

        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "ta");
        Assertions.assertEquals("{t:ta,data-t-ta}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName(null, "ta");
        Assertions.assertEquals("{ta}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_4 = attributeDefinitions.forHTMLName("ta");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{ta}", thtextDefinition_4.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_3.getAttributeName().toString());

        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "ti");
        Assertions.assertEquals("{t:ti,data-t-ti}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName(null, "ti");
        Assertions.assertEquals("{ti}", thtextDefinition_3.getAttributeName().toString());
        thtextDefinition_4 = attributeDefinitions.forHTMLName("ti");
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);
        Assertions.assertEquals("{ti}", thtextDefinition_4.getAttributeName().toString());
        thtextDefinition_3 = attributeDefinitions.forHTMLName("t", "teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_3.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName("t:teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_4.getAttributeName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "t:teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_4.getAttributeName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        thtextDefinition_4 = attributeDefinitions.forHTMLName("data-t-teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_4.getAttributeName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "data-t-teeee");
        Assertions.assertEquals("{t:teeee,data-t-teeee}", thtextDefinition_4.getAttributeName().toString());
        Assertions.assertSame(thtextDefinition_3, thtextDefinition_4);

        try {
            thtextDefinition_4 = attributeDefinitions.forHTMLName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forHTMLName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forXMLName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forXMLName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forXMLName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forTextName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forTextName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forTextName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forCSSName(null, null);
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forCSSName(null, "");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        try {
            thtextDefinition_4 = attributeDefinitions.forCSSName(null, " ");
            Assertions.assertTrue(false);
        } catch (final IllegalArgumentException e) {
            Assertions.assertTrue(true);
        }

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee,data-data-teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee,data-dataa-teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "data:data");
        Assertions.assertEquals("{data:data,data-data-data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "DATA:TEEEE");
        Assertions.assertEquals("{data:teeee,data-data-teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "DATA");
        Assertions.assertEquals("{data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{dataa:teeee,data-dataa-teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forHTMLName(null, "DATA:DATA");
        Assertions.assertEquals("{data:data,data-data-data}", thtextDefinition_4.getAttributeName().toString());


        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forXMLName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getAttributeName().toString());


        thtextDefinition_4 = attributeDefinitions.forTextName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forTextName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getAttributeName().toString());


        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forJavaScriptName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getAttributeName().toString());


        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "data:teeee");
        Assertions.assertEquals("{data:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "data");
        Assertions.assertEquals("{data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "dataa:teeee");
        Assertions.assertEquals("{dataa:teeee}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "data:data");
        Assertions.assertEquals("{data:data}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "DATA:TEEEE");
        Assertions.assertEquals("{DATA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "DATA");
        Assertions.assertEquals("{DATA}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "DATAA:TEEEE");
        Assertions.assertEquals("{DATAA:TEEEE}", thtextDefinition_4.getAttributeName().toString());

        thtextDefinition_4 = attributeDefinitions.forCSSName(null, "DATA:DATA");
        Assertions.assertEquals("{DATA:DATA}", thtextDefinition_4.getAttributeName().toString());

    }



    @Test
    public void testEmptyPrefix() {

        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        final AttributeDefinition ad01 = attributeDefinitions.forHTMLName("", "one");
        final AttributeDefinition ad02 = attributeDefinitions.forXMLName("", "one");
        final AttributeDefinition ad03 = attributeDefinitions.forTextName("", "one");
        final AttributeDefinition ad04 = attributeDefinitions.forJavaScriptName("", "one");
        final AttributeDefinition ad05 = attributeDefinitions.forCSSName("", "one");


        Assertions.assertEquals("{one}", ad01.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad02.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad03.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad04.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad05.getAttributeName().toString());

    }



    @Test
    public void testNullPrefix() {

        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        final AttributeDefinition ad01 = attributeDefinitions.forHTMLName(null, "one");
        final AttributeDefinition ad02 = attributeDefinitions.forXMLName(null, "one");
        final AttributeDefinition ad03 = attributeDefinitions.forTextName(null, "one");
        final AttributeDefinition ad04 = attributeDefinitions.forJavaScriptName(null, "one");
        final AttributeDefinition ad05 = attributeDefinitions.forCSSName(null, "one");


        Assertions.assertEquals("{one}", ad01.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad02.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad03.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad04.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad05.getAttributeName().toString());

    }



    @Test
    public void testWhitespacePrefix() {

        final AttributeDefinitions attributeDefinitions = new AttributeDefinitions(Collections.EMPTY_MAP);

        final AttributeDefinition ad01 = attributeDefinitions.forHTMLName(" ", "one");
        final AttributeDefinition ad02 = attributeDefinitions.forXMLName(" ", "one");
        final AttributeDefinition ad03 = attributeDefinitions.forTextName(" ", "one");
        final AttributeDefinition ad04 = attributeDefinitions.forJavaScriptName(" ", "one");
        final AttributeDefinition ad05 = attributeDefinitions.forCSSName(" ", "one");


        Assertions.assertEquals("{one}", ad01.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad02.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad03.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad04.getAttributeName().toString());
        Assertions.assertEquals("{one}", ad05.getAttributeName().toString());

    }



    
}

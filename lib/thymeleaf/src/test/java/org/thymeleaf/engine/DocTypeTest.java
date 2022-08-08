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


public final class DocTypeTest {


    @Test
    public void test() {

        final String doctypeHTML5UC = "<!DOCTYPE html>";
        final String doctypeHTML5LC = "<!doctype html>";

        final String doctypeXHTMLTransitional = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        final String doctypeXHTMLTransitionalWS = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" [\n <!-- an internal subset can be embedded here -->\n ]>";
        final String doctypeXHTMLTransitionalKLC = "<!doctype html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

        final String keywordUC = "DOCTYPE";
        final String keywordLC = "doctype";

        final String elementNameHtml = "html";

        final String typePublicUC = "PUBLIC";
        final String typeSystemUC = "SYSTEM";

        final String publicIdXHTMLTransitional = "-//W3C//DTD XHTML 1.0 Transitional//EN";
        final String systemIdXHTMLTransitional = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";

        final String internalSubsetWSXHTMLTransitional = "\n <!-- an internal subset can be embedded here -->\n ";

        DocType d1 =
                new DocType(
                    doctypeXHTMLTransitionalWS,
                    keywordUC,
                    elementNameHtml,
                    publicIdXHTMLTransitional,
                    systemIdXHTMLTransitional,
                    internalSubsetWSXHTMLTransitional,
                    "template", 11, 4);

        Assertions.assertSame(doctypeXHTMLTransitionalWS, d1.getDocType());
        Assertions.assertSame(keywordUC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertSame(typePublicUC, d1.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d1.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d1.getSystemId());
        Assertions.assertSame(internalSubsetWSXHTMLTransitional, d1.getInternalSubset());
        Assertions.assertEquals("template", d1.getTemplateName());
        Assertions.assertEquals(11, d1.getLine());
        Assertions.assertEquals(4, d1.getCol());

        d1 = new DocType(
                doctypeXHTMLTransitionalWS,
                keywordUC,
                elementNameHtml,
                publicIdXHTMLTransitional,
                systemIdXHTMLTransitional,
                internalSubsetWSXHTMLTransitional,
                "template", 10, 3);

        Assertions.assertSame(doctypeXHTMLTransitionalWS, d1.getDocType());
        Assertions.assertSame(keywordUC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertSame(typePublicUC, d1.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d1.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d1.getSystemId());
        Assertions.assertSame(internalSubsetWSXHTMLTransitional, d1.getInternalSubset());
        Assertions.assertEquals("template", d1.getTemplateName());
        Assertions.assertEquals(10, d1.getLine());
        Assertions.assertEquals(3, d1.getCol());

        d1 = new DocType(
                d1.getKeyword(),
                d1.getElementName(),
                d1.getPublicId(),
                d1.getSystemId(),
                null
        );

        Assertions.assertEquals(doctypeXHTMLTransitional, d1.getDocType());
        Assertions.assertSame(keywordUC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertSame(typePublicUC, d1.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d1.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d1.getSystemId());
        Assertions.assertNull(d1.getInternalSubset());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new DocType(
                keywordLC,
                d1.getElementName(),
                d1.getPublicId(),
                d1.getSystemId(),
                d1.getInternalSubset()
        );

        Assertions.assertEquals(doctypeXHTMLTransitionalKLC, d1.getDocType());
        Assertions.assertSame(keywordLC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertSame(typePublicUC, d1.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d1.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d1.getSystemId());
        Assertions.assertNull(d1.getInternalSubset());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new DocType(
                d1.getKeyword(),
                d1.getElementName(),
                "something", "someother",
                d1.getInternalSubset()
        );

        Assertions.assertEquals("<!doctype html PUBLIC \"something\" \"someother\">", d1.getDocType());
        Assertions.assertSame(keywordLC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertEquals(typePublicUC, d1.getType());
        Assertions.assertSame("something", d1.getPublicId());
        Assertions.assertSame("someother", d1.getSystemId());
        Assertions.assertNull(d1.getInternalSubset());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new DocType(
                d1.getKeyword(),
                d1.getElementName(),
                null, "someother",
                d1.getInternalSubset()
        );

        Assertions.assertEquals("<!doctype html SYSTEM \"someother\">", d1.getDocType());
        Assertions.assertSame(keywordLC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertEquals(typeSystemUC, d1.getType());
        Assertions.assertNull(d1.getPublicId());
        Assertions.assertSame("someother", d1.getSystemId());
        Assertions.assertNull(d1.getInternalSubset());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new DocType(
                d1.getKeyword(),
                d1.getElementName(),
                null, "someother",
                d1.getInternalSubset()
        );

        Assertions.assertEquals("<!doctype html SYSTEM \"someother\">", d1.getDocType());
        Assertions.assertSame(keywordLC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertEquals("SYSTEM", d1.getType());
        Assertions.assertNull(d1.getPublicId());
        Assertions.assertSame("someother", d1.getSystemId());
        Assertions.assertNull(d1.getInternalSubset());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new DocType(null, null);

        Assertions.assertEquals(doctypeHTML5UC, d1.getDocType());
        Assertions.assertSame(keywordUC, d1.getKeyword());
        Assertions.assertSame(elementNameHtml, d1.getElementName());
        Assertions.assertNull(d1.getType());
        Assertions.assertNull(d1.getPublicId());
        Assertions.assertNull(d1.getSystemId());
        Assertions.assertNull(d1.getInternalSubset());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());


        DocType d2 =
                new DocType(
                    keywordUC,
                    elementNameHtml,
                    publicIdXHTMLTransitional,
                    systemIdXHTMLTransitional,
                    internalSubsetWSXHTMLTransitional);

        Assertions.assertEquals(doctypeXHTMLTransitionalWS, d2.getDocType());
        Assertions.assertSame(keywordUC, d2.getKeyword());
        Assertions.assertSame(elementNameHtml, d2.getElementName());
        Assertions.assertSame(typePublicUC, d2.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d2.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d2.getSystemId());
        Assertions.assertSame(internalSubsetWSXHTMLTransitional, d2.getInternalSubset());
        Assertions.assertNull(d2.getTemplateName());
        Assertions.assertEquals(-1, d2.getLine());
        Assertions.assertEquals(-1, d2.getCol());

        d2 = new DocType(
                d2.getKeyword(),
                d2.getElementName(),
                d2.getPublicId(),
                d2.getSystemId(),
                null);

        Assertions.assertEquals(doctypeXHTMLTransitional, d2.getDocType());
        Assertions.assertSame(keywordUC, d2.getKeyword());
        Assertions.assertSame(elementNameHtml, d2.getElementName());
        Assertions.assertSame(typePublicUC, d2.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d2.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d2.getSystemId());
        Assertions.assertNull(d2.getInternalSubset());
        Assertions.assertNull(d2.getTemplateName());
        Assertions.assertEquals(-1, d2.getLine());
        Assertions.assertEquals(-1, d2.getCol());



        DocType d3 = new DocType(
                publicIdXHTMLTransitional,
                systemIdXHTMLTransitional);

        Assertions.assertEquals(doctypeXHTMLTransitional, d3.getDocType());
        Assertions.assertEquals(keywordUC, d3.getKeyword());
        Assertions.assertEquals(elementNameHtml, d3.getElementName());
        Assertions.assertEquals(typePublicUC, d3.getType());
        Assertions.assertSame(publicIdXHTMLTransitional, d3.getPublicId());
        Assertions.assertSame(systemIdXHTMLTransitional, d3.getSystemId());
        Assertions.assertNull(d3.getInternalSubset());
        Assertions.assertNull(d3.getTemplateName());
        Assertions.assertEquals(-1, d3.getLine());
        Assertions.assertEquals(-1, d3.getCol());


        DocType d4 =
                new DocType(null, null);

        Assertions.assertEquals(doctypeHTML5UC, d4.getDocType());
        Assertions.assertEquals(keywordUC, d4.getKeyword());
        Assertions.assertEquals(elementNameHtml, d4.getElementName());
        Assertions.assertNull(d4.getType());
        Assertions.assertNull(d4.getPublicId());
        Assertions.assertNull(d4.getSystemId());
        Assertions.assertNull(d4.getInternalSubset());
        Assertions.assertNull(d4.getTemplateName());
        Assertions.assertEquals(-1, d4.getLine());
        Assertions.assertEquals(-1, d4.getCol());

    }



    
}

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


public final class XmlDeclarationTest {


    @Test
    public void test() {

        final String keyword = XMLDeclaration.DEFAULT_KEYWORD;

        final String xmlDeclar1utfno = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>";
        final String xmlDeclar1utf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        final String xmlDeclar1 = "<?xml version=\"1.0\"?>";

        final String xmlDeclar1isono = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>";
        final String xmlDeclar11isoyes = "<?xml version=\"1.1\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";

        final String version1 = "1.0";
        final String version11 = "1.1";
        final String encodingUtf = "UTF-8";
        final String encodingIso = "ISO-8859-1";
        final String standaloneno = "no";
        final String standaloneyes = "yes";


        XMLDeclaration d1 =
                new XMLDeclaration(
                    xmlDeclar1utfno,
                    keyword,
                    version1,
                    encodingUtf,
                    standaloneno,
                    "template", 11, 4);

        Assertions.assertSame(xmlDeclar1utfno, d1.getXmlDeclaration());
        Assertions.assertSame(keyword, d1.getKeyword());
        Assertions.assertSame(version1, d1.getVersion());
        Assertions.assertSame(encodingUtf, d1.getEncoding());
        Assertions.assertSame(standaloneno, d1.getStandalone());
        Assertions.assertEquals("template", d1.getTemplateName());
        Assertions.assertEquals(11, d1.getLine());
        Assertions.assertEquals(4, d1.getCol());

        d1 = new XMLDeclaration(
                    xmlDeclar1utfno,
                    keyword,
                    version1,
                    encodingUtf,
                    standaloneno,
                    "template", 10, 3);

        Assertions.assertSame(xmlDeclar1utfno, d1.getXmlDeclaration());
        Assertions.assertSame(keyword, d1.getKeyword());
        Assertions.assertSame(version1, d1.getVersion());
        Assertions.assertSame(encodingUtf, d1.getEncoding());
        Assertions.assertSame(standaloneno, d1.getStandalone());
        Assertions.assertEquals("template", d1.getTemplateName());
        Assertions.assertEquals(10, d1.getLine());
        Assertions.assertEquals(3, d1.getCol());

        d1 = new XMLDeclaration(
                d1.getKeyword(),
                d1.getVersion(),
                d1.getEncoding(),
                null);

        Assertions.assertEquals(xmlDeclar1utf, d1.getXmlDeclaration());
        Assertions.assertSame(keyword, d1.getKeyword());
        Assertions.assertSame(version1, d1.getVersion());
        Assertions.assertSame(encodingUtf, d1.getEncoding());
        Assertions.assertNull(d1.getStandalone());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new XMLDeclaration(
                d1.getKeyword(),
                d1.getVersion(),
                null,
                d1.getStandalone());

        Assertions.assertEquals(xmlDeclar1, d1.getXmlDeclaration());
        Assertions.assertSame(keyword, d1.getKeyword());
        Assertions.assertSame(version1, d1.getVersion());
        Assertions.assertNull(d1.getEncoding());
        Assertions.assertNull(d1.getStandalone());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());

        d1 = new XMLDeclaration(
                d1.getKeyword(),
                version11,
                encodingIso,
                standaloneyes);

        Assertions.assertEquals(xmlDeclar11isoyes, d1.getXmlDeclaration());
        Assertions.assertSame(keyword, d1.getKeyword());
        Assertions.assertSame(version11, d1.getVersion());
        Assertions.assertSame(encodingIso, d1.getEncoding());
        Assertions.assertSame(standaloneyes, d1.getStandalone());
        Assertions.assertNull(d1.getTemplateName());
        Assertions.assertEquals(-1, d1.getLine());
        Assertions.assertEquals(-1, d1.getCol());



        XMLDeclaration d2 = new XMLDeclaration(
                XMLDeclaration.DEFAULT_KEYWORD,
                version1,
                encodingIso,
                standaloneno);

        Assertions.assertEquals(xmlDeclar1isono, d2.getXmlDeclaration());
        Assertions.assertSame(version1, d2.getVersion());
        Assertions.assertSame(encodingIso, d2.getEncoding());
        Assertions.assertSame(standaloneno, d2.getStandalone());
        Assertions.assertNull(d2.getTemplateName());
        Assertions.assertEquals(-1, d2.getLine());
        Assertions.assertEquals(-1, d2.getCol());



        XMLDeclaration d3 = new XMLDeclaration(
                XMLDeclaration.DEFAULT_KEYWORD,
                version1,
                null,
                null);

        Assertions.assertEquals(xmlDeclar1, d3.getXmlDeclaration());
        Assertions.assertSame(version1, d3.getVersion());
        Assertions.assertNull(d3.getEncoding());
        Assertions.assertNull(d3.getStandalone());
        Assertions.assertNull(d3.getTemplateName());
        Assertions.assertEquals(-1, d3.getLine());
        Assertions.assertEquals(-1, d3.getCol());


    }



    
}

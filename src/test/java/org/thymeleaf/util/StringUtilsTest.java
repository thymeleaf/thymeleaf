/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Le Roux Bernard
 * @since 1.1.2
 *
 */
//CHECKSTYLE:OFF
public class StringUtilsTest {
    /**
     * constructor.
     */
    public StringUtilsTest() {
        super();
    }

    /**
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        // empty block
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // empty block
    }
    @Before
    public void setUp() {
        // empty block
    }
    @After
    public void tearDown() {
        // empty block
    }

    /**
     * Test of capitalize method, of class StringUtils.
     */
    @Test
    public final void testCapitalize1() {
        Object target = "abc";
        String expResult = "Abc";
        String result = StringUtils.capitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public final void testCapitalize2() {
        Object target = "          abc";
        String expResult = "          abc";
        String result = StringUtils.capitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public final void testCapitalize3() {
        Object target = "";
        String expResult = "";
        String result = StringUtils.capitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public final void testCapitalize4() {
        Object target = null;
        String result = StringUtils.capitalize(target);
        assertEquals(result, null);
    }
    @Test
    public void testCapitalize5() {
        Object target = "          Abc";
        String expResult = "          Abc";
        String result = StringUtils.capitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalize6() {
        Object target = "abc def";
        String expResult = "Abc def";
        String result = StringUtils.capitalize(target);
        assertEquals(expResult, result);
    }
    /**
     * Test of unCapitalize method, of class StringUtils.
     */
    @Test
    public void testUnCapitalize1() {
        Object target = "ABC";
        String expResult = "aBC";
        String result = StringUtils.unCapitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public void testUnCapitalize2() {
        Object target = "          ABC";
        String expResult = "          ABC";
        String result = StringUtils.unCapitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public void testUnCapitalize3() {
        Object target = "";
        String expResult = "";
        String result = StringUtils.unCapitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public void testUnCapitalize4() {
        Object target = null;
        String result = StringUtils.unCapitalize(target);
        assertEquals(result, null);
    }
    @Test
    public void testUnCapitalize5() {
        Object target = "          Abc";
        String expResult = "          Abc";
        String result = StringUtils.unCapitalize(target);
        assertEquals(expResult, result);
    }
    @Test
    public void testUnCapitalize6() {
        Object target = "Abc Def";
        String expResult = "abc Def";
        String result = StringUtils.unCapitalize(target);
        assertEquals(expResult, result);
    }
    /**
     * Test of capitalizeWords method, of class StringUtils.
     */
    @Test
    public void testCapitalizeWords1() {
        Object s = "";
        String expResult = "";
        String result = StringUtils.capitalizeWords(s);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords2() {
        Object s = "   ";
        String expResult = "   ";
        String result = StringUtils.capitalizeWords(s);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords3() {
        Object s = "a";
        String expResult = "A";
        String result = StringUtils.capitalizeWords(s);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords4() {
        Object s = "A";
        String expResult = "A";
        String result = StringUtils.capitalizeWords(s);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords5() {
        Object s = "aaa bbb ccc";
        String expResult = "Aaa Bbb Ccc";
        String result = StringUtils.capitalizeWords(s);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords6() {
        Object s = "aaa   bbb   ccc";
        String expResult = "Aaa   Bbb   Ccc";
        String result = StringUtils.capitalizeWords(s);
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords7() {
        Object s = "a.ze tyu iop";
        String expResult = "A.ze Tyu Iop";
        String result = StringUtils.capitalizeWords(s, " ");
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords8() {
        Object s = "a....ze       tyu     iop";
        String expResult = "A....Ze       Tyu     Iop";
        String result = StringUtils.capitalizeWords(s, " .");
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords9() {
        Object s = "     aaaaa....zzzzz       ttttt     nnnnn";
        String expResult = "     Aaaaa....Zzzzz       Ttttt     Nnnnn";
        String result = StringUtils.capitalizeWords(s, " .");
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords10() {
        Object s = "     aaaaa....zzzzz       ttttt     nnnnn   ";
        String expResult = "     Aaaaa....Zzzzz       Ttttt     Nnnnn   ";
        String result = StringUtils.capitalizeWords(s, " .");
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords11() {
        Object s = "";
        String expResult = "";
        String result = StringUtils.capitalizeWords(s," .");
        assertEquals(expResult, result);
    }
    @Test
    public void testCapitalizeWords12() {
        Object s = null;
        String result = StringUtils.capitalizeWords(s);
        assertEquals(result, null);
    }
    @Test
    public void testCapitalizeWords13() {
        Object s = null;
        String result = StringUtils.capitalizeWords(s," .");
        assertEquals(result, null);
    }
    /**
     * Test of substring method, of class StringUtils.
     */
    @Test
    public void testSubstring1() {
        Object s = "abcdef";
        String expResult = "abcdef";
        String result = StringUtils.substring(s, 0);
        assertEquals(expResult, result);
    }
    @Test
    public void testSubstring2() {
        Object s = "abcdef";
        String expResult = "cdef";
        String result = StringUtils.substring(s, 2);
        assertEquals(expResult, result);
    }
    @Test(expected= IllegalArgumentException.class)
    public void testSubstring3() {
        Object s = null;
        String result = StringUtils.substring(s, 2);
    }
    @Test(expected= IllegalArgumentException.class)
    public void testSubstring4() {
        Object s = "abcdef";
        String result = StringUtils.substring(s, -2);
    }
    @Test(expected= IllegalArgumentException.class)
    public void testSubstring5() {
        Object s = "abcdef";
        String result = StringUtils.substring(s, 7);
    }
    public void testEscapeJavaScriptUrl1() {
        Object s = "http://www.test.com";
        String expResult = "http://www.test.com";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl2() {
        Object s = "https://www.test.com/someContext";
        String expResult = "https://www.test.com/someContext";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl3() {
        Object s = "https://www.test.com/someContext?param=value";
        String expResult = "https://www.test.com/someContext?param=value";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl4() {
        Object s = "https://www.test.com/someContext/?param=value";
        String expResult = "https://www.test.com/someContext/?param=value";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl5() {
        Object s = "https://www.test.com/someContext/?param=value&otherparam=value";
        String expResult = "https://www.test.com/someContext/?param=value&otherparam=value";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl6() {
        Object s = "ftp://username:pass@ftp.test.com/someContext/?param=value";
        String expResult = "ftp://username:pass@ftp.test.com/someContext/?param=value";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl7() {
        Object s = "ftp://username:pass@ftp.test.com/someContext/?param=${value}";
        String expResult = "ftp://username:pass@ftp.test.com/someContext/?param=${value}";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
    public void testEscapeJavaScriptUrl8() {
        Object s = "ftp://username:pass@ftp.test.com/${someContext}/?param=${value}";
        String expResult = "ftp://username:pass@ftp.test.com/${someContext}/?param=${value}";
        String result = StringUtils.escapeJavaScript(s);
        assertEquals(expResult, result);
    }
}
//CHECKSTYLE:ON

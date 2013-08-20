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
package org.thymeleaf.standard.expression;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public class LiteralSubstitutionUtilTest extends TestCase {



    public LiteralSubstitutionUtilTest() {
        super();
    }
    
    
    public void testLiteralSubstitution() throws Exception {
        
        test("|${one} ${two}|", "${one} + ' ' + ${two}");
        test("|${one}      |", "${one} + '      '");
        test("|     ${one}      |", "'     ' + ${one} + '      '");
        test("|     ${one}|", "'     ' + ${one}");
        test("|${one} et ${two}|", "${one} + ' et ' + ${two}");
        test("|Welcome, ${one} to application with name #{two}|", "'Welcome, ' + ${one} + ' to application with name ' + #{two}");
        test("${one}", "${one}");
        test("'lalala'", "'lalala'");
        test("null", "null");
        test("null and token", "null and token");
        test("4123.4l and token", "4123.4l and token");
        test("'Sum: ' + (10 + 2)", "'Sum: ' + (10 + 2)");
        test("'Sum: ' + |10 + 2|", "'Sum: ' + '10 + 2'");
        test("'Sum: ' + |10 + 'aaa 2|", "'Sum: ' + '10 + \\'aaa 2'");
        test("|Sum: | + |10 + 2|", "'Sum: ' + '10 + 2'");
        test("|Welcome, | + |${one}| + | to application| + ' with' + | name #{two}|",
             "'Welcome, ' + ${one} + ' to application' + ' with' + ' name ' + #{two}");

    }



    private void test(final String input, final String result) {
        final String output = LiteralSubstitutionUtil.performLiteralSubstitution(input);
        Assert.assertEquals(result, output);
        if (output.equals(input)) {
            Assert.assertTrue(input == output);
        }
    }
    

}

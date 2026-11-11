/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.expression;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;


public class SPELExpressionRestrictionsTest {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();


    private static boolean check(final String expression) {
        return SPELExpressionRestrictions.containsExternalAccessOutsideStringLiterals(PARSER, expression);
    }


    @Test
    public void testExpressionsThatCannotBeChecked() {
        // Syntax errors
        Assertions.assertTrue(check("'new x' +"));
        Assertions.assertTrue(check("'abc new x"));
        Assertions.assertTrue(check("'new x'' + 1"));
        // Nesting too deep to be parsed
        final StringBuilder strBuilder = new StringBuilder("'new x' + ");
        for (int i = 0; i < 100000; i++) {
            strBuilder.append('(');
        }
        strBuilder.append('1');
        for (int i = 0; i < 100000; i++) {
            strBuilder.append(')');
        }
        Assertions.assertTrue(check(strBuilder.toString()));
    }


    @Test
    public void testExpressionsThatCanBeChecked() {
        Assertions.assertFalse(check("'new x'"));
        Assertions.assertFalse(check("{'New x', 'param', 'T (x)', '@b'}.size()"));
        Assertions.assertTrue(check("'new x' + new java.util.ArrayList()"));
        Assertions.assertTrue(check("'new x' + T(java.lang.Math).abs(1)"));
        Assertions.assertTrue(check("'new x' + param.x"));
    }


    private static String repeat(final String s, final int n) {
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            strBuilder.append(s);
        }
        return strBuilder.toString();
    }


    @Test
    public void testLimits() throws Exception {
        // Nesting
        Assertions.assertFalse(check("'new x' + " + repeat("(", 16) + "1" + repeat(")", 16)));
        Assertions.assertTrue(check("'new x' + " + repeat("(", 17) + "1" + repeat(")", 17)));
        // Operators ('+' plus the '!' operators)
        Assertions.assertFalse(check("'new x' + " + repeat("!", 127) + "true"));
        Assertions.assertTrue(check("'new x' + " + repeat("!", 128) + "true"));
        // Length outside string literals
        Assertions.assertFalse(check("'new x' + " + repeat("not ", 125) + "true"));
        Assertions.assertTrue(check("'new x' + " + repeat("not ", 127) + "true"));
        // String literal contents do not count (SpEL itself limits expressions to 10,000 chars)
        Assertions.assertFalse(check("'" + repeat("new x, a-b? c:d! (e) [f] {g} ", 200) + "'"));
        Assertions.assertFalse(check("{'" + repeat("((((new x", 100) + "'}.size()"));
    }


    @Test
    public void testLimitsDoNotExhaustSmallStacks() throws Exception {
        // Expression at all the limits at the same time, checked on a small thread stack
        final String expression =
                "'new x' + " + repeat("(", 16) + repeat("not ", 88) + repeat("!", 120) + "true" + repeat(")", 16);
        final boolean[] result = new boolean[] { true };
        final Thread thread =
                new Thread(null, () -> result[0] = check(expression), "small-stack", 256L * 1024L);
        thread.start();
        thread.join();
        Assertions.assertFalse(result[0]);
    }

}

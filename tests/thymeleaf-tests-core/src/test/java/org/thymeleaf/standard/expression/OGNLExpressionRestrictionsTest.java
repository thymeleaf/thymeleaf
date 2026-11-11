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
package org.thymeleaf.standard.expression;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class OGNLExpressionRestrictionsTest {


    @Test
    public void testNodeTypesMatchOgnl() throws Exception {

        // All the node types in the OGNL library must be known: any change in OGNL needs to be reviewed
        final Set<String> ognlNodeTypes = new TreeSet<String>();
        final URL location = ognl.Ognl.class.getProtectionDomain().getCodeSource().getLocation();
        final File file = new File(location.toURI());
        if (file.isDirectory()) {
            final File[] classFiles = new File(file, "ognl").listFiles();
            Assertions.assertNotNull(classFiles);
            for (final File classFile : classFiles) {
                addNodeType(ognlNodeTypes, "ognl/" + classFile.getName());
            }
        } else {
            try (final JarFile jarFile = new JarFile(file)) {
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    addNodeType(ognlNodeTypes, entries.nextElement().getName());
                }
            }
        }
        Assertions.assertTrue(ognlNodeTypes.size() > 40, ognlNodeTypes.toString());

        final Set<String> expected = new TreeSet<String>(ognlNodeTypes);
        expected.remove("ognl.ASTCtor");
        expected.remove("ognl.ASTStaticField");
        expected.remove("ognl.ASTStaticMethod");

        final Set<String> actual = new TreeSet<String>();
        for (final Class<?> nodeType : OGNLExpressionRestrictions.getNodeTypesWithoutExternalAccess()) {
            actual.add(nodeType.getName());
        }
        Assertions.assertEquals(expected, actual);

    }


    private static void addNodeType(final Set<String> nodeTypes, final String entryName) {
        if (entryName.startsWith("ognl/AST") && entryName.endsWith(".class") && entryName.indexOf('$') < 0) {
            nodeTypes.add(entryName.substring(0, entryName.length() - ".class".length()).replace('/', '.'));
        }
    }


    @Test
    public void testExpressionsThatCannotBeChecked() {
        // Syntax errors
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' +"));
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'abc new x"));
        // Malformed unicode escape (reported by OGNL as a java.lang.Error)
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + \\uZZZZ"));
        // Nesting too deep to be parsed
        final StringBuilder strBuilder = new StringBuilder("'new x' + ");
        for (int i = 0; i < 100000; i++) {
            strBuilder.append('(');
        }
        strBuilder.append('1');
        for (int i = 0; i < 100000; i++) {
            strBuilder.append(')');
        }
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals(strBuilder.toString()));
    }


    @Test
    public void testExpressionsThatCanBeChecked() {
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x'"));
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("{'New x', 'param'}.size()"));
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + new java.util.ArrayList()"));
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + param.x"));
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
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + " + repeat("(", 16) + "1" + repeat(")", 16)));
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + " + repeat("(", 17) + "1" + repeat(")", 17)));
        // Operators ('+' plus the '!' operators)
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + " + repeat("!", 127) + "true"));
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + " + repeat("!", 128) + "true"));
        // Length outside string literals
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + " + repeat("not ", 125) + "true"));
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'new x' + " + repeat("not ", 127) + "true"));
        // Backslashes escape the next char in OGNL literals: an even number of them does not escape the quote
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals(
                "'new x\\\\' + " + repeat("(", 17) + "1" + repeat(")", 17)));
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals(
                "'new x\\\\\\' + " + repeat("(", 17) + "1" + repeat(")", 17) + "'"));
        // Unicode escapes are not decoded when counting, but the result is still decided by the parsed expression
        Assertions.assertTrue(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals(
                "'new x\\u0027 + new java.util.ArrayList() + \\u0027'"));
        // String literal contents do not count
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("'" + repeat("new x, a-b? c:d! (e) [f] {g} ", 400) + "'"));
        Assertions.assertFalse(OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals("{'" + repeat("((((new x", 100) + "'}.size()"));
    }


    @Test
    public void testLimitsDoNotExhaustSmallStacks() throws Exception {
        // Expression at all the limits at the same time, checked on a small thread stack
        final String expression =
                "'new x' + " + repeat("(", 16) + repeat("not ", 88) + repeat("!", 120) + "true" + repeat(")", 16);
        final boolean[] result = new boolean[] { true };
        final Thread thread =
                new Thread(null, () -> result[0] = OGNLExpressionRestrictions.containsExternalAccessOutsideStringLiterals(expression), "small-stack", 256L * 1024L);
        thread.start();
        thread.join();
        Assertions.assertFalse(result[0]);
    }

}

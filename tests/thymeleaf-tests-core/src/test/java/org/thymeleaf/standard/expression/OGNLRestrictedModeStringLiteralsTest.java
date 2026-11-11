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

import java.util.Locale;
import java.util.Random;

import ognl.ASTConst;
import ognl.ASTCtor;
import ognl.ASTMap;
import ognl.ASTMethod;
import ognl.ASTProperty;
import ognl.ASTStaticField;
import ognl.ASTStaticMethod;
import ognl.ASTVarRef;
import ognl.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.standard.util.StandardExpressionUtils;


public class OGNLRestrictedModeStringLiteralsTest {

    private static final String FORBIDDEN_MESSAGE =
            "Instantiation of new objects and access to static classes or parameters is forbidden in this context";

    private static final TemplateEngine TEMPLATE_ENGINE = new TemplateEngine();
    private static final OGNLVariableExpressionEvaluator EVALUATOR = new OGNLVariableExpressionEvaluator(true);


    private static String processRestricted(final String expression) {
        final Object result =
                EVALUATOR.evaluate(
                        new ExpressionContext(TEMPLATE_ENGINE.getConfiguration(), Locale.US),
                        new VariableExpression(expression), StandardExpressionExecutionContext.RESTRICTED);
        return String.valueOf(result);
    }

    private static String rootMessage(final Throwable throwable) {
        Throwable t = throwable;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return String.valueOf(t.getMessage());
    }

    private static void assertForbidden(final String expression) {
        final Exception e = Assertions.assertThrows(Exception.class, () -> processRestricted(expression), expression);
        Assertions.assertTrue(rootMessage(e).startsWith(FORBIDDEN_MESSAGE), expression + " -> " + rootMessage(e));
    }



    @Test
    public void testStringLiteralContents() {
        Assertions.assertEquals("1", processRestricted("{'New promotion'}.size()"));
        Assertions.assertEquals("new promotion", processRestricted("'new promotion'"));
        Assertions.assertEquals("Add a new item", processRestricted("'Add a new item'"));
        Assertions.assertEquals("Search param", processRestricted("'Search param'"));
        Assertions.assertEquals("param", processRestricted("\"param\""));
        Assertions.assertEquals("a @b@ T (x)", processRestricted("'a @b@ T (x)'"));
        Assertions.assertEquals("xnew y", processRestricted("'x' + 'new y'"));
        Assertions.assertEquals("2 new", processRestricted("{'Home','/'}.size() + ' new'"));
        Assertions.assertEquals("new\tx", processRestricted("'new\tx'"));
        // Escaped quotes do not end the literal
        Assertions.assertEquals("a' + new java.util.ArrayList() + 'b",
                processRestricted("'a\\' + new java.util.ArrayList() + \\'b'"));
        Assertions.assertEquals("a\" + new java.util.ArrayList() + \"b",
                processRestricted("\"a\\\" + new java.util.ArrayList() + \\\"b\""));
        Assertions.assertEquals("a\\u0027 new x", processRestricted("'a\\\\u0027 new x'"));
        Assertions.assertEquals("1", processRestricted("{'param'}.size()"));
    }


    @Test
    public void testExternalAccessOutsideStringLiterals() {
        assertForbidden("new java.util.ArrayList()");
        assertForbidden("'new x' + new java.util.ArrayList()");
        assertForbidden("{'New', new java.util.ArrayList()}");
        assertForbidden("'new x' + new java.util.ArrayList[1]");
        assertForbidden("'x' + @java.lang.Math@abs(1)");
        assertForbidden("'x' + @java.lang.Math@PI");
        assertForbidden("'new x' + #@java.util.LinkedHashMap@{}");
        assertForbidden("'param' + param.x");
        assertForbidden("'new x' + #param");
        assertForbidden("'new x' + param()");
        assertForbidden("'new x' + #root['param']");
        assertForbidden("'new x' + #root['PARAM']");
        assertForbidden("'x'.concat(param.x)");
        assertForbidden("'new x' + #f = :[ new java.util.ArrayList() ]");
        // Unicode escapes are processed before the expression is tokenized
        assertForbidden("'a\\u0027 + new java.util.ArrayList() + \\u0027b'");
        assertForbidden("'new x' + n\\u0065w java.util.ArrayList()");
        assertForbidden("'new x' + p\\u0061ram.x");
        assertForbidden("'new x' + new\tjava.util.ArrayList()");
        assertForbidden("'\u0130' + new java.util.ArrayList()");
        assertForbidden("'\u0000new' + new java.util.ArrayList()");
    }


    @Test
    public void testUnparseableExpressions() {
        assertForbidden("'abc new java.util.ArrayList()");
        assertForbidden("'new x' +");
        assertForbidden("'new x\\' + new java.util.ArrayList()");
    }


    @Test
    public void testRandomExpressions() {

        final String[] literalContents = {
                "new ", "New promotion", "new java.util.ArrayList()", "@java.lang.Math@abs(1)", "@x", "param",
                "param.x", "'", "\"", "\\", "\\u0027", "\\u0022", "\u0000", "\t", "\u0130", " ", "x", "+", "(", ")" };
        final String[] externalTerms = {
                "new java.util.ArrayList()", "@java.lang.Math@abs(1)", "#@java.util.LinkedHashMap@{}", "param.x",
                "n\\u0065w java.util.ArrayList()" };
        final String[] safeTerms = { "1", "'ab'.length()", "{'x'}.size()" };
        final String[] mutations = { "'", "\"", "\\", " ", "\\u0027" };

        final Random random = new Random(1118L);

        for (int i = 0; i < 20000; i++) {

            final StringBuilder strBuilder = new StringBuilder();
            final int terms = 1 + random.nextInt(4);
            for (int t = 0; t < terms; t++) {
                if (t > 0) {
                    strBuilder.append(" + ");
                }
                final int kind = random.nextInt(4);
                if (kind < 2) {
                    final char quote = random.nextBoolean() ? '\'' : '"';
                    final StringBuilder content = new StringBuilder();
                    final int parts = random.nextInt(4);
                    for (int p = 0; p < parts; p++) {
                        content.append(literalContents[random.nextInt(literalContents.length)]);
                    }
                    final String escaped = content.toString().replace("\\", "\\\\").replace(String.valueOf(quote), "\\" + quote);
                    strBuilder.append(quote).append(escaped).append(quote);
                } else if (kind == 2) {
                    strBuilder.append(safeTerms[random.nextInt(safeTerms.length)]);
                } else {
                    strBuilder.append(externalTerms[random.nextInt(externalTerms.length)]);
                }
            }
            if (random.nextInt(4) == 0) {
                final int pos = random.nextInt(strBuilder.length() + 1);
                if (random.nextBoolean() && pos < strBuilder.length()) {
                    strBuilder.deleteCharAt(pos);
                } else {
                    strBuilder.insert(pos, mutations[random.nextInt(mutations.length)]);
                }
            }
            final String expression = strBuilder.toString();

            Node tree = null;
            try {
                tree = (Node) ognl.Ognl.parseExpression(expression);
            } catch (final Exception | Error e) {
                // Not parseable
            }

            String result = null;
            String message = null;
            try {
                result = processRestricted(expression);
            } catch (final Exception | Error e) {
                message = rootMessage(e);
            }

            if (tree == null) {
                Assertions.assertNull(result, expression);
                if (StandardExpressionUtils.containsExternalAccess(expression)) {
                    Assertions.assertTrue(message.startsWith(FORBIDDEN_MESSAGE), expression + " -> " + message);
                }
            } else if (containsConstructorOrStatic(tree)) {
                Assertions.assertNull(result, expression);
                Assertions.assertTrue(message.startsWith(FORBIDDEN_MESSAGE), expression + " -> " + message);
            } else if (containsParam(tree)) {
                Assertions.assertNull(result, expression);
            } else {
                Assertions.assertTrue(message == null || !message.startsWith(FORBIDDEN_MESSAGE), expression + " -> " + message);
            }

        }

    }


    private static boolean containsConstructorOrStatic(final Node node) {
        if (node instanceof ASTCtor || node instanceof ASTStaticMethod || node instanceof ASTStaticField
                || (node instanceof ASTMap && node.toString().startsWith("#@"))) {
            return true;
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (containsConstructorOrStatic(node.jjtGetChild(i))) {
                return true;
            }
        }
        return false;
    }


    private static boolean containsParam(final Node node) {
        if ((node instanceof ASTProperty && node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTConst
                    && "param".equalsIgnoreCase(String.valueOf(((ASTConst) node.jjtGetChild(0)).getValue())))
                || (node instanceof ASTMethod && "param".equalsIgnoreCase(((ASTMethod) node).getMethodName()))
                || (node instanceof ASTVarRef && "#param".equalsIgnoreCase(node.toString()))) {
            return true;
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (containsParam(node.jjtGetChild(i))) {
                return true;
            }
        }
        return false;
    }

}

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

import java.util.Locale;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.thymeleaf.context.ExpressionContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.util.SpringStandardExpressionUtils;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.templateresolver.StringTemplateResolver;


public class SPELRestrictedModeStringLiteralsTest {

    private static final String FORBIDDEN_MESSAGE =
            "Instantiation of new objects and access to static classes or parameters is forbidden in this context";

    private static final SpringTemplateEngine TEMPLATE_ENGINE;

    static {
        TEMPLATE_ENGINE = new SpringTemplateEngine();
        TEMPLATE_ENGINE.setTemplateResolver(new StringTemplateResolver());
    }


    private static String processRestricted(final String expression) {
        final Object result =
                SPELVariableExpressionEvaluator.INSTANCE.evaluate(
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
        Assertions.assertEquals("a @b T (x)", processRestricted("'a @b T (x)'"));
        Assertions.assertEquals("xnew y", processRestricted("'x' + 'new y'"));
        Assertions.assertEquals("2 new", processRestricted("{'Home','/'}.size() + ' new'"));
        Assertions.assertEquals("new\tx", processRestricted("'new\tx'"));
        Assertions.assertEquals("new x", processRestricted("'new' + \" x\""));
        // Doubled quotes are escaped quotes, so these are single literals
        Assertions.assertEquals("a' new java.util.ArrayList() 'b",
                processRestricted("'a'' new java.util.ArrayList() ''b'"));
        Assertions.assertEquals("a\" new java.util.ArrayList() \"b",
                processRestricted("\"a\"\" new java.util.ArrayList() \"\"b\""));
        // Matched by the first check, but only inside string literals
        Assertions.assertEquals("/img/new.png", processRestricted("'/img/new.png'"));
        Assertions.assertEquals("Tom & Jerry", processRestricted("'Tom & Jerry'"));
        Assertions.assertEquals("a@b.example", processRestricted("'a@b.example'"));
        Assertions.assertEquals("x param", processRestricted("'x ' + 'param'"));
        Assertions.assertEquals("false", processRestricted("'@' == 'x' && true"));
        // Unicode escapes are not processed by SpEL
        Assertions.assertEquals("a\\u0027 new x \\u0027b", processRestricted("'a\\u0027 new x \\u0027b'"));
    }


    @Test
    public void testExternalAccessOutsideStringLiterals() {
        assertForbidden("new java.util.ArrayList()");
        assertForbidden("'new x' + new java.util.ArrayList()");
        assertForbidden("{'New', new java.util.ArrayList()}");
        assertForbidden("'new x' + new java.util.ArrayList[1]");
        assertForbidden("'x' + T(java.lang.Math).abs(1)");
        assertForbidden("'T (x)' + t(java.lang.Math).abs(1)");
        assertForbidden("'@x' + @someBean");
        assertForbidden("'param' + param.x");
        assertForbidden("'x'.concat(param.x)");
        assertForbidden("'a''' + new java.util.ArrayList() + '''b'");
        assertForbidden("\"a\"\"\" + new java.util.ArrayList() + \"\"\"b\"");
        assertForbidden("'a\\' + new java.util.ArrayList() + '\\b'");
        assertForbidden("'new x' + new\tjava.util.ArrayList()");
        assertForbidden("'new x' + new\u000Bjava.util.ArrayList()");
        assertForbidden("'\u0130' + new java.util.ArrayList()");
        assertForbidden("'\u0000new' + new java.util.ArrayList()");
        assertForbidden("'new x' + NEW java.util.ArrayList()");
    }


    @Test
    public void testUnparseableExpressions() {
        assertForbidden("'abc new java.util.ArrayList()");
        assertForbidden("'new x' +");
        assertForbidden("'new x'' + new java.util.ArrayList()");
    }


    @Test
    public void testRandomExpressions() {

        final String[] literalContents = {
                "new ", "New promotion", "new java.util.ArrayList()", "T(java.lang.Math)", "T (x)", "@b", "param",
                "param.x", "'", "\"", "\\", "\\u0027", "\u0000", "\t", "\u0130", " ", "x", "+", "(", ")" };
        final String[] externalTerms = {
                "new java.util.ArrayList()", "T(java.lang.Math).abs(1)", "@someBean", "param.x" };
        final String[] safeTerms = { "1", "'a'.length()", "{'x'}.size()" };
        final char[] mutationChars = { '\'', '"', '\\', ' ' };

        final SpelExpressionParser parser = new SpelExpressionParser();
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
                    final String quoteText = String.valueOf(quote);
                    strBuilder.append(quote).append(content.toString().replace(quoteText, quoteText + quoteText)).append(quote);
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
                    strBuilder.insert(pos, mutationChars[random.nextInt(mutationChars.length)]);
                }
            }
            final String expression = strBuilder.toString();
            SpelNode ast = null;
            try {
                ast = ((SpelExpression) parser.parseExpression(expression)).getAST();
            } catch (final Exception e) {
                // Not parseable
            }

            String result = null;
            String message = null;
            try {
                result = processRestricted(expression);
            } catch (final Exception e) {
                message = rootMessage(e);
            }

            if (ast == null) {
                Assertions.assertNull(result, expression);
                if (SpringStandardExpressionUtils.containsExternalAccess(expression)) {
                    Assertions.assertTrue(message.startsWith(FORBIDDEN_MESSAGE), expression + " -> " + message);
                }
            } else if (containsConstructorTypeOrBean(ast)) {
                Assertions.assertNull(result, expression);
                if (SpringStandardExpressionUtils.containsExternalAccess(withoutStringLiteralContents(expression, ast))) {
                    Assertions.assertTrue(message.startsWith(FORBIDDEN_MESSAGE), expression + " -> " + message);
                }
            } else if (containsParam(ast)) {
                Assertions.assertNull(result, expression);
            } else {
                Assertions.assertTrue(message == null || !message.startsWith(FORBIDDEN_MESSAGE), expression + " -> " + message);
            }

        }

    }


    private static String withoutStringLiteralContents(final String expression, final SpelNode ast) {
        final char[] chars = expression.toCharArray();
        markStringLiteralContents(ast, chars);
        final StringBuilder strBuilder = new StringBuilder();
        for (final char c : chars) {
            if (c != '\uFFFF') {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }


    private static void markStringLiteralContents(final SpelNode node, final char[] chars) {
        if (node instanceof StringLiteral) {
            for (int i = node.getStartPosition() + 1; i < node.getEndPosition() - 1; i++) {
                chars[i] = '\uFFFF';
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            markStringLiteralContents(node.getChild(i), chars);
        }
    }


    private static boolean containsConstructorTypeOrBean(final SpelNode node) {
        if (node instanceof ConstructorReference || node instanceof TypeReference || node instanceof BeanReference) {
            return true;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (containsConstructorTypeOrBean(node.getChild(i))) {
                return true;
            }
        }
        return false;
    }


    private static boolean containsParam(final SpelNode node) {
        if ((node instanceof PropertyOrFieldReference && "param".equalsIgnoreCase(((PropertyOrFieldReference) node).getName()))
                || (node instanceof MethodReference && "param".equalsIgnoreCase(((MethodReference) node).getName()))
                || (node instanceof VariableReference && "#param".equalsIgnoreCase(node.toStringAST()))) {
            return true;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            if (containsParam(node.getChild(i))) {
                return true;
            }
        }
        return false;
    }

}

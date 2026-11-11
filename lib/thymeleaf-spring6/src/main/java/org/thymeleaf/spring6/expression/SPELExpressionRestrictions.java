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
package org.thymeleaf.spring6.expression;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.util.SpringStandardExpressionUtils;


/**
 * <p>
 *   Checks applied to SpEL expressions executed in restricted mode.
 * </p>
 * <p>
 *   Note this class is <strong>meant for internal use only</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.7
 */
final class SPELExpressionRestrictions {

    private static final Logger logger = LoggerFactory.getLogger(SPELExpressionRestrictions.class);

    // Limits on the structure of the checked expressions (outside string literals, which are only located
    // approximately for this), so that parsing them cannot exhaust the thread stack
    private static final int MAX_LENGTH = 512;
    private static final int MAX_NESTING = 16;
    private static final int MAX_OPERATORS = 128;
    private static final String OPERATOR_CHARS = "!-~?:=+*/%<>&|^";



    /*
     * Confirms a match of SpringStandardExpressionUtils.containsExternalAccess(...) on the expression text without
     * the contents of the string literals located by the specified parser (parsing does not evaluate anything). Any
     * expression that cannot be checked this way for whatever reason is considered a match.
     */
    static boolean containsExternalAccessOutsideStringLiterals(
            final SpelExpressionParser parser, final String spelExpression) {
        try {
            if (exceedsLimits(spelExpression)) {
                return couldNotBeChecked(spelExpression, null);
            }
            return StringLiteralCheck.containsExternalAccess(parser, spelExpression);
        } catch (final StackOverflowError e) {
            return couldNotBeChecked(spelExpression, e);
        } catch (final VirtualMachineError e) {
            throw e;
        } catch (final Throwable t) {
            return couldNotBeChecked(spelExpression, t);
        }
    }


    private static boolean exceedsLimits(final String spelExpression) {
        int length = 0;
        int nesting = 0;
        int maxNesting = 0;
        int operators = 0;
        char quote = 0;
        final int len = spelExpression.length();
        for (int i = 0; i < len; i++) {
            final char c = spelExpression.charAt(i);
            if (quote != 0) {
                if (c == quote) {
                    if (i + 1 < len && spelExpression.charAt(i + 1) == quote) {
                        i++;
                    } else {
                        quote = 0;
                    }
                }
                continue;
            }
            if (c == '\'' || c == '"') {
                quote = c;
                continue;
            }
            length++;
            if (c == '(' || c == '[' || c == '{') {
                nesting++;
                maxNesting = Math.max(maxNesting, nesting);
            } else if (c == ')' || c == ']' || c == '}') {
                nesting = Math.max(0, nesting - 1);
            } else if (OPERATOR_CHARS.indexOf(c) >= 0) {
                operators++;
            }
        }
        return length > MAX_LENGTH || maxNesting > MAX_NESTING || operators > MAX_OPERATORS;
    }


    private static boolean couldNotBeChecked(final String spelExpression, final Throwable t) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[THYMELEAF][{}] SpringEL expression \"{}\" could not be checked on its parsed form{}, so it will be " +
                    "considered to contain object instantiation, static class access or parameter access",
                    TemplateEngine.threadIndex(), spelExpression, (t == null ? " (structure exceeds the check limits)" : ""), t);
        }
        return true;
    }



    /*
     * All SpEL AST types are only referenced from here, so that any problems obtaining them happen during the check.
     */
    private static final class StringLiteralCheck {

        static boolean containsExternalAccess(final SpelExpressionParser parser, final String spelExpression) {

            final SpelNode ast = ((SpelExpression) parser.parseExpression(spelExpression)).getAST();

            final List<StringLiteral> literals = new ArrayList<StringLiteral>();
            collectStringLiterals(ast, literals);
            if (literals.isEmpty()) {
                return true;
            }
            literals.sort((l1, l2) -> Integer.compare(l1.getStartPosition(), l2.getStartPosition()));

            final int len = spelExpression.length();
            final StringBuilder strBuilder = new StringBuilder(len);
            int pos = 0;
            for (final StringLiteral literal : literals) {
                final int start = literal.getStartPosition();
                final int end = literal.getEndPosition();
                final String text = literal.getOriginalValue();
                if (text == null || start < pos || end > len || end - start < 2 || text.length() != end - start
                        || !spelExpression.startsWith(text, start)
                        || (text.charAt(0) != '\'' && text.charAt(0) != '"')
                        || text.charAt(text.length() - 1) != text.charAt(0)) {
                    throw new IllegalStateException(
                            "String literal at position " + start + " does not match the expression text");
                }
                // Quotes are kept so that the text around the literal is not joined
                strBuilder.append(spelExpression, pos, start + 1);
                strBuilder.append(text.charAt(0));
                pos = end;
            }
            strBuilder.append(spelExpression, pos, len);

            return SpringStandardExpressionUtils.containsExternalAccess(strBuilder.toString());

        }


        private static void collectStringLiterals(final SpelNode node, final List<StringLiteral> literals) {
            if (node.getClass().equals(StringLiteral.class)) {
                literals.add((StringLiteral) node);
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                collectStringLiterals(node.getChild(i), literals);
            }
        }


        private StringLiteralCheck() {
            super();
        }

    }



    private SPELExpressionRestrictions() {
        super();
    }

}

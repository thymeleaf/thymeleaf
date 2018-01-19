/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.1.0
 *
 */
final class LiteralSubstitutionUtil {

    private static final char LITERAL_SUBSTITUTION_DELIMITER = '|';




    /*
     * The goal here is to convert literal substitution expressions (|...|) into a concatenation of variable and/or
     * literal expressions.
     *
     * Example:
     * 
     * # ------------------------------------------------------------
     * %CONTEXT
     * onevar = 'Hello'
     * twovar = 'World'
     * # ------------------------------------------------------------
     * %INPUT
     * <p th:text="|${onevar} ${twovar}|">...</p>
     * # ------------------------------------------------------------
     * %OUTPUT
     * <p>Hello World</p>
     * # ------------------------------------------------------------
     *
     * For this, the input text is scanned before simple expressions are decomposed and they are replaced by a series
     * of concatenations of literals and variables.
     *
     * So: |${onevar} ${twovar}| --> ${onevar} + ' ' + ${twovar}
     *
     * NOTE literal substitution expressions do not allow literals, numeric/boolean tokens, conditional expressions, etc.
     *
     */

    static String performLiteralSubstitution(final String input) {

        if (input == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        boolean inLiteralSubstitution = false;
        boolean inLiteralSubstitutionInsertion = false;

        int expLevel = 0;
        boolean inLiteral = false;
        boolean inNothing = true;

        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {

            final char c = input.charAt(i);

            if (c == LITERAL_SUBSTITUTION_DELIMITER && !inLiteralSubstitution && inNothing) {

                if (strBuilder == null) {
                    strBuilder = new StringBuilder(inputLen + 20);
                    strBuilder.append(input,0,i);
                }
                inLiteralSubstitution = true;

            } else if (c == LITERAL_SUBSTITUTION_DELIMITER && inLiteralSubstitution && inNothing) {

                if (inLiteralSubstitutionInsertion) {
                    strBuilder.append('\'');
                    inLiteralSubstitutionInsertion = false;
                }

                inLiteralSubstitution = false;

            } else if (inNothing &&
                    (c == VariableExpression.SELECTOR ||
                            c == SelectionVariableExpression.SELECTOR ||
                            c == MessageExpression.SELECTOR ||
                            c == LinkExpression.SELECTOR) &&
                    (i + 1 < inputLen && input.charAt(i+1) == SimpleExpression.EXPRESSION_START_CHAR)) {
                // We are opening an expression

                if (inLiteralSubstitution && inLiteralSubstitutionInsertion) {
                    strBuilder.append("\' + ");
                    inLiteralSubstitutionInsertion = false;
                } else if (inLiteralSubstitution && i > 0 && input.charAt(i - 1) == SimpleExpression.EXPRESSION_END_CHAR) {
                    // This expression is right after another one, with no characters between them
                    strBuilder.append(" + \'\' + ");
                }

                if (strBuilder != null) {
                    strBuilder.append(c);
                    strBuilder.append(SimpleExpression.EXPRESSION_START_CHAR);
                }

                expLevel = 1;
                i++; // This avoids the following '{', which we already know is there, to increment expLevel twice
                inNothing = false;

            } else if (expLevel == 1 && c == SimpleExpression.EXPRESSION_END_CHAR) {
                // We are closing an expression

                if (strBuilder != null) {
                    strBuilder.append(SimpleExpression.EXPRESSION_END_CHAR);
                }

                expLevel = 0;
                inNothing = true;

            } else if (expLevel > 0 && c == SimpleExpression.EXPRESSION_START_CHAR) {
                // We are in an expression. This is needed for correct nesting/unnesting of expressions

                if (strBuilder != null) {
                    strBuilder.append(SimpleExpression.EXPRESSION_START_CHAR);
                }
                expLevel++;

            } else if (expLevel > 1 && c == SimpleExpression.EXPRESSION_END_CHAR) {
                // We are in an expression. This is needed for correct nesting/unnesting of expressions

                if (strBuilder != null) {
                    strBuilder.append(SimpleExpression.EXPRESSION_END_CHAR);
                }
                expLevel--;

            } else if (expLevel > 0) {
                // We are in an expression and not closing it, so just add the char

                if (strBuilder != null) {
                    strBuilder.append(c);
                }

            } else if (inNothing && !inLiteralSubstitution &&
                       c == TextLiteralExpression.DELIMITER && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                // We enter a first-level text literal. We should not process any |'s inside

                inNothing = false;
                inLiteral = true;

                if (strBuilder != null) {
                    strBuilder.append(c);
                }

            } else if (inLiteral && !inLiteralSubstitution &&
                       c == TextLiteralExpression.DELIMITER && !TextLiteralExpression.isDelimiterEscaped(input, i)) {

                inLiteral = false;
                inNothing = true;

                if (strBuilder != null) {
                    strBuilder.append(c);
                }

            } else if (inLiteralSubstitution && inNothing) {
                // This char is not starting an expresion, but it is inside a literal substitution, so add it
                // (and start an insertion if needed)

                if (!inLiteralSubstitutionInsertion) {
                    if (input.charAt(i - 1) != LITERAL_SUBSTITUTION_DELIMITER) {
                        strBuilder.append(" + ");
                    }
                    strBuilder.append('\'');
                    inLiteralSubstitutionInsertion = true;
                }

                if (c == TextLiteralExpression.DELIMITER) {
                    strBuilder.append('\\');
                } else if (c == TextLiteralExpression.ESCAPE_PREFIX) {
                    strBuilder.append('\\');
                }

                strBuilder.append(c);

            } else {
                // No literal substitution or anything. Just add the char

                if (strBuilder != null) {
                    strBuilder.append(c);
                }

            }

        }

        if (strBuilder == null) {
            return input;
        }

        return strBuilder.toString();

    }



    private LiteralSubstitutionUtil() {
        super();
    }
    
}

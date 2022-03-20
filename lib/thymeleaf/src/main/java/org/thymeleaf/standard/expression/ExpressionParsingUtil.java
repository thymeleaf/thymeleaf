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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 2.1.0
 *
 */
final class ExpressionParsingUtil {


    /*
     *  PARSING STANDARD EXPRESSIONS IS DONE IN TWO PHASES:
     *     1.   Decomposing the expression into its components, by replacing all simple expressions
     *          (${...}, *{...}, @{...}, #{...}, ~{...}, literals and tokens) by placeholders and moving these
     *          expressions apart to their own "parsing nodes" (elements in the ExpressionParsingNode list
     *          contained at the ExpressionParsingState).
     *     1.b. (normally executed at the same time as (1)): Decomposing parenthesis, so that the order of
     *          execution is correctly established.
     *     2.   Composing expressions, by parsing each node in the ExpressionParsingState resulting from steps
     *          1 and 1b, into Expressions objects, which substitute their unparsed Strings in the list of
     *          expression parsing nodes as they are being parsed. The objective is to get an Expression object
     *          (instead of a String) at the position 0 of the node list, which will mean everything has been
     *          correctly parsed.
     *
     *  NOTE 1: At any moment, decomposition or decomposition operations can return null, meaning the expression
     *          is syntactically incorrect.
     *
     *  NOTE 2: The approaches taken for decomposing and composing expressions are different: whereas decomposition is
     *          entirely made by this class, which knows the format of every possible simple expression enough,
     *          composition is delegated to each of the ComplexExpression implementations in turn until one of them
     *          considers the passed expression parsed. This allows a fast simple expression identification and
     *          parsing (simple expressions are the most common case) whereas allows enough flexibility for the
     *          addition of new complex expression types.
     */




    private static final String[] PROTECTED_TOKENS;



    static {
        /*
         * We must add here every expression operator that could be mistaken by a token
         * ("and", "ne", "eq", etc.)
         */
        final List<String> protectedTokenList = new ArrayList<String>(30);
        protectedTokenList.addAll(Arrays.asList(AndExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(EqualsNotEqualsExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(GreaterLesserExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(MultiplicationDivisionRemainderExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(NegationExpression.OPERATORS));
        protectedTokenList.addAll(Arrays.asList(OrExpression.OPERATORS));
        PROTECTED_TOKENS = protectedTokenList.toArray(new String[protectedTokenList.size()]);
    }



    public static ExpressionParsingState decompose(final String input) {
        // Just before starting decomposing simple expressions, we perform the processing of literal substitutions...
        final ExpressionParsingState state =
                decomposeSimpleExpressions(LiteralSubstitutionUtil.performLiteralSubstitution(input));
        return decomposeNestingParenthesis(state, 0);
    }




    private static ExpressionParsingState decomposeSimpleExpressions(final String input) {

        if (input == null) {
            return null;
        }

        final ExpressionParsingState state = new ExpressionParsingState();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            state.addNode(input);
            return state;
        }

        final StringBuilder decomposedInput = new StringBuilder(24);
        final StringBuilder currentFragment = new StringBuilder(24);
        int currentIndex = 1;

        int expLevel = 0;
        boolean inLiteral = false;
        boolean inToken = false;
        boolean inNothing = true;

        final int inputLen = input.length();
         for (int i = 0; i < inputLen; i++) {

            /*
             * First, we check for finishing tokens
             */
            if (inToken && !Token.isTokenChar(input, i)) {

                if (finishCurrentToken(currentIndex, state, decomposedInput, currentFragment) != null) {
                    // If it's not null, it means the token was really accepted as such, and an expression object
                    // was created for it. So we should increment the index.
                    currentIndex++;
                }

                inToken = false;
                inNothing = true;

            }


            /*
             * Once token end has been checked, process the current character
             */

            final char c = input.charAt(i);

            if (inNothing && c == TextLiteralExpression.DELIMITER && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                // We are opening a literal

                finishCurrentFragment(decomposedInput, currentFragment);

                currentFragment.append(c);

                inLiteral = true;
                inNothing = false;

            } else if (inLiteral && c == TextLiteralExpression.DELIMITER && !TextLiteralExpression.isDelimiterEscaped(input, i)) {
                // We are closing a literal

                currentFragment.append(c);

                final TextLiteralExpression expr =
                        TextLiteralExpression.parseTextLiteralExpression(currentFragment.toString());
                if (addExpressionAtIndex(expr, currentIndex++, state, decomposedInput, currentFragment) == null) {
                    return null;
                }

                inLiteral = false;
                inNothing = true;

            } else if (inLiteral) {
                // Nothing else to check, we are in literal and we know we are not closing it. Just add char.

                currentFragment.append(c);

            } else if (inNothing &&
                        (c == VariableExpression.SELECTOR ||
                         c == SelectionVariableExpression.SELECTOR ||
                         c == MessageExpression.SELECTOR ||
                         c == LinkExpression.SELECTOR ||
                         c == FragmentExpression.SELECTOR) &&
                        (i + 1 < inputLen && input.charAt(i+1) == SimpleExpression.EXPRESSION_START_CHAR)) {
                // We are opening an expression

                finishCurrentFragment(decomposedInput, currentFragment);

                currentFragment.append(c);
                currentFragment.append(SimpleExpression.EXPRESSION_START_CHAR);
                i++; // We already know what's at i+1

                expLevel = 1;
                inNothing = false;

            } else if (expLevel == 1 && c == SimpleExpression.EXPRESSION_END_CHAR) {
                // We are closing an expression

                currentFragment.append(SimpleExpression.EXPRESSION_END_CHAR);

                final char expSelectorChar = currentFragment.charAt(0);

                final Expression expr;
                switch (expSelectorChar) {
                    case VariableExpression.SELECTOR:
                        expr = VariableExpression.parseVariableExpression(currentFragment.toString()); break;
                    case SelectionVariableExpression.SELECTOR:
                        expr = SelectionVariableExpression.parseSelectionVariableExpression(currentFragment.toString()); break;
                    case MessageExpression.SELECTOR:
                        expr = MessageExpression.parseMessageExpression(currentFragment.toString()); break;
                    case LinkExpression.SELECTOR:
                        expr = LinkExpression.parseLinkExpression(currentFragment.toString()); break;
                    case FragmentExpression.SELECTOR:
                        expr = FragmentExpression.parseFragmentExpression(currentFragment.toString()); break;
                    default:
                        return null;
                }

                if (addExpressionAtIndex(expr, currentIndex++, state, decomposedInput, currentFragment) == null) {
                    return null;
                }

                expLevel = 0;
                inNothing = true;

            } else if (expLevel > 0 && c == SimpleExpression.EXPRESSION_START_CHAR) {
                // We are in an expression. This is needed for correct nesting/unnesting of expressions

                expLevel++;
                currentFragment.append(SimpleExpression.EXPRESSION_START_CHAR);

            } else if (expLevel > 1 && c == SimpleExpression.EXPRESSION_END_CHAR) {
                // We are in an expression. This is needed for correct nesting/unnesting of expressions

                expLevel--;
                currentFragment.append(SimpleExpression.EXPRESSION_END_CHAR);

            } else if (expLevel > 0) {
                // We are in an expression and not closing it, so just add the char

                currentFragment.append(c);

            } else if (inNothing && Token.isTokenChar(input, i)) {
                // We are opening a token

                finishCurrentFragment(decomposedInput, currentFragment);

                currentFragment.append(c);

                inToken = true;
                inNothing = false;

            } else {
                // We might be in a token or not. Doesn't matter. If we are,
                // at this point we already know c is a valid token char.

                currentFragment.append(c);

            }


        }

        if (inLiteral || expLevel > 0) {
            return null;
        }

        if (inToken) {
            // las part was a token, add it

            if (finishCurrentToken(currentIndex++, state, decomposedInput, currentFragment) != null) {
                // If it's not null, it means the token was really accepted as such, and an expression object
                // was created for it. So we should increment the index.
                currentIndex++;
            }

        }

        decomposedInput.append(currentFragment);

        state.insertNode(0, decomposedInput.toString());

        return state;

    }



    private static Expression addExpressionAtIndex(final Expression expression, final int index,
            final ExpressionParsingState state, final StringBuilder decomposedInput,
            final StringBuilder currentFragment) {

        if (expression == null) {
            return null;
        }

        decomposedInput.append(Expression.PARSING_PLACEHOLDER_CHAR);
        decomposedInput.append(String.valueOf(index));
        decomposedInput.append(Expression.PARSING_PLACEHOLDER_CHAR);
        state.addNode(expression);
        currentFragment.setLength(0);

        return expression;

    }



    private static void finishCurrentFragment(
            final StringBuilder decomposedInput, final StringBuilder currentFragment) {
        decomposedInput.append(currentFragment);
        currentFragment.setLength(0);
    }



    private static Expression finishCurrentToken(
            final int currentIndex, final ExpressionParsingState state, final StringBuilder decomposedInput,
            final StringBuilder currentFragment) {

        final String token = currentFragment.toString();

        final Expression expr = parseAsToken(token);
        if (addExpressionAtIndex(expr, currentIndex, state, decomposedInput, currentFragment) == null) {
            // Token was not considered as such, so we just push the fragment into the input string
            decomposedInput.append(currentFragment);
            currentFragment.setLength(0);
            return null;
        }

        return expr;

    }




    private static Expression parseAsToken(final String token) {

        if (ArrayUtils.contains(PROTECTED_TOKENS, token.toLowerCase())) {
            // If token is protected, we should just do nothing. Returning null should force the caller
            // to push the fragment back into the input string and continue parsing
            return null;
        }

        final NumberTokenExpression numberTokenExpr = NumberTokenExpression.parseNumberTokenExpression(token);
        if (numberTokenExpr != null) {
            return numberTokenExpr;
        }

        final BooleanTokenExpression booleanTokenExpr = BooleanTokenExpression.parseBooleanTokenExpression(token);
        if (booleanTokenExpr != null) {
            return booleanTokenExpr;
        }

        final NullTokenExpression nullTokenExpr = NullTokenExpression.parseNullTokenExpression(token);
        if (nullTokenExpr != null) {
            return nullTokenExpr;
        }

        final NoOpTokenExpression noOpTokenExpr = NoOpTokenExpression.parseNoOpTokenExpression(token);
        if (noOpTokenExpr != null) {
            return noOpTokenExpr;
        }

        final GenericTokenExpression genericTokenExpr = GenericTokenExpression.parseGenericTokenExpression(token);
        if (genericTokenExpr != null) {
            return genericTokenExpr;
        }

        return null;

    }














    public static ExpressionParsingState unnest(final ExpressionParsingState state) {
        Validate.notNull(state, "Parsing state cannot be null");
        return decomposeNestingParenthesis(state, 0);
    }



    private static ExpressionParsingState decomposeNestingParenthesis(
            final ExpressionParsingState state, final int nodeIndex) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        final String input = state.get(nodeIndex).getInput();

        final StringBuilder decomposedString = new StringBuilder(24);
        final StringBuilder currentFragment = new StringBuilder(24);
        int currentIndex = state.size();
        final List<Integer> nestedInputs = new ArrayList<Integer>(6);

        int parLevel = 0;

        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {

            final char c = input.charAt(i);

            if (c == Expression.NESTING_START_CHAR) {

                if (parLevel == 0) {
                    // starting nested
                    decomposedString.append(currentFragment);
                    currentFragment.setLength(0);
                } else {
                    currentFragment.append(Expression.NESTING_START_CHAR);
                }

                parLevel++;


            } else if (c == Expression.NESTING_END_CHAR) {

                parLevel--;

                if (parLevel < 0) {
                    return null;
                }

                if (parLevel == 0) {
                    // ending nested
                    final int nestedIndex = currentIndex++;
                    nestedInputs.add(Integer.valueOf(nestedIndex));
                    decomposedString.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    decomposedString.append(String.valueOf(nestedIndex));
                    decomposedString.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    state.addNode(currentFragment.toString());
                    currentFragment.setLength(0);
                } else {
                    currentFragment.append(Expression.NESTING_END_CHAR);
                }

            } else {

                currentFragment.append(c);

            }

        }

        if (parLevel > 0) {
            return null;
        }

        decomposedString.append(currentFragment);

        state.setNode(nodeIndex, decomposedString.toString());


        for (final Integer nestedInput : nestedInputs) {
            if (decomposeNestingParenthesis(state, nestedInput.intValue()) == null) {
                return null;
            }
        }

        return state;

    }











    public static ExpressionParsingState compose(final ExpressionParsingState state) {
        return compose(state, 0);
    }




    static ExpressionParsingState compose(final ExpressionParsingState state, final int nodeIndex) {

        if (state == null || nodeIndex >= state.size()) {
            return null;
        }

        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        /*
         * STEP 1: Check whether node is just an expression placeholder (like '%123%')
         *         and, if it is, simply substitute it by its referenced expression.
         */

        final int parsedIndex = parseAsSimpleIndexPlaceholder(input);
        if (parsedIndex != -1) {
            if (compose(state, parsedIndex) == null) {
                return null;
            }
            if (!state.hasExpressionAt(parsedIndex)) {
                return null;
            }
            state.setNode(nodeIndex, state.get(parsedIndex).getExpression());
            return state;
        }

        /*
         * STEP 2: Try composing this node as a conditional expression
         */

        if (ConditionalExpression.composeConditionalExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 3: Try composing this node as a default expression
         */

        if (DefaultExpression.composeDefaultExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 4: Try composing this node as an OR expression
         */

        if (OrExpression.composeOrExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 5: Try composing this node as an AND expression
         */

        if (AndExpression.composeAndExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 6: Try composing this node as an EQUALS or NOT EQUALS expression
         */

        if (EqualsNotEqualsExpression.composeEqualsNotEqualsExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 7: Try composing this node as a GREATER or LESSER expression
         */

        if (GreaterLesserExpression.composeGreaterLesserExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 8: Try composing this node as a ADDITION or SUBTRACTION expression
         */

        if (AdditionSubtractionExpression.composeAdditionSubtractionExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 9: Try composing this node as a MULTIPLICATION, DIVISION or REMAINDER expression
         */

        if (MultiplicationDivisionRemainderExpression.composeMultiplicationDivisionRemainderExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 10: Try composing this node as a MINUS expression
         */

        if (MinusExpression.composeMinusExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        /*
         * STEP 11: Try composing this node as a NEGATION expression
         */

        if (NegationExpression.composeNegationExpression(state, nodeIndex) == null) {
            return null;
        }
        if (state.hasExpressionAt(nodeIndex)) {
            return state;
        }

        return null;

    }





    public static int parseAsSimpleIndexPlaceholder(final String placeholder) {
        // Input should never be null
        final String str = placeholder.trim();
        final int strLen = str.length();
        if (strLen <= 2) {
            return -1;
        }
        if (str.charAt(0) != Expression.PARSING_PLACEHOLDER_CHAR || str.charAt(strLen-1) != Expression.PARSING_PLACEHOLDER_CHAR) {
            return -1;
        }
        for (int i = 1; i < strLen-1; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return -1;
            }
        }
        return Integer.parseInt(str.substring(1, strLen - 1));
    }




    static Expression parseAndCompose(final ExpressionParsingState state, final String parseTarget) {
        /*
         * Takes a String (probably a substring of a parsing node), checks whether it is a reference to
         * another node or not and, if not, performs a composition operation on it. Then just composes
         * the target.
         */
        int index = parseAsSimpleIndexPlaceholder(parseTarget);
        if (index == -1) {
            // parseTarget is not a mere index placeholder, so add it and compose it
            index = state.size();
            state.addNode(parseTarget);
        }
        if (compose(state, index) == null || !state.hasExpressionAt(index)) {
            return null;
        }
        return state.get(index).getExpression();
    }




    private ExpressionParsingUtil() {
        super();
    }




}

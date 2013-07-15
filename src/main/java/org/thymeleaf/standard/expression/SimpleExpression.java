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

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class SimpleExpression extends Expression {
    
    private static final long serialVersionUID = 9145380484247069725L;
    
    
    static final char EXPRESSION_START_CHAR = '{';
    static final char EXPRESSION_END_CHAR = '}';
    
    
    
    protected SimpleExpression() {
        super();
    }
    
    
    
    static List<ExpressionParsingNode> decomposeSimpleExpressionsExceptNumberLiterals(final String input) {
        return decomposeSimpleExpressions(input, false); 
    }
    
    
    static List<ExpressionParsingNode> decomposeSimpleExpressions(final String input) {
        return decomposeSimpleExpressions(input, true); 
    }

    
    
    private static List<ExpressionParsingNode> decomposeSimpleExpressions(
            final String input, final boolean decomposeNumberLiterals) {
        
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        
        final StringBuilder inputWithPlaceholders = new StringBuilder();
        StringBuilder fragment = new StringBuilder();
        final List<ExpressionParsingNode> fragments = new ArrayList<ExpressionParsingNode>(10);
        int currentIndex = 1;
        
        int expLevel = 0;
        boolean inLiteral = false;
        boolean inNumber = false;
        boolean inToken = false;
        
        char expSelectorChar = (char)0;
        
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            
            final char c = input.charAt(i);

            /*
             * First of all, we must check if we were dealing with a number until now
             */
            if (!inToken && !inLiteral && expLevel == 0 && inNumber &&
                    !(Character.isDigit(c) || c == NumberLiteralExpression.DECIMAL_POINT)) {
                if (decomposeNumberLiterals) {
                    // end number without adding current char to it
                    inNumber = false;
                    inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    inputWithPlaceholders.append(String.valueOf(currentIndex++));
                    inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    final NumberLiteralExpression literalExpr = 
                        NumberLiteralExpression.parseNumberLiteral(fragment.toString());
                    if (literalExpr == null) {
                        return null;
                    }
                    fragments.add(new ExpressionParsingNode(literalExpr));
                    fragment = new StringBuilder();
                } else {
                    inNumber = false;
                }
            }

            /*
             * Now we check for finishing tokens
             */
            if (!inNumber && !inLiteral && expLevel == 0 && inToken && !Character.isLetter(c)) {
                // end token without adding current char to it
                inToken = false;

                final BooleanLiteralExpression booleanLiteralExpr =
                        BooleanLiteralExpression.parseBooleanLiteral(fragment.toString());
                if (booleanLiteralExpr != null) {

                    inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    inputWithPlaceholders.append(String.valueOf(currentIndex++));
                    inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    fragments.add(new ExpressionParsingNode(booleanLiteralExpr));
                    fragment = new StringBuilder();

                } else {

                    final NullLiteralExpression nullLiteralExpr =
                            NullLiteralExpression.parseNullLiteral(fragment.toString());
                    if (nullLiteralExpr != null) {

                        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                        inputWithPlaceholders.append(String.valueOf(currentIndex++));
                        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                        fragments.add(new ExpressionParsingNode(nullLiteralExpr));
                        fragment = new StringBuilder();

                    }

                }

                // If token is not of one of the recognized types, just let the process
                // continue (once we've set the inToken flag to false)

            }

            /*
             * Once numbers and tokens are checked, process the current character
             */
            if (expLevel == 0 && c == TextLiteralExpression.DELIMITER) {
                
                if (i == 0 || !isEscaping(input, i)) {
                    
                    inLiteral = !inLiteral;
                    if (inLiteral) {
                        // starting literal
                        inputWithPlaceholders.append(fragment);
                        fragment = new StringBuilder();
                        fragment.append(c);
                    } else {
                        // ending literal
                        inputWithPlaceholders.append(PARSING_PLACEHOLDER_CHAR);
                        inputWithPlaceholders.append(String.valueOf(currentIndex++));
                        inputWithPlaceholders.append(PARSING_PLACEHOLDER_CHAR);
                        fragment.append(c);
                        final TextLiteralExpression literalExpr = 
                            TextLiteralExpression.parseTextLiteral(fragment.toString());
                        if (literalExpr == null) {
                            return null;
                        }
                        fragments.add(new ExpressionParsingNode(literalExpr));
                        fragment = new StringBuilder();
                    }
                    
                } else {
                    
                    fragment.append(c);
                    
                }
                
            } else if (!inLiteral && c == EXPRESSION_START_CHAR) {

                if (i > 0) {
                    
                    if (expLevel == 0) {
                        final char cPrev = input.charAt(i - 1);
                        if (cPrev == VariableExpression.SELECTOR || cPrev == SelectionVariableExpression.SELECTOR || 
                            cPrev == MessageExpression.SELECTOR || cPrev == LinkExpression.SELECTOR) {
                            // starting expression
                            fragment.deleteCharAt(fragment.length() - 1);
                            inputWithPlaceholders.append(fragment);
                            fragment = new StringBuilder();
                            fragment.append(cPrev);
                            expLevel++;
                            expSelectorChar = cPrev;
                        }
                    } else {
                        expLevel++;
                    }
                    
                }

                fragment.append(c);
                
            } else if (!inLiteral && c == EXPRESSION_END_CHAR) {

                if (expLevel > 0) {
                    
                    expLevel--;
                    
                    if (expLevel == 0) {
                        // ending expression
                        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                        inputWithPlaceholders.append(String.valueOf(currentIndex++));
                        inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                        fragment.append(c);
                        Expression expr = null;
                        switch (expSelectorChar) {
                            case VariableExpression.SELECTOR: 
                                expr = VariableExpression.parseVariable(fragment.toString()); break;
                            case SelectionVariableExpression.SELECTOR: 
                                expr = SelectionVariableExpression.parseSelectionVariable(fragment.toString()); break;
                            case MessageExpression.SELECTOR: 
                                expr = MessageExpression.parseMessage(fragment.toString()); break;
                            case LinkExpression.SELECTOR: 
                                expr = LinkExpression.parseLink(fragment.toString()); break;
                            default:
                                return null;
                                        
                        }
                        if (expr == null) {
                            return null;
                        }
                        fragments.add(new ExpressionParsingNode(expr));
                        expSelectorChar = (char)0;
                        fragment = new StringBuilder();
                    } else {
                        fragment.append(c);
                    }
                    
                } else {
                    fragment.append(c);
                }
                
            } else if (decomposeNumberLiterals && !inLiteral && !inToken && expLevel == 0 && Character.isDigit(c)) {
                
                if (!inNumber) {
                    // starting number
                    inNumber = true;
                    inputWithPlaceholders.append(fragment);
                    fragment = new StringBuilder();
                }
                
                fragment.append(c);

            } else if (!inLiteral && !inNumber && expLevel == 0 && Character.isLetter(c)) {
                // We will separate all tokens first, and later decide what they mean (true, false, etc.).

                if (!inToken) {
                    // starting token
                    inToken = true;
                    inputWithPlaceholders.append(fragment);
                    fragment = new StringBuilder();
                }

                fragment.append(c);

            } else {

                fragment.append(c);
               
            }
            
            
        }
        
        if (inLiteral || expLevel > 0) {
            return null;
        }
        
        if (decomposeNumberLiterals && inNumber) {
            // last part was a number, add it
            inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
            inputWithPlaceholders.append(String.valueOf(currentIndex++));
            inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
            final NumberLiteralExpression literalExpr = 
                NumberLiteralExpression.parseNumberLiteral(fragment.toString());
            if (literalExpr == null) {
                return null;
            }
            fragments.add(new ExpressionParsingNode(literalExpr));
            fragment = new StringBuilder();
        }

        if (inToken) {
            // las part was a token, add it

            final BooleanLiteralExpression booleanLiteralExpr =
                    BooleanLiteralExpression.parseBooleanLiteral(fragment.toString());
            if (booleanLiteralExpr != null) {

                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                inputWithPlaceholders.append(String.valueOf(currentIndex++));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                fragments.add(new ExpressionParsingNode(booleanLiteralExpr));
                fragment = new StringBuilder();

            } else {

                final NullLiteralExpression nullLiteralExpr =
                        NullLiteralExpression.parseNullLiteral(fragment.toString());
                if (nullLiteralExpr != null) {

                    inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    inputWithPlaceholders.append(String.valueOf(currentIndex++));
                    inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                    fragments.add(new ExpressionParsingNode(nullLiteralExpr));
                    fragment = new StringBuilder();

                }

            }

            // If token is not of one of the recognized types, just let the process
            // continue (once we've set the inToken flag to false)

        }

        inputWithPlaceholders.append(fragment);
        
        final List<ExpressionParsingNode> result = new ArrayList<ExpressionParsingNode>(fragments.size() + 4);
        result.add(new ExpressionParsingNode(inputWithPlaceholders.toString()));
        result.addAll(fragments);

        return result; 
        
    }


    

    
    

    
    
    static List<ExpressionParsingNode> addNumberLiteralDecomposition(
            final List<ExpressionParsingNode> inputExprs, final int inputIndex) {

        
        if (inputExprs == null || inputExprs.size() == 0 || inputIndex >= inputExprs.size()) {
            return null;
        }

        final String input = inputExprs.get(inputIndex).getInput();

        
        final StringBuilder inputWithPlaceholders = new StringBuilder();
        StringBuilder fragment = new StringBuilder();
        final List<ExpressionParsingNode> fragments = new ArrayList<ExpressionParsingNode>(10);
        int currentIndex = inputExprs.size();
        
        boolean inNumber = false;
        boolean inExpression = false;
        
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            
            final char c = input.charAt(i);

            /*
             * First of all, we must check if we were dealing with a number until now
             */
            if (inNumber && !(Character.isDigit(c) || c == NumberLiteralExpression.DECIMAL_POINT)) {
                // end number without adding current char to it
                inNumber = false;
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                inputWithPlaceholders.append(String.valueOf(currentIndex++));
                inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
                final NumberLiteralExpression literalExpr = 
                    NumberLiteralExpression.parseNumberLiteral(fragment.toString());
                if (literalExpr == null) {
                    return null;
                }
                fragments.add(new ExpressionParsingNode(literalExpr));
                fragment = new StringBuilder();
            }
                
                
            if (!inExpression && Character.isDigit(c)) {
                
                if (!inNumber) {
                    // starting number
                    inNumber = true;
                    inputWithPlaceholders.append(fragment);
                    fragment = new StringBuilder();
                }
                
                fragment.append(c);
                
            } else if (c == Expression.PARSING_PLACEHOLDER_CHAR){
                
                inExpression = !inExpression;
                fragment.append(c);
                
            } else {
                
                fragment.append(c);
               
            }
            
            
        }
        
        if (inNumber) {
            // last part was a number, add it
            inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
            inputWithPlaceholders.append(String.valueOf(currentIndex++));
            inputWithPlaceholders.append(Expression.PARSING_PLACEHOLDER_CHAR);
            final NumberLiteralExpression literalExpr = 
                NumberLiteralExpression.parseNumberLiteral(fragment.toString());
            if (literalExpr == null) {
                return null;
            }
            fragments.add(new ExpressionParsingNode(literalExpr));
            fragment = new StringBuilder();
        }
        
        inputWithPlaceholders.append(fragment);
        
        final List<ExpressionParsingNode> result = inputExprs;
        result.set(inputIndex, new ExpressionParsingNode(inputWithPlaceholders.toString()));
        result.addAll(fragments);

        return result; 
        
    }


    
    
    
    
    private static boolean isEscaping(final String input, final int pos) {
        // Only an odd number of \'s will indicate escaping
        if (pos == 0 || input.charAt(pos - 1) != '\\') {
            return false;
        }
        int i = pos - 1;
        boolean odd = false;
        while (i >= 0) {
            if (input.charAt(i) == '\\') {
                odd = !odd;
            } else {
                return odd;
            }
            i--;
        }
        return odd;
    }
    

    
    
    
    
    static Object executeSimple(final Configuration configuration, final IProcessingContext processingContext, final SimpleExpression expression, 
            final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {
        
        if (expression instanceof VariableExpression) {
            return VariableExpression.executeVariable(configuration, processingContext, (VariableExpression)expression, expressionEvaluator, expContext);
        }
        if (expression instanceof MessageExpression) {
            return MessageExpression.executeMessage(configuration, processingContext, (MessageExpression)expression, expressionEvaluator, expContext);
        }
        if (expression instanceof TextLiteralExpression) {
            return TextLiteralExpression.executeTextLiteral(processingContext, (TextLiteralExpression)expression, expContext);
        }
        if (expression instanceof NumberLiteralExpression) {
            return NumberLiteralExpression.executeNumberLiteral(processingContext, (NumberLiteralExpression)expression, expContext);
        }
        if (expression instanceof BooleanLiteralExpression) {
            return BooleanLiteralExpression.executeBooleanLiteral(processingContext, (BooleanLiteralExpression)expression, expContext);
        }
        if (expression instanceof NullLiteralExpression) {
            return NullLiteralExpression.executeNullLiteral(processingContext, (NullLiteralExpression)expression, expContext);
        }
        if (expression instanceof LinkExpression) {
            return LinkExpression.executeLink(configuration, processingContext, (LinkExpression)expression, expressionEvaluator, expContext);
        }
        if (expression instanceof SelectionVariableExpression) {
            return SelectionVariableExpression.executeSelectionVariable(configuration, processingContext, (SelectionVariableExpression)expression, expressionEvaluator, expContext);
        }
        if (expression instanceof SelectionVariableExpression) {
            return SelectionVariableExpression.executeSelectionVariable(configuration, processingContext, (SelectionVariableExpression)expression, expressionEvaluator, expContext);
        }

        throw new TemplateProcessingException("Unrecognized simple expression: " + expression.getClass().getName());
        
    }
    
}

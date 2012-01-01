/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.ExpressionEvaluationException;






/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class Expression implements Serializable {

    private static final long serialVersionUID = 1608378943284014151L;

    
    static final char PARSING_PLACEHOLDER_CHAR = '\u00A7';
    
    static final char NESTING_START_CHAR = '(';
    static final char NESTING_END_CHAR = ')';
    
    
    
    protected Expression() {
        super();
    }
    
    
    public abstract String getStringRepresentation();
    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }

    
    
    static Expression parse(final String input) {
        
        if (input == null || input.trim().equals("")) {
            return null;
        }

        List<ExpressionParsingNode> decomposition = SimpleExpression.decomposeSimpleExpressions(input);
        if (decomposition == null) {
            return null;
        }
        
        if (!decomposition.get(0).isExpression()) {
            
            decomposition = unnest(decomposition, 0);
            
            if (decomposition == null) {
                return null;
            }
            
            if (!decomposition.get(0).isExpression()) {
            
                decomposition = ComplexExpression.composeComplexExpressions(decomposition, 0);
    
                if (decomposition == null) {
                    return null;
                }
                
            }
            
        }
        
        final ExpressionParsingNode rootNode = decomposition.get(0);
        if (rootNode == null || !rootNode.isExpression()) {
            return null;
        }
        
        return rootNode.getExpression();
        
    }
    

    
    

    
    static List<ExpressionParsingNode> unnest(
            final List<ExpressionParsingNode> inputExprs, int inputIndex) {
        
        if (inputExprs == null || inputExprs.size() == 0 || inputIndex >= inputExprs.size()) {
            return null;
        }

        final String input = inputExprs.get(inputIndex).getInput();
        
        final StringBuilder inputWithPlaceholders = new StringBuilder();
        StringBuilder fragment = new StringBuilder();
        final List<ExpressionParsingNode> fragments = new ArrayList<ExpressionParsingNode>();
        int currentIndex = inputExprs.size();
        final List<Integer> nestedInputs = new ArrayList<Integer>();
        
        int parLevel = 0;
        
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            
            final char c = input.charAt(i);

            if (c == NESTING_START_CHAR) {

                if (parLevel == 0) {
                    // starting nested
                    inputWithPlaceholders.append(fragment);
                    fragment = new StringBuilder();
                } else {
                    fragment.append(c);
                }
                
                parLevel++;

                
            } else if (c == NESTING_END_CHAR) {
                    
                parLevel--;
                
                if (parLevel < 0) {
                    return null;
                }
                    
                if (parLevel == 0) {
                    // ending nested
                    final int nestedIndex = currentIndex++;
                    nestedInputs.add(Integer.valueOf(nestedIndex));
                    inputWithPlaceholders.append(PARSING_PLACEHOLDER_CHAR);
                    inputWithPlaceholders.append(String.valueOf(nestedIndex));
                    inputWithPlaceholders.append(PARSING_PLACEHOLDER_CHAR);
                    fragments.add(new ExpressionParsingNode(fragment.toString()));
                    fragment = new StringBuilder();
                } else {
                    fragment.append(c);
                }
                
            } else {
                
                fragment.append(c);
               
            }
            
            
        }
        
        if (parLevel > 0) {
            return null;
        }
        
        
        inputWithPlaceholders.append(fragment);

        List<ExpressionParsingNode> result = inputExprs;
        result.set(inputIndex, new ExpressionParsingNode(inputWithPlaceholders.toString()));
        result.addAll(fragments);
        
        for (final Integer nestedInput : nestedInputs) {
            result = unnest(result, nestedInput.intValue());
        }
        
        
        return result; 
        
    }
    
    
    
    
    static int placeHolderToIndex(final String placeholder) {
        // INPUT SHOULD NEVER BE NULL!!!
        final String str = placeholder.trim();
        int len = str.length();
        if (len <= 2) {
            return -1;
        }
        if (str.charAt(0) != PARSING_PLACEHOLDER_CHAR || str.charAt(len-1) != PARSING_PLACEHOLDER_CHAR) {
            return -1;
        }
        for (int i = 1; i < len-1; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return -1;
            }
        }
        return Integer.parseInt(str.substring(1, len - 1));
    }
    
    
    
    
    
    
    
    static Object execute(final Arguments arguments, final Expression expression, final IStandardExpressionEvaluator expressionEvaluator) {
        
        if (expression instanceof SimpleExpression) {
            return SimpleExpression.executeSimple(arguments, (SimpleExpression)expression, expressionEvaluator);
        } else  if (expression instanceof ComplexExpression) {
            return ComplexExpression.executeComplex(arguments, (ComplexExpression)expression, expressionEvaluator);
        }
        
        throw new ExpressionEvaluationException("Unrecognized expression: " + expression.getClass().getName());
        
    }
    
    
}

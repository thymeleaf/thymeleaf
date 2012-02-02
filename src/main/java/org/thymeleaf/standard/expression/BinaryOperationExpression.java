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

import java.util.List;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class BinaryOperationExpression extends ComplexExpression {

    
    private static final long serialVersionUID = 7524261639178859585L;


    private final Expression left;
    private final Expression right;

    
    protected BinaryOperationExpression(final Expression left, final Expression right) {
        super();
        Validate.notNull(left, "Left-side expression cannot be null");
        Validate.notNull(right, "Right-side expression cannot be null");
        this.left = left;
        this.right = right;
    }
    
    public Expression getLeft() {
        return this.left;
    }

    public Expression getRight() {
        return this.right;
    }

    
    
    protected String getStringRepresentation(final String operator) {
        final StringBuilder sb = new StringBuilder();
        if (this.left instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.left);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.left);
        }
        sb.append(' ');
        sb.append(operator);
        sb.append(' ');
        if (this.right instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.right);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.right);
        }
        return sb.toString();
    }
    
    
    
    
    protected static List<ExpressionParsingNode> composeBinaryOperationExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex, final String[] operators,
            final boolean[] leniencies, 
            final Class<? extends BinaryOperationExpression>[] operationClasses) {

        // Returning "decomposition"/"result"  means "try next kind of operator in chain"
        // Returning "null" means parsing error
        //
        // "Lenient" means that if the last operator occurrence found does not result
        // in a valid parse result, the previous-to-last (and all previous to that)
        // should also be tried in sequence (this is mainly to avoid "-" symbols from
        // minus operators getting in the way of subtraction operations).
        
        final String input = decomposition.get(inputIndex).getInput();
        if (input.equals("")) {
            return null;
        }

        String scannedInput = input.toLowerCase(); 
        boolean checkPreviousOperatorOccurence = false;
        
        List<ExpressionParsingNode> result = decomposition;

        do {
            
            int operatorIndex = -1;
            int operatorPosFrom = -1;
            int operatorPosTo = Integer.MAX_VALUE;
            int operatorLen = 0;
            
            for (int i = 0; i < operators.length; i++) {
                // Trying to fail quickly...
                final int currentOperatorPosFrom = scannedInput.lastIndexOf(operators[i]);
                if (currentOperatorPosFrom != -1) {
                    final int currentOperatorLen = operators[i].length();
                    final int currentOperatorPosTo = currentOperatorPosFrom + currentOperatorLen;
                    if (operatorPosFrom == -1 || operatorPosTo < currentOperatorPosFrom || 
                            (currentOperatorLen > operatorLen && currentOperatorPosTo >= operatorPosTo)) {
                        // the last condition is for allowing "<=" not to be confused with "<" or "neq" with "eq"
                        operatorPosFrom = currentOperatorPosFrom;
                        operatorLen = operators[i].length();
                        operatorPosTo = currentOperatorPosFrom + operatorLen;
                        operatorIndex = i;
                    }
                }
            }
            if (operatorPosFrom == -1) {
                return decomposition;
            }
            
            result =
                doComposeBinaryOperationExpression(
                        decomposition, inputIndex, operators[operatorIndex], operationClasses[operatorIndex], input, operatorPosFrom);
            
            if (leniencies[operatorIndex]) {
                if (result == null) {
                    checkPreviousOperatorOccurence = true;
                    scannedInput = scannedInput.substring(0, operatorPosFrom);
                } else {
                    checkPreviousOperatorOccurence = false;
                }
            } else {
                checkPreviousOperatorOccurence = false;
            }
            
            
        } while (checkPreviousOperatorOccurence);
        
        
        
        return result;
        
    }
        
        
    
    
    
    private static List<ExpressionParsingNode> doComposeBinaryOperationExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex, final String operator,
            final Class<? extends BinaryOperationExpression> operationClass,
            final String input, final int operatorPos) {
             
        List<ExpressionParsingNode> result = decomposition;
        
        final String leftStr = input.substring(0, operatorPos).trim();
        final String rightStr = input.substring(operatorPos + operator.length()).trim();
         
        if (leftStr.equals("") || rightStr.equals("")) {
            return null;
        }
        
        int leftIndex = Expression.placeHolderToIndex(leftStr);
        if (leftIndex == -1) {
            leftIndex = result.size();
            result.add(new ExpressionParsingNode(leftStr));
        }
        result = ComplexExpression.composeComplexExpressions(result, leftIndex);
        if (result == null) {
            return null;
        }

        
        int rightIndex = Expression.placeHolderToIndex(rightStr);
        if (rightIndex == -1) {
            rightIndex = result.size();
            result.add(new ExpressionParsingNode(rightStr));
        }
        result = ComplexExpression.composeComplexExpressions(result, rightIndex);
        if (result == null) {
            return null;
        }
        
        
        final ExpressionParsingNode leftEPN = result.get(leftIndex);
        final Expression leftExpr = leftEPN.getExpression();
        if (leftExpr == null) {
            return null;
        }
        
        final ExpressionParsingNode rightEPN = result.get(rightIndex);
        final Expression rightExpr = rightEPN.getExpression();
        if (rightExpr == null) {
            return null;
        }
        
        
        try {
            final BinaryOperationExpression operationExpression =
                operationClass.getDeclaredConstructor(Expression.class, Expression.class).
                            newInstance(leftExpr, rightExpr);
            result.set(inputIndex, new ExpressionParsingNode(operationExpression));
            
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during creation of Binary Operation expression for operator: \"" + operator + "\"", e);
        }
        
        
        return result;
        
    }
    
    
    
    
}

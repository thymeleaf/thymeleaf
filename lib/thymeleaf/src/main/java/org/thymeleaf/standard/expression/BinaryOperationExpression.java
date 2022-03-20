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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
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


    private final IStandardExpression left;
    private final IStandardExpression right;

    
    protected BinaryOperationExpression(final IStandardExpression left, final IStandardExpression right) {
        super();
        Validate.notNull(left, "Left-side expression cannot be null");
        Validate.notNull(right, "Right-side expression cannot be null");
        this.left = left;
        this.right = right;
    }
    
    public IStandardExpression getLeft() {
        return this.left;
    }

    public IStandardExpression getRight() {
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
    
    
    
    
    protected static ExpressionParsingState composeBinaryOperationExpression(
            final ExpressionParsingState state, final int nodeIndex, final String[] operators,
            final boolean[] leniencies, final Class<? extends BinaryOperationExpression>[] operationClasses,
            final Method leftAllowedMethod, final Method rightAllowedMethod) {

        // Returning "state" means "try next in chain" or "success"
        // Returning "null" means parsing error
        //
        // "Lenient" means that if the last operator occurrence found does not result
        // in a valid parse result, the previous-to-last (and all previous to that)
        // should also be tried in sequence (this is mainly to avoid "-" symbols from
        // minus operators getting in the way of subtraction operations).

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        String scannedInput = input.toLowerCase(); 

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
                return state;
            }
            
            if (doComposeBinaryOperationExpression(
                        state, nodeIndex, operators[operatorIndex], operationClasses[operatorIndex],
                        leftAllowedMethod, rightAllowedMethod, input, operatorPosFrom) == null) {

                if (leniencies[operatorIndex]) {
                    scannedInput = scannedInput.substring(0, operatorPosFrom);
                } else {
                    return null;
                }

            } else {
                return state;
            }

            
        } while (true);
        

    }
        
        
    
    
    
    private static ExpressionParsingState doComposeBinaryOperationExpression(
            final ExpressionParsingState state, final int nodeIndex, final String operator,
            final Class<? extends BinaryOperationExpression> operationClass,
            final Method leftAllowedMethod, final Method rightAllowedMethod,
            final String input, final int operatorPos) {
             
        final String leftStr = input.substring(0, operatorPos).trim();
        final String rightStr = input.substring(operatorPos + operator.length()).trim();
         
        if (leftStr.length() == 0 || rightStr.length() == 0) {
            return null;
        }

        final Expression leftExpr = ExpressionParsingUtil.parseAndCompose(state, leftStr);
        try {
            if (leftExpr == null || !((Boolean)leftAllowedMethod.invoke(null,leftExpr)).booleanValue()) {
                return null;
            }
        } catch (final IllegalAccessException e) {
            // Should never happen, would be a programming error
            throw new TemplateProcessingException("Error invoking operand validation in binary operation", e);
        } catch (final InvocationTargetException e) {
            // Should never happen, would be a programming error
            throw new TemplateProcessingException("Error invoking operand validation in binary operation", e);
        }

        final Expression rightExpr = ExpressionParsingUtil.parseAndCompose(state, rightStr);
        try {
            if (rightExpr == null || !((Boolean)rightAllowedMethod.invoke(null,rightExpr)).booleanValue()) {
                return null;
            }
        } catch (final IllegalAccessException e) {
            // Should never happen, would be a programming error
            throw new TemplateProcessingException("Error invoking operand validation in binary operation", e);
        } catch (final InvocationTargetException e) {
            // Should never happen, would be a programming error
            throw new TemplateProcessingException("Error invoking operand validation in binary operation", e);
        }

        try {

            final BinaryOperationExpression operationExpression =
                operationClass.getDeclaredConstructor(IStandardExpression.class, IStandardExpression.class).
                            newInstance(leftExpr, rightExpr);
            state.setNode(nodeIndex, operationExpression);
            
        } catch (final TemplateProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new TemplateProcessingException(
                    "Error during creation of Binary Operation expression for operator: \"" + operator + "\"", e);
        }

        return state;
        
    }
    
    
    
    
}

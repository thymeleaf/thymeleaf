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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class ConditionalExpression extends ComplexExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionalExpression.class);


    private static final long serialVersionUID = -6966177717462316363L;
    
    
    private static final char CONDITION_SUFFIX_CHAR = '?';
    private static final char CONDITION_THENELSE_SEPARATOR_CHAR = ':';
    
    
    private final Expression conditionExpression;
    private final Expression thenExpression;
    private final Expression elseExpression;

    
    public ConditionalExpression(final Expression conditionExpression, 
            final Expression thenExpression, final Expression elseExpression) {
        super();
        Validate.notNull(conditionExpression, "Condition expression cannot be null");
        Validate.notNull(thenExpression, "Then expression cannot be null");
        Validate.notNull(elseExpression, "Else expression cannot be null");
        this.conditionExpression = conditionExpression;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }
    
    public Expression getConditionExpression() {
        return this.conditionExpression;
    }

    public Expression getThenExpression() {
        return this.thenExpression;
    }

    public Expression getElseExpression() {
        return this.elseExpression;
    }
    
    @Override
    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        if (this.conditionExpression instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.conditionExpression);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.conditionExpression);
        }
        sb.append(CONDITION_SUFFIX_CHAR);
        sb.append(' ');
        if (this.thenExpression instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.thenExpression);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.thenExpression);
        }
        sb.append(' ');
        sb.append(CONDITION_THENELSE_SEPARATOR_CHAR);
        sb.append(' ');
        if (this.elseExpression instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.elseExpression);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.elseExpression);
        }
        return sb.toString();
    }
    
    
    
    
    static List<ExpressionParsingNode> composeConditionalExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {

        // Returning "result" means "try next in chain"
        // Returning "null" means parsing error
        
        final ExpressionParsingNode inputParsingNode = decomposition.get(inputIndex);
        
        List<ExpressionParsingNode> result = decomposition;
        
        final String input = inputParsingNode.getInput();
            
        if (input == null || input.trim().equals("")) {
            return null;
        }

        // Trying to fail quickly...
        int condSuffixPos = input.indexOf(CONDITION_SUFFIX_CHAR);
        if (condSuffixPos == -1) {
            return result;
        }
        
        final String condStr = input.substring(0, condSuffixPos);
        final String remainder = input.substring(condSuffixPos + 1);

        if (remainder.indexOf(CONDITION_SUFFIX_CHAR) != -1) {
            // There are two "?" symbols
            return null;
        }
        
        int thenElseSepPos = remainder.indexOf(CONDITION_THENELSE_SEPARATOR_CHAR);
        if (remainder.lastIndexOf(CONDITION_THENELSE_SEPARATOR_CHAR) != thenElseSepPos) {
            // There are two ":" symbols
            return null;
        }

        String thenStr = null;
        String elseStr = null;
        if (thenElseSepPos != -1) {
            if (thenElseSepPos == 0) {
                // Maybe it is a default operation
                return result;
            }
            thenStr = remainder.substring(0, thenElseSepPos);
            elseStr = remainder.substring(thenElseSepPos + 1);
        } else {
            thenStr = remainder;
        }

        
        int condIndex = Expression.placeHolderToIndex(condStr);
        if (condIndex == -1) {
            List<ExpressionParsingNode> newResult = 
                new ArrayList<ExpressionParsingNode>(result);
            condIndex = newResult.size();
            newResult.add(new ExpressionParsingNode(condStr));
            newResult = ComplexExpression.composeComplexExpressions(newResult, condIndex);
            if (newResult == null) {
                return null;
            }
            result = newResult;
        } else {
            result = ComplexExpression.composeComplexExpressions(result, condIndex);
            if (result == null) {
                return null;
            }
        }
        
        
        int thenIndex = Expression.placeHolderToIndex(thenStr);
        if (thenIndex == -1) {
            List<ExpressionParsingNode> newResult = 
                new ArrayList<ExpressionParsingNode>(result);
            thenIndex = newResult.size();
            newResult.add(new ExpressionParsingNode(thenStr));
            newResult = ComplexExpression.composeComplexExpressions(newResult, thenIndex);
            if (newResult == null) {
                return null;
            }
            result = newResult;
        } else {
            result = ComplexExpression.composeComplexExpressions(result, thenIndex);
            if (result == null) {
                return null;
            }
        }

        
        final ExpressionParsingNode condEPN = result.get(condIndex);
        final Expression condExpr = condEPN.getExpression();
        if (condExpr == null) {
            return null;
        }
        
        
        final ExpressionParsingNode thenEPN = result.get(thenIndex);
        final Expression thenExpr = thenEPN.getExpression();
        if (thenExpr == null) {
            return null;
        }
        
        
        Expression elseExpr = VariableExpression.NULL_VALUE;
        if (elseStr != null) {
            
            int elseIndex = Expression.placeHolderToIndex(elseStr);
            if (elseIndex == -1) {
                List<ExpressionParsingNode> newResult = 
                    new ArrayList<ExpressionParsingNode>(result);
                elseIndex = newResult.size();
                newResult.add(new ExpressionParsingNode(elseStr));
                newResult = ComplexExpression.composeComplexExpressions(newResult, elseIndex);
                if (newResult == null) {
                    return null;
                }
                result = newResult;
            } else {
                result = ComplexExpression.composeComplexExpressions(result, elseIndex);
                if (result == null) {
                    return null;
                }
            }

            final ExpressionParsingNode elseEPN = result.get(elseIndex);
            elseExpr = elseEPN.getExpression();
            if (elseExpr == null) {
                return null;
            }
            
        }

        
        final ConditionalExpression conditionalExpressionResult = 
            new ConditionalExpression(condExpr, thenExpr, elseExpr);
        
        result.set(inputIndex, new ExpressionParsingNode(conditionalExpressionResult));
        
        return result;
        
    }
    
    


    
    static Object executeConditional(final Configuration configuration, final IProcessingContext processingContext, 
            final ConditionalExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating conditional expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final Object condObj = 
            Expression.execute(configuration, processingContext, expression.getConditionExpression(), expressionEvaluator, expContext);
        final boolean cond = ObjectUtils.evaluateAsBoolean(condObj);
        
        if (cond) {
            return Expression.execute(configuration, processingContext, expression.getThenExpression(), expressionEvaluator, expContext);
        }
        return Expression.execute(configuration, processingContext, expression.getElseExpression(), expressionEvaluator, expContext);
        
    }
    
    
}

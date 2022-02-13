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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Conditional complex expression (Thymeleaf Standard Expressions)
 * </p>
 * <p>
 *   Note a class with this name existed since 1.1, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class ConditionalExpression extends ComplexExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionalExpression.class);


    private static final long serialVersionUID = -6966177717462316363L;
    
    
    private static final char CONDITION_SUFFIX_CHAR = '?';
    private static final char CONDITION_THENELSE_SEPARATOR_CHAR = ':';
    // These are all the tokens registered by this expression
    static final String[] OPERATORS =
            new String[] {String.valueOf(CONDITION_SUFFIX_CHAR), String.valueOf(CONDITION_THENELSE_SEPARATOR_CHAR)};


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
    
    
    
    
    static ExpressionParsingState composeConditionalExpression(
            final ExpressionParsingState state, final int nodeIndex) {

        // Returning "state" means "try next in chain" or "success"
        // Returning "null" means parsing error
        
        final String input = state.get(nodeIndex).getInput();
            
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        // Trying to fail quickly...
        final int condSuffixPos = input.indexOf(CONDITION_SUFFIX_CHAR);
        if (condSuffixPos == -1) {
            return state;
        }
        
        final String condStr = input.substring(0, condSuffixPos);
        final String remainder = input.substring(condSuffixPos + 1);

        if (remainder.indexOf(CONDITION_SUFFIX_CHAR) != -1) {
            // There are two "?" symbols
            return null;
        }

        final int thenElseSepPos = remainder.indexOf(CONDITION_THENELSE_SEPARATOR_CHAR);
        if (remainder.lastIndexOf(CONDITION_THENELSE_SEPARATOR_CHAR) != thenElseSepPos) {
            // There are two ":" symbols
            return null;
        }

        String thenStr = null;
        String elseStr = null;
        if (thenElseSepPos != -1) {
            if (thenElseSepPos == 0) {
                // Maybe it is a default operation
                return state;
            }
            thenStr = remainder.substring(0, thenElseSepPos);
            elseStr = remainder.substring(thenElseSepPos + 1);
        } else {
            thenStr = remainder;
        }

        
        final Expression condExpr = ExpressionParsingUtil.parseAndCompose(state, condStr);
        if (condExpr == null) {
            return null;
        }

        final Expression thenExpr = ExpressionParsingUtil.parseAndCompose(state, thenStr);
        if (thenExpr == null) {
            return null;
        }

        Expression elseExpr = VariableExpression.NULL_VALUE;
        if (elseStr != null) {
            elseExpr = ExpressionParsingUtil.parseAndCompose(state, elseStr);
            if (elseExpr == null) {
                return null;
            }
        }

        
        final ConditionalExpression conditionalExpressionResult = 
            new ConditionalExpression(condExpr, thenExpr, elseExpr);
        state.setNode(nodeIndex,conditionalExpressionResult);
        
        return state;
        
    }
    
    


    
    static Object executeConditional(
            final IExpressionContext context,
            final ConditionalExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating conditional expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final Object condObj = expression.getConditionExpression().execute(context, expContext);
        final boolean cond = EvaluationUtils.evaluateAsBoolean(condObj);
        
        if (cond) {
            return expression.getThenExpression().execute(context, expContext);
        }
        return expression.getElseExpression().execute(context, expContext);
        
    }
    
    
}

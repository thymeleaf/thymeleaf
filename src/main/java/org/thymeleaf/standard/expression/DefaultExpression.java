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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class DefaultExpression extends ComplexExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultExpression.class);


    private static final long serialVersionUID = 1830867943963082362L;


    private static final String DEFAULT_OPERATOR = "?:";
    
    
    private final Expression queriedExpression;
    private final Expression defaultExpression;

    
    public DefaultExpression(final Expression queriedExpression, final Expression defaultExpression) {
        super();
        Validate.notNull(queriedExpression, "Queried expression cannot be null");
        Validate.notNull(defaultExpression, "Default expression cannot be null");
        this.queriedExpression = queriedExpression;
        this.defaultExpression = defaultExpression;
    }
    
    public Expression getQueriedExpression() {
        return this.queriedExpression;
    }

    public Expression getDefaultExpression() {
        return this.defaultExpression;
    }

    
    @Override
    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        if (this.queriedExpression instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.queriedExpression);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.queriedExpression);
        }
        sb.append(' ');
        sb.append(DEFAULT_OPERATOR);
        sb.append(' ');
        if (this.defaultExpression instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.defaultExpression);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.defaultExpression);
        }
        return sb.toString();
    }
    
    
    
    
    static List<ExpressionParsingNode> composeDefaultExpression(
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
        int defaultOperatorPos = input.indexOf(DEFAULT_OPERATOR);
        if (defaultOperatorPos == -1) {
            return result;
        }
        
        final String queriedStr = input.substring(0, defaultOperatorPos);
        final String defaultStr = input.substring(defaultOperatorPos + 2);
        
        if (defaultStr.indexOf(DEFAULT_OPERATOR) != -1) {
            // There are two "?:" operators
            return null;
        }
        
        

        
        int queriedIndex = Expression.placeHolderToIndex(queriedStr);
        if (queriedIndex == -1) {
            List<ExpressionParsingNode> newResult = 
                new ArrayList<ExpressionParsingNode>(result);
            queriedIndex = newResult.size();
            newResult.add(new ExpressionParsingNode(queriedStr));
            newResult = ComplexExpression.composeComplexExpressions(newResult, queriedIndex);
            if (newResult == null) {
                return null;
            }
            result = newResult;
        } else {
            result = ComplexExpression.composeComplexExpressions(result, queriedIndex);
            if (result == null) {
                return null;
            }
        }
        
        
        int defaultIndex = Expression.placeHolderToIndex(defaultStr);
        if (defaultIndex == -1) {
            List<ExpressionParsingNode> newResult = 
                new ArrayList<ExpressionParsingNode>(result);
            defaultIndex = newResult.size();
            newResult.add(new ExpressionParsingNode(defaultStr));
            newResult = ComplexExpression.composeComplexExpressions(newResult, defaultIndex);
            if (newResult == null) {
                return null;
            }
            result = newResult;
        } else {
            result = ComplexExpression.composeComplexExpressions(result, defaultIndex);
            if (result == null) {
                return null;
            }
        }
        
        
        final ExpressionParsingNode queriedEPN = result.get(queriedIndex);
        final Expression queriedExpr = queriedEPN.getExpression();
        if (queriedExpr == null) {
            return null;
        }
        
        final ExpressionParsingNode defaultEPN = result.get(defaultIndex);
        final Expression defaultExpr = defaultEPN.getExpression();
        if (defaultExpr == null) {
            return null;
        }
        
        final DefaultExpression defaultExpressionResult = 
            new DefaultExpression(queriedExpr, defaultExpr);
        
        result.set(inputIndex, new ExpressionParsingNode(defaultExpressionResult));
        
        return result;
        
    }
    

    
    
    static Object executeDefault(final Arguments arguments, final DefaultExpression expression, 
            final IStandardExpressionEvaluator expressionEvaluator) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating default expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final Object queriedValue = 
            Expression.execute(arguments, expression.getQueriedExpression(), expressionEvaluator);
        
        if (queriedValue == null) {
            return Expression.execute(arguments, expression.getDefaultExpression(), expressionEvaluator);
        }
        return queriedValue;
        
    }

    
}

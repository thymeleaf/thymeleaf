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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.ObjectUtils;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class OrExpression extends BinaryOperationExpression {


    private static final long serialVersionUID = -8085738202412415337L;

    private static final Logger logger = LoggerFactory.getLogger(OrExpression.class);

    private static final String OPERATOR = "or";
    private static final String[] OPERATORS = new String[] {OPERATOR};
    private static final boolean[] LENIENCIES = new boolean[] { false };
    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = 
        (Class<? extends BinaryOperationExpression>[]) new Class<?>[] { OrExpression.class };

    
    public OrExpression(final Expression left, final Expression right) {
        super(left, right);
    }

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(OPERATOR);
    }
    
    
    
    
    protected static List<ExpressionParsingNode> composeOrExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {
        return composeBinaryOperationExpression(
                decomposition, inputIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES);
    }
    
    
    

    
    static Object executeOr(final Configuration configuration, final IProcessingContext processingContext, 
            final OrExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating OR expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final Object leftValue = 
            Expression.execute(configuration, processingContext, expression.getLeft(), expressionEvaluator);
        
        // Short circuit
        final boolean leftBooleanValue = ObjectUtils.evaluateAsBoolean(leftValue);
        if (leftBooleanValue) {
            return Boolean.TRUE;
        }

        final Object rightValue = 
            Expression.execute(configuration, processingContext, expression.getRight(), expressionEvaluator);
        
        final boolean rightBooleanValue = ObjectUtils.evaluateAsBoolean(rightValue);
        return Boolean.valueOf(rightBooleanValue);
        
    }
    
    
}

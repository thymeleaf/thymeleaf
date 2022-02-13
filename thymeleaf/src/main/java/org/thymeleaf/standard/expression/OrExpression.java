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

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;


/**
 * <p>
 *   Logical OR complex expression (Thymeleaf Standard Expressions)
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
public final class OrExpression extends BinaryOperationExpression {


    private static final long serialVersionUID = -8085738202412415337L;

    private static final Logger logger = LoggerFactory.getLogger(OrExpression.class);

    private static final String OPERATOR = "or";
    static final String[] OPERATORS = new String[] {OPERATOR};
    private static final boolean[] LENIENCIES = new boolean[] { false };
    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = 
        (Class<? extends BinaryOperationExpression>[]) new Class<?>[] { OrExpression.class };

    private static final Method LEFT_ALLOWED_METHOD;
    private static final Method RIGHT_ALLOWED_METHOD;


    static {
        try {
            LEFT_ALLOWED_METHOD = OrExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = OrExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    
    public OrExpression(final IStandardExpression left, final IStandardExpression right) {
        super(left, right);
    }

    
    @Override
    public String getStringRepresentation() {
        return getStringRepresentation(OPERATOR);
    }




    static boolean isRightAllowed(final IStandardExpression right) {
        return right != null && !(right instanceof Token && !(right instanceof BooleanTokenExpression));
    }

    static boolean isLeftAllowed(final IStandardExpression left) {
        return left != null && !(left instanceof Token && !(left instanceof BooleanTokenExpression));
    }


    
    
    static ExpressionParsingState composeOrExpression(
            final ExpressionParsingState state, final int inputIndex) {
        return composeBinaryOperationExpression(
                state, inputIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
    
    
    

    
    static Object executeOr(
            final IExpressionContext context,
            final OrExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating OR expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final Object leftValue = expression.getLeft().execute(context, expContext);

        // Short circuit
        final boolean leftBooleanValue = EvaluationUtils.evaluateAsBoolean(leftValue);
        if (leftBooleanValue) {
            return Boolean.TRUE;
        }

        final Object rightValue = expression.getRight().execute(context, expContext);

        final boolean rightBooleanValue = EvaluationUtils.evaluateAsBoolean(rightValue);
        return Boolean.valueOf(rightBooleanValue);
        
    }
    
    
}

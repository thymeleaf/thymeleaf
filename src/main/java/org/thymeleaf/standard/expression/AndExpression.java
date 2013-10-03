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

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class AndExpression extends BinaryOperationExpression {


    private static final long serialVersionUID = -6085038102412415337L;

    private static final Logger logger = LoggerFactory.getLogger(AndExpression.class);
    
    private static final String OPERATOR = "and";
    static final String[] OPERATORS = new String[] {OPERATOR};
    private static final boolean[] LENIENCIES = new boolean[] { false };
    @SuppressWarnings("unchecked")
    private static final Class<? extends BinaryOperationExpression>[] OPERATOR_CLASSES = 
        (Class<? extends BinaryOperationExpression>[]) new Class<?>[] { AndExpression.class };

    private static Method LEFT_ALLOWED_METHOD;
    private static Method RIGHT_ALLOWED_METHOD;


    static {
        try {
            LEFT_ALLOWED_METHOD = AndExpression.class.getDeclaredMethod("isLeftAllowed", IStandardExpression.class);
            RIGHT_ALLOWED_METHOD = AndExpression.class.getDeclaredMethod("isRightAllowed", IStandardExpression.class);
        } catch (final NoSuchMethodException e) {
            throw new TemplateProcessingException("Cannot register is*Allowed methods in binary operation expression", e);
        }
    }



    public AndExpression(final IStandardExpression left, final IStandardExpression right) {
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

    
    
    
    static ExpressionParsingState composeAndExpression(
            final ExpressionParsingState state, int nodeIndex) {
        return composeBinaryOperationExpression(
                state, nodeIndex, OPERATORS, LENIENCIES, OPERATOR_CLASSES, LEFT_ALLOWED_METHOD, RIGHT_ALLOWED_METHOD);
    }
    
    
    

    
    static Object executeAnd(final Configuration configuration, final IProcessingContext processingContext, 
            final AndExpression expression, final StandardExpressionExecutionContext expContext,
            final IStandardConversionService conversionService) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating AND expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final Object leftValue = expression.getLeft().execute(configuration, processingContext, expContext);

        // Short circuit
        final boolean leftBooleanValue = conversionService.convert(leftValue, Boolean.class);
        if (!leftBooleanValue) {
            return Boolean.FALSE;
        }

        final Object rightValue = expression.getRight().execute(configuration, processingContext, expContext);
        
        final boolean rightBooleanValue = conversionService.convert(rightValue, Boolean.class);
        return Boolean.valueOf(rightBooleanValue);
        
    }



}

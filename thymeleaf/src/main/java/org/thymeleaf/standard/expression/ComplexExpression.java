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

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;



/**
 * <p>
 *   Base abstract class for complex expressions (Thymeleaf Standard Expressions)
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
public abstract class ComplexExpression extends Expression {

    
    private static final long serialVersionUID = -3807499386899890260L;



    protected ComplexExpression() {
        super();
    }





    
    static Object executeComplex(
            final IExpressionContext context,
            final ComplexExpression expression, final StandardExpressionExecutionContext expContext) {
        
        if (expression instanceof AdditionExpression) {
            return AdditionExpression.executeAddition(context, (AdditionExpression)expression, expContext);
        }
        if (expression instanceof SubtractionExpression) {
            return SubtractionExpression.executeSubtraction(context, (SubtractionExpression)expression, expContext);
        }
        if (expression instanceof MultiplicationExpression) {
            return MultiplicationExpression.executeMultiplication(context, (MultiplicationExpression)expression, expContext);
        }
        if (expression instanceof DivisionExpression) {
            return DivisionExpression.executeDivision(context, (DivisionExpression)expression, expContext);
        }
        if (expression instanceof RemainderExpression) {
            return RemainderExpression.executeRemainder(context, (RemainderExpression)expression, expContext);
        }
        if (expression instanceof ConditionalExpression) {
            return ConditionalExpression.executeConditional(context, (ConditionalExpression)expression, expContext);
        }
        if (expression instanceof DefaultExpression) {
            return DefaultExpression.executeDefault(context, (DefaultExpression)expression, expContext);
        }
        if (expression instanceof MinusExpression) {
            return MinusExpression.executeMinus(context, (MinusExpression)expression, expContext);
        }
        if (expression instanceof NegationExpression) {
            return NegationExpression.executeNegation(context, (NegationExpression)expression, expContext);
        }
        if (expression instanceof AndExpression) {
            return AndExpression.executeAnd(context, (AndExpression)expression, expContext);
        }
        if (expression instanceof OrExpression) {
            return OrExpression.executeOr(context, (OrExpression)expression, expContext);
        }
        if (expression instanceof EqualsExpression) {
            return EqualsExpression.executeEquals(context, (EqualsExpression)expression, expContext);
        }
        if (expression instanceof NotEqualsExpression) {
            return NotEqualsExpression.executeNotEquals(context, (NotEqualsExpression)expression, expContext);
        }
        if (expression instanceof GreaterThanExpression) {
            return GreaterThanExpression.executeGreaterThan(context, (GreaterThanExpression)expression, expContext);
        }
        if (expression instanceof GreaterOrEqualToExpression) {
            return GreaterOrEqualToExpression.executeGreaterOrEqualTo(context, (GreaterOrEqualToExpression)expression, expContext);
        }
        if (expression instanceof LessThanExpression) {
            return LessThanExpression.executeLessThan(context, (LessThanExpression)expression, expContext);
        }
        if (expression instanceof LessOrEqualToExpression) {
            return LessOrEqualToExpression.executeLessOrEqualTo(context, (LessOrEqualToExpression)expression, expContext);
        }

        throw new TemplateProcessingException("Unrecognized complex expression: " + expression.getClass().getName());
        
    }
    
    
}

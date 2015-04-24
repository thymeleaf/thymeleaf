/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1 (reimplemented in 3.0.0)
 *
 */
public abstract class ComplexExpression extends Expression {

    
    private static final long serialVersionUID = -3807499386899890260L;



    protected ComplexExpression() {
        super();
    }





    
    static Object executeComplex(final IProcessingContext processingContext,
            final ComplexExpression expression, final StandardExpressionExecutionContext expContext) {
        
        if (expression instanceof AdditionExpression) {
            return AdditionExpression.executeAddition(processingContext, (AdditionExpression)expression, expContext);
        }
        if (expression instanceof SubtractionExpression) {
            return SubtractionExpression.executeSubtraction(processingContext, (SubtractionExpression)expression, expContext);
        }
        if (expression instanceof MultiplicationExpression) {
            return MultiplicationExpression.executeMultiplication(processingContext, (MultiplicationExpression)expression, expContext);
        }
        if (expression instanceof DivisionExpression) {
            return DivisionExpression.executeDivision(processingContext, (DivisionExpression)expression, expContext);
        }
        if (expression instanceof RemainderExpression) {
            return RemainderExpression.executeRemainder(processingContext, (RemainderExpression)expression, expContext);
        }
        if (expression instanceof ConditionalExpression) {
            return ConditionalExpression.executeConditional(processingContext, (ConditionalExpression)expression, expContext);
        }
        if (expression instanceof DefaultExpression) {
            return DefaultExpression.executeDefault(processingContext, (DefaultExpression)expression, expContext);
        }
        if (expression instanceof MinusExpression) {
            return MinusExpression.executeMinus(processingContext, (MinusExpression)expression, expContext);
        }
        if (expression instanceof NegationExpression) {
            return NegationExpression.executeNegation(processingContext, (NegationExpression)expression, expContext);
        }
        if (expression instanceof AndExpression) {
            return AndExpression.executeAnd(processingContext, (AndExpression)expression, expContext);
        }
        if (expression instanceof OrExpression) {
            return OrExpression.executeOr(processingContext, (OrExpression)expression, expContext);
        }
        if (expression instanceof EqualsExpression) {
            return EqualsExpression.executeEquals(processingContext, (EqualsExpression)expression, expContext);
        }
        if (expression instanceof NotEqualsExpression) {
            return NotEqualsExpression.executeNotEquals(processingContext, (NotEqualsExpression)expression, expContext);
        }
        if (expression instanceof GreaterThanExpression) {
            return GreaterThanExpression.executeGreaterThan(processingContext, (GreaterThanExpression)expression, expContext);
        }
        if (expression instanceof GreaterOrEqualToExpression) {
            return GreaterOrEqualToExpression.executeGreaterOrEqualTo(processingContext, (GreaterOrEqualToExpression)expression, expContext);
        }
        if (expression instanceof LessThanExpression) {
            return LessThanExpression.executeLessThan(processingContext, (LessThanExpression)expression, expContext);
        }
        if (expression instanceof LessOrEqualToExpression) {
            return LessOrEqualToExpression.executeLessOrEqualTo(processingContext, (LessOrEqualToExpression)expression, expContext);
        }

        throw new TemplateProcessingException("Unrecognized complex expression: " + expression.getClass().getName());
        
    }
    
    
}

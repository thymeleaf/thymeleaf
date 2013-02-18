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

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class ComplexExpression extends Expression {

    
    private static final long serialVersionUID = -3807499386899890260L;



    protected ComplexExpression() {
        super();
    }


    
    static List<ExpressionParsingNode> composeComplexExpressions(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {

        if (decomposition == null) {
            return null;
        }
        
        if (decomposition.get(inputIndex).isExpression()) {
            return decomposition;
        }

        List<ExpressionParsingNode> result = decomposition;

        result = 
            tryComposeUnitaryExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result =
            ConditionalExpression.composeConditionalExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result =
            DefaultExpression.composeDefaultExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result = 
            OrExpression.composeOrExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result = 
            AndExpression.composeAndExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result =
            EqualsNotEqualsExpression.composeEqualsNotEqualsExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result =
            GreaterLesserExpression.composeGreaterLesserExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result = 
            AdditionSubtractionExpression.composeAdditionSubtractionExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result = 
            MultiplicationDivisionRemainderExpression.composeMultiplicationDivisionRemainderExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result = 
            MinusExpression.composeMinusExpression(result, inputIndex);

        if (result == null) {
            return null;
        }
        if (result.get(inputIndex).isExpression()) {
            return result;
        }

        result = 
            NegationExpression.composeNegationExpression(result, inputIndex);

        return result;
        
    }
    

    
    private static List<ExpressionParsingNode> tryComposeUnitaryExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {

        final ExpressionParsingNode inputParsingNode = decomposition.get(inputIndex);
        
        List<ExpressionParsingNode> result = decomposition;
        
        final String input = inputParsingNode.getInput();
            
        if (input == null || input.trim().equals("")) {
            return null;
        }
    
        final int valueIndex = Expression.placeHolderToIndex(input);
        
        if (valueIndex != -1) {
            
            result =
                ComplexExpression.composeComplexExpressions(result, valueIndex);
            if (result == null) {
                return null;
            }
                
            final ExpressionParsingNode epn = result.get(valueIndex);
            final Expression expr = epn.getExpression();
            if (expr == null) {
                return null;
            }
            
            result.set(inputIndex, new ExpressionParsingNode(expr));
            
        }
        
        return result; 
        
    }


    
    static Object executeComplex(final Configuration configuration, final IProcessingContext processingContext, 
            final ComplexExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {
        
        if (expression instanceof AdditionExpression) {
            return AdditionExpression.executeAddition(configuration, processingContext, (AdditionExpression)expression, expressionEvaluator, expContext);
        } else if (expression instanceof SubtractionExpression) {
            return SubtractionExpression.executeSubtraction(configuration, processingContext, (SubtractionExpression)expression, expressionEvaluator, expContext);
        } else if (expression instanceof MultiplicationExpression) {
            return MultiplicationExpression.executeMultiplication(configuration, processingContext, (MultiplicationExpression)expression, expressionEvaluator, expContext);
        } else if (expression instanceof DivisionExpression) {
            return DivisionExpression.executeDivision(configuration, processingContext, (DivisionExpression)expression, expressionEvaluator, expContext);
        } else if (expression instanceof RemainderExpression) {
            return RemainderExpression.executeRemainder(configuration, processingContext, (RemainderExpression)expression, expressionEvaluator, expContext);
        } else if (expression instanceof ConditionalExpression) {
            return ConditionalExpression.executeConditional(configuration, processingContext, (ConditionalExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof DefaultExpression) {
            return DefaultExpression.executeDefault(configuration, processingContext, (DefaultExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof MinusExpression) {
            return MinusExpression.executeMinus(configuration, processingContext, (MinusExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof NegationExpression) {
            return NegationExpression.executeNegation(configuration, processingContext, (NegationExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof AndExpression) {
            return AndExpression.executeAnd(configuration, processingContext, (AndExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof OrExpression) {
            return OrExpression.executeOr(configuration, processingContext, (OrExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof EqualsExpression) {
            return EqualsExpression.executeEquals(configuration, processingContext, (EqualsExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof NotEqualsExpression) {
            return NotEqualsExpression.executeNotEquals(configuration, processingContext, (NotEqualsExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof GreaterThanExpression) {
            return GreaterThanExpression.executeGreaterThan(configuration, processingContext, (GreaterThanExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof GreaterOrEqualToExpression) {
            return GreaterOrEqualToExpression.executeGreaterOrEqualTo(configuration, processingContext, (GreaterOrEqualToExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof LessThanExpression) {
            return LessThanExpression.executeLessThan(configuration, processingContext, (LessThanExpression)expression, expressionEvaluator, expContext);
        } else  if (expression instanceof LessOrEqualToExpression) {
            return LessOrEqualToExpression.executeLessOrEqualTo(configuration, processingContext, (LessOrEqualToExpression)expression, expressionEvaluator, expContext);
        }
        
        throw new TemplateProcessingException("Unrecognized complex expression: " + expression.getClass().getName());
        
    }
    
    
}

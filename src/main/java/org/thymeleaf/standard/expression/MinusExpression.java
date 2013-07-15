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

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class MinusExpression extends ComplexExpression {


    private static final long serialVersionUID = -9056215047277857192L;
    

    private static final Logger logger = LoggerFactory.getLogger(MinusExpression.class);

    
    private static final char OPERATOR = '-';

    
    private final Expression operand;

    
    public MinusExpression(final Expression operand) {
        super();
        Validate.notNull(operand, "Operand cannot be null");
        this.operand = operand;
    }
    
    public Expression getOperand() {
        return this.operand;
    }

    
    
    @Override
    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(OPERATOR);
        if (this.operand instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.operand);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.operand);
        }
        return sb.toString();
    }
    
    
    
    
    public static List<ExpressionParsingNode> composeMinusExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {

        // Returning "result" means "try next in chain"
        // Returning "null" means parsing error
        
        final ExpressionParsingNode inputParsingNode = decomposition.get(inputIndex);
        
        List<ExpressionParsingNode> result = decomposition;
        
        final String input = inputParsingNode.getInput();
            
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final String trimmedInput = input.trim(); 
        
        // Trying to fail quickly...
        int operatorPos = trimmedInput.lastIndexOf(OPERATOR);
        if (operatorPos == -1) { 
            return result;
        }
        if (operatorPos != 0) {
            // The '-' symbol should be the first character, after trimming.
            return result;
        }
        
        final String operandStr = trimmedInput.substring(1);
        
        int index = Expression.placeHolderToIndex(operandStr);
        if (index == -1) {
            index = result.size();
            result.add(new ExpressionParsingNode(operandStr));
            result = ComplexExpression.composeComplexExpressions(result, index);
            if (result == null) {
                return null;
            }
        } else {
            result = ComplexExpression.composeComplexExpressions(result, index);
            if (result == null) {
                return null;
            }
        }

        
        final ExpressionParsingNode epn = result.get(index);
        final Expression expr = epn.getExpression();
        if (expr == null) {
            return null;
        }

        
        final MinusExpression minusExpression = new MinusExpression(expr); 
        result.set(inputIndex, new ExpressionParsingNode(minusExpression));
        
        return result;
        
    }
    
    

    static Object executeMinus(final Configuration configuration, final IProcessingContext processingContext, 
            final MinusExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating minus expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object operandValue = 
            Expression.execute(configuration, processingContext, expression.getOperand(), expressionEvaluator, expContext);
        
        if (operandValue == null) {
            operandValue = "null";
        }
        
        final BigDecimal operandNumberValue = ObjectUtils.evaluateAsNumber(operandValue);
        if (operandNumberValue != null) {
            // Addition will act as a mathematical 'plus'
            return operandNumberValue.multiply(BigDecimal.valueOf(-1));
        }
        
        throw new TemplateProcessingException(
            "Cannot execute minus: operand is \"" + LiteralValue.unwrap(operandValue) + "\"");
        
    }

    
}

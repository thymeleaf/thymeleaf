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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Minus (numeric negative) complex expression (Thymeleaf Standard Expressions)
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
public final class MinusExpression extends ComplexExpression {


    private static final long serialVersionUID = -9056215047277857192L;
    

    private static final Logger logger = LoggerFactory.getLogger(MinusExpression.class);

    
    private static final char OPERATOR = '-';
    // Future proof, just in case in the future we add other tokens as operators
    static final String[] OPERATORS = new String[] {String.valueOf(OPERATOR)};

    
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
    
    
    
    
    public static ExpressionParsingState composeMinusExpression(
            final ExpressionParsingState state, final int nodeIndex) {

        // Returning "state" means "try next in chain" or "success"
        // Returning "null" means parsing error

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final String trimmedInput = input.trim();
        
        // Trying to fail quickly...
        final int operatorPos = trimmedInput.lastIndexOf(OPERATOR);
        if (operatorPos == -1) { 
            return state;
        }
        if (operatorPos != 0) {
            // The '-' symbol should be the first character, after trimming.
            return state;
        }
        
        final String operandStr = trimmedInput.substring(1);

        final Expression operandExpr = ExpressionParsingUtil.parseAndCompose(state, operandStr);
        if (operandExpr == null) {
            return null;
        }

        final MinusExpression minusExpression = new MinusExpression(operandExpr);
        state.setNode(nodeIndex, minusExpression);
        
        return state;
        
    }
    
    

    static Object executeMinus(
            final IExpressionContext context,
            final MinusExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating minus expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object operandValue = expression.getOperand().execute(context, expContext);
        
        if (operandValue == null) {
            operandValue = "null";
        }

        final BigDecimal operandNumberValue = EvaluationUtils.evaluateAsNumber(operandValue);
        if (operandNumberValue != null) {
            // Addition will act as a mathematical 'plus'
            return operandNumberValue.multiply(BigDecimal.valueOf(-1));
        }
        
        throw new TemplateProcessingException(
            "Cannot execute minus: operand is \"" + LiteralValue.unwrap(operandValue) + "\"");
        
    }

    
}

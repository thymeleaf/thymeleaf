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
 *   Boolean negation complex expression (Thymeleaf Standard Expressions)
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
public final class NegationExpression extends ComplexExpression {

    
    private static final long serialVersionUID = -7131967162611145337L;


    private static final Logger logger = LoggerFactory.getLogger(NegationExpression.class);

    
    private static final String OPERATOR_1 = "!";
    private static final String OPERATOR_2 = "not";
    // Future proof, just in case in the future we add "not" as an operator
    static final String[] OPERATORS = new String[] {OPERATOR_1, OPERATOR_2};

    
    private final Expression operand;

    
    public NegationExpression(final Expression operand) {
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
        sb.append(OPERATOR_1);
        if (this.operand instanceof ComplexExpression) {
            sb.append(Expression.NESTING_START_CHAR);
            sb.append(this.operand);
            sb.append(Expression.NESTING_END_CHAR);
        } else {
            sb.append(this.operand);
        }
        return sb.toString();
    }
    
    
    
    
    public static ExpressionParsingState composeNegationExpression(
            final ExpressionParsingState state, final int nodeIndex) {

        // Returning "state" means "try next in chain" or "success"
        // Returning "null" means parsing error

        final String input = state.get(nodeIndex).getInput();

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final String trimmedInput = input.trim(); 
        
        // Trying to fail quickly...
        String operatorFound = null;
        int operatorPos = trimmedInput.lastIndexOf(OPERATOR_1);
        if (operatorPos == -1) {
            operatorPos = trimmedInput.lastIndexOf(OPERATOR_2);
            if (operatorPos == -1) {
                return state;
            }
            operatorFound = OPERATOR_2;
        } else {
            operatorFound = OPERATOR_1;
        }

        if (operatorPos != 0) {
            // The operator (any of them) should be at the first character, after trimming.
            return state;
        }
        
        final String operandStr = trimmedInput.substring(operatorFound.length());

        final Expression operandExpr = ExpressionParsingUtil.parseAndCompose(state, operandStr);
        if (operandExpr == null) {
            return null;
        }

        final NegationExpression minusExpression = new NegationExpression(operandExpr);
        state.setNode(nodeIndex, minusExpression);
        
        return state;
        
    }
    
    

    static Object executeNegation(
            final IExpressionContext context,
            final NegationExpression expression, final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating negation expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final Object operandValue = expression.getOperand().execute(context, expContext);

        final boolean operandBooleanValue = EvaluationUtils.evaluateAsBoolean(operandValue);
        
        return Boolean.valueOf(!operandBooleanValue);
        
    }

    
}

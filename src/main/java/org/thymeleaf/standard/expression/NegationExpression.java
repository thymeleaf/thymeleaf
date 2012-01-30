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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class NegationExpression extends ComplexExpression {

    
    private static final long serialVersionUID = -7131967162611145337L;


    private static final Logger logger = LoggerFactory.getLogger(NegationExpression.class);

    
    private static final char OPERATOR = '!';

    
    private final Expression operand;

    
    private NegationExpression(final Expression operand) {
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
    
    
    
    
    public static List<ExpressionParsingNode> composeNegationExpression(
            final List<ExpressionParsingNode> decomposition, int inputIndex) {

        // Returning "result" means "try next in chain"
        // Returning "null" means parsing error
        
        final ExpressionParsingNode inputParsingNode = decomposition.get(inputIndex);
        
        List<ExpressionParsingNode> result = decomposition;
        
        final String input = inputParsingNode.getInput();
            
        if (input == null || input.trim().equals("")) {
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

        
        final NegationExpression minusExpression = new NegationExpression(expr); 
        result.set(inputIndex, new ExpressionParsingNode(minusExpression));
        
        return result;
        
    }
    
    

    static Object executeNegation(final Arguments arguments, final TemplateResolution templateResolution, 
            final NegationExpression expression, final IStandardExpressionEvaluator expressionEvaluator) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating negation expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        Object operandValue = 
            Expression.execute(arguments, templateResolution, expression.getOperand(), expressionEvaluator);
        
        final boolean operandBooleanValue = ObjectUtils.evaluateAsBoolean(operandValue);
        
        return Boolean.valueOf(!operandBooleanValue);
        
    }

    
}

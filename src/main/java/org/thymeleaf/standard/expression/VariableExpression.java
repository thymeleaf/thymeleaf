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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class VariableExpression extends SimpleExpression implements IStandardVariableExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(VariableExpression.class);
    

    private static final long serialVersionUID = -4911752782987240708L;
    

    static final char SELECTOR = '$';
    
    
    private static final Pattern VAR_PATTERN = 
        Pattern.compile("^\\s*\\$\\{(.+?)\\}\\s*$", Pattern.DOTALL);
    
    static final Expression NULL_VALUE = VariableExpression.parseVariableExpression("${null}");


    
    
    private final String expression;
    private final boolean convertToString;

    private volatile Object cachedExpression = null;
    
    
    
    public VariableExpression(final String expression) {
        this(expression, false);
    }


    /**
     * 
     * @param expression expression
     * @param convertToString convertToString
     * @since 2.1.0
     */
    public VariableExpression(final String expression, final boolean convertToString) {
        super();
        Validate.notNull(expression, "Expression cannot be null");
        this.expression = expression;
        this.convertToString = convertToString;
    }



    public String getExpression() {
        return this.expression;
    }

    public boolean getUseSelectionAsRoot() {
        return false;
    }


    /**
     * 
     * @return the result
     * @since 2.1.0
     */
    public boolean getConvertToString() {
        return this.convertToString;
    }



    // Meant only to be used internally, in order to avoid cache calls
    public Object getCachedExpression() {
        return this.cachedExpression;
    }


    // Meant only to be used internally, in order to avoid cache calls
    public void setCachedExpression(final Object cachedExpression) {
        this.cachedExpression = cachedExpression;
    }


    
    @Override
    public String getStringRepresentation() {
        return String.valueOf(SELECTOR) + 
               String.valueOf(SimpleExpression.EXPRESSION_START_CHAR) +
               (this.convertToString? String.valueOf(SimpleExpression.EXPRESSION_START_CHAR) : "") +
               this.expression +
               (this.convertToString? String.valueOf(SimpleExpression.EXPRESSION_END_CHAR) : "") +
               String.valueOf(SimpleExpression.EXPRESSION_END_CHAR);
    }
    
    
    
    static VariableExpression parseVariableExpression(final String input) {
        final Matcher matcher = VAR_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }
        final String expression = matcher.group(1);
        final int expressionLen = expression.length();
        if (expressionLen > 2 &&
                expression.charAt(0) == SimpleExpression.EXPRESSION_START_CHAR &&
                expression.charAt(expressionLen - 1) == SimpleExpression.EXPRESSION_END_CHAR) {
            // Double brackets = enable to-String conversion
            return new VariableExpression(expression.substring(1, expressionLen - 1), true);
        }
        return new VariableExpression(expression, false);
    }
    

    
    
    
    
    
    static Object executeVariableExpression(
            final IExpressionContext context,
            final VariableExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating variable expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final StandardExpressionExecutionContext evalExpContext =
            (expression.getConvertToString()? expContext.withTypeConversion() : expContext.withoutTypeConversion());

        final Object result = expressionEvaluator.evaluate(context, expression, evalExpContext);

        if (!expContext.getForbidUnsafeExpressionResults()) {
            return result;
        }

        // We are only allowing results of type Number and Boolean, and cosidering the rest of data types "unsafe",
        // as they could be rendered into a non-trustable String. This is mainly useful for helping prevent code
        // injection in th:on* event handlers.
        if (result == null
                || result instanceof Number
                || result instanceof Boolean) {
            return result;
        }

        throw new TemplateProcessingException(
                "Only variable expressions returning numbers or booleans are allowed in this context, any other data" +
                "types are not trusted in the context of this expression, including Strings or any other " +
                "object that could be rendered as a text literal. A typical case is HTML attributes for event handlers (e.g. " +
                "\"onload\"), in which textual data from variables should better be output to \"data-*\" attributes and then " +
                "read from the event handler.");

    }
    
    
    
}

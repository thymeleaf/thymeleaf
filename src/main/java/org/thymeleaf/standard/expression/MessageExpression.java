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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.exceptions.ExpressionEvaluationException;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class MessageExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageExpression.class);
    
    private static final long serialVersionUID = 8394399541792390735L;
    
    
    static final char SELECTOR = '#';
    
    private static final char PARAMS_START_CHAR = '(';
    private static final char PARAMS_END_CHAR = ')';
    
    private static final Pattern MSG_PATTERN = 
        Pattern.compile("^\\s*\\#\\{(.+?)\\}\\s*$", Pattern.DOTALL);
    

    
    private final Expression base;
    private final ExpressionSequence parameters;
         
    
    
    private MessageExpression(final Expression base, final ExpressionSequence parameters) {
        super();
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }
   
    
    
    public Expression getBase() {
        return this.base;
    }
    
    public ExpressionSequence getParameters() {
        return this.parameters;
    }
    
    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }

    @Override
    public String getStringRepresentation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(SELECTOR);
        sb.append(SimpleExpression.EXPRESSION_START_CHAR);
        sb.append(this.base);
        if (hasParameters()) {
            sb.append(PARAMS_START_CHAR);
            sb.append(this.parameters.getStringRepresentation());
            sb.append(PARAMS_END_CHAR);
        }
        sb.append(SimpleExpression.EXPRESSION_END_CHAR);
        return sb.toString();
    }
    

    
    static MessageExpression parseMessage(final String input) {

        final Matcher matcher = MSG_PATTERN.matcher(input);
        
        if (!matcher.matches()) {
            return null;
        }

        final String content = matcher.group(1);

        if (content == null || content.trim().equals("")) {
            return null;
        }
        
        final String trimmedInput = content.trim();
        
        if (trimmedInput.endsWith(String.valueOf(PARAMS_END_CHAR))) {
            
            boolean inLiteral = false;
            int nestParLevel = 0;
            
            for (int i = trimmedInput.length() - 1; i >= 0; i--) {
                
                final char c = trimmedInput.charAt(i);
                
                if (c == TextLiteralExpression.DELIMITER) {
                    
                    if (i == 0 || content.charAt(i - 1) != '\\') {
                        inLiteral = !inLiteral;
                    }
                
                } else if (c == PARAMS_END_CHAR) {
                    
                    nestParLevel++;
                        
                } else if (c == PARAMS_START_CHAR) {
                    
                    nestParLevel--;
                    
                    if (nestParLevel < 0) {
                        return null;
                    }
                    
                    if (nestParLevel == 0) {
                        
                        if (i == 0) {
                            return null;
                        }
                        
                        final String base = trimmedInput.substring(0, i);
                        final String parameters = trimmedInput.substring(i + 1, trimmedInput.length() - 1);

                        final Expression baseExpr = computeBase(base);
                        if (baseExpr == null) {
                            return null;
                        }
                        
                        final ExpressionSequence parametersExprSeq = ExpressionSequence.parse(parameters);
                        if (parametersExprSeq == null) {
                            return null;
                        }
                        
                        return new MessageExpression(baseExpr, parametersExprSeq);
                        
                    }
                    
                }
            }
            
            return null;
            
        }
            
        
        final Expression baseExpr = computeBase(trimmedInput); 
        if (baseExpr == null) {
            return null;
        }
        
        return new MessageExpression(baseExpr, null);
        
    }
    

    
    private static Expression computeBase(final String baseStr) {
        // Base will be tried to be computed first as token, then as expression
        final Token token = Token.parse(baseStr);
        if (token != null) {
            return TextLiteralExpression.parseTextLiteral(token.getValue());
        }
        return Expression.parse(baseStr);
    }
    

    
    
    
    

    static Object executeMessage(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final MessageExpression expression, final IStandardExpressionEvaluator expressionEvaluator) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating message: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final Expression baseExpression = expression.getBase();
        Object messageKey = 
            Expression.execute(arguments, templateResolution, baseExpression, expressionEvaluator);
        messageKey = LiteralValue.unwrap(messageKey);
        if (messageKey != null && !(messageKey instanceof String)) {
            messageKey = messageKey.toString();
        }
        if (messageKey == null || ((String)messageKey).trim().equals("")) {
            throw new ExpressionEvaluationException(
                    "Message key for message resolution must be a non-null and non-empty String");
        }
        

        final Object[] messageParameters = 
            new Object[(expression.hasParameters()? expression.getParameters().size() : 0)];
        int parIndex = 0;
        if (expression.hasParameters()) {
            for (final Expression parameter  : expression.getParameters()) {
                final Object result = 
                    Expression.execute(arguments, templateResolution, parameter, expressionEvaluator);
                messageParameters[parIndex++] = LiteralValue.unwrap(result);
            }
        }

        return MessageResolutionUtils.resolveMessageForTemplate(
                arguments, templateResolution, (String)messageKey, messageParameters);
        
    }

    
    
}

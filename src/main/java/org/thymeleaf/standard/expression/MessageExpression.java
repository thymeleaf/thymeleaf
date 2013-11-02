/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.StringUtils;
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
    

    
    private final IStandardExpression base;
    private final ExpressionSequence parameters;
         
    
    
    public MessageExpression(final IStandardExpression base, final ExpressionSequence parameters) {
        super();
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }
   
    
    
    public IStandardExpression getBase() {
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

        if (StringUtils.isEmptyOrWhitespace(content)) {
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

                        final Expression baseExpr = parseDefaultAsLiteral(base);
                        if (baseExpr == null) {
                            return null;
                        }
                        
                        final ExpressionSequence parametersExprSeq =
                                ExpressionSequenceUtils.internalParseExpressionSequence(parameters);
                        if (parametersExprSeq == null) {
                            return null;
                        }
                        
                        return new MessageExpression(baseExpr, parametersExprSeq);
                        
                    }
                    
                }
            }
            
            return null;
            
        }
            
        
        final Expression baseExpr = parseDefaultAsLiteral(trimmedInput);
        if (baseExpr == null) {
            return null;
        }
        
        return new MessageExpression(baseExpr, null);
        
    }




    private static Expression parseDefaultAsLiteral(final String input) {

        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }

        final Expression expr = Expression.parse(input);
        if (expr == null) {
            return Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(input));
        }
        return expr;

    }

    
    

    static Object executeMessage(final Configuration configuration,
            final IProcessingContext processingContext, final MessageExpression expression, 
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating message: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        if (!(processingContext instanceof Arguments)) {
            throw new TemplateProcessingException(
                    "Cannot evaluate expression \"" + expression + "\". Message externalization expressions " +
                    "can only be evaluated in a template-processing environment (as a part of an in-template expression) " +
                    "where evaluation context is an " + Arguments.class.getClass() + " instance instead of an instance of " +
                    processingContext.getClass().getName());
        }
        
        final Arguments arguments = (Arguments) processingContext;

        final IStandardExpression baseExpression = expression.getBase();
        Object messageKey = baseExpression.execute(configuration, arguments, expContext);
        messageKey = LiteralValue.unwrap(messageKey);
        if (messageKey != null && !(messageKey instanceof String)) {
            messageKey = messageKey.toString();
        }
        if (StringUtils.isEmptyOrWhitespace((String)messageKey)) {
            throw new TemplateProcessingException(
                    "Message key for message resolution must be a non-null and non-empty String");
        }
        

        final Object[] messageParameters = 
            new Object[(expression.hasParameters()? expression.getParameters().size() : 0)];
        if (expression.hasParameters()) {
            int parIndex = 0;
            for (final IStandardExpression parameter  : expression.getParameters()) {
                final Object result = parameter.execute(configuration, arguments, expContext);
                messageParameters[parIndex++] = LiteralValue.unwrap(result);
            }
        }

        return MessageResolutionUtils.resolveMessageForTemplate(
                arguments, (String)messageKey, messageParameters);
        
    }

    
    
}

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * @since 1.1
 *
 */
public final class LinkExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkExpression.class);
    
    private static final long serialVersionUID = -564516592085017252L;
    
    static final char SELECTOR = '@';
    private static final char PARAMS_START_CHAR = '(';
    private static final char PARAMS_END_CHAR = ')';
    
    private static final Pattern LINK_PATTERN = 
        Pattern.compile("^\\s*\\@\\{(.+?)\\}\\s*$", Pattern.DOTALL);
    
    private static final List<Object> EMPTY_PARAMETER_VALUE = Collections.singletonList((Object)"");
    
    
    private final Expression base;
    private final AssignationSequence parameters;
    
    
    
    
    public LinkExpression(final Expression base, final AssignationSequence parameters) {
        super();
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }
    
    
    
    
    public Expression getBase() {
        return this.base;
    }
    
    public AssignationSequence getParameters() {
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

    
    
    
    static LinkExpression parseLink(final String input) {
        
        final Matcher matcher = LINK_PATTERN.matcher(input);
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
                        
                        final AssignationSequence parametersAssigSeq = AssignationSequence.parse(parameters);
                        if (parametersAssigSeq == null) {
                            return null;
                        }
                        
                        return new LinkExpression(baseExpr, parametersAssigSeq);
                        
                    }
                    
                }
            }
            
            return null;
            
        }

        final Expression baseExpr = computeBase(trimmedInput); 
        if (baseExpr == null) {
            return null;
        }
        
        return new LinkExpression(baseExpr, null);
        
    }
    
    

    
    private static Expression computeBase(final String baseStr) {
        // Base will be tried to be computed first as token, then as expression
        final Token token = Token.parse(baseStr);
        if (token != null) {
            return TextLiteralExpression.parseTextLiteral(token.getValue());
        }
        return Expression.parse(baseStr);
    }
    

    
    
    
    
    


    static Object executeLink(
            final Arguments arguments, final LinkExpression expression, 
            final IStandardExpressionEvaluator expressionEvaluator) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating link: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final Expression baseExpression = expression.getBase();
        Object base = 
            Expression.execute(arguments, baseExpression, expressionEvaluator);
        base = LiteralValue.unwrap(base);
        if (base == null || !(base instanceof String) || ((String)base).trim().equals("")) {
            throw new TemplateProcessingException(
                    "Base for message resolution must be a non-null and non-empty String " +
                    "(currently: " + (base == null? null : base.getClass().getName()) + ")");
        }

        final String linkBase = (String) base;
        
        if (!isWebContext(arguments.getContext()) && !isLinkBaseAbsolute(linkBase)) {
            throw new TemplateProcessingException(
                    "Link base \"" + linkBase + "\" is not absolute. Non-absolute links " +
                    "can only be processed if context implements the " + 
                    IWebContext.class.getName() + " interface (context is of class: " +
                    arguments.getContext().getClass().getName() + ")");
        }
        
        @SuppressWarnings("unchecked")
        final Map<String,List<Object>> parameters =
            (expression.hasParameters()?
                    resolveParameters(arguments, expression.getParameters(), expressionEvaluator) :
                    (Map<String,List<Object>>) Collections.EMPTY_MAP);
        
        
        final int questionMarkPosition = linkBase.indexOf("?"); 
        
        final StringBuffer parametersBuffer = new StringBuffer();
        for (final Map.Entry<String,List<Object>> parameterEntry : parameters.entrySet()) {
            final String parameterName = parameterEntry.getKey();
            for (final Object parameterObjectValue : parameterEntry.getValue()) {
                if (parametersBuffer.length() == 0) {
                    if (questionMarkPosition == -1) {
                        parametersBuffer.append("?");
                    } else {
                        parametersBuffer.append("&");
                    }
                } else {
                    parametersBuffer.append("&");
                }
                final String parameterValue =
                    (parameterObjectValue == null? "" : parameterObjectValue.toString());
                try {
                    parametersBuffer.append(parameterName + "=" + URLEncoder.encode(parameterValue, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new TemplateProcessingException("Exception while processing link parameters", e);
                }
            }
        }
        
        if (isLinkBaseAbsolute(linkBase)) {
            return linkBase + parametersBuffer.toString();
        }
        
        final IWebContext webContext = (IWebContext) arguments.getContext();
        
        String sessionFragment = "";
        
        final HttpServletRequest request = webContext.getHttpServletRequest();
        final HttpSession session = webContext.getHttpSession();
        if(null != session){
            final String sessionID  = session.getId();
            
            if (!request.isRequestedSessionIdFromCookie()) {
                sessionFragment = ";jsessionid=" + sessionID;
            }
        }
        
        if (isLinkBaseContextRelative(linkBase)) {
            
            final String contextName = request.getContextPath();
            
            if (questionMarkPosition == -1) {
                return contextName + linkBase + sessionFragment + parametersBuffer.toString();
            }
            
            final String linkBasePart1 = linkBase.substring(0,questionMarkPosition);
            final String linkBasePart2 = linkBase.substring(questionMarkPosition);
            return contextName + linkBasePart1 + sessionFragment + linkBasePart2 + parametersBuffer.toString();
            
        }
        
        return linkBase + sessionFragment + parametersBuffer.toString();
        
    }
    
    

    
    private static boolean isWebContext(final IContext context) {
        return context instanceof IWebContext;
    }
    
    
    private static boolean isLinkBaseAbsolute(final String linkBase) {
        return (linkBase.contains("://") || linkBase.toLowerCase().startsWith("mailto:"));
    }
    
    
    private static boolean isLinkBaseContextRelative(final String linkBase) {
        return linkBase.startsWith("/");
    }
    
    
    private static Map<String,List<Object>> resolveParameters(
            final Arguments arguments, final AssignationSequence assignationValues, 
            final IStandardExpressionEvaluator expressionEvaluator) {
        
        final Map<String,List<Object>> parameters = new LinkedHashMap<String,List<Object>>(assignationValues.size() + 1, 1.0f);
        for (final Assignation assignationValue : assignationValues) {
            
            final String parameterName = assignationValue.getLeft().getValue();
            final Expression parameterExpression = assignationValue.getRight();
            
            final Object value = Expression.execute(arguments, parameterExpression, expressionEvaluator);
            if (value == null) {
                parameters.put(parameterName, EMPTY_PARAMETER_VALUE);
            } else {
                parameters.put(parameterName, convertParameterValueToList(LiteralValue.unwrap(value)));
            }
            
        }
        return parameters;
        
    }

    
    
    
    private static List<Object> convertParameterValueToList(final Object parameterValue) {
        
        if (parameterValue instanceof Iterable<?>) {
            final List<Object> result = new ArrayList<Object>();
            for (final Object obj : (Iterable<?>) parameterValue) {
                result.add(obj);
            }
            return result;
        } else if (parameterValue.getClass().isArray()){
            final List<Object> result = new ArrayList<Object>();
            if (parameterValue instanceof byte[]) {
                for (final byte obj : (byte[]) parameterValue) {
                    result.add(Byte.valueOf(obj));
                }
            } else if (parameterValue instanceof short[]) {
                for (final short obj : (short[]) parameterValue) {
                    result.add(Short.valueOf(obj));
                }
            } else if (parameterValue instanceof int[]) {
                for (final int obj : (int[]) parameterValue) {
                    result.add(Integer.valueOf(obj));
                }
            } else if (parameterValue instanceof long[]) {
                for (final long obj : (long[]) parameterValue) {
                    result.add(Long.valueOf(obj));
                }
            } else if (parameterValue instanceof float[]) {
                for (final float obj : (float[]) parameterValue) {
                    result.add(Float.valueOf(obj));
                }
            } else if (parameterValue instanceof double[]) {
                for (final double obj : (double[]) parameterValue) {
                    result.add(Double.valueOf(obj));
                }
            } else if (parameterValue instanceof boolean[]) {
                for (final boolean obj : (boolean[]) parameterValue) {
                    result.add(Boolean.valueOf(obj));
                }
            } else if (parameterValue instanceof char[]) {
                for (final char obj : (char[]) parameterValue) {
                    result.add(Character.valueOf(obj));
                }
            } else {
                for (final Object obj : (Object[]) parameterValue) {
                    result.add(obj);
                }
            }
            return result;
        } else{
            return Collections.singletonList(parameterValue);
        }
        
    }
    

    
}

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
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
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
    
    private static final String URL_PARAM_NO_VALUE = "%%%__NO_VALUE__%%%";
    
    
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
                            // It was not a parameter specification, but a base URL surrounded by parentheses! 
                            final Expression baseExpr = parseBaseDefaultAsLiteral(trimmedInput);
                            if (baseExpr == null) {
                                return null;
                            }
                            return new LinkExpression(baseExpr, null);
                        }
                        
                        final String base = trimmedInput.substring(0, i).trim();
                        final String parameters = trimmedInput.substring(i + 1, trimmedInput.length() - 1).trim();

                        final Expression baseExpr = parseBaseDefaultAsLiteral(base);
                        if (baseExpr == null) {
                            return null;
                        }
                        
                        final AssignationSequence parametersAssigSeq = 
                                AssignationSequence.parse(
                                        parameters, true /* allow parameters without value or equals sign */);
                        if (parametersAssigSeq == null) {
                            return null;
                        }
                        
                        return new LinkExpression(baseExpr, parametersAssigSeq);
                        
                    }
                    
                }
            }

            return null;
            
        }

        final Expression baseExpr = parseBaseDefaultAsLiteral(trimmedInput);
        if (baseExpr == null) {
            return null;
        }

        return new LinkExpression(baseExpr, null);
        
    }





    private static Expression parseBaseDefaultAsLiteral(final String base) {

        if (StringUtils.isEmptyOrWhitespace(base)) {
            return null;
        }

        final Expression expr = Expression.parse(base);
        if (expr == null) {
            return Expression.parse(TextLiteralExpression.wrapStringIntoLiteral(base));
        }
        return expr;

    }

    
    
    


    static Object executeLink(final Configuration configuration,
            final IProcessingContext processingContext, final LinkExpression expression, 
            final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating link: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        final Expression baseExpression = expression.getBase();
        Object base = 
            Expression.execute(configuration, processingContext, baseExpression, expressionEvaluator, expContext);
        base = LiteralValue.unwrap(base);
        if (base == null || !(base instanceof String) || StringUtils.isEmptyOrWhitespace((String) base)) {
            throw new TemplateProcessingException(
                    "Base for link URL creation must be a non-null and non-empty String " +
                    "(currently: " + (base == null? null : base.getClass().getName()) + ")");
        }

        String linkBase = (String) base;
        
        if (!isWebContext(processingContext.getContext()) && !isLinkBaseAbsolute(linkBase) && !isLinkBaseServerRelative(linkBase)) {
            throw new TemplateProcessingException(
                    "Link base \"" + linkBase + "\" cannot be context relative (/) or page relative unless you implement the " + 
                    IWebContext.class.getName() + " interface (context is of class: " +
                    processingContext.getContext().getClass().getName() + ")");
        }
        
        @SuppressWarnings("unchecked")
        final Map<String,List<Object>> parameters =
            (expression.hasParameters()?
                    resolveParameters(configuration, processingContext, expression, expressionEvaluator, expContext) :
                    (Map<String,List<Object>>) Collections.EMPTY_MAP);
        
        /*
         * Detect URL fragments (selectors after '#') so that they can be output at the end of 
         * the URL, after parameters.
         */
        final int hashPosition = linkBase.indexOf('#');
        String urlFragment = "";
        // If hash position == 0 we will not consider it as marking an
        // URL fragment.
        if (hashPosition > 0) {
            // URL fragment String will include the # sign
            urlFragment = linkBase.substring(hashPosition);
            linkBase = linkBase.substring(0, hashPosition);
        }
        
        /*
         * Check for the existence of a question mark symbol in the link base itself
         */
        final int questionMarkPosition = linkBase.indexOf('?');
        
        final StringBuilder parametersBuilder = new StringBuilder();
        
        for (final Map.Entry<String,List<Object>> parameterEntry : parameters.entrySet()) {
            
            final String parameterName = parameterEntry.getKey();
            final List<Object> parameterValues = parameterEntry.getValue();
            
            for (final Object parameterObjectValue : parameterValues) {

                // Insert a separator with the previous parameter, if needed
                if (parametersBuilder.length() == 0) {
                    if (questionMarkPosition == -1) {
                        parametersBuilder.append("?");
                    } else {
                        parametersBuilder.append("&");
                    }
                } else {
                    parametersBuilder.append("&");
                }

                final String parameterValue =
                    (parameterObjectValue == null? "" : parameterObjectValue.toString());
                
                if (URL_PARAM_NO_VALUE.equals(parameterValue)) {
                    
                    // This is a parameter without a value and even without an "=" symbol
                    parametersBuilder.append(parameterName);
                    
                } else {
                
                    try {
                        parametersBuilder.append(parameterName).append("=").append(URLEncoder.encode(parameterValue, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new TemplateProcessingException("Exception while processing link parameters", e);
                    }
                    
                }
                
            }
            
        }

        
        /*
         * Context is not web: URLs can only be absolute or server-relative
         */
        if (!isWebContext(processingContext.getContext())) {
            
            if (isLinkBaseAbsolute(linkBase)) {
                return linkBase + parametersBuilder + urlFragment;
            }
            // isLinkBaseServerRelative(linkBase) == true
            return linkBase.substring(1) + parametersBuilder + urlFragment;
            
        }
        

        /*
         * Context is web 
         */
        
        final IWebContext webContext = (IWebContext) processingContext.getContext();
        
        final HttpServletRequest request = webContext.getHttpServletRequest();
        final HttpServletResponse response = webContext.getHttpServletResponse();

        String url = null;
        
        if (isLinkBaseContextRelative(linkBase)) {
            
            url = request.getContextPath() + linkBase + parametersBuilder + urlFragment;
            
        } else if (isLinkBaseServerRelative(linkBase)) {
            
            // remove the "~" from the link base
            url = linkBase.substring(1) + parametersBuilder + urlFragment;
            
        } else if (isLinkBaseAbsolute(linkBase)) {
            
            url = linkBase + parametersBuilder + urlFragment;
            
        } else {
            // Link base is current-URL-relative
            
            url = linkBase + parametersBuilder + urlFragment;
            
        }

        return (response != null? response.encodeURL(url) : url);
        
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
    
    
    private static boolean isLinkBaseServerRelative(final String linkBase) {
        return linkBase.startsWith("~/");
    }
    
    
    private static Map<String,List<Object>> resolveParameters(
            final Configuration configuration, final IProcessingContext processingContext, 
            final LinkExpression expression, final IStandardVariableExpressionEvaluator expressionEvaluator,
            final StandardExpressionExecutionContext expContext) {

        final AssignationSequence assignationValues = expression.getParameters();

        final Map<String,List<Object>> parameters = new LinkedHashMap<String,List<Object>>(assignationValues.size() + 1, 1.0f);
        for (final Assignation assignationValue : assignationValues) {
            
            final Expression parameterNameExpr = assignationValue.getLeft();
            final Expression parameterValueExpr = assignationValue.getRight();

            // We know parameterNameExpr cannot be null (the Assignation class would not allow it)
            final Object parameterNameValue =
                    Expression.execute(configuration, processingContext, parameterNameExpr, expressionEvaluator, expContext);
            final String parameterName =
                    (parameterNameValue == null? null : parameterNameValue.toString());

            if (StringUtils.isEmptyOrWhitespace(parameterName)) {
                throw new TemplateProcessingException(
                        "Parameters in link expression \"" + expression.getStringRepresentation() + "\" are " +
                        "incorrect: parameter name expression \"" + parameterNameExpr.getStringRepresentation() +
                        "\" evaluated as null or empty string.");
            }

            List<Object> currentParameterValues = parameters.get(parameterName);
            if (currentParameterValues == null) {
                currentParameterValues = new ArrayList<Object>(4);
                parameters.put(parameterName, currentParameterValues);
            }
            
            if (parameterValueExpr == null) {
                // If this is null, it means we want to render the parameter without a value and
                // also without an equals sign.
                currentParameterValues.add(URL_PARAM_NO_VALUE);
            } else {
                final Object value = 
                        Expression.execute(configuration, processingContext, parameterValueExpr, expressionEvaluator, expContext);
                if (value == null) {
                    // Not the same as not specifying a value!
                    currentParameterValues.add("");
                } else {
                    currentParameterValues.addAll(convertParameterValueToList(LiteralValue.unwrap(value)));
                }
            }
            
        }
        return parameters;
        
    }

    
    
    
    private static List<Object> convertParameterValueToList(final Object parameterValue) {
        
        if (parameterValue instanceof Iterable<?>) {
            final List<Object> result = new ArrayList<Object>(4);
            for (final Object obj : (Iterable<?>) parameterValue) {
                result.add(obj);
            }
            return result;
        } else if (parameterValue.getClass().isArray()){
            final List<Object> result = new ArrayList<Object>(4);
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
                final Object[] objParameterValue = (Object[]) parameterValue;
                Collections.addAll(result, objParameterValue);
            }
            return result;
        } else{
            return Collections.singletonList(parameterValue);
        }
        
    }
    

    
}

/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.aurora.context.IProcessingContext;
import org.thymeleaf.aurora.context.IWebContext;
import org.thymeleaf.aurora.context.IWebVariablesMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;
import org.unbescape.uri.UriEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * @since 1.1 (reimplemented in 3.0.0)
 *
 */
public final class LinkExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkExpression.class);
    
    private static final long serialVersionUID = -564516592085017252L;
    
    static final char SELECTOR = '@';
    private static final char PARAMS_START_CHAR = '(';
    private static final char PARAMS_END_CHAR = ')';
    private static final char URL_TEMPLATE_DELIMITER_PREFIX = '{';
    private static final char URL_TEMPLATE_DELIMITER_SUFFIX = '}';

    private static final Pattern LINK_PATTERN = 
        Pattern.compile("^\\s*\\@\\{(.+?)\\}\\s*$", Pattern.DOTALL);
    
    private static final String URL_PARAM_NO_VALUE = "%%%__NO_VALUE__%%%";


    
    private final IStandardExpression base;
    private final AssignationSequence parameters;
    
    
    
    
    public LinkExpression(final IStandardExpression base, final AssignationSequence parameters) {
        super();
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
        this.parameters = parameters;
    }
    
    
    
    
    public IStandardExpression getBase() {
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
                
                } else if (!inLiteral && c == PARAMS_END_CHAR) {
                    
                    nestParLevel++;
                        
                } else if (!inLiteral && c == PARAMS_START_CHAR) {
                    
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
                                AssignationUtils.internalParseAssignationSequence(
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

    
    
    


    static Object executeLink(
            final IProcessingContext processingContext, final LinkExpression expression,
            final StandardExpressionExecutionContext expContext) {

        /*
         *  DEVELOPMENT NOTE: Reasons why Spring's RequestDataValueProcessor#processUrl(...) is not applied here
         *                    instead of at th:href and th:src.
         *
         *      1. Reduce complexity, as Dialects would need to add one more execution attribute for a wrapper
         *         able to apply such post-processor.
         *      2. Avoid link expressions in "th:action" be applied "processUrl(...)" and then "processAction(...)",
         *         which would break compatiliby with Spring's FormTag class.
         *         - The only way to avoid this would be to mess around with StandardExpressionExecutionContexts,
         *           which would mean much more complexity.
         *      3. Avoid that URLs that are not link expressions (or not only) but are anyway expressed with th:href
         *         or th:src end up not being processed.
         */

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating link: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        final IStandardExpression baseExpression = expression.getBase();
        Object base = baseExpression.execute(processingContext, expContext);

        base = LiteralValue.unwrap(base);
        if (base != null && !(base instanceof String)) {
            base = base.toString();
        }
        if (base == null || StringUtils.isEmptyOrWhitespace((String) base)) {
            base = "";
        }

        String linkBase = (String) base;
        
        if (!processingContext.isWeb() && !isLinkBaseAbsolute(linkBase) && !isLinkBaseServerRelative(linkBase)) {
            throw new TemplateProcessingException(
                    "Link base \"" + linkBase + "\" cannot be context relative (/) or page relative unless the context " +
                    "used for executing the engine implements the " + IWebContext.class.getName() + " interface");
        }
        
        @SuppressWarnings("unchecked")
        final Map<String,List<Object>> parameters =
            (expression.hasParameters()?
                    resolveParameters(processingContext, expression, expContext) :
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

        linkBase = replaceTemplateParamsInBase(linkBase, parameters);

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

                parametersBuilder.append(UriEscape.escapeUriQueryParam(parameterName));

                final String parameterValue = (parameterObjectValue == null? "" : parameterObjectValue.toString());

                if (!URL_PARAM_NO_VALUE.equals(parameterValue)) {
                    parametersBuilder.append("=").append(UriEscape.escapeUriQueryParam(parameterValue));
                }
                
            }
            
        }

        
        /*
         * Context is not web: URLs can only be absolute or server-relative
         */
        if (!processingContext.isWeb()) {
            
            if (isLinkBaseAbsolute(linkBase)) {
                return linkBase + parametersBuilder + urlFragment;
            }
            // isLinkBaseServerRelative(linkBase) == true
            return linkBase.substring(1) + parametersBuilder + urlFragment;
            
        }
        

        /*
         * Context is web 
         */
        
        final IWebVariablesMap webVariablesMap = (IWebVariablesMap) processingContext.getVariablesMap();
        
        final HttpServletRequest request = webVariablesMap.getRequest();
        final HttpServletResponse response = webVariablesMap.getResponse();

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
    
    

    
    private static boolean isLinkBaseAbsolute(final String linkBase) {
        return (linkBase.contains("://") ||
                linkBase.toLowerCase().startsWith("mailto:") || // Email URLs
                linkBase.startsWith("//")); // protocol-relative URLs
    }


    private static boolean isLinkBaseContextRelative(final String linkBase) {
        return linkBase.startsWith("/") && !linkBase.startsWith("//");
    }

    
    private static boolean isLinkBaseServerRelative(final String linkBase) {
        return linkBase.startsWith("~/");
    }
    
    
    private static Map<String,List<Object>> resolveParameters(
            final IProcessingContext processingContext, final LinkExpression expression,
            final StandardExpressionExecutionContext expContext) {

        final AssignationSequence assignationValues = expression.getParameters();

        final Map<String,List<Object>> parameters = new LinkedHashMap<String,List<Object>>(assignationValues.size() + 1, 1.0f);
        for (final Assignation assignationValue : assignationValues) {
            
            final IStandardExpression parameterNameExpr = assignationValue.getLeft();
            final IStandardExpression parameterValueExpr = assignationValue.getRight();

            // We know parameterNameExpr cannot be null (the Assignation class would not allow it)
            final Object parameterNameValue = parameterNameExpr.execute(processingContext, expContext);
            final String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

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
                        parameterValueExpr.execute(processingContext, expContext);
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




    static String replaceTemplateParamsInBase(final String linkBase, final Map<String,List<Object>> parameters) {

        /*
         * Before trying to perform any matching operations for variable templates, we try to determine
         * whether they would be really needed. If no '{' char is found in linkBase, then it is just returned
         * back unchanged.
         */
        final int linkBaseLen = linkBase.length();
        boolean templateFound = false;
        for (int i = 0; i < linkBaseLen; i++) {
            final char c = linkBase.charAt(i);
            if (c == URL_TEMPLATE_DELIMITER_PREFIX) {
                templateFound = true;
                break;
            }
        }
        if (!templateFound) {
            return linkBase;
        }

        /*
         * Search {templateVar} in linkBase, and replace with value.
         * Parameters can be multivalued, in which case they will be comma-separated.
         * Parameter values will be URL-path-encoded. If there is a '?' char, only parameter values before this
         * char will be URL-path-encoded, whereas parameters after it will be URL-query-encoded.
         */

        final int questionMarkPosition = linkBase.indexOf('?');

        String basePath;
        String baseQuery;
        if (questionMarkPosition == -1) {
            basePath = linkBase;
            baseQuery = null;
        } else {
            basePath = linkBase.substring(0, questionMarkPosition);
            baseQuery = linkBase.substring(questionMarkPosition);
        }

        final Set<String> usedParams = new HashSet<String>(5);
        for (final Map.Entry<String,List<Object>> param : parameters.entrySet()) {

            final String paramName = param.getKey();
            final List<Object> paramValues = param.getValue();
            final String template = URL_TEMPLATE_DELIMITER_PREFIX + paramName + URL_TEMPLATE_DELIMITER_SUFFIX;

            if (basePath.contains(template)) {

                usedParams.add(paramName);
                final StringBuilder strBuilder = new StringBuilder();
                for (final Object parameterObjectValue : paramValues) {
                    final String parameterValue = (parameterObjectValue == null? "" : parameterObjectValue.toString());
                    if (!URL_PARAM_NO_VALUE.equals(parameterValue)) {
                        if (strBuilder.length() > 0) {
                            strBuilder.append(',');
                        }
                        strBuilder.append(parameterValue);
                    }
                }
                basePath = basePath.replace(template, UriEscape.escapeUriPath(strBuilder.toString()));

            } else if (baseQuery != null && baseQuery.contains(template)) {

                usedParams.add(paramName);
                final StringBuilder strBuilder = new StringBuilder();
                for (final Object parameterObjectValue : paramValues) {
                    final String parameterValue = (parameterObjectValue == null? "" : parameterObjectValue.toString());
                    if (!URL_PARAM_NO_VALUE.equals(parameterValue)) {
                        if (strBuilder.length() > 0) {
                            strBuilder.append(',');
                        }
                        strBuilder.append(parameterValue);
                    }
                }
                baseQuery = baseQuery.replace(template, UriEscape.escapeUriQueryParam(strBuilder.toString()));

            }

        }

        /*
         * Once parameters are applied as templates, we remove them from the parameters map so that they are not
         * additionally added to the query string.
         */
        for (final String usedParam : usedParams) {
            parameters.remove(usedParam);
        }

        return basePath + (baseQuery != null? baseQuery : "");

    }

    
}

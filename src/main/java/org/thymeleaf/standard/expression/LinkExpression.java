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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Link expression (Thymeleaf Standard Expressions)
 * </p>
 * <p>
 *   Note a class with this name existed since 1.1, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * @since 3.0.0
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

    
    
    
    static LinkExpression parseLinkExpression(final String input) {
        
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

    
    
    


    static Object executeLinkExpression(final IExpressionContext context, final LinkExpression expression) {

        /*
         *  DEVELOPMENT NOTE: Reasons why Spring's RequestDataValueProcessor#processUrl(...) is not applied here
         *                    instead of at th:href and th:src.
         *
         *      1. Reduce complexity, as Dialects would need to add one more execution attribute for a wrapper
         *         able to apply such post-processor.
         *      2. Avoid link expressions in "th:action" be applied "processUrl(...)" and then "processAction(...)",
         *         which would break compatibility with Spring's FormTag class.
         *         - The only way to avoid this would be to mess around with StandardExpressionExecutionContexts,
         *           which would mean much more complexity.
         *      3. Avoid that URLs that are not link expressions (or not only) but are anyway expressed with th:href
         *         or th:src end up not being processed.
         */

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating link: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }

        if (!(context instanceof ITemplateContext)) {
            throw new TemplateProcessingException(
                    "Cannot evaluate expression \"" + expression + "\". Link expressions " +
                    "can only be evaluated in a template-processing environment (as a part of an in-template expression) " +
                    "where processing context is an implementation of " + ITemplateContext.class.getClass() + ", which it isn't (" +
                    context.getClass().getName() + ")");
        }

        final ITemplateContext templateContext = (ITemplateContext)context;

        final IStandardExpression baseExpression = expression.getBase();

        // The URL base in a link expression will always be executed in RESTRICTED mode, so we will forbid that
        // base URLs come directly from user input (request parameters). Note this restriction does not need to apply
        // to URL parameters.
        Object base = baseExpression.execute(templateContext, StandardExpressionExecutionContext.RESTRICTED);

        base = LiteralValue.unwrap(base);
        if (base != null && !(base instanceof String)) {
            base = base.toString();
        }
        if (base == null || StringUtils.isEmptyOrWhitespace((String) base)) {
            base = "";
        }

        /*
         * Resolve the parameters from the expression into a LinkParameters object.
         * Note the parameters variable might be null if there are no parameters
         *
         * Also note that link parameters, which should be correctly URL-encoded before being added to
         * the query string of the URL, will always be executed using UNRESTRICTED mode, so that request
         * params can be directly passed along to other generated URLs.
         */
        final Map<String, Object> parameters =
                resolveParameters(templateContext, expression, StandardExpressionExecutionContext.NORMAL);


        /*
         * Call the link builder with the link base and computed parameters
         */

        return templateContext.buildLink((String)base, parameters);

    }




    
    private static Map<String, Object> resolveParameters(
            final IExpressionContext context,
            final LinkExpression expression, final StandardExpressionExecutionContext expContext) {

        if (!expression.hasParameters()) {
            return null;
        }


        final List<Assignation> assignationValues = expression.getParameters().getAssignations();
        final int assignationValuesLen = assignationValues.size();

        final Map<String,Object> parameters = new LinkedHashMap<String, Object>(assignationValuesLen);
        final HashMap<String,String> normalizedParameterNames = new LinkedHashMap<String, String>(assignationValuesLen + 1, 1.0f);

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignationValue = assignationValues.get(i);
            
            final IStandardExpression parameterNameExpr = assignationValue.getLeft();
            final IStandardExpression parameterValueExpr = assignationValue.getRight();

            // We know parameterNameExpr cannot be null (the Assignation class would not allow it)
            final Object parameterNameValue = parameterNameExpr.execute(context, expContext);
            String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

            if (StringUtils.isEmptyOrWhitespace(parameterName)) {
                throw new TemplateProcessingException(
                        "Parameters in link expression \"" + expression.getStringRepresentation() + "\" are " +
                        "incorrect: parameter name expression \"" + parameterNameExpr.getStringRepresentation() +
                        "\" evaluated as null or empty string.");
            }

            final Object parameterValue;
            if (parameterValueExpr == null) {
                // If this is null, it means we want to render the parameter without a value and
                // also without an equals sign.
                parameterValue = null;
            } else {
                final Object value = parameterValueExpr.execute(context, expContext);
                if (value == null) {
                    // Not the same as not specifying a value!
                    parameterValue = "";
                } else {
                    parameterValue = LiteralValue.unwrap(value);
                }
            }

            // Normalize the parameter name before adding it to the map (the first appearance of the
            final String lowerParameterName = parameterName.toLowerCase();
            if (normalizedParameterNames.containsKey(lowerParameterName)) {
                parameterName = normalizedParameterNames.get(lowerParameterName);
            } else {
                normalizedParameterNames.put(lowerParameterName, parameterName);
            }

            // Add the parameter to tne map
            addParameter(parameters, parameterName, parameterValue);
            
        }
        return parameters;
        
    }


    
    
    private static void addParameter(final Map<String, Object> parameters, final String parameterName, final Object parameterValue) {

        Validate.notEmpty(parameterName, "Parameter name cannot be null");

        final Object normalizedParameterValue = normalizeParameterValue(parameterValue);

        if (parameters.containsKey(parameterName)) {
            // Parameter already exists, therefore we will be appending to an existing value

            Object currentValue = parameters.get(parameterName);

            if (currentValue == null || !(currentValue instanceof List<?>)) {
                final List<Object> newValue = new ArrayList<Object>(3);
                newValue.add(currentValue);
                currentValue = newValue;
                parameters.put(parameterName, currentValue);
            }

            if (normalizedParameterValue != null && normalizedParameterValue instanceof List<?>) {
                ((List<Object>) currentValue).addAll((List<?>)normalizedParameterValue);
            } else {
                ((List<Object>) currentValue).add(normalizedParameterValue);
            }

            return;

        }

        // Parameter does not exist, so its new and we might need to add the original name in order to find it later
        parameters.put(parameterName, normalizedParameterValue);

    }


    private static Object normalizeParameterValue(final Object parameterValue) {
        // After calling this, all parameter values that are either arrays or iterables (e.g. collections) will
        // be converted to a mutable ArrayList. All parameter values that are neither arrays nor iterables will
        // be left unchanged. That should allow us save a lot of arraylists for single-valued parameters (which
        // are the vast majority).

        if (parameterValue == null) {
            return null;
        }


        if (parameterValue instanceof Iterable<?>) {

            if (parameterValue instanceof List<?>) {
                // faster than iterating as a generic Iterable<?>
                return new ArrayList<Object>((List<?>) parameterValue);
            }
            if (parameterValue instanceof Set<?>) {
                // faster than iterating as a generic Iterable<?>
                return new ArrayList<Object>((Set<?>)parameterValue);
            }

            final List<Object> result = new ArrayList<Object>(4);
            for (final Object obj : (Iterable<?>) parameterValue) {
                result.add(obj);
            }
            return result;

        }

        if (parameterValue.getClass().isArray()){

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

        }

        // Just return the parameter value object - no list wrapper to be built
        return parameterValue;

    }


}

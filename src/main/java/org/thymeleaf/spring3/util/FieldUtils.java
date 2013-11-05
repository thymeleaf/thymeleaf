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
package org.thymeleaf.spring3.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.SelectionVariableExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Tobias Gafner
 * 
 * @since 1.0
 *
 */
public final class FieldUtils {

    public static final String ALL_FIELDS = "*";
    public static final String GLOBAL_EXPRESSION = "global";
    public static final String ALL_EXPRESSION = "all";
    
    

    public static boolean hasErrors(final Arguments arguments, final String field) {
        return hasErrors(arguments.getConfiguration(), arguments, field);
    }
    
    /**
     * @since 2.1.0 
     */
    public static boolean hasAnyErrors(final Arguments arguments) {
        return hasAnyErrors(arguments.getConfiguration(), arguments);
    }
    
    /**
     * @since 2.1.0 
     */
    public static boolean hasGlobalErrors(final Arguments arguments) {
        return hasGlobalErrors(arguments.getConfiguration(), arguments);
    }
    
    public static boolean hasErrors(final Configuration configuration, 
            final IProcessingContext processingContext, final String field) {

	
        return checkErrors(configuration, processingContext, convertToFieldExpression(field));
        
    }
    
    public static boolean hasAnyErrors(final Configuration configuration, 
            final IProcessingContext processingContext) {

        return checkErrors(configuration, processingContext, ALL_EXPRESSION);
        
    }
    
    public static boolean hasGlobalErrors(final Configuration configuration, 
            final IProcessingContext processingContext) {
        
        return checkErrors(configuration, processingContext, GLOBAL_EXPRESSION);
        
    }
    
    

    public static List<String> errors(final Arguments arguments, final String field) {
        return computeErrors(arguments.getConfiguration(), arguments, field);
    }

    public static List<String> errors(final Configuration configuration,
            final IProcessingContext processingContext, final String field) {
        return computeErrors(configuration, processingContext, convertToFieldExpression(field));
    }
    
    public static List<String> errors(final Configuration configuration,
            final IProcessingContext processingContext) {
        return computeErrors(configuration, processingContext, ALL_EXPRESSION);
    }
    
    public static List<String> globalErrors(final Configuration configuration,
            final IProcessingContext processingContext) {

        return computeErrors(configuration, processingContext, GLOBAL_EXPRESSION);
    }
    
    private static List<String> computeErrors(final Configuration configuration,
            final IProcessingContext processingContext, final String fieldExpression) {

        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(configuration, processingContext, fieldExpression);

        final String[] errorCodes = bindStatus.getErrorMessages();
        return Arrays.asList(errorCodes);
        
    }


    public static String idFromName(final String fieldName) {
        return StringUtils.deleteAny(fieldName, "[]");
    }
    
    
    
    private static String convertToFieldExpression(final String field) {
        if (field == null) {
            return null;
        }
        if (field.trim().startsWith("*") || field.trim().startsWith("$")) {
            return field;
        }
        final StringBuilder strBuilder = new StringBuilder(20);
        strBuilder.append('*');
        strBuilder.append('{');
        strBuilder.append(field);
        strBuilder.append('}');
        return strBuilder.toString();
    }



    private static boolean checkErrors(final Configuration configuration, 
            final IProcessingContext processingContext, final String expression) {
        final BindStatus bindStatus =
                FieldUtils.getBindStatus(configuration, processingContext, expression);
        return bindStatus.isError();
    }


    /**
     * @deprecated Deprecated in 2.1.0.
     *             Use {@link #getBindStatusFromParsedExpression(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, boolean, String)}
     *             or {@link #getBindStatus(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String)}
     *             instead. Will be removed in 3.0.
     */
    @Deprecated
    public static BindStatus getBindStatus(
            final Arguments arguments, final String expression, final boolean allowAllFields) {
        return getBindStatus(arguments.getConfiguration(), arguments, expression);
    }


    /**
     * @deprecated Deprecated in 2.1.0.
     *             Use {@link #getBindStatusFromParsedExpression(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, boolean, String)}
     *             or {@link #getBindStatus(org.thymeleaf.Configuration, org.thymeleaf.context.IProcessingContext, String)}
     *             instead. Will be removed in 3.0.
     */
    @Deprecated
    public static BindStatus getBindStatus(final Configuration configuration,
            final IProcessingContext processingContext, final String expression, final boolean allowAllFields) {
        return getBindStatus(configuration, processingContext, expression);
    }



    public static BindStatus getBindStatus(final Configuration configuration,
           final IProcessingContext processingContext, final String expression) {
        return getBindStatus(configuration, processingContext, false, expression);
    }


    /**
     * @since 2.1.1
     */
    public static BindStatus getBindStatus(final Configuration configuration,
            final IProcessingContext processingContext, final boolean optional, final String expression) {

        Validate.notNull(expression, "Expression cannot be null");

        if (GLOBAL_EXPRESSION.equals(expression) || ALL_EXPRESSION.equals(expression) || ALL_FIELDS.equals(expression)) {
            // If "global", "all" or "*" are used without prefix, they must be inside a form, so we add *{...}
            return getBindStatus(configuration, processingContext, "*{" + expression + "}");
        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final IStandardExpression expressionObj =
                expressionParser.parseExpression(configuration, processingContext, expression);

        if (expressionObj == null) {
            throw new TemplateProcessingException(
                    "Expression \"" + expression + "\" is not valid: cannot perform Spring bind");
        }

        if (expressionObj instanceof SelectionVariableExpression) {
            final String bindExpression = ((SelectionVariableExpression)expressionObj).getExpression();
            return getBindStatusFromParsedExpression(configuration, processingContext, optional, true, bindExpression);
        }

        if (expressionObj instanceof VariableExpression) {
            final String bindExpression = ((VariableExpression)expressionObj).getExpression();
            return getBindStatusFromParsedExpression(configuration, processingContext, optional, false, bindExpression);
        }

        throw new TemplateProcessingException(
                "Expression \"" + expression + "\" is not valid: only variable expressions ${...} or " +
                "selection expressions *{...} are allowed in Spring field bindings");

    }



    /**
     * @since 2.1.0
     */
    public static BindStatus getBindStatusFromParsedExpression(
            final Configuration configuration, final IProcessingContext processingContext,
            final boolean useSelectionAsRoot, final String expression) {

        return getBindStatusFromParsedExpression(
                configuration, processingContext, false, useSelectionAsRoot, expression);

    }



    /**
     * @since 2.1.1
     */
    public static BindStatus getBindStatusFromParsedExpression(
            final Configuration configuration, final IProcessingContext processingContext,
            final boolean optional, final boolean useSelectionAsRoot, final String expression) {

        /*
         * This version of the getBindStatus method should only be called after parsing, and therefore the
         * passed expression must be a fragment of the already parsed expression. Note this is important because
         * this method performs no preprocessing on the expression!
         */

        // This method will return null if no binding is found and optional == true

        final RequestContext requestContext =
                (RequestContext) processingContext.getContext().getVariables().get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return null;
        }

        final String completeExpression =
                FieldUtils.validateAndGetValueExpression(processingContext, useSelectionAsRoot, expression);

        if (completeExpression == null) {
            return null;
        }


        if (!optional) {
            return new BindStatus(requestContext, completeExpression, false);
        }


        if (isBound(requestContext, expression, completeExpression)) {
            // Creating an instance of BindStatus for an unbound object results in an exception,
            // so we avoid it by checking first.
            return new BindStatus(requestContext, completeExpression, false);
        }

        return null;

    }

    
    
    
    private static String validateAndGetValueExpression(
            final IProcessingContext processingContext, final boolean useSelectionAsRoot, final String expression) {

        /*
         * Only asterisk syntax (selection variable expressions) are allowed here.
         */
        
        if (useSelectionAsRoot) {

            VariableExpression boundObjectValue =
                    (VariableExpression) processingContext.getLocalVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);
            if (boundObjectValue == null) {
                // Try the deprecated name, to avoid legacy issues
                boundObjectValue =
                        (VariableExpression) processingContext.getLocalVariable(SpringContextVariableNames.SPRING_FORM_COMMAND_VALUE);
            }

            final String boundObjectExpression =
                    (boundObjectValue == null? null : boundObjectValue.getExpression());

            if (GLOBAL_EXPRESSION.equals(expression)) {
                // Should return null if no object previously bound: nothing to apply 'global' on!
                if (boundObjectExpression == null) {
                    return null;
                }
                return boundObjectExpression;
            }
            if (ALL_EXPRESSION.equals(expression) || ALL_FIELDS.equals(expression)) {
                // Should return null if no object previously bound: nothing to apply '*' on!
                if (boundObjectExpression == null) {
                    return null;
                }
                return boundObjectExpression + '.' + ALL_FIELDS;
            }

            if (boundObjectExpression == null) {
                return expression;
            }

            return boundObjectExpression + '.' + expression;
            
        }

        return expression;

    }




    private static boolean isBound(
            final RequestContext requestContext, final String expression, final String completeExpression) {

        final int dotPos = completeExpression.indexOf('.');
        if (dotPos == -1) { // Spring only allows second-level binding for conversions! ("x.y", not "x")
            return false;
        }

        // The bound bean name is everything before the first dot (or everything, if no dot present)
        final String beanName = completeExpression.substring(0, dotPos);

        // The getErrors() method is not extremely efficient, but it has a cache map, so it should be fine
        return (requestContext.getErrors(beanName, false) != null);

    }



    
    private FieldUtils() {
	    super();
    }

	
}

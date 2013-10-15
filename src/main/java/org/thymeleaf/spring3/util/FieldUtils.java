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

	
        return checkErrors(configuration, processingContext, convertToFieldExpression(field), true);
        
    }
    
    public static boolean hasAnyErrors(final Configuration configuration, 
            final IProcessingContext processingContext) {

        return checkErrors(configuration, processingContext, ALL_EXPRESSION, true);
        
    }
    
    public static boolean hasGlobalErrors(final Configuration configuration, 
            final IProcessingContext processingContext) {
        
        return checkErrors(configuration, processingContext, GLOBAL_EXPRESSION, true);
        
    }
    
    

    public static List<String> errors(final Arguments arguments, final String field) {
        return errors(arguments.getConfiguration(), arguments, field);
    }

    public static List<String> errors(final Configuration configuration,
            final IProcessingContext processingContext, final String field) {

        return errors(configuration, processingContext, convertToFieldExpression(field), true);
    }
    
    public static List<String> errors(final Configuration configuration,
            final IProcessingContext processingContext) {
        return errors(configuration, processingContext, ALL_EXPRESSION, true);
        
    }
    
    public static List<String> globalErrors(final Configuration configuration,
            final IProcessingContext processingContext) {

        return errors(configuration, processingContext, GLOBAL_EXPRESSION, true);
    }
    
    private static List<String> errors(final Configuration configuration, 
            final IProcessingContext processingContext, final String fieldExpression, final boolean allowAllFields) {

        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(configuration, processingContext, fieldExpression, true);

        if (bindStatus == null) {
            throw new TemplateProcessingException(
                    "A BindStatus couldn't be obtained for expression '" + fieldExpression + "'. Maybe a RequestContext has " +
                            "not been registered, or there is no available binding for the object the expression references " +
                            "(e.g. if this expression selects a field inside a form, the form-backing bean might not have been " +
                            "referenced at the <form> tag with a 'th:object' attribute)");
        }

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
            final IProcessingContext processingContext, final String expression, final boolean allowAllFields) {
        final BindStatus bindStatus =
                FieldUtils.getBindStatus(configuration, processingContext, expression, allowAllFields);
        if (bindStatus == null) {
            throw new TemplateProcessingException(
                    "A BindStatus couldn't be obtained for expression '" + expression + "'. Maybe a RequestContext has " +
                    "not been registered, or there is no available binding for the object the expression references " +
                    "(e.g. if this expression selects a field inside a form, the form-backing bean might not have been " +
                    "referenced at the <form> tag with a 'th:object' attribute)");
        }
        return bindStatus.isError();
    }
    


    public static BindStatus getBindStatus(
            final Arguments arguments, final String expression, final boolean allowAllFields) {
        return getBindStatus(arguments.getConfiguration(), arguments, expression, allowAllFields);
    }
    
    
    public static BindStatus getBindStatus(final Configuration configuration, 
            final IProcessingContext processingContext, final String expression, final boolean allowAllFields) {

        // This method will return null if no binding is found!

        final RequestContext requestContext =
            (RequestContext) processingContext.getContext().getVariables().get(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return null;
        }

        String bindExpression = expression;

        if(allowAllFields) {
            if (GLOBAL_EXPRESSION.equals(bindExpression) || ALL_EXPRESSION.equals(bindExpression) || ALL_FIELDS.equals(bindExpression)) {
        	    // If "global", "all" or "*" are used without prefix, they must be inside a form, so we add *{...}
                bindExpression = "*{" + bindExpression + "}";
            }
        }


        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final IStandardExpression expressionObj =
                expressionParser.parseExpression(configuration, processingContext, bindExpression);
        
        final String completeExpression = 
            FieldUtils.validateAndGetValueExpression(processingContext, expressionObj);

        if (completeExpression == null) {
            return null;
        }

        if (isBound(requestContext, completeExpression)) {
            // Creating an instance of BindStatus for an unbound object results in an exception,
            // so we avoid it by checking first.
            return new BindStatus(requestContext, completeExpression, false);
        }

        return null;

    }

    
    
    
    private static String validateAndGetValueExpression(
            final IProcessingContext processingContext, final IStandardExpression expression) {

        /*
         * Only asterisk syntax (selection variable expressions) are allowed here.
         */
        
        if (expression instanceof SelectionVariableExpression) {

            VariableExpression boundObjectValue =
                    (VariableExpression) processingContext.getLocalVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);
            if (boundObjectValue == null) {
                // Try the deprecated name, to avoid legacy issues
                boundObjectValue =
                        (VariableExpression) processingContext.getLocalVariable(SpringContextVariableNames.SPRING_FORM_COMMAND_VALUE);
            }

            final String boundObjectExpression =
                    (boundObjectValue == null? null : boundObjectValue.getExpression());
            final String selectionExpression = ((SelectionVariableExpression)expression).getExpression();

            if (GLOBAL_EXPRESSION.equals(selectionExpression)) {
                // Should return null if no object previously bound: nothing to apply 'global' on!
                if (boundObjectExpression == null) {
                    return null;
                }
                return boundObjectExpression;
            }
            if (ALL_EXPRESSION.equals(selectionExpression) || ALL_FIELDS.equals(selectionExpression)) {
                // Should return null if no object previously bound: nothing to apply '*' on!
                if (boundObjectExpression == null) {
                    return null;
                }
                return boundObjectExpression + '.' + ALL_FIELDS;
            }

            if (boundObjectExpression == null) {
                return selectionExpression;
            }

            return boundObjectExpression + '.' + selectionExpression;
            
        } else if (expression instanceof VariableExpression) {

            return ((VariableExpression)expression).getExpression();

        }
        
        throw new TemplateProcessingException(
                "Expression \"" + expression + "\" is not valid: only selection variable expressions " +
                "*{...} are allowed in field specifications");
        
    }




    private static boolean isBound(final RequestContext requestContext, final String expression) {

        int dotPos = expression.indexOf('.');
        // The bound bean name is everything befo: re the first dot (or everything, if no dot present)
        final String beanName =
                (dotPos == -1? expression : expression.substring(0, dotPos));

        // The getErrors() method is not extremely efficient, but it has a cache map, so it should be fine
        return (requestContext.getErrors(beanName, false) != null);

    }



    
    private FieldUtils() {
	    super();
    }

	
}

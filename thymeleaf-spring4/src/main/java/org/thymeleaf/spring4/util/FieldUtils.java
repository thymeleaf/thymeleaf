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
package org.thymeleaf.spring4.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
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
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public final class FieldUtils {

    public static final String ALL_FIELDS = "*";
    public static final String GLOBAL_EXPRESSION = "global";
    public static final String ALL_EXPRESSION = "all";



    public static boolean hasErrors(final IProcessingContext processingContext, final String field) {
        return checkErrors(processingContext, convertToFieldExpression(field));
    }

    public static boolean hasAnyErrors(final IProcessingContext processingContext) {
        return checkErrors(processingContext, ALL_EXPRESSION);
    }

    public static boolean hasGlobalErrors(final IProcessingContext processingContext) {
        return checkErrors(processingContext, GLOBAL_EXPRESSION);
    }



    public static List<String> errors(final IProcessingContext processingContext, final String field) {
        return computeErrors(processingContext, convertToFieldExpression(field));
    }

    public static List<String> errors(final IProcessingContext processingContext) {
        return computeErrors(processingContext, ALL_EXPRESSION);
    }

    public static List<String> globalErrors(final IProcessingContext processingContext) {
        return computeErrors(processingContext, GLOBAL_EXPRESSION);
    }

    private static List<String> computeErrors(final IProcessingContext processingContext, final String fieldExpression) {

        final BindStatus bindStatus = FieldUtils.getBindStatus(processingContext, fieldExpression);
        if (bindStatus == null) {
            return Collections.EMPTY_LIST;
        }

        final String[] errorCodes = bindStatus.getErrorMessages();
        if (errorCodes == null || errorCodes.length == 0) {
            // If we don't need a new object, we avoid creating it
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(errorCodes);

    }


    public static List<DetailedError> detailedErrors(final IProcessingContext processingContext) {
        return computeDetailedErrors(processingContext, ALL_EXPRESSION, true, true);
    }


    private static List<DetailedError> computeDetailedErrors(
            final IProcessingContext processingContext, final String fieldExpression,
            final boolean includeGlobalErrors, final boolean includeFieldErrors) {

        final BindStatus bindStatus =
                FieldUtils.getBindStatus(processingContext, fieldExpression);
        if (bindStatus == null) {
            return Collections.EMPTY_LIST;
        }

        final Errors errors = bindStatus.getErrors();
        if (errors == null) {
            return Collections.EMPTY_LIST;
        }

        final RequestContext requestContext =
                (RequestContext) processingContext.getVariables().getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
        if (requestContext == null) {
            return Collections.EMPTY_LIST;
        }

        // We will try to avoid creating the List if we don't need it
        List<DetailedError> errorObjects = null;

        if (includeGlobalErrors) {
            final List<ObjectError> globalErrors = errors.getGlobalErrors();
            for (final ObjectError globalError : globalErrors) {
                final String message = requestContext.getMessage(globalError, false);
                final DetailedError errorObject =
                        new DetailedError(globalError.getCode(), globalError.getArguments(), message);
                if (errorObjects == null) {
                    errorObjects = new ArrayList<DetailedError>(errors.getErrorCount() + 2);
                }
                errorObjects.add(errorObject);
            }
        }

        if (includeFieldErrors) {
            final List<FieldError> fieldErrors = errors.getFieldErrors();
            for (final FieldError fieldError : fieldErrors) {
                final String message = requestContext.getMessage(fieldError, false);
                final DetailedError errorObject =
                        new DetailedError(fieldError.getField(), fieldError.getCode(), fieldError.getArguments(), message);
                if (errorObjects == null) {
                    errorObjects = new ArrayList<DetailedError>(errors.getErrorCount() + 2);
                }
                errorObjects.add(errorObject);
            }
        }

        if (errorObjects == null) {
            return Collections.EMPTY_LIST;
        }
        return errorObjects;

    }



    public static String idFromName(final String fieldName) {
        return StringUtils.deleteAny(fieldName, "[]");
    }



    private static String convertToFieldExpression(final String field) {
        if (field == null) {
            return null;
        }
        final String trimmedField = field.trim();
        if (trimmedField.length() == 0) {
            return null;
        }
        final char firstc = trimmedField.charAt(0);
        if (firstc == '*' || firstc == '$') {
            return field;
        }
        return "*{" + field + "}";
    }



    private static boolean checkErrors(final IProcessingContext processingContext, final String expression) {
        final BindStatus bindStatus = FieldUtils.getBindStatus(processingContext, expression);
        return bindStatus.isError();
    }




    public static BindStatus getBindStatus(final IProcessingContext processingContext, final String expression) {
        return getBindStatus(processingContext, false, expression);
    }


    public static BindStatus getBindStatus(
            final IProcessingContext processingContext, final boolean optional, final String expression) {

        Validate.notNull(expression, "Expression cannot be null");

        if (GLOBAL_EXPRESSION.equals(expression) || ALL_EXPRESSION.equals(expression) || ALL_FIELDS.equals(expression)) {
            // If "global", "all" or "*" are used without prefix, they must be inside a form, so we add *{...}
            final String completeExpression = "*{" + expression + "}";
            return getBindStatus(processingContext, optional, completeExpression);
        }

        final IEngineConfiguration configuration = processingContext.getConfiguration();

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final IStandardExpression expressionObj = expressionParser.parseExpression(processingContext, expression);

        if (expressionObj == null) {
            throw new TemplateProcessingException(
                    "Expression \"" + expression + "\" is not valid: cannot perform Spring bind");
        }

        if (expressionObj instanceof SelectionVariableExpression) {
            final String bindExpression = ((SelectionVariableExpression)expressionObj).getExpression();
            return getBindStatusFromParsedExpression(processingContext, optional, true, bindExpression);
        }

        if (expressionObj instanceof VariableExpression) {
            final String bindExpression = ((VariableExpression)expressionObj).getExpression();
            return getBindStatusFromParsedExpression(processingContext, optional, false, bindExpression);
        }

        throw new TemplateProcessingException(
                "Expression \"" + expression + "\" is not valid: only variable expressions ${...} or " +
                "selection expressions *{...} are allowed in Spring field bindings");

    }



    public static BindStatus getBindStatusFromParsedExpression(
            final IProcessingContext processingContext,
            final boolean useSelectionAsRoot, final String expression) {

        return getBindStatusFromParsedExpression(
                processingContext, false, useSelectionAsRoot, expression);

    }



    public static BindStatus getBindStatusFromParsedExpression(
            final IProcessingContext processingContext,
            final boolean optional, final boolean useSelectionAsRoot, final String expression) {

        /*
         * This version of the getBindStatus method should only be called after parsing, and therefore the
         * passed expression must be a fragment of the already parsed expression. Note this is important because
         * this method performs no preprocessing on the expression!
         */

        // This method will return null if no binding is found and optional == true

        final RequestContext requestContext =
                (RequestContext) processingContext.getVariables().getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
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


        if (isBound(requestContext, completeExpression)) {
            // Creating an instance of BindStatus for an unbound object results in an (expensive) exception,
            // so we avoid it by checking first. Because the check is a simplification, we still handle the exception.
            try {
                return new BindStatus(requestContext, completeExpression, false);
            } catch (final NotReadablePropertyException ignored) {
                return null;
            }
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
                    (VariableExpression) processingContext.getVariables().getVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);

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
                return boundObjectExpression + "." + ALL_FIELDS;
            }

            if (boundObjectExpression == null) {
                return expression;
            }

            return boundObjectExpression + "." + expression;

        }

        return expression;

    }




    private static boolean isBound(final RequestContext requestContext, final String completeExpression) {

        final int dotPos = completeExpression.indexOf('.');
        if (dotPos == -1) { // Spring only allows second-level binding for conversions! ("x.y", not "x")
            return false;
        }

        // The bound bean name is everything before the first dot (or everything, if no dot present)
        final String beanName = completeExpression.substring(0, dotPos);

        // The getErrors() method is not extremely efficient, but it has a cache map, so it should be fine
        final boolean beanValid = requestContext.getErrors(beanName, false) != null;
        if (beanValid && completeExpression.length() > dotPos) {
            final String path = completeExpression.substring(dotPos + 1, completeExpression.length() - 1);
            // We will validate the rest of the expression as a bean property identifier or a bean property expression.
            return validateBeanPath(path);
        }
        return false;

    }


    /*
     * This method determines whether a fragment of a bean path is a valid bean property identifier
     * or bean property expression.
     */
    private static boolean validateBeanPath(final CharSequence path) {
        final int pathLen = path.length();
        for (int charPos = 0; charPos < pathLen; charPos++) {
            final char c = path.charAt(charPos);
            if (!Character.isJavaIdentifierPart(c) || c == '.') {
                return false;
            }
        }
        return true;
    }




    private FieldUtils() {
        super();
    }

}

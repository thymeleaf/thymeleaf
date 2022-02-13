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
package org.thymeleaf.spring5.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.SelectionVariableExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Static utility class containing methods for dealing with form fields in Spring-enabled environments.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @author Tobias Gafner
 *
 * @since 3.0.3
 *
 */
public final class FieldUtils {

    public static final String ALL_FIELDS = "*";
    public static final String GLOBAL_EXPRESSION = "global";
    public static final String ALL_EXPRESSION = "all";



    public static boolean hasErrors(final IExpressionContext context, final String field) {
        return checkErrors(context, convertToFieldExpression(field));
    }

    public static boolean hasAnyErrors(final IExpressionContext context) {
        return checkErrors(context, ALL_EXPRESSION);
    }

    public static boolean hasGlobalErrors(final IExpressionContext context) {
        return checkErrors(context, GLOBAL_EXPRESSION);
    }



    public static List<String> errors(final IExpressionContext context, final String field) {
        return computeErrors(context, convertToFieldExpression(field));
    }

    public static List<String> errors(final IExpressionContext context) {
        return computeErrors(context, ALL_EXPRESSION);
    }

    public static List<String> globalErrors(final IExpressionContext context) {
        return computeErrors(context, GLOBAL_EXPRESSION);
    }

    private static List<String> computeErrors(final IExpressionContext context, final String fieldExpression) {

        final IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, fieldExpression);
        if (bindStatus == null) {
            return Collections.EMPTY_LIST;
        }

        final String[] errorMessages = bindStatus.getErrorMessages();
        if (errorMessages == null || errorMessages.length == 0) {
            // If we don't need a new object, we avoid creating it
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(errorMessages);

    }




    public static List<DetailedError> detailedErrors(final IExpressionContext context) {
        return computeDetailedErrors(context, ALL_EXPRESSION);
    }


    public static List<DetailedError> detailedErrors(final IExpressionContext context, final String field) {
        return computeDetailedErrors(context, convertToFieldExpression(field));
    }

    public static List<DetailedError> globalDetailedErrors(final IExpressionContext context) {
        return computeDetailedErrors(context, GLOBAL_EXPRESSION);
    }


    private static List<DetailedError> computeDetailedErrors(
            final IExpressionContext context, final String fieldExpression) {

        final IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, fieldExpression);
        if (bindStatus == null) {
            return Collections.EMPTY_LIST;
        }

        final Errors errors = bindStatus.getErrors();
        if (errors == null) {
            return Collections.EMPTY_LIST;
        }

        final IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
        if (requestContext == null) {
            return Collections.EMPTY_LIST;
        }

        // We will try to avoid creating the List if we don't need it
        List<DetailedError> errorObjects = null;

        final String bindExpression = bindStatus.getExpression();

        if (bindExpression == null || ALL_EXPRESSION.equals(bindExpression) || ALL_FIELDS.equals(bindExpression)) {
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

        if (bindExpression != null) {
            final List<FieldError> fieldErrors = errors.getFieldErrors(bindStatus.getExpression());
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



    private static boolean checkErrors(
            final IExpressionContext context, final String expression) {
        final IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, expression);
        if (bindStatus == null) {
            throw new TemplateProcessingException(
                    "Could not bind form errors using expression \"" + expression + "\". Please check this " +
                    "expression is being executed inside the adequate context (e.g. a <form> with a th:object " +
                    "attribute)");
        }
        return bindStatus.isError();
    }




    public static IThymeleafBindStatus getBindStatus(
            final IExpressionContext context, final String expression) {
        return getBindStatus(context, false, expression);
    }


    public static IThymeleafBindStatus getBindStatus(
            final IExpressionContext context,
            final boolean optional, final String expression) {

        Validate.notNull(expression, "Expression cannot be null");

        if (GLOBAL_EXPRESSION.equals(expression) || ALL_EXPRESSION.equals(expression) || ALL_FIELDS.equals(expression)) {
            // If "global", "all" or "*" are used without prefix, they must be inside a form, so we add *{...}
            final String completeExpression = "*{" + expression + "}";
            return getBindStatus(context, optional, completeExpression);
        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        final IStandardExpression expressionObj = expressionParser.parseExpression(context, expression);

        if (expressionObj == null) {
            throw new TemplateProcessingException(
                    "Expression \"" + expression + "\" is not valid: cannot perform Spring bind");
        }

        if (expressionObj instanceof SelectionVariableExpression) {
            final String bindExpression = ((SelectionVariableExpression)expressionObj).getExpression();
            return getBindStatusFromParsedExpression(context, optional, true, bindExpression);
        }

        if (expressionObj instanceof VariableExpression) {
            final String bindExpression = ((VariableExpression)expressionObj).getExpression();
            return getBindStatusFromParsedExpression(context, optional, false, bindExpression);
        }

        throw new TemplateProcessingException(
                "Expression \"" + expression + "\" is not valid: only variable expressions ${...} or " +
                "selection expressions *{...} are allowed in Spring field bindings");

    }



    public static IThymeleafBindStatus getBindStatusFromParsedExpression(
            final IExpressionContext context,
            final boolean useSelectionAsRoot, final String expression) {

        return getBindStatusFromParsedExpression(
                context, false, useSelectionAsRoot, expression);

    }



    public static IThymeleafBindStatus getBindStatusFromParsedExpression(
            final IExpressionContext context,
            final boolean optional, final boolean useSelectionAsRoot, final String expression) {

        /*
         * This version of the getBindStatus method should only be called after parsing, and therefore the
         * passed expression must be a fragment of the already parsed expression. Note this is important because
         * this method performs no preprocessing on the expression!
         */

        // This method will return null if no binding is found and optional == true

        final IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
        if (requestContext == null) {
            return null;
        }

        final String completeExpression =
                FieldUtils.validateAndGetValueExpression(context, useSelectionAsRoot, expression);

        if (completeExpression == null) {
            return null;
        }


        if (!optional) {
            return requestContext.getBindStatus(completeExpression, false);
        }


        if (isBound(requestContext, completeExpression)) {
            // Creating an instance of BindStatus for an unbound object results in an (expensive) exception,
            // so we avoid it by checking first. Because the check is a simplification, we still handle the exception.
            try {
                return requestContext.getBindStatus(completeExpression, false);
            } catch (final NotReadablePropertyException ignored) {
                return null;
            }
        }

        return null;

    }




    private static String validateAndGetValueExpression(
            final IExpressionContext context, final boolean useSelectionAsRoot, final String expression) {

        /*
         * Only asterisk syntax (selection variable expressions) are allowed here.
         */

        if (useSelectionAsRoot) {

            VariableExpression boundObjectValue =
                    (VariableExpression) context.getVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION);

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




    private static boolean isBound(final IThymeleafRequestContext requestContext, final String completeExpression) {

        final int dotPos = completeExpression.indexOf('.');
        if (dotPos == -1) { // Spring only allows second-level binding for conversions! ("x.y", not "x")
            return false;
        }

        // The bound bean name is everything before the first dot (or everything, if no dot present)
        final String beanName = completeExpression.substring(0, dotPos);

        // The getErrors() method is not extremely efficient, but it has a cache map, so it should be fine
        final boolean beanValid = requestContext.getErrors(beanName, false).isPresent();
        if (beanValid && completeExpression.length() > dotPos) {
            final String path = completeExpression.substring(dotPos + 1, completeExpression.length());
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
        boolean inKey = false;
        for (int charPos = 0; charPos < pathLen; charPos++) {
            final char c = path.charAt(charPos);
            if (!inKey && c == PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR) {
                inKey = true;
            }
            else if (inKey && c == PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR) {
                inKey = false;
            }
            else if (!inKey && !Character.isJavaIdentifierPart(c) && c != '.') {
                return false;
            }
        }
        return true;
    }




    private FieldUtils() {
        super();
    }

}

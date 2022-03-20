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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.ArrayPropertyAccessor;
import ognl.EnumerationPropertyAccessor;
import ognl.IteratorPropertyAccessor;
import ognl.ListPropertyAccessor;
import ognl.MapPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.SetPropertyAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IContext;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class OGNLShortcutExpression {

    private static final Logger LOGGER = LoggerFactory.getLogger(OGNLShortcutExpression.class);

    private static final String EXPRESSION_CACHE_TYPE_OGNL_SHORTCUT = "ognlsc";
    private static final Object[] NO_PARAMS = new Object[0];

    private final String[] expressionLevels;


    OGNLShortcutExpression(final String[] expressionLevels) {
        super();
        this.expressionLevels = expressionLevels;
    }


    Object evaluate(
            final IEngineConfiguration configuration, final Map<String, Object> context, final Object root)
            throws Exception {

        final ICacheManager cacheManager = configuration.getCacheManager();
        final ICache<ExpressionCacheKey, Object> expressionCache = (cacheManager == null? null : cacheManager.getExpressionCache());

        Object target = root;
        for (final String propertyName : this.expressionLevels) {

            // If target is null, we will mimic what OGNL does in these cases...
            if (target == null) {
                throw new OgnlException("source is null for getProperty(null, \"" + propertyName + "\")");
            }

            // For the best integration possible, we will ask OGNL which property accessor it would use for
            // this target object, and then depending on the result apply our equivalent or just default to
            // OGNL evaluation if it is a custom property accessor we do not implement.
            final Class<?> targetClass = OgnlRuntime.getTargetClass(target);
            final PropertyAccessor ognlPropertyAccessor = OgnlRuntime.getPropertyAccessor(targetClass);

            // Depending on the returned OGNL property accessor, we will try to apply ours
            if (target instanceof Class<?>) {

                // Because of the way OGNL works, the "OgnlRuntime.getTargetClass(...)" of a Class object is the class
                // object itself, so we might be trying to apply a PropertyAccessor to a Class instead of a real object,
                // something we avoid by means of this shortcut
                target = getObjectProperty(expressionCache, propertyName, target);

            } else if (OGNLContextPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getContextProperty(propertyName, context, target);

            } else if (ObjectPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getObjectProperty(expressionCache, propertyName, target);

            } else if (MapPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getMapProperty(propertyName, (Map<?, ?>) target);

            } else if (ListPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getListProperty(expressionCache, propertyName, (List<?>) target);

            } else if (SetPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getSetProperty(expressionCache, propertyName, (Set<?>) target);

            } else if (IteratorPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getIteratorProperty(expressionCache, propertyName, (Iterator<?>) target);

            } else if (EnumerationPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getEnumerationProperty(expressionCache, propertyName, (Enumeration<?>) target);

            } else if (ArrayPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {

                target = getArrayProperty(expressionCache, propertyName, (Object[]) target);

            } else {
                // OGNL would like to apply a different property accessor (probably a custom one we do not know). In
                // these cases, we must signal the problem with this exception and let the expression evaluator
                // default to normal OGNL evaluation.
                throw new OGNLShortcutExpressionNotApplicableException();
            }

        }

        return target;

    }






    private static Object getContextProperty(
            final String propertyName, final Map<String, Object> context, final Object target)
            throws OgnlException {

        if (OGNLContextPropertyAccessor.REQUEST_PARAMETERS_RESTRICTED_VARIABLE_NAME.equals(propertyName) &&
                context != null && context.containsKey(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS)) {
            throw new OgnlException(
                    "Access to variable \"" + propertyName + "\" is forbidden in this context. Note some restrictions apply to " +
                    "variable access. For example, accessing request parameters is forbidden in preprocessing and " +
                    "unescaped expressions, and also in fragment inclusion specifications.");
        }

        // 'execInfo' translation from context variable to expression object - deprecated and to be removed in 3.1
        if ("execInfo".equals(propertyName)) { // Quick check to avoid deprecated method call
            final Object execInfoResult = checkExecInfo(propertyName, context);
            if (execInfoResult != null) {
                return execInfoResult;
            }
        }

        return ((IContext) target).getVariable(propertyName);

    }


    /**
     * Translation from 'execInfo' context variable (${execInfo}) to 'execInfo' expression object (${#execInfo}), needed
     * since 3.0.0.
     *
     * Note this is expressed as a separate method in order to mark this as deprecated and make it easily locatable.
     *
     * @param propertyName the name of the property being accessed (we are looking for 'execInfo').
     * @param context the expression context, which should contain the expression objects.
     * @deprecated created (and deprecated) in 3.0.0 in order to support automatic conversion of calls to the 'execInfo'
     *             context variable (${execInfo}) into the 'execInfo' expression object (${#execInfo}), which is its
     *             new only valid form. This method, along with the infrastructure for execInfo conversion in
     *             StandardExpressionUtils#mightNeedExpressionObjects(...) will be removed in 3.1.
     */
    @Deprecated
    private static Object checkExecInfo(final String propertyName, final Map<String,Object> context) {
        if ("execInfo".equals(propertyName)) {
            LOGGER.warn(
                    "[THYMELEAF][{}] Found Thymeleaf Standard Expression containing a call to the context variable " +
                    "\"execInfo\" (e.g. \"${execInfo.templateName}\"), which has been deprecated. The " +
                    "Execution Info should be now accessed as an expression object instead " +
                    "(e.g. \"${#execInfo.templateName}\"). Deprecated use is still allowed, but will be removed " +
                    "in future versions of Thymeleaf.",
                    TemplateEngine.threadIndex());
            return context.get("execInfo");
        }
        return null;
    }



    private static Object getObjectProperty(
            final ICache<ExpressionCacheKey,Object> expressionCache, final String propertyName, final Object target) {

        final Class<?> currClass = OgnlRuntime.getTargetClass(target);
        final ExpressionCacheKey cacheKey = computeMethodCacheKey(currClass, propertyName);

        Method readMethod = null;

        if (expressionCache != null) {
            readMethod = (Method) expressionCache.get(cacheKey);
        }

        if (readMethod == null) {

            final BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(currClass);
            } catch (final IntrospectionException e) {
                // Something went wrong during introspection - wash hands, just let OGNL decide what to do
                throw new OGNLShortcutExpressionNotApplicableException();
            }

            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            if (propertyDescriptors != null) {
                for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor.getName().equals(propertyName)) {
                        readMethod = propertyDescriptor.getReadMethod();
                        if (readMethod != null && expressionCache != null) {
                            expressionCache.put(cacheKey, readMethod);
                        }
                        break;
                    }
                }
            }

        }

        if (readMethod == null) {
            // The property name does not match any getter methods - better let OGNL decide what to do
            throw new OGNLShortcutExpressionNotApplicableException();
        }

        try {
            return readMethod.invoke(target, NO_PARAMS);
        } catch (final IllegalAccessException e) {
            // Oops! we better let OGNL take care of this its own way...
            throw new OGNLShortcutExpressionNotApplicableException();
        } catch (final InvocationTargetException e) {
            // Oops! we better let OGNL take care of this its own way...
            throw new OGNLShortcutExpressionNotApplicableException();
        }

    }




    private static Object getMapProperty(final String propertyName, final Map<?,?> map) {

        /*
         * This method will try to mimic the behaviour of the ognl.MapPropertyAccessor class, with the exception
         * that OGNLShortcutExpressions do not process indexed map access (map['key']), only normal property map
         * access (map.key), so indexed access will not be taken into account in this accessor method.
         *
         * The main reason for not implementing support for indexed map access in OGNLShortcutExpression is that
         * in an indexed access expression in OGNL a variable could be used as index instead of a literal
         * (note that this is not allowed in SpringEL, but it is in OGNL), and resolving such index variable or more
         * complex expression would add quite a lot of complexity to this supposedly-simple mechanism. So in those
         * cases, it is just better to allow OGNL to do its job.
         */

        if (propertyName.equals("size")) {
            return Integer.valueOf(map.size());
        }
        if (propertyName.equals("keys") || propertyName.equals("keySet")) {
            return map.keySet();
        }
        if (propertyName.equals("values")) {
            return map.values();
        }
        if (propertyName.equals("isEmpty")) {
            return map.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        }
        return map.get(propertyName);

    }



    public static Object getListProperty(
            final ICache<ExpressionCacheKey,Object> expressionCache, final String propertyName, final List<?> list) {

        /*
         * This method will try to mimic the behaviour of the ognl.ListPropertyAccessor class, with the exception
         * that OGNLShortcutExpressions do not process indexed list access (list[3]), only access to the properties
         * of the list object like 'size', 'iterator', etc.
         *
         * The main reason for not implementing support for indexed list access in OGNLShortcutExpression is similar
         * to that of indexed map access (with the difference that typical literal-based indexed access to lists
         * is based on numeric literals instead of text literals).
         */

        if (propertyName.equals("size")) {
            return Integer.valueOf(list.size());
        }
        if (propertyName.equals("iterator")) {
            return list.iterator();
        }
        if (propertyName.equals("isEmpty") || propertyName.equals("empty")) {
            return list.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        }

        // Default to treating the list object as any other object
        return getObjectProperty(expressionCache, propertyName, list);

    }



    public static Object getArrayProperty(
            final ICache<ExpressionCacheKey,Object> expressionCache, final String propertyName, final Object[] array) {

        /*
         * This method will try to mimic the behaviour of the ognl.ArrayPropertyAccessor class, with the exception
         * that OGNLShortcutExpressions do not process indexed array access (array[3]), only access to the properties
         * of the array object, namely 'length'.
         *
         * The main reason for not implementing support for indexed array access in OGNLShortcutExpression is similar
         * to that of indexed map access (with the difference that typical literal-based indexed access to arrays
         * is based on numeric literals instead of text literals).
         */

        if (propertyName.equals("length")) {
            return Integer.valueOf(Array.getLength(array));
        }

        // Default to treating the array object as any other object
        return getObjectProperty(expressionCache, propertyName, array);

    }



    public static Object getEnumerationProperty(
            final ICache<ExpressionCacheKey,Object> expressionCache, final String propertyName, final Enumeration enumeration) {

        /*
         * This method will try to mimic the behaviour of the ognl.EnumerationPropertyAccessor class, with the exception
         * that OGNLShortcutExpressions do not process indexed array access (array[3]), only access to the properties
         * of the enumeration object.
         */

        if (propertyName.equals("next") || propertyName.equals("nextElement")) {
            return enumeration.nextElement();
        }
        if (propertyName.equals("hasNext") || propertyName.equals("hasMoreElements")) {
            return enumeration.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE;
        }

        // Default to treating the enumeration object as any other object
        return getObjectProperty(expressionCache, propertyName, enumeration);

    }



    public static Object getIteratorProperty(
            final ICache<ExpressionCacheKey,Object> expressionCache, final String propertyName, final Iterator<?> iterator) {

        /*
         * This method will try to mimic the behaviour of the ognl.IteratorPropertyAccessor class, with the exception
         * that OGNLShortcutExpressions do not process indexed iterator access (array[3]), only access to the properties
         * of the iterator object.
         */

        if (propertyName.equals("next")) {
            return iterator.next();
        }
        if (propertyName.equals("hasNext")) {
            return iterator.hasNext() ? Boolean.TRUE : Boolean.FALSE;
        }

        // Default to treating the iterator object as any other object
        return getObjectProperty(expressionCache, propertyName, iterator);

    }



    public static Object getSetProperty(
            final ICache<ExpressionCacheKey,Object> expressionCache, final String propertyName, final Set<?> set) {

        /*
         * This method will try to mimic the behaviour of the ognl.IteratorPropertyAccessor class, with the exception
         * that OGNLShortcutExpressions do not process indexed iterator access (array[3]), only access to the properties
         * of the iterator object.
         */

        if (propertyName.equals("size")) {
            return Integer.valueOf(set.size());
        }
        if (propertyName.equals("iterator")) {
            return set.iterator();
        }
        if (propertyName.equals("isEmpty")) {
            return set.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        }

        // Default to treating the set object as any other object
        return getObjectProperty(expressionCache, propertyName, set);

    }






    static String[] parse(final String expression) {
        return doParseExpr(expression, 0, 0, expression.length());
    }


    private static String[] doParseExpr(final String expression, final int level, final int offset, final int len) {

        int codepoint;
        int i = offset;
        boolean firstChar = true;

        while (i < len) {

            codepoint = Character.codePointAt(expression, i);

            if (codepoint == '.') {
                break;
            }

            if (firstChar) {
                if (!Character.isJavaIdentifierStart(codepoint)) {
                    return null;
                }
                firstChar = false;
            } else {
                if (!Character.isJavaIdentifierPart(codepoint)) {
                    return null;
                }
            }

            i++;

        }

        final String[] result;
        if (i < len) {
            result = doParseExpr(expression, level + 1, i + 1, len);
            if (result == null) {
                return null;
            }
        } else {
            result = new String[level + 1];
        }

        result[level] = expression.substring(offset, i);

        if ("true".equalsIgnoreCase(result[level]) || "false".equalsIgnoreCase(result[level]) || "null".equalsIgnoreCase(result[level])) {
            return null;
        }

        return result;

    }



    private static ExpressionCacheKey computeMethodCacheKey(final Class<?> targetClass, final String propertyName) {
        return new ExpressionCacheKey(EXPRESSION_CACHE_TYPE_OGNL_SHORTCUT, targetClass.getName(), propertyName);
    }




    /*
     * This exception signals that the OGNLShortcutExpression mechanism is not applicable for the current
     * expression, and therefore the OGNLVariableExpressionEvaluator should default to standard pure-OGNL
     * evaluation.
     *
     * Most common reason for this is the existence of a custom property accessor registered in OGNL for accessing
     * the properties of one of the objects involved in the expression, which behaviour (the custom property accessor's)
     * cannot be replicated by OGNLShortcutExpressions.
     */
    static class OGNLShortcutExpressionNotApplicableException extends RuntimeException {

        OGNLShortcutExpressionNotApplicableException() {
            super();
        }

    }



}

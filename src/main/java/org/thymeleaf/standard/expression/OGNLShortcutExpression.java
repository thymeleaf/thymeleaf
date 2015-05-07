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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.OgnlRuntime;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IVariablesMap;
import org.thymeleaf.text.ITextRepository;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
final class OGNLShortcutExpression {

    private static final String OGNL_SHORTCUT_EXPRESSION_PREFIX = "ognlsc|";
    private static final Object[] NO_PARAMS = new Object[0];

    private final String[] expressionLevels;


    OGNLShortcutExpression(final String[] expressionLevels) {
        super();
        this.expressionLevels = expressionLevels;
    }


    Object evaluate(final IProcessingContext processingContext, final Object root) throws Exception {

        final IEngineConfiguration configuration = processingContext.getConfiguration();
        final ITextRepository textRepository = configuration.getTextRepository();
        final ICacheManager cacheManager = configuration.getCacheManager();
        final ICache<String, Object> expressionCache = (cacheManager == null? null : cacheManager.getExpressionCache());

        Object curr = root;
        for (final String propertyName : this.expressionLevels) {

            if (curr == null) {
                throw new NullPointerException();
            } else if (curr instanceof IVariablesMap) {
                curr = getVariablesMapProperty(propertyName, curr);
            } else if (curr instanceof Map<?,?>) {
                curr = getMapProperty(propertyName, (Map<?,?>)curr);
            } else if (curr instanceof List<?>) {
                curr = getListProperty(textRepository, expressionCache, propertyName, (List<?>)curr);
            } else if (curr instanceof Set<?>) {
                curr = getSetProperty(textRepository, expressionCache, propertyName, (Set<?>) curr);
            } else if (curr instanceof Iterator<?>) {
                curr = getIteratorProperty(textRepository, expressionCache, propertyName, (Iterator<?>) curr);
            } else if (curr instanceof Enumeration<?>) {
                curr = getEnumerationProperty(textRepository, expressionCache, propertyName, (Enumeration<?>) curr);
            } else if (curr instanceof Object[]) {
                curr = getArrayProperty(textRepository, expressionCache, propertyName, (Object[]) curr);
            } else {
                curr = getObjectProperty(textRepository, expressionCache, propertyName, curr);
            }

        }

        return curr;

    }






    private static Object getVariablesMapProperty(final String propertyName, final Object target) {
        return ((IVariablesMap) target).getVariable(propertyName);
    }



    private static Object getObjectProperty(
            final ITextRepository textRepository, final ICache<String,Object> expressionCache,
            final String propertyName, final Object target)
            throws Exception {

        final Class<?> currClass = OgnlRuntime.getTargetClass(target);
        final String cacheKey = computeMethodCacheKey(textRepository, currClass, propertyName);

        Method readMethod = null;

        if (expressionCache != null) {
            readMethod = (Method) expressionCache.get(cacheKey);
        }

        if (readMethod == null) {
            BeanInfo beanInfo = Introspector.getBeanInfo(currClass);
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getName().equals(propertyName)) {
                    readMethod = propertyDescriptor.getReadMethod();
                    if (expressionCache != null) {
                        expressionCache.put(cacheKey, readMethod);
                    }
                    break;
                }
            }
        }

        if (readMethod == null) {
            throw new IllegalArgumentException("No property \"" + propertyName + "\" in class " + currClass);
        }

        return readMethod.invoke(target, NO_PARAMS);

    }



    private static Object getMapProperty(final String propertyName, final Map<?,?> map) throws Exception {

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
            final ITextRepository textRepository, final ICache<String,Object> expressionCache,
            final String propertyName, final List<?> list)
            throws Exception {

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
        return getObjectProperty(textRepository, expressionCache, propertyName, list);

    }



    public static Object getArrayProperty(
            final ITextRepository textRepository, final ICache<String,Object> expressionCache,
            final String propertyName, final Object[] array)
            throws Exception {

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
        return getObjectProperty(textRepository, expressionCache, propertyName, array);

    }



    public static Object getEnumerationProperty(
            final ITextRepository textRepository, final ICache<String,Object> expressionCache,
            final String propertyName, final Enumeration enumeration)
            throws Exception {

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
        return getObjectProperty(textRepository, expressionCache, propertyName, enumeration);

    }



    public static Object getIteratorProperty(
            final ITextRepository textRepository, final ICache<String,Object> expressionCache,
            final String propertyName, final Iterator<?> iterator)
            throws Exception {

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
        return getObjectProperty(textRepository, expressionCache, propertyName, iterator);

    }



    public static Object getSetProperty(
            final ITextRepository textRepository, final ICache<String,Object> expressionCache,
            final String propertyName, final Set<?> set)
            throws Exception {

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
        return getObjectProperty(textRepository, expressionCache, propertyName, set);

    }






    static String[] parse(final String expression) {
        return doParseExpr(expression, 0, 0, expression.length());
    }


    private static String[] doParseExpr(final String expression, final int level, final int offset, final int len) {

        char c;
        int codepoint;
        int i = offset;
        boolean firstChar = true;

        while (i < len) {

            c = expression.charAt(i);

            if (c == '.') {
                break;
            } else if (c < Character.MIN_HIGH_SURROGATE) { // shortcut: U+D800 is the lower limit of high-surrogate chars.
                codepoint = (int) c;
            } else if (Character.isHighSurrogate(c) && i + 1 < len) { // i has already been increased
                final char c1 = expression.charAt(i + 1);
                if (Character.isLowSurrogate(c1)) {
                    codepoint = Character.toCodePoint(c, c1);
                    i++;
                } else {
                    codepoint = (int) c;
                }
            } else { // just a normal, single-char, high-valued codepoint
                codepoint = (int) c;
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

        return result;

    }



    private static String computeMethodCacheKey(
            final ITextRepository textRepository, final Class<?> targetClass, final String propertyName) {
        return textRepository.getText(OGNL_SHORTCUT_EXPRESSION_PREFIX, targetClass.getName(), propertyName);
    }





    public static void main(String[] args) throws Exception {

        final List<String> list = new ArrayList<String>();
        list.add("one value");

        final Map<String,Object> root = new HashMap<String, Object>();
        root.put("list", list);

        final One one = new One("huey!");
        root.put("salute", one);


        System.out.println(ognl.Ognl.getValue("list[0]", root));
        System.out.println(ognl.Ognl.getValue("salute.two", root));

        System.out.println(OgnlRuntime.getPropertyAccessor(Map.class));
        System.out.println(OgnlRuntime.getPropertyAccessor(List.class));
        System.out.println(OgnlRuntime.getPropertyAccessor(One.class));
        System.out.println(OgnlRuntime.getPropertyAccessor(Serializable.class));



    }


    public static class One {

        private final String two;

        public One(final String one) {
            super();
            this.two = one;
        }

        public String getTwo() {
            return this.two;
        }

    }


}

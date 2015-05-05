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
import java.lang.reflect.Method;

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

    private final String[] exprLevels;


    OGNLShortcutExpression(final String[] exprLevels) {
        super();
        this.exprLevels = exprLevels;
    }


    Object evaluate(final IProcessingContext processingContext, final Object root) throws Exception {

        final IEngineConfiguration configuration = processingContext.getConfiguration();
        final ITextRepository textRepository = configuration.getTextRepository();

        Object curr = root;
        for (final String exprLevel : this.exprLevels) {

            if (curr == null) {

                throw new NullPointerException();

            } else if (curr instanceof IVariablesMap) {

                curr = ((IVariablesMap) curr).getVariable(exprLevel);

            } else {

                final Class<?> currClass = curr.getClass();
                final String cacheKey = computeMethodCacheKey(textRepository, currClass, exprLevel);

                Method readMethod = null;
                ICache<String,Object> cache = null;

                final ICacheManager cacheManager = configuration.getCacheManager();
                if (cacheManager != null) {
                    cache = cacheManager.getExpressionCache();
                    if (cache != null) {
                        readMethod = (Method) cache.get(cacheKey);
                    }
                }

                if (readMethod == null) {
                    BeanInfo beanInfo = Introspector.getBeanInfo(currClass);
                    final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                    for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        if (propertyDescriptor.getName().equals(exprLevel)) {
                            readMethod = propertyDescriptor.getReadMethod();
                            if (cache != null) {
                                cache.put(cacheKey, readMethod);
                            }
                            break;
                        }
                    }
                }

                if (readMethod == null) {
                    throw new IllegalArgumentException("No property \"" + exprLevel + "\" in class " + currClass);
                }

                curr = readMethod.invoke(curr, NO_PARAMS);

            }

        }

        return curr;

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


}

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
package org.thymeleaf.expression;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Expression object for performing type conversion operations inside Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   {@code #conversions}.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class Conversions {

    private final IExpressionContext context;


    public Conversions(final IExpressionContext context) {
        super();
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
    }




    public Object convert(final Object target, final String className) {

        try {
            final Class<?> clazz = ClassLoaderUtils.loadClass(className);
            return convert(target, clazz);
        } catch (final ClassNotFoundException e) {
            try {
                final Class<?> clazz = ClassLoaderUtils.loadClass("java.lang." + className);
                return convert(target, clazz);
            } catch (final ClassNotFoundException ex) {
                throw new IllegalArgumentException("Cannot convert to class '" + className + "'", e);
            }
        }

    }


    public Object convert(final Object target, final Class<?> clazz) {

        final IStandardConversionService conversionService =
                StandardExpressions.getConversionService(this.context.getConfiguration());
        return conversionService.convert(this.context, target, clazz);
    }


}

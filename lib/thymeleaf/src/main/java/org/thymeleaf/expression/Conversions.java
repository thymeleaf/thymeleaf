/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.ExpressionUtils;
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
    private final boolean restricted;


    /**
     * <p>
     *   Creates a new instance to be used in normal (non-restricted) mode.
     * </p>
     *
     * @param context the expression context.
     */
    public Conversions(final IExpressionContext context) {
        this(context, false);
    }


    /**
     * <p>
     *   Creates a new instance, specifying whether it is meant to be used by expressions executed in restricted
     *   mode, in which only an allowlist of types can be used as conversion targets.
     * </p>
     *
     * @param context the expression context.
     * @param restricted whether this instance is meant for restricted-mode expressions.
     * @since 3.1.6
     */
    public Conversions(final IExpressionContext context, final boolean restricted) {
        super();
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
        this.restricted = restricted;
    }




    public Object convert(final Object target, final String className) {

        // In restricted mode, simple names (e.g. 'String') cannot be checked against the allowlist before they are
        // resolved (possibly as java.lang.* classes), but the resolved class is always checked afterwards.
        if (!this.restricted || className.indexOf('.') >= 0) {
            checkConversionAllowed(className);
        }
        try {
            final Class<?> clazz = ClassLoaderUtils.loadClass(className);
            return convert(target, clazz);
        } catch (final ClassNotFoundException e) {
            // Only simple names can be java.lang.* classes. The loaded class is checked by convert(Object, Class)
            if (className.indexOf('.') < 0) {
                try {
                    final Class<?> clazz = ClassLoaderUtils.loadClass("java.lang." + className);
                    return convert(target, clazz);
                } catch (final ClassNotFoundException ex) {
                    // Not a java.lang.* class either
                }
            }
            throw new IllegalArgumentException("Cannot convert to class '" + className + "'", e);
        }

    }


    public Object convert(final Object target, final Class<?> clazz) {

        checkConversionAllowed(clazz.getName());
        // When the target is an array, the conversion service will also convert the elements to its component type,
        // so this component type (at any array dimension) is also a conversion target that needs to be checked.
        Class<?> componentType = clazz.getComponentType();
        while (componentType != null) {
            checkConversionAllowed(componentType.getName());
            componentType = componentType.getComponentType();
        }
        final IStandardConversionService conversionService =
                StandardExpressions.getConversionService(this.context.getConfiguration());
        return conversionService.convert(this.context, target, clazz);
    }


    private void checkConversionAllowed(final String className) {
        // Conversion target types are subject to the same type restrictions as expressions.
        // In restricted mode, only an allowlist of value types can be used as conversion targets.
        if (this.restricted) {
            if (!ExpressionUtils.isTypeAllowedForRestrictedInstantiation(className)) {
                throw new TemplateProcessingException(
                        "Type \"" + className + "\" cannot be used as a target for type conversion operations " +
                        "(#conversions) in a restricted expression execution context.");
            }
        } else if (ExpressionUtils.isTypeForbidden(className)) {
            throw new TemplateProcessingException(
                    "Type \"" + className + "\" cannot be used as a target for type conversion operations " +
                    "(#conversions): access to this type is forbidden in expressions.");
        }
    }


}

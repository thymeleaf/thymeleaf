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
package org.thymeleaf.expression;

import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.context.IProcessingContext;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Utility class for performing boolean operations.
 * </p>
 * <p>
 *   An object of this class is usually available in variable evaluation expressions with the name
 *   <tt>#bools</tt>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0 (reimplemented in 3.0.0)
 *
 */
public final class Conversions {

    private final IEngineConfiguration configuration;
    private final IProcessingContext processingContext;


    public Conversions(final IEngineConfiguration configuration, final IProcessingContext processingContext) {
        super();
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(processingContext, "Processing context cannot be null");
        this.configuration = configuration;
        this.processingContext = processingContext;
    }




    public Object convert(final Object target, final String className) {

        final ClassLoader cl = ClassLoaderUtils.getClassLoader(Conversions.class);
        try {
            final Class<?> clazz = cl.loadClass(className);
            return convert(target, clazz);
        } catch (final ClassNotFoundException e) {
            try {
                final Class<?> clazz = cl.loadClass("java.lang." + className);
                return convert(target, clazz);
            } catch (final ClassNotFoundException ex) {
                throw new IllegalArgumentException("Cannot convert to class '" + className + "'", e);
            }
        }

    }


    public Object convert(final Object target, final Class<?> clazz) {

        final IStandardConversionService conversionService =
                StandardExpressions.getConversionService(this.configuration);
        return conversionService.convert(this.processingContext, target, clazz);
    }


}

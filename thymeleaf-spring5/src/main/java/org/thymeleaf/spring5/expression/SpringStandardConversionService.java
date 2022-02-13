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
package org.thymeleaf.spring5.expression;


import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeConverter;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.AbstractStandardConversionService;

/**
 * <p>
 *   Implementation of {@link org.thymeleaf.standard.expression.IStandardConversionService} that delegates
 *   to Spring's type conversion system.
 * </p>
 * <p>
 *   If there is a {@link org.springframework.core.convert.ConversionService} available at the application
 *   context, it will be used for conversion.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public final class SpringStandardConversionService extends AbstractStandardConversionService {


    private static final TypeDescriptor TYPE_STRING = TypeDescriptor.valueOf(String.class);



    public SpringStandardConversionService() {
        // Should only be instanced from SpringStandardDialect
        super();
    }



    @Override
    protected String convertToString(
            final IExpressionContext context,
            final Object object) {

        if (object == null) {
            return null;
        }
        final TypeDescriptor objectTypeDescriptor = TypeDescriptor.forObject(object);
        final TypeConverter typeConverter = getSpringConversionService(context);
        if (typeConverter == null || !typeConverter.canConvert(objectTypeDescriptor, TYPE_STRING)) {
            return super.convertToString(context, object);
        }
        return (String) typeConverter.convertValue(object, objectTypeDescriptor, TYPE_STRING);

    }


    @Override
    protected <T> T convertOther(
            final IExpressionContext context,
            final Object object, final Class<T> targetClass) {

        if (object == null) {
            return null;
        }
        final TypeDescriptor objectTypeDescriptor = TypeDescriptor.forObject(object);
        final TypeDescriptor targetTypeDescriptor = TypeDescriptor.valueOf(targetClass);
        final TypeConverter typeConverter = getSpringConversionService(context);
        if (typeConverter == null || !typeConverter.canConvert(objectTypeDescriptor, targetTypeDescriptor)) {
            return super.convertOther(context, object, targetClass);
        }
        return (T) typeConverter.convertValue(object, objectTypeDescriptor, targetTypeDescriptor);

    }







    private static TypeConverter getSpringConversionService(final IExpressionContext context) {

        final EvaluationContext evaluationContext =
                (EvaluationContext) context.getVariable(
                        ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);

        if (evaluationContext != null) {
            return evaluationContext.getTypeConverter();
        }

        return null;

    }



}

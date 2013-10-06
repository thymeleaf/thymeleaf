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
package org.thymeleaf.spring3.expression;


import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeConverter;
import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.standard.expression.AbstractStandardConversionService;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public final class SpringStandardConversionService extends AbstractStandardConversionService {


    private static final TypeDescriptor TYPE_STRING = TypeDescriptor.valueOf(String.class);



    public SpringStandardConversionService() {
        // Should only be instanced from SpringStandardDialect
        super();
    }



    @Override
    protected String convertToString(final Configuration configuration, final IProcessingContext processingContext, final Object object) {

        final TypeConverter typeConverter = getSpringConversionService(processingContext);
        if (typeConverter == null) {
            return super.convertToString(configuration, processingContext, object);
        }
        final TypeDescriptor objectTypeDescriptor = TypeDescriptor.forObject(object);
        if (!typeConverter.canConvert(objectTypeDescriptor, TYPE_STRING)) {
            return super.convertToString(configuration, processingContext, object);
        }
        return (String) typeConverter.convertValue(object, objectTypeDescriptor, TYPE_STRING);
    }




    private static final TypeConverter getSpringConversionService(final IProcessingContext processingContext) {

        final EvaluationContext evaluationContext =
                (EvaluationContext) processingContext.getContext().getVariables().
                        get(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME);

        if (evaluationContext != null) {
            return evaluationContext.getTypeConverter();
        }

        return null;

    }



}

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
package org.thymeleaf.spring3.dialect;

import java.util.Set;

import org.thymeleaf.expression.IExpressionObjectsFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring3.expression.SPELVariableExpressionEvaluator;
import org.thymeleaf.spring3.expression.SpringStandardConversionService;
import org.thymeleaf.spring3.expression.SpringStandardExpressionObjectsFactory;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public class SpringStandardDialect extends StandardDialect {

    private static final String NAME = "SpringStandard";
    private static final String PREFIX = "th";



    public static final String MVC_EXPRESSION_OBJECT_NAME = "mvc";


    private final IExpressionObjectsFactory SPRING_STANDARD_EXPRESSION_OBJECTS_FACTORY = new SpringStandardExpressionObjectsFactory();
    private final IStandardConversionService SPRING_STANDARD_CONVERSION_SERVICE = new SpringStandardConversionService();
    
    
    
    
    public SpringStandardDialect() {
        super(NAME, PREFIX, createSpringStandardProcessorsSet());
    }




    @Override
    public IStandardVariableExpressionEvaluator getVariableExpressionEvaluator() {
        return SPELVariableExpressionEvaluator.INSTANCE;
    }



    @Override
    public IStandardConversionService getConversionService() {
        return SPRING_STANDARD_CONVERSION_SERVICE;
    }


    @Override
    public IExpressionObjectsFactory getExpressionObjectsFactory() {
        return SPRING_STANDARD_EXPRESSION_OBJECTS_FACTORY;
    }




    /**
     * <p>
     *   Create a the set of SpringStandard processors, all of them freshly instanced.
     * </p>
     * 
     * @return the set of SpringStandard processors.
     */
    public static Set<IProcessor> createSpringStandardProcessorsSet() {
        /*
         * It is important that we create new instances here because, if there are
         * several dialects in the TemplateEngine that extend StandardDialect, they should
         * not be returning the exact same instances for their processors in order
         * to allow specific instances to be directly linked with their owner dialect.
         */
        
        final Set<IProcessor> standardProcessors = StandardDialect.createStandardProcessorsSet();
return standardProcessors;
//        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>(40);
//
//        for (final IProcessor standardProcessor : standardProcessors) {
//            // There are several processors we need to remove from the Standard Dialect set
//            if (!(standardProcessor instanceof StandardObjectAttrProcessor) &&
//                !(standardProcessor instanceof StandardActionAttrProcessor) &&
//                !(standardProcessor instanceof StandardHrefAttrProcessor) &&
//                !(standardProcessor instanceof StandardMethodAttrProcessor) &&
//                !(standardProcessor instanceof StandardSrcAttrProcessor) &&
//
//                !(standardProcessor instanceof StandardValueAttrProcessor)) {
//                processors.add(standardProcessor);
//            }
//        }
//
//        processors.add(new SpringActionAttrProcessor());
//        processors.add(new SpringHrefAttrProcessor());
//        processors.add(new SpringMethodAttrProcessor());
//        processors.add(new SpringSrcAttrProcessor());
//        processors.add(new SpringValueAttrProcessor());
//        processors.add(new SpringObjectAttrProcessor());
//        processors.add(new SpringErrorsAttrProcessor());
//        processors.addAll(Arrays.asList(SpringInputGeneralFieldAttrProcessor.PROCESSORS));
//        processors.add(new SpringInputPasswordFieldAttrProcessor());
//        processors.add(new SpringInputCheckboxFieldAttrProcessor());
//        processors.add(new SpringInputRadioFieldAttrProcessor());
//        processors.add(new SpringInputFileFieldAttrProcessor());
//        processors.add(new SpringSelectFieldAttrProcessor());
//        processors.add(new SpringOptionFieldAttrProcessor());
//        processors.add(new SpringTextareaFieldAttrProcessor());
//        processors.add(new SpringErrorClassAttrProcessor());
//
//        return processors;
        
    }


}

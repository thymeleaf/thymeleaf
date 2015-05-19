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
package org.thymeleaf.spring4.expression;

import java.util.Map;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.standard.expression.StandardExpressionObjectsFactory;

/**
 * <p>
 *   Builds an instance of {@link SpringStandardExpressionObjects} to be used by SpringStandard dialects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class SpringStandardExpressionObjectsFactory extends StandardExpressionObjectsFactory {


    public SpringStandardExpressionObjectsFactory() {
        super();
    }


    @Override
    public IExpressionObjects buildExpressionObjects(final IProcessingContext processingContext) {
        return new SpringStandardExpressionObjects(processingContext);
    }


    public Map<String, String> getObjectDefinitions() {

        final Map<String,String> definitions = super.getObjectDefinitions();

        definitions.put(
                SpringStandardExpressionObjects.FIELDS_EXPRESSION_OBJECT_NAME,
                "Access to form field errors and binding info");
        definitions.put(
                SpringStandardExpressionObjects.THEMES_EXPRESSION_OBJECT_NAME,
                "Spring MVC themes operation");
        definitions.put(
                SpringStandardExpressionObjects.MVC_EXPRESSION_OBJECT_NAME,
                "Creation of Spring MVC controller-bound URLs with MvcUriComponentsBuilder");

        return definitions;

    }


}

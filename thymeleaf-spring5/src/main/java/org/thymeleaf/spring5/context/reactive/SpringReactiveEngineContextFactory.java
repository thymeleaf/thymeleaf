/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.context.reactive;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IEngineContextFactory;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringReactiveEngineContextFactory implements IEngineContextFactory {




    public SpringReactiveEngineContextFactory() {
        super();
    }




    public IEngineContext createEngineContext(
            final IEngineConfiguration configuration, final TemplateData templateData,
            final Map<String, Object> templateResolutionAttributes, final IContext context) {

        Validate.notNull(context, "Context object cannot be null");

        final Set<String> variableNames = context.getVariableNames();

        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof ISpringReactiveWebContext) {
                final ISpringReactiveWebContext srContext = (ISpringReactiveWebContext)context;
                return new SpringReactiveWebEngineContext(
                        configuration, templateData, templateResolutionAttributes,
                        srContext.getExchange(), srContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new EngineContext(
                    configuration, templateData, templateResolutionAttributes,
                    context.getLocale(), Collections.EMPTY_MAP);
        }

        final Map<String,Object> variables = new LinkedHashMap<>(variableNames.size() + 1, 1.0f);
        for (final String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof ISpringReactiveWebContext) {
            final ISpringReactiveWebContext srContext = (ISpringReactiveWebContext)context;
            return new SpringReactiveWebEngineContext(
                    configuration, templateData, templateResolutionAttributes,
                    srContext.getExchange(), srContext.getLocale(), variables);
        }

        return new EngineContext(
                configuration, templateData, templateResolutionAttributes,
                context.getLocale(), variables);

    }


}

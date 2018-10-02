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
package org.thymeleaf.spring5.context.webflux;

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
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Spring WebFlux-based implementation of the {@link IEngineContextFactory} interface.
 * </p>
 * <p>
 *   This factory will examine the {@code context} being passed as a parameter and, depending on whether
 *   this context object implements the {@link ISpringWebFluxContext} interface or not (i.e. whether support
 *   for Spring WebFlux should be enabled or not), return a {@link SpringWebFluxEngineContext} or
 *   a simple {@link EngineContext} instance as a result.
 * </p>
 * <p>
 *   This is the default factory implementation used by {@link SpringWebFluxTemplateEngine}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public class SpringWebFluxEngineContextFactory implements IEngineContextFactory {




    public SpringWebFluxEngineContextFactory() {
        super();
    }




    public IEngineContext createEngineContext(
            final IEngineConfiguration configuration, final TemplateData templateData,
            final Map<String, Object> templateResolutionAttributes, final IContext context) {

        Validate.notNull(context, "Context object cannot be null");

        final Set<String> variableNames = context.getVariableNames();

        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof ISpringWebFluxContext) {
                final ISpringWebFluxContext srContext = (ISpringWebFluxContext)context;
                return new SpringWebFluxEngineContext(
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
        if (context instanceof ISpringWebFluxContext) {
            final ISpringWebFluxContext srContext = (ISpringWebFluxContext)context;
            return new SpringWebFluxEngineContext(
                    configuration, templateData, templateResolutionAttributes,
                    srContext.getExchange(), srContext.getLocale(), variables);
        }

        return new EngineContext(
                configuration, templateData, templateResolutionAttributes,
                context.getLocale(), variables);

    }


}

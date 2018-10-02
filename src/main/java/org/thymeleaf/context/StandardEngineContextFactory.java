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
package org.thymeleaf.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Standard implementation of the {@link IEngineContextFactory} interface.
 * </p>
 * <p>
 *   This factory will examine the {@code context} being passed as a parameter and, depending on whether
 *   this context object implements the {@link IWebContext} interface or not (i.e. whether support for the
 *   Servlet API should be enabled or not), return a {@link WebEngineContext} or an {@link EngineContext}
 *   instance as a result.
 * </p>
 * <p>
 *   This is the default factory implementation used by {@link org.thymeleaf.TemplateEngine}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardEngineContextFactory implements IEngineContextFactory {




    public StandardEngineContextFactory() {
        super();
    }




    public IEngineContext createEngineContext(
            final IEngineConfiguration configuration, final TemplateData templateData,
            final Map<String, Object> templateResolutionAttributes, final IContext context) {

        Validate.notNull(context, "Context object cannot be null");

        // NOTE calling getVariableNames() on an IWebContext would be very expensive, as it would mean
        // calling HttpServletRequest#getAttributeNames(), which is very slow in some common implementations
        // (e.g. Apache Tomcat). So it's a good thing we might have tried to reuse the IEngineContext
        // before calling this factory.
        final Set<String> variableNames = context.getVariableNames();

        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof IWebContext) {
                final IWebContext webContext = (IWebContext)context;
                return new WebEngineContext(
                        configuration, templateData, templateResolutionAttributes,
                        webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(),
                        webContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new EngineContext(
                    configuration, templateData, templateResolutionAttributes,
                    context.getLocale(), Collections.EMPTY_MAP);
        }

        final Map<String,Object> variables = new LinkedHashMap<String, Object>(variableNames.size() + 1, 1.0f);
        for (final String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof IWebContext) {
            final IWebContext webContext = (IWebContext)context;
            return new WebEngineContext(
                    configuration, templateData, templateResolutionAttributes,
                    webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(),
                    webContext.getLocale(), variables);
        }

        return new EngineContext(
                configuration, templateData, templateResolutionAttributes,
                context.getLocale(), variables);

    }


}

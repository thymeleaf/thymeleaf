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
package org.thymeleaf.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * <p>
 *   Static factory class for creating suitable {@link IEngineContext} objects from existing {@link IContext}
 *   context objects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StandardEngineContextFactory {


    public static IEngineContext buildEngineContext(
            final IEngineConfiguration configuration, final TemplateResolution templateResolution, final IContext context) {

        if (context instanceof IEngineContext) {
            // If this context is already an IEngineContext, we will not clone it
            return (IEngineContext) context;
        }

        // NOTE calling getVariableNames() on an IWebContext would be very expensive, as it would mean
        // calling HttpServletRequest#getAttributeNames(), which is very slow in some common implementations
        // (e.g. Apache Tomcat). So it's a good thing we might have reused the IEngineContext above.
        final Set<String> variableNames = context.getVariableNames();

        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof IWebContext) {
                final IWebContext webContext = (IWebContext)context;
                return new WebEngineContext(
                        configuration, templateResolution,
                        webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(),
                        webContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new EngineContext(
                    configuration, templateResolution,
                    context.getLocale(), Collections.EMPTY_MAP);
        }

        final Map<String,Object> variables = new LinkedHashMap<String, Object>(variableNames.size() + 1, 1.0f);
        for (final String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof IWebContext) {
            final IWebContext webContext = (IWebContext)context;
            return new WebEngineContext(
                    configuration, templateResolution,
                    webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(),
                    webContext.getLocale(), variables);
        }

        return new EngineContext(
                configuration, templateResolution,
                context.getLocale(), variables);

    }



    private StandardEngineContextFactory() {
        super();
    }


}

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
package org.thymeleaf.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IWebContext;

/**
 * <p>
 *   Static manager class for creating suitable {@link IEngineContext} objects from existing {@link IContext}
 *   context objects if needed, as well as making sure the adequate template resolution objects are set into
 *   these context objects.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class EngineContextManager {


    static IEngineContext prepareEngineContext(
            final IEngineConfiguration configuration,
            final TemplateData templateData, final Map<String, Object> templateResolutionAttributes,
            final IContext context) {

        final IEngineContext engineContext =
                createEngineContextIfNeeded(configuration, templateData, templateResolutionAttributes, context);

        // We will always do this, even if the context is a new object (in which case it would be completely needed)
        // because we want to make sure the 'disposeEngineContext' call that will come afterwards can safely
        // decrease the level
        engineContext.increaseLevel();

        if (context instanceof IEngineContext) {
            // Set the template resolution into the context, but only if we haven't just created it
            engineContext.setTemplateData(templateData);
        }

        return engineContext;

    }


    static void disposeEngineContext(final IEngineContext engineContext) {
        engineContext.decreaseLevel();
    }




    private static IEngineContext createEngineContextIfNeeded(
            final IEngineConfiguration configuration,
            final TemplateData templateData, final Map<String, Object> templateResolutionAttributes,
            final IContext context) {

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



    private EngineContextManager() {
        super();
    }


}

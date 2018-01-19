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
package org.thymeleaf.engine;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.IEngineContextFactory;

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

        // It's the engine context factory the one who has the responsibility of creating the specific
        // implementation of the engine context needed.
        final IEngineContextFactory engineContextFactory = configuration.getEngineContextFactory();
        return engineContextFactory.createEngineContext(
                configuration, templateData, templateResolutionAttributes, context);

    }



    private EngineContextManager() {
        super();
    }


}

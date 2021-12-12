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

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;

/**
 * <p>
 *   Common interface for all factory instances in charge of creating the {@link IEngineContext} implementations
 *   that will be used during template execution.
 * </p>
 * <p>
 *   Engine Contexts (implementations of {@link IEngineContext}) are the type of context really used by the engine
 *   during template processing. These factories will be called in order to create {@link IEngineContext} instances
 *   from the original {@link IContext} implementations (the much simpler {@code context} objects that were used
 *   for calling the template engine).
 * </p>
 * <p>
 *   The specific implementation of this interface to be used for template processing can be obtained and set at
 *   {@link org.thymeleaf.TemplateEngine} instances by means of its
 *   {@link org.thymeleaf.TemplateEngine#getEngineContextFactory()} and
 *   {@link org.thymeleaf.TemplateEngine#setEngineContextFactory(IEngineContextFactory)} methods.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see StandardEngineContextFactory
 *
 * @since 3.0.0
 *
 */
public interface IEngineContextFactory {


    /**
     * <p>
     *   Creates a new {@link IEngineContext} to be used for processing a specific template.
     * </p>
     * <p>
     *   Note this factory method will be only called once during the processing of a template. Once a engine context
     *   instance has been created, the engine will try to reuse it for any nested processing operations as far as
     *   possible. This means that, e.g., the {@code templateData} specified here will only be the <em>root-level</em>
     *   template data (the one for the template that is actually being used as an
     *   {@link org.thymeleaf.ITemplateEngine}{@code .process(...)} argument). Any {@code th:insert} or
     *   {@code th:replace} operations inside that template will not ask this factory to create a new engine context,
     *   but instead just increase the nesting level of the already-existing one
     *   (see {@link IEngineContext#increaseLevel()}) and set the new, nested template data for that level
     *   (see {@link IEngineContext#setTemplateData(TemplateData)}).
     * </p>
     * <p>
     *   Note also that the {@code context} object passed here as an argument will normally correspond to the
     *   simple {@link IContext} implementation used for calling
     *   {@link org.thymeleaf.ITemplateEngine}{@code .process(...)} and, therefore, will normally be an object
     *   of class {@link EngineContext}, {@link WebContext} or similar.
     * </p>
     *
     * @param configuration the engine configuration being used.
     * @param templateData the {@link TemplateData} to be applied at level 0, i.e. the top-level template being processed.
     * @param templateResolutionAttributes the template resolution attributes specified for processing this template.
     * @param context the context, normally the one used for calling the Template Engine itself.
     * @return a new, freshly built engine context instance.
     */
    public IEngineContext createEngineContext(
            final IEngineConfiguration configuration, final TemplateData templateData,
            final Map<String, Object> templateResolutionAttributes, final IContext context);


}

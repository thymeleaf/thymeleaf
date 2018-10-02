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
package org.thymeleaf.templateparser.markup.decoupled;

import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/**
 * <p>
 *   Common interface for all resolver objects in charge of obtaining the resource that should contain the
 *   decoupled template logic for a template being processed.
 * </p>
 * <p>
 *   The specific instance of this class that will be used can be configured at the
 *   {@link org.thymeleaf.TemplateEngine} using its
 *   {@link org.thymeleaf.TemplateEngine#setDecoupledTemplateLogicResolver(IDecoupledTemplateLogicResolver)} and
 *   {@link TemplateEngine#getDecoupledTemplateLogicResolver()} methods.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see StandardDecoupledTemplateLogicResolver
 *
 * @since 3.0.0
 * 
 */
public interface IDecoupledTemplateLogicResolver {


    /**
     * <p>
     *   Resolve an {@link ITemplateResource} object containing the decoupled template logic to be applied to the
     *   template being processed.
     * </p>
     * <p>
     *   Normally, this <em>decoupled template logic resource</em> will be obtained from the original template's
     *   resource itself, but implementations can opt for different mechanisms.
     * </p>
     *
     * @param configuration the configuration object being used.
     * @param ownerTemplate the owner of the template for which this is being resolved, or null if it is a first-level template.
     * @param template the template for which decoupled logic is being.
     * @param templateSelectors the selectors to be used, defining the fragments that should be processed.
     * @param resource the resource of the resolved template.
     * @param templateMode the template mode to be applied to the resolved template.
     * @return the resource containing the decoupled template logic, or {@code null} if there isn't any.
     */
    public ITemplateResource resolveDecoupledTemplateLogic(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final Set<String> templateSelectors,
            final ITemplateResource resource, final TemplateMode templateMode);


}

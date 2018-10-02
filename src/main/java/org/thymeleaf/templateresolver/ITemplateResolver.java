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
package org.thymeleaf.templateresolver;

import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/**
 * <p>
 *   Interface for all Template Resolvers.
 * </p>
 * <p>
 *   Template resolvers are in charge of resolving templates into
 *   {@link TemplateResolution} objects that contain additional information related to
 *   the template like:
 * </p>
 * <ul>
 *   <li>Its corresponding <i>template resource</i> (see
 *       {@link org.thymeleaf.templateresource.ITemplateResource}).</li>
 *   <li>The Template Mode to be applied to this template: {@link TemplateMode}</li>
 *   <li>Whether the template can be cached or not.</li>
 *   <li>If the template can be cached, (optionally) the time it will live in cache.</li>
 * </ul>
 * <p>
 *   The Template Resolver will usually get all this information from a set of configurations
 *   like applicability patterns, template mode patterns, etc. Each {@link ITemplateResolver}
 *   implementation will provide its own set of methods for specifying such configurations. 
 * </p>
 * <p>
 *   The fact that a Template Resolver returns a {@link TemplateResolution} does not necessarily
 *   mean that the resolved template resource exists. It might only be so if the template resolver
 *   is configured to perform an <em>existence check</em> on the resource before returning a resolution
 *   result (by means of calling {@link ITemplateResource#exists()}), which might be configurable on
 *   a per-{@link ITemplateResolver}-implementation basis. Implementations might choose not to check
 *   resource existance by default in order to avoid the possible performance impact of a double access
 *   to the resource.
 * </p>
 * <p>
 *   A Template Engine can be configured several template resolvers, and these will
 *   be asked in order (according to the value returned by {@link #getOrder()}) to return
 *   a {@link TemplateResolution} object for each template name. If a template resolver
 *   returns null for a specific resolution, the next one in the chain is asked. Template Resolvers
 *   that are not configured an order will be executed last in the chain.
 * </p>
 * <p>
 *   Implementations of this interface should be <strong>thread-safe</strong>.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @see ITemplateResource
 * @see ClassLoaderTemplateResolver
 * @see FileTemplateResolver
 * @see ServletContextTemplateResolver
 * @see StringTemplateResolver
 * @see UrlTemplateResolver
 *
 * @since 3.0.0
 *
 */
public interface ITemplateResolver {

    
    /**
     * <p>
     *   Returns the name of this template resolver. Used in logs and configuration
     *   details.
     * </p>
     * 
     * @return the template resolver name.
     */
    public String getName();

    
    /**
     * <p>
     *   Return the order in which this template resolver will be executed in the
     *   chain when several template resolvers are set for the same Template Engine.
     * </p>
     * 
     * @return the order of this resolver in the chain.
     */
    public Integer getOrder();


    /**
     * <p>
     *   Tries to resolve a template.
     * </p>
     * <p>
     *   The method arguments contain all the info needed for trying to
     *   resolve the template. The Template Resolver will apply its configuration
     *   (prefixes/suffixes, template mode patterns, cache configurations, etc) and
     *   return a {@link TemplateResolution} object.
     * </p>
     * <p>
     *   The {@code ownerTemplate}, which might be null, will be specified when the template
     *   is resolved in order to be used as a fragent to be inserted into a higher level
     *   template (the <em>owner</em>). Most template resolver implementations will simply ignore
     *   this argument, but others might change their resolution results depending on the
     *   owner template that is inserting the resolved fragment.
     * </p>
     * <p>
     *   The fact that a Template Resolver returns a {@link TemplateResolution} does not necessarily
     *   mean that the resolved template resource exists. It might only be so if the template resolver
     *   is configured to perform an <em>existence check</em> on the resource before returning a resolution
     *   result (by means of calling {@link ITemplateResource#exists()}), which might be configurable on
     *   a per-{@link ITemplateResolver}-implementation basis. Implementations might choose not to check
     *   resource existance by default in order to avoid the possible performance impact of a double access
     *   to the resource.
     * </p>
     * <p>
     *   Note that the <em>template selectors</em> that might be used for a executing or inserting a template
     *   are not specified to the template resolver. The reason is template selectors are applied by the parser,
     *   not the template resolvers, and allowing the resolver to take any decisions based on template selectors
     *   (like e.g. omitting some output from the resource) could harm the correctness of the selection operation
     *   performed by the parser.
     * </p>
     * 
     * @param configuration the engine configuration.
     * @param ownerTemplate the containing template from which we want to resolve a new one as a fragment. Can be null.
     * @param template the template to be resolved (usually its name).
     * @param templateResolutionAttributes the template resolution attributes to be used (usually coming from a
     *                                     {@link org.thymeleaf.TemplateSpec} instance. Can be null.
     * @return a TemplateResolution object (which might represent an existing resource or not), or null if the
     *         template could not be resolved.
     */
    public TemplateResolution resolveTemplate(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template,
            final Map<String, Object> templateResolutionAttributes);

}

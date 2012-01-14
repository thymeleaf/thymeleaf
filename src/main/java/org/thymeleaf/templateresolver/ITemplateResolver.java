/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.TemplateProcessingParameters;

/**
 * <p>
 *   Interface for all Template Resolvers.
 * </p>
 * <p>
 *   Template resolvers are in charge of resolving template names into 
 *   {@link TemplateResolution} objects that contain additional information related to
 *   the template like:
 * </p>
 * <ul>
 *   <li>Its corresponding <i>resource name</i>: the name that will be passed to the
 *       returned Resource Resolver in order to read the template.</li>
 *   <li>The Resource Resolver (implementation of {@link org.thymeleaf.resourceresolver.IResourceResolver})
 *       to be used for trying to read this template.</li>
 *   <li>The Template Mode to be applied to this template: XML, VALIDXML, XHTML, VALIDXHTML, HTML5 or LEGACYHTML5.</li>
 *   <li>The character encoding to be used when reading this template.</li>
 *   <li>Whether the template can be cached or not.</li>
 *   <li>If the template can be cached, (optionally) the time it will live in cache.</li>
 * </ul>
 * <p>
 *   The Template Resolver will usually get all this information from a set of configurations
 *   like applicability patterns, template mode patterns, etc. Each {@link ITemplateResolver}
 *   implementation will provide its own set of methods for specifying such configurations. 
 * </p>
 * <p>
 *   Note that it is allowed for a Template Resolver to return a result even if a template
 *   will not be resolvable by its Resource Resolver in the end. Many times it is not 
 *   possible to know whether a template can be effectively resolved by a template 
 *   resolver until the template <i>resource</i> is actually read into an InputStream so, 
 *   in order to avoid two read operations for each template, many times Template Resolvers 
 *   will return a result but Resource Resolvers will return none once executed.
 * </p>
 * <p>
 *   A Template Engine can be configured several template resolvers, and these will
 *   be asked in order (according to the value returned by {@link #getOrder()}) to return
 *   a {@link TemplateResolution} object for each template name. If a template resolver
 *   returns null or its resource resolver does, the next one in the chain is asked. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
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
     *   The <tt>templateProcessingParameters</tt> parameter contains all the info needed for trying to
     *   resolve the template (esp. the <i>template name</i>). The Template Resolver
     *   will apply its configuration (prefixes/suffixes, template mode patterns,
     *   cache configurations, etc) and return a {@link TemplateResolution} object.
     * </p>
     * <p>
     *   This method can return null if the template resolver is completely certain
     *   that a template cannot be resolved by it. But returning a result does not
     *   mean the contrary, because it could be that the {@link org.thymeleaf.resourceresolver.IResourceResolver}
     *   object returned in the result is not effectively able to resolve the
     *   <i>resource</i> corresponding to this template. As sometimes this cannot be known in
     *   advance (the template resource would have to be read two times
     *   for that), it will not be until the Template Engine executes the Resource
     *   Resolver that it will know whether the template was correctly resolved by
     *   a Template Resolver or not.
     * </p>
     * 
     * @param templateProcessingParameters the information required to resolve a template
     * @return a TemplateResolution object containing (maybe valid) resource resolution
     *         info for the template, or null.
     */
    /*
     * Templates are resolved by String name (templateProcessingParameters.getTemplateName())
     * Will return null if template cannot be handled by this template resolver.
     */
    public TemplateResolution resolveTemplate(final TemplateProcessingParameters templateProcessingParameters);
    
    
    /**
     * <p>
     *   Initialize the Template Resolver. Once initialized, none of its configuration
     *   parameters should be allowed to change.
     * </p>
     * <p>
     *   This method is called by TemplateEngine. <b>Do not use directly in your code</b>.
     * </p>
     */
    public void initialize();
    
}

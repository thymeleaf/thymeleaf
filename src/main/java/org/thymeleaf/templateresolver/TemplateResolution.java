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
package org.thymeleaf.templateresolver;

import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Result of the execution of a Template Resolver.
 * </p>
 * <p>
 *   A TemplateResolution object is created by implementations of {@link ITemplateResolver}
 *   when templates are resolved, and it contains: 
 * </p>
 * <ul>
 *   <li>The <i>resource name</i> corresponding to the resolved template: this will be the name 
 *       that will be passed to the included Resource Resolver in order to read the template.</li>
 *   <li>The Resource Resolver (implementation of {@link org.thymeleaf.resourceresolver.IResourceResolver})
 *       to be used for trying to read this template.</li>
 *   <li>The Template Mode to be applied to this template.</li>
 *   <li>The character encoding to be used when reading this template.</li>
 *   <li>The validity of this template resolution, indicating whether this template can be included
 *       in cache once resolved, and the logic that will determine for how long and in which
 *       circumstances its cache entry will still be considered valid.</li>
 * </ul>
 * <p>
 *   The fact that a Template Resolver returns a {@link TemplateResolution} does not necessarily
 *   mean that the template will be effectively be resolved by the Template Resolver. For this,
 *   the Resource Resolver will also need to be able to resolve the declared template's resource. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class TemplateResolution {

    private final String templateName;
    private final String resourceName;
    private final IResourceResolver resourceResolver;
    private final String characterEncoding;
    private final TemplateMode templateMode;
    private final ITemplateResolutionValidity validity;


    
    public TemplateResolution(
            final String templateName, final String resourceName, 
            final IResourceResolver resourceResolver, final String characterEncoding, 
            final TemplateMode templateMode,
            final ITemplateResolutionValidity validity) {
        super();
        Validate.notNull(templateName, "Template name cannot be null");
        Validate.notNull(resourceName, "Resource name cannot be null");
        Validate.notNull(resourceResolver, "Resource resolver cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(validity, "Validity cannot be null");
        this.templateName = templateName;
        this.resourceName = resourceName;
        this.resourceResolver = resourceResolver;
        this.characterEncoding = characterEncoding;
        this.templateMode = templateMode;
        this.validity = validity;
    }
    

    /**
     * <p>
     *   Returns the template name.
     * </p>
     * <p>
     *   This should be the same name used for calling the 
     *   {@link org.thymeleaf.TemplateEngine#process(String, org.thymeleaf.context.IContext)}
     *   method.
     * </p>
     * 
     * @return the template name.
     */
    public String getTemplateName() {
        return this.templateName;
    }
    

    /**
     * <p>
     *   Returns the resource name.
     * </p>
     * <p>
     *   The <i>resource name</i> is the identifier that will be used for actually reading
     *   the template by means of the Resource Resolver (see {@link #getResourceResolver()}). 
     * </p> 
     * <p>
     *   This resource name will typically be computed by the Template Resolver from the template
     *   name. For example, a Template Resolver could have a prefix and a suffix configured
     *   so that starting from the template name <tt>"home"</tt> it returns the resource name 
     *   <tt>"/WEB-INF/templates/web/home.html"</tt>, which can be then read by a
     *   {@link org.thymeleaf.resourceresolver.ClassLoaderResourceResolver} instance.
     * </p>
     * 
     * @return the resource name.
     */
    public String getResourceName() {
        return this.resourceName;
    }
    
    
    /**
     * <p>
     *   Returns the resource resolver that should be used for actually reading the template
     *   (using the provided <i>resource name</i>, see {@link #getResourceName()}).
     * </p>
     * 
     * @return the resource resolver.
     */
    public IResourceResolver getResourceResolver() {
        return this.resourceResolver;
    }
    

    /**
     * <p>
     *   Returns the character encoding that should be used for reading the template
     *   (using the Resource Resolver). 
     * </p>
     * 
     * @return the character encoding.
     */
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }


    /**
     * <p>
     *   Returns the template mode to be applied.
     * </p>
     * 
     * @return the template mode.
     */
    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    /**
     * <p>
     *   Returns the template resolution <i>validity</i>.
     * </p>
     * <p>
     *   This validity establishes whether the template can be included in the
     *   template cache, and also for how long its resolution will be considered <i>valid</i>.
     * </p>
     * <p>
     *   When a cached template is not considered valid, its cache entry is discarded
     *   and it is resolved again.
     * </p>
     * 
     * @return the validity object
     */
    public ITemplateResolutionValidity getValidity() {
        return this.validity;
    }
    
    
}

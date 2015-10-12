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

import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Result of the execution of a Template Resolver.
 * </p>
 * <p>
 *   A TemplateResolution object is created by implementations of {@link ITemplateResolver}
 *   when templates are resolved, and it contains not only the resource representing the resolved
 *   template, but also the template mode to be used and the cache validity to be applied.
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
 *   Note a class with this name existed since 1.0, but it was completely reimplemented in 3.0.0.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class TemplateResolution {

    private final String template;
    private final ITemplateResource templateResource;
    private final TemplateMode templateMode;
    private final ICacheEntryValidity validity;


    
    public TemplateResolution(
            final String template,
            final ITemplateResource templateResource,
            final TemplateMode templateMode,
            final ICacheEntryValidity validity) {
        super();
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(templateResource, "Template Resource cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(validity, "Validity cannot be null");
        this.template = template;
        this.templateResource = templateResource;
        this.templateMode = templateMode;
        this.validity = validity;
    }
    

    /**
     * <p>
     *   Returns the template (usually a template name).
     * </p>
     * <p>
     *   This should be the same template used for calling the
     *   {@link org.thymeleaf.TemplateEngine#process(String, org.thymeleaf.context.IContext)}
     *   method.
     * </p>
     * 
     * @return the template.
     */
    public String getTemplate() {
        return this.template;
    }


    /**
     * <p>
     *   Returns the template resource.
     * </p>
     * <p>,
     *   This should be the same name used for calling the
     *   {@link org.thymeleaf.TemplateEngine#process(String, org.thymeleaf.context.IContext)}
     *   method.
     * </p>
     *
     * @return the template name.
     */
    public ITemplateResource getTemplateResource() {
        return this.templateResource;
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
    public ICacheEntryValidity getValidity() {
        return this.validity;
    }
    
    
}

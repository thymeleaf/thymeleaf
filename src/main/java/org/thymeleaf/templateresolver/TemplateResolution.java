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
 *   Objects of this class should <strong>not</strong> be considered thread-safe.
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

    private final ITemplateResource templateResource;
    private final boolean templateResourceExistenceVerified;
    private final TemplateMode templateMode;
    private final boolean useDecoupledLogic;
    private final ICacheEntryValidity validity;


    
    public TemplateResolution(
            final ITemplateResource templateResource,
            final TemplateMode templateMode,
            final ICacheEntryValidity validity) {
        this(templateResource, false, templateMode, false, validity);
    }


    public TemplateResolution(
            final ITemplateResource templateResource,
            final boolean templateResourceExistenceVerified,
            final TemplateMode templateMode,
            final boolean useDecoupledLogic,
            final ICacheEntryValidity validity) {
        super();
        Validate.notNull(templateResource, "Template Resource cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(validity, "Validity cannot be null");
        this.templateResource = templateResource;
        this.templateResourceExistenceVerified = templateResourceExistenceVerified;
        this.templateMode = templateMode;
        this.useDecoupledLogic = useDecoupledLogic;
        this.validity = validity;
    }


    /**
     * <p>
     *   Returns the template resource.
     * </p>
     * <p>
     *   Template resource instances are usually created by implementations of
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver}.
     * </p>
     * <p>
     *   Note that, even if this resource object will never be {@code null}, the existence of the
     *   resource object does not necessarily imply the existence of the resource itself unless
     *   the template resolver was configured for calling {@link ITemplateResource#exists()} upon
     *   template resolution.
     * </p>
     *
     * @return the template resource
     */
    public ITemplateResource getTemplateResource() {
        return this.templateResource;
    }


    /**
     * <p>
     *   Returns the template mode to be applied to the template, as suggested by the
     *   {@link ITemplateResolver}.
     * </p>
     * <p>
     *   Note that this template mode can be ignored if the template being resolved is configured to be
     *   executed with a specific template mode regardless of what the template resolver suggests.
     * </p>
     * 
     * @return the template mode for the resolved template
     */
    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    /**
     * <p>
     *   Returns whether the existence of the resource returned by the resolution mechanism has
     *   been already verified to actually exist by the template resolver during resolution.
     * </p>
     * <p>
     *   This allows avoiding further checks in case the {@code resource.exists()} execution is
     *   costly.
     * </p>
     * <p>
     *   Note a {@code false} here does not mean the resource does not exist, but simply that
     *   its existence was not verified (true) during resolution.
     * </p>
     *
     * @return whether the existence of the resource was verified during resolution.
     */
    public boolean isTemplateResourceExistenceVerified() {
        return this.templateResourceExistenceVerified;
    }


    /**
     * <p>
     *   Returns whether the existence of decoupled logic (normally in a separate file) should be checked
     *   for this template during parsing.
     * </p>
     * <p>
     *   Note a {@code true} here does not mean that a decoupled logic resource has to necessarily exist for this
     *   template, only that its existence should be checked and used if it exists.
     * </p>
     *
     * @return whether the existence of decoupled logic should be checked.
     */
    public boolean getUseDecoupledLogic() {
        return this.useDecoupledLogic;
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

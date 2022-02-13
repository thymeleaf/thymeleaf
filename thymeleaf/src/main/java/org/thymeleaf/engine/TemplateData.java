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

import java.util.Set;

import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

/**
 * <p>
 *   Class containing all the data related to a template that is currently being processed.
 * </p>
 * <p>
 *   Objects of this class are meant to be a part of the {@link ITemplateContext} instances
 *   offered by the engine to processing artifacts such as processors, pre-/post-processors, etc.
 * </p>
 * <p>
 *   These objects contain some metadata about the template being processed, and also the template resource
 *   itself, coming from the {@link org.thymeleaf.templateresolver.TemplateResolution}
 *   produced by <em>template resolvers</em> ({@link org.thymeleaf.templateresolver.ITemplateResolver}).
 * </p>
 * <p>
 *   The <em>template</em> contained usually represents the <em>template name</em>, but can be the entire
 *   template contents if the template has been resolved by a
 *   {@link org.thymeleaf.templateresolver.StringTemplateResolver} or an analogous implementation of
 *   {@link org.thymeleaf.templateresolver.ITemplateResolver}.
 * </p>
 * <p>
 *   Objects of this class should <strong>not</strong> be considered thread-safe.
 * </p>
 *
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see org.thymeleaf.TemplateSpec
 * @see org.thymeleaf.templateresolver.TemplateResolution
 *
 * @since 3.0.0
 *
 */
public final class TemplateData {

    private final String template;
    private final Set<String> templateSelectors;
    private final ITemplateResource templateResource;
    private final TemplateMode templateMode;
    private final ICacheEntryValidity cacheValidity;


    /**
     * <p>
     *   Builds a new {@code TemplateData} object.
     * </p>
     * <p>
     *   This constructor should be considered internal, as there should be no reason why
     *   instances of this class should be created from outside the Template Engine itself.
     * </p>
     *
     * @param template the template
     * @param templateSelectors the template selectors
     * @param templateResource the template resource
     * @param templateMode the template mode
     */
    TemplateData(
            final String template,
            final Set<String> templateSelectors,
            final ITemplateResource templateResource,
            final TemplateMode templateMode,
            final ICacheEntryValidity cacheValidity) {

        super();

        // NO VALIDATIONS OR TRANSFORMATIONS ARE PERFORMED ON DATA. This constructor is package-protected so that
        // objects of this class can only be created from the engine (specifically, the TemplateManager). This
        // template manager will make sure only templateSelectors and templateResolutionAttributes can be null, and
        // that if they aren't they should be non-empty. Also, templateSelectors will come ordered by natural order,
        // an operation that is performed at the TemplateSpec constructor itself.
        this.template = template;
        this.templateSelectors = templateSelectors;
        this.templateResource = templateResource;
        this.templateMode = templateMode;
        this.cacheValidity = cacheValidity;

    }


    /**
     * <p>
     *   Returns the template (usually the template name).
     * </p>
     * <p>
     *   This <em>template</em> normally represents the <em>template name</em>, but can be the entire template
     *   contents if the template was specified as a String and resolved by a
     *   {@link org.thymeleaf.templateresolver.StringTemplateResolver} or equivalent.
     * </p>
     *
     * @return the template. Cannot be null.
     */
    public String getTemplate() {
        return this.template;
    }


    /**
     * <p>
     *   Returns whether this spec has template selectors specified or not.
     * </p>
     *
     * @return {@code true} of there are template selectors, {@code false} if not.
     */
    public boolean hasTemplateSelectors() {
        // Checking for null is enough, as we have already processed this in the constructor
        return this.templateSelectors != null;
    }


    /**
     * <p>
     *   Returns the template selectors, if there are any.
     * </p>
     * <p>
     *   Template selectors allow the possibility to process only a part of the specified template, expressing
     *   this selection in a syntax similar to jQuery, CSS or XPath selectors. Note this is only available for
     *   <em>markup template modes</em> ({@code HTML}, {@code XML}). For more info on <em>template selectors</em>
     *   syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     *   documentation.
     * </p>
     *
     * @return the template selectors, or {@code null} if there are none.
     */
    public Set<String> getTemplateSelectors() {
        return this.templateSelectors;
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
     * @return the template resource. Cannot be null.
     */
    public ITemplateResource getTemplateResource() {
        return this.templateResource;
    }


    /**
     * <p>
     *   Returns the template mode the template is being processed with.
     * </p>
     * <p>
     *   Most times this template mode is the one suggested by the
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver} that resolved the template, but
     *   in those times that a template mode was <em>forced</em> by specifying it at a
     *   {@link org.thymeleaf.TemplateSpec} object or at a call to the {@link org.thymeleaf.engine.TemplateManager},
     *   these will override the template mode suggested by the template resolver.
     * </p>
     * 
     * @return the template mode for the template. Cannot be null.
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
        return this.cacheValidity;
    }


}

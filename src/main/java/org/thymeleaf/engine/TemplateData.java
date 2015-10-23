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

import java.util.Map;
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
 *   Also, these objects contain basically the same info as {@link org.thymeleaf.TemplateSpec},
 *   but adding all the information coming from {@link org.thymeleaf.templateresolver.TemplateResolution}
 *   results produced by <em>template resolvers</em> ({@link org.thymeleaf.templateresolver.ITemplateResolver}).
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
    private final ICacheEntryValidity validity;
    private final Map<String,Object> templateResolutionAttributes;


    /**
     * <p>
     *   Builds a new <tt>TemplateData</tt> object.
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
     * @param validity the template cache validity
     * @param templateResolutionAttributes the template resolution attributes
     */
    TemplateData(
            final String template,
            final Set<String> templateSelectors,
            final ITemplateResource templateResource,
            final TemplateMode templateMode,
            final ICacheEntryValidity validity,
            final Map<String,Object> templateResolutionAttributes) {

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
        this.validity = validity;
        this.templateResolutionAttributes = templateResolutionAttributes;

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
     * @return <tt>true</tt> of there are template selectors, <tt>false</tt> if not.
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
     *   <em>markup template modes</em> (<tt>HTML</tt>, <tt>XML</tt>). For more info on <em>template selectors</em>
     *   syntax, have a look at <a href="http://www.attoparser.org">AttoParser</a>'s <em>markup selectors</em>
     *   documentation.
     * </p>
     *
     * @return the template selectors, or <tt>null</tt> if there are none.
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
     *   Note that, even if this resource object will never be <tt>null</tt>, the existence of the
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
        return this.validity;
    }


    /**
     * <p>
     *   Returns whether this spec includes template resolution attributes or not.
     * </p>
     *
     * @return <tt>true</tt> of there are template resolution attributes, <tt>false</tt> if not.
     */
    public boolean hasTemplateResolutionAttributes() {
        // Checking for null is enough, as we have already processed this in the constructor
        return this.templateResolutionAttributes != null;
    }


    /**
     * <p>
     *   Returns the template resolution attributes, if any have been specified.
     * </p>
     * <p>
     *   The template resolution attributes are meant to be passed to the template resolvers (see
     *   {@link org.thymeleaf.templateresolver.ITemplateResolver} during template resolution, as a way
     *   of configuring their execution for the template being processed.
     * </p>
     * <p>
     *   Note that template resolution attributes are considered a part of the <em>identifier</em> of a template,
     *   so they will be used as a part of the keys for cached templates. <strong>It is therefore
     *   required that template attribute maps contain values with valid {@link #equals(Object)}
     *   and {@link #hashCode()} implementations</strong>. Therefore, using simple (and fast)
     *   <tt>Map&lt;String,String&gt;</tt> maps is the recommended option.
     * </p>
     *
     * @return the template resolution attributes.
     */
    public Map<String, Object> getTemplateResolutionAttributes() {
        return this.templateResolutionAttributes;
    }


}

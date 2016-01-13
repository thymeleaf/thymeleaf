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

import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.PatternSpec;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Convenience base class for all Template Resolvers.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public abstract class AbstractTemplateResolver implements ITemplateResolver {

    /**
     * <p>
     *   By default, resources will not be checked their existence before being returned. This tries to
     *   avoid a possible performance impact from performing a double access to the resource (one for checking
     *   existence, another one for reading it).
     * </p>
     */
    public static final boolean DEFAULT_EXISTENCE_CHECK = false;

    
    private String name = this.getClass().getName();
    private Integer order = null;
    private boolean checkExistence = DEFAULT_EXISTENCE_CHECK;

    private final PatternSpec resolvablePatternSpec = new PatternSpec();
    



    protected AbstractTemplateResolver() {
        super();
    }



    /**
     * <p>
     *   Returns the name of the template resolver
     * </p>
     * 
     * @return the name of the template resolver
     */
    public final String getName() {
        return this.name;
    }


    /**
     * <p>
     *   Sets a new name for the Template Resolver.
     * </p>
     * 
     * @param name the new name
     */
    public final void setName(final String name) {
        this.name = name;
    }
    
    
    /**
     * <p>
     *   Returns the order in which this template resolver will be asked to resolve
     *   templates as a part of the chain of resolvers configured into the template engine.
     * </p>
     * <p>
     *   Order should start with 1.
     * </p>
     * 
     * @return the order in which this template resolver will be called in the chain.
     */
    public final Integer getOrder() {
        return this.order;
    }


    /**
     * <p>
     *   Sets a new order for the template engine in the chain. Order should start with 1.
     * </p>
     * 
     * @param order the new order.
     */
    public final void setOrder(final Integer order) {
        this.order = order;
    }
    
    

    
    /**
     * <p>
     *   Returns the <i>pattern spec</i> specified for establishing which
     *   templates can be resolved by this template resolver. For those templates
     *   which names do not match this patterns, the Template Resolver will return null.
     * </p>
     * <p>
     *   This allows for a fast discard of those templates that the developer might
     *   know for sure that will not be resolvable by the Resource Resolver used by
     *   this Template Resolver, so that an execution of the resource resolver is not needed.
     * </p>
     * 
     * @return the pattern spec
     */
    public final PatternSpec getResolvablePatternSpec() {
        return this.resolvablePatternSpec;
    }
    
    /**
     * <p>
     *   Returns the <i>patterns</i> (as String) specified for establishing which
     *   templates can be resolved by this template resolver. For those templates
     *   which names do not match this patterns, the Template Resolver will return null.
     * </p>
     * <p>
     *   This allows for a fast discard of those templates that the developer might
     *   know for sure that will not be resolvable by the Resource Resolver used by
     *   this Template Resolver, so that an execution of the resource resolver is not needed.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getResolvablePatternSpec()}.getPatterns()
     * </p>
     * 
     * @return the pattern spec
     */
    public final Set<String> getResolvablePatterns() {
        return this.resolvablePatternSpec.getPatterns();
    }

    /**
     * <p>
     *   Sets the new <i>patterns</i> to be applied for establishing which
     *   templates can be resolved by this template resolver. For those templates
     *   which names do not match this patterns, the Template Resolver will return null.
     * </p>
     * <p>
     *   This allows for a fast discard of those templates that the developer might
     *   know for sure that will not be resolvable by the Resource Resolver used by
     *   this Template Resolver, so that an execution of the resource resolver is not needed.
     * </p>
     * <p>
     *   This is a convenience method equivalent to {@link #getResolvablePatternSpec()}.setPatterns(Set&lt;String&gt;)
     * </p>
     * 
     * @param resolvablePatterns the new patterns
     */
    public final void setResolvablePatterns(final Set<String> resolvablePatterns) {
        this.resolvablePatternSpec.setPatterns(resolvablePatterns);
    }



    /**
     * <p>
     *   Returns whether template resources will be checked for existence before being returned or not.
     * </p>
     * <p>
     *   Default value is <tt>FALSE</tt>.
     * </p>
     * <p>
     *   Checking resources for existence will make the template resolver execute {@link ITemplateResource#exists()}
     *   for each resolved resource before returning a {@link TemplateResolution}, returning <tt>null</tt> if the
     *   resource does not exist.
     * </p>
     * <p>
     *   This allows resolvers to pass control to the next {@link ITemplateResolver} in
     *   the chain based on real resource existence and not only on the matching performed by the <em>resolvable
     *   patterns</em> specified at {@link #getResolvablePatterns()}. But at the same time, might pose a performance
     *   issue on certain scenarios (e.g. HTTP URL resolution) that require actually accessing the resource in order
     *   to determine its existence, being the resource accessed twice in those cases (once for determining its
     *   existence, another time for reading it).
     * </p>
     * <p>
     *   If this <em>existence check</em> is enabled and a resource is determined to not exist,
     *   {@link ITemplateResolver#resolveTemplate(IEngineConfiguration, String, String, Map)} will return <tt>null</tt>.
     * </p>
     *
     * @return <tt>true</tt> if resource existence will be checked, <tt>false</tt> if not
     *
     * @since 3.0.0
     *
     */
    public final boolean getCheckExistence() {
        return this.checkExistence;
    }


    /**
     * <p>
     *   Sets whether template resources will be checked for existence before being returned or not.
     * </p>
     * <p>
     *   Default value is <tt>FALSE</tt>.
     * </p>
     * <p>
     *   Checking resources for existence will make the template resolver execute {@link ITemplateResource#exists()}
     *   for each resolved resource before returning a {@link TemplateResolution}, returning <tt>null</tt> if the
     *   resource does not exist.
     * </p>
     * <p>
     *   This allows resolvers to pass control to the next {@link ITemplateResolver} in
     *   the chain based on real resource existence and not only on the matching performed by the <em>resolvable
     *   patterns</em> specified at {@link #getResolvablePatterns()}. But at the same time, might pose a performance
     *   issue on certain scenarios (e.g. HTTP URL resolution) that require actually accessing the resource in order
     *   to determine its existence, being the resource accessed twice in those cases (once for determining its
     *   existence, another time for reading it).
     * </p>
     * <p>
     *   If this <em>existence check</em> is enabled and a resource is determined to not exist,
     *   {@link ITemplateResolver#resolveTemplate(IEngineConfiguration, String, String, Map)} will return <tt>null</tt>.
     * </p>
     *
     * @param checkExistence <tt>true</tt> if resource existence should be checked, <tt>false</tt> if not
     *
     * @since 3.0.0
     *
     */
    public final void setCheckExistence(final boolean checkExistence) {
        this.checkExistence = checkExistence;
    }


    public final TemplateResolution resolveTemplate(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template,
            final Map<String, Object> templateResolutionAttributes) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        // ownerTemplate CAN be null
        Validate.notNull(template, "Template Name cannot be null");
        // templateResolutionAttributes CAN be null

        if (!computeResolvable(configuration, ownerTemplate, template, templateResolutionAttributes)) {
            return null;
        }

        final ITemplateResource templateResource = computeTemplateResource(configuration, ownerTemplate, template, templateResolutionAttributes);
        if (templateResource == null) {
            return null;
        }

        if (this.checkExistence && !templateResource.exists()) { // will only check if flag set to true
            return null;
        }

        return new TemplateResolution(
                templateResource,
                this.checkExistence,
                computeTemplateMode(configuration, ownerTemplate, template, templateResolutionAttributes),
                computeValidity(configuration, ownerTemplate, template, templateResolutionAttributes));
        
    }
    
    
    
    
    /**
     * <p>
     *   Computes whether a template can be resolved by this resolver or not, 
     *   applying the corresponding patterns. Meant only for use or override by subclasses.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template to be resolved (usually its name).
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     *
     * @return whether the template is resolvable or not.
     */
    protected boolean computeResolvable(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes) {
        if (this.resolvablePatternSpec.isEmpty()) {
            return true;
        }
        return this.resolvablePatternSpec.matches(template);
    }




    
    
    /**
     * <p>
     *   Computes the resolved template resource.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template to be resolved (usually its name).
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     *
     * @return the template resource, or null if this template cannot be resolved (or the resource does not exist).
     */
    protected abstract ITemplateResource computeTemplateResource(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes);

    
    
    /**
     * <p>
     *   Computes the template mode that should be applied to a template, according
     *   to existing configuration.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template to be resolved (usually its name).
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     *
     * @return the template mode proposed by the template resolver for the resolved template.
     */
    protected abstract TemplateMode computeTemplateMode(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes);
    
    
    
    /**
     * <p>
     *   Computes the validity to be applied to the template resolution. This
     *   includes determining whether the template can be cached or not, and
     *   also in what circumstances (for instance, for how much time) can
     *   its cache entry be considered valid.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param ownerTemplate the owner template, if the resource being computed is a fragment. Might be null.
     * @param template the template to be resolved (usually its name).
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     * @return the validity
     */
    protected abstract ICacheEntryValidity computeValidity(final IEngineConfiguration configuration, final String ownerTemplate, final String template, final Map<String, Object> templateResolutionAttributes);
    
    
    
}

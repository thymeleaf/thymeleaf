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

import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.context.IContext;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.PatternSpec;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Convenience base class for all Template Resolvers.
 * </p>
 * <p>
 *   This class allows configuration of:
 * </p>
 * <ul>
 *   <li>Template Resolver name</li>
 *   <li>Template Resolver order (in chain)</li>
 *   <li>Template Resolver applicability patterns</li>
 * </ul>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public abstract class AbstractTemplateResolver 
        implements ITemplateResolver {

    
    private String name = this.getClass().getName();
    private Integer order = null;

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
    public String getName() {
        return this.name;
    }


    /**
     * <p>
     *   Sets a new name for the Template Resolver.
     * </p>
     * 
     * @param name the new name
     */
    public void setName(final String name) {
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
    public Integer getOrder() {
        return this.order;
    }


    /**
     * <p>
     *   Sets a new order for the template engine in the chain. Order should start with 1.
     * </p>
     * 
     * @param order the new order.
     */
    public void setOrder(final Integer order) {
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
    public PatternSpec getResolvablePatternSpec() {
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
    public Set<String> getResolvablePatterns() {
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
    public void setResolvablePatterns(final Set<String> resolvablePatterns) {
        this.resolvablePatternSpec.setPatterns(resolvablePatterns);
    }
    
    
    
    
    
    
    
    
    public TemplateResolution resolveTemplate(
            final IEngineConfiguration configuration, final IContext context, final String templateName) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(templateName, "Template Name cannot be null");
        Validate.notNull(context, "Context cannot be null");

        if (!computeResolvable(configuration, context, templateName)) {
            return null;
        }
        
        return new TemplateResolution(
                templateName,
                computeResourceName(configuration, context, templateName),
                computeResourceResolver(configuration, context, templateName),
                computeCharacterEncoding(configuration, context, templateName),
                computeTemplateMode(configuration, context, templateName),
                computeValidity(configuration, context, templateName));
        
    }
    
    
    
    
    /**
     * <p>
     *   Computes whether a template can be resolved by this resolver or not, 
     *   applying the corresponding patterns. <b>Meant only for use by subclasses</b>. 
     * </p>
     *
     * @param configuration the engine configuration.
     * @param context the context being applied to the template execution.
     * @param templateName the name of the template to be resolved.
     * @return whether the template is resolvable or not
     */
    protected boolean computeResolvable(
            final IEngineConfiguration configuration, final IContext context, final String templateName) {
        if (this.resolvablePatternSpec.getPatterns().isEmpty()) {
            return true;
        }
        return this.resolvablePatternSpec.matches(templateName);
    }
    
    
    
    
    /**
     * <p>
     *   Computes the resource name from the template name, applying aliases,
     *   prefix/suffix, or any other artifacts the Template Resolver might need
     *   to apply. 
     * </p>
     *
     * @param configuration the engine configuration.
     * @param context the context being applied to the template execution.
     * @param templateName the name of the template to be resolved.
     * @return the resource name
     */
    protected abstract String computeResourceName(
            final IEngineConfiguration configuration, final IContext context, final String templateName);

    
    
    /**
     * <p>
     *   Computes the resource resolver that should be applied to a template, according
     *   to existing configuration.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param context the context being applied to the template execution.
     * @param templateName the name of the template to be resolved.
     * @return the resource resolver to be applied
     */
    protected abstract IResourceResolver computeResourceResolver(
            final IEngineConfiguration configuration, final IContext context, final String templateName);

    
    
    /**
     * <p>
     *   Computes the character encoding that should be applied when reading template resource, according
     *   to existing configuration.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param context the context being applied to the template execution.
     * @param templateName the name of the template to be resolved.
     * @return the resource resolver to be applied
     */
    protected abstract String computeCharacterEncoding(
            final IEngineConfiguration configuration, final IContext context, final String templateName);

    
    
    /**
     * <p>
     *   Computes the template mode that should be applied to a template, according
     *   to existing configuration.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param context the context being applied to the template execution.
     * @param templateName the name of the template to be resolved.
     * @return the template mode to be applied
     */
    protected abstract TemplateMode computeTemplateMode(
            final IEngineConfiguration configuration, final IContext context, final String templateName);
    
    
    
    /**
     * <p>
     *   Computes the validity to be applied to the template resolution. This
     *   includes determining whether the template can be cached or not, and
     *   also in what circumstances (for instance, for how much time) can
     *   its cache entry be considered valid.
     * </p>
     *
     * @param configuration the engine configuration.
     * @param context the context being applied to the template execution.
     * @param templateName the name of the template to be resolved.
     * @return the validity
     */
    protected abstract ICacheEntryValidity computeValidity(
            final IEngineConfiguration configuration, final IContext context, final String templateName);
    
    
    
}

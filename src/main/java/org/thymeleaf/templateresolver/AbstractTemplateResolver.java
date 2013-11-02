/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.PatternSpec;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.AlreadyInitializedException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.resourceresolver.IResourceResolver;
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
 * @since 1.0
 *
 */
public abstract class AbstractTemplateResolver 
        implements ITemplateResolver {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateResolver.class);

    
    private String name = null;
    private Integer order = null;

    private final PatternSpec resolvablePatternSpec = new PatternSpec();
    

    private volatile boolean initialized;


    protected AbstractTemplateResolver() {
        super();
        this.initialized = false;
    }

    
    protected final boolean isInitialized() {
        return this.initialized;
    }

    
    /**
     * <p>
     *   Initialize this template resolver.
     * </p>
     * <p>
     *   Once initialized the configuration parameters of this template resolvers
     *   cannot be changed.
     * </p>
     * <p>
     *   Initialization is automatically triggered by the Template Engine before
     *   processing the first template.
     * </p>
     */
    public final synchronized void initialize() {
        
        if (!isInitialized()) {
            
            if (this.name == null) {
                this.name = this.getClass().getName();
            }
            
            logger.info("[THYMELEAF] INITIALIZING TEMPLATE RESOLVER: " + this.name);
            
            /*
             *  Initialize pattern specs to avoid further modifications
             */
            this.resolvablePatternSpec.initialize();
            
            initializeSpecific();
            
            this.initialized = true;

            logger.info("[THYMELEAF] TEMPLATE RESOLVER INITIALIZED OK");
            
        }
        
    }
    
    
    /**
     * <p>
     *   Initialize specific aspects of a subclass. This method is called during initialization
     *   of TemplateResolver ({@link #initialize()}) and is meant for being overridden by subclasses. 
     * </p>
     */
    protected void initializeSpecific() {
        // Nothing to be executed here. Meant for extension
    }
    

    /**
     * <p>
     *   Check the template resolver is not initialized, and throw an exception if it is.
     * </p>
     * <p>
     *   Calling this method allows to protect calls to methods that change the configuration,
     *   ensuring the template resolver has not been initialized yet.
     * </p>
     */
    protected final void checkNotInitialized() {
        if (isInitialized()) {
            throw new AlreadyInitializedException(
                    "Cannot modify template resolver when it has already been initialized");
        }
    }
    
    
    /**
     * <p>
     *   Check the template resolver is initialized, and throw an exception if it is not.
     * </p>
     * <p>
     *   Calling this method allows to protect calls to methods that need the template
     *   resolver to be already initialized.
     * </p>
     */
    protected final void checkInitialized() {
        if (!isInitialized()) {
            throw new NotInitializedException("Template Resolver has not been initialized");
        }
    }
    


    /**
     * <p>
     *   Returns the name of the template resolver
     * </p>
     * 
     * @return the name of the template resolver
     */
    public String getName() {
        checkInitialized();
        return this.name;
    }

    
    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the name
     */
    protected String unsafeGetName() {
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
        checkNotInitialized();
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
        checkInitialized();
        return this.order;
    }

    
    /**
     * <p>
     *   Unsynchronized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the order
     */
    protected Integer unsafeGetOrder() {
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
        checkNotInitialized();
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
        checkInitialized();
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
        checkInitialized();
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
     *   This is a convenience method equivalent to {@link #getResolvablePatternSpec()}.setPatterns(Set<String>)
     * </p>
     * 
     * @param resolvablePatterns the new patterns
     */
    public void setResolvablePatterns(final Set<String> resolvablePatterns) {
        checkNotInitialized();
        this.resolvablePatternSpec.setPatterns(resolvablePatterns);
    }
    
    
    
    
    
    
    
    
    public TemplateResolution resolveTemplate(final TemplateProcessingParameters templateProcessingParameters) {

        checkInitialized();
        
        Validate.notNull(templateProcessingParameters, "Template Processing Parameters cannot be null");
        
        if (!computeResolvable(templateProcessingParameters)) {
            return null;
        }
        
        return new TemplateResolution(
                templateProcessingParameters.getTemplateName(), 
                computeResourceName(templateProcessingParameters), 
                computeResourceResolver(templateProcessingParameters), 
                computeCharacterEncoding(templateProcessingParameters), 
                computeTemplateMode(templateProcessingParameters), 
                computeValidity(templateProcessingParameters));
        
    }
    
    
    
    
    /**
     * <p>
     *   Computes whether a template can be resolved by this resolver or not, 
     *   applying the corresponding patterns. <b>Meant only for use by subclasses</b>. 
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @return whether the template is resolvable or not
     */
    protected boolean computeResolvable(final TemplateProcessingParameters templateProcessingParameters) {
        if (this.resolvablePatternSpec.getPatterns().isEmpty()) {
            return true;
        }
        return this.resolvablePatternSpec.matches(templateProcessingParameters.getTemplateName());
    }
    
    
    
    
    /**
     * <p>
     *   Computes the resource name from the template name, applying aliases,
     *   prefix/suffix, or any other artifacts the Template Resolver might need
     *   to apply. 
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @return the resource name
     */
    protected abstract String computeResourceName(final TemplateProcessingParameters templateProcessingParameters);    

    
    
    /**
     * <p>
     *   Computes the resource resolver that should be applied to a template, according
     *   to existing configuration.
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @return the resource resolver to be applied
     */
    protected abstract IResourceResolver computeResourceResolver(final TemplateProcessingParameters templateProcessingParameters);

    
    
    /**
     * <p>
     *   Computes the character encoding that should be applied when reading template resource, according
     *   to existing configuration.
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @return the resource resolver to be applied
     */
    protected abstract String computeCharacterEncoding(final TemplateProcessingParameters templateProcessingParameters);

    
    
    /**
     * <p>
     *   Computes the template mode that should be applied to a template, according
     *   to existing configuration.
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @return the template mode to be applied
     */
    protected abstract String computeTemplateMode(final TemplateProcessingParameters templateProcessingParameters);
    
    
    
    /**
     * <p>
     *   Computes the validity to be applied to the template resolution. This
     *   includes determining whether the template can be cached or not, and
     *   also in what circumstances (for instance, for how much time) can
     *   its cache entry be considered valid.
     * </p>
     * 
     * @param templateProcessingParameters the template processing parameters
     * @return the validity
     */
    protected abstract ITemplateResolutionValidity computeValidity(final TemplateProcessingParameters templateProcessingParameters);
    
    
    
}

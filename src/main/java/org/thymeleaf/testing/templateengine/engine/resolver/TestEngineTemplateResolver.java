/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.engine.resolver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.PatternSpec;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.AlreadyInitializedException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.templateresolver.AlwaysValidTemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.NonCacheableTemplateResolutionValidity;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.Validate;







public class TestEngineTemplateResolver implements ITemplateResolver {

    public static final String TEST_TEMPLATE_CONVERSION_CHARSET = "UTF-8";
    
    private static final Logger logger = LoggerFactory.getLogger(TestEngineTemplateResolver.class);

    
    private String name = null;
    private Integer order = null;

    private final PatternSpec resolvablePatternSpec = new PatternSpec();
    

    private volatile boolean initialized;
    
                   
    public TestEngineTemplateResolver() {
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



    
    
    
    public TemplateResolution resolveTemplate(
            final TemplateProcessingParameters templateProcessingParameters) {

        checkInitialized();
        
        Validate.notNull(templateProcessingParameters, "Template Processing Parameters cannot be null");

        final ITest test = TestExecutor.getThreadTest(); 
        final String testName = TestExecutor.getThreadTestName();

        // Check template mode
        final String templateMode = test.getTemplateMode();
        if (templateMode == null) {
            throw new TestEngineExecutionException(
                    "Template mode is null for test \"" + testName + "\", which is forbidden");
        }

        
        // Check input
        final ITestResource input = test.getInput();
        if (input == null) {
            throw new TestEngineExecutionException(
                    "Input is null for test \"" + testName + "\", which is forbidden");
        }
        
        // Organize inputs
        final Map<String,ITestResource> allInputs = new HashMap<String,ITestResource>();
        final Map<String,ITestResource> additionalInputs = test.getAdditionalInputs();
        if (additionalInputs != null) {
            allInputs.putAll(additionalInputs);
        }
        allInputs.put(testName, test.getInput());
        
        // The resource resolver is created instead of reusing one for concurrency reasons 
        final TestEngineResourceResolver resourceResolver = 
                new TestEngineResourceResolver(allInputs, TEST_TEMPLATE_CONVERSION_CHARSET);
        
        // Compute validity according to the "inputCacheable" property established at the test
        final ITemplateResolutionValidity validity =
                (test.isInputCacheable()? 
                        AlwaysValidTemplateResolutionValidity.INSTANCE :
                        NonCacheableTemplateResolutionValidity.INSTANCE);
                
        
        
        // Create the template resolution object with all the gathered info
        return new TemplateResolution(
                templateProcessingParameters.getTemplateName(), 
                templateProcessingParameters.getTemplateName(), 
                resourceResolver, 
                TEST_TEMPLATE_CONVERSION_CHARSET, 
                templateMode, 
                validity);
        
    }
    
    
}

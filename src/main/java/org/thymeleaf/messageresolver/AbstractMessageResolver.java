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
package org.thymeleaf.messageresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.exceptions.AlreadyInitializedException;
import org.thymeleaf.exceptions.NotInitializedException;

/**
 * <p>
 *   Base abstract implementation for message resolvers implementing the 
 *   {@link IMessageResolver} interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractMessageResolver
        implements IMessageResolver {

    
    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageResolver.class);

    private String name = null;
    private Integer order = null;
    
    private volatile boolean initialized;
    
    
    
    
    protected AbstractMessageResolver() {
        super();
        this.initialized = false;
    }

    
    
    
    protected final boolean isInitialized() {
        return this.initialized;
    }
    
    public final synchronized void initialize() {
        
        if (!isInitialized()) {
            
            if (this.name == null) {
                this.name = this.getClass().getName();
            }
            
            logger.info("[THYMELEAF] INITIALIZING MESSAGE RESOLVER: " + this.name);
            
            initializeSpecific();
            
            this.initialized = true;

            logger.info("[THYMELEAF] MESSAGE RESOLVER INITIALIZED OK");
            
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
     *   Check the message resolver is not initialized, and throw an exception if it is.
     * </p>
     * <p>
     *   Calling this method allows to protect calls to methods that change the configuration,
     *   ensuring the message resolver has not been initialized yet.
     * </p>
     */
    protected final void checkNotInitialized() {
        if (isInitialized()) {
            throw new AlreadyInitializedException(
                    "Cannot modify message resolver when it has already been initialized");
        }
    }
    
    
    /**
     * <p>
     *   Check the message resolver is initialized, and throw an exception if it is not.
     * </p>
     * <p>
     *   Calling this method allows to protect calls to methods that need the message
     *   resolver to be already initialized.
     * </p>
     */
    protected final void checkInitialized() {
        if (!isInitialized()) {
            throw new NotInitializedException("Message Resolver has not been initialized");
        }
    }
    


    public final String getName() {
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
    protected final String unsafeGetName() {
        return this.name;
    }


    /**
     * <p>
     *   Sets a name for this message resolver.
     * </p>
     * 
     * @param name the new name
     */
    public void setName(final String name) {
        checkNotInitialized();
        this.name = name;
    }
    
    
    public final Integer getOrder() {
        checkInitialized();
        return this.order;
    }

    
    /**
     * <p>
     *   Uninitialized method <b>meant only for use by subclasses</b>. 
     * </p>
     * 
     * @return the order
     */
    protected final Integer unsafeGetOrder() {
        return this.order;
    }


    /**
     * <p>
     *   Sets a new order for the message resolver. 
     * </p>
     * 
     * @param order the new order
     */
    public void setOrder(final Integer order) {
        checkNotInitialized();
        this.order = order;
    }

    
}

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
package org.thymeleaf.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * <p>
 *   Represents a limited set or pool of resources, which needs to be used with
 *   exclusive ownership.
 * </p>
 * 
 * <p>
 *   Should be used in a similar fashion to the following so that resources
 *   are always released properly:
 * </p>
 * 
 * <pre>
 *   MyResource resource = myLimitedResource.allocate();
 *   try {
 *       // actual code using the resource
 *   } finally {
 *       myLimitedResource.release(resource);
 *   }
 * </pre>
 * 
 * @since 2.0.0
 * 
 * @author Guven Demir
 * 
 */
public final class ResourcePool<T> {
    
    private final LinkedList<T> pool;
    private final Set<T> allocated;
    private final Semaphore semaphore;
    
    private final IResourceFactory<T> resourceFactory;

    
    
    public ResourcePool(final Collection<T> resources) {
        super();
        Validate.notNull(resources, "Resources for pool cannot be null");
        this.pool = new LinkedList<T>(resources);
        this.allocated = new HashSet<T>(this.pool.size() + 1, 1.0f);
        this.semaphore = new Semaphore(this.pool.size());
        this.resourceFactory = null;
    }
    

    public ResourcePool(final IResourceFactory<T> resourceFactory, final int poolSize) {
        super();
        Validate.notNull(resourceFactory, "Resource factory for pool cannot be null");
        this.resourceFactory = resourceFactory;
        this.pool = new LinkedList<T>();
        for (int i = 0; i < poolSize; i++) {
            final T resource = this.resourceFactory.createResource();
            if (resource == null) {
                throw new IllegalStateException(
                        "Resource created by factory \"" + this.resourceFactory.getClass().getName() + "\"returned null");
            }
            this.pool.add(resource);
        }
        this.allocated = new HashSet<T>(this.pool.size() + 1, 1.0f);
        this.semaphore = new Semaphore(this.pool.size());
    }

    
    
    /**
     * <p>
     *   Allocates and returns a resource from the pool.
     * </p>
     * 
     * <p>
     *   Blocks until a resource is available when a resource is not
     *   available immediately.
     * </p>
     * 
     * @return the allocated resource, heving been removed from the allocation pool.
     */
    public T allocate() {
        
        try {
            
            this.semaphore.acquire();
            
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        synchronized(this) {
            final T object = this.pool.removeFirst();
            this.allocated.add(object);
            return object;
        }
        
    }
    

    
    /**
     * <p>
     *   Releases a previously allocated resource.
     * </p>
     * 
     * <p>
     *   Might also be used to introduce new resources, e.g. in place of
     *   a broken resource.
     * </p>
     * 
     * @param resource the resource to be released and returned to the pool.
     * 
     */
    public void release(final T resource) {
        
        synchronized(this) {
            if (this.allocated.contains(resource)) {
                this.pool.addLast(resource);
                this.allocated.remove(resource);
            }
        }
        
        this.semaphore.release();
        
    }
    

    
    /**
     * <p>
     *   Discards an allocated resource and forces the resource
     *   factory to create a new one, if a resource factory has been
     *   specified during pool instantiation.
     * </p>
     * <p>
     *   If a resource factory has not been specified, this method will
     *   raise an <tt>IllegalStateException</tt>.
     * </p>
     * 
     * @param resource the resource to be discarded and substituted with a
     *        new one.
     * @since 2.0.7
     * 
     */
    public void discardAndReplace(final T resource) {
        
        if (this.resourceFactory == null) {
            throw new IllegalStateException(
                    "Cannot execute 'discardAndReplace' operation: no resource " +
                    "factory has been set.");
        }
        
        synchronized(this) {
            if (this.allocated.contains(resource)) {
                final T newResource = this.resourceFactory.createResource();
                this.pool.addLast(newResource);
                this.allocated.remove(resource);
            }
        }
        
        this.semaphore.release();
        
    }
    
    
    
    
    /**
     * <i>
     *   Objects implementing this interface are in charge of 
     *   creating the resources that conform the pool.
     * </i>
     * 
     * @author Daniel Fern&aacute;ndez
     * @since 2.0.6
     *
     */
    public static interface IResourceFactory<T> {
        
        public T createResource();
        
    }

    
    
}

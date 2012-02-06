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
    
    
    public ResourcePool(final Collection<T> resources) {
        super();
        this.pool = new LinkedList<T>(resources);
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
    
}

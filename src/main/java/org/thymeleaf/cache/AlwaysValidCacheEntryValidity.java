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
package org.thymeleaf.cache;

/**
 * <p>
 *   Simple implementation of {@link ICacheEntryValidity}
 *   that considers the template resolution to be always cacheable
 *   and always valid.
 * </p>
 * <p>
 *   This means that a cache entry for this template resolution would
 *   only be evicted by the effect of LRU (being the least-recently
 *   used entry).
 * </p>
 * 
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class AlwaysValidCacheEntryValidity
        implements ICacheEntryValidity {

    /**
     * <p>
     *   Singleton instance. Meant to avoid creating too many objects of this class.
     * </p>
     */
    public static final AlwaysValidCacheEntryValidity INSTANCE =
        new AlwaysValidCacheEntryValidity();

    
    public AlwaysValidCacheEntryValidity() {
        super();
    }


    /**
     * <p>
     *   Returns true. Templates are always considered cacheable using this
     *   validity implementation.
     * </p>
     * 
     * @return true
     */
    public boolean isCacheable() {
        return true;
    }
    

    /**
     * <p>
     *   Returns true. Template cache entries using this validity are always
     *   considered valid, and thus only evicted from cache by LRU.
     * </p>
     * 
     * @return true
     */
    public boolean isCacheStillValid() {
        return true;
    }
    
}

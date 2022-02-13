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
 *   that considers the template resolution to be non-cacheable.
 * </p>
 * 
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class NonCacheableCacheEntryValidity
        implements ICacheEntryValidity {

    
    /**
     * <p>
     *   Singleton instance. Meant to avoid creating too many objects of this class.
     * </p>
     */
    public static final NonCacheableCacheEntryValidity INSTANCE =
        new NonCacheableCacheEntryValidity();
    
    public NonCacheableCacheEntryValidity() {
        super();
    }


    /**
     * <p>
     *   Returns false. Template Resolutions using this validity are always 
     *   considered to be non-cacheable.
     * </p>
     * 
     * @return false
     */
    public boolean isCacheable() {
        return false;
    }
    

    /**
     * <p>
     *   This method will never be called, because templates using this
     *   validity implementation are always considered to be non-cacheable.
     * </p>
     * 
     * @return false
     */
    public boolean isCacheStillValid() {
        return false;
    }
    
}

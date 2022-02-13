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
 *   Common interface for all objects defining the validity of
 *   a template resolution.
 * </p>
 * <p>
 *   These objects are queried by the Template Cache for knowing
 *   whether a template is cacheable or not, and also for determining
 *   if an existing cache entry is still valid.
 * </p>
 * <p>
 *   A typical implementation is {@link TTLCacheEntryValidity},
 *   which determines the validity of a cache entry based on a TTL (time-to-live).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface ICacheEntryValidity {

    /**
     * <p>
     *   Returns whether the template resolution can be included into the cache
     *   or not.
     * </p>
     * 
     * @return {@code true} if the parsed template can be included into the cache,
     *         {@code false} if not.
     */
    public boolean isCacheable();

    
    /**
     * <p>
     *   Returns whether this template resolution is still valid or not (and therefore
     *   its corresponding cache entry. Will only be ever called if 
     *   {@link #isCacheable()} returns true.
     * </p>
     * <p>
     *   This method will be called by the Parsed Template Cache before returning
     *   a cache entry, so that it can be invalidated if needed, and so trigger a new
     *   template resolution operation.
     * </p>
     * 
     * @return whether the template resolution can be still considered valid or not.
     */
    public boolean isCacheStillValid();
    
}

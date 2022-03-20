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


import java.util.Set;

/**
 * <p>
 *   Common interface for all the cache objects used by the template engine.
 * </p>
 * <p>
 *   This is the interface that must be implemented by all cache objects managed
 *   by {@link ICacheManager} implementations.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 * @param <K> the type of the cache keys
 * @param <V> the type of the cache values
 */
public interface ICache<K, V> {

    /**
     * <p>
     *   Insert a new value into the cache.
     * </p>
     * 
     * @param key the key of the new entry
     * @param value the value to be cached
     */
    public void put(final K key, final V value);
    
    /**
     * <p>
     *   Retrieve a value from the cache.
     * </p>
     * 
     * @param key the key of the value to be retrieved
     * @return the retrieved value, or null if no value exists for the specified key.
     */
    public V get(final K key);
    
    /**
     * <p>
     *   Retrieve a value from the cache, using the specified validity checker
     *   to ensure the entry is still valid. If the cache already has a default validity
     *   checker, this method should override this setting and use the one specified
     *   instead.
     * </p>
     * 
     * @param key the key of the value to be retrieved
     * @param validityChecker the validity checker to be used to ensure the entry is still valid.
     * @return the retrieved value, or null if no value exists for the specified key.
     */
    public V get(final K key, final ICacheEntryValidityChecker<? super K, ? super V> validityChecker);
    
    /**
     * <p>
     *   Clear the entire cache.   
     * </p>
     */
    public void clear();
    
    /**
     * <p>
     *   Clears a specific entry in the cache.
     * </p>
     * 
     * @param key the key of the entry to be cleared.
     */
    public void clearKey(final K key);

    /**
     * <p>
     *   Returns all the keys contained in this cache. Note this method might return keys for entries
     *   that are already invalid, so the result of calling {@link #get(Object)} for these keys might
     *   be {@code null}.
     * </p>
     *
     * @return the complete set of cache keys. Might include keys for already-invalid (non-cleaned) entries.
     * @since 3.0.0
     */
    public Set<K> keySet();

}

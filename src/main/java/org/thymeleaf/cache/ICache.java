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
package org.thymeleaf.cache;



public interface ICache<K, V> {

    public void put(final K key, final V value);
    public V get(final K key);
    public V get(final K key, final ICacheEntryValidityChecker<? super K, ? super V> validityChecker);
    public void clear();
    public void clearKey(final K key);

}

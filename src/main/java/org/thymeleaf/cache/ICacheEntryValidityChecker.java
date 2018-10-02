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

import java.io.Serializable;

/**
 * <p>
 *   Defines the logic needed to (optionally) validate an entry living in an
 *   {@link ICache} object before returning it as the result of a {@code get}
 *   operation. If not valid, the entry will be removed
 *   from the cache (and null will be returned).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 * @param <K> The type of the cache map keys
 * @param <V> The type of the cache map values
 */
public interface ICacheEntryValidityChecker<K,V> extends Serializable {

    public boolean checkIsValueStillValid(final K key, final V value, final long entryCreationTimestamp);

}
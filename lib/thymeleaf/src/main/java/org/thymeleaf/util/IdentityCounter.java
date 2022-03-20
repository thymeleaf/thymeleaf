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
package org.thymeleaf.util;

import java.io.Serializable;
import java.util.IdentityHashMap;


/**
 * <p>
 *   Purpose-specific set that can tell whether an object (by reference)
 *   has already been added to it.
 * </p>
 * <p>
 *   Objects of this class are <b>not thread-safe</b>.
 * </p>
 *
 * @author Rustam Miftakhutdinov
 *
 * @since 2.0.0
 *
 * @param <T> The type of the objects being counted
 */
public final class IdentityCounter<T> implements Serializable {

    private static final long serialVersionUID = -6965348731301112911L;

    private final IdentityHashMap<T, Object> counted;

    public IdentityCounter(final int expectedMaxSize) {
        super();
        this.counted = new IdentityHashMap<T, Object>(expectedMaxSize);
    }

    public void count(final T object) {
        this.counted.put(object, null);
    }

    public boolean isAlreadyCounted(final T object) {
        return this.counted.containsKey(object);
    }

}

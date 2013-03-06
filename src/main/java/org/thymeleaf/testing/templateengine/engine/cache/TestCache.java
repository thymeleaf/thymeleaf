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
package org.thymeleaf.testing.templateengine.engine.cache;

import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidityChecker;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;







public final class TestCache<V> implements ICache<String,V> {

    
    private final ICache<String,V> cache;
    

    public TestCache(final ICache<String,V> cache) {
        super();
        this.cache = cache;
    }

    
    
    
    private static String prefix(final String key) {
        final String testName = TestExecutor.getThreadTestName();
        return testName + "_" + key;
    }
    
    
    
    
    public void put(final String key, final V value) {
        this.cache.put(prefix(key), value);
    }

    public V get(final String key) {
        return this.cache.get(prefix(key));
    }

    public V get(final String key,
            final ICacheEntryValidityChecker<? super String, ? super V> validityChecker) {
        return this.cache.get(prefix(key), validityChecker);
    }

    public void clear() {
        this.cache.clear();
    }

    public void clearKey(final String key) {
        this.cache.clearKey(prefix(key));
    }

    
    
    
}

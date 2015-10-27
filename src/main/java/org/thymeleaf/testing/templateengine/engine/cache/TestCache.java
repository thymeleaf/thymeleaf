/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Set;

import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidityChecker;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;







public final class TestCache<K,V> implements ICache<K,V> {

    
    private final ICache<K,V> cache;
    

    public TestCache(final ICache<K,V> cache) {
        super();
        this.cache = cache;
    }

    
    
    
    private Object prefix(final K key) {
        final String testName = TestExecutor.getThreadTestName();
        if (key instanceof String) {
            return testName + "_" + key;
        }
        if (key instanceof TemplateCacheKey) {
            final TemplateCacheKey cacheKey = (TemplateCacheKey)key;
            return new TemplateCacheKey(
                    cacheKey.getOwnerTemplate(), testName + "_" + cacheKey.getTemplate(), cacheKey.getTemplateSelectors(),
                    cacheKey.getLineOffset(), cacheKey.getColOffset(),
                    cacheKey.getTemplateMode(), cacheKey.getTemplateResolutionAttributes());
        }
        if (key instanceof ExpressionCacheKey) {
            final ExpressionCacheKey cacheKey = (ExpressionCacheKey)key;
            return new ExpressionCacheKey(testName + "_" + cacheKey.getType(), cacheKey.getExpression());
        }
        return key;
    }
    
    
    
    
    public void put(final K key, final V value) {
        this.cache.put((K)prefix(key), value);
    }

    public V get(final K key) {
        return this.cache.get((K)prefix(key));
    }

    public V get(final K key,
            final ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {
        return this.cache.get((K)prefix(key), validityChecker);
    }

    public void clear() {
        this.cache.clear();
    }

    public void clearKey(final K key) {
        this.cache.clearKey((K)prefix(key));
    }

    public Set<K> keySet() {
        return this.cache.keySet();
    }


}

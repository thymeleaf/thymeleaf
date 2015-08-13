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

import java.util.List;
import java.util.Properties;

import org.thymeleaf.cache.FragmentCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.StandardCacheManager;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.engine.ParsedFragmentMarkup;
import org.thymeleaf.engine.ParsedTemplateMarkup;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;







public final class TestCacheManager implements ICacheManager {

    
    private final StandardCacheManager cacheManager;
    
    
    public TestCacheManager() {
        super();
        this.cacheManager = new StandardCacheManager();
    }


    public ICache<TemplateCacheKey, ParsedTemplateMarkup> getTemplateCache() {
        return new TestCache<TemplateCacheKey, ParsedTemplateMarkup>(this.cacheManager.getTemplateCache());
    }


    public ICache<FragmentCacheKey, ParsedFragmentMarkup> getFragmentCache() {
        return new TestCache<FragmentCacheKey, ParsedFragmentMarkup>(this.cacheManager.getFragmentCache());
    }


    public ICache<String, Properties> getMessageCache() {
        return new TestCache<String, Properties>(this.cacheManager.getMessageCache());
    }


    public ICache<String, Object> getExpressionCache() {
        return new TestCache<String, Object>(this.cacheManager.getExpressionCache());
    }


    @SuppressWarnings("unchecked")
    public <K, V> ICache<K, V> getSpecificCache(final String name) {
        final String testName = TestExecutor.getThreadTestName();
        final ICache<K,V> specificCache = this.cacheManager.getSpecificCache(testName);
        if (specificCache == null) {
            return null;
        }
        return new TestCache<K,V>(specificCache);
    }


    public List<String> getAllSpecificCacheNames() {
        return this.cacheManager.getAllSpecificCacheNames();
    }


    public void clearAllCaches() {
        this.cacheManager.clearAllCaches();
    }
    
    
    
    
}

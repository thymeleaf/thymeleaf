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
package org.thymeleaf.cache;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.thymeleaf.engine.ParsedFragmentMarkup;
import org.thymeleaf.engine.ParsedTemplateMarkup;


/**
 * <p>
 *   Common abstract class for {@link ICacheManager} implementations, useful
 *   for taking care of the lazy initialization of cache objects when their
 *   corresponding <tt>getXCache()</tt> methods are called.
 * </p>
 * 
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0 (reimplemented in 3.0.0)
 *
 */
public abstract class AbstractCacheManager implements ICacheManager {


    private volatile ICache<TemplateCacheKey,ParsedTemplateMarkup> templateCache;
    private volatile boolean templateCacheInitialized = false;
    
    private volatile ICache<FragmentCacheKey,ParsedFragmentMarkup> fragmentCache;
    private volatile boolean fragmentCacheInitialized = false;
    
    private volatile ICache<String,Object> expressionCache;
    private volatile boolean expressionCacheInitialized = false;
    
    private volatile ICache<String,Properties> messageCache;
    private volatile boolean messageCacheInitialized = false;

    
    protected AbstractCacheManager() {
        super();
    }
    
    
    public final ICache<TemplateCacheKey, ParsedTemplateMarkup> getTemplateCache() {
        if (!this.templateCacheInitialized) {
            synchronized(this) {
                if (!this.templateCacheInitialized) {
                    this.templateCache = initializeTemplateCache();
                    this.templateCacheInitialized = true;
                }
            }
        }
        return this.templateCache;
    }

    
    public final ICache<FragmentCacheKey, ParsedFragmentMarkup> getFragmentCache() {
        if (!this.fragmentCacheInitialized) {
            synchronized(this) {
                if (!this.fragmentCacheInitialized) {
                    this.fragmentCache = initializeFragmentCache();
                    this.fragmentCacheInitialized = true;
                }
            }
        }
        return this.fragmentCache;
    }

    
    public final ICache<String, Properties> getMessageCache() {
        if (!this.messageCacheInitialized) {
            synchronized(this) {
                if (!this.messageCacheInitialized) {
                    this.messageCache = initializeMessageCache();
                    this.messageCacheInitialized = true;
                }
            }
        }
        return this.messageCache;
    }

    public final ICache<String, Object> getExpressionCache() {
        if (!this.expressionCacheInitialized) {
            synchronized(this) {
                if (!this.expressionCacheInitialized) {
                    this.expressionCache = initializeExpressionCache();
                    this.expressionCacheInitialized = true;
                }
            }
        }
        return this.expressionCache;
    }

    
    public <K, V> ICache<K, V> getSpecificCache(final String name) {
        // No specific caches are used by default
        return null;
    }


    public List<String> getAllSpecificCacheNames() {
        // No specific caches are used by default
        return Collections.emptyList();
    }


    public void clearAllCaches() {

        final ICache<TemplateCacheKey, ParsedTemplateMarkup> templateCacheObj = getTemplateCache();
        if (templateCacheObj != null) {
            templateCacheObj.clear();
        }
        
        final ICache<FragmentCacheKey, ParsedFragmentMarkup> fragmentCacheObj = getFragmentCache();
        if (fragmentCacheObj != null) {
            fragmentCacheObj.clear();
        }
        
        final ICache<String, Properties> messageCacheObj = getMessageCache();
        if (messageCacheObj != null) {
            messageCacheObj.clear();
        }
        
        final ICache<String, Object> expressionCacheObj = getExpressionCache();
        if (expressionCacheObj != null) {
            expressionCacheObj.clear();
        }
        
        final List<String> allSpecificCacheNamesObj = getAllSpecificCacheNames();
        if (allSpecificCacheNamesObj != null) {
            for (final String specificCacheName : allSpecificCacheNamesObj) {
                final ICache<?,?> specificCache = getSpecificCache(specificCacheName);
                if (specificCache != null) {
                    specificCache.clear();
                }
            }
        }
        
    }


    protected abstract ICache<TemplateCacheKey,ParsedTemplateMarkup> initializeTemplateCache();

    protected abstract ICache<FragmentCacheKey,ParsedFragmentMarkup> initializeFragmentCache();
    
    protected abstract ICache<String,Properties> initializeMessageCache();
    
    protected abstract ICache<String,Object> initializeExpressionCache();
    
}

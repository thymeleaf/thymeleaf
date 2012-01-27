/*
 * =============================================================================
 *
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.List;
import java.util.Properties;

import org.thymeleaf.Template;
import org.thymeleaf.dom.Node;







/**
 * 
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0
 *
 */
public abstract class AbstractCacheManager implements ICacheManager {


    private final ICache<String,Template> templateCache;
    private final ICache<String,List<Node>> fragmentCache;
    private final ICache<String,Object> expressionCache;
    private final ICache<String,Properties> messageCache;

    
    public AbstractCacheManager() {
        super();
        this.templateCache = initializeTemplateCache();
        this.fragmentCache = initializeFragmentCache();
        this.messageCache = initializeMessageCache();
        this.expressionCache = initializeExpressionCache();
    }
    
    
    public final ICache<String, Template> getTemplateCache() {
        return this.templateCache;
    }
    
    public final ICache<String, List<Node>> getFragmentCache() {
        return this.fragmentCache;
    }

    public final ICache<String, Properties> getMessageCache() {
        return this.messageCache;
    }

    public final ICache<String, Object> getExpressionCache() {
        return this.expressionCache;
    }

    public <K, V> ICache<K, V> getSpecificCache(String name) {
        // No specific caches are used by default
        return null;
    }


    protected abstract ICache<String,Template> initializeTemplateCache();

    protected abstract ICache<String,List<Node>> initializeFragmentCache();
    
    protected abstract ICache<String,Properties> initializeMessageCache();
    
    protected abstract ICache<String,Object> initializeExpressionCache();
    
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.Node;







/**
 * 
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0
 *
 */
public class StandardCacheManager extends AbstractCacheManager {
    
    public static final String DEFAULT_TEMPLATE_CACHE_NAME = "TEMPLATE_CACHE";
    public static final int DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE = 10;
    public static final int DEFAULT_TEMPLATE_CACHE_MAX_SIZE = 50;
    public static final boolean DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES = true;
    
    public static final String DEFAULT_FRAGMENT_CACHE_NAME = "FRAGMENT_CACHE";
    public static final int DEFAULT_FRAGMENT_CACHE_INITIAL_SIZE = 50;
    public static final int DEFAULT_FRAGMENT_CACHE_MAX_SIZE = 300;
    public static final boolean DEFAULT_FRAGMENT_CACHE_USE_SOFT_REFERENCES = true;
    
    public static final String DEFAULT_MESSAGE_CACHE_NAME = "MESSAGE_CACHE";
    public static final int DEFAULT_MESSAGE_CACHE_INITIAL_SIZE = 50;
    public static final int DEFAULT_MESSAGE_CACHE_MAX_SIZE = 300;
    public static final boolean DEFAULT_MESSAGE_CACHE_USE_SOFT_REFERENCES = true;
    
    public static final String DEFAULT_EXPRESSION_CACHE_NAME = "EXPRESSION_CACHE";
    public static final int DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE = 300;
    public static final int DEFAULT_EXPRESSION_CACHE_MAX_SIZE = 800;
    public static final boolean DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES = true;
        
    
    
    public StandardCacheManager() {
        super();
    }

    
    
    @Override
    protected final ICache<String, Template> initializeTemplateCache() {
        return new StandardCache<String, Template>(
                getTemplateCacheName(), getTemplateCacheUseSoftReferences(), 
                getTemplateCacheInitialSize(), getTemplateCacheMaxSize(), 
                getTemplateValidityChecker(), getTemplateCacheLogger());
    }
    
    @Override
    protected final ICache<String, List<Node>> initializeFragmentCache() {
        return new StandardCache<String, List<Node>>(
                getFragmentCacheName(), getFragmentCacheUseSoftReferences(), 
                getFragmentCacheInitialSize(), getFragmentCacheMaxSize(), 
                getFragmentValidityChecker(), getFragmentCacheLogger());
    }

    
    @Override
    protected final ICache<String, Properties> initializeMessageCache() {
        return new StandardCache<String, Properties>(
                getMessageCacheName(), getMessageCacheUseSoftReferences(), 
                getMessageCacheInitialSize(), getMessageCacheMaxSize(), 
                getMessageValidityChecker(), getMessageCacheLogger());
    }

    
    @Override
    protected final ICache<String, Object> initializeExpressionCache() {
        return new StandardCache<String, Object>(
                getExpressionCacheName(), getExpressionCacheUseSoftReferences(), 
                getExpressionCacheInitialSize(), getExpressionCacheMaxSize(), 
                getExpressionValidityChecker(), getExpressionCacheLogger());
    }
    
    
    
    
    protected String getTemplateCacheName() {
        return DEFAULT_TEMPLATE_CACHE_NAME;
    }
    
    protected boolean getTemplateCacheUseSoftReferences() {
        return DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES;
    }
    
    protected int getTemplateCacheInitialSize() {
        return DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE;
    }
    
    protected int getTemplateCacheMaxSize() {
        return DEFAULT_TEMPLATE_CACHE_MAX_SIZE;
    }
    
    protected Logger getTemplateCacheLogger() {
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getTemplateCacheName());
    }
    
    protected ICacheEntryValidityChecker<String,Template> getTemplateValidityChecker() {
        return new StandardParsedTemplateEntryValidator();
    }


    
    
    protected String getFragmentCacheName() {
        return DEFAULT_FRAGMENT_CACHE_NAME;
    }
    
    protected boolean getFragmentCacheUseSoftReferences() {
        return DEFAULT_FRAGMENT_CACHE_USE_SOFT_REFERENCES;
    }
    
    protected int getFragmentCacheInitialSize() {
        return DEFAULT_FRAGMENT_CACHE_INITIAL_SIZE;
    }
    
    protected int getFragmentCacheMaxSize() {
        return DEFAULT_FRAGMENT_CACHE_MAX_SIZE;
    }
    
    protected Logger getFragmentCacheLogger() {
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getFragmentCacheName());
    }
    
    protected ICacheEntryValidityChecker<String,List<Node>> getFragmentValidityChecker() {
        return null;
    }
    
    
    protected String getMessageCacheName() {
        return DEFAULT_MESSAGE_CACHE_NAME;
    }
    
    protected boolean getMessageCacheUseSoftReferences() {
        return DEFAULT_MESSAGE_CACHE_USE_SOFT_REFERENCES;
    }
    
    protected int getMessageCacheInitialSize() {
        return DEFAULT_MESSAGE_CACHE_INITIAL_SIZE;
    }
    
    protected int getMessageCacheMaxSize() {
        return DEFAULT_MESSAGE_CACHE_MAX_SIZE;
    }
    
    protected Logger getMessageCacheLogger() {
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getMessageCacheName());
    }
    
    protected ICacheEntryValidityChecker<String,Properties> getMessageValidityChecker() {
        return null;
    }

    
    
    protected String getExpressionCacheName() {
        return DEFAULT_EXPRESSION_CACHE_NAME;
    }
    
    protected boolean getExpressionCacheUseSoftReferences() {
        return DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES;
    }
    
    protected int getExpressionCacheInitialSize() {
        return DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE;
    }
    
    protected int getExpressionCacheMaxSize() {
        return DEFAULT_EXPRESSION_CACHE_MAX_SIZE;
    }
    
    protected Logger getExpressionCacheLogger() {
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getExpressionCacheName());
    }
    
    protected ICacheEntryValidityChecker<String,Object> getExpressionValidityChecker() {
        return null;
    }
    
    
}

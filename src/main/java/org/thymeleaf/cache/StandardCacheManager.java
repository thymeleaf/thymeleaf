/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
 * <p>
 *   Standard implementation of {@link ICacheManager}, returning
 *   configurable instances of {@link StandardCache} for each of
 *   the default caches defined at the cache manager interface.
 * </p>
 * <p>
 *   Each cache allows the configuration of the following parameters:
 * </p>
 * <ul>
 *   <li>Its <i>name</i> (will be displayed in logs).</li>
 *   <li>Its <i>initial size</i>: the size the cache will be initialized with.</li>
 *   <li>Its <i>maximum size</i>: the maximum size the cache will be allowed to reach.
 *       Some special values:
 *       <ul>
 *         <li><tt>-1</tt> means no limit in size.</li>
 *         <li><tt>0</tt> means this cache will not be used at 
 *             all (<tt>getXCache()</tt> will return <tt>null</tt>).</li>
 *       </ul>
 *   </li>
 *   <li>Whether the cache should use <i>soft references</i> or not
 *       (<tt>java.lang.ref.SoftReference</tt>). Using Soft References
 *       allows the cache to be <i>memory-sensitive</i>, allowing the garbage collector
 *       to dispose cache entries if memory is critical, before raising an
 *       <tt>OutOfMemoryError</tt>.</li>
 *   <li>The <i>name of the logger</i> that will output trace information for the
 *       cache object. Configuring this allows a finer-grained log configuration that
 *       allows the more effective inspection of cache behaviour. If not specifically
 *       set, <tt>org.thymeleaf.TemplateEngine.cache.${cacheName}</tt> will be used.</li>
 *   <li>An (optional) <i>validity checker</i> implementing {@link ICacheEntryValidityChecker},
 *       which will be applied on each entry upon retrieval from cache in order to ensure
 *       it is still valid and can be used.
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.0.0
 *
 */
public class StandardCacheManager extends AbstractCacheManager {

    
    /**
     * Default template cache name: "TEMPLATE_CACHE"
     */
    public static final String DEFAULT_TEMPLATE_CACHE_NAME = "TEMPLATE_CACHE";
    
    /**
     * Default template cache initial size: 10
     */
    public static final int DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE = 10;
    
    /**
     * Default template cache maximum size: 50
     */
    public static final int DEFAULT_TEMPLATE_CACHE_MAX_SIZE = 50;
    
    /**
     * Default template cache "use soft references" flag: true
     */
    public static final boolean DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES = true;
    
    /**
     * Default template cache logger name: null (default behaviour = org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE)
     */
    public static final String DEFAULT_TEMPLATE_CACHE_LOGGER_NAME = null;
    
    /**
     * Default template cache validity checker: an instance of {@link StandardParsedTemplateEntryValidator}.
     */
    public static final ICacheEntryValidityChecker<String,Template> DEFAULT_TEMPLATE_CACHE_VALIDITY_CHECKER = new StandardParsedTemplateEntryValidator();

    
    
    /**
     * Default fragment cache name: "FRAGMENT_CACHE"
     */
    public static final String DEFAULT_FRAGMENT_CACHE_NAME = "FRAGMENT_CACHE";
    
    /**
     * Default fragment cache initial size: 20
     */
    public static final int DEFAULT_FRAGMENT_CACHE_INITIAL_SIZE = 20;
    
    /**
     * Default fragment cache maximum size: 300
     */
    public static final int DEFAULT_FRAGMENT_CACHE_MAX_SIZE = 300;
    
    /**
     * Default fragment cache "use soft references" flag: true
     */
    public static final boolean DEFAULT_FRAGMENT_CACHE_USE_SOFT_REFERENCES = true;
    
    /**
     * Default fragment cache logger name: null (default behaviour = org.thymeleaf.TemplateEngine.cache.FRAGMENT_CACHE)
     */
    public static final String DEFAULT_FRAGMENT_CACHE_LOGGER_NAME = null;
    
    /**
     * Default fragment cache validity checker: null
     */
    public static final ICacheEntryValidityChecker<String,List<Node>> DEFAULT_FRAGMENT_CACHE_VALIDITY_CHECKER = null;
   

    
    /**
     * Default message cache name: "MESSAGE_CACHE"
     */
    public static final String DEFAULT_MESSAGE_CACHE_NAME = "MESSAGE_CACHE";
    
    /**
     * Default message cache initial size: 20
     */
    public static final int DEFAULT_MESSAGE_CACHE_INITIAL_SIZE = 20;
    
    /**
     * Default message cache maximum size: 300
     */
    public static final int DEFAULT_MESSAGE_CACHE_MAX_SIZE = 300;
    
    /**
     * Default message cache "use soft references" flag: true
     */
    public static final boolean DEFAULT_MESSAGE_CACHE_USE_SOFT_REFERENCES = true;
    
    /**
     * Default message cache logger name: null (default behaviour = org.thymeleaf.TemplateEngine.cache.MESSAGE_CACHE)
     */
    public static final String DEFAULT_MESSAGE_CACHE_LOGGER_NAME = null;
    
    /**
     * Default message cache validity checker: null
     */
    public static final ICacheEntryValidityChecker<String,Properties> DEFAULT_MESSAGE_CACHE_VALIDITY_CHECKER = null;

    
    /**
     * Default expression cache name: "EXPRESSION_CACHE"
     */
    public static final String DEFAULT_EXPRESSION_CACHE_NAME = "EXPRESSION_CACHE";
    
    /**
     * Default expression cache initial size: 100
     */
    public static final int DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE = 100;
    
    /**
     * Default expression cache maximum size: 500
     */
    public static final int DEFAULT_EXPRESSION_CACHE_MAX_SIZE = 500;
    
    /**
     * Default expression cache "use soft references" flag: true
     */
    public static final boolean DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES = true;
    
    /**
     * Default expression cache logger name: null (default behaviour = org.thymeleaf.TemplateEngine.cache.EXPRESSION_CACHE)
     */
    public static final String DEFAULT_EXPRESSION_CACHE_LOGGER_NAME = null;

    /**
     * Default expression cache validity checker: null
     */
    public static final ICacheEntryValidityChecker<String,Object> DEFAULT_EXPRESSION_CACHE_VALIDITY_CHECKER = null;

    
    
    
    private String templateCacheName = DEFAULT_TEMPLATE_CACHE_NAME;
    private int templateCacheInitialSize = DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE;
    private int templateCacheMaxSize = DEFAULT_TEMPLATE_CACHE_MAX_SIZE;
    private boolean templateCacheUseSoftReferences = DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES;
    private String templateCacheLoggerName = DEFAULT_TEMPLATE_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<String,Template> templateCacheValidityChecker = DEFAULT_TEMPLATE_CACHE_VALIDITY_CHECKER;
    
    private String fragmentCacheName = DEFAULT_FRAGMENT_CACHE_NAME;
    private int fragmentCacheInitialSize = DEFAULT_FRAGMENT_CACHE_INITIAL_SIZE;
    private int fragmentCacheMaxSize = DEFAULT_FRAGMENT_CACHE_MAX_SIZE;
    private boolean fragmentCacheUseSoftReferences = DEFAULT_FRAGMENT_CACHE_USE_SOFT_REFERENCES;
    private String fragmentCacheLoggerName = DEFAULT_FRAGMENT_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<String,List<Node>> fragmentCacheValidityChecker = DEFAULT_FRAGMENT_CACHE_VALIDITY_CHECKER;
   
    private String messageCacheName = DEFAULT_MESSAGE_CACHE_NAME;
    private int messageCacheInitialSize = DEFAULT_MESSAGE_CACHE_INITIAL_SIZE;
    private int messageCacheMaxSize = DEFAULT_MESSAGE_CACHE_MAX_SIZE;
    private boolean messageCacheUseSoftReferences = DEFAULT_MESSAGE_CACHE_USE_SOFT_REFERENCES;
    private String messageCacheLoggerName = DEFAULT_MESSAGE_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<String,Properties> messageCacheValidityChecker = DEFAULT_MESSAGE_CACHE_VALIDITY_CHECKER;
    
    private String expressionCacheName = DEFAULT_EXPRESSION_CACHE_NAME;
    private int expressionCacheInitialSize = DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE;
    private int expressionCacheMaxSize = DEFAULT_EXPRESSION_CACHE_MAX_SIZE;
    private boolean expressionCacheUseSoftReferences = DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES;
    private String expressionCacheLoggerName = DEFAULT_EXPRESSION_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<String,Object> expressionCacheValidityChecker = DEFAULT_EXPRESSION_CACHE_VALIDITY_CHECKER;
    
    
    
    public StandardCacheManager() {
        super();
    }

    
    
    @Override
    protected final ICache<String, Template> initializeTemplateCache() {
        final int maxSize = getTemplateCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, Template>(
                getTemplateCacheName(), getTemplateCacheUseSoftReferences(), 
                getTemplateCacheInitialSize(), maxSize, 
                getTemplateCacheValidityChecker(), getTemplateCacheLogger());
    }
    
    @Override
    protected final ICache<String, List<Node>> initializeFragmentCache() {
        final int maxSize = getFragmentCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, List<Node>>(
                getFragmentCacheName(), getFragmentCacheUseSoftReferences(), 
                getFragmentCacheInitialSize(), maxSize, 
                getFragmentCacheValidityChecker(), getFragmentCacheLogger());
    }

    
    @Override
    protected final ICache<String, Properties> initializeMessageCache() {
        final int maxSize = getMessageCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, Properties>(
                getMessageCacheName(), getMessageCacheUseSoftReferences(), 
                getMessageCacheInitialSize(), maxSize, 
                getMessageCacheValidityChecker(), getMessageCacheLogger());
    }

    
    @Override
    protected final ICache<String, Object> initializeExpressionCache() {
        final int maxSize = getExpressionCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<String, Object>(
                getExpressionCacheName(), getExpressionCacheUseSoftReferences(), 
                getExpressionCacheInitialSize(), maxSize, 
                getExpressionCacheValidityChecker(), getExpressionCacheLogger());
    }
    
    
    
    
    public String getTemplateCacheName() {
        return this.templateCacheName;
    }
    
    public boolean getTemplateCacheUseSoftReferences() {
        return this.templateCacheUseSoftReferences;
    }
    
    public int getTemplateCacheInitialSize() {
        return this.templateCacheInitialSize;
    }
    
    public int getTemplateCacheMaxSize() {
        return this.templateCacheMaxSize;
    }
    
    public String getTemplateCacheLoggerName() {
        return this.templateCacheLoggerName;
    }
    
    public ICacheEntryValidityChecker<String,Template> getTemplateCacheValidityChecker() {
        return this.templateCacheValidityChecker;
    }
    
    public final Logger getTemplateCacheLogger() {
        final String loggerName = getTemplateCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getTemplateCacheName());
    }


    
    
    public String getFragmentCacheName() {
        return this.fragmentCacheName;
    }
    
    public boolean getFragmentCacheUseSoftReferences() {
        return this.fragmentCacheUseSoftReferences;
    }
    
    public int getFragmentCacheInitialSize() {
        return this.fragmentCacheInitialSize;
    }
    
    public int getFragmentCacheMaxSize() {
        return this.fragmentCacheMaxSize;
    }
    
    public String getFragmentCacheLoggerName() {
        return this.fragmentCacheLoggerName;
    }
    
    public ICacheEntryValidityChecker<String,List<Node>> getFragmentCacheValidityChecker() {
        return this.fragmentCacheValidityChecker;
    }
    
    public final Logger getFragmentCacheLogger() {
        final String loggerName = getFragmentCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getFragmentCacheName());
    }

    
    
    
    public String getMessageCacheName() {
        return this.messageCacheName;
    }
    
    public boolean getMessageCacheUseSoftReferences() {
        return this.messageCacheUseSoftReferences;
    }
    
    public int getMessageCacheInitialSize() {
        return this.messageCacheInitialSize;
    }
    
    public int getMessageCacheMaxSize() {
        return this.messageCacheMaxSize;
    }
    
    public String getMessageCacheLoggerName() {
        return this.messageCacheLoggerName;
    }
    
    public ICacheEntryValidityChecker<String,Properties> getMessageCacheValidityChecker() {
        return this.messageCacheValidityChecker;
    }

    public final Logger getMessageCacheLogger() {
        final String loggerName = getMessageCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getMessageCacheName());
    }

    
    
    
    public String getExpressionCacheName() {
        return this.expressionCacheName;
    }
    
    public boolean getExpressionCacheUseSoftReferences() {
        return this.expressionCacheUseSoftReferences;
    }
    
    public int getExpressionCacheInitialSize() {
        return this.expressionCacheInitialSize;
    }
    
    public int getExpressionCacheMaxSize() {
        return this.expressionCacheMaxSize;
    }
    
    public String getExpressionCacheLoggerName() {
        return this.expressionCacheLoggerName;
    }
    
    public ICacheEntryValidityChecker<String,Object> getExpressionCacheValidityChecker() {
        return this.expressionCacheValidityChecker;
    }

    public final Logger getExpressionCacheLogger() {
        final String loggerName = getExpressionCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getExpressionCacheName());
    }



    
    
    public void setTemplateCacheName(final String templateCacheName) {
        this.templateCacheName = templateCacheName;
    }

    public void setTemplateCacheInitialSize(final int templateCacheInitialSize) {
        this.templateCacheInitialSize = templateCacheInitialSize;
    }

    public void setTemplateCacheMaxSize(final int templateCacheMaxSize) {
        this.templateCacheMaxSize = templateCacheMaxSize;
    }

    public void setTemplateCacheUseSoftReferences(final boolean templateCacheUseSoftReferences) {
        this.templateCacheUseSoftReferences = templateCacheUseSoftReferences;
    }

    public void setTemplateCacheLoggerName(final String templateCacheLoggerName) {
        this.templateCacheLoggerName = templateCacheLoggerName;
    }

    public void setTemplateCacheValidityChecker(final ICacheEntryValidityChecker<String, Template> templateCacheValidityChecker) {
        this.templateCacheValidityChecker = templateCacheValidityChecker;
    }

    
    
    public void setFragmentCacheName(final String fragmentCacheName) {
        this.fragmentCacheName = fragmentCacheName;
    }

    public void setFragmentCacheInitialSize(final int fragmentCacheInitialSize) {
        this.fragmentCacheInitialSize = fragmentCacheInitialSize;
    }

    public void setFragmentCacheMaxSize(final int fragmentCacheMaxSize) {
        this.fragmentCacheMaxSize = fragmentCacheMaxSize;
    }

    public void setFragmentCacheUseSoftReferences(final boolean fragmentCacheUseSoftReferences) {
        this.fragmentCacheUseSoftReferences = fragmentCacheUseSoftReferences;
    }

    public void setFragmentCacheLoggerName(final String fragmentCacheLoggerName) {
        this.fragmentCacheLoggerName = fragmentCacheLoggerName;
    }
    
    public void setFragmentCacheValidityChecker(final ICacheEntryValidityChecker<String, List<Node>> fragmentCacheValidityChecker) {
        this.fragmentCacheValidityChecker = fragmentCacheValidityChecker;
    }



    public void setMessageCacheName(final String messageCacheName) {
        this.messageCacheName = messageCacheName;
    }

    public void setMessageCacheInitialSize(final int messageCacheInitialSize) {
        this.messageCacheInitialSize = messageCacheInitialSize;
    }

    public void setMessageCacheMaxSize(final int messageCacheMaxSize) {
        this.messageCacheMaxSize = messageCacheMaxSize;
    }

    public void setMessageCacheUseSoftReferences(final boolean messageCacheUseSoftReferences) {
        this.messageCacheUseSoftReferences = messageCacheUseSoftReferences;
    }

    public void setMessageCacheLoggerName(final String messageCacheLoggerName) {
        this.messageCacheLoggerName = messageCacheLoggerName;
    }
    
    public void setMessageCacheValidityChecker(final ICacheEntryValidityChecker<String, Properties> messageCacheValidityChecker) {
        this.messageCacheValidityChecker = messageCacheValidityChecker;
    }

    
    
    public void setExpressionCacheName(final String expressionCacheName) {
        this.expressionCacheName = expressionCacheName;
    }

    public void setExpressionCacheInitialSize(final int expressionCacheInitialSize) {
        this.expressionCacheInitialSize = expressionCacheInitialSize;
    }

    public void setExpressionCacheMaxSize(final int expressionCacheMaxSize) {
        this.expressionCacheMaxSize = expressionCacheMaxSize;
    }

    public void setExpressionCacheUseSoftReferences(final boolean expressionCacheUseSoftReferences) {
        this.expressionCacheUseSoftReferences = expressionCacheUseSoftReferences;
    }

    public void setExpressionCacheLoggerName(final String expressionCacheLoggerName) {
        this.expressionCacheLoggerName = expressionCacheLoggerName;
    }

    public void setExpressionCacheValidityChecker(final ICacheEntryValidityChecker<String, Object> expressionCacheValidityChecker) {
        this.expressionCacheValidityChecker = expressionCacheValidityChecker;
    }

    
    
    
    
}

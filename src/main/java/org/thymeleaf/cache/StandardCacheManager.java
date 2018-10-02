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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.engine.TemplateModel;


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
 *         <li>{@code -1} means no limit in size.</li>
 *         <li>{@code 0} means this cache will not be used at
 *             all ({@code getXCache()} will return {@code null}).</li>
 *       </ul>
 *   </li>
 *   <li>Whether the cache should use <i>soft references</i> or not
 *       ({@code java.lang.ref.SoftReference}). Using Soft References
 *       allows the cache to be <i>memory-sensitive</i>, allowing the garbage collector
 *       to dispose cache entries if memory is critical, before raising an
 *       {@code OutOfMemoryError}.</li>
 *   <li>The <i>name of the logger</i> that will output trace information for the
 *       cache object. Configuring this allows a finer-grained log configuration that
 *       allows the more effective inspection of cache behaviour. If not specifically
 *       set, {@code org.thymeleaf.TemplateEngine.cache.${cacheName}} will be used.</li>
 *   <li>An (optional) <i>validity checker</i> implementing {@link ICacheEntryValidityChecker},
 *       which will be applied on each entry upon retrieval from cache in order to ensure
 *       it is still valid and can be used.
 * </ul>
 * <p>
 *   Note a class with this name existed since 2.0.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class StandardCacheManager extends AbstractCacheManager {

    
    /**
     * Default template cache name: {@value}
     */
    public static final String DEFAULT_TEMPLATE_CACHE_NAME = "TEMPLATE_CACHE";
    
    /**
     * Default template cache initial size: {@value}
     */
    public static final int DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE = 20;
    
    /**
     * Default template cache maximum size: {@value}
     */
    public static final int DEFAULT_TEMPLATE_CACHE_MAX_SIZE = 200;

    /**
     * Default template cache "enable counters" flag: {@value}
     */
    public static final boolean DEFAULT_TEMPLATE_CACHE_ENABLE_COUNTERS = false;

    /**
     * Default template cache "use soft references" flag: {@value}
     */
    public static final boolean DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES = true;
    
    /**
     * Default template cache logger name: null (default behaviour = org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE)
     */
    public static final String DEFAULT_TEMPLATE_CACHE_LOGGER_NAME = null;
    
    /**
     * Default template cache validity checker: an instance of {@link StandardParsedTemplateEntryValidator}.
     */
    public static final ICacheEntryValidityChecker<TemplateCacheKey,TemplateModel> DEFAULT_TEMPLATE_CACHE_VALIDITY_CHECKER = new StandardParsedTemplateEntryValidator();

    
    /**
     * Default expression cache name: {@value}
     */
    public static final String DEFAULT_EXPRESSION_CACHE_NAME = "EXPRESSION_CACHE";
    
    /**
     * Default expression cache initial size: {@value}
     */
    public static final int DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE = 100;
    
    /**
     * Default expression cache maximum size: {@value}
     */
    public static final int DEFAULT_EXPRESSION_CACHE_MAX_SIZE = 500;

    /**
     * Default expression cache "enable counters" flag: {@value}
     */
    public static final boolean DEFAULT_EXPRESSION_CACHE_ENABLE_COUNTERS = false;

    /**
     * Default expression cache "use soft references" flag: {@value}
     */
    public static final boolean DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES = true;
    
    /**
     * Default expression cache logger name: null (default behaviour = org.thymeleaf.TemplateEngine.cache.EXPRESSION_CACHE)
     */
    public static final String DEFAULT_EXPRESSION_CACHE_LOGGER_NAME = null;

    /**
     * Default expression cache validity checker: null
     */
    public static final ICacheEntryValidityChecker<ExpressionCacheKey,Object> DEFAULT_EXPRESSION_CACHE_VALIDITY_CHECKER = null;

    
    
    
    private String templateCacheName = DEFAULT_TEMPLATE_CACHE_NAME;
    private int templateCacheInitialSize = DEFAULT_TEMPLATE_CACHE_INITIAL_SIZE;
    private int templateCacheMaxSize = DEFAULT_TEMPLATE_CACHE_MAX_SIZE;
    private boolean templateCacheEnableCounters = DEFAULT_TEMPLATE_CACHE_ENABLE_COUNTERS;
    private boolean templateCacheUseSoftReferences = DEFAULT_TEMPLATE_CACHE_USE_SOFT_REFERENCES;
    private String templateCacheLoggerName = DEFAULT_TEMPLATE_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<TemplateCacheKey,TemplateModel> templateCacheValidityChecker = DEFAULT_TEMPLATE_CACHE_VALIDITY_CHECKER;

    private String expressionCacheName = DEFAULT_EXPRESSION_CACHE_NAME;
    private int expressionCacheInitialSize = DEFAULT_EXPRESSION_CACHE_INITIAL_SIZE;
    private int expressionCacheMaxSize = DEFAULT_EXPRESSION_CACHE_MAX_SIZE;
    private boolean expressionCacheEnableCounters = DEFAULT_EXPRESSION_CACHE_ENABLE_COUNTERS;
    private boolean expressionCacheUseSoftReferences = DEFAULT_EXPRESSION_CACHE_USE_SOFT_REFERENCES;
    private String expressionCacheLoggerName = DEFAULT_EXPRESSION_CACHE_LOGGER_NAME;
    private ICacheEntryValidityChecker<ExpressionCacheKey,Object> expressionCacheValidityChecker = DEFAULT_EXPRESSION_CACHE_VALIDITY_CHECKER;
    
    
    
    public StandardCacheManager() {
        super();
    }

    
    
    @Override
    protected final ICache<TemplateCacheKey, TemplateModel> initializeTemplateCache() {
        final int maxSize = getTemplateCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<TemplateCacheKey, TemplateModel>(
                getTemplateCacheName(), getTemplateCacheUseSoftReferences(), 
                getTemplateCacheInitialSize(), maxSize,
                getTemplateCacheValidityChecker(), getTemplateCacheLogger(), getTemplateCacheEnableCounters());
    }

    
    @Override
    protected final ICache<ExpressionCacheKey, Object> initializeExpressionCache() {
        final int maxSize = getExpressionCacheMaxSize();
        if (maxSize == 0) {
            return null;
        }
        return new StandardCache<ExpressionCacheKey, Object>(
                getExpressionCacheName(), getExpressionCacheUseSoftReferences(), 
                getExpressionCacheInitialSize(), maxSize,
                getExpressionCacheValidityChecker(), getExpressionCacheLogger(), getExpressionCacheEnableCounters());
    }
    
    
    
    
    public String getTemplateCacheName() {
        return this.templateCacheName;
    }
    
    public boolean getTemplateCacheUseSoftReferences() {
        return this.templateCacheUseSoftReferences;
    }

    private boolean getTemplateCacheEnableCounters() {
        return this.templateCacheEnableCounters;
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
    
    public ICacheEntryValidityChecker<TemplateCacheKey,TemplateModel> getTemplateCacheValidityChecker() {
        return this.templateCacheValidityChecker;
    }
    
    public final Logger getTemplateCacheLogger() {
        final String loggerName = getTemplateCacheLoggerName();
        if (loggerName != null) {
            return LoggerFactory.getLogger(loggerName);
        }
        return LoggerFactory.getLogger(TemplateEngine.class.getName() + ".cache." + getTemplateCacheName());
    }

    
    
    
    public String getExpressionCacheName() {
        return this.expressionCacheName;
    }
    
    public boolean getExpressionCacheUseSoftReferences() {
        return this.expressionCacheUseSoftReferences;
    }

    private boolean getExpressionCacheEnableCounters() {
        return this.expressionCacheEnableCounters;
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
    
    public ICacheEntryValidityChecker<ExpressionCacheKey,Object> getExpressionCacheValidityChecker() {
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

    public void setTemplateCacheValidityChecker(final ICacheEntryValidityChecker<TemplateCacheKey, TemplateModel> templateCacheValidityChecker) {
        this.templateCacheValidityChecker = templateCacheValidityChecker;
    }

    public void setTemplateCacheEnableCounters(boolean templateCacheEnableCounters) {
        this.templateCacheEnableCounters = templateCacheEnableCounters;
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

    public void setExpressionCacheValidityChecker(final ICacheEntryValidityChecker<ExpressionCacheKey, Object> expressionCacheValidityChecker) {
        this.expressionCacheValidityChecker = expressionCacheValidityChecker;
    }

    public void setExpressionCacheEnableCounters(boolean expressionCacheEnableCounters) {
        this.expressionCacheEnableCounters = expressionCacheEnableCounters;
    }
    
    
    
}

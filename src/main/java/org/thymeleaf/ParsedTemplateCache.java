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
package org.thymeleaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.CacheMap;
import org.thymeleaf.util.CacheMap.ICacheMapEntryValidityChecker;
import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
final class ParsedTemplateCache {

    
    public static final String PARSER_CACHE_LOGGER_NAME = TemplateEngine.class.getName() + ".PARSERCACHE";
    protected static final Logger logger = LoggerFactory.getLogger(PARSER_CACHE_LOGGER_NAME);
    
    private final CacheMap<String,ParsedTemplate> cache;

    
    ParsedTemplateCache(final int cacheSize) {
        super();
        // Although CacheMap allows it, Parsed Template Cache cannot have unlimited size
        Validate.isTrue(cacheSize >= 0, "Parsed Template Cache size must be 0 (no cache) or > 1");
        if (cacheSize > 0) {
            this.cache = 
                    new CacheMap<String,ParsedTemplate>(
                           "ParsedTemplateCache.cache", true, 16, cacheSize,
                           new ParsedTemplateEntryValidator());
        } else {
            this.cache = null;
        }
    }
    
    
    
    ParsedTemplate getParsedTemplate(final String templateName) {
        if (this.cache == null) {
            // No cache (set to size 0 in configuration)
            return null;
        }
        final ParsedTemplate parsedTemplate = this.cache.get(templateName);
        if (logger.isDebugEnabled()) {
            if (parsedTemplate == null) {
                logger.debug(
                        "[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in Parsed Template Cache for template \"{}\".", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), templateName});
            } else {
                logger.debug(
                        "[THYMELEAF][{}][{}][CACHE_HIT] Cache hit in Parsed Template Cache for template \"{}\".", 
                        new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), templateName});
            }
        }
        return parsedTemplate;
    }
    
    
    
    void putParsedTemplate(final ParsedTemplate parsedTemplate) {
        if (this.cache == null) {
            // No cache (set to size 0 in configuration)
            return;
        }
        this.cache.put(parsedTemplate.getTemplateName(), parsedTemplate);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[THYMELEAF][{}][{}][CACHE_ADD] Adding cache entry in Parsed Template Cache for template \"{}\".", 
                    new Object[] {TemplateEngine.threadIndex(), TemplateEngine.threadTemplateName(), parsedTemplate.getTemplateName()});
        }
    }
    

    
    void clearParsedTemplateCache() {
        if (this.cache == null) {
            // No cache (set to size 0 in configuration)
            return;
        }
        this.cache.clear();
    }
    
    
    void clearParsedTemplateCacheFor(final String templateName) {
        if (this.cache == null) {
            // No cache (set to size 0 in configuration)
            return;
        }
        this.cache.clearKey(templateName);
    }
    
    
    
    
    final static class ParsedTemplateEntryValidator 
            implements ICacheMapEntryValidityChecker<String,ParsedTemplate> {
        
        private static final long serialVersionUID = -5853535204141790247L;

        public ParsedTemplateEntryValidator() {
            super();
        }

        public boolean checkIsValueStillValid(
                final String key, final ParsedTemplate value, final long entryCreationTimestamp) {
            return value.getTemplateResolution().getValidity().isCacheStillValid();
        }
        
    }
    

}

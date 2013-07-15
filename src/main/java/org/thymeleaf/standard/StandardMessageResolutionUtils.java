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
package org.thymeleaf.standard;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class StandardMessageResolutionUtils {

    
    private static final Logger logger = LoggerFactory.getLogger(StandardMessageResolutionUtils.class);
    
    private static final Object[] EMPTY_MESSAGE_PARAMETERS = new Object[0];
    
    private static final String THYMELEAF_MARKUP_SUFFIX = ".thtml";
    private static final String XML_SUFFIX = ".xml";
    private static final String XHTML_SUFFIX = ".xhtml";
    private static final String HTML_SUFFIX = ".html";
    private static final String HTM_SUFFIX = ".htm";
    private static final String JSP_SUFFIX = ".jsp";
    
    private static final String TEMPLATE_CACHE_PREFIX = "{template_msg}";

    
    
    
    public static String resolveMessageForTemplate(
            final Arguments arguments, final String key, final Object[] messageParameters, 
            final Properties defaultMessages) {
        
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(arguments.getContext().getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");
        
        final Locale locale = arguments.getContext().getLocale();

        final String templateName = arguments.getTemplateResolution().getTemplateName();
        final String cacheKey = TEMPLATE_CACHE_PREFIX + templateName + '_' + locale.toString();

        Properties properties = null;
        ICache<String,Properties> messagesCache = null;
        
        final ICacheManager cacheManager = arguments.getConfiguration().getCacheManager();
        if (cacheManager != null) {
            messagesCache = cacheManager.getMessageCache();
            if (messagesCache != null) {
                properties = messagesCache.get(cacheKey);
            }
        }
        
        if (properties == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[THYMELEAF][{}] Resolving uncached messages for template \"{}\" and locale \"{}\". Messages will be retrieved from files", new Object[] {TemplateEngine.threadIndex(), templateName, locale});
            }
            properties = loadMessagesForTemplate(arguments, defaultMessages);
            if (messagesCache != null) {
                messagesCache.put(cacheKey, properties);
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Resolving messages for template \"{}\" and locale \"{}\". Messages are CACHED", new Object[] {TemplateEngine.threadIndex(), templateName, locale});
            }
        }

        final String messageValue = properties.getProperty(key);

        if (messageValue == null) {
            return null;
        }

        final MessageFormat messageFormat = new MessageFormat(messageValue, locale);
        return messageFormat.format((messageParameters != null? messageParameters : EMPTY_MESSAGE_PARAMETERS));

    }
    
    
    
    
    
    private static Properties loadMessagesForTemplate(
            final Arguments arguments, final Properties defaultMessages) {

        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(arguments.getContext().getLocale(), "Locale in context cannot be null");
        
        final String resourceName = arguments.getTemplateResolution().getResourceName();
        final IResourceResolver resourceResolver = arguments.getTemplateResolution().getResourceResolver();
        final Locale locale = arguments.getContext().getLocale();
        
        final String templateBaseName = getTemplateFileNameBase(resourceName);
        
        return MessageResolutionUtils.loadCombinedMessagesFilesFromBaseName(
                arguments, resourceResolver, templateBaseName, locale, defaultMessages);
        
    }
    
    
    
    
    private static String getTemplateFileNameBase(final String templateFileName) {
        if (templateFileName.endsWith(THYMELEAF_MARKUP_SUFFIX)) {
            return templateFileName.substring(0, templateFileName.length() - THYMELEAF_MARKUP_SUFFIX.length());
        }
        if (templateFileName.endsWith(HTML_SUFFIX)) {
            return templateFileName.substring(0, templateFileName.length() - HTML_SUFFIX.length());
        }
        if (templateFileName.endsWith(XHTML_SUFFIX)) {
            return templateFileName.substring(0, templateFileName.length() - XHTML_SUFFIX.length());
        }
        if (templateFileName.endsWith(HTM_SUFFIX)) {
            return templateFileName.substring(0, templateFileName.length() - HTM_SUFFIX.length());
        }
        if (templateFileName.endsWith(XML_SUFFIX)) {
            return templateFileName.substring(0, templateFileName.length() - XML_SUFFIX.length());
        }
        if (templateFileName.endsWith(JSP_SUFFIX)) {
            return templateFileName.substring(0, templateFileName.length() - JSP_SUFFIX.length());
        }
        return templateFileName;
    }
    
    
    
    
    private StandardMessageResolutionUtils() {
        super();
    }
    
}

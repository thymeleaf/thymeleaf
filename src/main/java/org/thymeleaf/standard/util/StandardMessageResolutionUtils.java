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
package org.thymeleaf.standard.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Static utility class for message resolution operations in the Standard Dialects.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
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
    
    private static final String TEMPLATE_CACHE_PREFIX = "tmsg|";

    
    
    
    public static String resolveMessageForTemplate(
            final ITemplateContext context, final String key, final Object[] messageParameters,
            final Properties defaultMessages) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(context.getLocale(), "Locale returned by context cannot be null");
        Validate.notNull(key, "Message key cannot be null");

        final IEngineConfiguration configuration = context.getConfiguration();
        final Locale locale = context.getLocale();

        final String templateName = context.getTemplateResolution().getTemplate();
        final String cacheKey = TEMPLATE_CACHE_PREFIX + templateName + "_" + computeLocaleToString(locale);

        Properties properties = null;
        ICache<String,Properties> messagesCache = null;
        
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            messagesCache = cacheManager.getMessageCache();
            if (messagesCache != null) {
                properties = messagesCache.get(cacheKey);
            }
        }
        
        if (properties == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Resolving uncached messages for template \"{}\" and locale \"{}\". Messages will be retrieved from files", new Object[] {TemplateEngine.threadIndex(), templateName, locale});
            }
            properties = loadMessagesForTemplate(context, defaultMessages);
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

        // Most times messages will not contain parameters, and we avoid the expensive MessageFormat operation
        if (!isFormatCandidate(messageValue)) {
            return messageValue;
        }

        final MessageFormat messageFormat = new MessageFormat(messageValue, locale);
        return messageFormat.format((messageParameters != null? messageParameters : EMPTY_MESSAGE_PARAMETERS));

    }


    /*
     * This will allow us determine whether a message might actually contain parameter placeholders.
     */
    private static boolean isFormatCandidate(final String message) {
        char c;
        int n = message.length();
        while (n-- != 0) {
            c = message.charAt(n);
            if (c == '}' || c == '\'') {
                return true;
            }
        }
        return false;
    }


    // Calling locale.toString is surprisingly expensive, so we will try to us some shortcuts
    // NOTE there is one like this at MessageResolutionUtils. It's private and duplicated because it is
    //      a low-level implementation detail
    private static String computeLocaleToString(final Locale locale) {
        String localeStr = locale.getLanguage();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();
        if (country.length() > 0) {
            if (localeStr.length() > 0) {
                localeStr = localeStr + "_" + country;
            } else {
                localeStr = country;
            }
        }
        if (variant.length() > 0) {
            if (localeStr.length() > 0) {
                localeStr = localeStr + "_" + variant;
            } else {
                localeStr = variant;
            }
        }
        return localeStr;
    }


    
    
    private static Properties loadMessagesForTemplate(
            final ITemplateContext context, final Properties defaultMessages) {

        final String resourceName = context.getTemplateResolution().getResourceName();
        final IResourceResolver resourceResolver = context.getTemplateResolution().getResourceResolver();
        final Locale locale = context.getLocale();
        
        final String templateBaseName =
                getTemplateFileNameBase(context.getConfiguration().getTextRepository(), resourceName);
        
        return MessageResolutionUtils.loadCombinedMessagesFilesFromBaseName(
                context.getConfiguration(), resourceResolver, templateBaseName, locale, defaultMessages);
        
    }
    
    
    
    
    private static String getTemplateFileNameBase(final ITextRepository textRepository, final String templateFileName) {
        if (templateFileName.endsWith(THYMELEAF_MARKUP_SUFFIX)) {
            return textRepository.getText(templateFileName, 0, templateFileName.length() - THYMELEAF_MARKUP_SUFFIX.length());
        }
        if (templateFileName.endsWith(HTML_SUFFIX)) {
            return textRepository.getText(templateFileName, 0, templateFileName.length() - HTML_SUFFIX.length());
        }
        if (templateFileName.endsWith(XHTML_SUFFIX)) {
            return textRepository.getText(templateFileName, 0, templateFileName.length() - XHTML_SUFFIX.length());
        }
        if (templateFileName.endsWith(HTM_SUFFIX)) {
            return textRepository.getText(templateFileName, 0, templateFileName.length() - HTM_SUFFIX.length());
        }
        if (templateFileName.endsWith(XML_SUFFIX)) {
            return textRepository.getText(templateFileName, 0, templateFileName.length() - XML_SUFFIX.length());
        }
        if (templateFileName.endsWith(JSP_SUFFIX)) {
            return textRepository.getText(templateFileName, 0, templateFileName.length() - JSP_SUFFIX.length());
        }
        return templateFileName;
    }
    
    
    
    
    private StandardMessageResolutionUtils() {
        super();
    }
    
}

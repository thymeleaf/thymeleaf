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
package org.thymeleaf.util;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.message.absent.IAbsentMessageFormatter;
import org.thymeleaf.message.resolver.IMessageResolver;
import org.thymeleaf.message.resolver.MessageResolution;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.resourceresolver.IResourceResolver;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 1.0 (reimplemented in 3.0.0)
 *
 */
public final class MessageResolutionUtils {

    
    private static final Logger logger = LoggerFactory.getLogger(MessageResolutionUtils.class);

    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String SUFFIX_FOR_DEFAULT = "";
    private static final Pattern CLASS_NAME_SEPARATOR_PATTERN = Pattern.compile("\\.");

    private static final String CLASS_CACHE_PREFIX = "cmsg|";

    
    public static String resolveMessageForTemplate(
            final ITemplateProcessingContext processingContext, final String messageKey, final Object[] messageParameters) {
        return resolveMessageForTemplate(processingContext, messageKey, messageParameters, true);
    }
    
    public static String resolveMessageForTemplate(
            final ITemplateProcessingContext processingContext, final String messageKey, final Object[] messageParameters,
            final boolean returnStringAlways) {

        Validate.notNull(processingContext, "Processing Context cannot be null");
        Validate.notNull(processingContext.getVariables(), "Variables Map returned by Processing Context cannot be null");
        Validate.notNull(messageKey, "Message key cannot be null");
        
        final Set<IMessageResolver> messageResolvers = 
            processingContext.getConfiguration().getMessageResolvers();
        
        MessageResolution messageResolution = null;
        for (final IMessageResolver messageResolver : messageResolvers) {
            if (messageResolution == null) {
                messageResolution =
                    messageResolver.resolveMessage(processingContext, messageKey, messageParameters);
            }
        }
        
        if (messageResolution == null) {
            if (!returnStringAlways) {
                return null;
            }

            final IAbsentMessageFormatter absentMessageFormatter = processingContext.getConfiguration().getAbsentMessageFormatter();
            return absentMessageFormatter.getAbsentMessageRepresentation(messageKey, processingContext.getLocale());
            
        }
        
        return messageResolution.getResolvedMessage();
        
    }
    
    
    
    
    public static String resolveMessageForClass(
            final IEngineConfiguration configuration,
            final Class<?> targetClass, final Locale locale,
            final String messageKey, final Object[] messageParameters) {
        return resolveMessageForClass(configuration, targetClass, locale, messageKey, messageParameters, true);
    }
    
    
    
    public static String resolveMessageForClass(
            final IEngineConfiguration configuration,
            final Class<?> targetClass, final Locale locale,
            final String messageKey, final Object[] messageParameters, 
            final boolean returnStringAlways) {
        
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(targetClass, "Target class cannot be null");
        Validate.notNull(locale, "Locale in context cannot be null");
        Validate.notNull(messageKey, "Message key cannot be null");

        final String className = targetClass.getName();
        final String cacheKey = CLASS_CACHE_PREFIX + className + "_" + computeLocaleToString(locale);
        
        ICache<String,Properties> messagesCache = null;
        Properties properties = null;
        
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            messagesCache = cacheManager.getMessageCache();
            if (messagesCache != null) {
                properties = messagesCache.get(cacheKey);
            }
        }
        
        if (properties == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[THYMELEAF][{}] Resolving uncached messages for class \"{}\" and locale \"{}\". Messages will be retrieved from files", new Object[] {TemplateEngine.threadIndex(), targetClass.getName(), locale});
            }
            properties = loadMessagesForClass(targetClass, locale);
            if (messagesCache != null) {
                messagesCache.put(cacheKey, properties);
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Resolving messages for class \"{}\" and locale \"{}\". Messages are CACHED", new Object[] {TemplateEngine.threadIndex(), targetClass.getName(), locale});
            }
        }

        final String messageValue = properties.getProperty(messageKey);

        if (messageValue == null) {

            if (returnStringAlways) {
                final IAbsentMessageFormatter absentMessageFormatter = configuration.getAbsentMessageFormatter();
                return absentMessageFormatter.getAbsentMessageRepresentation(messageKey, locale);
            }

            return null;

        }

        if (messageParameters == null || messageParameters.length == 0) {
            return messageValue;
        }

        final MessageFormat messageFormat = new MessageFormat(messageValue, locale);
        return messageFormat.format(messageParameters);

    }

    
    
    
    private static Properties loadMessagesForClass(
            final Class<?> targetClass, final Locale locale) {
        
        Validate.notNull(targetClass, "Target class cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        
        final List<Properties> properties = new ArrayList<Properties>(5);
        Class<?> currentClass = targetClass;
        
        String base = getClassNameBase(currentClass);
        properties.add(loadCombinedMessagesFilesFromBaseName(null, null, null, base, locale, null));
        
        while (!currentClass.getSuperclass().equals(Object.class)) {
            currentClass = currentClass.getSuperclass();
            base = getClassNameBase(currentClass);
            properties.add(loadCombinedMessagesFilesFromBaseName(null, null, null, base, locale, null));
        }
        
        return combineMessages(properties, null);
        
    }

    
    
    
    
    public static Properties loadCombinedMessagesFilesFromBaseName(
            final IEngineConfiguration configuration, final IContext context, final IResourceResolver resourceResolver,
            final String baseName, final Locale locale, final Properties defaultMessages) {

        /*
         * Both arguments and resource resolver can be null 
         * (will use a ClassLoaderResourceResolver if so).
         */
        
        Validate.notNull(baseName, "Base name cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        
        final List<String> messageResourceNames = getMessageFileNamesFromBase(baseName, locale);

        final IResourceResolver usedResourceResolver = 
            (resourceResolver != null? resourceResolver : new ClassLoaderResourceResolver());
        
        final List<Properties> messages = new ArrayList<Properties>(10);
        for (final String messageResourceName : messageResourceNames) {
            final IResource messageFileResource =
                usedResourceResolver.resolveResource(configuration, context, messageResourceName, null);
            if (messageFileResource != null) {
                messages.add(loadMessages(messageFileResource));
                if (logger.isDebugEnabled()) {
                    logger.debug("[THYMELEAF][{}] Loading messages for locale \"{}\" from processed file: {}", new Object[] {TemplateEngine.threadIndex(), locale, messageResourceName});
                }
            }
        }
        
        return combineMessages(messages, defaultMessages);
        
    }
    
    
    
    
    private static Properties combineMessages(final List<Properties> props, final Properties defaultMessages) {
        
        Validate.notNull(props, "Message specifications cannot be null");
        
        final Properties messages =
            (defaultMessages == null? new Properties() : new Properties(defaultMessages));
        
        for (final Properties messagesSpecification : props) {
            if (messagesSpecification != null) {
                for (final Entry<Object,Object> entry : messagesSpecification.entrySet()) {
                    if (!messages.containsKey(entry.getKey())) {
                        messages.put(entry.getKey(), entry.getValue()); 
                    }
                }
            }
        }
        
        return messages;
        
    }
    
    
    
    
    
    private static Properties loadMessages(final IResource propertiesResource) {
        if (propertiesResource == null) {
            return null;
        }
        final Properties properties = new Properties();
        // This is not as efficient as it could be, but we avoid using the Properties.load(Reader) method
        // because it was added in Java 6. Thus we have to do quite complicated and memory-hungry things to
        // obtain an InputStream from the IResource. Luckily, this is cached most of the times.
        final String propertiesResourceStr = propertiesResource.readFully();
        try {
            properties.load(new ByteArrayInputStream(propertiesResourceStr.getBytes("ISO8859-1")));
            // No need to close this input stream - just iterating a byte[]
        } catch (final Exception e) {
            throw new TemplateInputException("Exception loading messages file", e);
        }
        return properties;
    }
    
     
    
    
    
    
    private static List<String> getMessageFileNamesFromBase(
            final String messagesFileNameBase, final Locale locale) {

        final List<String> propertiesFileNames = new ArrayList<String>(5);
        
        if (StringUtils.isEmptyOrWhitespace(locale.getLanguage())) {
            throw new TemplateProcessingException(
                    "Locale \"" + locale.toString() + "\" " +
                    "cannot be used as it does not specify a language.");
        }
        
        if (!StringUtils.isEmptyOrWhitespace(locale.getVariant())) {
            final String propertiesFileName = 
                getMessagesFileNameWithSuffix(
                        messagesFileNameBase, getSuffixForLanguageCountryVariant(locale));
            propertiesFileNames.add(propertiesFileName);
        }
        
        if (!StringUtils.isEmptyOrWhitespace(locale.getCountry())) {
            final String propertiesFileName = 
                getMessagesFileNameWithSuffix(
                        messagesFileNameBase, getSuffixForLanguageCountry(locale));
            propertiesFileNames.add(propertiesFileName);
        }
        
        if (!StringUtils.isEmptyOrWhitespace(locale.getLanguage())) {
            final String propertiesFileName = 
                getMessagesFileNameWithSuffix(
                        messagesFileNameBase, getSuffixForLanguage(locale));
            propertiesFileNames.add(propertiesFileName);
        }
        
        final String propertiesFileName = 
            getMessagesFileNameWithSuffix(
                    messagesFileNameBase, SUFFIX_FOR_DEFAULT);
        propertiesFileNames.add(propertiesFileName);
        
        return propertiesFileNames;
        
    }
    
    
    
    
    private static String getClassNameBase(final Class<?> targetClass) {
        return CLASS_NAME_SEPARATOR_PATTERN.matcher(targetClass.getName()).replaceAll("/");
    }

    
    private static String getMessagesFileNameWithSuffix(
            final String baseFileName, final String suffix) {
        if (StringUtils.isEmptyOrWhitespace(suffix)) {
            return baseFileName + PROPERTIES_SUFFIX;
        }
        return baseFileName + "_" + suffix + PROPERTIES_SUFFIX;
    }
    
    
    
    
    
    private static String getSuffixForLanguageCountryVariant(final Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry() + "-" + locale.getVariant();
    }
    
    private static String getSuffixForLanguageCountry(final Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }
    
    private static String getSuffixForLanguage(final Locale locale) {
        return locale.getLanguage();
    }



    // Calling locale.toString is surprisingly expensive, so we will try to us some shortcuts
    // NOTE there is one like this at StandardMessageResolutionUtils. It's private and duplicated because it is
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

    
    
    
    private MessageResolutionUtils() {
        super();
    }
    

}

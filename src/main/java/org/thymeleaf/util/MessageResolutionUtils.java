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
package org.thymeleaf.util;

import java.io.InputStream;
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
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.resourceresolver.IResourceResolver;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 1.0
 *
 */
public final class MessageResolutionUtils {

    
    private static final Logger logger = LoggerFactory.getLogger(MessageResolutionUtils.class);

    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String SUFFIX_FOR_DEFAULT = "";
    private static final Pattern CLASS_NAME_SEPARATOR_PATTERN = Pattern.compile("\\.");

    private static final String CLASS_CACHE_PREFIX = "{class_msg}";

    
    public static String resolveMessageForTemplate(
            final Arguments arguments, final String messageKey, final Object[] messageParameters) {
        return resolveMessageForTemplate(arguments, messageKey, messageParameters, true);
    }
    
    public static String resolveMessageForTemplate(
            final Arguments arguments, final String messageKey, final Object[] messageParameters, 
            final boolean returnStringAlways) {

        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(arguments.getContext(), "Context cannot be null");
        Validate.notNull(messageKey, "Message key cannot be null");
        
        final Set<IMessageResolver> messageResolvers = 
            arguments.getConfiguration().getMessageResolvers();
        
        MessageResolution messageResolution = null;
        for (final IMessageResolver messageResolver : messageResolvers) {
            if (messageResolution == null) {
                messageResolution =
                    messageResolver.resolveMessage(arguments, messageKey, messageParameters);
            }
        }
        
        if (messageResolution == null) {
            
            if (!returnStringAlways) {
                return null;
            }
            
            return getAbsentMessageRepresentation(messageKey, arguments.getContext().getLocale());
            
        }
        
        return messageResolution.getResolvedMessage();
        
    }
    
    
    
    
    public static String resolveMessageForClass(
            final Configuration configuration, 
            final Class<?> targetClass, final Locale locale,
            final String messageKey, final Object[] messageParameters) {
        return resolveMessageForClass(configuration, targetClass, locale, messageKey, messageParameters, true);
    }
    
    
    
    public static String resolveMessageForClass(
            final Configuration configuration, 
            final Class<?> targetClass, final Locale locale,
            final String messageKey, final Object[] messageParameters, 
            final boolean returnStringAlways) {
        
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(targetClass, "Target class cannot be null");
        Validate.notNull(locale, "Locale in context cannot be null");
        Validate.notNull(messageKey, "Message key cannot be null");

        final String className = targetClass.getName();
        final String cacheKey = CLASS_CACHE_PREFIX + className + "_" + locale.toString();
        
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
                return getAbsentMessageRepresentation(messageKey, locale);
            }

            return null;

        }

        if (messageParameters == null || messageParameters.length == 0) {
            return messageValue;
        }

        final MessageFormat messageFormat = new MessageFormat(messageValue, locale);
        return messageFormat.format(messageParameters);

    }
    
    
    
    public static String getAbsentMessageRepresentation(final String messageKey, final Locale locale) {
        Validate.notNull(messageKey, "Message key cannot be null");
        if (locale != null) {
            return "??"+messageKey+"_" + locale.toString() + "??";
        }
        return "??"+messageKey+"_" + "??";
    }
    
    
    
    
    private static Properties loadMessagesForClass(
            final Class<?> targetClass, final Locale locale) {
        
        Validate.notNull(targetClass, "Target class cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        
        final List<Properties> properties = new ArrayList<Properties>();
        Class<?> currentClass = targetClass;
        
        String base = getClassNameBase(currentClass);
        properties.add(loadCombinedMessagesFilesFromBaseName(null, null, base, locale, null));
        
        while (currentClass.getSuperclass() != Object.class) {
            currentClass = currentClass.getSuperclass();
            base = getClassNameBase(currentClass);
            properties.add(loadCombinedMessagesFilesFromBaseName(null, null, base, locale, null));
        }
        
        return combineMessages(properties, null);
        
    }

    
    
    
    
    public static Properties loadCombinedMessagesFilesFromBaseName(
            final Arguments arguments, final IResourceResolver resourceResolver,
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
        
        final List<Properties> messages = new ArrayList<Properties>();
        for (final String messageResourceName : messageResourceNames) {
            final InputStream messageFileInputStream =
                usedResourceResolver.getResourceAsStream((arguments == null? null : arguments.getTemplateProcessingParameters()), messageResourceName);
            if (messageFileInputStream != null) {
                messages.add(loadMessages(messageFileInputStream));
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
    
    
    
    
    
    private static Properties loadMessages(final InputStream propertiesIS) {
        if (propertiesIS == null) {
            return null;
        }
        final Properties properties = new Properties();
        try {
            properties.load(propertiesIS);
        } catch (final Exception e) {
            throw new TemplateInputException("Exception loading messages file", e);
        } finally {
            try {
                propertiesIS.close();
            } catch (Exception e) {
                throw new TemplateInputException("Exception loading messages file", e);
            }
        }
        return properties;
    }
    
     
    
    
    
    
    private static List<String> getMessageFileNamesFromBase(
            final String messagesFileNameBase, final Locale locale) {

        final List<String> propertiesFileNames = new ArrayList<String>();
        
        if (locale.getLanguage() == null || locale.getLanguage().trim().equals("")) {
            throw new TemplateProcessingException(
                    "Locale \"" + locale.toString() + "\" " +
                    "cannot be used as it does not specify a language.");
        }
        
        if (locale.getVariant() != null && !locale.getVariant().trim().equals("")) {
            final String propertiesFileName = 
                getMessagesFileNameWithSuffix(
                        messagesFileNameBase, getSuffixForLanguageCountryVariant(locale));
            propertiesFileNames.add(propertiesFileName);
        }
        
        if (locale.getCountry() != null && !locale.getCountry().trim().equals("")) {
            final String propertiesFileName = 
                getMessagesFileNameWithSuffix(
                        messagesFileNameBase, getSuffixForLanguageCountry(locale));
            propertiesFileNames.add(propertiesFileName);
        }
        
        if (locale.getLanguage() != null && !locale.getLanguage().trim().equals("")) {
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
        if (suffix == null || suffix.trim().equals("")) {
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
    

    
    
    
    private MessageResolutionUtils() {
        super();
    }
    

}

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
package org.thymeleaf.messageresolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
final class StandardMessageResolutionUtils {


    private static final Map<String,String> EMPTY_MESSAGES = Collections.emptyMap();
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";
    private static final Object[] EMPTY_MESSAGE_PARAMETERS = new Object[0];



    static Map<String,String> resolveMessagesForTemplate(final ITemplateResource templateResource, final Locale locale) {

        // Let the resource tell us about its 'base name'
        final String resourceBaseName = templateResource.getBaseName();
        if (resourceBaseName == null || resourceBaseName.length() == 0) {
            // No way to compute base name -> no messages
            return EMPTY_MESSAGES;
        }

        // Compute all the resource names we should use: *_gl_ES-gheada.properties, *_gl_ES.properties, _gl.properties...
        // The order here is important: as we will let values from more specific files overwrite those in less specific,
        // (e.g. a value for gl_ES will have more precedence than a value for gl). So we will iterate these resource
        // names from less specific to more specific.
        final List<String> messageResourceNames = computeMessageResourceNamesFromBase(resourceBaseName, locale);

        // Build the combined messages
        Map<String,String> combinedMessages = null;
        for (final String messageResourceName : messageResourceNames) {

            try {

                final ITemplateResource messageResource = templateResource.relative(messageResourceName);
                final Reader messageResourceReader = messageResource.reader();
                if (messageResourceReader != null) {

                    final Properties messageProperties = readMessagesResource(messageResourceReader);
                    if (messageProperties != null && !messageProperties.isEmpty()) {

                        if (combinedMessages == null) {
                            combinedMessages = new HashMap<String,String>(20);
                        }

                        for (final Map.Entry<Object,Object> propertyEntry : messageProperties.entrySet()) {
                            combinedMessages.put((String)propertyEntry.getKey(), (String)propertyEntry.getValue());
                        }

                    }

                }

            } catch (final IOException ignored) {
                // File might not exist, simply try the next one
            }

        }

        if (combinedMessages == null) {
            return EMPTY_MESSAGES;
        }

        return Collections.unmodifiableMap(combinedMessages);

    }






    static Map<String,String> resolveMessagesForOrigin(final Class<?> origin, final Locale locale) {

        final Map<String,String> combinedMessages = new HashMap<String, String>(20);

        Class<?> currentClass = origin;
        combinedMessages.putAll(resolveMessagesForSpecificClass(currentClass, locale));

        while (!currentClass.getSuperclass().equals(Object.class)) {

            currentClass = currentClass.getSuperclass();
            final Map<String,String> messagesForCurrentClass = resolveMessagesForSpecificClass(currentClass, locale);
            for (final String messageKey : messagesForCurrentClass.keySet()) {
                if (!combinedMessages.containsKey(messageKey)) {
                    combinedMessages.put(messageKey, messagesForCurrentClass.get(messageKey));
                }
            }

        }

        return Collections.unmodifiableMap(combinedMessages);

    }




    private static Map<String,String> resolveMessagesForSpecificClass(final Class<?> originClass, final Locale locale) {


        final ClassLoader originClassLoader = originClass.getClassLoader();
        final String originClassName = originClass.getName();

        final String resourceBaseName = StringUtils.replace(originClassName, ".", "/");

        // Compute all the resource names we should use: *_gl_ES-gheada.properties, *_gl_ES.properties, _gl.properties...
        // The order here is important: as we will let values from more specific files overwrite those in less specific,
        // (e.g. a value for gl_ES will have more precedence than a value for gl). So we will iterate these resource
        // names from less specific to more specific.
        final List<String> messageResourceNames = computeMessageResourceNamesFromBase(resourceBaseName, locale);

        // Build the combined messages
        Map<String,String> combinedMessages = null;
        for (final String messageResourceName : messageResourceNames) {

            final InputStream inputStream = originClassLoader.getResourceAsStream(messageResourceName);
            if (inputStream != null) {

                // At this point we cannot be specified a character encoding (that's only for template resolution),
                // so we will use the standard character encoding for .properties files, which is ISO-8859-1
                // (see Properties#load(InputStream) javadoc).
                final InputStreamReader messageResourceReader = new InputStreamReader(inputStream);

                final Properties messageProperties = readMessagesResource(messageResourceReader);
                if (messageProperties != null && !messageProperties.isEmpty()) {

                    if (combinedMessages == null) {
                        combinedMessages = new HashMap<String,String>(20);
                    }

                    for (final Map.Entry<Object,Object> propertyEntry : messageProperties.entrySet()) {
                        combinedMessages.put((String)propertyEntry.getKey(), (String)propertyEntry.getValue());
                    }

                }

            }

        }

        if (combinedMessages == null) {
            return EMPTY_MESSAGES;
        }

        return Collections.unmodifiableMap(combinedMessages);

    }




    private static List<String> computeMessageResourceNamesFromBase(
            final String resourceBaseName, final Locale locale) {

        final List<String> resourceNames = new ArrayList<String>(5);

        if (StringUtils.isEmptyOrWhitespace(locale.getLanguage())) {
            throw new TemplateProcessingException(
                    "Locale \"" + locale.toString() + "\" " +
                    "cannot be used as it does not specify a language.");
        }

        resourceNames.add(resourceBaseName + PROPERTIES_FILE_EXTENSION);
        resourceNames.add(resourceBaseName + "_" + locale.getLanguage() + PROPERTIES_FILE_EXTENSION);

        if (!StringUtils.isEmptyOrWhitespace(locale.getCountry())) {
            resourceNames.add(
                    resourceBaseName + "_" + locale.getLanguage() + "_" + locale.getCountry() + PROPERTIES_FILE_EXTENSION);
        }

        if (!StringUtils.isEmptyOrWhitespace(locale.getVariant())) {
            resourceNames.add(
                    resourceBaseName + "_" + locale.getLanguage() + "_" + locale.getCountry() + "-" + locale.getVariant() + PROPERTIES_FILE_EXTENSION);
        }

        return resourceNames;

    }




    private static Properties readMessagesResource(final Reader propertiesReader) {
        if (propertiesReader == null) {
            return null;
        }
        final Properties properties = new Properties();
        try {
            // Note Properties#load(Reader) this is JavaSE 6 specific, but Thymeleaf 3.0 does
            // not support Java 5 anymore...
            properties.load(propertiesReader);
        } catch (final Exception e) {
            throw new TemplateInputException("Exception loading messages file", e);
        } finally {
            try {
                propertiesReader.close();
            } catch (final Throwable ignored) {
                // ignore errors closing
            }
        }
        return properties;
    }



    static String formatMessage(final Locale locale, final String message, final Object[] messageParameters) {
        if (message == null) {
            return null;
        }
        if (!isFormatCandidate(message)) { // trying to avoid creating MessageFormat if not needed
            return message;
        }
        final MessageFormat messageFormat = new MessageFormat(message, locale);
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



    private StandardMessageResolutionUtils() {
        super();
    }

}

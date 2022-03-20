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

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Standard implementation of {@link IMessageResolver}.
 * </p>
 * <p>
 *   This class will first try to perform message resolution based on the template context, then
 *   on the origin, and finally on the specified default messages (if any).
 * </p>
 * <p>
 *   <strong>Step 1: Template-based message resolution</strong>
 * </p>
 * <p>
 *   For template-based resolution, not only the template being executed will be examined, but also
 *   templates corresponding to fragments being inserted, so that if template A inserts a fragment from
 *   B, and that fragment from B inserts a fragment from C, the requested message will be searched
 *   in this order: A, B, C.
 * </p>
 * <p>
 *   Note the order specified above allows container templates to override default message values specified
 *   at children (inserted fragment) templates.
 * </p>
 * <p>
 *   For each of these templates, several {@code .properties} files will be examined. For example,
 *   a message in template {@code /WEB-INF/templates/home.html} for locale
 *   {@code gl_ES-gheada} ("gl" = language, "ES" = country, "gheada" = variant) would be looked for
 *   in {@code .properties} files in the following sequence:
 * </p>
 * <ul>
 *   <li>{@code /WEB-INF/templates/home_gl_ES-gheada.properties}</li>
 *   <li>{@code /WEB-INF/templates/home_gl_ES.properties}</li>
 *   <li>{@code /WEB-INF/templates/home_gl.properties}</li>
 *   <li>{@code /WEB-INF/templates/home.properties}</li>
 * </ul>
 * <p>
 *   Note the resolution mechanism used for accessing these template-based {@code .properties} files will
 *   be the same used for resolving the templates themselves. So for templates resolved from the ServletContext
 *   its messages files will be searched at the ServletContext, for templates resolved from URL the corresponding
 *   derived URLs will be called, etc.
 * </p>
 * <p>
 *   <strong>Step 2: Origin-based message resolution</strong>
 * </p>
 * <p>
 *   If no suitable message value is found during template-based resolution, origin-based resolution
 *   is performed. This allows the resolution of messages from {@code .properties} files living
 *   in the classpath (and only in the classpath) in files corresponding with the names of the
 *   classes being used as origin.
 * </p>
 * <p>
 *   For example, a processor {@code my.company.processor.SomeDataProcessor} using its own class
 *   as <em>origin</em> will be able to resolve messages from a
 *   {@code my/company/processor/SomeDataProcessor_gl_ES.properties} file in the classpath.
 * </p>
 * <p>
 *   Also, if a message is not found there, resolution will be tried for each of the superclasses this
 *   {@code my.company.processor.SomeDataProcessor} class extends, until a suitable message is found, or
 *   no more superclasses (except {@code java.lang.Object} exist).
 * </p>
 * <p>
 *   <strong>Step 3: Defaults-based message resolution</strong>
 * </p>
 * <p>
 *   If both template-based and origin-based message resolution fail, resolution will be tried using
 *   the <em>default messages</em> specified via this class's {@link #setDefaultMessages(Properties)} or
 *   {@link #addDefaultMessage(String, String)} methods.
 * </p>
 * <p>
 *   Defaults-based message resolution is not locale-dependent.
 * </p>
 * <p>
 *   <strong>Absent message specification</strong>
 * </p>
 * <p>
 *   Message resolution will return null if no message is found, in which case callers will have the possibility
 *   to choose between asking the resolver to create an <em>absent message representation</em> or not.
 *   This is precisely what the {@code useAbsentMessageRepresentation} flag does in
 *   {@link ITemplateContext#getMessage(Class, String, Object[], boolean)}.
 * </p>
 * <p>
 *   An absent message representation looks like {@code ??mymessage_gl_ES??} and is useful to quickly determine
 *   when a message is lacking from the application's configuration. Note {@code #{...}} message expressions will
 *   always ask for an {@code absent message representation}, whereas methods in the {@code #messages}
 *   expression object will do it depending on the specific method being called.
 * </p>
 * <p>
 *   <strong>Message caching</strong>
 * </p>
 * <p>
 *   This implementation will cache template-based messages for those templates that are resolved (by their
 *   corresponding {@link org.thymeleaf.templateresolver.ITemplateResolver}) as <em>cacheable</em>. Non-cacheable
 *   templates will not have their messages cached.
 * </p>
 * <p>
 *   Origin-based messages will be always cached.
 * </p>
 * <p>
 *   <strong>Extensibility</strong>
 * </p>
 * <p>
 *   This implementation is designed for allowing the following extension points:
 * </p>
 * <ul>
 *   <li>{@link #resolveMessagesForTemplate(String, ITemplateResource, Locale)}: the mechanism for resolving
 *       the messages for a specific uncached template. Might be called several times, one per nested template.</li>
 *   <li>{@link #resolveMessagesForOrigin(Class, Locale)}: the mechanism for resolving the messages for a specific
 *       unchecked origin class. Might be called several times, one per class/superclass.</li>
 *   <li>{@link #formatMessage(Locale, String, Object[])}: the way resolved messages are actually formated along
 *       with their parameters (by default a {@link java.text.MessageFormat} is used).</li>
 *   <li>{@link #createAbsentMessageRepresentation(ITemplateContext, Class, String, Object[])}: the
 *       mechanism for creating <em>absent message</em> representations, which can be customized if needed.</li>
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public class StandardMessageResolver extends AbstractMessageResolver {

    private final ConcurrentHashMap<String,ConcurrentHashMap<Locale,Map<String,String>>> messagesByLocaleByTemplate =
            new ConcurrentHashMap<String,ConcurrentHashMap<Locale,Map<String,String>>>(20, 0.9f, 2);
    private final ConcurrentHashMap<Class<?>,ConcurrentHashMap<Locale,Map<String,String>>> messagesByLocaleByOrigin =
            new ConcurrentHashMap<Class<?>,ConcurrentHashMap<Locale,Map<String,String>>>(20, 0.9f, 2);
    private final Properties defaultMessages;


    public StandardMessageResolver() {
        super();
        this.defaultMessages = new Properties();
    }

    
    

    /**
     * <p>
     *   Returns the default messages. These messages will be used 
     *   if no other messages can be found.  
     * </p>
     * 
     * @return the default messages
     */
    public final Properties getDefaultMessages() {
        return this.defaultMessages;
    }


    /**
     * <p>
     *   Sets the default messages. These messages will be used 
     *   if no other messages can be found.
     * </p>
     * 
     * @param defaultMessages the new default messages
     */
    public final void setDefaultMessages(final Properties defaultMessages) {
        if (defaultMessages != null) {
            this.defaultMessages.putAll(defaultMessages);
        }
    }
    

    /**
     * <p>
     *   Adds a new message to the set of default messages.
     * </p>
     * 
     * @param key the message key
     * @param value the message value (text)
     */
    public final void addDefaultMessage(final String key, final String value) {
        Validate.notNull(key, "Key for default message cannot be null");
        Validate.notNull(value, "Value for default message cannot be null");
        this.defaultMessages.put(key, value);
    }

    
    /**
     * <p>
     *   Clears the set of default messages.
     * </p>
     */
    public final void clearDefaultMessages() {
        this.defaultMessages.clear();
    }







    public final String resolveMessage(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        return resolveMessage(context, origin, key, messageParameters, true, true, true);
    }


    public final String resolveMessage(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters,
            final boolean performTemplateBasedResolution, final boolean performOriginBasedResolution,
            final boolean performDefaultBasedResolution) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");

        final Locale locale = context.getLocale();

        /*
         * FIRST STEP: Look for the message using template-based resolution
         *
         * Note that resolution is top-down, this is, starts at the first-level template (the one being executed)
         * and only if a key is not found will try resolving for nested templates in the order they have been nested.
         *
         * This allows container templates to override the messages defined in fragments, which will act as defaults.
         */
        if (performTemplateBasedResolution) {

            for (final TemplateData templateData : context.getTemplateStack()) {

                final String template = templateData.getTemplate();
                final ITemplateResource templateResource = templateData.getTemplateResource();
                final boolean templateCacheable = templateData.getValidity().isCacheable();

                Map<String, String> messagesForLocaleForTemplate;

                // We will ONLY cache messages for cacheable templates. This should adequately control cache growth
                if (templateCacheable) {

                    ConcurrentHashMap<Locale, Map<String, String>> messagesByLocaleForTemplate = this.messagesByLocaleByTemplate.get(template);
                    if (messagesByLocaleForTemplate == null) {
                        this.messagesByLocaleByTemplate.putIfAbsent(template, new ConcurrentHashMap<Locale, Map<String, String>>(4));
                        messagesByLocaleForTemplate = this.messagesByLocaleByTemplate.get(template);
                    }

                    messagesForLocaleForTemplate = messagesByLocaleForTemplate.get(locale);
                    if (messagesForLocaleForTemplate == null) {
                        messagesForLocaleForTemplate = resolveMessagesForTemplate(template, templateResource, locale);
                        if (messagesForLocaleForTemplate == null) {
                            messagesForLocaleForTemplate = Collections.emptyMap();
                        }
                        messagesByLocaleForTemplate.putIfAbsent(locale, messagesForLocaleForTemplate);
                        // We retrieve it again in order to be sure its the stored map (because of the 'putIfAbsent')
                        messagesForLocaleForTemplate = messagesByLocaleForTemplate.get(locale);
                    }

                } else {

                    messagesForLocaleForTemplate = resolveMessagesForTemplate(template, templateResource, locale);
                    if (messagesForLocaleForTemplate == null) {
                        messagesForLocaleForTemplate = Collections.emptyMap();
                    }

                }

                // Once the messages map has been retrieved, just use it
                final String message = messagesForLocaleForTemplate.get(key);
                if (message != null) {
                    return formatMessage(locale, message, messageParameters);
                }

                // Will try the next resolver (if any)

            }

        }


        /*
         * SECOND STEP: Look for the message using origin-based resolution
         */
        if (performOriginBasedResolution && origin != null) {

            ConcurrentHashMap<Locale, Map<String, String>> messagesByLocaleForOrigin = this.messagesByLocaleByOrigin.get(origin);
            if (messagesByLocaleForOrigin == null) {
                this.messagesByLocaleByOrigin.putIfAbsent(origin, new ConcurrentHashMap<Locale, Map<String, String>>(4));
                messagesByLocaleForOrigin = this.messagesByLocaleByOrigin.get(origin);
            }

            Map<String, String> messagesForLocaleForOrigin = messagesByLocaleForOrigin.get(locale);
            if (messagesForLocaleForOrigin == null) {
                messagesForLocaleForOrigin = resolveMessagesForOrigin(origin, locale);
                if (messagesForLocaleForOrigin == null) {
                    messagesForLocaleForOrigin = Collections.emptyMap();
                }
                messagesByLocaleForOrigin.putIfAbsent(locale, messagesForLocaleForOrigin);
                // We retrieve it again in order to be sure its the stored map (because of the 'putIfAbsent')
                messagesForLocaleForOrigin = messagesByLocaleForOrigin.get(locale);
            }

            // Once the messages map has been retrieved, just use it
            final String message = messagesForLocaleForOrigin.get(key);
            if (message != null) {
                return formatMessage(locale, message, messageParameters);
            }

        }



        /*
         * THIRD STEP: Try default messages.
         */
        if (performDefaultBasedResolution && this.defaultMessages != null) {

            final String message = this.defaultMessages.getProperty(key);
            if (message != null) {
                return formatMessage(locale, message, messageParameters);
            }

        }


        /*
         * NOT FOUND, return null
         */
        return null;

    }






    /**
     * <p>
     *   Resolve messages for a specific template and locale.
     * </p>
     * <p>
     *   This is meant to be overridden by subclasses if necessary, so that the way in which messages
     *   are obtained for a specific template can be modified without changing the rest of the
     *   message resolution mechanisms.
     * </p>
     * <p>
     *   The standard mechanism will look for {@code .properties} files at the same location as
     *   the template (using the same resource resolution mechanism), and with the same name base.
     * </p>
     *
     * @param template the template
     * @param templateResource the template resource
     * @param locale the locale
     * @return a Map containing all the possible messages for the specified template and locale. Can return null.
     */
    protected Map<String,String> resolveMessagesForTemplate(
            final String template, final ITemplateResource templateResource, final Locale locale) {
        return StandardMessageResolutionUtils.resolveMessagesForTemplate(templateResource, locale);
    }




    /**
     * <p>
     *   Resolve messages for a specific origin and locale.
     * </p>
     * <p>
     *   This is meant to be overridden by subclasses if necessary, so that the way in which messages
     *   are obtained for a specific origin can be modified without changing the rest of the
     *   message resolution mechanisms.
     * </p>
     * <p>
     *   The standard mechanism will look for files in the classpath (only classpath),
     *   at the same package and with the same name as the origin class, with {@code .properties}
     *   extension.
     * </p>
     *
     * @param origin the origin
     * @param locale the locale
     * @return a Map containing all the possible messages for the specified origin and locale. Can return null.
     */
    protected Map<String,String> resolveMessagesForOrigin(final Class<?> origin, final Locale locale) {
        return StandardMessageResolutionUtils.resolveMessagesForOrigin(origin, locale);
    }




    /**
     * <p>
     *   Format a message, merging it with its parameters, before returning.
     * </p>
     * <p>
     *   This is meant to be overridden by subclasses if necessary. The default mechanism will simply
     *   use a standard {@link java.text.MessageFormat} instance.
     * </p>
     *
     * @param locale the locale
     * @param message the resolved message
     * @param messageParameters the message parameters (might be null)
     * @return the formatted message
     */
    protected String formatMessage(
            final Locale locale, final String message, final Object[] messageParameters) {
        return StandardMessageResolutionUtils.formatMessage(locale, message, messageParameters);
    }




    public String createAbsentMessageRepresentation(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        Validate.notNull(key, "Message key cannot be null");
        if (context.getLocale() != null) {
            return "??"+key+"_" + context.getLocale().toString() + "??";
        }
        return "??"+key+"_" + "??";
    }


}

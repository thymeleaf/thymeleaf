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
package org.thymeleaf;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.StandardCacheManager;
import org.thymeleaf.context.IContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dom.Document;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.NotInitializedException;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateOutputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templatewriter.ITemplateWriter;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Main class for the execution of templates.
 * </p>
 * <p>
 *   In order to execute Thymeleaf templates, an instance of this class (or one of
 *   its subclasses) must be created.
 * </p>
 * 
 * <h3>Creating an instance of <tt>TemplateEngine</tt></h3>
 * <p>
 *   An instance of this class can be created at any time by calling its constructor:
 * </p>
 * <code>
 *   final TemplateEngine templateEngine = new TemplateEngine();
 * </code>
 * <p>
 *   Creation and configuration of <tt>TemplateEngine</tt> instances is expensive, so it is
 *   recommended to create only one instance of this class (or at least one instance per
 *   dialect/configuration) and use it to process multiple templates.
 * </p>
 * 
 * <h3>Configuring the <tt>TemplateEngine</tt></h3>
 * <p>
 *   Once created, an instance of <tt>TemplateEngine</tt> has to be configured by
 *   setting the following <b>required</b> parameters:
 * </p>
 * <ul>
 *   <li>One or more <b>Template Resolvers</b> (instances of {@link ITemplateResolver}), in
 *       charge of reading or obtaining the templates so that the engine is able to process them. If
 *       only one template resolver is set (the most common case), the {@link #setTemplateResolver(ITemplateResolver)}
 *       method can be used for this. If more resolvers are to be set, both the
 *       {@link #setTemplateResolvers(Set)} and {@link #addTemplateResolver(ITemplateResolver)} methods
 *       can be used.</li>
 * </ul>
 * <p>
 *   Also, the following parameters can be optionally set:
 * </p>
 * <ul>
 *   <li>One or more <b>Dialects</b> (instances of {@link IDialect}), defining the way in which templates
 *       will be processed: DOM processors, expression parsers, etc. If no
 *       dialect is explicitly set, a unique instance of {@link org.thymeleaf.standard.StandardDialect}
 *       (the <i>Standard Dialect</i>) will be used.
 *       <ul>
 *         <li>Dialects define a <i>default prefix</i>, which will be used for them if not otherwise specified.</li>
 *         <li>When setting/adding dialects, a non-default prefix can be specified for each of them.</li>
 *         <li>Several dialects can use the same prefix, effectively acting as an aggregate dialect.</li>
 *         <li>All specified dialects will be validated to ensure no conflicts with DOCTYPE translations or resolution entries
 *             exist. Dialects defining a DOCTYPE translation or resolution entry <i>equal</i> to another one in a
 *             different dialect are not considered to be in conflict.</li>
 *         <li>Dialect leniency will be computed per-prefix, so that a prefix will be considered to be <i>lenient</i>
 *             if at least one of the dialects configured for it is lenient.</li>
 *         <li>Note that defining a non-default prefix for a dialect might affect its validation features
 *             if this dialect includes DTD files for such purpose (e.g. the Standard Dialect).</li> 
 *       </ul>
 *   </li>
 *   <li>One or more <b>Message Resolvers</b> (instances of {@link IMessageResolver}), in
 *       charge of resolving externalized messages. If no message resolver is explicitly set, the default
 *       setting specified by {@link #setDefaultMessageResolvers(Set)} will be applied (this
 *       default setting defaults itself to a single instance of {@link StandardMessageResolver}). 
 *       If only one message resolver is set, the {@link #setMessageResolver(IMessageResolver)} method
 *       can be used for this. If more resolvers are to be set, both the
 *       {@link #setMessageResolvers(Set)} and {@link #addMessageResolver(IMessageResolver)} methods
 *       can be used.</li>
 *   <li>A set of <b>Template Mode Handlers</b> (instances of {@link ITemplateModeHandler}, which will
 *       take care of reading/parsing templates and also writing the results of processing them for a 
 *       specific template mode. The presence of these template mode handlers defines which are the valid
 *       values for the <tt>templateMode</tt> attribute of template resolution results 
 *       ({@link TemplateResolution#getTemplateMode()}). If not explicitly set, template mode handlers
 *       will be initialized to {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}. 
 *   <li>A <b>Cache Manager</b> (instance of {@link ICacheManager}. The Cache Manager is in charge of
 *       providing the cache objects (instances of {@link org.thymeleaf.cache.ICache}) to be used for
 *       caching (at least) templates, fragments, messages and expressions. By default, a 
 *       {@link StandardCacheManager} instance is used. If a null cache manager is specified by calling
 *       {@link #setCacheManager(ICacheManager)}, no caches will be used throughout the system at all.</li>
 * </ul>
 * 
 * <h3>Template Execution</h3>
 * <h4>1. Creating a context</h4>
 * <p>
 *   All template executions require a <i>context</i>. A context is an object that
 *   implements the {@link IContext} interface, and that contains at least the following
 *   data:
 * </p>
 * <ul>
 *   <li>The <i>locale</i> to be used for message externalization (internationalization).</li>
 *   <li>The <i>context variables</i>. A map of variables that will be available for
 *       use from expressions in the executed template.</li>  
 * </ul>
 * <p>
 *   Two {@link IContext} implementations are provided out-of-the-box:
 * </p>
 * <ul>
 *   <li>{@link org.thymeleaf.context.Context}, a standard implementation containing only
 *       the required data.</li>
 *   <li>{@link org.thymeleaf.context.WebContext}, a web-specific implementation 
 *       extending the {@link org.thymeleaf.context.IWebContext} subinterface, offering
 *       request, session and servletcontext (application) attributes in special variables
 *       inside the <i>context variables</i> map. Using an implementation of 
 *       {@link org.thymeleaf.context.IWebContext} is required when using Thymeleaf for 
 *       generating HTML/XHTML interfaces in web applications.</li> 
 * </ul>
 * <p>
 *   Creating a {@link org.thymeleaf.context.Context} instance is very simple:
 * </p>
 * <code>
 *   final IContext ctx = new Context();<br />
 *   ctx.setVariable("allItems", items);
 * </code>
 * <p>
 *   A {@link org.thymeleaf.context.WebContext} would also need 
 *   {@link javax.servlet.http.HttpServletRequest} and 
 *   {@link javax.servlet.ServletContext} objects as constructor arguments: 
 * </p>
 * <code>
 *   final IContext ctx = new WebContext(request, servletContext);<br />
 *   ctx.setVariable("allItems", items);
 * </code>
 * <p>
 *   See the documentation for these specific implementations for more details.
 * </p>
 * 
 * <h4>2. Template Processing</h4>
 * <p>
 *   In order to execute templates, the {@link #process(String, IContext)} and
 *   {@link #process(String, IContext, Writer)} methods can be used:
 * </p>
 * <p>
 *   Without a writer, the processing result will be returned as a String:
 * </p>
 * <code>
 *   final String result = templateEngine.process("mytemplate", ctx);
 * </code>
 * <p>
 *   By specifying a writer, we can avoid the creation of a String containing the
 *   whole processing result by writing this result into the output stream as soon 
 *   as it is produced from the processed DOM. This is specially useful in web 
 *   scenarios:
 * </p>
 * <code>
 *   templateEngine.process("mytemplate", ctx, httpServletResponse.getWriter());
 * </code>
 * <p>
 *   The <tt>"mytemplate"</tt> String argument is the <i>template name</i>, and it
 *   will relate to the physical/logical location of the template itself in a way
 *   configured at the template resolver/s. 
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class TemplateEngine {

    /**
     * <p>
     *   Name of the <tt>TIMER</tt> logger. This logger will output the time required
     *   for executing each template processing operation.
     * </p>
     * <p>
     *   The value of this constant is <tt>org.thymeleaf.TemplateEngine.TIMER</tt>. This
     *   allows you to set a specific configuration and/or appenders for timing info at your logging
     *   system configuration.
     * </p>
     */
    public static final String TIMER_LOGGER_NAME = TemplateEngine.class.getName() + ".TIMER";
    

    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TIMER_LOGGER_NAME);
    
    private static volatile long processIndex = 0L;
    private static ThreadLocal<Long> currentProcessIndex = new ThreadLocal<Long>();
    private static ThreadLocal<Locale> currentProcessLocale = new ThreadLocal<Locale>();
    private static ThreadLocal<String> currentProcessTemplateName = new ThreadLocal<String>();
    private static ThreadLocal<TemplateEngine> currentProcessTemplateEngine = new ThreadLocal<TemplateEngine>();
    
    
    private final Configuration configuration;
    private TemplateRepository templateRepository;

    private volatile boolean initialized;
    


    

    /**
     * <p>
     *   Constructor for <tt>TemplateEngine</tt> objects.
     * </p>
     * <p>
     *   This is the only way to create a <tt>TemplateEngine</tt> instance (which
     *   should be configured after creation).
     * </p>
     */
    public TemplateEngine() {
        super();
        this.configuration = new Configuration();
        this.initialized = false;
        setCacheManager(new StandardCacheManager());
        setDefaultMessageResolvers(Collections.singleton(new StandardMessageResolver()));
        setDefaultTemplateModeHandlers(StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS);
    }

    
    


    /**
     * <p>
     *   Checks whether the <tt>TemplateEngine</tt> has already been initialized
     *   or not. A <tt>TemplateEngine</tt> is initialized when the {@link #initialize()}
     *   method is called the first time a template is processed.
     * </p>
     * <p>
     *   Normally, there is no good reason why users would need to call this method.
     * </p>
     * 
     * @return <tt>true</tt> if the template engine has already been initialized,
     *         <tt>false</tt> if not.
     */
    protected final boolean isInitialized() {
        return this.initialized;
    }


    
    /**
     * <p>
     *   Returns the configuration object. Meant to be used only by subclasses of TemplateEngine.
     * </p>
     * 
     * @return the current configuration
     */
    protected Configuration getConfiguration() {
        return this.configuration;
    }
    
    
    /**
     * <p>
     *   Returns the template repository. Normally there is no reason why users
     *   would want to obtain or use this object directly (and it is not recommended
     *   behaviour).
     * </p>
     * 
     * @return the template repository
     */
    public TemplateRepository getTemplateRepository() {
        if (!isInitialized()) {
            throw new NotInitializedException("Template Engine has not been initialized");
        }
        return this.templateRepository;
    }

    
    /**
     * <p>
     *   Returns the configured dialects, referenced by their prefixes.
     * </p>
     * 
     * @return the {@link IDialect} instances currently configured.
     */
    public final Map<String,IDialect> getDialectsByPrefix() {
        return this.configuration.getDialects();
    }
    
    
    /**
     * <p>
     *   Returns the configured dialects.
     * </p>
     * 
     * @return the {@link IDialect} instances currently configured.
     */
    public final Set<IDialect> getDialects() {
        return Collections.unmodifiableSet(
                new HashSet<IDialect>(this.configuration.getDialects().values()));
    }

    /**
     * <p>
     *   Sets a new unique dialect for this template engine.
     * </p>
     * <p>
     *   This operation is equivalent to removing all the currently configured dialects and then
     *   adding this one.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param dialect the new unique {@link IDialect} to be used.
     */
    public void setDialect(final IDialect dialect) {
        this.configuration.setDialect(dialect);
    }

    /**
     * <p>
     *   Adds a new dialect for this template engine, using the specified prefix.
     * </p>
     * <p>
     *   This dialect will be added to the set of currently configured ones.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param prefix the prefix that will be used for this dialect
     * @param dialect the new {@link IDialect} to be added to the existing ones.
     */
    public void addDialect(final String prefix, final IDialect dialect) {
        this.configuration.addDialect(prefix, dialect);
    }

    /**
     * <p>
     *   Adds a new dialect for this template engine, using the dialect's specified
     *   default dialect.
     * </p>
     * <p>
     *   This dialect will be added to the set of currently configured ones.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param dialect the new {@link IDialect} to be added to the existing ones.
     */
    public void addDialect(final IDialect dialect) {
        this.configuration.addDialect(dialect.getPrefix(), dialect);
    }

    /**
     * <p>
     *   Sets a new set of dialects for this template engine, referenced
     *   by the prefixes they will be using.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param dialects the new map of {@link IDialect} objects to be used, referenced
     *        by their prefixes.
     */
    public void setDialectsByPrefix(final Map<String,IDialect> dialects) {
        this.configuration.setDialects(dialects);
    }

    /**
     * <p>
     *   Sets a new set of dialects for this template engine, all of them using
     *   their default prefixes.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param dialects the new set of {@link IDialect} objects to be used.
     */
    public void setDialects(final Set<IDialect> dialects) {
        Validate.notNull(dialects, "Dialect set cannot be null");
        final Map<String,IDialect> dialectMap = new LinkedHashMap<String, IDialect>();
        for (final IDialect dialect : dialects)  {
            dialectMap.put(dialect.getPrefix(), dialect);
        }
        this.configuration.setDialects(dialectMap);
    }

    
    /**
     * <p>
     *   Removes all the currently configured dialects.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     */
    public void clearDialects() {
        this.configuration.clearDialects();
    }

    

    /**
     * <p>
     *   Returns the Set of template resolvers currently configured.
     * </p>
     * 
     * @return the template resolvers.
     */
    public final Set<ITemplateResolver> getTemplateResolvers() {
        return this.configuration.getTemplateResolvers();
    }

    /**
     * <p>
     *   Sets the entire set of template resolvers.
     * </p>
     * 
     * @param templateResolvers the new template resolvers.
     */
    public void setTemplateResolvers(final Set<? extends ITemplateResolver> templateResolvers) {
        this.configuration.setTemplateResolvers(templateResolvers);
    }

    /**
     * <p>
     *   Adds a new template resolver to the current set.
     * </p>
     * 
     * @param templateResolver the new template resolver.
     */
    public void addTemplateResolver(final ITemplateResolver templateResolver) {
        this.configuration.addTemplateResolver(templateResolver);
    }

    /**
     * <p>
     *   Sets a single template resolver for this template engine.
     * </p>
     * <p>
     *   Calling this method is equivalent to calling {@link #setTemplateResolvers(Set)}
     *   passing a Set with only one template resolver.
     * </p>
     * 
     * @param templateResolver the template resolver to be set.
     */
    public void setTemplateResolver(final ITemplateResolver templateResolver) {
        this.configuration.setTemplateResolver(templateResolver);
    }

    
    /**
     * <p>
     *   Returns the cache manager in effect. This manager is in charge of providing
     *   the various caches needed by the system during its process.
     * </p>
     * <p>
     *   By default, an instance of {@link org.thymeleaf.cache.StandardCacheManager}
     *   is set.
     * </p>
     * 
     * @return the cache manager
     */
    public ICacheManager getCacheManager() {
        return this.configuration.getCacheManager();
    }
    
    /**
     * <p>
     *   Sets the Cache Manager to be used. If set to null, no caches will be used 
     *   throughout the engine.
     * </p>
     * <p>
     *   By default, an instance of {@link org.thymeleaf.cache.StandardCacheManager}
     *   is set.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param cacheManager the cache manager to be set.
     * 
     */
    public void setCacheManager(final ICacheManager cacheManager) {
        // Can be set to null (= no caches at all)
        this.configuration.setCacheManager(cacheManager);
    }

    
    /**
     * <p>
     *   Returns the set of Message Resolvers configured for this Template Engine.
     * </p>
     * 
     * @return the set of message resolvers.
     */
    public final Set<IMessageResolver> getMessageResolvers() {
        return this.configuration.getMessageResolvers();
    }

    /**
     * <p>
     *   Sets the message resolvers to be used by this template engine.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param messageResolvers the Set of template resolvers.
     */
    public void setMessageResolvers(final Set<? extends IMessageResolver> messageResolvers) {
        this.configuration.setMessageResolvers(messageResolvers);
    }
    
    /**
     * <p>
     *   Adds a message resolver to the set of message resolvers to be used
     *   by the template engine.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param messageResolver the new message resolver to be added.
     */
    public void addMessageResolver(final IMessageResolver messageResolver) {
        this.configuration.addMessageResolver(messageResolver);
    }

    /**
     * <p>
     *   Sets a single message resolver for this template engine.
     * </p>
     * <p>
     *   Calling this method is equivalent to calling {@link #setMessageResolvers(Set)}
     *   passing a Set with only one message resolver.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param messageResolver the message resolver to be set.
     */
    public void setMessageResolver(final IMessageResolver messageResolver) {
        this.configuration.setMessageResolver(messageResolver);
    }

    /**
     * <p>
     *   Sets the default message resolvers. These are used when no message resolvers
     *   are set via the {@link #setMessageResolver(IMessageResolver)}, 
     *   {@link #setMessageResolvers(Set)} or {@link #addMessageResolver(IMessageResolver)}
     *   methods.
     * </p>
     * <p>
     *   This method is useful for creating subclasses of <tt>TemplateEngine</tt> that
     *   establish default configurations for message resolvers.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param defaultMessageResolvers the default message resolvers.
     */
    public void setDefaultMessageResolvers(final Set<? extends IMessageResolver> defaultMessageResolvers) {
        this.configuration.setDefaultMessageResolvers(defaultMessageResolvers);
    }

    
    /**
     * <p>
     *   Returns the set of Template Mode Handlers configured for this 
     *   Template Engine.
     * </p>
     * <p>
     *   By default, template mode handlers set are
     *   {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
     * </p>
     * 
     * @return the set of Template Mode Handlers.
     */
    public final Set<ITemplateModeHandler> getTemplateModeHandlers() {
        return this.configuration.getTemplateModeHandlers();
    }

    /**
     * <p>
     *   Sets the Template Mode Handlers to be used by this template engine.
     *   Every available template mode must have its corresponding handler.
     * </p>
     * <p>
     *   By default, template mode handlers set are
     *   {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param templateModeHandlers the Set of Template Mode Handlers.
     */
    public void setTemplateModeHandlers(final Set<? extends ITemplateModeHandler> templateModeHandlers) {
        this.configuration.setTemplateModeHandlers(templateModeHandlers);
    }
    
    /**
     * <p>
     *   Adds a Template Mode Handler to the set of Template Mode Handlers to be used
     *   by the template engine.
     *   Every available template mode must have its corresponding handler.
     * </p>
     * <p>
     *   By default, template mode handlers set are
     *   {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param templateModeHandler the new Template Mode Handler to be added.
     */
    public void addTemplateModeHandler(final ITemplateModeHandler templateModeHandler) {
        this.configuration.addTemplateModeHandler(templateModeHandler);
    }

    /**
     * <p>
     *   Sets the default Template Mode Handlers. These are used when no Template Mode Handlers
     *   are set via the {@link #setTemplateModeHandlers(Set)} or 
     *   {@link #addTemplateModeHandler(ITemplateModeHandler)} methods.
     * </p>
     * <p>
     *   This method is useful for creating subclasses of <tt>TemplateEngine</tt> that
     *   establish default configurations for Template Mode Handlers.
     * </p>
     * <p>
     *   By default, template mode handlers set are
     *   {@link StandardTemplateModeHandlers#ALL_TEMPLATE_MODE_HANDLERS}
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param defaultTemplateModeHandlers the default Template Mode Handlers.
     */
    public void setDefaultTemplateModeHandlers(final Set<? extends ITemplateModeHandler> defaultTemplateModeHandlers) {
        this.configuration.setDefaultTemplateModeHandlers(defaultTemplateModeHandlers);
    }
    
    
    
    

    
    
    /**
     * <p>
     *   Completely clears the Template Cache.
     * </p>
     * <p>
     *   If this method is called before the TemplateEngine has been initialized,
     *   it causes its initialization.
     * </p>
     */
    public void clearTemplateCache() {
        if (!isInitialized()) {
            initialize();
        }
        this.templateRepository.clearTemplateCache();
    }


    /**
     * <p>
     *   Clears the entry in the Template Cache for the specified
     *   template, if it is currently cached.
     * </p>
     * <p>
     *   If this method is called before the TemplateEngine has been initialized,
     *   it causes its initialization.
     * </p>
     * 
     * @param templateName the name of the template to be cleared from cache.
     */
    public void clearTemplateCacheFor(final String templateName) {
        Validate.notNull(templateName, "Template name cannot be null");
        if (!isInitialized()) {
            initialize();
        }
        this.templateRepository.clearTemplateCacheFor(templateName);
    }
    
    
    
    
    
    
    
    /**
     * <p>
     *   Internal method that initializes the Template Engine instance. This method 
     *   is called before the first execution of {@link #process(String, IContext)} 
     *   in order to create all the structures required for a quick execution of 
     *   templates.
     * </p>
     * <p>
     *   THIS METHOD IS INTERNAL AND SHOULD <b>NEVER</b> BE CALLED DIRECTLY.
     * </p>
     * <p>
     *   If a subclass of <tt>TemplateEngine</tt> needs additional steps for
     *   initialization, the {@link #initializeSpecific()} method should
     *   be overridden.
     * </p>
     */
    public final synchronized void initialize() {
        
        if (!isInitialized()) {
            
            logger.info("[THYMELEAF] INITIALIZING TEMPLATE ENGINE");

            this.configuration.initialize();
            
            this.templateRepository = new TemplateRepository(this.configuration);
            
            initializeSpecific();
            
            this.initialized = true;

            // Log configuration details
            this.configuration.printConfiguration();
            
            logger.info("[THYMELEAF] TEMPLATE ENGINE INITIALIZED");
            
        }
        
    }
    

    /**
     * <p>
     *   This method performs additional initializations required for a
     *   <tt>TemplateEngine</tt>. It is called by {@link #initialize()}.
     * </p>
     * <p> 
     *   The implementation of this method does nothing, and it is designed 
     *   for being overridden by subclasses of <tt>TemplateEngine</tt>. 
     * </p>
     */
    protected void initializeSpecific() {
        // Nothing to be executed here. Meant for extension
    }
    
    
    
    /**
     * <p>
     *   Internal method that retrieves the thread-local index for the
     *   current template execution. 
     * </p>
     * <p>
     *   THIS METHOD IS INTERNAL AND SHOULD <b>NEVER</b> BE CALLED DIRECTLY.
     * </p>
     * 
     * @return the index of the current execution.
     */
    public static Long threadIndex() {
        return currentProcessIndex.get();
    }

    /**
     * <p>
     *   Internal method that retrieves the thread-local locale for the
     *   current template execution. 
     * </p>
     * <p>
     *   THIS METHOD IS INTERNAL AND SHOULD <b>NEVER</b> BE CALLED DIRECTLY.
     * </p>
     * 
     * @return the locale of the current template execution.
     */
    public static Locale threadLocale() {
        return currentProcessLocale.get();
    }

    
    private static void newThreadIndex() {
        currentProcessIndex.set(Long.valueOf(processIndex++));
    }
    

    private static void setThreadLocale(final Locale locale) {
        currentProcessLocale.set(locale);
    }

    
    
    /**
     * <p>
     *   Internal method that retrieves the thread-local template name for the
     *   current template execution. 
     * </p>
     * <p>
     *   THIS METHOD IS INTERNAL AND SHOULD <b>NEVER</b> BE CALLED DIRECTLY.
     * </p>
     * 
     * @return the template name for the current engine execution.
     */
    public static String threadTemplateName() {
        return currentProcessTemplateName.get();
    }

    
    private static void setThreadTemplateName(final String templateName) {
        currentProcessTemplateName.set(templateName);
    }

    
    
    /**
     * <p>
     *   Internal method that retrieves the thread-local template engine for the
     *   current template execution. 
     * </p>
     * <p>
     *   THIS METHOD IS INTERNAL AND SHOULD <b>NEVER</b> BE CALLED DIRECTLY.
     * </p>
     * 
     * @return the template engine for the current engine execution.
     * 
     * @since 2.0.9
     */
    public static TemplateEngine threadTemplateEngine() {
        return currentProcessTemplateEngine.get();
    }

    
    private static void setThreadTemplateEngine(final TemplateEngine templateEngine) {
        currentProcessTemplateEngine.set(templateEngine);
    }


    

    /**
     * <p>
     *   Process a template. This method receives both a <i>template name</i> and a <i>context</i>.
     * </p>
     * <p>
     *   The template name will be used as input for the template resolvers, queried in chain
     *   until one of them resolves the template, which will then be executed.
     * </p>
     * <p>
     *   The context will contain the variables that will be available for the execution of
     *   expressions inside the template.
     * </p>
     * 
     * @param templateName the name of the template.
     * @param context the context.
     * @return a String containing the result of evaluating the specified template 
     *         with the provided context.
     */
    public final String process(final String templateName, final IContext context) {
        final StringWriter stringWriter = new StringWriter();
        process(templateName, context, stringWriter);
        return stringWriter.toString();
    }
    


    /**
     * <p>
     *   Process a template. This method receives a <i>template name</i>, a <i>context</i> and
     *   also a {@link Writer}, so that there is no need to create a String object containing the
     *   whole processing results because these will be written to the specified writer as
     *   soon as they are generated from the processed DOM tree. This is specially useful for
     *   web environments (using {@link javax.servlet.http.HttpServletResponse#getWriter()}).
     * </p>
     * <p>
     *   The template name will be used as input for the template resolvers, queried in chain
     *   until one of them resolves the template, which will then be executed.
     * </p>
     * <p>
     *   The context will contain the variables that will be available for the execution of
     *   expressions inside the template.
     * </p>
     * 
     * @param templateName the name of the template.
     * @param context the context.
     * @param writer the writer the results will be output to.
     * 
     * @since 2.0.0 
     */
    public final void process(final String templateName, final IContext context, final Writer writer) {
        
        if (!isInitialized()) {
            initialize();
        }
        
        try {
            
            Validate.notNull(templateName, "Template name cannot be null");
            Validate.notNull(context, "Context cannot be null");
            
            
            final long startMs = System.nanoTime();

            setThreadTemplateName(templateName);
            
            newThreadIndex();
            setThreadLocale(context.getLocale());
            setThreadTemplateEngine(this);

            if (logger.isDebugEnabled()) {
                logger.debug("[THYMELEAF][{}] STARTING PROCESS OF TEMPLATE \"{}\" WITH LOCALE {}", new Object[] {TemplateEngine.threadIndex(), templateName, context.getLocale()});
            }
            
            // Add context execution info
            context.addContextExecutionInfo(templateName);
            
            final TemplateProcessingParameters templateProcessingParameters = 
                new TemplateProcessingParameters(this.configuration, templateName, context);
            
            process(templateProcessingParameters, writer);
            
            final long endMs = System.nanoTime();
            
            if (logger.isDebugEnabled()) {
                logger.debug("[THYMELEAF][{}] FINISHED PROCESS AND OUTPUT OF TEMPLATE \"{}\" WITH LOCALE {}", new Object[] {TemplateEngine.threadIndex(), templateName, context.getLocale()});
            }
            
            if (timerLogger.isDebugEnabled()) {
                final BigDecimal elapsed = BigDecimal.valueOf(endMs - startMs);
                final BigDecimal elapsedMs = elapsed.divide(BigDecimal.valueOf(1000000), RoundingMode.HALF_UP);
                timerLogger.debug(
                        "[THYMELEAF][{}][{}][{}][{}][{}] TEMPLATE \"{}\" WITH LOCALE {} PROCESSED IN {} nanoseconds (approx. {}ms)", 
                        new Object[] {TemplateEngine.threadIndex(), 
                                templateName, context.getLocale(), elapsed, elapsedMs,
                                templateName, context.getLocale(), elapsed, elapsedMs});
            }
            
        } catch (final TemplateOutputException e) {
            logger.error("[THYMELEAF][{}] Exception processing template \"{}\": {}", new Object[] {TemplateEngine.threadIndex(), templateName, e.getMessage()});
            throw e;
        } catch (final TemplateEngineException e) {
            logger.error("[THYMELEAF][{}] Exception processing template \"{}\": {}", new Object[] {TemplateEngine.threadIndex(), templateName, e.getMessage()});
            throw e;
        } catch (final RuntimeException e) {
            logger.error("[THYMELEAF][{}] Exception processing template \"{}\": {}", new Object[] {TemplateEngine.threadIndex(), templateName, e.getMessage()});
            throw new TemplateProcessingException("Exception processing template", templateName, e);
        }
        
    }
    
    

    private final void process(final TemplateProcessingParameters templateProcessingParameters, final Writer writer) {
        
        final String templateName = templateProcessingParameters.getTemplateName();
        
        final Template template = this.templateRepository.getTemplate(templateProcessingParameters);
            
        final TemplateResolution templateResolution = template.getTemplateResolution();
        
        final Document document = template.getDocument();
        
        final Arguments arguments = 
            new Arguments(templateProcessingParameters, templateResolution, this.templateRepository, document);
        
        if (logger.isDebugEnabled()) {
            logger.debug("[THYMELEAF][{}] Starting process on template \"{}\" using mode \"{}\"", 
                    new Object[] { TemplateEngine.threadIndex(), templateName, templateResolution.getTemplateMode() });
        }
        
        document.process(arguments);
        
        if (logger.isDebugEnabled()) {
            logger.debug("[THYMELEAF][{}] Finished process on template \"{}\" using mode \"{}\"", 
                    new Object[] { TemplateEngine.threadIndex(), templateName, templateResolution.getTemplateMode() });
        }
        
        final String templateMode = 
                arguments.getTemplateResolution().getTemplateMode();
        final ITemplateModeHandler templateModeHandler =
                this.configuration.getTemplateModeHandler(templateMode);
        final ITemplateWriter templateWriter = templateModeHandler.getTemplateWriter();

        if (templateWriter == null) {
            throw new ConfigurationException(
                    "No template writer defined for template mode \"" + templateMode + "\"");
        }
        
        try {
            templateWriter.write(arguments, writer, document);
        } catch (IOException e) {
            throw new TemplateOutputException("Error during creation of output", e);
        }
    
    }

    


    
    
}

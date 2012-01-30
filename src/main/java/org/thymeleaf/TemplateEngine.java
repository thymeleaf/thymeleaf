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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.IContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.translation.IDocTypeTranslation;
import org.thymeleaf.exceptions.OutputCreationException;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;


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
 *       will be processed: attributes, tags, value and expression processors, etc. If no
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
 *   <li>The <b>size of the template cach&eacute;</b>, set by means of the
 *       {@link #setParsedTemplateCacheSize(int)} method. This cach&eacute; will be used to store the
 *       parsed DOM trees of templates in order to reduce the amount of input/output operations
 *       needed. It uses the LRU (Least Recently Used) algorithm. If not set, this parameter
 *       will default to {@link Configuration#DEFAULT_PARSED_TEMPLATE_CACHE_SIZE}.</li>
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
 *   A {@link org.thymeleaf.context.WebContext} will also need an 
 *   {@link javax.servlet.http.HttpServletRequest} object as a constructor argument: 
 * </p>
 * <code>
 *   final IContext ctx = new WebContext(request);<br />
 *   ctx.setVariable("allItems", items);
 * </code>
 * <p>
 *   See the documentation for these specific implementations for more details.
 * </p>
 * 
 * <h4>2. Template Processing</h4>
 * <p>
 *   In order to execute templates, the {@link #process(String, IContext)} method
 *   will be used:
 * </p>
 * <code>
 *   final String result = templateEngine.process("mytemplate", ctx);
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
    

    /**
     * <p>
     *   Constant containing the char that is used for substituting the &amp; character
     *   in entities present in DOM Text nodes. These symbols are substituted so that
     *   Thymeleaf can output entities in Text nodes in exactly the way they are written
     *   in templates, without resolving or re-escaping them. 
     * </p>
     * <p>
     *   Developers should never have the need to use this constant unless they are manually
     *   processing Text nodes that might contain ampersand ('&') symbols.
     * </p>
     * 
     * @since 1.1
     */
    public static final char CHAR_ENTITY_START_SUBSTITUTE = '\u0194';

    
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final Logger timerLogger = LoggerFactory.getLogger(TIMER_LOGGER_NAME);
    
    private static long processIndex = 0L;
    private static ThreadLocal<Long> currentProcessIndex = new ThreadLocal<Long>();
    private static ThreadLocal<Locale> currentProcessLocale = new ThreadLocal<Locale>();
    private static ThreadLocal<String> currentProcessTemplateName = new ThreadLocal<String>();
    
    
    private final Configuration configuration;
    private TemplateParser templateParser;

    private boolean initialized;
    


    

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
        setDefaultMessageResolvers(Collections.singleton(new StandardMessageResolver()));
    }

    
    


    /**
     * <p>
     *   Checks whether the <tt>TemplateEngine</tt> has already been initialized
     *   or not. A <tt>TemplateEngine</tt> is initialized when the {@link #initialize()}
     *   method is called the first time a template is processed.
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
     * 
     * @param messageResolver the new message resolver to be added.
     */
    public void addMessageResolver(final IMessageResolver messageResolver) {
        this.configuration.addMessageResolver(messageResolver);
    }

    /**
     * <p>
     *   Sets a single messae resolver for this template engine.
     * </p>
     * <p>
     *   Calling this method is equivalent to calling {@link #setMessageResolvers(Set)}
     *   passing a Set with only one message resolver.
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
     * 
     * @param defaultMessageResolvers the default message resolvers.
     */
    public void setDefaultMessageResolvers(final Set<? extends IMessageResolver> defaultMessageResolvers) {
        this.configuration.setDefaultMessageResolvers(defaultMessageResolvers);
    }


    /**
     * <p>
     *   Returns the size of the LRU cach&eacute; of parsed templates.
     * </p>
     * 
     * @return the current size of the cach&eacute;
     */
    public final int getParsedTemplateCacheSize() {
        return this.configuration.getParsedTemplateCacheSize();
    }

    
    /**
     * <p>
     *   Sets the new size for the template cach&eacute;.
     * </p>
     * <p>
     *   This operation can only be executed before processing templates for the first
     *   time. Once a template is processed, the template engine is considered to be
     *   <i>initialized</i>, and from then on any attempt to change its configuration
     *   will result in an exception.
     * </p>
     * 
     * @param parsedTemplateCacheSize the new size for the cach&eacute;
     */
    public void setParsedTemplateCacheSize(final int parsedTemplateCacheSize) {
        this.configuration.setParsedTemplateCacheSize(parsedTemplateCacheSize);
    }
    
    
    
    

    
    
    /**
     * <p>
     *   Completely clears the Parsed Template Cache.
     * </p>
     * <p>
     *   If this method is called before the TemplateEngine has been initialized,
     *   it causes its initialization.
     * </p>
     */
    public void clearParsedTemplateCache() {
        if (!isInitialized()) {
            initialize();
        }
        this.templateParser.clearParsedTemplateCache();
    }


    /**
     * <p>
     *   Clears the entry in the Parsed Template Cache for the specified
     *   template, if it is currently cached.
     * </p>
     * <p>
     *   If this method is called before the TemplateEngine has been initialized,
     *   it causes its initialization.
     * </p>
     * 
     * @param templateName the name of the template to be cleared from cache.
     */
    public void clearParsedTemplateCacheFor(final String templateName) {
        Validate.notNull(templateName, "Template name cannot be null");
        if (!isInitialized()) {
            initialize();
        }
        this.templateParser.clearParsedTemplateCacheFor(templateName);
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
    protected final synchronized void initialize() {
        
        if (!isInitialized()) {
            
            logger.info("[THYMELEAF] INITIALIZING TEMPLATE ENGINE");

            this.configuration.initialize();
            
            this.templateParser = new TemplateParser(this.configuration);
            
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

    
    private synchronized static void newThreadIndex() {
        currentProcessIndex.set(Long.valueOf(processIndex++));
    }
    

    private synchronized static void setThreadLocale(final Locale locale) {
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

    
    private synchronized static void setThreadTemplateName(final String templateName) {
        currentProcessTemplateName.set(templateName);
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

            if (logger.isDebugEnabled()) {
                logger.debug("[THYMELEAF][{}] STARTING PROCESS OF TEMPLATE \"{}\" WITH LOCALE {}", new Object[] {TemplateEngine.threadIndex(), templateName, context.getLocale()});
            }
            
            // Add context execution info
            context.addContextExecutionInfo(templateName);
            
            final Arguments arguments = 
                new Arguments(this.configuration, this.templateParser, templateName, context);
            
            final String result = process(arguments);
            
            final long endMs = System.nanoTime();
            
            if (logger.isDebugEnabled()) {
                logger.debug("[THYMELEAF][{}] FINISHED PROCESS OF TEMPLATE \"{}\" WITH LOCALE {}", new Object[] {TemplateEngine.threadIndex(), templateName, context.getLocale()});
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
            
            return result;

        } catch (final OutputCreationException e) {
            final Throwable cause = e.getCause();
            if (cause != null && cause instanceof TransformerException) {
                processOutputTransformerException((TransformerException) cause, templateName);
            }
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

    

    
    private final String process(final Arguments arguments) {
        
        final String templateName = arguments.getTemplateName();
        
        final ParsedTemplate parsedTemplate = this.templateParser.parseDocument(arguments);
            
        final TemplateResolution templateResolution = parsedTemplate.getTemplateResolution();
        final Document document = parsedTemplate.getDocument();
        
        if (logger.isDebugEnabled()) {
            logger.debug("[THYMELEAF][{}] Starting DOM transformations on template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }
        
        final DocumentType documentType = 
            DOMDocumentProcessor.transform(arguments, templateResolution, document);
        
        if (logger.isDebugEnabled()) {
            logger.debug("[THYMELEAF][{}] Finished DOM transformations on template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }

        boolean outputDocType = false;
        DocTypeIdentifier outputPublicId = null;
        DocTypeIdentifier outputSystemId = null;
        
        final TemplateMode templateMode = templateResolution.getTemplateMode();
        
        if (documentType != null) {
            
            final String publicId = documentType.getPublicId();
            final String systemId = documentType.getSystemId();

            if (logger.isDebugEnabled()) {
                if (publicId == null || publicId.trim().equals("")) {
                    logger.debug("[THYMELEAF][{}] Original DOCTYPE is: SYSTEM \"{}\"", TemplateEngine.threadIndex(), systemId);
                } else {
                    logger.debug("[THYMELEAF][{}] Original DOCTYPE is: PUBLIC \"{}\" \"{}\"", new Object[] {TemplateEngine.threadIndex(), publicId, systemId});
                }
            }
            final IDocTypeTranslation translation =
                this.configuration.getDocTypeTranslationBySource(publicId, systemId);
            outputDocType = true;
            if (translation != null) {
                outputPublicId = translation.getTargetPublicID();
                outputSystemId = translation.getTargetSystemID();
                if (logger.isDebugEnabled()) {
                    if (outputPublicId.isNone()) {
                        logger.debug("[THYMELEAF][{}] Translated DOCTYPE is: SYSTEM \"{}\"", TemplateEngine.threadIndex(), outputSystemId);
                    } else {
                        logger.debug("[THYMELEAF][{}] Translated DOCTYPE is: PUBLIC \"{}\" \"{}\"", new Object[] {TemplateEngine.threadIndex(), outputPublicId, outputSystemId});
                    }
                }
            } else {
                outputPublicId = DocTypeIdentifier.forValue(documentType.getPublicId());
                outputSystemId = DocTypeIdentifier.forValue(documentType.getSystemId());
                if (logger.isDebugEnabled()) {
                    logger.debug("[THYMELEAF][{}] DOCTYPE will not be translated", TemplateEngine.threadIndex());
                }
            }
            
        } else {
            
            if (templateMode.isHTML5()) {
                outputDocType = true;
                outputPublicId = DocTypeIdentifier.NONE;
                outputSystemId = DocTypeIdentifier.NONE;
            }
            
        }
        
        final String output = 
            OutputHandler.output(arguments, templateResolution, document, outputDocType, outputPublicId, outputSystemId);
        
        return output;
    
    }

    
    

    private static void processOutputTransformerException(final TransformerException e, final String templateName) {
        
        final Throwable cause = e.getCause();
        if (cause != null && cause instanceof RuntimeException) {
            final String msg = cause.getMessage();
            if (msg != null && msg.contains("Namespace for prefix") && msg.contains("has not been declared")) {
                
                final String explanation =
                    msg + " This can happen if your output still contains tags/attributes with a prefix for which " +
                    "an xmlns:* attribute has not been declared at the document root (maybe an unprocessed tag/attribute - check your dialects' leniency)";
                
                logger.error("[THYMELEAF][{}] Exception processing template \"{}\": {}", new Object[] {TemplateEngine.threadIndex(), templateName, explanation});
                throw new TemplateProcessingException(explanation);
                
            }
        }
            
    }
    
    
    
}

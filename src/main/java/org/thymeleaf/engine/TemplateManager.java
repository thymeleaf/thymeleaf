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
package org.thymeleaf.engine;

import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateparser.markup.XMLTemplateParser;
import org.thymeleaf.templateparser.text.CSSTemplateParser;
import org.thymeleaf.templateparser.text.JavaScriptTemplateParser;
import org.thymeleaf.templateparser.text.TextTemplateParser;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class TemplateManager {

    private static final Logger logger = LoggerFactory.getLogger(TemplateManager.class);

    private static final int DEFAULT_PARSER_POOL_SIZE = 40;
    private static final int DEFAULT_PARSER_BLOCK_SIZE = 2048;

    private static final Set<ITemplateResolver> STRING_TEMPLATE_RESOLVER_SET =
            Collections.singleton((ITemplateResolver)new StringTemplateResolver());

    private final IEngineConfiguration configuration;

    private final ITemplateParser htmlParser;
    private final ITemplateParser xmlParser;
    private final ITemplateParser textParser;
    private final ITemplateParser javascriptParser;
    private final ITemplateParser cssParser;


    private final ICache<TemplateCacheKey,TemplateModel> templateCache; // might be null! (= no cache)





    /**
     * <p>
     *   This constructor should only be called directly for <strong>testing purposes</strong>.
     * </p>
     *
     * @param configuration the engine configuration
     */
    public TemplateManager(final IEngineConfiguration configuration) {
        
        super();

        Validate.notNull(configuration, "Configuration cannot be null");

        this.configuration = configuration;

        final ICacheManager cacheManager = this.configuration.getCacheManager();

        if (cacheManager == null) {
            this.templateCache = null;
        } else {
            this.templateCache = cacheManager.getTemplateCache();
        }

        final boolean standardDialectPresent = this.configuration.isStandardDialectPresent();
        final String standardDialectPrefix = this.configuration.getStandardDialectPrefix();

        // TODO Make these parser implementations configurable: one parser per template mode, then make default implementations extensible/configurable (e.g. AttoParser config)
        this.htmlParser = new HTMLTemplateParser(DEFAULT_PARSER_POOL_SIZE,DEFAULT_PARSER_BLOCK_SIZE);
        this.xmlParser = new XMLTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE);
        this.textParser = new TextTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, standardDialectPresent, standardDialectPrefix);
        this.javascriptParser = new JavaScriptTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, standardDialectPresent, standardDialectPrefix);
        this.cssParser = new CSSTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, standardDialectPresent, standardDialectPrefix);

    }
    

    
    
    
    /**
     * <p>
     *   Clears the template cache.
     * </p>
     */
    public void clearCaches() {
        if (this.templateCache != null) {
            this.templateCache.clear();
        }
    }

    
    /**
     * <p>
     *   Clears any existing entries for template of the specified
     *   name at the template cache.
     * </p>
     * 
     * @param template the name of the template whose entries have to be cleared.
     */
    public void clearCachesFor(final String template) {
        Validate.notNull(template, "Cannot specify null template");
        if (this.templateCache != null) {
            final Set<TemplateCacheKey> keysToBeRemoved = new HashSet<TemplateCacheKey>(4);
            final Set<TemplateCacheKey> templateCacheKeys = this.templateCache.keySet();
            // We are iterating twice and creating a temporary set just in case the 'keySet' Set is still connected
            // to the original cache store and we provoke ConcurrentModificationExceptions when removing entries
            for (final TemplateCacheKey templateCacheKey : templateCacheKeys) {
                final String ownerTemplate = templateCacheKey.getOwnerTemplate();
                if (ownerTemplate != null) {
                    // It's not a standalone template, so we are interested on the owner template
                    if (ownerTemplate.equals(template)) {
                        keysToBeRemoved.add(templateCacheKey);
                    }
                } else {
                    if (templateCacheKey.getTemplate().equals(template)) {
                        keysToBeRemoved.add(templateCacheKey);
                    }
                }
            }
            for (final TemplateCacheKey keyToBeRemoved : keysToBeRemoved) {
                this.templateCache.clearKey(keyToBeRemoved);
            }
        }
    }






    /*
     * -------------
     * PARSE methods
     * -------------
     *
     * Parse methods will create 'template models' that are basically collections of events in the form of an
     * immutable IModel implementation.
     */


    public TemplateModel parseStandalone(
            final String template, final String[] templateSelectors,
            final TemplateMode templateMode,
            final boolean useCache) {

        Validate.notNull(template, "Template cannot be null");
        // templateSelectors CAN be null if we are going to render the entire template
        // templateMode CAN be null if we are going to use the mode specified by the template resolver

        return parse(
                null, template, templateSelectors,
                0, 0,
                templateMode,
                useCache);

    }


    public TemplateModel parseNested(
            final String ownerTemplate, final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final boolean useCache) {

        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when parsing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");

        return parse(
                ownerTemplate, template, null,
                lineOffset, colOffset,
                templateMode,
                useCache);

    }


    private TemplateModel parse(
            final String ownerTemplate, final String template, final String[] templateSelectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final boolean useCache) {


        final TemplateCacheKey cacheKey =
                useCache? new TemplateCacheKey(ownerTemplate, template, templateSelectors, lineOffset, colOffset, templateMode) : null;

        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.templateCache != null) {
            final TemplateModel cached =  this.templateCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }


        /*
         * If this template is a textual template (i.e. not a template name, but the template itself expressed
         * as a String object), we need to force the use of a StringTemplateResolver for resolution
         */
        final Set<ITemplateResolver> templateResolvers =
                (ownerTemplate != null? STRING_TEMPLATE_RESOLVER_SET : this.configuration.getTemplateResolvers());


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final Resolution resolution = resolve(this.configuration, templateResolvers, template, templateMode, useCache);

        // TODO when called from utext or others, it might be better to check if there are any structures and return a mere Text if not...

        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateModel object as the result of reading the template
         */
        final TemplateModel parsedTemplate = new TemplateModel(this.configuration, resolution.templateResolution);
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(parsedTemplate.getInternalModel());


        /*
         * PROCESS THE TEMPLATE
         */
        processResolvedResource(
                ownerTemplate, template, resolution.templateResource, templateSelectors,
                lineOffset, colOffset,
                resolution.templateResolution.getTemplateMode(),
                builderHandler);


        /*
         * Cache the template if it is cacheable
         */
        if (useCache && this.templateCache != null) {
            if (resolution.templateResolution.getValidity().isCacheable()) {
                this.templateCache.put(cacheKey, parsedTemplate);
            }
        }
        
        return parsedTemplate;
        
    }






    /*
     * ---------------
     * PROCESS methods
     * ---------------
     *
     * Processing means executing a template that has already been parsed into a TemplateModel object
     */


    public void process(
            final TemplateModel template,
            final IContext context,
            final Writer writer) {

        Validate.isTrue(
                this.configuration == template.getConfiguration(),
                "Specified template was built by a different Template Engine instance");

        /*
         * Create the context instance that corresponds to this execution of the template engine
         */
        final IEngineContext engineContext =
                EngineContextManager.prepareEngineContext(this.configuration, template.getTemplateResolution(), context);

        /*
         * Create the handler chain to process the data
         */
        final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext, writer);

        /*
         *  Process the template
         */
        processTemplateModel(template, processingHandlerChain);


        /*
         * Dispose the engine context now that processing has been done
         */
        EngineContextManager.disposeEngineContext(engineContext);

    }






    /*
     * -------------------------
     * PARSE-AND-PROCESS methods
     * -------------------------
     *
     * These methods perform the whole cycle of a template's processing: resolving, parsing and processing it, be
     * it a standalone template or a nested one like e.g. an unescaped text, a conditional comment, etc.
     */


    public void parseAndProcessStandalone(
            final String template, final String[] templateSelectors,
            final TemplateMode templateMode,
            final IContext context,
            final Writer writer,
            final boolean useCache) {

        Validate.notNull(template, "Template cannot be null");
        // templateSelectors CAN actually be null if we are going to process the entire template
        // templateMode CAN be null if we are going to use the mode specified by the template resolver
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");

        parseAndProcess(
                null, template, templateSelectors,
                0, 0,
                templateMode,
                context,
                writer,
                useCache);

    }


    public void parseAndProcessNested(
            final String ownerTemplate, final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final Writer writer,
            final boolean useCache) {

        Validate.notNull(ownerTemplate, "Owner Template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when processing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");

        parseAndProcess(
                ownerTemplate, template, null,
                lineOffset, colOffset,
                templateMode,
                context,
                writer,
                useCache);

    }


    private void parseAndProcess(
            final String ownerTemplate, final String template, final String[] templateSelectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final Writer writer,
            final boolean useCache) {


        final TemplateCacheKey cacheKey =
                useCache? new TemplateCacheKey(ownerTemplate, template, templateSelectors, lineOffset, colOffset, templateMode) : null;


        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.templateCache != null) {

            final TemplateModel cached =  this.templateCache.get(cacheKey);

            if (cached != null) {

                // Prepare the context instance that corresponds to this execution of the template engine
                final IEngineContext engineContext =
                        EngineContextManager.prepareEngineContext(this.configuration, cached.getTemplateResolution(), context);

                // Create the handler chain to process the data
                final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext, writer);

                // Process the cached template itself
                processTemplateModel(cached, processingHandlerChain);

                // Dispose the engine context now that processing has been done
                EngineContextManager.disposeEngineContext(engineContext);

                return;

            }

        }


        /*
         * If this template is a textual template (i.e. not a template name, but the template itself expressed
         * as a String object), we need to force the use of a StringTemplateResolver for resolution
         */
        final Set<ITemplateResolver> templateResolvers =
                (ownerTemplate != null? STRING_TEMPLATE_RESOLVER_SET : this.configuration.getTemplateResolvers());


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final Resolution resolution = resolve(this.configuration, templateResolvers, template, templateMode, useCache);


        /*
         * Prepare the context instance that corresponds to this execution of the template engine
         */
        final IEngineContext engineContext =
                EngineContextManager.prepareEngineContext(this.configuration, resolution.templateResolution, context);


        /*
         * Create the handler chain to process the data
         */
        final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(engineContext, writer);


        /*
         * If the resolved template is cacheable, so we will first read it as an object, cache it, and then process it
         */
        if (useCache && resolution.templateResolution.getValidity().isCacheable() && this.templateCache != null) {

            // Create the handler chain to create the Template object
            final TemplateModel parsedTemplate = new TemplateModel(this.configuration, resolution.templateResolution);
            final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(parsedTemplate.getInternalModel());
            // Process the cached template itself
            processResolvedResource(
                    ownerTemplate, template, resolution.templateResource, templateSelectors,
                    lineOffset, colOffset,
                    engineContext.getTemplateMode(),
                    builderHandler);
            // Put the new template into cache
            this.templateCache.put(cacheKey, parsedTemplate);
            // Process the read (+cached) template itself
            processTemplateModel(parsedTemplate, processingHandlerChain);

        } else {

            //  Process the template, which is not cacheable (so no worry about caching)
            processResolvedResource(
                    ownerTemplate, template, resolution.templateResource, templateSelectors,
                    lineOffset, colOffset,
                    engineContext.getTemplateMode(),
                    processingHandlerChain);

        }


        /*
         * Dispose the engine context now that processing has been done
         */
        EngineContextManager.disposeEngineContext(engineContext);


    }












    private static Resolution resolve(
            final IEngineConfiguration configuration,
            final Set<ITemplateResolver> templateResolvers,
            final String template,
            final TemplateMode templateMode,
            final boolean useCache) {

        // Note that the MARKUP SELECTORS that might be used for a executing or inserting a template
        // are not specified to the template resolver. The reason is markup selectors are applied by the parser,
        // not the template resolvers, and allowing the resolver to take any decisions based on markup selectors
        // (like e.g. omitting some output from the resource) could harm the correctness of the selection operation
        // performed by the parser.

        TemplateResolution templateResolution = null;
        ITemplateResource templateResource = null;

        for (final ITemplateResolver templateResolver : templateResolvers) {

            templateResolution = templateResolver.resolveTemplate(configuration, template);
            templateResource = (templateResolution != null? templateResolution.getTemplateResource() : null);

            if (templateResolution != null && templateResource != null) {

                final TemplateMode expectedTemplateMode =
                        (templateMode == null ? templateResolution.getTemplateMode() : templateMode);
                final ICacheEntryValidity expectedCacheEntryValidity =
                        (useCache? templateResolution.getValidity() : NonCacheableCacheEntryValidity.INSTANCE);

                if (templateMode != null || !useCache) {
                    // We need to force the template mode or the cache entry validity
                    templateResolution =
                            new TemplateResolution(
                                    templateResolution.getTemplate(), templateResolution.getTemplateResource(),
                                    expectedTemplateMode, expectedCacheEntryValidity);
                }

                break;

            } else {

                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), LoggingUtils.loggifyTemplateName(template)});
                }

            }

        }

        if (templateResolution == null || templateResource == null) {
            throw new TemplateInputException(
                    "Error resolving template \"" + LoggingUtils.loggifyTemplateName(template) + "\", " +
                    "template might not exist or might not be accessible by " +
                    "any of the configured Template Resolvers");
        }

        if (!template.equals(templateResolution.getTemplate())) {
            throw new TemplateInputException(
                    "One of the Template Resolvers has tried to change the template name, which is forbidden as it " +
                    "could provoke issues such as cache inconsistencies (template was asked as \"" + template + "\" " +
                    "but resolved as \"" + templateResolution.getTemplate() + "\")");
        }

        return new Resolution(templateResolution, templateResource);

    }




    private void processResolvedResource(
            final String ownerTemplate, final String template, final ITemplateResource resource, final String[] templateSelectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof ModelBuilderTemplateHandler) {
                if (templateSelectors != null) {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getDescription()), Arrays.toString(templateSelectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getDescription()));
                }
            } else {
                logger.trace("[THYMELEAF][{}] Starting processing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getDescription()));
            }
        }

        /*
         * Handler chain is in place - now we must use it for calling the parser and initiate the processing
         */
        if (templateMode == TemplateMode.HTML) {
            if (ownerTemplate == null) {
                this.htmlParser.parseStandalone(this.configuration, template, resource, templateSelectors, templateMode, templateHandler);
            } else {
                this.htmlParser.parseNested(this.configuration, ownerTemplate, template, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.XML) {
            if (ownerTemplate == null) {
                this.xmlParser.parseStandalone(this.configuration, template, resource, templateSelectors, templateMode, templateHandler);
            } else {
                this.xmlParser.parseNested(this.configuration, ownerTemplate, template, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.TEXT) {
            if (ownerTemplate == null) {
                this.textParser.parseStandalone(this.configuration, template, resource, templateSelectors, templateMode, templateHandler);
            } else {
                this.textParser.parseNested(this.configuration, ownerTemplate, template, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.JAVASCRIPT) {
            if (ownerTemplate == null) {
                this.javascriptParser.parseStandalone(this.configuration, template, resource, templateSelectors, templateMode, templateHandler);
            } else {
                this.javascriptParser.parseNested(this.configuration, ownerTemplate, template, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.CSS) {
            if (ownerTemplate == null) {
                this.cssParser.parseStandalone(this.configuration, template, resource, templateSelectors, templateMode, templateHandler);
            } else {
                this.cssParser.parseNested(this.configuration, ownerTemplate, template, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else {
            throw new IllegalArgumentException(
                "Cannot process \"" + LoggingUtils.loggifyTemplateName(resource.getDescription()) + "\" " +
                "with unsupported template mode: " + templateMode);
        }

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof ModelBuilderTemplateHandler) {
                if (templateSelectors != null) {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getDescription()), Arrays.toString(templateSelectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getDescription()));
                }
            } else {
                logger.trace("[THYMELEAF][{}] Finished processing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getDescription()));
            }
        }

    }




    private static void processTemplateModel(final TemplateModel template, final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting processing of template \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template.getTemplateResolution().getTemplate()));
        }

        template.getInternalModel().process(templateHandler);

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished processing of template \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template.getTemplateResolution().getTemplate()));
        }

    }





    private static ITemplateHandler createTemplateProcessingHandlerChain(
            final IEngineContext context,
            final Writer writer) {

        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * Declare the pair of pointers that will allow us to build the chain of template handlers
         */
        ITemplateHandler firstHandler = null;
        ITemplateHandler lastHandler = null;

        /*
         * First type of handlers to be added: pre-processors (if any)
         */
        final Set<IPreProcessor> preProcessors = configuration.getPreProcessors(context.getTemplateMode());
        if (preProcessors != null) {
            for (final IPreProcessor preProcessor : preProcessors) {
                final Class<? extends ITemplateHandler> preProcessorClass = preProcessor.getHandlerClass();
                final ITemplateHandler preProcessorHandler;
                try {
                    preProcessorHandler = preProcessorClass.newInstance();
                } catch (final Exception e) {
                    // This should never happen - class was already checked during configuration to contain a zero-arg constructor
                    throw new TemplateProcessingException(
                            "An exception happened during the creation of a new instance of pre-processor " + preProcessorClass.getClass().getName(), e);
                }
                // Initialize the pre-processor
                preProcessorHandler.setContext(context);
                if (firstHandler == null) {
                    firstHandler = preProcessorHandler;
                    lastHandler = preProcessorHandler;
                } else {
                    lastHandler.setNext(preProcessorHandler);
                    lastHandler = preProcessorHandler;
                }
            }
        }


        /*
         * Initialize and add to the chain te Processor Handler itself, the central piece of the chain
         */
        final ProcessorTemplateHandler processorHandler = new ProcessorTemplateHandler();
        processorHandler.setContext(context);
        if (firstHandler == null) {
            firstHandler = processorHandler;
            lastHandler = processorHandler;
        } else {
            lastHandler.setNext(processorHandler);
            lastHandler = processorHandler;
        }


        /*
         * After the Processor Handler, we now must add the post-processors (if any)
         */
        final Set<IPostProcessor> postProcessors = configuration.getPostProcessors(context.getTemplateMode());
        if (postProcessors != null) {
            for (final IPostProcessor postProcessor : postProcessors) {
                final Class<? extends ITemplateHandler> postProcessorClass = postProcessor.getHandlerClass();
                final ITemplateHandler postProcessorHandler;
                try {
                    postProcessorHandler = postProcessorClass.newInstance();
                } catch (final Exception e) {
                    // This should never happen - class was already checked during configuration to contain a zero-arg constructor
                    throw new TemplateProcessingException(
                            "An exception happened during the creation of a new instance of post-processor " + postProcessorClass.getClass().getName(), e);
                }
                // Initialize the pre-processor
                postProcessorHandler.setContext(context);
                if (firstHandler == null) {
                    firstHandler = postProcessorHandler;
                    lastHandler = postProcessorHandler;
                } else {
                    lastHandler.setNext(postProcessorHandler);
                    lastHandler = postProcessorHandler;
                }
            }
        }


        /*
         * Last step: the OUTPUT HANDLER
         */
        final OutputTemplateHandler outputHandler = new OutputTemplateHandler(writer);
        outputHandler.setContext(context);
        if (firstHandler == null) {
            firstHandler = outputHandler;
        } else {
            lastHandler.setNext(outputHandler);
        }

        return firstHandler;

    }





    private static final class Resolution {

        final TemplateResolution templateResolution;
        final ITemplateResource templateResource;

        public Resolution(final TemplateResolution templateResolution, final ITemplateResource templateResource) {
            super();
            this.templateResolution = templateResolution;
            this.templateResource = templateResource;
        }

    }


}

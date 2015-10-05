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
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.TemplateProcessingContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resourceresolver.IResourceResolver;
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
     * @param configuration the configuration being currently used.
     */
    public TemplateManager(final IEngineConfiguration configuration) {
        
        super();
        
        Validate.notNull(configuration, "Configuration object cannot be null");
        
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager == null) {
            this.templateCache = null;
        } else {
            this.templateCache = cacheManager.getTemplateCache();
        }

        final boolean standardDialectPresent = configuration.isStandardDialectPresent();
        final String standardDialectPrefix = configuration.getStandardDialectPrefix();

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
            final IEngineConfiguration configuration,
            final String template, final String[] selectors,
            final TemplateMode templateMode,
            final IContext context,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // selectors CAN be null if we are going to render the entire template
        // templateMode CAN be null if we are going to use the mode specified by the template resolver
        Validate.notNull(context, "Context cannot be null");

        return parse(
                configuration,
                null, template, selectors,
                0, 0,
                templateMode,
                context,
                useCache);

    }


    public TemplateModel parseNested(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when parsing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(context, "Context cannot be null");

        return parse(
                configuration,
                ownerTemplate, template, null,
                lineOffset, colOffset,
                templateMode,
                context,
                useCache);

    }


    private TemplateModel parse(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final String[] selectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final boolean useCache) {


        final TemplateCacheKey cacheKey =
                useCache? new TemplateCacheKey(ownerTemplate, template, selectors, lineOffset, colOffset, templateMode) : null;

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
                (ownerTemplate != null? STRING_TEMPLATE_RESOLVER_SET : configuration.getTemplateResolvers());


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final Resolution resolution =
                resolve(configuration, templateResolvers, template, templateMode, context);


        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateModel object as the result of reading the template
         */
        final TemplateModel parsedTemplate = new TemplateModel(configuration, resolution.templateResolution);
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(parsedTemplate.getInternalModel());


        /*
         * PROCESS THE TEMPLATE
         */
        processResolvedResource(
                configuration,
                ownerTemplate, resolution.resource, selectors,
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
     * Processing is the act of resolving, parsing and executing a template, be it a standalone template or a
     * nested one like e.g. an unescaped text, a conditional comment, etc.
     */


    public void processStandalone(
            final IEngineConfiguration configuration,
            final String template, final String[] selectors,
            final TemplateMode templateMode,
            final IContext context,
            final Writer writer,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // selectors CAN actually be null if we are going to process the entire template
        // templateMode CAN be null if we are going to use the mode specified by the template resolver
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");

        process(
                configuration,
                null, template, selectors,
                0, 0,
                templateMode,
                context,
                writer,
                useCache);

    }


    public void processNested(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final Writer writer,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner Template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when processing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");

        process(
                configuration,
                ownerTemplate, template, null,
                lineOffset, colOffset,
                templateMode,
                context,
                writer,
                useCache);

    }


    private void process(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template, final String[] selectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final Writer writer,
            final boolean useCache) {


        final TemplateCacheKey cacheKey =
                useCache? new TemplateCacheKey(ownerTemplate, template, selectors, lineOffset, colOffset, templateMode) : null;


        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.templateCache != null) {

            final TemplateModel cached =  this.templateCache.get(cacheKey);

            if (cached != null) {

                // Create the Processing Context instance that corresponds to this execution of the template engine
                final ITemplateProcessingContext processingContext =
                            new TemplateProcessingContext(configuration, this, cached.getTemplateResolution(), context);

                // Create the handler chain to process the data
                final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(processingContext, writer);

                // Process the cached template itself
                processParsedModel(cached, processingHandlerChain);

                return;

            }

        }


        /*
         * If this template is a textual template (i.e. not a template name, but the template itself expressed
         * as a String object), we need to force the use of a StringTemplateResolver for resolution
         */
        final Set<ITemplateResolver> templateResolvers =
                (ownerTemplate != null? STRING_TEMPLATE_RESOLVER_SET : configuration.getTemplateResolvers());


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final Resolution resolution =
                resolve(configuration, templateResolvers, template, templateMode, context);


        /*
         * Create the Processing Context instance that corresponds to this execution of the template engine
         */
        final ITemplateProcessingContext processingContext =
                    new TemplateProcessingContext(configuration, this, resolution.templateResolution, context);


        /*
         * Create the handler chain to process the data
         */
        final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(processingContext, writer);


        /*
         * If the resolved template is cacheable, so we will first read it as an object, cache it, and then process it
         */
        if (useCache && resolution.templateResolution.getValidity().isCacheable() && this.templateCache != null) {
            // Create the handler chain to create the Template object
            final TemplateModel parsedTemplate = new TemplateModel(processingContext.getConfiguration(), resolution.templateResolution);
            final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(parsedTemplate.getInternalModel());
            // Process the cached template itself
            processResolvedResource(
                    processingContext.getConfiguration(),
                    ownerTemplate, resolution.resource, selectors,
                    lineOffset, colOffset,
                    processingContext.getTemplateMode(),
                    builderHandler);
            // Put the new template into cache
            this.templateCache.put(cacheKey, parsedTemplate);
            // Process the read (+cached) template itself
            processParsedModel(parsedTemplate, processingHandlerChain);
            return;
        }


        /*
         *  Process the template, which is not cacheable (so no worry about caching)
         */
        processResolvedResource(
                processingContext.getConfiguration(),
                ownerTemplate, resolution.resource, selectors,
                lineOffset, colOffset,
                processingContext.getTemplateMode(),
                processingHandlerChain);

    }












    private static Resolution resolve(
            final IEngineConfiguration configuration,
            final Set<ITemplateResolver> templateResolvers,
            final String template,
            final TemplateMode templateMode,
            final IContext context) {

        TemplateResolution templateResolution = null;
        IResource templateResource = null;

        for (final ITemplateResolver templateResolver : templateResolvers) {

            templateResolution = templateResolver.resolveTemplate(configuration, context, template);

            if (templateResolution != null) {

                if (templateMode != null && templateResolution.getTemplateMode() != templateMode) {
                    // We need to force the template mode
                    templateResolution =
                            new TemplateResolution(
                                    templateResolution.getTemplateName(), templateResolution.getResourceName(),
                                    templateResolution.getResourceResolver(), templateResolution.getCharacterEncoding(),
                                    templateMode, templateResolution.getValidity());
                }

                final String resourceName = templateResolution.getResourceName();
                final IResourceResolver resourceResolver = templateResolution.getResourceResolver();

                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] Trying to resolve template \"{}\" as resource \"{}\" with resource resolver \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), LoggingUtils.loggifyTemplateName(resourceName), resourceResolver.getName()});
                }

                templateResource =
                        resourceResolver.resolveResource(configuration, context, resourceName, templateResolution.getCharacterEncoding());

                if (templateResource == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                "[THYMELEAF][{}] Template \"{}\" could not be resolved as resource \"{}\" with resource resolver \"{}\"",
                                new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), LoggingUtils.loggifyTemplateName(resourceName), resourceResolver.getName()});
                    }
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                "[THYMELEAF][{}] Template \"{}\" was correctly resolved as resource \"{}\" in mode {} with resource resolver \"{}\"",
                                new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), LoggingUtils.loggifyTemplateName(resourceName), templateResolution.getTemplateMode(), resourceResolver.getName()});
                    }
                    break;
                }

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

        return new Resolution(templateResolution, templateResource);

    }




    private void processResolvedResource(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final IResource resource, final String[] selectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof ModelBuilderTemplateHandler) {
                if (selectors != null) {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getName()), Arrays.toString(selectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getName()));
                }
            } else {
                logger.trace("[THYMELEAF][{}] Starting processing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getName()));
            }
        }

        /*
         * Handler chain is in place - now we must use it for calling the parser and initiate the processing
         */
        if (templateMode == TemplateMode.HTML) {
            if (ownerTemplate == null) {
                this.htmlParser.parseStandalone(configuration, resource, selectors, templateMode, templateHandler);
            } else {
                this.htmlParser.parseNested(configuration, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.XML) {
            if (ownerTemplate == null) {
                this.xmlParser.parseStandalone(configuration, resource, selectors, templateMode, templateHandler);
            } else {
                this.xmlParser.parseNested(configuration, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.TEXT) {
            if (ownerTemplate == null) {
                this.textParser.parseStandalone(configuration, resource, selectors, templateMode, templateHandler);
            } else {
                this.textParser.parseNested(configuration, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.JAVASCRIPT) {
            if (ownerTemplate == null) {
                this.javascriptParser.parseStandalone(configuration, resource, selectors, templateMode, templateHandler);
            } else {
                this.javascriptParser.parseNested(configuration, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.CSS) {
            if (ownerTemplate == null) {
                this.cssParser.parseStandalone(configuration, resource, selectors, templateMode, templateHandler);
            } else {
                this.cssParser.parseNested(configuration, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else {
            throw new IllegalArgumentException(
                "Cannot process \"" + LoggingUtils.loggifyTemplateName(resource.getName()) + "\" " +
                "with unsupported template mode: " + templateMode);
        }

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof ModelBuilderTemplateHandler) {
                if (selectors != null) {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getName()), Arrays.toString(selectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getName()));
                }
            } else {
                logger.trace("[THYMELEAF][{}] Finished processing of \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(resource.getName()));
            }
        }

    }




    private static void processParsedModel(final TemplateModel template, final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting processing of template \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template.getTemplateResolution().getTemplateName()));
        }

        template.getInternalModel().process(templateHandler);

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished processing of template \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template.getTemplateResolution().getTemplateName()));
        }

    }





    private static ITemplateHandler createTemplateProcessingHandlerChain(
            final ITemplateProcessingContext processingContext, final Writer writer) {

        final IEngineConfiguration configuration = processingContext.getConfiguration();

        /*
         * Declare the pair of pointers that will allow us to build the chain of template handlers
         */
        ITemplateHandler firstHandler = null;
        ITemplateHandler lastHandler = null;

        /*
         * First type of handlers to be added: pre-processors (if any)
         */
        final Set<IPreProcessor> preProcessors = configuration.getPreProcessors(processingContext.getTemplateMode());
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
                preProcessorHandler.setProcessingContext(processingContext);
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
        processorHandler.setProcessingContext(processingContext);
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
        final Set<IPostProcessor> postProcessors = configuration.getPostProcessors(processingContext.getTemplateMode());
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
                postProcessorHandler.setProcessingContext(processingContext);
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
        outputHandler.setProcessingContext(processingContext);
        if (firstHandler == null) {
            firstHandler = outputHandler;
        } else {
            lastHandler.setNext(outputHandler);
        }

        return firstHandler;

    }





    private static final class Resolution {

        final TemplateResolution templateResolution;
        final IResource resource;

        public Resolution(final TemplateResolution templateResolution, final IResource resource) {
            super();
            this.templateResolution = templateResolution;
            this.resource = resource;
        }

    }


}

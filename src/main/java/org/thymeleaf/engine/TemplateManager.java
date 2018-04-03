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
package org.thymeleaf.engine;

import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.markup.HTMLTemplateParser;
import org.thymeleaf.templateparser.markup.XMLTemplateParser;
import org.thymeleaf.templateparser.raw.RawTemplateParser;
import org.thymeleaf.templateparser.text.CSSTemplateParser;
import org.thymeleaf.templateparser.text.JavaScriptTemplateParser;
import org.thymeleaf.templateparser.text.TextTemplateParser;
import org.thymeleaf.templateresolver.ITemplateResolver;
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

    private final IEngineConfiguration configuration;

    private final ITemplateParser htmlParser;
    private final ITemplateParser xmlParser;
    private final ITemplateParser textParser;
    private final ITemplateParser javascriptParser;
    private final ITemplateParser cssParser;
    private final ITemplateParser rawParser;


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

        // TODO Make these parser implementations configurable: one parser per template mode, then make default implementations extensible/configurable (e.g. AttoParser config)
        this.htmlParser = new HTMLTemplateParser(DEFAULT_PARSER_POOL_SIZE,DEFAULT_PARSER_BLOCK_SIZE);
        this.xmlParser = new XMLTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE);
        this.textParser = new TextTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, standardDialectPresent);
        this.javascriptParser = new JavaScriptTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, standardDialectPresent);
        this.cssParser = new CSSTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, standardDialectPresent);
        this.rawParser = new RawTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE);

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
            final ITemplateContext context, final String template, final Set<String> templateSelectors,
            final TemplateMode templateMode, final boolean useCache, final boolean failIfNotExists) {

        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // templateSelectors CAN be null if we are going to render the entire template
        // templateMode CAN be null if we are going to use the mode specified by the template resolver
        // templateResolutionAttributes CAN be null


        final String ownerTemplate = context.getTemplateData().getTemplate();
        final Map<String,Object> templateResolutionAttributes = context.getTemplateResolutionAttributes();

        final Set<String> cleanTemplateSelectors;
        if (templateSelectors != null && !templateSelectors.isEmpty()) {
            Validate.containsNoEmpties(
                    templateSelectors, "If specified, the Template Selector set cannot contain any nulls or empties");
            if (templateSelectors.size() == 1) {
                cleanTemplateSelectors = Collections.singleton(templateSelectors.iterator().next());
            } else {
                // We will be using a TreeSet because we want the selectors to be ORDERED, so that comparison at the
                // equals(...) method works alright
                cleanTemplateSelectors = Collections.unmodifiableSet(new TreeSet<String>(templateSelectors));
            }
        } else {
            cleanTemplateSelectors = null;
        }


        final TemplateCacheKey cacheKey =
                useCache?
                        new TemplateCacheKey(
                                ownerTemplate,
                                template, cleanTemplateSelectors,
                                0, 0,
                                templateMode,
                                templateResolutionAttributes)
                        : null;

        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.templateCache != null) {
            final TemplateModel cached =  this.templateCache.get(cacheKey);
            if (cached != null) {
                /*
                 * Just at the end, and importantly AFTER CACHING, check if we need to apply any pre-processors
                 * to this model before returning and letting the engine insert the model in any way it needs.
                 */
                return applyPreProcessorsIfNeeded(context, cached);
            }
        }


        /*
         * Resolve the template
         */
        final TemplateResolution templateResolution =
                resolveTemplate(this.configuration, ownerTemplate, template, templateResolutionAttributes, failIfNotExists);


        /*
         * Once the template has been resolved (or tried to), and depending on the value of our 'failIfNotExists'
         * flag, we will check two conditions in which we will be returning null:
         *
         *    1. No template resolver has been able to resolve the template (this can happen if resolvers are
         *       configured with the 'checkExistence' flag to true).
         *    2. If the template was resolved, its existence should be checked in order to avoid exceptions during
         *       the reading phase.
         *
         * NOTE we will not cache this "null" result because the fact that a template is cacheable or not is
         * determined by template resolvers. And in this case there is no template resolver being applied
         * (actually, we are here because no resolver had success).
         */
        if (!failIfNotExists) {

            if (templateResolution == null) {
                // No resolver could resolve this
                return null;
            }

            if (!templateResolution.isTemplateResourceExistenceVerified()) {
                final ITemplateResource resource = templateResolution.getTemplateResource();
                if (resource == null || !resource.exists()) {
                    // Calling resource.exists() each time is not great, but think this only happens if the resource
                    // has not been cached (e.g. when it does not exist)
                    return null;
                }
            }

        }


        /*
         * Build the TemplateData object
         */
        final TemplateData templateData =
                buildTemplateData(templateResolution, template, cleanTemplateSelectors, templateMode, useCache);


        /*
         *  Create the Template Handler that will be in charge of building the TemplateModel
         */
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);


        /*
         * PROCESS THE TEMPLATE
         */
        final ITemplateParser parser = getParserForTemplateMode(templateData.getTemplateMode());
        parser.parseStandalone(
                this.configuration,
                ownerTemplate, template, cleanTemplateSelectors, templateData.getTemplateResource(),
                templateData.getTemplateMode(), templateResolution.getUseDecoupledLogic(), builderHandler);

        final TemplateModel templateModel = builderHandler.getModel();


        /*
         * Cache the template if it is cacheable
         */
        if (useCache && this.templateCache != null) {
            if (templateResolution.getValidity().isCacheable()) {
                this.templateCache.put(cacheKey, templateModel);
            }
        }


        /*
         * Last step: just at the end, and importantly AFTER CACHING, check if we need to apply any pre-processors
         * to this model before returning and letting the engine insert the model in any way it needs.
         */
        return applyPreProcessorsIfNeeded(context, templateModel);

    }




    /*
     * This method manually applies preprocessors to template models that have just been parsed or obtained from
     * cache. This is needed for fragments, just before these fragments (coming from templates, not simply parsed
     * text) are returned to whoever needs them (usually the fragment insertion mechanism).
     *
     * NOTE that PRE-PROCESSOR INSTANCES ARE NOT SHARED among the different fragments being inserted
     *      in a template (or between fragments and the main template). The reason for this is that pre-processors are
     *      implementations of ITemplateHandler and therefore instances are inserted into processing chains that cannot
     *      be broken (if a pre-processor is used for the main template its "next" step in the chain cannot be
     *      'momentarily' changed in order to be a fragment-building handler instead of the ProcessorTemplateHandler)
     *
     *      The only way therefore among pre-processor instances to actually share information is by setting it into
     *      the context.
     */
    private TemplateModel applyPreProcessorsIfNeeded(final ITemplateContext context, final TemplateModel templateModel) {

        final TemplateData templateData = templateModel.getTemplateData();

        if (this.configuration.getPreProcessors(templateData.getTemplateMode()).isEmpty()) {
            return templateModel;
        }

        final IEngineContext engineContext =
                EngineContextManager.prepareEngineContext(this.configuration, templateData, context.getTemplateResolutionAttributes(), context);

        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);
        final ITemplateHandler processingHandlerChain =
                createTemplateProcessingHandlerChain(engineContext, true, false, builderHandler, null);

        templateModel.process(processingHandlerChain);

        EngineContextManager.disposeEngineContext(engineContext);

        return builderHandler.getModel();

    }




    public TemplateModel parseString(
            final TemplateData ownerTemplateData, final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final boolean useCache) {

        Validate.notNull(ownerTemplateData, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when parsing a nested template
        // templateMode CAN be null (if we are using the owner's)

        final String ownerTemplate = ownerTemplateData.getTemplate();

        final TemplateMode definitiveTemplateMode =
                (templateMode != null? templateMode : ownerTemplateData.getTemplateMode());


        final TemplateCacheKey cacheKey =
                useCache?
                        new TemplateCacheKey(
                                ownerTemplate,
                                template, null,
                                lineOffset, colOffset,
                                definitiveTemplateMode,
                                null) // template resolution attributes do not affect string fragments: no resolution!
                        : null;

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
         * Compute the cache validity. In order for a String fragment to be cacheable, we will have to have
         * specified the 'useCache' parameter as true, and the owner template must be cacheable
         */
        final ICacheEntryValidity cacheValidity =
                (useCache && ownerTemplateData.getValidity().isCacheable()?
                        AlwaysValidCacheEntryValidity.INSTANCE : NonCacheableCacheEntryValidity.INSTANCE);


        /*
         * Build the TemplateData
         *
         * NOTE how, by default, we are using the owner's TemplateData. And even if the template mode changes
         * and we need to create a new TemplateData object, we will keep the original name and resource.
         * This is because we want the elements inside the fragment to me reported as belonging to the
         * container template, not to the fragment String considered as a fragment in its own (which
         * wouldn't make sense)
         */
        final TemplateData templateData =
                (templateMode == null?
                        // No change in Template Mode -> simply use the owner's template data
                        ownerTemplateData :
                        // Template Mode changed -> new TemplateData, very similar but different template mode
                        new TemplateData(
                                ownerTemplateData.getTemplate(), ownerTemplateData.getTemplateSelectors(),
                                ownerTemplateData.getTemplateResource(), templateMode, cacheValidity));


        /*
         * Create the Template Handler that will be in charge of building the TemplateModel
         *
         * NOTE how we are using the owner's TemplateData and not a new one created for this fragment, because
         * we want the elements inside the fragment to me reported as belonging to the container template,
         * not to the fragment String considered as a fragment in its own (which wouldn't make sense)
         */
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);


        /*
         * PROCESS THE TEMPLATE
         */
        final ITemplateParser parser = getParserForTemplateMode(templateData.getTemplateMode());
        // NO RESOURCE is sent to the parser, in this case. We simply pass the String template
        parser.parseString(this.configuration, ownerTemplate, template, lineOffset, colOffset, definitiveTemplateMode, builderHandler);

        final TemplateModel parsedTemplate = builderHandler.getModel();


        /*
         * Cache the template if it is cacheable
         */
        if (useCache && this.templateCache != null) {
            if (cacheValidity.isCacheable()) {
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
            final ITemplateContext context,
            final Writer writer) {

        Validate.isTrue(
                this.configuration == template.getConfiguration(),
                "Specified template was built by a different Template Engine instance");

        /*
         * Create the context instance that corresponds to this execution of the template engine
         */
        final IEngineContext engineContext =
                EngineContextManager.prepareEngineContext(this.configuration, template.getTemplateData(), context.getTemplateResolutionAttributes(), context);

        /*
         * Create the handler chain to process the data.
         *
         * In this case we are only processing an already existing model, which was created after some computation
         * at template-processing time. So this does not come directly from a template, and therefore pre-processors
         * should not be applied.
         *
         * As for post-processors, we know the result of this will not be directly written to output in most cases but
         * instead used to create a String that is afterwards inserted into the model as a Text node. In the only cases
         * in which this is not true is when this is used inside any kind of Lazy-processing CharSequence writer like
         * LazyProcessingCharSequence, and in such case we know those CharSequences are only used when there are
         * NO post-processors, so we are safe anyway.
         */
        final ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
        final ITemplateHandler processingHandlerChain =
                createTemplateProcessingHandlerChain(engineContext, false, false, processorTemplateHandler, writer);

        /*
         *  Process the template
         */
        template.process(processingHandlerChain);


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
     * These methods perform the whole cycle of a template's processing: resolving, parsing and processing.
     * This is only meant to be called from the TemplateEngine
     */


    public void parseAndProcess(
            final TemplateSpec templateSpec,
            final IContext context,
            final Writer writer) {

        Validate.notNull(templateSpec, "Template Specification cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(writer, "Writer cannot be null");


        // TemplateSpec will already have validated its contents, so need to do it here (template selectors,
        // resolution attributes, etc.)

        final String template = templateSpec.getTemplate();
        final Set<String> templateSelectors = templateSpec.getTemplateSelectors();
        final TemplateMode templateMode = templateSpec.getTemplateMode();
        final Map<String, Object> templateResolutionAttributes = templateSpec.getTemplateResolutionAttributes();

        final TemplateCacheKey cacheKey =
                    new TemplateCacheKey(
                            null, // ownerTemplate
                            template, templateSelectors,
                            0, 0, // lineOffset, colOffset
                            templateMode,
                            templateResolutionAttributes);


        /*
         * First look at the cache - it might be already cached
         */
        if (this.templateCache != null) {

            final TemplateModel cached =  this.templateCache.get(cacheKey);

            if (cached != null) {

                final IEngineContext engineContext =
                        EngineContextManager.prepareEngineContext(this.configuration, cached.getTemplateData(), templateResolutionAttributes, context);

                /*
                 * Create the handler chain to process the data.
                 * This is PARSE + PROCESS, so its called from the TemplateEngine, and the only case in which we should apply
                 * both pre-processors and post-processors (besides creating a last output-to-writer step)
                 */
                final ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
                final ITemplateHandler processingHandlerChain =
                        createTemplateProcessingHandlerChain(engineContext, true, true, processorTemplateHandler, writer);

                cached.process(processingHandlerChain);

                EngineContextManager.disposeEngineContext(engineContext);

                return;

            }

        }


        /*
         * Resolve the template
         */
        final TemplateResolution templateResolution =
                resolveTemplate(this.configuration, null, template, templateResolutionAttributes, true);


        /*
         * Build the TemplateData object
         */
        final TemplateData templateData =
                buildTemplateData(templateResolution, template, templateSelectors, templateMode, true);


        /*
         * Prepare the context instance that corresponds to this execution of the template engine
         */
        final IEngineContext engineContext =
                EngineContextManager.prepareEngineContext(this.configuration, templateData, templateResolutionAttributes, context);


        /*
         * Create the handler chain to process the data.
         * This is PARSE + PROCESS, so its called from the TemplateEngine, and the only case in which we should apply
         * both pre-processors and post-processors (besides creating a last output-to-writer step)
         */
        final ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
        final ITemplateHandler processingHandlerChain =
                createTemplateProcessingHandlerChain(engineContext, true, true, processorTemplateHandler, writer);


        /*
         * Obtain the parser
         */
        final ITemplateParser parser = getParserForTemplateMode(engineContext.getTemplateMode());


        /*
         * If the resolved template is cacheable, so we will first read it as an object, cache it, and then process it
         */
        if (templateResolution.getValidity().isCacheable() && this.templateCache != null) {

            // Create the handler chain to create the Template object
            final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);

            // Process the template into a TemplateModel
            parser.parseStandalone(
                    this.configuration,
                    null, template, templateSelectors, templateData.getTemplateResource(),
                    engineContext.getTemplateMode(), templateResolution.getUseDecoupledLogic(), builderHandler);

            // Obtain the TemplateModel
            final TemplateModel templateModel = builderHandler.getModel();

            // Put the new template into cache
            this.templateCache.put(cacheKey, templateModel);

            // Process the read (+cached) template itself
            templateModel.process(processingHandlerChain);

        } else {

            //  Process the template, which is not cacheable (so no worry about caching)
            parser.parseStandalone(
                    this.configuration,
                    null, template, templateSelectors, templateData.getTemplateResource(),
                    engineContext.getTemplateMode(), templateResolution.getUseDecoupledLogic(),  processingHandlerChain);

        }


        /*
         * Dispose the engine context now that processing has been done
         */
        EngineContextManager.disposeEngineContext(engineContext);


    }


    public ThrottledTemplateProcessor parseAndProcessThrottled(final TemplateSpec templateSpec, final IContext context) {

        Validate.notNull(templateSpec, "Template Specification cannot be null");
        Validate.notNull(context, "Context cannot be null");


        // TemplateSpec will already have validated its contents, so need to do it here (template selectors,
        // resolution attributes, etc.)

        final String template = templateSpec.getTemplate();
        final Set<String> templateSelectors = templateSpec.getTemplateSelectors();
        final TemplateMode templateMode = templateSpec.getTemplateMode();
        final Map<String, Object> templateResolutionAttributes = templateSpec.getTemplateResolutionAttributes();

        final TemplateCacheKey cacheKey =
                new TemplateCacheKey(
                        null, // ownerTemplate
                        template, templateSelectors,
                        0, 0, // lineOffset, colOffset
                        templateMode,
                        templateResolutionAttributes);


        /*
         * Instantiate the throttling artifacts, including the throttled writer, which might be only for
         */
        final TemplateFlowController flowController = new TemplateFlowController();

        final ThrottledTemplateWriter throttledTemplateWriter;
        if (templateSpec.isOutputSSE()) {
            throttledTemplateWriter = new SSEThrottledTemplateWriter(template, flowController);
        } else {
            throttledTemplateWriter = new ThrottledTemplateWriter(template, flowController);
        }


        /*
         * First look at the cache - it might be already cached
         */
        if (this.templateCache != null) {

            final TemplateModel cached =  this.templateCache.get(cacheKey);

            if (cached != null) {

                final IEngineContext engineContext =
                        EngineContextManager.prepareEngineContext(this.configuration, cached.getTemplateData(), templateResolutionAttributes, context);

                /*
                 * Create the handler chain to process the data.
                 * This is PARSE + PROCESS, so its called from the TemplateEngine, and the only case in which we should apply
                 * both pre-processors and post-processors (besides creating a last output-to-writer step)
                 */
                final ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
                processorTemplateHandler.setFlowController(flowController);
                final ITemplateHandler processingHandlerChain =
                        createTemplateProcessingHandlerChain(engineContext, true, true, processorTemplateHandler, throttledTemplateWriter);

                /*
                 * Return the throttled template processor
                 */
                return new ThrottledTemplateProcessor(
                        templateSpec, engineContext, cached, processingHandlerChain,
                        processorTemplateHandler, flowController, throttledTemplateWriter);

            }

        }


        /*
         * Resolve the template
         */
        final TemplateResolution templateResolution =
                resolveTemplate(this.configuration, null, template, templateResolutionAttributes, true);


        /*
         * Build the TemplateData object
         */
        final TemplateData templateData =
                buildTemplateData(templateResolution, template, templateSelectors, templateMode, true);


        /*
         * Prepare the context instance that corresponds to this execution of the template engine
         */
        final IEngineContext engineContext =
                EngineContextManager.prepareEngineContext(this.configuration, templateData, templateResolutionAttributes, context);


        /*
         * Create the handler chain to process the data.
         * This is PARSE + PROCESS, so its called from the TemplateEngine, and the only case in which we should apply
         * both pre-processors and post-processors (besides creating a last output-to-writer step)
         */
        final ProcessorTemplateHandler processorTemplateHandler = new ProcessorTemplateHandler();
        processorTemplateHandler.setFlowController(flowController);
        final ITemplateHandler processingHandlerChain =
                createTemplateProcessingHandlerChain(engineContext, true, true, processorTemplateHandler, throttledTemplateWriter);


        /*
         * Obtain the parser
         */
        final ITemplateParser parser = getParserForTemplateMode(engineContext.getTemplateMode());


        /*
         * Parse the template into a TemplateModel. Even if we are not using the cache, throttled template processings
         * will always be processed first into a TemplateModel, so that throttling can then be applied on an
         * already-in-memory sequence of events
         */
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(this.configuration, templateData);
        parser.parseStandalone(
                this.configuration,
                null, template, templateSelectors, templateData.getTemplateResource(),
                engineContext.getTemplateMode(), templateResolution.getUseDecoupledLogic(), builderHandler);
        final TemplateModel templateModel = builderHandler.getModel();


        /*
         * If cache is active, put the cached TemplateModel into cache
         */
        if (templateResolution.getValidity().isCacheable() && this.templateCache != null) {

            // Put the new template into cache
            this.templateCache.put(cacheKey, templateModel);

        }


        /*
         * Return the throttled template processor
         */
        return new ThrottledTemplateProcessor(
                templateSpec, engineContext, templateModel, processingHandlerChain,
                processorTemplateHandler, flowController, throttledTemplateWriter);

    }






    private static TemplateResolution resolveTemplate(
            final IEngineConfiguration configuration,
            final String ownerTemplate,
            final String template,
            final Map<String, Object> templateResolutionAttributes,
            final boolean failIfNotExists) {

        // Note that the MARKUP SELECTORS that might be used for a executing or inserting a template
        // are not specified to the template resolver. The reason is markup selectors are applied by the parser,
        // not the template resolvers, and allowing the resolver to take any decisions based on markup selectors
        // (like e.g. omitting some output from the resource) could harm the correctness of the selection operation
        // performed by the parser.

        for (final ITemplateResolver templateResolver : configuration.getTemplateResolvers()) {

            final TemplateResolution templateResolution =
                    templateResolver.resolveTemplate(configuration, ownerTemplate, template, templateResolutionAttributes);
            if (templateResolution != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] Template resolver match! Resolver \"{}\" will resolve template \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), LoggingUtils.loggifyTemplateName(template)});
                }
                return templateResolution;
            }

            if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), LoggingUtils.loggifyTemplateName(template)});
            }

        }

        if (!failIfNotExists) {
            // In this case we will not consider that a "not exists" means a failure. Maybe we are in a scenario
            // (e.g. some types of operations with FragmentExpressions) in which we desire this.
            return null;
        }

        throw new TemplateInputException(
                "Error resolving template [" + template + "], " +
                "template might not exist or might not be accessible by " +
                "any of the configured Template Resolvers");

    }




    private static TemplateData buildTemplateData(
            final TemplateResolution templateResolution,
            final String template,
            final Set<String> templateSelectors,
            final TemplateMode templateMode,
            final boolean useCache) {

        final TemplateMode definitiveTemplateMode =
                (templateMode == null ? templateResolution.getTemplateMode() : templateMode);

        final ICacheEntryValidity definitiveCacheEntryValidity =
                (useCache? templateResolution.getValidity() : NonCacheableCacheEntryValidity.INSTANCE);

        return new TemplateData(
                template, templateSelectors, templateResolution.getTemplateResource(), definitiveTemplateMode, definitiveCacheEntryValidity);


    }




    private ITemplateParser getParserForTemplateMode(final TemplateMode templateMode) {
        switch (templateMode) {
            case HTML:       return this.htmlParser;
            case XML:        return this.xmlParser;
            case TEXT:       return this.textParser;
            case JAVASCRIPT: return this.javascriptParser;
            case CSS:        return this.cssParser;
            case RAW:        return this.rawParser;
            default:
                throw new IllegalArgumentException("No parser exists for template mode: " + templateMode);
        }
    }





    private static ITemplateHandler createTemplateProcessingHandlerChain(
            final IEngineContext context,
            final boolean setPreProcessors, final boolean setPostProcessors,
            final ITemplateHandler handler, final Writer writer) {

        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * Declare the pair of pointers that will allow us to build the chain of template handlers
         */
        ITemplateHandler firstHandler = null;
        ITemplateHandler lastHandler = null;

        /*
         * First type of handlers to be added: pre-processors (if any)
         */
        if (setPreProcessors) {
            final Set<IPreProcessor> preProcessors = configuration.getPreProcessors(context.getTemplateMode());
            if (preProcessors != null && preProcessors.size() > 0) {
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
        }


        /*
         * Initialize and add to the chain te Processor Handler itself, the central piece of the chain
         */
        handler.setContext(context);
        if (firstHandler == null) {
            firstHandler = handler;
            lastHandler = handler;
        } else {
            lastHandler.setNext(handler);
            lastHandler = handler;
        }


        /*
         * After the Processor Handler, we now must add the post-processors (if any)
         */
        if (setPostProcessors) {
            final Set<IPostProcessor> postProcessors = configuration.getPostProcessors(context.getTemplateMode());
            if (postProcessors != null && postProcessors.size() > 0) {
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
        }


        /*
         * Last step: the OUTPUT HANDLER
         */
        if (writer != null) {
            final OutputTemplateHandler outputHandler = new OutputTemplateHandler(writer);
            outputHandler.setContext(context);
            if (firstHandler == null) {
                firstHandler = outputHandler;
            } else {
                lastHandler.setNext(outputHandler);
            }
        }

        return firstHandler;

    }




}

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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.FragmentCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.cache.NonCacheableCacheEntryValidity;
import org.thymeleaf.cache.TemplateCacheKey;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.TemplateProcessingContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resource.StringResource;
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
import org.thymeleaf.util.StringUtils;
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


    private final ICache<TemplateCacheKey,ParsedTemplateMarkup> templateCache; // might be null! (= no cache)
    private final ICache<FragmentCacheKey,ParsedFragmentMarkup> fragmentCache; // might be null! (= no cache)





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
            this.fragmentCache = null;
        } else {
            this.templateCache = cacheManager.getTemplateCache();
            this.fragmentCache = cacheManager.getFragmentCache();
        }

        this.htmlParser = new HTMLTemplateParser(DEFAULT_PARSER_POOL_SIZE,DEFAULT_PARSER_BLOCK_SIZE);
        this.xmlParser = new XMLTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE);
        this.textParser = new TextTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, configuration.getStandardDialectPrefix());
        this.javascriptParser = new JavaScriptTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, configuration.getStandardDialectPrefix());
        this.cssParser = new CSSTemplateParser(DEFAULT_PARSER_POOL_SIZE, DEFAULT_PARSER_BLOCK_SIZE, configuration.getStandardDialectPrefix());

    }
    

    
    
    
    /**
     * <p>
     *   Clears all the template-related caches (both template cache and fragment cache).
     * </p>
     */
    public void clearCaches() {
        if (this.templateCache != null) {
            this.templateCache.clear();
        }
        if (this.fragmentCache != null) {
            this.fragmentCache.clear();
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
                if (templateCacheKey.getTemplate().equals(template)) {
                    keysToBeRemoved.add(templateCacheKey);
                }
            }
            for (final TemplateCacheKey keyToBeRemoved : keysToBeRemoved) {
                this.templateCache.clearKey(keyToBeRemoved);
            }
        }
        if (this.fragmentCache != null) {
            // We will be removing all entries at the fragment cache related to this template
            final Set<FragmentCacheKey> keysToBeRemoved = new HashSet<FragmentCacheKey>(4);
            final Set<FragmentCacheKey> fragmentCacheKeys = this.fragmentCache.keySet();
            // We are iterating twice and creating a temporary set just in case the 'keySet' Set is still connected
            // to the original cache store and we provoke ConcurrentModificationExceptions when removing entries
            for (final FragmentCacheKey fragmentCacheKey : fragmentCacheKeys) {
                if (fragmentCacheKey.getTemplate().equals(template)) {
                    keysToBeRemoved.add(fragmentCacheKey);
                }
            }
            for (final FragmentCacheKey keyToBeRemoved : keysToBeRemoved) {
                this.fragmentCache.clearKey(keyToBeRemoved);
            }
        }
    }
    
    






    public ParsedTemplateMarkup parseTemplate(
            final IEngineConfiguration configuration, final IContext context, final String template) {
        return parseTemplate(configuration, context, template, null);
    }


    public ParsedTemplateMarkup parseTemplate(
            final IEngineConfiguration configuration, final IContext context, final String template, final String[] markupSelectors) {
        
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // Markup Selectors CAN be null

        // TODO Maybe all cache keys should include the TemplateMode? (at least if we allow mixing modes in includes...) NOTE Its better if they don't include
        // template mode because this allows us to use 'template' directly as key most of the times
        final String cacheKey = computeCacheKey(configuration.getTextRepository(), template, markupSelectors);


        /*
         * First look at the cache - it might be already cached
         */
        if (this.templateCache != null) {
            final ParsedTemplateMarkup cached =  this.templateCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final TemplateAndResourceResolution resolution = resolveTemplate(configuration, template, context);


        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateMarkup object as the result of reading the template
         */
        final ParsedTemplateMarkup parsedTemplate = new ParsedTemplateMarkup(configuration, resolution.templateResolution);
        final MarkupBuilderTemplateHandler builderHandler = new MarkupBuilderTemplateHandler(true, parsedTemplate.getInternalMarkup());


        /*
         * PROCESS THE TEMPLATE
         */
        processResolvedResource(
                configuration, resolution.resource, markupSelectors, false, resolution.templateResolution.getTemplateMode(), builderHandler);


        /*
         * Cache the template if it is cacheable
         */
        if (this.templateCache != null) {
            if (resolution.templateResolution.getValidity().isCacheable()) {
                this.templateCache.put(cacheKey, parsedTemplate);
            }
        }
        
        return parsedTemplate;
        
    }







    public ParsedFragmentMarkup parseFragment(
            final IEngineConfiguration configuration,
            final String template, final String[] markupSelectors,
            final boolean textualTemplate, final TemplateMode forcedTemplateMode,
            final IContext context,
            final boolean useCache) {
        return parseFragment(configuration, template, markupSelectors, null, textualTemplate, forcedTemplateMode, context, useCache);
    }


    public ParsedFragmentMarkup parseFragment(
            final IEngineConfiguration configuration,
            final String template, final String fragment,
            final boolean textualTemplate, final TemplateMode forcedTemplateMode,
            final IContext context,
            final boolean useCache) {
        return parseFragment(configuration, template, null, fragment, textualTemplate, forcedTemplateMode, context, useCache);
    }


    private ParsedFragmentMarkup parseFragment(
            final IEngineConfiguration configuration,
            final String template, final String[] markupSelectors, final String fragment,
            final boolean textualTemplate, final TemplateMode forcedTemplateMode,
            final IContext context,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.isTrue(fragment == null || forcedTemplateMode != null,
                "When a textual fragment is specified, (forced) template mode must be specified too");
        // markup selectors CAN actually be null if we are going to render the entire template
        // forcedTemplateMode CAN also be null if we are going to use the mode specified by the template resolver

        // NOTE that thanks to the double method signature offered, markupSelectors and fragment will never
        //      be specified at the same time.

        final FragmentCacheKey cacheKey =
                useCache? new FragmentCacheKey(template, markupSelectors, fragment, textualTemplate, forcedTemplateMode) : null;

        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.fragmentCache != null) {
            final ParsedFragmentMarkup cached =  this.fragmentCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }


        final IResource resource;
        final TemplateMode templateMode;
        final ICacheEntryValidity cacheEntryValidity;

        if (fragment != null) {
            // This is a textual fragment (i.e. just a text from a template -- we don't need to resolve a template and
            // match markup selectors)


            /*
             * Create the Resource (a StringResource, in this case) representing the fragment to parse
             */
            resource = new StringResource(cacheKey.toString(), fragment);

            /*
             * Template Mode has to be always specified (forced) for textual fragments
             */
            templateMode = forcedTemplateMode;

            /*
             * Cache entry validity for textual fragments will be 'always' if cache is used
             */
            cacheEntryValidity = useCache? AlwaysValidCacheEntryValidity.INSTANCE : NonCacheableCacheEntryValidity.INSTANCE;


        } else {
            // This is not a textual fragment -- we need to resolve a template and, maybe, match markup selectors


            /*
             * If this template is a textual template (i.e. not a template name, but the template itself expressed
             * as a String object), we need to force the use of a StringTemplateResolver for resolution
             */
            final Set<ITemplateResolver> templateResolvers =
                    (textualTemplate? STRING_TEMPLATE_RESOLVER_SET : configuration.getTemplateResolvers());

            /*
             * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
             */
            final TemplateAndResourceResolution resolution =
                    resolveTemplate(configuration, templateResolvers, template, forcedTemplateMode, context);

            /*
             * Assign the values
             */
            resource = resolution.resource;
            templateMode = resolution.templateResolution.getTemplateMode();
            cacheEntryValidity = resolution.templateResolution.getValidity();


        }


        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateMarkup object as the result of reading the template
         */
        final ParsedFragmentMarkup parsedFragment = new ParsedFragmentMarkup(configuration, templateMode, cacheEntryValidity);
        final MarkupBuilderTemplateHandler builderHandler = new MarkupBuilderTemplateHandler(true, parsedFragment.getInternalMarkup());

        /*
         * PROCESS THE TEMPLATE
         */
        processResolvedResource(
                configuration, resource, markupSelectors, true, templateMode, builderHandler);


        /*
         * Cache the template if it is cacheable
         */
        if (useCache && this.fragmentCache != null) {
            if (parsedFragment.getValidity().isCacheable()) {
                this.fragmentCache.put(cacheKey, parsedFragment);
            }
        }

        return parsedFragment;

    }






    // TODO Why doesn't this processTemplate method resemble parseFragment in order to not really needing
    //      resolution of textualFragments? manually creating a StringResource should be enough...
    //      Also, if this is done, the way textualFragments in parseFragment are dealtWith... should be changed?
    //      And in fact... is StringTemplateResolver actually needed at all?


    public void processTemplate(final IEngineConfiguration configuration,
                                final String template, final String[] markupSelectors,
                                final boolean textualTemplate, final TemplateMode forcedTemplateMode,
                                final IContext context,
                                final Writer writer,
                                final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // markup selectors CAN actually be null if we are going to render the entire template
        // forcedTemplateMode CAN also be null if we are going to use the mode specified by the template resolver

        final TemplateCacheKey cacheKey =
                useCache? new TemplateCacheKey(template, markupSelectors, textualTemplate, forcedTemplateMode) : null;


        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.templateCache != null) {
            final ParsedTemplateMarkup cached =  this.templateCache.get(cacheKey);
            if (cached != null) {
                // Create the Processing Context instance that corresponds to this execution of the template engine
                final ITemplateProcessingContext processingContext =
                        new TemplateProcessingContext(configuration, this, cached.getTemplateResolution(), context);
                // Create the handler chain to process the data
                final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(processingContext, writer);
                // Process the cached template itself
                processParsedMarkup(cached, processingHandlerChain);
                return;
            }
        }


        /*
         * If this template is a textual template (i.e. not a template name, but the template itself expressed
         * as a String object), we need to force the use of a StringTemplateResolver for resolution
         */
        final Set<ITemplateResolver> templateResolvers =
                (textualTemplate? STRING_TEMPLATE_RESOLVER_SET : configuration.getTemplateResolvers());


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final TemplateAndResourceResolution resolution =
                resolveTemplate(configuration, templateResolvers, template, forcedTemplateMode, context);


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
            final ParsedTemplateMarkup parsedTemplate = new ParsedTemplateMarkup(processingContext.getConfiguration(), resolution.templateResolution);
            final MarkupBuilderTemplateHandler builderHandler = new MarkupBuilderTemplateHandler(false, parsedTemplate.getInternalMarkup());
            // Process the cached template itself
            processResolvedResource(
                    processingContext.getConfiguration(), resolution.resource, markupSelectors, false, processingContext.getTemplateMode(),
                    builderHandler);
            // Put the new template into cache
            this.templateCache.put(cacheKey, parsedTemplate);
            // Process the read (+cached) template itself
            processParsedMarkup(parsedTemplate, processingHandlerChain);
            return;
        }


        /*
         *  Process the template, which is not cacheable (so no worry about caching)
         */
        processResolvedResource(
                processingContext.getConfiguration(), resolution.resource, markupSelectors, false, processingContext.getTemplateMode(),
                processingHandlerChain);

    }












    private static TemplateAndResourceResolution resolveTemplate(
            final IEngineConfiguration configuration,
            final Set<ITemplateResolver> templateResolvers,
            final String template,
            final TemplateMode forcedTemplateMode,
            final IContext context) {

        TemplateResolution templateResolution = null;
        IResource templateResource = null;

        for (final ITemplateResolver templateResolver : templateResolvers) {

            templateResolution = templateResolver.resolveTemplate(configuration, context, template);

            if (templateResolution != null) {

                if (forcedTemplateMode != null && templateResolution.getTemplateMode() != forcedTemplateMode) {
                    // We need to force the template mode
                    templateResolution =
                            new TemplateResolution(
                                    templateResolution.getTemplateName(), templateResolution.getResourceName(),
                                    templateResolution.getResourceResolver(), templateResolution.getCharacterEncoding(),
                                    forcedTemplateMode, templateResolution.getValidity());
                }

                final String resourceName = templateResolution.getResourceName();
                final IResourceResolver resourceResolver = templateResolution.getResourceResolver();

                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] Trying to resolve template \"{}\" as resource \"{}\" with resource resolver \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), loggifyTemplate(template), loggifyTemplate(resourceName), resourceResolver.getName()});
                }

                templateResource =
                        resourceResolver.resolveResource(configuration, context, resourceName, templateResolution.getCharacterEncoding());

                if (templateResource == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                "[THYMELEAF][{}] Template \"{}\" could not be resolved as resource \"{}\" with resource resolver \"{}\"",
                                new Object[] {TemplateEngine.threadIndex(), loggifyTemplate(template), loggifyTemplate(resourceName), resourceResolver.getName()});
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                "[THYMELEAF][{}] Template \"{}\" was correctly resolved as resource \"{}\" in mode {} with resource resolver \"{}\"",
                                new Object[] {TemplateEngine.threadIndex(), loggifyTemplate(template), loggifyTemplate(resourceName), templateResolution.getTemplateMode(), resourceResolver.getName()});
                    }
                    break;
                }

            } else {

                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), loggifyTemplate(template)});
                }

            }

        }

        if (templateResolution == null || templateResource == null) {
            throw new TemplateInputException(
                    "Error resolving template \"" + loggifyTemplate(template) + "\", " +
                    "template might not exist or might not be accessible by " +
                    "any of the configured Template Resolvers");
        }

        return new TemplateAndResourceResolution(templateResolution, templateResource);

    }




    private void processResolvedResource(
            final IEngineConfiguration configuration,
            final IResource templateResource, final String[] markupSelectors,
            final boolean fragment, final TemplateMode templateMode,
            final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof MarkupBuilderTemplateHandler) {
                if (markupSelectors != null) {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), loggifyTemplate(templateResource.getName()), Arrays.toString(markupSelectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\"", TemplateEngine.threadIndex(), loggifyTemplate(templateResource.getName()));
                }
            } else {
                logger.trace("[THYMELEAF][{}] Starting processing of \"{}\"", TemplateEngine.threadIndex(), loggifyTemplate(templateResource.getName()));
            }
        }

        /*
         * Handler chain is in place - now we must use it for calling the parser and initiate the processing
         */
        if (templateMode == TemplateMode.HTML) {
            if (fragment) {
                this.htmlParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                this.htmlParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else if (templateMode == TemplateMode.XML) {
            if (fragment) {
                this.xmlParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                this.xmlParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else if (templateMode == TemplateMode.TEXT) {
            if (fragment) {
                this.textParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                this.textParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else if (templateMode == TemplateMode.JAVASCRIPT) {
            if (fragment) {
                this.javascriptParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                this.javascriptParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else if (templateMode == TemplateMode.CSS) {
            if (fragment) {
                this.cssParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                this.cssParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else {
            throw new IllegalArgumentException(
                "Cannot process \"" + loggifyTemplate(templateResource.getName()) + "\" " +
                "with unsupported template mode: " + templateMode);
        }

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof MarkupBuilderTemplateHandler) {
                if (markupSelectors != null) {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), loggifyTemplate(templateResource.getName()), Arrays.toString(markupSelectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\"", TemplateEngine.threadIndex(), loggifyTemplate(templateResource.getName()));
                }
            } else {
                logger.trace("[THYMELEAF][{}] Finished processing of \"{}\"", TemplateEngine.threadIndex(), loggifyTemplate(templateResource.getName()));
            }
        }

    }




    private static void processParsedMarkup(final ParsedTemplateMarkup template, final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting processing of template \"{}\"", TemplateEngine.threadIndex(), loggifyTemplate(template.getTemplateResolution().getTemplateName()));
        }

        template.getInternalMarkup().process(templateHandler);

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished processing of template \"{}\"", TemplateEngine.threadIndex(), loggifyTemplate(template.getTemplateResolution().getTemplateName()));
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
        final List<Class<? extends ITemplateHandler>> preProcessors = configuration.getPreProcessors();
        if (preProcessors != null) {
            for (final Class<? extends ITemplateHandler> preProcessorClass : preProcessors) {
                final ITemplateHandler preProcessor;
                try {
                    preProcessor = preProcessorClass.newInstance();
                } catch (final Exception e) {
                    // This should never happen - class was already checked during configuration to contain a zero-arg constructor
                    throw new TemplateProcessingException(
                            "An exception happened during the creation of a new instance of pre-processor " + preProcessorClass.getClass().getName(), e);
                }
                // Initialize the pre-processor
                preProcessor.setProcessingContext(processingContext);
                if (firstHandler == null) {
                    firstHandler = preProcessor;
                    lastHandler = preProcessor;
                } else {
                    lastHandler.setNext(preProcessor);
                    lastHandler = preProcessor;
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
        final List<Class<? extends ITemplateHandler>> postProcessors = configuration.getPostProcessors();
        if (postProcessors != null) {
            for (final Class<? extends ITemplateHandler> postProcessorClass : postProcessors) {
                final ITemplateHandler postProcessor;
                try {
                    postProcessor = postProcessorClass.newInstance();
                } catch (final Exception e) {
                    // This should never happen - class was already checked during configuration to contain a zero-arg constructor
                    throw new TemplateProcessingException(
                            "An exception happened during the creation of a new instance of post-processor " + postProcessorClass.getClass().getName(), e);
                }
                // Initialize the pre-processor
                postProcessor.setProcessingContext(processingContext);
                if (firstHandler == null) {
                    firstHandler = postProcessor;
                    lastHandler = postProcessor;
                } else {
                    lastHandler.setNext(postProcessor);
                    lastHandler = postProcessor;
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




    private static String loggifyTemplate(final String template) {
        if (template == null) {
            return null;
        }
        return StringUtils.abbreviate(template, 40).replace('\n','\\');
    }






    private static final class TemplateAndResourceResolution {

        final TemplateResolution templateResolution;
        final IResource resource;

        public TemplateAndResourceResolution(final TemplateResolution templateResolution, final IResource resource) {
            super();
            this.templateResolution = templateResolution;
            this.resource = resource;
        }

    }


}

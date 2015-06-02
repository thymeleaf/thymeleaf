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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.AlwaysValidCacheEntryValidity;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.TemplateProcessingContext;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resource.StringResource;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.HTMLTemplateParser;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.XMLTemplateParser;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.text.ITextRepository;
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

    private static final ITemplateParser htmlParser = new HTMLTemplateParser(40,2048);
    private static final ITemplateParser xmlParser = new XMLTemplateParser(40, 2048);

    private final ICache<String,ParsedTemplateMarkup> templateCache; // might be null! (= no cache)
    private final ICache<String,ParsedFragmentMarkup> fragmentCache; // might be null! (= no cache)





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

    }
    

    
    
    
    /**
     * <p>
     *   Clears the template cache.
     * </p>
     */
    public void clearTemplateCache() {
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
    public void clearTemplateCacheFor(final String template) {
        // TODO cache keys are more complex than this, this method doesn't really do its job alright
        if (this.templateCache != null) {
            this.templateCache.clearKey(template);
        }
    }
    
    
    
    
    
    /**
     * <p>
     *   Clears the fragments cache.
     * </p>
     */
    public void clearFragmentCache() {
        if (this.fragmentCache != null) {
            this.fragmentCache.clear();
        }
    }

    
    /**
     * <p>
     *   Clears a specific entry at the fragment cache.
     * </p>
     * 
     * @param fragment the fragment to be cleared.
     */
    public void clearFragmentCacheFor(final String fragment) {
        // TODO cache keys are more complex than this, this method doesn't really do its job alright
        if (this.fragmentCache != null) {
            this.fragmentCache.clearKey(fragment);
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

        // TODO Maybe cache keys should include the TemplateMode? (at least if we allow mixing modes in includes...) NOTE Its better if they don't include
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
        final TemplateAndResourceResolution resolution = resolveTemplate(configuration, context, template);


        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateMarkup object as the result of reading the template
         */
        final ParsedTemplateMarkup parsedTemplate = new ParsedTemplateMarkup(configuration, resolution.templateResolution);
        final MarkupBuilderTemplateHandler builderHandler = new MarkupBuilderTemplateHandler(true, parsedTemplate.getInternalMarkup());


        /*
         * PROCESS THE TEMPLATE
         */
        processAsResource(
                configuration, resolution.templateResolution.getTemplateMode(), false, resolution.resource, markupSelectors, builderHandler);


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







    public ParsedFragmentMarkup parseTextualFragment(
            final ITemplateProcessingContext processingContext, final String template, final String textualFragment) {
        return parseTextualFragment(processingContext.getConfiguration(), processingContext.getTemplateMode(), template, textualFragment);
    }


    public ParsedFragmentMarkup parseTextualFragment(
            final IEngineConfiguration configuration, final TemplateMode templateMode,
            final String template, final String textualFragment) {

        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(textualFragment, "Textual Fragment cannot be null");


        final String cacheKey = computeCacheKey(configuration.getTextRepository(), template, textualFragment);

        /*
         * First look at the cache - it might be already cached
         */
        if (this.fragmentCache != null) {
            final ParsedFragmentMarkup cached =  this.fragmentCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }


        /*
         * Create the Resource (a StringResource, in this case) representing the fragment to parse
         */
        final IResource fragmentResource = new StringResource(cacheKey, textualFragment);


        /*
         *  Create the Template Handler that will be in charge of building a ParsedFragmentMarkup object as the result of
         *  reading the template.
         *
         *  Note we will be using validity = ALWAYS because these textual fragments are indexed by their entire text,
         *  and the result of parsing them should never change.
         */
        final ParsedFragmentMarkup parsedFragment =
                new ParsedFragmentMarkup(configuration, templateMode, AlwaysValidCacheEntryValidity.INSTANCE);
        final MarkupBuilderTemplateHandler builderHandler = new MarkupBuilderTemplateHandler(true, parsedFragment.getInternalMarkup());


        /*
         * PROCESS THE TEMPLATE
         */
        processAsResource(configuration, templateMode, true, fragmentResource, null, builderHandler);


        /*
         * Cache the template if it is cacheable
         */
        if (this.fragmentCache != null) {
            if (parsedFragment.getValidity().isCacheable()) {
                this.fragmentCache.put(cacheKey, parsedFragment);
            }
        }

        return parsedFragment;

    }






    public ParsedFragmentMarkup parseTemplateFragment(
            final ITemplateProcessingContext processingContext, final String template, final String[] markupSelectors) {
        return parseTemplateFragment(
                processingContext.getConfiguration(), processingContext.getVariables(), template, markupSelectors);
    }


    public ParsedFragmentMarkup parseTemplateFragment(
            final IEngineConfiguration configuration, final IContext context,
            final String template, final String[] markupSelectors) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // markupSelectors CAN be null (selecting the whole template as a fragment...

        final String cacheKey = computeCacheKey(configuration.getTextRepository(), template, markupSelectors);

        /*
         * First look at the cache - it might be already cached
         */
        if (this.fragmentCache != null) {
            final ParsedFragmentMarkup cached =  this.fragmentCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final TemplateAndResourceResolution resolution =
                resolveTemplate(configuration, context, template);


        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateMarkup object as the result of reading the template
         */
        final ParsedFragmentMarkup parsedFragment =
                new ParsedFragmentMarkup(configuration, resolution.templateResolution.getTemplateMode(), resolution.templateResolution.getValidity());
        final MarkupBuilderTemplateHandler builderHandler =
                new MarkupBuilderTemplateHandler(true, parsedFragment.getInternalMarkup());


        /*
         * PROCESS THE TEMPLATE
         */
        processAsResource(
                configuration, resolution.templateResolution.getTemplateMode(), true,
                resolution.resource, markupSelectors, builderHandler);


        /*
         * Cache the template if it is cacheable
         */
        if (this.fragmentCache != null) {
            if (parsedFragment.getValidity().isCacheable()) {
                this.fragmentCache.put(cacheKey, parsedFragment);
            }
        }

        return parsedFragment;

    }









    public void processTemplate(final IEngineConfiguration configuration, final IContext context,
                                final String template, final Writer writer) {
        processTemplate(configuration, context, template, null, writer);
    }


    public void processTemplate(final IEngineConfiguration configuration, final IContext context,
                                final String template, final String[] markupSelectors, final Writer writer) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // Markup Selectors CAN be null

        final String cacheKey = computeCacheKey(configuration.getTextRepository(), template, markupSelectors);


        /*
         * First look at the cache - it might be already cached
         */
        if (this.templateCache != null) {
            final ParsedTemplateMarkup cached =  this.templateCache.get(cacheKey);
            if (cached != null) {
                // Create the Processing Context instance that corresponds to this execution of the template engine
                final ITemplateProcessingContext processingContext =
                        new TemplateProcessingContext(configuration, this, cached.getTemplateResolution(), context);
                // Create the handler chain to process the data
                final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(processingContext, writer);
                // Process the cached template itself
                processAsObject(cached, processingHandlerChain);
                return;
            }
        }


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final TemplateAndResourceResolution resolution = resolveTemplate(configuration, context, template);


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
        if (resolution.templateResolution.getValidity().isCacheable() && this.templateCache != null) {
            // Create the handler chain to create the Template object
            final ParsedTemplateMarkup parsedTemplate = new ParsedTemplateMarkup(processingContext.getConfiguration(), resolution.templateResolution);
            final MarkupBuilderTemplateHandler builderHandler = new MarkupBuilderTemplateHandler(false, parsedTemplate.getInternalMarkup());
            // Process the cached template itself
            processAsResource(
                    processingContext.getConfiguration(), processingContext.getTemplateMode(), false,
                    resolution.resource, markupSelectors, builderHandler);
            // Put the new template into cache
            this.templateCache.put(cacheKey, parsedTemplate);
            // Process the read (+cached) template itself
            processAsObject(parsedTemplate, processingHandlerChain);
            return;
        }


        /*
         *  Process the template, which is not cacheable (so no worry about caching)
         */
        processAsResource(
                processingContext.getConfiguration(), processingContext.getTemplateMode(), false,
                resolution.resource, markupSelectors, processingHandlerChain);

    }












    private static TemplateAndResourceResolution resolveTemplate(final IEngineConfiguration configuration, final IContext context, final String template) {

        final Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        TemplateResolution templateResolution = null;
        IResource templateResource = null;

        for (final ITemplateResolver templateResolver : templateResolvers) {

            templateResolution = templateResolver.resolveTemplate(configuration, context, template);

            if (templateResolution != null) {

                final String resourceName = templateResolution.getResourceName();
                final IResourceResolver resourceResolver = templateResolution.getResourceResolver();

                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Trying to resolve template \"{}\" as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), template, resourceName, resourceResolver.getName()});
                }

                templateResource =
                        resourceResolver.resolveResource(configuration, context, resourceName, templateResolution.getCharacterEncoding());

                if (templateResource == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}] Template \"{}\" could not be resolved as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), template, resourceName, resourceResolver.getName()});
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[THYMELEAF][{}] Template \"{}\" was correctly resolved as resource \"{}\" in mode {} with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), template, resourceName, templateResolution.getTemplateMode(), resourceResolver.getName()});
                    }
                    break;
                }

            } else {

                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), template});
                }

            }

        }

        if (templateResolution == null || templateResource == null) {
            throw new TemplateInputException(
                    "Error resolving template \"" + template + "\", " +
                    "template might not exist or might not be accessible by " +
                    "any of the configured Template Resolvers");
        }

        return new TemplateAndResourceResolution(templateResolution, templateResource);

    }





    private static void processAsResource(
            final IEngineConfiguration configuration, final TemplateMode templateMode, final boolean fragment,
            final IResource templateResource, final String[] markupSelectors, final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof MarkupBuilderTemplateHandler) {
                if (markupSelectors != null) {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), templateResource.getName(), Arrays.asList(markupSelectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Starting parsing of \"{}\"", TemplateEngine.threadIndex(), templateResource.getName());
                }
            } else {
                logger.trace("[THYMELEAF][{}] Starting processing of \"{}\"", TemplateEngine.threadIndex(), templateResource.getName());
            }
        }

        /*
         * Handler chain is in place - now we must use it for calling the parser and initiate the processing
         */
        if (templateMode.isHTML()) {
            if (fragment) {
                htmlParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                htmlParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else if (templateMode.isXML()) {
            if (fragment) {
                xmlParser.parseFragment(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            } else {
                xmlParser.parseTemplate(configuration, templateMode, templateResource, markupSelectors, templateHandler);
            }
        } else {
            throw new IllegalArgumentException(
                "Cannot process \"" + templateResource.getName() + "\" " +
                "with unsupported template mode: " + templateMode);
        }

        if (logger.isTraceEnabled()) {
            if (templateHandler instanceof MarkupBuilderTemplateHandler) {
                if (markupSelectors != null) {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\" with selector/s \"{}\"",
                            new Object[] {TemplateEngine.threadIndex(), templateResource.getName(), Arrays.asList(markupSelectors)});
                } else {
                    logger.trace("[THYMELEAF][{}] Finished parsing of \"{}\"", TemplateEngine.threadIndex(), templateResource.getName());
                }
            } else {
                logger.trace("[THYMELEAF][{}] Finished processing of \"{}\"", TemplateEngine.threadIndex(), templateResource.getName());
            }
        }

    }




    private static void processAsObject(final ParsedTemplateMarkup template, final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting processing of template \"{}\"", TemplateEngine.threadIndex(), template.getTemplateResolution().getTemplateName());
        }

        template.getInternalMarkup().process(templateHandler);

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished processing of template \"{}\"", TemplateEngine.threadIndex(), template.getTemplateResolution().getTemplateName());
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






    private static String computeCacheKey(
            final ITextRepository textRepository, final String template, final String textualFragment) {

        if (textualFragment == null) {
            return template;
        }
        return textRepository.getText(template, "##", textualFragment);
    }


    private static String computeCacheKey(
            final ITextRepository textRepository, final String template, final String[] markupSelectors) {

        if (markupSelectors == null || markupSelectors.length == 0) {
            return template;
        }
        final String markupSelectorsKeyFragment = computeMarkupSelectorsKeyFragment(markupSelectors);
        return textRepository.getText(template, "::", markupSelectorsKeyFragment);
    }


    private static String computeMarkupSelectorsKeyFragment(final String[] markupSelectors) {

        if (markupSelectors.length == 1) {
            if (markupSelectors[0] == null) {
                return "";
            }
            return markupSelectors[0];
        }
        final StringBuilder strBuilder = new StringBuilder(markupSelectors.length * 16);
        for (int i = 0; i < markupSelectors.length; i++) {
            if (strBuilder.length() > 0) {
                strBuilder.append(',');
            }
            strBuilder.append(markupSelectors[i] != null? markupSelectors[i] : "");
        }
        return strBuilder.toString();

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

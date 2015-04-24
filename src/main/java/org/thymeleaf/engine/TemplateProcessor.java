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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.TemplateProcessingContext;
import org.thymeleaf.aurora.parser.HTMLTemplateParser;
import org.thymeleaf.aurora.parser.ITemplateParser;
import org.thymeleaf.aurora.parser.XMLTemplateParser;
import org.thymeleaf.aurora.resource.IResource;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class TemplateProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateProcessor.class);

    private static final ITemplateParser htmlParser = new HTMLTemplateParser(40,2048);
    private static final ITemplateParser xmlParser = new XMLTemplateParser(40, 2048);

    private final ICache<String,Template> templateCache; // might be null! (= no cache)
    private final ICache<String,Template> fragmentCache; // might be null! (= no cache)





    /**
     * <p>
     *   This constructor should only be called directly for <strong>testing purposes</strong>.
     * </p>
     *
     * @param configuration the configuration being currently used.
     */
    public TemplateProcessor(final IEngineConfiguration configuration) {
        
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
     * @param templateName the name of the template whose entries have to be cleared.
     */
    public void clearTemplateCacheFor(final String templateName) {
        if (this.templateCache != null) {
            this.templateCache.clearKey(templateName);
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
        if (this.fragmentCache != null) {
            this.fragmentCache.clearKey(fragment);
        }
    }
    
    
    
    
    
    

    public Template readTemplate(final IEngineConfiguration configuration, final IContext context, final String templateName) {
        
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(templateName, "Template Name cannot be null");


        /*
         * First look at the cache - it might be already cached
         */
        if (this.templateCache != null) {
            final Template cached =  this.templateCache.get(templateName);
            if (cached != null) {
                return cached;
            }
        }


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final TemplateResourceResolution resourceResolution = resolveTemplate(configuration, context, templateName);


        /*
         * Create the Processing Context instance that corresponds to this execution of the template engine
         */
        final ITemplateProcessingContext processingContext =
                new TemplateProcessingContext(configuration, resourceResolution.templateResolution, context);


        /*
         *  Create the Template Handler that will be in charge of building a Template object as the result of reading the template
         */
        final TemplateBuilderTemplateHandler builderHandlerChain = createTemplateBuildingHandlerChain(processingContext);


        /*
         * PROCESS THE TEMPLATE
         */
        processTemplateAsResource(processingContext, resourceResolution.resource, builderHandlerChain);


        /*
         * Once processed, the builder template handler will be able to return a Template object that we can cache
         */
        final Template template = builderHandlerChain.getTemplate();


        /*
         * Cache the template if it is cacheable
         */
        if (this.templateCache != null) {
            if (resourceResolution.templateResolution.getValidity().isCacheable()) {
                this.templateCache.put(templateName, template);
            }
        }
        
        return template;
        
    }






    public void processTemplate(final IEngineConfiguration configuration, final IContext context,
                                final String templateName, final Writer writer) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");
        Validate.notNull(templateName, "Template Name cannot be null");


        /*
         * First look at the cache - it might be already cached
         */
        if (this.templateCache != null) {
            final Template cached =  this.templateCache.get(templateName);
            if (cached != null) {
                // Create the Processing Context instance that corresponds to this execution of the template engine
                final ITemplateProcessingContext processingContext =
                        new TemplateProcessingContext(configuration, cached.getTemplateResolution(), context);
                // Create the handler chain to process the data
                final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(processingContext, writer);
                // Process the cached template itself
                processTemplateAsObject(cached, processingHandlerChain);
                return;
            }
        }


        /*
         * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
         */
        final TemplateResourceResolution resourceResolution = resolveTemplate(configuration, context, templateName);


        /*
         * Create the Processing Context instance that corresponds to this execution of the template engine
         */
        final ITemplateProcessingContext processingContext =
                new TemplateProcessingContext(configuration, resourceResolution.templateResolution, context);


        /*
         * Create the handler chain to process the data
         */
        final ITemplateHandler processingHandlerChain = createTemplateProcessingHandlerChain(processingContext, writer);


        /*
         * If the resolved template is cacheable, so we will first read it as an object, cache it, and then process it
         */
        if (resourceResolution.templateResolution.getValidity().isCacheable() && this.templateCache != null) {
            // Create the handler chain to create the Template object
            final TemplateBuilderTemplateHandler builderHandlerChain = createTemplateBuildingHandlerChain(processingContext);
            // Process the cached template itself
            processTemplateAsResource(processingContext, resourceResolution.resource, builderHandlerChain);
            // Once processed, the builder template handler will be able to return a Template object that we can cache
            final Template template = builderHandlerChain.getTemplate();
            // Put the new template into cache
            this.templateCache.put(templateName, template);
            // Process the read (+cached) template itself
            processTemplateAsObject(template, processingHandlerChain);
            return;
        }


        /*
         *  Process the template, which is not cacheable (so no worry about caching)
         */
        processTemplateAsResource(processingContext, resourceResolution.resource, processingHandlerChain);

    }












    private static TemplateResourceResolution resolveTemplate(final IEngineConfiguration configuration, final IContext context, final String templateName) {

        final Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        TemplateResolution templateResolution = null;
        IResource templateResource = null;

        for (final ITemplateResolver templateResolver : templateResolvers) {

            templateResolution = templateResolver.resolveTemplate(configuration, context, templateName);

            if (templateResolution != null) {

                final String resourceName = templateResolution.getResourceName();
                final IResourceResolver resourceResolver = templateResolution.getResourceResolver();

                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Trying to resolve template \"{}\" as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, resourceResolver.getName()});
                }

                templateResource =
                        resourceResolver.getResource(configuration, context, resourceName, templateResolution.getCharacterEncoding());

                if (templateResource == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}] Template \"{}\" could not be resolved as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, resourceResolver.getName()});
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[THYMELEAF][{}] Template \"{}\" was correctly resolved as resource \"{}\" in mode {} with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, templateResolution.getTemplateMode(), resourceResolver.getName()});
                    }
                    break;
                }

            } else {

                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), templateName});
                }

            }

        }

        if (templateResolution == null || templateResource == null) {
            throw new TemplateInputException(
                    "Error resolving template \"" + templateName + "\", " +
                    "template might not exist or might not be accessible by " +
                    "any of the configured Template Resolvers");
        }

        return new TemplateResourceResolution(templateResolution, templateResource);

    }





    private static void processTemplateAsResource(
            final ITemplateProcessingContext processingContext, final IResource templateResource,
            final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting processing of template \"{}\"", TemplateEngine.threadIndex(), processingContext.getTemplateResolution().getTemplateName());
        }

        /*
         * Handler chain is in place - now we must use it for calling the parser and initiate the processing
         */
        final TemplateMode templateMode = processingContext.getTemplateMode();
        if (templateMode.isHTML()) {
            htmlParser.parse(processingContext.getConfiguration(), templateMode, templateResource, templateHandler);
        } else if (templateMode.isXML()) {
            xmlParser.parse(processingContext.getConfiguration(), templateMode, templateResource, templateHandler);
        } else {
            throw new IllegalArgumentException(
                "Cannot process template \"" + processingContext.getTemplateResolution().getResourceName() + "\" " +
                "with unsupported template mode: " + templateMode);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished processing of template \"{}\"", TemplateEngine.threadIndex(), processingContext.getTemplateResolution().getTemplateName());
        }

    }




    private static void processTemplateAsObject(final Template template, final ITemplateHandler templateHandler) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting processing of template \"{}\"", TemplateEngine.threadIndex(), template.getTemplateResolution().getTemplateName());
        }

        template.process(templateHandler);

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





    private static TemplateBuilderTemplateHandler createTemplateBuildingHandlerChain(
            final ITemplateProcessingContext processingContext) {

        final TemplateBuilderTemplateHandler templateBuilderTemplateHandler = new TemplateBuilderTemplateHandler();
        templateBuilderTemplateHandler.setProcessingContext(processingContext);

        return templateBuilderTemplateHandler;

    }



    // TODO Add fragment (as in th:utext) processing and caching. They should be modelled as Template objects too!

    
    
    private static String computeFragmentCacheKey(final String templateMode, final String fragment) {
        return '{' +  templateMode + '}' + fragment;
    }
    



    private static final class TemplateResourceResolution {

        final TemplateResolution templateResolution;
        final IResource resource;

        public TemplateResourceResolution(final TemplateResolution templateResolution, final IResource resource) {
            super();
            this.templateResolution = templateResolution;
            this.resource = resource;
        }

    }


}

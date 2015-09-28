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
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.resource.IResource;
import org.thymeleaf.resource.StringResource;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.ParsableArtifactType;
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


    private final ICache<TemplateCacheKey,ParsedTemplateModel> templateCache; // might be null! (= no cache)
    private final ICache<FragmentCacheKey,ParsedFragmentModel> fragmentCache; // might be null! (= no cache)





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
        if (this.fragmentCache != null) {
            // We will be removing all entries at the fragment cache related to this template
            final Set<FragmentCacheKey> keysToBeRemoved = new HashSet<FragmentCacheKey>(4);
            final Set<FragmentCacheKey> fragmentCacheKeys = this.fragmentCache.keySet();
            // We are iterating twice and creating a temporary set just in case the 'keySet' Set is still connected
            // to the original cache store and we provoke ConcurrentModificationExceptions when removing entries
            for (final FragmentCacheKey fragmentCacheKey : fragmentCacheKeys) {
                final String ownerTemplate = fragmentCacheKey.getOwnerTemplate();
                if (ownerTemplate != null) {
                    // It's not a standalone template, so we are interested on the owner template
                    if (ownerTemplate.equals(template)) {
                        keysToBeRemoved.add(fragmentCacheKey);
                    }
                } else {
                    if (fragmentCacheKey.getFragment().equals(template)) {
                        keysToBeRemoved.add(fragmentCacheKey);
                    }
                }
            }
            for (final FragmentCacheKey keyToBeRemoved : keysToBeRemoved) {
                this.fragmentCache.clearKey(keyToBeRemoved);
            }
        }
    }






    /*
     * -------------
     * PARSE methods
     * -------------
     *
     * Parse methods will create either 'parsed fragment' or 'parsed templates' that are basically collections of
     * events. There are two main differences between parsed templates and parsed fragments:
     *
     *    * Parsed Fragments don't contain 'templateStart' and 'templateEnd' events. Parsed Templates do contain them.
     *    * Parsed Templates contain the entire TemplateResolution object, which is required in order to cache template
     *      resolution and not having to resolve templates each time if they are already cached.
     *
     */


    public ParsedTemplateModel parseStandaloneTemplate(
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

        return parseTemplate(
                configuration,
                null, template, selectors,
                0, 0,
                templateMode,
                context,
                useCache);

    }


    public ParsedTemplateModel parseNestedTemplate(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String template,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        // NOTE selectors cannot be specified when parsing a nested template
        Validate.notNull(templateMode, "Template mode cannot be null");
        // NOTE that context is not needed in this case because we will not be using the TemplateResolvers

        return parseTemplate(
                configuration,
                ownerTemplate, template, null,
                lineOffset, colOffset,
                templateMode,
                null,
                useCache);

    }


    private ParsedTemplateModel parseTemplate(
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
            final ParsedTemplateModel cached =  this.templateCache.get(cacheKey);
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
        final ParsedTemplateModel parsedTemplate = new ParsedTemplateModel(configuration, resolution.templateResolution);
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(true, parsedTemplate.getInternalModel());


        /*
         * PROCESS THE TEMPLATE
         */
        processResolvedResource(
                configuration,
                ParsableArtifactType.TEMPLATE,
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







    public ParsedFragmentModel parseStandaloneFragment(
            final IEngineConfiguration configuration,
            final String fragment, final String[] selectors,
            final TemplateMode templateMode,
            final IContext context,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(fragment, "Fragment cannot be null");
        // selectors CAN be null if we are going to render the entire fragment
        // templateMode CAN be null if we are going to use the mode specified by the template resolver
        Validate.notNull(context, "Context cannot be null");

        return parseFragment(
                configuration,
                null, fragment, selectors,
                0, 0,
                templateMode,
                context,
                useCache);

    }


    public ParsedFragmentModel parseNestedFragment(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String fragment,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final boolean useCache) {

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(fragment, "Fragment cannot be null");
        // NOTE selectors cannot be specified when parsing a nested fragment
        Validate.notNull(templateMode, "Template mode cannot be null");
        // NOTE that context is not needed in this case because we will not be using the TemplateResolvers

        return parseFragment(
                configuration,
                ownerTemplate, fragment, null,
                lineOffset, colOffset,
                templateMode,
                null,
                useCache);

    }


    private ParsedFragmentModel parseFragment(
            final IEngineConfiguration configuration,
            final String ownerTemplate, final String fragment, final String[] selectors,
            final int lineOffset, final int colOffset,
            final TemplateMode templateMode,
            final IContext context,
            final boolean useCache) {


        final FragmentCacheKey cacheKey =
                useCache? new FragmentCacheKey(ownerTemplate, fragment, selectors, lineOffset, colOffset, templateMode) : null;

        /*
         * First look at the cache - it might be already cached
         */
        if (useCache && this.fragmentCache != null) {
            final ParsedFragmentModel cached =  this.fragmentCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }


        final IResource computedResource;
        final TemplateMode computedTemplateMode;
        final ICacheEntryValidity computedCacheEntryValidity;

        if (ownerTemplate != null) {
            // This is a nested fragment (i.e. just a text from a template -- we don't need to resolve a template and
            // match selectors). Also, we know that in this case, selectors == null.

            /*
             * Create the Resource (a StringResource, in this case) representing the fragment to parse
             * Resource name will be its cache key, or the fragment itself if no cache is to be used
             */
            computedResource = new StringResource((cacheKey != null? cacheKey.toString() : fragment), fragment);

            /*
             * Template Mode has to be always specified (forced) for textual fragments
             */
            computedTemplateMode = templateMode;

            /*
             * Cache entry validity for textual fragments will be 'always' if cache is used
             */
            computedCacheEntryValidity = useCache? AlwaysValidCacheEntryValidity.INSTANCE : NonCacheableCacheEntryValidity.INSTANCE;

        } else {
            // This is not a nested fragment -- we need to resolve a template and, maybe, match selectors

            /*
             * Resolve the template, obtain the IResource and its metadata (TemplateResolution)
             */
            final Resolution resolution =
                    resolve(configuration, configuration.getTemplateResolvers(), fragment, templateMode, context);

            /*
             * Assign the values
             */
            computedResource = resolution.resource;
            computedTemplateMode = resolution.templateResolution.getTemplateMode();
            computedCacheEntryValidity = resolution.templateResolution.getValidity();

        }


        /*
         *  Create the Template Handler that will be in charge of building a ParsedTemplateModel object as the result of reading the template
         */
        final ParsedFragmentModel parsedFragment = new ParsedFragmentModel(configuration, computedTemplateMode, computedCacheEntryValidity);
        final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(true, parsedFragment.getInternalModel());

        /*
         * PROCESS THE TEMPLATE
         */
        processResolvedResource(
                configuration,
                ParsableArtifactType.FRAGMENT,
                ownerTemplate, computedResource, selectors,
                lineOffset, colOffset,
                computedTemplateMode,
                builderHandler);


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






    /*
     * ---------------
     * PROCESS methods
     * ---------------
     *
     * Note ONLY TEMPLATES can be processed (not fragments). Fragments lack 'templateStart' and 'templateEnd'
     * events, and are meant only to be parsed and then added to templates being processed. Templates, on the other
     * hand, can not only be parsed (and then cached), but also completely processed and then its output written
     * to a Writer. The very fact that the output of 'processing' is a Writer output requires the presence of
     * 'templateStart' and 'templateEnd' events, so a 'processFragment' set of methods would not make sense.
     *
     * Also note that given fragments for a template cannot be parsed/processed for a different template mode than
     * the template's, processing those (probably nested) fragments as complete templates in their required template
     * mode is the only way to include them into the containing template. The most prominent example of this
     * is the inlining mechanism.
     */


    public void processStandaloneTemplate(
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

        processTemplate(
                configuration,
                null, template, selectors,
                0, 0,
                templateMode,
                context,
                writer,
                useCache);

    }


    public void processNestedTemplate(
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

        processTemplate(
                configuration,
                ownerTemplate, template, null,
                lineOffset, colOffset,
                templateMode,
                context,
                writer,
                useCache);

    }


    private void processTemplate(
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

            final ParsedTemplateModel cached =  this.templateCache.get(cacheKey);

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
            final ParsedTemplateModel parsedTemplate = new ParsedTemplateModel(processingContext.getConfiguration(), resolution.templateResolution);
            final ModelBuilderTemplateHandler builderHandler = new ModelBuilderTemplateHandler(false, parsedTemplate.getInternalModel());
            // Process the cached template itself
            processResolvedResource(
                    processingContext.getConfiguration(),
                    ParsableArtifactType.TEMPLATE,
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
                ParsableArtifactType.TEMPLATE,
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
            final ParsableArtifactType artifactType,
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
                this.htmlParser.parseStandalone(configuration, artifactType, resource, selectors, templateMode, templateHandler);
            } else {
                this.htmlParser.parseNested(configuration, artifactType, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.XML) {
            if (ownerTemplate == null) {
                this.xmlParser.parseStandalone(configuration, artifactType, resource, selectors, templateMode, templateHandler);
            } else {
                this.xmlParser.parseNested(configuration, artifactType, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.TEXT) {
            if (ownerTemplate == null) {
                this.textParser.parseStandalone(configuration, artifactType, resource, selectors, templateMode, templateHandler);
            } else {
                this.textParser.parseNested(configuration, artifactType, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.JAVASCRIPT) {
            if (ownerTemplate == null) {
                this.javascriptParser.parseStandalone(configuration, artifactType, resource, selectors, templateMode, templateHandler);
            } else {
                this.javascriptParser.parseNested(configuration, artifactType, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
            }
        } else if (templateMode == TemplateMode.CSS) {
            if (ownerTemplate == null) {
                this.cssParser.parseStandalone(configuration, artifactType, resource, selectors, templateMode, templateHandler);
            } else {
                this.cssParser.parseNested(configuration, artifactType, ownerTemplate, resource, lineOffset, colOffset, templateMode, templateHandler);
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




    private static void processParsedModel(final ParsedTemplateModel template, final ITemplateHandler templateHandler) {

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

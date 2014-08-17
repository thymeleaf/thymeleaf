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
package org.thymeleaf;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   The template repository is an internal utility class that is responsible before the
 *   Template Engine of retrieving templates (parsed) and fragments, performing all the necessary
 *   operations against the caches in order to obtain the required data.
 * </p>
 * <p>
 *   Each {@link TemplateEngine} uses a single object of this class.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class TemplateRepository {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);

    private final ICache<String,Template> templateCache; // might be null! (= no cache)
    private final ICache<String,List<Node>> fragmentCache; // might be null! (= no cache)
    private final Map<String,ITemplateParser> parsersByTemplateMode;
    
    
    /**
     * <p>
     *   This constructor should only be called directly for <p>testing purposes</p>.
     * </p>
     * 
     * @param configuration the configuration being currently used.
     */
    public TemplateRepository(final Configuration configuration) {
        
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
            
        this.parsersByTemplateMode = new HashMap<String,ITemplateParser>(10, 1.0f);
        for (final ITemplateModeHandler handler : configuration.getTemplateModeHandlers()) {
            this.parsersByTemplateMode.put(handler.getTemplateModeName(), handler.getTemplateParser());
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
    
    
    
    
    
    

    /**
     * <p>
     *   Obtains a template. This method is responsible of providing a {@link Template} to the 
     *   {@link TemplateEngine} by following these steps:
     * </p>
     * <ul>
     *   <li>Try to get the template from the cache (if it exists).</li>
     *   <li>Querying all the configured {@link ITemplateResolver} objects until one of them resolves
     *       the template.</li>
     *   <li>If resolved, use the associated {@link IResourceResolver} object to obtain an {@link InputStream}
     *       on it.</li>
     *   <li>Obtain the {@link ITemplateModeHandler} object associated to the template mode assigned
     *       to the resolved template (by the template resolver).</li>
     *   <li>Use the {@link ITemplateParser} of the selected {@link ITemplateModeHandler} to read and
     *       parse the template into a {@link Document}.</li>
     *   <li>If required and allowed by configuration, put the resolved template into the cache.</li>
     * </ul>
     * 
     * @param templateProcessingParameters the parameters object containing all the necessary pieces of
     *                                     data in order to adequately resolve the template.
     * @return the resolved and parsed Template.
     */
    public Template getTemplate(final TemplateProcessingParameters templateProcessingParameters) {
        
        Validate.notNull(templateProcessingParameters, "Template Processing Parameters cannot be null");

        final String templateName = templateProcessingParameters.getTemplateName();

        if (this.templateCache != null) {
            final Template cached = 
                this.templateCache.get(templateName);
            if (cached != null) {
                return cached.createDuplicate();
            }
        }
        
        final Configuration configuration = templateProcessingParameters.getConfiguration();
        final Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        TemplateResolution templateResolution = null;
        InputStream templateInputStream = null;
        
        for (final ITemplateResolver templateResolver : templateResolvers) {
                
                templateResolution = templateResolver.resolveTemplate(templateProcessingParameters);
                
                if (templateResolution != null) {
                    
                    final String resourceName = templateResolution.getResourceName();

                    final IResourceResolver resourceResolver = templateResolution.getResourceResolver();
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}] Trying to resolve template \"{}\" as resource \"{}\" with resource resolver \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateName, resourceName, resourceResolver.getName()});
                    }
                    
                    templateInputStream = 
                        resourceResolver.getResourceAsStream(templateProcessingParameters, resourceName);
                    
                    if (templateInputStream == null) {
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
        
        if (templateResolution == null || templateInputStream == null) {
            throw new TemplateInputException(
                    "Error resolving template \"" + templateProcessingParameters.getTemplateName() + "\", " +
                    "template might not exist or might not be accessible by " +
                    "any of the configured Template Resolvers");
        }

        
        final String templateMode = templateResolution.getTemplateMode();
        
        final ITemplateParser templateParser = this.parsersByTemplateMode.get(templateMode);
        if (templateParser == null) {
            throw new TemplateInputException(
                    "Template mode \"" + templateMode + "\" has not been configured");
        }
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting parsing of template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }
        
        
        final String characterEncoding = templateResolution.getCharacterEncoding();
        Reader reader = null;
        if (!StringUtils.isEmptyOrWhitespace(characterEncoding)) {
            try {
                reader = new InputStreamReader(templateInputStream, characterEncoding);
            } catch (final UnsupportedEncodingException e) {
                throw new TemplateInputException("Exception parsing document", e);
            }
        } else {
            reader = new InputStreamReader(templateInputStream);
        }
        
        final Document document = 
                templateParser.parseTemplate(configuration, templateName, reader);
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished parsing of template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }

        document.precompute(configuration);
        
        final Template template =
            new Template(templateName, templateResolution, document);

        if (this.templateCache != null) {
            if (templateResolution.getValidity().isCacheable()) {
                this.templateCache.put(templateName, template);
                return template.createDuplicate();
            }
        }
        
        return template;
        
    }

    

    /**
     * <p>
     *   Obtains a fragment. A <tt>fragment</tt> is a piece of template code that is usually
     *   read from a different source and needs parsing for converting it into a DOM subtree.
     * </p>
     * <p>
     *   Common examples of <i>fragments</i> are the messages in a <tt>Messages.properties</tt>
     *   file that contain tags like &lt;strong&gt; &lt;u&gt;, etc. and are included in thymeleaf
     *   templates via an attribute like <tt>th:utext</tt>.
     * </p>
     * 
     * @param arguments the execution arguments
     * @param fragment the fragment to be processed
     * @return the result of processing the fragment: a list of {@link Node} that can be linked
     *         to the DOM being processed.
     */
    public List<Node> getFragment(final Arguments arguments, final String fragment) {
        
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(fragment, "Fragment cannot be null");

        final String templateMode = arguments.getTemplateResolution().getTemplateMode();
        final String cacheKey = computeFragmentCacheKey(templateMode, fragment);

        if (this.fragmentCache != null) {
            final List<Node> fragmentNodes = this.fragmentCache.get(cacheKey);
            if (fragmentNodes != null) {
                return cloneFragmentNodes(fragmentNodes);
            }
        }
        
        final Configuration configuration = arguments.getConfiguration();
        
        final ITemplateParser templateParser =
                configuration.getTemplateModeHandler(templateMode).getTemplateParser();
      
        final List<Node> fragmentNodes = templateParser.parseFragment(configuration, fragment);
        
        if (this.fragmentCache != null) {
            this.fragmentCache.put(cacheKey, fragmentNodes);
            return cloneFragmentNodes(fragmentNodes);
        }


        return fragmentNodes;
        
    }
    
    
    
    private static String computeFragmentCacheKey(final String templateMode, final String fragment) {
        return '{' +  templateMode + '}' + fragment;
    }
    
    
    private static List<Node> cloneFragmentNodes(final List<Node> fragmentNodes) {
        if (fragmentNodes == null) {
            return null;
        }
        final List<Node> clonedNodes = new ArrayList<Node>(fragmentNodes.size() + 2);
        for (final Node fragmentNode : fragmentNodes) {
            clonedNodes.add(fragmentNode.cloneNode(null, false));
        }
        return clonedNodes;
    }
    
}

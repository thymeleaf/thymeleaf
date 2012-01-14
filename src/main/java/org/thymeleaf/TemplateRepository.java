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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.dom.Document;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templatecache.ITemplateCache;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class TemplateRepository {

    private static final Logger logger = LoggerFactory.getLogger(TemplateRepository.class);

    private final ITemplateCache templateCache; // might be null! (= no cache)
    private final Map<String,ITemplateParser> parsersByTemplateMode;
    
    
    TemplateRepository(final Configuration configuration) {
        super();
        Validate.notNull(configuration, "Configuration object cannot be null");
        this.templateCache = configuration.getTemplateCache();
        this.parsersByTemplateMode = new HashMap<String,ITemplateParser>();
        for (final ITemplateModeHandler handler : configuration.getTemplateModeHandlers()) {
            this.parsersByTemplateMode.put(handler.getTemplateModeName(), handler.getTemplateParser());
        }
    }
    

    
    
    
    
    public void clearTemplateCache() {
        if (this.templateCache != null) {
            this.templateCache.clearParsedTemplateCache();
        }
    }

    
    public void clearTemplateCacheFor(final String templateName) {
        if (this.templateCache != null) {
            this.templateCache.clearParsedTemplateCacheFor(templateName);
        }
    }
    
    
    
    
    
    
    
    
    public Template getTemplate(final TemplateProcessingParameters templateProcessingParameters) {

        final String templateName = templateProcessingParameters.getTemplateName();

        if (this.templateCache != null) {
            final Template cached = 
                this.templateCache.getParsedTemplate(templateName);
            if (cached != null) {
                return cached.clone();
            }
        }
        
        final Configuration configuration = templateProcessingParameters.getConfiguration();
        final Set<ITemplateResolver> templateResolvers = configuration.getTemplateResolvers();
        TemplateResolution templateResolution = null;
        InputStream templateInputStream = null;
        
        for (final ITemplateResolver templateResolver : templateResolvers) {
            
            if (templateInputStream == null) {
                
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
                    }
                    
                } else {
                    
                    if (logger.isTraceEnabled()) {
                        logger.trace("[THYMELEAF][{}] Skipping template resolver \"{}\" for template \"{}\"", new Object[] {TemplateEngine.threadIndex(), templateResolver.getName(), templateName});
                    }
                    
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
        
        // templateParser cannot be null (Configuration already checked that)
        final ITemplateParser templateParser = this.parsersByTemplateMode.get(templateMode);
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Starting parsing of template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }
        
        
        final Document document = 
                templateParser.parseTemplate(configuration, templateName, templateInputStream, templateResolution.getCharacterEncoding());
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Finished parsing of template \"{}\"", TemplateEngine.threadIndex(), templateName);
        }

        document.precompute(configuration);
        
        final Template template =
            new Template(templateName, templateResolution, document);

        if (this.templateCache != null) {
            if (templateResolution.getValidity().isCacheable()) {
                this.templateCache.putParsedTemplate(template);
                return template.clone();
            }
        }
        
        return template;
        
    }

    
    
}

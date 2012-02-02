/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Objects of this class contain all the required arguments for template
 *   resolution and parsing.
 * </p>
 * <p>
 *   These objects are created internally by the Template Engine in order
 *   to provide the Template Parser and the Template Resolvers with the info
 *   they need to read and parse the template document.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class TemplateProcessingParameters {

    
    private final Configuration configuration;
    private final String templateName;
    private final IContext context;

    

    /**
     * <p>
     *   Create a new TemplateProcessingParameters instance.
     * </p>
     * <p>
     *   <b>Mainly for internal use</b>. Should not be called directly except
     *   when processing a template (e.g. a fragment) using the {@link TemplateEngine}
     *   from a element/attribute processor.
     * </p>
     * 
     * @param configuration the configuration
     * @param templateName the template name
     * @param context the context
     */
    public TemplateProcessingParameters(
            final Configuration configuration,
            final String templateName,
            final IContext context) {
        
        super();
        
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(templateName, "Template name cannot be null");
        Validate.notNull(context, "Context cannot be null");
        
        this.configuration = configuration;
        this.templateName = templateName;
        this.context = context;
        
    }
    


    
    
    /**
     * <p>
     *   Returns the Template Engine configuration being used for
     *   processing templates.
     * </p>
     * 
     * @return the configuration
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    /**
     * <p>
     *   Returns the name of the template currently being processed.
     * </p>
     * 
     * @return the template name
     */
    public String getTemplateName() {
        return this.templateName;
    }

    /**
     * <p>
     *   Returns the current context specified for template processing.
     * </p>
     * 
     * @return the current context
     */
    public IContext getContext() {
        return this.context;
    }

    
    /**
     * <p>
     *   Returns the execution attributes.
     * </p>
     * 
     * @return the current map of execution attributes
     */
    public Map<String,Object> getExecutionAttributes() {
        return this.configuration.getExecutionAttributes();
    }

    
    /**
     * <p>
     *   Returns the execution attribute with the specified name.
     * </p>
     * 
     * @param attributeName the name of the attribute to be retrieved
     * @return the value of the attribute
     */
    public Object getExecutionAttribute(final String attributeName) {
        return this.configuration.getExecutionAttributes().get(attributeName);
    }
    
}

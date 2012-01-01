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
package org.thymeleaf.resourceresolver;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link IResourceResolver} that resolves
 *   resources as servlet context resources:
 * </p>
 * <p>
 *   <tt>
 *     servletContext.getResourceAsStream(resourceName)
 *   </tt>
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class ServletContextResourceResolver 
        implements IResourceResolver {

    public static final String NAME = "SERVLETCONTEXT";
    

    
    public ServletContextResourceResolver() {
        super();
    }
    
    
    public String getName() {
        return NAME; 
    }

    
    

    public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters, final String resourceName) {
        
        Validate.notNull(templateProcessingParameters, "Template Processing Parameters cannot be null");
        Validate.notNull(resourceName, "Resource name cannot be null");
        
        final IContext context = templateProcessingParameters.getContext();
        if (!(context instanceof IWebContext)) {
            throw new TemplateProcessingException(
                    "Resource resolution by ServletContext with " +
                    this.getClass().getName() + " can only be performed " +
                    "when context implements " + IWebContext.class.getName() + 
                    " [current context: " + context.getClass().getName() + "]");
        }
        
        final ServletContext servletContext = 
            ((IWebContext)context).getServletContext();
        if (servletContext == null) {
            throw new TemplateProcessingException("Thymeleaf context returned a null ServletContext");
        }
        
        return servletContext.getResourceAsStream(resourceName);
        
    }

    
}


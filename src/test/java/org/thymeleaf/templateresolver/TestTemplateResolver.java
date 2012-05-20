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
package org.thymeleaf.templateresolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.resourceresolver.IResourceResolver;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class TestTemplateResolver implements ITemplateResolver {

    private final String template;
    

    
    public TestTemplateResolver(final String template) {
        super();
        this.template = template;
    }

    public String getName() {
        return "TEST EXPRESSION TEMPLATE RESOLVER";
    }

    public Integer getOrder() {
        return Integer.valueOf(1);
    }

    public TemplateResolution resolveTemplate(final TemplateProcessingParameters templateProcessingParameters) {
        
        final String templateName = templateProcessingParameters.getTemplateName();

        final int placeholderPos = this.template.indexOf("{%%}");
        final String result =
            this.template.substring(0,placeholderPos) + 
            templateName +
            this.template.substring(placeholderPos + 4);
        
        final IResourceResolver resourceResolver = new TestResourceResolver(result);
        
        final TemplateResolution templateResolution = 
            new TemplateResolution(
                    templateName, "TEST", resourceResolver, 
                    "UTF-8", "HTML5", 
                    new NonCacheableTemplateResolutionValidity());
        
        return templateResolution;
    }

    public void initialize() {
        // nothing to be done;
    }
    
    
    
    
    public static class TestResourceResolver implements IResourceResolver {

        private final String result;
        
        TestResourceResolver(final String result) {
            super();
            this.result = result;
        }
        
        public String getName() {
            return "TEST EXPRESSION RESOURCE RESOLVER";
        }

        public InputStream getResourceAsStream(
                final TemplateProcessingParameters templateProcessingParameters, final String resourceName) {
            
            try {
                return new ByteArrayInputStream(this.result.getBytes("UTF-8"));
            } catch (final UnsupportedEncodingException e) {
                throw new TemplateInputException("ERROR READING TEMPLATE: " + this.result);
            }
            
        }
        
    }
    
}

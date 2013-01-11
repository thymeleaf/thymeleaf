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
package org.thymeleaf.testing.templateengine.engine.resolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.AbstractTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolutionValidity;
import org.thymeleaf.templateresolver.NonCacheableTemplateResolutionValidity;
import org.thymeleaf.testing.templateengine.engine.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.ITest;







public class TestTemplateResolver extends AbstractTemplateResolver {

    public static final String TEST_TEMPLATE_CONVERSION_CHARSET = "UTF-8";

    
    private TestResourceResolver resolver = new TestResourceResolver();
    private ITest test = null;

    
    
    public TestTemplateResolver() {
        super();
    }

    
    
    
    public void setTest(final ITest test) {
        this.test = test;
    }

    
    
    
    @Override
    protected String computeResourceName(
            final TemplateProcessingParameters templateProcessingParameters) {
        return templateProcessingParameters.getTemplateName();
    }

    
    

    @Override
    protected IResourceResolver computeResourceResolver(
            final TemplateProcessingParameters templateProcessingParameters) {

        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(this.test.getInput().getBytes(TEST_TEMPLATE_CONVERSION_CHARSET));
        } catch (final Exception e) {
            throw new TestEngineExecutionException("Exception resolving test template from in-memory String");
        }

        this.resolver.setStream(stream);
        return this.resolver;
        
    }

    
    
    
    @Override
    protected String computeCharacterEncoding(
            final TemplateProcessingParameters templateProcessingParameters) {
        return TEST_TEMPLATE_CONVERSION_CHARSET;
    }

    
    
    
    @Override
    protected String computeTemplateMode(
            final TemplateProcessingParameters templateProcessingParameters) {
        return this.test.getTemplateMode();
    }

    
    
    
    @Override
    protected ITemplateResolutionValidity computeValidity(
            final TemplateProcessingParameters templateProcessingParameters) {
        return NonCacheableTemplateResolutionValidity.INSTANCE;
    }
    
    
}

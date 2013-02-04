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
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;






public class TestResourceResolver implements IResourceResolver {

    public static final String NAME = "TEST";
    
    
    private final ITestResource resource;
    private final String characterEncoding;
    

    public TestResourceResolver(final ITestResource resource, final String characterEncoding) {
        super();
        Validate.notNull(resource, "Resource cannot be null");
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        this.resource = resource;
        this.characterEncoding = characterEncoding;
    }

    


    public String getName() {
        return NAME;
    }

    
    public ITestResource getTestResource() {
        return this.resource;
    }
    

    
    public InputStream getResourceAsStream(
            final TemplateProcessingParameters templateProcessingParameters,
            final String resourceName) {

        try {
            
            final String input = this.resource.read();
            if (input == null) {
                return null;
            }
            
            return new ByteArrayInputStream(input.getBytes(this.characterEncoding));
            
        } catch (final Exception e) {
            throw new TestEngineExecutionException("Exception resolving test template from in-memory String");
        }
        
    }
    
    
}

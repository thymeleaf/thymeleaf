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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.util.Validate;






public class TestResourceResolver implements IResourceResolver {

    public static final String NAME = "TEST";
    
    
    private final Map<String,ITestResource> resources;
    private final String characterEncoding;
    

    public TestResourceResolver(
            final Map<String,ITestResource> resources, final String characterEncoding) {
        super();
        Validate.notNull(resources, "Resources map cannot be null");
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        this.resources = Collections.unmodifiableMap(new HashMap<String,ITestResource>(resources));
        this.characterEncoding = characterEncoding;
    }

    


    public String getName() {
        return NAME;
    }

    
    public Map<String,ITestResource> getTestResources() {
        return this.resources;
    }

    
    public ITestResource getTestResource(final String resourceName) {
        Validate.notNull(resourceName, "Resource name cannot be null");
        return this.resources.get(resourceName);
    }
    

    
    public InputStream getResourceAsStream(
            final TemplateProcessingParameters templateProcessingParameters,
            final String resourceName) {

        try {
            
            final ITestResource resource = this.resources.get(resourceName);
            if (resource == null) {
                return null;
            }
            
            final String input = resource.readAsText();
            if (input == null) {
                return null;
            }
            
            return new ByteArrayInputStream(input.getBytes(this.characterEncoding));
            
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Exception resolving test resource \"" + resourceName + "\"");
        }
        
    }
    
    
}

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
package org.thymeleaf.testing.templateengine.resource;

import java.io.File;

import org.thymeleaf.util.Validate;






public final class FileTestResourceResolver implements ITestResourceResolver {

    public static final FileTestResourceResolver UTF8_RESOLVER = new FileTestResourceResolver("UTF-8");
    public static final FileTestResourceResolver ISO8859_1_RESOLVER = new FileTestResourceResolver("ISO-8859-1");
    
    private final String characterEncoding;
    
    
    
    public FileTestResourceResolver(final String characterEncoding) {
        super();
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        this.characterEncoding = characterEncoding;
    }
    
    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    
    
    public ITestResource resolve(final String resourceName) {
        
        if (resourceName == null) {
            return null;
        }
        
        final File file = new File(resourceName);
        return new FileTestResource(file, this);
    }
    
    

    public ITestResource resolveRelative(final String resourceName,
            final ITestResource relative) {
        return null;
    }

    
}

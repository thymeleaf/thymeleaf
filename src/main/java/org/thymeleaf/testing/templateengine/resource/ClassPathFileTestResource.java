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
package org.thymeleaf.testing.templateengine.resource;

import java.io.InputStream;
import java.net.URL;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.util.EscapeUtils;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;





public final class ClassPathFileTestResource 
        extends AbstractTestResource implements ITestResourceItem, IClassPathTestResource {
    
    private final String characterEncoding;
    private final URL resourceURL;

    
    public ClassPathFileTestResource(
            final String resourceName, final String characterEncoding) {
        
        super(resourceName);
        
        Validate.notNull(resourceName, "Resource name cannot be null");
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        
        this.characterEncoding = characterEncoding;
        
        final ClassLoader cl = 
                ClassLoaderUtils.getClassLoader(ClassPathFileTestResource.class);
        this.resourceURL = cl.getResource(resourceName);

        if (this.resourceURL == null) {
            throw new TestEngineExecutionException(
                    "Error while reading classpath resource \"" + resourceName + "\". " +
            		"Could not obtain resource as URL.");
        }
        
    }
    

    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public URL getResourceURL() {
        return this.resourceURL;
    }


    

    public String readAsText() {
        try {
            final InputStream is = this.resourceURL.openStream();
            final String text = ResourceUtils.read(is, this.characterEncoding);
            return EscapeUtils.unescapeUnicode(text);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Error reading class path resource: \"" + getName() + "\" from URL " +
                    "\"" + this.resourceURL + "\"", e);
        }
    }
    


    
    
}

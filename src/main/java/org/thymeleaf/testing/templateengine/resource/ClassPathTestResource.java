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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;





public final class ClassPathTestResource extends AbstractTestResource {

    private final String characterEncoding;
    
    
    public ClassPathTestResource(final String classPathResourceName, final ClassPathTestResourceResolver resolver) {
        super(classPathResourceName, resolver);
        Validate.notNull(resolver, "Test resource resolver cannot be null");
        this.characterEncoding = resolver.getCharacterEncoding();
    }
    
    
    
    public String readAsText() {

        final String classPathResourceName = getName();
        
        final ClassLoader cl = 
                ClassLoaderUtils.getClassLoader(ClassPathTestResource.class);

        BufferedReader reader = null;
        try {
            
            final InputStream is = cl.getResourceAsStream(classPathResourceName);
            
            reader = new BufferedReader(new InputStreamReader(is, this.characterEncoding));
            final StringBuilder strBuilder = new StringBuilder();
            String line = reader.readLine();
            if (line != null) {
                strBuilder.append(line);
                while ((line = reader.readLine()) != null) {
                    strBuilder.append('\n');
                    strBuilder.append(line);
                }
            }

            return strBuilder.toString();
            
        } catch (final Throwable t) {
            throw new RuntimeException( 
                    "Could not read classpath resource \"" + classPathResourceName + "\"", t);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (final Throwable ignored) {
                // ignored
            }
        }

    }
    
}

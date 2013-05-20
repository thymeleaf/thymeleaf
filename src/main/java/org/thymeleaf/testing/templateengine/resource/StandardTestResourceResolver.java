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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;






public class StandardTestResourceResolver implements ITestResourceResolver {

    public static final StandardTestResourceResolver UTF8_RESOLVER = new StandardTestResourceResolver("UTF-8");
    public static final StandardTestResourceResolver ISO8859_1_RESOLVER = new StandardTestResourceResolver("ISO-8859-1");
    
    private final String characterEncoding;
    
    
    
    public StandardTestResourceResolver(final String characterEncoding) {
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
        
        final ClassLoader cl = 
                ClassLoaderUtils.getClassLoader(ClassPathFileTestResource.class);
        final URL resourceURL = cl.getResource(resourceName);

        if (this.resourceURL == null) {
            throw new TestEngineExecutionException(
                    "Error while reading classpath resource \"" + classPathResourceName + "\". " +
                    "Could not obtain resource as URL.");
        }
        
        return new ClassPathFileTestResource(resourceName, this);

    }
    
    

    public ITestResource resolveRelative(
            final String resourceName, final ITestResource relative) {
        
        if (resourceName == null) {
            return null;
        }
        
        final String original = relative.getName();
        final List<String> originalTokens = 
                new ArrayList<String>(Arrays.asList(
                        StringUtils.split(original,"/")));
        final String[] newTokens = StringUtils.split(resourceName,"/");
        
        if (!(original.endsWith("/"))) {
            originalTokens.remove(originalTokens.size() - 1);
        }
        
        for (final String newToken : newTokens) {
            if (newToken == null || newToken.trim().equals("")) {
                continue;
            }
            if (newToken.equals("..")) {
                originalTokens.remove(originalTokens.size() - 1);
                continue;
            }
            originalTokens.add(newToken);
        }
        
        return new ClassPathFileTestResource(StringUtils.join(originalTokens,"/"), this.characterEncoding);
        
    }
    
    
    public FileTestResource resolveRelative(final String resourceName) {
        
        if (resourceName == null) {
            return null;
        }
        
        File file = (this.resourceFile.isDirectory()? this.resourceFile : this.resourceFile.getParentFile());  
        final String[] newTokens = StringUtils.split(resourceName, File.pathSeparator);
        
        for (final String newToken : newTokens) {
            if (newToken == null || newToken.trim().equals("")) {
                continue;
            }
            if (newToken.equals("..")) {
                file = file.getParentFile();
                continue;
            }
            
            originalTokens.add(newToken);
        }
        
        return new FileTestResource(StringUtils.join(originalTokens,"/"), this.characterEncoding);
        
    }

    
}

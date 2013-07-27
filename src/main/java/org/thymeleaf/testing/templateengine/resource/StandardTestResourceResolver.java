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
    
    private static final String CLASSPATH_RESOURCE_PREFIX = "classpath:";
    private static final String FILE_RESOURCE_PREFIX = "file:";
    
    private final String characterEncoding;
    
    
    
    public StandardTestResourceResolver(final String characterEncoding) {
        super();
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        this.characterEncoding = characterEncoding;
    }
    
    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    
    
    
    private boolean hasPrefix(final String resourceName) {
        if (resourceName == null) {
            return false;
        }
        return resourceName.startsWith(CLASSPATH_RESOURCE_PREFIX) ||
               resourceName.startsWith(FILE_RESOURCE_PREFIX);
    }
    
    
    
    
    public ITestResource resolve(final String resourceName) {
        
        Validate.notNull(resourceName, "Resource name cannot be null");
        
        if (!hasPrefix(resourceName)) {
            throw new IllegalArgumentException(
                    "Resource name \"" + resourceName + "\" has no recognized prefix, and it has not been declared as relative.");
        }

        if (resourceName.startsWith(CLASSPATH_RESOURCE_PREFIX)) {
            return resolveClassPathTestResource(resourceName.substring(CLASSPATH_RESOURCE_PREFIX.length()));
        }
        
        if (resourceName.startsWith(FILE_RESOURCE_PREFIX)) {
            return resolveLocalTestResource(resourceName.substring(FILE_RESOURCE_PREFIX.length()));
        }

        throw new IllegalArgumentException(
                "Resource name \"" + resourceName + "\" has no recognized prefix, and it has not been declared as relative.");
        
    }

    
    
    
    

    
    protected ITestResource resolveClassPathTestResource(final String resourceName) {
        
        Validate.notNull(resourceName, "Resource name cannot be null");
        
        final ClassLoader cl = 
                ClassLoaderUtils.getClassLoader(StandardTestResourceResolver.class);
        
        final URL resourceURL = cl.getResource(resourceName);
        if (resourceURL == null) {
            throw new TestEngineExecutionException(
                    "Error while reading classpath resource \"" + resourceName + "\". " +
                    "Could not obtain resource as URL.");
        }

        try {
            final File resourceFile = new File(resourceURL.toURI());
            if (resourceFile.isDirectory()) {
                return new LocalFolderTestResource(resourceFile, this.characterEncoding);
            }
            return new LocalFileTestResource(resourceFile, this.characterEncoding);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            return new ClassPathFileTestResource(resourceName, this.characterEncoding);
        }
        
    }

    
    
    
    
    protected ITestResource resolveLocalTestResource(final String resourceName) {
        
        Validate.notNull(resourceName, "Resource name cannot be null");
        
        try {
            final File resourceFile = new File(resourceName);
            if (resourceFile.isDirectory()) {
                return new LocalFolderTestResource(resourceFile, this.characterEncoding);
            }
            return new LocalFileTestResource(resourceFile, this.characterEncoding);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Error while reading file resource \"" + resourceName + "\".", e);
        }
        
    }
    
    

    

    

    public ITestResource resolve(
            final String resourceName, final ITestResource relativeTo) {
        
        Validate.notNull(resourceName, "Resource name cannot be null");
        
        if (hasPrefix(resourceName)) {
            // Resource name has prefix, we will not consider it relative
            return resolve(resourceName);
        }
        
        if (relativeTo == null) {
            // It has no prefix, but it no relative reference has been specified
            throw new IllegalArgumentException(
                    "Resource name \"" + resourceName + "\" has no recognized prefix, and the resource it should be relative to was specified as null.");
        }
        
        if (resourceName.startsWith(CLASSPATH_RESOURCE_PREFIX)) {
            // We consider the name to be non-relative
            return resolveClassPathTestResource(resourceName.substring(CLASSPATH_RESOURCE_PREFIX.length()));
        }
        
        if (relativeTo instanceof IClassPathTestResource) {
            final IClassPathTestResource classPathFileTestResource = (IClassPathTestResource) relativeTo;
            return resolveRelativeClassPathTestResource(resourceName, classPathFileTestResource);
        }
        
        if (relativeTo instanceof ILocalTestResource) {
            final ILocalTestResource localFileTestResource = (ILocalTestResource) relativeTo;
            return resolveRelativeLocalTestResource(resourceName, localFileTestResource);
        }
        
        throw new TestEngineExecutionException(
                "Error while resolving relative resource \"" + resourceName + "\". The resource it " +
                "should be relative to is of an unknown class: " + relativeTo.getClass().getName());
        
    }
    
    
    
    
    
    
    protected ITestResource resolveRelativeClassPathTestResource(
            final String resourceName, final IClassPathTestResource relativeTo) {
        
        final String nameRelativeTo = relativeTo.getName();
        final List<String> originalTokens = 
                new ArrayList<String>(Arrays.asList(
                        StringUtils.split(nameRelativeTo,"/")));
        final String[] newTokens = StringUtils.split(resourceName,"/");
        
        if (!(nameRelativeTo.endsWith("/"))) {
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
        
        return resolveClassPathTestResource(StringUtils.join(originalTokens,'/'));
        
    }

    
    
    
    
    protected ITestResource resolveRelativeLocalTestResource(
            final String resourceName, final ILocalTestResource relativeTo) {
        
        final File fileRelativeTo = relativeTo.getResourceFile();
        final String[] newTokens = StringUtils.split(resourceName,"/");
        
        File file = fileRelativeTo.getAbsoluteFile();
        
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        
        for (final String newToken : newTokens) {
            if (newToken == null || newToken.trim().equals("")) {
                continue;
            }
            if (newToken.equals("..")) {
                file = file.getParentFile();
                continue;
            }
            boolean resolved = false;
            for (final File containedFile : file.listFiles()) {
                if (newToken.equals(containedFile.getName())) {
                    file = containedFile;
                    resolved = true;
                    break;
                }
            }
            if (!resolved) {
                throw new TestEngineExecutionException(
                        "Error while resolving relative resource \"" + resourceName + "\" relative to " +
                		"\"" + relativeTo.getName() + "\". File does not exist.");
            }
        }
        
        if (file.isDirectory()) {
            return new LocalFolderTestResource(file, this.characterEncoding);
        }
        return new LocalFileTestResource(file, this.characterEncoding);
        
    }
    
    
    
}

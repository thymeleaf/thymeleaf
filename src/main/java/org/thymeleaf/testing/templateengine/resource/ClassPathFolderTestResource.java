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
import java.util.Collections;
import java.util.List;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;





public final class ClassPathFolderTestResource 
        extends AbstractTestResource implements ITestResourceContainer {

    private final String characterEncoding;
    private final File resourceFile;

    
    public ClassPathFolderTestResource(final String resourceName, final String characterEncoding) {
        
        super(resourceName);
        
        Validate.notNull(resourceName, "Resource name cannot be null");
        
        this.characterEncoding = characterEncoding;
        
        final ClassLoader cl = 
                ClassLoaderUtils.getClassLoader(ClassPathFolderTestResource.class);
        
        final URL resourceURL = cl.getResource(resourceName);
        if (resourceURL == null) {
            throw new TestEngineExecutionException(
                    "Error while reading classpath resource container \"" + resourceName + "\". " +
            		"Could not obtain resource as URL.");
        }
        
        try {
            this.resourceFile = new File(resourceURL.toURI());
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Error while reading classpath resource container \"" + resourceName + "\" as " +
                    "URL \"" + resourceURL + "\". This can happen when resource is a folder but it is " +
                    "contained in a .jar file. Folders can only be read from classpath when uncompressed in the " +
                    "file system.", e);
        }
        
        if (!this.resourceFile.isDirectory()) {
            throw new TestEngineExecutionException(
                    "Error while reading classpath resource container \"" + resourceName + "\" as " +
                    "URL \"" + resourceURL + "\". Resource is NOT a folder.");
        }
        
    }



    
    
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public File getResourceFile() {
        return this.resourceFile;
    }




    public List<ITestResource> getContainedResources() {
        
        final List<ITestResource> containedResources = new ArrayList<ITestResource>();
        final File[] fileList = this.resourceFile.listFiles();
        for (final File containedFile : fileList) {
            final ITestResource containedResource =
                    (containedFile.isDirectory()?
                            new FolderTestResource(containedFile, this.characterEncoding) :
                            new FileTestResource(containedFile, this.characterEncoding)); 
            containedResources.add(containedResource);
        }
        return Collections.unmodifiableList(containedResources);

    }
    

    
    
}

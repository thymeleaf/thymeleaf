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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;





public class FolderTestResource 
        extends AbstractTestResource implements ITestResourceContainer {
    
    
    private final File resourceFile;
    private final String characterEncoding;


    
    public FolderTestResource(final File file, final String characterEncoding) {
        
        super(validateFile(file));
        
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        
        this.resourceFile = file;
        this.characterEncoding = characterEncoding;
        
        if (!this.resourceFile.isDirectory()) {
            throw new TestEngineExecutionException(
                    "Error while reading folder resource container \"" + this.resourceFile.getName() + "\". " +
            		"Resource is NOT a folder.");
        }
        
    }

    
    
    private static String validateFile(final File file) {
        Validate.notNull(file, "Resource file cannot be null");
        return file.getName();
    }


    

    
    public File getResourceFile() {
        return this.resourceFile;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
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

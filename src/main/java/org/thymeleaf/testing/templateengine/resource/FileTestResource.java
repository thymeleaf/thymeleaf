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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.util.Validate;





public class FileTestResource extends AbstractTestResource {
    
    
    private final File file;
    private final String characterEncoding;
    
    

    
    public static ITestResource resolve(final String resourceName, final String characterEncoding) {
        if (resourceName == null) {
            return null;
        }
        final File file = new File(resourceName);
        return new FileTestResource(file, characterEncoding);
    }
    

    
    
    public FileTestResource(final File file, final String characterEncoding) {
        super(validateFile(file));
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        this.file = file;
        this.characterEncoding = characterEncoding;
    }
    
    
    private static String validateFile(final File file) {
        Validate.notNull(file, "Resource file cannot be null");
        return file.getName();
    }

    
    
    public String readAsText() {

        synchronized (this.file) {
            BufferedReader reader = null;
            try {
                
                final FileInputStream is = new FileInputStream(this.file);
                
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
                        "Could not read resource file \"" + this.file.getAbsolutePath() + "\"", t);
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


    
    public boolean isContainer() {
        return this.file.isDirectory();
    }
    
    

    public List<ITestResource> getContainedResources() {
        if (this.file.isDirectory()) {
            final List<ITestResource> containedResources = new ArrayList<ITestResource>();
            final File[] fileList = this.file.listFiles();
            for (final File containedFile : fileList) {
                containedResources.add(new FileTestResource(containedFile, this.characterEncoding));
            }
            return Collections.unmodifiableList(containedResources);
        }
        return Collections.emptyList();
    }
    
    
    
    public ITestResource resolveRelative(final String resourceName) {
        if (resourceName == null) {
            return null;
        }
        final File file = new File(resourceName);
        return new FileTestResource(file, this.characterEncoding);
    }

    
}

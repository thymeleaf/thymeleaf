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
package org.thymeleaf.testing.templateengine.standard.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.testing.templateengine.builder.ITestableBuilder;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.util.Validate;





public class LocalFolderStandardTestSequenceBuilder extends AbstractStandardTestSequenceBuilder {
    
    private final File folder;
    private final String fileNameSuffix;
    
    
    @SuppressWarnings("null")
    public LocalFolderStandardTestSequenceBuilder(final File folder, final String fileNameSuffix) {
        super((folder != null? folder.getName() : null));
        Validate.notNull(folder, "Folder cannot be null");
        Validate.isTrue(folder.exists(), "Specified file \"" + folder.getAbsolutePath() + "\" does not exist");
        Validate.isTrue(folder.isDirectory(), "Specified file \"" + folder.getAbsolutePath() + "\" is not a folder");
        this.folder = folder;
        this.fileNameSuffix = fileNameSuffix;
    }

    
    public File getFolder() {
        return this.folder;
    }
    
    public String getFileNameSuffix() {
        return this.fileNameSuffix;
    }
    
    
    @Override
    protected final List<ITestable> getSequenceContent(final String executionId) {
        
        if (!this.folder.isDirectory()) {
            return Collections.emptyList();
        }
        
        final List<ITestable> testables = new ArrayList<ITestable>();
        for (final File fileInFolder : this.folder.listFiles()) {
            if (fileInFolder.isDirectory()) {
                final ITestableBuilder builder = createBuilderForFolder(fileInFolder);
                if (builder != null) {
                    testables.add(builder.build(executionId));
                }
                continue;
            }
            if (this.fileNameSuffix == null || fileInFolder.getName().endsWith(this.fileNameSuffix)) {
                final ITestableBuilder builder = createBuilderForFile(fileInFolder);
                if (builder != null) {
                    testables.add(builder.build(executionId));
                }
            }
        }
        return testables;
    }
    
    
    
    protected ITestableBuilder createBuilderForFile(final File file) {
        return new FileStandardTestBuilder(file);
    }
    
    
    protected ITestableBuilder createBuilderForFolder(final File file) {
        return new LocalFolderStandardTestSequenceBuilder(file, getFileNameSuffix());
    }

    
    
}

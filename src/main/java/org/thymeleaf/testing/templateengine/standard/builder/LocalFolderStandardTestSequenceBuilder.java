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

import org.thymeleaf.testing.templateengine.builder.ITestBuilder;
import org.thymeleaf.util.Validate;





public class LocalFolderStandardTestSequenceBuilder extends AbstractStandardTestSequenceBuilder {
    
    private final File folder;
    private final boolean recursive;
    private final String fileNameSuffix;
    
    
    public LocalFolderStandardTestSequenceBuilder(final File folder, final boolean recursive, final String fileNameSuffix) {
        super();
        Validate.notNull(folder, "Folder cannot be null");
        Validate.isTrue(folder.exists(), "Specified file \"" + folder.getAbsolutePath() + "\" does not exist");
        Validate.isTrue(folder.isDirectory(), "Specified file \"" + folder.getAbsolutePath() + "\" is not a folder");
        this.folder = folder;
        this.recursive = recursive;
        this.fileNameSuffix = fileNameSuffix;
    }

    
    
    @Override
    protected final List<ITestBuilder> getTestBuilders(final String executionId) {

        final List<File> files = getFilesInFolder(this.folder, this.recursive, this.fileNameSuffix);
        final List<ITestBuilder> builders = new ArrayList<ITestBuilder>(); 
        for (final File file : files) {
            builders.add(createBuilderForFile(executionId, file));
        }
        return builders;
        
    }
    

    
    private static List<File> getFilesInFolder(final File folder, final boolean recursive, final String fileNameSuffix) {
        if (!folder.isDirectory()) {
            return Collections.emptyList();
        }
        final List<File> files = new ArrayList<File>();
        for (final File fileInFolder : folder.listFiles()) {
            if (fileInFolder.isDirectory()) {
                if (recursive) {
                    files.addAll(getFilesInFolder(fileInFolder, recursive, fileNameSuffix));
                }
                continue;
            }
            if (fileNameSuffix == null || fileInFolder.getName().endsWith(fileNameSuffix)) { 
                files.add(fileInFolder);
            }
        }
        return files;
    }
    
    
    
    @SuppressWarnings("unused")
    protected ITestBuilder createBuilderForFile(final String executionId, final File file) {
        return new FileStandardTestBuilder(file);
    }

    
    
}

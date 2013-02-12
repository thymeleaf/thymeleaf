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

import org.thymeleaf.testing.templateengine.builder.ITestSequenceBuilder;
import org.thymeleaf.util.Validate;





public class LocalFolderStandardTestSuiteBuilder extends AbstractStandardTestSuiteBuilder {

    
    private final File folder;
    private final String fileNameSuffix;
    
    
    public LocalFolderStandardTestSuiteBuilder(final String suiteName, final File folder, final String fileNameSuffix) {
        super(suiteName);
        Validate.notNull(suiteName, "SuiteName cannot be null");
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
    protected ITestSequenceBuilder getSequenceBuilder(final String executionId) {
        return new LocalFolderStandardTestSequenceBuilder(this.folder, this.fileNameSuffix);
    }

    
    
}

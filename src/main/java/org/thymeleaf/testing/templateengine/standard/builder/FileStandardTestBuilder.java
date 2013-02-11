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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;





public class FileStandardTestBuilder extends AbstractStandardTestBuilder {
    
    private final File file;
    
    public FileStandardTestBuilder(final File file) {
        super();
        Validate.notNull(file, "File cannot be null");
        this.file = file;
    }
    


    @Override
    protected String getDocumentName(final String executionId) {
        return this.file.getName();
    }

    
    @Override
    protected Reader getDocumentReader(final String executionId) {
        try {
            return new FileReader(this.file);
        } catch (final FileNotFoundException e) {
            throw new TestEngineExecutionException( 
                    executionId, "Test file \"" + this.file.getAbsolutePath() + "\" does not exist");
        }
    }
    
}

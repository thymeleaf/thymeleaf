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

import java.io.Reader;
import java.io.StringReader;

import org.thymeleaf.util.Validate;





public class StringStandardTestBuilder extends AbstractStandardTestBuilder {
    
    private final String documentName;
    private final String content;
    
    
    public StringStandardTestBuilder(final String documentName, final String content) {
        super();
        Validate.notNull(documentName, "Document name cannot be null");
        Validate.notNull(content, "Content cannot be null");
        this.documentName = documentName;
        this.content = content;
    }
    


    @Override
    protected String getDocumentName(final String executionId) {
        return this.documentName;
    }

    
    @Override
    protected Reader getDocumentReader(final String executionId) {
        return new StringReader(this.content);
    }
    
}

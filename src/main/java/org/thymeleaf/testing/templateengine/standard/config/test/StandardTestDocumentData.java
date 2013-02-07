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
package org.thymeleaf.testing.templateengine.standard.config.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.util.Validate;




public class StandardTestDocumentData {

    private final String documentName;
    private final Map<String,String> directiveValues;
    
    
    public StandardTestDocumentData(
            final String documentName,
            final Map<String,String> directiveValues) {
        super();
        Validate.notNull(documentName, "Document name cannot be null");
        Validate.notNull(directiveValues, "Directive values map cannot be null");
        this.documentName = documentName;
        this.directiveValues = Collections.unmodifiableMap(new HashMap<String,String>(directiveValues));
    }

    
    public String getDocumentName() {
        return this.documentName;
    }
    
    public Map<String,String> getAllDirectiveValues() {
        return this.directiveValues;
    }
    
    public String getDirectiveValue(final String directiveName) {
        return this.directiveValues.get(directiveName);
    }
    
}

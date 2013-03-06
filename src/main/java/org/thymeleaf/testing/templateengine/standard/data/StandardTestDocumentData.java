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
package org.thymeleaf.testing.templateengine.standard.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.directive.StandardTestDirectiveUtils;
import org.thymeleaf.util.Validate;




public class StandardTestDocumentData {

    private final String executionId;
    private final String documentName;
    private final Map<String,Map<String,String>> directivesByName;
    
    
    public StandardTestDocumentData(final String executionId, final String documentName) {
        super();
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(documentName, "Document name cannot be null");
        this.executionId = executionId;
        this.documentName = documentName;
        this.directivesByName = new HashMap<String, Map<String,String>>();
    }

    
    public String getExecutionId() {
        return this.executionId;
    }
    
    public String getDocumentName() {
        return this.documentName;
    }
    
    
    public Set<String> getAllDirectiveNames() {
        return this.directivesByName.keySet();
    }
    
    
    
    public Set<String> getAllDirectiveQualifiersForName(final String directiveName) {
        
        final Map<String,String> directivesForNameByQualifier = 
                getDirectivesForNameByQualifier(directiveName);

        if (directivesForNameByQualifier == null) {
            return Collections.emptySet();
        }
        
        return directivesForNameByQualifier.keySet();
        
    }
    
    
    
    public String getDirectiveValue(final String directiveName, final String directiveQualifier) {
        
        final Map<String,String> directivesForNameByQualifier = 
                getDirectivesForNameByQualifier(directiveName);

        if (directivesForNameByQualifier == null) {
            return null;
        }
        
        return directivesForNameByQualifier.get(directiveQualifier);
        
    }
    

    
    public Map<String,String> getDirectivesForNameByQualifier(final String directiveName) {
        
        Validate.notNull(directiveName, "Directive name cannot be null");
        
        String targetDirectiveName = directiveName;
        if (StandardTestDirectiveUtils.hasQualifier(targetDirectiveName)) {
            targetDirectiveName = StandardTestDirectiveUtils.extractDirectiveName(targetDirectiveName);
        }
        
        final Map<String,String> directivesForNameByQualifier = this.directivesByName.get(targetDirectiveName);

        if (directivesForNameByQualifier == null) {
            return Collections.emptyMap();
        }
        
        return Collections.unmodifiableMap(directivesForNameByQualifier);
        
    }


    
    public void setDirectiveValue(final String directive, final String directiveValue) {
        
        final String directiveName = StandardTestDirectiveUtils.extractDirectiveName(directive);
        final String directiveQualifier = StandardTestDirectiveUtils.extractDirectiveQualifier(directive);
        
        if (directiveName == null) {
            throw new TestEngineExecutionException(
                    this.executionId, "Invalid directive name \"" + directive +"\" " +
                                "specified for document \"" + this.documentName + "\"");
        }
            
        Map<String,String> directivesForNameByQualifier = this.directivesByName.get(directiveName);
        if (directivesForNameByQualifier == null) {
            directivesForNameByQualifier = new HashMap<String,String>();
            this.directivesByName.put(directiveName, directivesForNameByQualifier);
        }
        directivesForNameByQualifier.put(directiveQualifier, directiveValue);
        
    }
    
    
}

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
import java.util.Set;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.util.DirectiveUtils;
import org.thymeleaf.util.Validate;




public class StandardTestDocumentData {

    private final String executionId;
    private final String documentName;
    private final Map<String,Map<String,String>> directivesByName;
    
    
    public StandardTestDocumentData(
            final String executionId, final String documentName,
            final Map<String,String> directiveValues) {
        super();
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(documentName, "Document name cannot be null");
        Validate.notNull(directiveValues, "Directive values map cannot be null");
        this.executionId = executionId;
        this.documentName = documentName;
        this.directivesByName = groupDirectivesByName(executionId, documentName, directiveValues);
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
        if (DirectiveUtils.hasQualifier(targetDirectiveName)) {
            targetDirectiveName = DirectiveUtils.extractDirectiveName(targetDirectiveName);
        }
        
        final Map<String,String> directivesForNameByQualifier = this.directivesByName.get(targetDirectiveName);

        if (directivesForNameByQualifier == null) {
            return Collections.emptyMap();
        }
        
        return Collections.unmodifiableMap(directivesForNameByQualifier);
        
    }

    
    
    private static Map<String,Map<String,String>> groupDirectivesByName(
            final String executionId, final String documentName, 
            final Map<String,String> directiveValues) {
        
        final Map<String,Map<String,String>> directivesByName = new HashMap<String,Map<String,String>>();
        
        for (final Map.Entry<String,String> directivesValuesEntry : directiveValues.entrySet()) {
            
            final String directive = directivesValuesEntry.getKey();
            final String directiveValue = directivesValuesEntry.getValue();
            
            final String directiveName = DirectiveUtils.extractDirectiveName(directive);
            final String directiveQualifier = DirectiveUtils.extractDirectiveQualifier(directive);
            
            if (directiveName == null) {
                throw new TestEngineExecutionException(
                        executionId, "Invalid directive name \"" + directive +"\" " +
                                    "found in document \"" + documentName + "\"");
            }
            
            Map<String,String> directivesForNameByQualifier = directivesByName.get(directiveName);
            if (directivesForNameByQualifier == null) {
                directivesForNameByQualifier = new HashMap<String,String>();
                directivesByName.put(directiveName, directivesForNameByQualifier);
            }
            directivesForNameByQualifier.put(directiveQualifier, directiveValue);
            
        }

        return directivesByName;
        
    }
    
    
}

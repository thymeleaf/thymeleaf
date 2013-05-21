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
package org.thymeleaf.testing.templateengine.standard.test.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceItem;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestRawData;
import org.thymeleaf.util.Validate;






public class StandardTestReader implements IStandardTestReader {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char FIELD_PREFIX_CHAR = '%';
    

    public static final Pattern FIELD_DEFINITION_PATTERN =
            Pattern.compile("([\\p{Alnum}_-]*)(\\[(\\S*)\\])?");
    public static final int FIELD_NAME_GROUP = 1;
    public static final int FIELD_QUALIFIER_GROUP = 3;


    
    
    
    public StandardTestReader() {
        super();
    }
    
    
    
    
    
    public StandardTestRawData readTestResource(
            final String executionId, final ITestResource resource) 
            throws IOException {

        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(resource, "Resource cannot be null");
 
        if (!(resource instanceof ITestResourceItem)) {
            throw new TestEngineExecutionException(
                    "Document resource specified for test \"" + resource.getName() + "\" which is a container, not an item " +
                    "(maybe a folder?)");
        }
        
        final String resourceContents = ((ITestResourceItem)resource).readAsText();
        final BufferedReader r = new BufferedReader(new StringReader(resourceContents));

        final StandardTestRawData data = new StandardTestRawData(resource);
        
        try {
            
            String currentFieldName = null;
            String currentFieldQualifier = null;
            StringBuilder strBuilder = null;
            
            String line = r.readLine();
            while (line != null) {
                
                if (isCommentLine(line)) {
                    
                    line = r.readLine();
                    continue;
                    
                }
                    
                final String newFieldDefinition = extractFieldDefinition(line);
                if (newFieldDefinition == null) {
                    // This is not a new field, it is content belonging to the previously-defined field
                    if (currentFieldName == null) {
                        line = r.readLine();
                        continue;
                    }
                    if (strBuilder != null) {
                        if (strBuilder.length() > 0) {
                            strBuilder.append('\n');
                        }
                        strBuilder.append(line);
                    }
                    line = r.readLine();
                    continue;
                }
                
                if (currentFieldName != null && strBuilder != null) {
                    data.setValue(currentFieldName, currentFieldQualifier, strBuilder.toString());
                }
                
                final int lineLen = line.length();
                currentFieldName = extractFieldName(newFieldDefinition);
                currentFieldQualifier = extractFieldQualifier(newFieldDefinition);
                
                strBuilder = new StringBuilder();
                if ((newFieldDefinition.length() + 1) < (lineLen - 1)) {
                    int valueStart = newFieldDefinition.length() + 1;
                    while (valueStart < lineLen && Character.isWhitespace(line.charAt(valueStart))) { 
                        valueStart++; 
                    }
                    if (valueStart < (lineLen - 1)) {
                        strBuilder.append(line.substring(valueStart));
                    }
                }
                
                line = r.readLine();
                
            }

            if (currentFieldName != null && strBuilder != null) {
                data.setValue(currentFieldName, currentFieldQualifier, strBuilder.toString());
            }
            
        } finally {
            
            try {
                r.close();
            } catch (final Throwable t) {
                // ignored
            }
            
        }
        
        return data;
        
    }

    
    
    protected boolean isCommentLine(final String line) {
        return (line.length() > 0 && line.charAt(0) == COMMENT_PREFIX_CHAR);
    }
    
    
    private static String extractFieldDefinition(final String line) {
    
        final int lineLen = line.length();
        if (lineLen <= 0) {
            return null;
        }
        
        char c = line.charAt(0);
        if (c != FIELD_PREFIX_CHAR) {
            return null;
        }
        
        int i = 1;
        while (i < lineLen) {
            c = line.charAt(i);
            if (Character.isWhitespace(c)) {
                break;
            }
            i++; 
        }
    
        final String tentativeFieldDefinition = line.substring(1, i);
        
        return (isFieldDefinition(tentativeFieldDefinition)) ? tentativeFieldDefinition : null;
        
    }
    
    
    

    private static boolean isFieldDefinition(final String name) {
        if (name == null) {
            return false;
        }
        final Matcher m = FIELD_DEFINITION_PATTERN.matcher(name);
        return m.matches();
    }
    

    
    private static String extractFieldName(final String fieldDefinition) {
        if (fieldDefinition == null) {
            return null;
        }
        final Matcher m = FIELD_DEFINITION_PATTERN.matcher(fieldDefinition);
        if (!m.matches()) {
            return null;
        }
        final String name = m.group(FIELD_NAME_GROUP);
        if (name == null || name.trim().equals("")) {
            return null;
        }
        return name.trim();
    }
    

    
    private static String extractFieldQualifier(final String fieldDefinition) {
        if (fieldDefinition == null) {
            return null;
        }
        final Matcher m = FIELD_DEFINITION_PATTERN.matcher(fieldDefinition);
        if (!m.matches()) {
            return null;
        }
        final String qualifier = m.group(FIELD_QUALIFIER_GROUP);
        if (qualifier == null || qualifier.trim().equals("")) {
            return null;
        }
        return qualifier.trim();
    }
    

    
    
}

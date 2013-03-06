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
package org.thymeleaf.testing.templateengine.standard.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.FileTestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.data.StandardTestDocumentData;
import org.thymeleaf.testing.templateengine.standard.directive.StandardTestDirectiveSpec;
import org.thymeleaf.util.Validate;






public final class StandardTestReaderUtils {

    
    
    public static void readDocument(final Reader documentReader, 
            final StandardTestDocumentData data) 
            throws IOException {

        Validate.notNull(documentReader, "Document reader cannot be null");
        Validate.notNull(data, "Data object cannot be null");
        
        final BufferedReader r = new BufferedReader(documentReader);
        
        try {
            
            String currentDirectiveName = null;
            StringBuilder strBuilder = null;
            
            String line = r.readLine();
            while (line != null) {
                
                if (isComment(line)) {
                    
                    line = r.readLine();
                    continue;
                    
                }
                    
                final int directiveEnd = identifyDirective(line);
                if (directiveEnd == -1) {
                    // This is not a directive, it is content belonging to the previously
                    // defined directive
                    if (currentDirectiveName == null) {
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
                
                if (currentDirectiveName != null && strBuilder != null) {
                    data.setDirectiveValue(currentDirectiveName, strBuilder.toString());
                }
                
                final int lineLen = line.length();
                currentDirectiveName = line.substring(1, directiveEnd);
                
                strBuilder = new StringBuilder();
                if (directiveEnd < (lineLen - 1)) {
                    int valueStart = directiveEnd;
                    while (valueStart < lineLen && Character.isWhitespace(line.charAt(valueStart))) { 
                        valueStart++; 
                    }
                    if (valueStart < (lineLen - 1)) {
                        strBuilder.append(line.substring(valueStart));
                    }
                }
                
                line = r.readLine();
                
            }

            if (currentDirectiveName != null && strBuilder != null) {
                data.setDirectiveValue(currentDirectiveName, strBuilder.toString());
            }
            
        } finally {
            
            try {
                r.close();
            } catch (final Throwable t) {
                // ignored
            }
            
        }
        
    }

    
    
    private static boolean isComment(final String line) {
        return (line.length() > 0 && line.charAt(0) == StandardTestDirectiveSpec.COMMENT_PREFIX_CHAR);
    }
    
    
    private static int identifyDirective(final String line) {
    
        final int lineLen = line.length();
        if (lineLen <= 0) {
            return -1;
        }
        
        char c = line.charAt(0);
        if (c != StandardTestDirectiveSpec.DIRECTIVE_PREFIX_CHAR) {
            return -1;
        }
        
        int i = 1;
        while (i < lineLen) {
            c = line.charAt(i);
            if (Character.isWhitespace(c)) {
                break;
            }
            i++; 
        }
    
        return i;
        
    }
    

    
    
    
    public static ITestResource createResource(final String executionId, final String fileSuffix, final String contents) {

        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(fileSuffix, "File suffix cannot be null");
        Validate.notNull(contents, "Contents cannot be null");
        
        try {

            final String prefix = 
                    "thymeleaf-testing" + 
                    (executionId != null? ("-" + executionId) : "") + 
                    (fileSuffix != null? ("-" + fileSuffix) : "") + "-";
            
            final File tempFile = File.createTempFile(prefix, null);
            tempFile.deleteOnExit();
            
            FileWriter writer = null;
            try {
                writer = new FileWriter(tempFile, false);
                writer.write(contents);
            } catch (final Throwable t) {
                throw new TestEngineExecutionException(executionId, 
                        "Could not write contents of temporary file for execution \"" + executionId + "\"", t);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (final Throwable ignored) {
                    // ignored
                }
            }
            
            return new FileTestResource(tempFile);
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(executionId, 
                    "Could not create temporary file for execution \"" + executionId + "\"", t);
        }
        
    }
    
    
    
    
    
    private StandardTestReaderUtils() {
        super();
    }

    
}

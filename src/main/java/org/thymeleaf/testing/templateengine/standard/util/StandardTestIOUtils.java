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
package org.thymeleaf.testing.templateengine.standard.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

import org.thymeleaf.testing.templateengine.exception.TestEngineConfigurationException;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestFileDirectives;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestFileData;
import org.thymeleaf.testing.templateengine.test.resource.FileTestResource;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;






public final class StandardTestIOUtils {

    
    
    public static StandardTestFileData readTestFile(final String executionId, 
            final Reader reader, final Set<String> fields) {

        Validate.notNull(reader, "Reader cannot be null");
        Validate.notNull(fields, "Fields set cannot be null");
        
        final BufferedReader r = new BufferedReader(reader);
        final HashMap<String,String> data = new HashMap<String, String>();
        
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
                    data.put(currentDirectiveName, strBuilder.toString());
                }
                
                final int lineLen = line.length();
                currentDirectiveName = line.substring(1, directiveEnd);
                
                if (fields.contains(currentDirectiveName)) {
                    
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
                    
                } else {
                    
                    strBuilder = null;
                    
                }
                
                line = r.readLine();
                
            }

            if (currentDirectiveName != null && strBuilder != null) {
                data.put(currentDirectiveName, strBuilder.toString());
            }

            return new StandardTestFileData(data);
            
        } catch (final IOException e) {
            
            throw new TestEngineExecutionException(executionId, e);
            
        } finally {
            
            try {
                r.close();
            } catch (final Throwable t) {
                // ignored
            }
            
        }
        
    }

    
    
    private static boolean isComment(final String line) {
        return (line.length() > 0 && line.charAt(0) == StandardTestFileDirectives.COMMENT_PREFIX_CHAR);
    }
    
    
    private static int identifyDirective(final String line) {
    
        final int lineLen = line.length();
        if (lineLen <= 0) {
            return -1;
        }
        
        char c = line.charAt(0);
        if (c != StandardTestFileDirectives.DIRECTIVE_PREFIX_CHAR) {
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
    

    
    
    
    public static ITestResource createResource(final String suiteName, final String fileIdentifier, final String contents) {
        
        try {

            final String prefix = 
                    "thymeleaf-testing-" + 
                    (suiteName != null? ("-" + suiteName) : "") + 
                    (fileIdentifier != null? ("-" + fileIdentifier) : "") + "-";
            
            final File tempFile = File.createTempFile(prefix, null);
            tempFile.deleteOnExit();
            
            FileWriter writer = null;
            try {
                writer = new FileWriter(tempFile, false);
                writer.write(contents);
            } catch (final Throwable t) {
                throw new TestEngineConfigurationException(suiteName, 
                        "Could not write contents of temporary file for suite \"" + suiteName + "\"", t);
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
            
        } catch (final TestEngineConfigurationException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineConfigurationException(suiteName, 
                    "Could not create temporary file for suite \"" + suiteName + "\"", t);
        }
        
    }
    
    
    
    
    
    private StandardTestIOUtils() {
        super();
    }

    
}

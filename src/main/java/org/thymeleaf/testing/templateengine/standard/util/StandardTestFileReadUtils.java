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
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

import org.thymeleaf.testing.templateengine.engine.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestFileDirectives;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestFileData;
import org.thymeleaf.util.Validate;






public final class StandardTestFileReadUtils {

    
    
    public static StandardTestFileData readTestFile(final Reader reader, final Set<String> fields) {

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
            
            throw new TestEngineExecutionException(e);
            
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
    

    
    
    private StandardTestFileReadUtils() {
        super();
    }
    
    
    
}

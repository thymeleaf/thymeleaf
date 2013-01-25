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
package org.thymeleaf.testing.templateengine.standard.testfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.engine.TestEngineExecutionException;






public class StandardTestFileReader {

    
    
    
    public StandardTestFileReader() {
        super();
    }
    
    
    
    public Map<String,String> readTestFile(final Reader reader) {
        
        
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
                data.put(currentDirectiveName, strBuilder.toString());
            }

            return data;
            
        } catch (final IOException e) {
            throw new TestEngineExecutionException(e);
        }
        
    }

    
    
    protected boolean isComment(final String line) {
        return (line.length() > 0 && line.charAt(0) == StandardTestFileNaming.COMMENT_PREFIX_CHAR);
    }
    
    
    private int identifyDirective(final String line) {
    
        final int lineLen = line.length();
        if (lineLen <= 0) {
            return -1;
        }
        
        char c = line.charAt(0);
        if (c != StandardTestFileNaming.DIRECTIVE_PREFIX_CHAR) {
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
    

    
    
    public static void main(final String[] args) {
        
        final StandardTestFileReader r = new StandardTestFileReader();
        
        final String test01 = 
                "%TEST lala\n" +
                "%TEE  \n" +
                "%TUS   lele\n" +
                "looolo\n" +
                "%TUDD\n" +
                "looolo\n" +
                "looolo\n" +
                "%TUD22    \n" +
                "looolo\n" +
                "%OOOOP\n" +
                " lsd aasdasd";
        
        final Reader test01Reader = new StringReader(test01);
        
        
        
        System.out.println(r.readTestFile(test01Reader));
        
    }
    
    
}

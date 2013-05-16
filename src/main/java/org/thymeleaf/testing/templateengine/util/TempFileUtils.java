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
package org.thymeleaf.testing.templateengine.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;






public final class TempFileUtils {

    
    
    public static File createTempFile(final String prefix, 
            final String suffix, final String contents, final String characterEncoding) {

        Validate.notNull(contents, "Contents cannot be null");
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        
        try {

            final String totalPrefix = "thymeleaf-testing" + (prefix != null? "-" + prefix : "");
            final File tempFile = File.createTempFile(totalPrefix, suffix);
            tempFile.deleteOnExit();
            
            Writer writer = null;
            try {
                final FileOutputStream os = new FileOutputStream(tempFile, false);
                writer = new OutputStreamWriter(os, characterEncoding);
                writer.write(contents);
            } catch (final Throwable t) {
                throw new TestEngineExecutionException( 
                        "Could not write contents of temporary file \"" + tempFile.getAbsolutePath() + "\"", t);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (final Throwable ignored) {
                    // ignored
                }
            }
            
            return tempFile;
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException( 
                    "Could not create temporary file", t);
        }
        
    }
    
    
    
    
    private TempFileUtils() {
        super();
    }
    
    
    
}

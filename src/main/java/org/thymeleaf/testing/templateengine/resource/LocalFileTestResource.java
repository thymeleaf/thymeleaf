/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.util.EscapeUtils;
import org.thymeleaf.testing.templateengine.util.ResourceUtils;
import org.thymeleaf.util.Validate;





public class LocalFileTestResource 
        extends AbstractTestResource implements ITestResourceItem, ILocalTestResource {

    private final File resourceFile;
    private final String characterEncoding;
    private final boolean avoidUnicodeUnescapeOnRead;

    
    public LocalFileTestResource(final File file, final String characterEncoding) {
        this(file, characterEncoding, false);
    }


    public LocalFileTestResource(final File file, final String characterEncoding, final boolean avoidUnicodeUnescapeOnRead) {
        super(validateFile(file));
        Validate.notNull(characterEncoding, "Character encoding cannot be null");
        this.resourceFile = file.getAbsoluteFile();
        this.characterEncoding = characterEncoding;
        this.avoidUnicodeUnescapeOnRead = avoidUnicodeUnescapeOnRead;
    }

    
    private static String validateFile(final File file) {
        Validate.notNull(file, "Resource file cannot be null");
        return file.getAbsolutePath();
    }

    
    
    
    
    public File getResourceFile() {
        return this.resourceFile;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    
    

    public String readAsText() {
        try {
            final InputStream is = new FileInputStream(this.resourceFile);
            final String text = ResourceUtils.read(is, this.characterEncoding);
            return this.avoidUnicodeUnescapeOnRead? text : EscapeUtils.unescapeUnicode(text);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(
                    "Error reading file resource: \"" + getName() + "\"", e);
        }
    }

    
    
    
}

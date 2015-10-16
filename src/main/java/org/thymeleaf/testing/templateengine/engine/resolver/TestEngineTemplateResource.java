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
package org.thymeleaf.testing.templateengine.engine.resolver;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceItem;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;


public class TestEngineTemplateResource implements ITemplateResource {


    private final ITestResource resource;
    private final String characterEncoding;




    public TestEngineTemplateResource(final ITestResource resource, final String characterEncoding) {

        super();

        Validate.notNull(resource, "Resource cannot be null");
        // Character encoding CAN be null (system default will be used)

        this.resource = resource;
        this.characterEncoding = characterEncoding;

    }

    

    public String getDescription() {
        return this.resource.getName();
    }


    public String getBaseName() {
        // This operation is not allowed in test resource (only needed for StandardMessageResolver anyway)
        return null;
    }


    public boolean exists() {
        return true;
    }


    public Reader reader() throws IOException {

        if (!(this.resource instanceof ITestResourceItem)) {
            throw new TestEngineExecutionException(
                    "Test specifies an input \"" + this.resource.getName() + "\" which is a container, not an item " +
                            "(maybe a folder?)");
        }

        final String input = ((ITestResourceItem)this.resource).readAsText();
        if (input == null) {
            throw new FileNotFoundException(String.format("Test resource \"%s\" does not exist", this.resource.getName()));
        }

        final InputStream inputStream = new ByteArrayInputStream(input.getBytes(this.characterEncoding));

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new InputStreamReader(inputStream, this.characterEncoding);
        }

        return new InputStreamReader(inputStream);

    }


    public ITemplateResource relative(final String relativePath) throws IOException {
        throw new IOException(String.format("Cannot create relative resource for test resource \"%s\"", this.resource.getName()));
    }


}

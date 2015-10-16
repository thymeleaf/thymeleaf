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
package org.thymeleaf.templateresource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StringTemplateResource implements ITemplateResource {


    private final String name;
    private final String resource;




    public StringTemplateResource(final String resource) {
        this(null, resource);
    }


    public StringTemplateResource(final String name, final String resource) {

        super();

        // name can be null (resource will be used as name)
        Validate.notNull(resource, "Resource cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.name = (name == null? resource : name);
        this.resource = resource;

    }




    public String getDescription() {
        return this.name;
    }




    public String getBaseName() {
        // This kind of resource cannot be used for computing derivative names from its base
        return null;
    }




    public Reader reader() throws IOException {
        return new StringReader(this.resource);
    }




    public ITemplateResource relative(final String relativePath) throws IOException {
        throw new FileNotFoundException(
                String.format("Cannot create a relative resource for String resource  \"%s\"", this.name));
    }




    public boolean exists() {
        return true;
    }





}

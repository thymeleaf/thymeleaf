/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Implementation of {@link ITemplateResource} that represents a template completely contained in memory inside
 *   a {@code String} object.
 * </p>
 * <p>
 *   Objects of this class are usually created by {@link org.thymeleaf.templateresolver.StringTemplateResolver}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class StringTemplateResource implements ITemplateResource {


    private final String resource;




    public StringTemplateResource(final String resource) {
        super();
        Validate.notNull(resource, "Resource cannot be null or empty");
        this.resource = resource;
    }




    public String getDescription() {
        return this.resource;
    }




    public String getBaseName() {
        // This kind of resource cannot be used for computing derivative names from its base
        return null;
    }




    public Reader reader() throws IOException {
        return new StringReader(this.resource);
    }




    public ITemplateResource relative(final String relativeLocation) {
        throw new TemplateInputException(
                String.format("Cannot create a relative resource for String resource  \"%s\"", this.resource));
    }




    public boolean exists() {
        return true;
    }





}

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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebApplication;

/**
 * <p>
 *   Implementation of {@link ITemplateResource} accessible from the context of a web application.
 *   The paths of these resources start at the web application file root which, for instance, in
 *   servlet-based applications is {@code /WEB-INF}.
 * </p>
 * <p>
 *   Objects of this class are usually created by {@link WebApplicationTemplateResolver}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.1.0
 *
 */
public final class WebApplicationTemplateResource implements ITemplateResource {


    private final IWebApplication webApplication;
    private final String path;
    private final String characterEncoding;



    public WebApplicationTemplateResource(final IWebApplication webApplication, final String path, final String characterEncoding) {

        super();

        Validate.notNull(webApplication, "Web Application object cannot be null");
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.webApplication = webApplication;
        final String cleanPath = TemplateResourceUtils.cleanPath(path);
        this.path = (cleanPath.charAt(0) != '/' ? ("/" + cleanPath) : cleanPath);
        this.characterEncoding = characterEncoding;

    }




    public String getDescription() {
        return this.path;
    }




    public String getBaseName() {
        return TemplateResourceUtils.computeBaseName(this.path);
    }




    public Reader reader() throws IOException {

        final InputStream inputStream = this.webApplication.getResourceAsStream(this.path);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("Web Application resource \"%s\" does not exist", this.path));
        }

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream), this.characterEncoding));
        }

        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));

    }




    public ITemplateResource relative(final String relativeLocation) {

        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");

        final String fullRelativeLocation = TemplateResourceUtils.computeRelativeLocation(this.path, relativeLocation);
        return new WebApplicationTemplateResource(this.webApplication, fullRelativeLocation, this.characterEncoding);

    }




    public boolean exists() {
        return this.webApplication.resourceExists(this.path);
    }


}

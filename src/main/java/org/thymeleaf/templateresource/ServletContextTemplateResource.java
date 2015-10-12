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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class ServletContextTemplateResource implements ITemplateResource {


    private final ServletContext servletContext;
    private final String path;
    private final String characterEncoding;



    public ServletContextTemplateResource(final ServletContext servletContext, final String path, final String characterEncoding) {

        super();

        Validate.notNull(servletContext, "ServletContext cannot be null");
        Validate.notEmpty(path, "Resource Path cannot be null or empty");
        // Character encoding CAN be null (system default will be used)

        this.servletContext = servletContext;
        this.path = (path.charAt(0) != '/' ? ("/" + path) : path);
        this.characterEncoding = characterEncoding;

    }




    public String getName() {
        return this.path;
    }




    public Reader reader() throws IOException {

        final InputStream inputStream = this.servletContext.getResourceAsStream(this.path);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("ServletContext resource \"%s\" does not exist", this.path));
        }

        if (!StringUtils.isEmptyOrWhitespace(this.characterEncoding)) {
            return new InputStreamReader(inputStream, this.characterEncoding);
        }

        return new InputStreamReader(inputStream);

    }




    public ITemplateResource relative(final String relativePath) throws IOException {

        Validate.notEmpty(relativePath, "Relative Path cannot be null or empty");

        final String fullRelativePath = createRelativePath(this.path, relativePath);
        return new ServletContextTemplateResource(this.servletContext, fullRelativePath, this.characterEncoding);

    }




    public boolean exists() {
        try {
            return (this.servletContext.getResource(this.path) != null);
        } catch (final MalformedURLException e) {
            return false;
        }
    }




    static String createRelativePath(final String path, final String relativePath) {
        final int separatorPos = path.lastIndexOf('/');
        if (separatorPos != -1) {
            final StringBuilder pathBuilder = new StringBuilder(path.length() + relativePath.length());
            pathBuilder.append(path, 0, separatorPos);
            if (relativePath.charAt(0) != '/') {
                pathBuilder.append('/');
            }
            pathBuilder.append(relativePath);
            return pathBuilder.toString();
        }
        return relativePath;
    }


}

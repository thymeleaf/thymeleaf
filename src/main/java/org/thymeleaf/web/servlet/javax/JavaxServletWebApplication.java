/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2021, The THYMELEAF team (http://www.thymeleaf.org)
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

package org.thymeleaf.web.servlet.javax;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.thymeleaf.util.Validate;
import org.thymeleaf.web.servlet.IServletWebApplication;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public final class JavaxServletWebApplication implements IServletWebApplication {

    private final ServletContext servletContext;


    JavaxServletWebApplication(final ServletContext servletContext) {
        super();
        Validate.notNull(servletContext, "Servlet context cannot be null");
        this.servletContext = servletContext;
    }


    @Override
    public Enumeration<String> getAttributeNames() {
        return this.servletContext.getAttributeNames();
    }

    @Override
    public Object getAttributeValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.servletContext.getAttribute(name);
    }

    @Override
    public void setAttributeValue(final String name, final Object value) {
        Validate.notNull(name, "Name cannot be null");
        this.servletContext.setAttribute(name, value);
    }


    @Override
    public InputStream getResourceAsStream(final String path) {
        Validate.notNull(path, "Path cannot be null");
        return this.servletContext.getResourceAsStream(path);
    }

    @Override
    public URL getResource(final String path) throws MalformedURLException {
        Validate.notNull(path, "Path cannot be null");
        return this.servletContext.getResource(path);
    }


    @Override
    public Object getNativeObject() {
        return this.servletContext;
    }

}

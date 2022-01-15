/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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

package org.thymeleaf.web.servlet;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
final class JavaxServletWebSession implements IServletWebSession {

    private final HttpServletRequest request;
    private HttpSession session;


    JavaxServletWebSession(final HttpServletRequest request) {
        super();
        Validate.notNull(request, "Request cannot be null");
        this.request = request;
        this.session = this.request.getSession(false); // Might initialize property as null
    }


    @Override
    public boolean exists() {
        return this.session != null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        if (this.session == null) {
            return Collections.emptyEnumeration();
        }
        return this.session.getAttributeNames();
    }

    @Override
    public Object getAttributeValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        if (this.session == null) {
            return null;
        }
        return this.session.getAttribute(name);
    }

    @Override
    public void setAttributeValue(final String name, final Object value) {
        Validate.notNull(name, "Name cannot be null");
        if (this.session == null) {
            // Setting an attribute will actually create a new session
            this.session = this.request.getSession(true);
        }
        this.session.setAttribute(name, value);
    }


    @Override
    public Object getNativeSessionObject() {
        return this.session;
    }

}

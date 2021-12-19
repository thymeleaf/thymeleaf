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

package org.thymeleaf.spring5.web.webflux;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public final class SpringWebFluxWebApplication implements ISpringWebFluxWebApplication {

    /*
     * There is no equivalent in Spring WebFlux to an application-level attribute container (e.g. ServletContext).
     * So for the attribute part of this interface, this implementation will simply keep an empty attribute map.
     */


    SpringWebFluxWebApplication() {
        super();
    }


    public static SpringWebFluxWebApplication buildApplication() {
        return new SpringWebFluxWebApplication();
    }


    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public void setAttributeValue(final String name, final Object value) {
        Validate.notNull(name, "Name cannot be null");
        throw new UnsupportedOperationException("No support for application-level attributes in Spring WebFlux");
    }

    @Override
    public void removeAttribute(final String name) {
        Validate.notNull(name, "Name cannot be null");
        throw new UnsupportedOperationException("No support for application-level attributes in Spring WebFlux");
    }


    @Override
    public boolean resourceExists(final String path) {
        Validate.notNull(path, "Path cannot be null");
        throw new UnsupportedOperationException("No support for webapplication-based resource resolution in Spring WebFlux");
    }

    @Override
    public InputStream getResourceAsStream(final String path) {
        Validate.notNull(path, "Path cannot be null");
        throw new UnsupportedOperationException("No support for webapplication-based resource resolution in Spring WebFlux");
    }


    @Override
    public Object getNativeObject() {
        return null;
    }

}

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

package org.thymeleaf.spring6.web.webflux;

import java.net.URI;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
final class SpringWebFluxWebRequest implements ISpringWebFluxWebRequest {

    private final ServerHttpRequest request;


    SpringWebFluxWebRequest(final ServerHttpRequest request) {
        super();
        Validate.notNull(request, "Server HTTP Request cannot be null");
        this.request = request;
    }


    @Override
    public String getMethod() {
        return this.request.getMethodValue();
    }

    @Override
    public URI getURI() {
        return this.request.getURI();
    }

    @Override
    public String getApplicationPath() {
        return this.request.getPath().contextPath().value();
    }

    @Override
    public String getPathWithinApplication() {
        return this.request.getPath().pathWithinApplication().value();
    }


    @Override
    public MultiValueMap<String, String> getHeaderMultiValueMap() {
        return this.request.getHeaders();
    }


    @Override
    public MultiValueMap<String, String> getParameterMultiValueMap() {
        return this.request.getQueryParams();
    }


    @Override
    public MultiValueMap<String, HttpCookie> getCookieMultiValueMap() {
        return this.request.getCookies();
    }


    @Override
    public Object getNativeRequestObject() {
        return this.request;
    }




}

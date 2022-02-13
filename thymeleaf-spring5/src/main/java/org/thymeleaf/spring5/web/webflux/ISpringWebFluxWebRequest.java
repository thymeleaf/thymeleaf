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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebRequest;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 * 
 */
public interface ISpringWebFluxWebRequest extends IWebRequest {


    public URI getURI();

    @Override
    default String getScheme() {
        return getURI().getScheme();
    }

    @Override
    default String getServerName() {
        return getURI().getHost();
    }

    @Override
    default Integer getServerPort() {
        return Integer.valueOf(getURI().getPort());
    }

    @Override
    default String getQueryString() {
        return getURI().getRawQuery();
    }


    public MultiValueMap<String,String> getHeaderMultiValueMap();

    @Override
    default boolean containsHeader(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return getHeaderMultiValueMap().containsKey(name);
    }

    @Override
    default int getHeaderCount() {
        return getHeaderMultiValueMap().size();
    }

    @Override
    default Set<String> getAllHeaderNames() {
        return getHeaderMultiValueMap().keySet();
    }

    @Override
    default Map<String, String[]> getHeaderMap() {
        return MultiValueMapUtil.stringToStringArrayMultiMap(getHeaderMultiValueMap());
    }

    @Override
    default String getHeaderValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return getHeaderMultiValueMap().getFirst(name);
    }

    @Override
    default String[] getHeaderValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final List<String> headerValueList = getHeaderMultiValueMap().get(name);
        return headerValueList.toArray(new String[headerValueList.size()]);
    }


    public MultiValueMap<String,String> getParameterMultiValueMap();

    @Override
    default boolean containsParameter(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return getParameterMultiValueMap().containsKey(name);
    }

    @Override
    default int getParameterCount() {
        return getParameterMultiValueMap().size();
    }

    @Override
    default Set<String> getAllParameterNames() {
        return getParameterMultiValueMap().keySet();
    }

    @Override
    default Map<String, String[]> getParameterMap() {
        return MultiValueMapUtil.stringToStringArrayMultiMap(getParameterMultiValueMap());
    }

    @Override
    default String getParameterValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return getParameterMultiValueMap().getFirst(name);
    }

    @Override
    default String[] getParameterValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final List<String> parameterValueList = getParameterMultiValueMap().get(name);
        return parameterValueList.toArray(new String[parameterValueList.size()]);
    }


    public MultiValueMap<String,HttpCookie> getCookieMultiValueMap();

    @Override
    default boolean containsCookie(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return getCookieMultiValueMap().containsKey(name);
    }

    @Override
    default int getCookieCount() {
        return getCookieMultiValueMap().size();
    }

    @Override
    default Set<String> getAllCookieNames() {
        return getCookieMultiValueMap().keySet();
    }

    @Override
    default Map<String, String[]> getCookieMap() {
        return MultiValueMapUtil.cookieToStringArrayMultiMap(getCookieMultiValueMap());
    }

    @Override
    default String getCookieValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final HttpCookie cookie = getCookieMultiValueMap().getFirst(name);
        return (cookie == null)? null : cookie.getValue();
    }

    @Override
    default String[] getCookieValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final List<HttpCookie> cookieList = getCookieMultiValueMap().get(name);
        return cookieList.stream().map(HttpCookie::getValue).toArray(String[]::new);
    }


    public Object getNativeRequestObject();

}

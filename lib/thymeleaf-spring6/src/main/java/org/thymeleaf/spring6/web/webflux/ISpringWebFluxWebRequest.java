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
import java.util.Collections;
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
        final URI uri = getURI();
        return (uri == null)? null : uri.getScheme();
    }

    @Override
    default String getServerName() {
        final URI uri = getURI();
        return (uri == null)? null : uri.getHost();
    }

    @Override
    default Integer getServerPort() {
        final URI uri = getURI();
        return (uri == null)? null : Integer.valueOf(uri.getPort());
    }

    @Override
    default String getQueryString() {
        final URI uri = getURI();
        return (uri == null)? null : uri.getRawQuery();
    }


    public MultiValueMap<String,String> getHeaderMultiValueMap();

    @Override
    default boolean containsHeader(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,String> headerMultiValueMap = getHeaderMultiValueMap();
        return headerMultiValueMap != null && headerMultiValueMap.containsKey(name);
    }

    @Override
    default int getHeaderCount() {
        final MultiValueMap<String,String> headerMultiValueMap = getHeaderMultiValueMap();
        return (headerMultiValueMap == null) ? 0 : headerMultiValueMap.size();
    }

    @Override
    default Set<String> getAllHeaderNames() {
        final MultiValueMap<String,String> headerMultiValueMap = getHeaderMultiValueMap();
        return (headerMultiValueMap == null) ? Collections.emptySet() : headerMultiValueMap.keySet();
    }

    @Override
    default Map<String, String[]> getHeaderMap() {
        final MultiValueMap<String,String> headerMultiValueMap = getHeaderMultiValueMap();
        if (headerMultiValueMap == null) {
            return Collections.emptyMap();
        }
        return MultiValueMapUtil.stringToStringArrayMultiMap(headerMultiValueMap);
    }

    @Override
    default String getHeaderValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,String> headerMultiValueMap = getHeaderMultiValueMap();
        return (headerMultiValueMap == null) ? null : headerMultiValueMap.getFirst(name);
    }

    @Override
    default String[] getHeaderValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,String> headerMultiValueMap = getHeaderMultiValueMap();
        if (headerMultiValueMap == null) {
            return MultiValueMapUtil.EMPTY_VALUES;
        }
        final List<String> headerValueList = headerMultiValueMap.get(name);
        if (headerValueList == null) {
            return MultiValueMapUtil.EMPTY_VALUES;
        }
        return headerValueList.toArray(new String[headerValueList.size()]);
    }


    public MultiValueMap<String,String> getParameterMultiValueMap();

    @Override
    default boolean containsParameter(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,String> parameterMultiValueMap = getParameterMultiValueMap();
        return parameterMultiValueMap != null && parameterMultiValueMap.containsKey(name);
    }

    @Override
    default int getParameterCount() {
        final MultiValueMap<String,String> parameterMultiValueMap = getParameterMultiValueMap();
        return (parameterMultiValueMap == null)? 0 : parameterMultiValueMap.size();
    }

    @Override
    default Set<String> getAllParameterNames() {
        final MultiValueMap<String,String> parameterMultiValueMap = getParameterMultiValueMap();
        return (parameterMultiValueMap == null)? null : parameterMultiValueMap.keySet();
    }

    @Override
    default Map<String, String[]> getParameterMap() {
        final MultiValueMap<String,String> parameterMultiValueMap = getParameterMultiValueMap();
        if (parameterMultiValueMap == null) {
            return Collections.emptyMap();
        }
        return MultiValueMapUtil.stringToStringArrayMultiMap(parameterMultiValueMap);
    }

    @Override
    default String getParameterValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,String> parameterMultiValueMap = getParameterMultiValueMap();
        return (parameterMultiValueMap == null)? null : parameterMultiValueMap.getFirst(name);
    }

    @Override
    default String[] getParameterValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,String> parameterMultiValueMap = getParameterMultiValueMap();
        if (parameterMultiValueMap == null) {
            return MultiValueMapUtil.EMPTY_VALUES;
        }
        final List<String> parameterValueList = parameterMultiValueMap.get(name);
        if (parameterValueList == null) {
            return MultiValueMapUtil.EMPTY_VALUES;
        }
        return parameterValueList.toArray(new String[parameterValueList.size()]);
    }


    public MultiValueMap<String,HttpCookie> getCookieMultiValueMap();

    @Override
    default boolean containsCookie(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,HttpCookie> cookieMultiValueMap = getCookieMultiValueMap();
        return cookieMultiValueMap != null && cookieMultiValueMap.containsKey(name);
    }

    @Override
    default int getCookieCount() {
        final MultiValueMap<String,HttpCookie> cookieMultiValueMap = getCookieMultiValueMap();
        return (cookieMultiValueMap == null)? 0 : cookieMultiValueMap.size();
    }

    @Override
    default Set<String> getAllCookieNames() {
        final MultiValueMap<String,HttpCookie> cookieMultiValueMap = getCookieMultiValueMap();
        return (cookieMultiValueMap == null)? Collections.emptySet() : cookieMultiValueMap.keySet();
    }

    @Override
    default Map<String, String[]> getCookieMap() {
        final MultiValueMap<String,HttpCookie> cookieMultiValueMap = getCookieMultiValueMap();
        if (cookieMultiValueMap == null) {
            return Collections.emptyMap();
        }
        return MultiValueMapUtil.cookieToStringArrayMultiMap(cookieMultiValueMap);
    }

    @Override
    default String getCookieValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,HttpCookie> cookieMultiValueMap = getCookieMultiValueMap();
        if (cookieMultiValueMap == null) {
            return null;
        }
        final HttpCookie cookie = cookieMultiValueMap.getFirst(name);
        return (cookie == null)? null : cookie.getValue();
    }

    @Override
    default String[] getCookieValues(final String name) {
        Validate.notNull(name, "Name cannot be null");
        final MultiValueMap<String,HttpCookie> cookieMultiValueMap = getCookieMultiValueMap();
        if (cookieMultiValueMap == null) {
            return MultiValueMapUtil.EMPTY_VALUES;
        }
        final List<HttpCookie> cookieList = cookieMultiValueMap.get(name);
        if (cookieList == null) {
            return MultiValueMapUtil.EMPTY_VALUES;
        }
        return cookieList.stream().map(HttpCookie::getValue).toArray(String[]::new);
    }


    public Object getNativeRequestObject();

}

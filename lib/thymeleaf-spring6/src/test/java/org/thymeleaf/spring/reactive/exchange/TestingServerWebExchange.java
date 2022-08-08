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
package org.thymeleaf.spring.reactive.exchange;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public final class TestingServerWebExchange implements ServerWebExchange {

    private final TestingServerHttpRequest request;
    private final TestingServerHttpResponse response;
    private final TestingWebSession session;
    private final Map<String,Object> attributes;


    public TestingServerWebExchange(
            final String path, final Map<String,List<String>> queryParams,
            final Map<String,Object> requestAttributes, final Map<String,Object> sessionAttributes) {
        super();
        this.request = new TestingServerHttpRequest(path, queryParams);
        this.response = new TestingServerHttpResponse();
        this.session = new TestingWebSession(sessionAttributes);
        this.attributes = requestAttributes;
    }


    public TestingServerWebExchange(final String path) {
        super();
        this.request = new TestingServerHttpRequest(path, new HashMap<>());
        this.response = new TestingServerHttpResponse();
        this.session = new TestingWebSession(new HashMap<>());
        this.attributes = new HashMap<>();
    }


    @Override
    public ServerHttpRequest getRequest() {
        return this.request;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return this.response;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public <T> T getAttribute(final String s) {
        return (T)this.attributes.get(s);
    }

    @Override
    public <T> T getRequiredAttribute(final String name) {
        return null;
    }

    @Override
    public <T> T getAttributeOrDefault(final String name, final T defaultValue) {
        return null;
    }

    @Override
    public Mono<WebSession> getSession() {
        return Mono.just(this.session);
    }

    @Override
    public <T extends Principal> Mono<T> getPrincipal() {
        return null;
    }

    @Override
    public Mono<MultiValueMap<String, String>> getFormData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocaleContext getLocaleContext() {
        return new SimpleLocaleContext(Locale.US);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return null;
    }

    @Override
    public String transformUrl(final String s) {
        return "[" + s + "]";
    }

    @Override
    public void addUrlTransformer(final Function<String, String> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLogPrefix() {
        return null;
    }

    @Override
    public Builder mutate() {
        return null;
    }

    @Override
    public boolean isNotModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkNotModified(final Instant instant) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkNotModified(final String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkNotModified(final String s, final Instant instant) {
        throw new UnsupportedOperationException();
    }

}

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

import java.security.Principal;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
final class SpringWebFluxWebExchange implements ISpringWebFluxWebExchange {

    private final SpringWebFluxWebRequest webRequest;
    private final SpringWebFluxWebApplication webApplication;

    private SpringWebFluxWebSession webSession; // can be null, and is always lazily initialized
    private boolean webSessionInitialized;

    private final ServerWebExchange exchange;


    SpringWebFluxWebExchange(final SpringWebFluxWebRequest webRequest,
                             final SpringWebFluxWebApplication webApplication,
                             final ServerWebExchange exchange) {
        super();
        Validate.notNull(webRequest, "Request cannot be null");
        Validate.notNull(webApplication, "Application cannot be null");
        Validate.notNull(exchange, "Server Web Exchange cannot be null");
        this.webRequest = webRequest;
        this.webApplication = webApplication;
        this.exchange = exchange;
        // Session is lazily initialized because it requires the model to have been resolved (from Mono<Session>)
        this.webSession = null;
        this.webSessionInitialized = false;
    }


    @Override
    public ISpringWebFluxWebRequest getRequest() {
        return this.webRequest;
    }

    @Override
    public ISpringWebFluxWebSession getSession() {
        // ServerWebExchange returns a Mono<WebSession>, which SpringStandardDialect makes sure to ask reactor to
        // resolve before the view gets executed.
        // NOTE this will not be available until just before the view starts executing
        if (!this.webSessionInitialized) {
            final WebSession session = this.exchange.getAttribute(SpringContextUtils.WEB_SESSION_ATTRIBUTE_NAME);
            if (session != null) {
                this.webSession = new SpringWebFluxWebSession(session);
                this.webSessionInitialized = true;
            }
        }
        return this.webSession;
    }

    @Override
    public ISpringWebFluxWebApplication getApplication() {
        return this.webApplication;
    }


    @Override
    public Principal getPrincipal() {
        // ServerWebExchange returns a Mono<Principal>, which SpringStandardDialect makes sure to ask reactor to
        // resolve before the view gets executed.
        // NOTE this will not be available until just before the view starts executing
        return this.exchange.getAttribute(SpringContextUtils.WEB_EXCHANGE_PRINCIPAL_ATTRIBUTE_NAME);
    }

    @Override
    public Locale getLocale() {
        return this.exchange.getLocaleContext().getLocale();
    }


    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.exchange.getAttributes());
    }

    @Override
    public Object getAttributeValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return this.exchange.getAttribute(name);
    }

    @Override
    public void setAttributeValue(final String name, final Object value) {
        Validate.notNull(name, "Name cannot be null");
        this.exchange.getAttributes().put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        Validate.notNull(name, "Name cannot be null");
        this.exchange.getAttributes().remove(name);
    }

    @Override
    public Object getNativeObject() {
        return this.exchange;
    }


    @Override
    public String transformURL(final String url) {
        return this.exchange.transformUrl(url);
    }

}

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
package org.thymeleaf.spring5.context.webflux;

import java.util.Locale;
import java.util.Map;

import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;

/**
 * <p>
 *   Basic Spring WebFlux-oriented implementation of the {@link ISpringWebFluxContext} interfaces,
 *   easily usable as a context for calling the {@link org.thymeleaf.spring5.ISpringWebFluxTemplateEngine}
 *   from outside a {@link org.thymeleaf.spring5.view.reactive.ThymeleafReactiveView}.
 * </p>
 * <p>
 *   This class is not thread-safe, and should not be shared across executions of templates.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.8
 *
 */
public class SpringWebFluxContext extends AbstractContext implements ISpringWebFluxContext {


    private final ServerWebExchange exchange;
    private final ReactiveAdapterRegistry reactiveAdapterRegistry; // nullable


    /**
     * <p>
     *     Build a new instance of this Spring WebFlux-specific context object.
     * </p>
     *
     * @param exchange the Spring WebFlux exchange object, containing request, response and session. Cannot be null.
     */
    public SpringWebFluxContext(final ServerWebExchange exchange) {
        this(exchange, null, null, null);
    }

    /**
     * <p>
     *     Build a new instance of this Spring WebFlux-specific context object.
     * </p>
     *
     * @param exchange the Spring WebFlux exchange object, containing request, response and session. Cannot be null.
     * @param locale the locale to be used for executing Thymeleaf. Can be null (Locale.getDefault() will be used).
     */
    public SpringWebFluxContext(final ServerWebExchange exchange, final Locale locale) {
        this(exchange, null, locale, null);
    }

    /**
     * <p>
     *     Build a new instance of this Spring WebFlux-specific context object.
     * </p>
     *
     * @param exchange the Spring WebFlux exchange object, containing request, response and session. Cannot be null.
     * @param locale the locale to be used for executing Thymeleaf. Can be null (Locale.getDefault() will be used).
     * @param variables the variables to be used for executing Thymeleaf. Can be null.
     */
    public SpringWebFluxContext(
            final ServerWebExchange exchange,
            final Locale locale, final Map<String, Object> variables) {
        this(exchange, null, locale, variables);
    }

    /**
     * <p>
     *     Build a new instance of this Spring WebFlux-specific context object.
     * </p>
     *
     * @param exchange the Spring WebFlux exchange object, containing request, response and session. Cannot be null.
     * @param reactiveAdapterRegistry the Spring WebFlux reactive adapter object, used in cases when it is needed
     *                                to turn non-Reactor reactive streams (RxJava, etc.) into Reactor equivalents
     *                                (Flux, Mono) in order to be used by Thymeleaf as data drivers
     *                                (see {@link IReactiveDataDriverContextVariable}). Can be null.
     * @param locale the locale to be used for executing Thymeleaf. Can be null (Locale.getDefault() will be used).
     * @param variables the variables to be used for executing Thymeleaf. Can be null.
     */
    public SpringWebFluxContext(
            final ServerWebExchange exchange,
            final ReactiveAdapterRegistry reactiveAdapterRegistry,
            final Locale locale, final Map<String, Object> variables) {
        super(locale, variables);
        Validate.notNull(exchange, "ServerWebExchange cannot be null in Spring WebFlux contexts");
        // reactiveAdapterRegistry CAN be null
        this.exchange = exchange;
        this.reactiveAdapterRegistry = reactiveAdapterRegistry;
    }


    // This method is not included in the interface as it is more an implementation detail usable for
    // a specific scenario of Publisher-normalisation in WebFlux.
    public ReactiveAdapterRegistry getReactiveAdapterRegistry() {
        return this.reactiveAdapterRegistry;
    }

    @Override
    public ServerHttpRequest getRequest() {
        return this.exchange.getRequest();
    }

    @Override
    public Mono<WebSession> getSession() {
        return this.exchange.getSession();
    }

    @Override
    public ServerHttpResponse getResponse() {
        return this.exchange.getResponse();
    }

    @Override
    public ServerWebExchange getExchange() {
        return this.exchange;
    }

}

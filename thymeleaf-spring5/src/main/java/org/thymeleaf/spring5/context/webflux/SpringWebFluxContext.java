/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.context.AbstractContext;
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


    public SpringWebFluxContext(final ServerWebExchange exchange) {
        super();
        this.exchange = exchange;
    }

    public SpringWebFluxContext(final ServerWebExchange exchange, final Locale locale) {
        super(locale);
        this.exchange = exchange;
    }

    public SpringWebFluxContext(
            final ServerWebExchange exchange,
            final Locale locale, final Map<String, Object> variables) {
        super(locale, variables);
        this.exchange = exchange;
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

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
package thymeleafsandbox.springreactive.thymeleaf.context;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.context.AbstractContext;
import reactor.core.publisher.Mono;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class SpringReactiveWebContext extends AbstractContext implements ISpringReactiveWebContext {

    private final ServerWebExchange exchange;


    public SpringReactiveWebContext(final ServerWebExchange exchange) {
        super();
        this.exchange = exchange;
    }

    public SpringReactiveWebContext(final ServerWebExchange exchange, final Locale locale) {
        super(locale);
        this.exchange = exchange;
    }

    public SpringReactiveWebContext(final ServerWebExchange exchange, final Locale locale, final Map<String, Object> variables) {
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
        return null;
    }

}

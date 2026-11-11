/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.integration.webflux;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

// Sets "/app" as context path for paths starting with it, and marks every URL passed to the exchange's URL
// transformers when the request asks for it
public class TestWebFilter implements WebFilter {

    public static final String TRANSFORM_HEADER = "X-Test-Transform";

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        ServerWebExchange filtered = exchange;
        if (exchange.getRequest().getPath().value().startsWith("/app/")) {
            filtered = exchange.mutate().request(request -> request.contextPath("/app")).build();
        }
        if (exchange.getRequest().getHeaders().getFirst(TRANSFORM_HEADER) != null) {
            filtered.addUrlTransformer(url -> url + "~tx");
        }
        return chain.filter(filtered);
    }

}

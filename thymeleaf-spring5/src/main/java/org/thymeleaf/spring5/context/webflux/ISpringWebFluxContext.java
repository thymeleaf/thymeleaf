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

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.context.IContext;
import reactor.core.publisher.Mono;

/**
 * <p>
 *   Specialization of the {@link IContext} interface to be implemented by contexts used for template
 *   processing in Spring WebFlux environments.
 * </p>
 * <p>
 *   Objects implementing this interface add to the usual {@link IContext} data the Spring WebFlux-related
 *   artifacts needed to perform functions such as URL rewriting or request/session access.
 * </p>
 * <p>
 *   A basic implementation of this interface is provided by {@link SpringWebFluxExpressionContext}, but
 *   there is normally no reason why users should use this interface (or its implementations) directly.
 * </p>
 *
 * @see SpringWebFluxExpressionContext
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public interface ISpringWebFluxContext extends IContext {

    /**
     * <p>
     *   Returns the {@link ServerHttpRequest} object associated with the template execution.
     * </p>
     *
     * @return the request object.
     */
    public ServerHttpRequest getRequest();

    /**
     * <p>
     *   Returns the {@link ServerHttpResponse} object associated with the template execution.
     * </p>
     *
     * @return the response object.
     */
    public ServerHttpResponse getResponse();

    /**
     * <p>
     *   Returns the {@link WebSession} object associated with the template execution.
     * </p>
     * <p>
     *   The returned {@link Mono} will always return an instance, either matching the client's session id
     *   or a new session. Note that calling this method does not create the session object itself.
     * </p>
     *
     * @return the session object. Might be null if no session has been created.
     */
    public Mono<WebSession> getSession();

    /**
     * <p>
     *   Returns the {@link ServerWebExchange} object associated with the template execution.
     * </p>
     *
     * @return the servlet context object.
     */
    public ServerWebExchange getExchange();

}

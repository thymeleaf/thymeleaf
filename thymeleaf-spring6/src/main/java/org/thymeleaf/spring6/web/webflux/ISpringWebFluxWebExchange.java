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

import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebExchange;

/**
 * <p>
 *   Spring WebFlux-based interface for a web exchange.
 * </p>
 * <p>
 *   Note {@link #getSession()} and {@link #getPrincipal()} might return null not only if they are
 *   indeed null, but also if they have not yet been resolved. These structures are declared as {@code Mono<?>}
 *   in WebFlux exchange and request structures, and will only be resolved just before the rendering of the view starts.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public interface ISpringWebFluxWebExchange extends IWebExchange {


    public ISpringWebFluxWebRequest getRequest();
    public ISpringWebFluxWebSession getSession();
    public ISpringWebFluxWebApplication getApplication();


    public Map<String, Object> getAttributes();

    @Override
    default boolean containsAttribute(final String name) {
        Validate.notNull(name, "Name cannot be null");
        // Attribute map in ServerWebExchange, which does not allow null values. So containsKey is enough
        // to be equivalent to the Servlet implementations (in which null = removal)
        return getAttributes().containsKey(name);
    }

    @Override
    default int getAttributeCount() {
        return getAttributes().size();
    }

    @Override
    default Set<String> getAllAttributeNames() {
        return getAttributes().keySet();
    }

    @Override
    default Map<String, Object> getAttributeMap() {
        return getAttributes();
    }

    @Override
    default Object getAttributeValue(final String name) {
        Validate.notNull(name, "Name cannot be null");
        return getAttributes().get(name);
    }


    public Object getNativeExchangeObject();


}

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

import org.springframework.core.ReactiveAdapterRegistry;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebApplication;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.1.0
 *
 */
public interface ISpringWebFluxWebApplication extends IWebApplication {

    public ReactiveAdapterRegistry getReactiveAdapterRegistry();

    public Map<String, Object> getAttributes();

    @Override
    default boolean containsAttribute(final String name) {
        Validate.notNull(name, "Name cannot be null");
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

}

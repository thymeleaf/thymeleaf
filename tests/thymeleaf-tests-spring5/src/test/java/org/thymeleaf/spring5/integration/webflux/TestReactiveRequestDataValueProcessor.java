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

import java.util.Collections;
import java.util.Map;

import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import org.springframework.web.server.ServerWebExchange;

public class TestReactiveRequestDataValueProcessor implements RequestDataValueProcessor {

    @Override
    public String processAction(final ServerWebExchange exchange, final String action, final String httpMethod) {
        return action + (action.indexOf('?') >= 0 ? "&" : "?") + "rdvp=" + httpMethod;
    }

    @Override
    public String processFormFieldValue(final ServerWebExchange exchange, final String name, final String value, final String type) {
        return "color".equals(name) ? value.toUpperCase() : value;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(final ServerWebExchange exchange) {
        return Collections.singletonMap("_extra", "extra-value");
    }

    @Override
    public String processUrl(final ServerWebExchange exchange, final String url) {
        return url.replace("/marked", "/processed");
    }

}

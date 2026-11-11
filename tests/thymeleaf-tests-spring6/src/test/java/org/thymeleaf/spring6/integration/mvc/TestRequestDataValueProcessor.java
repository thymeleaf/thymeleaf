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
package org.thymeleaf.spring6.integration.mvc;

import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

public class TestRequestDataValueProcessor implements RequestDataValueProcessor {

    @Override
    public String processAction(final HttpServletRequest request, final String action, final String httpMethod) {
        return action + (action.indexOf('?') >= 0 ? "&" : "?") + "rdvp=" + httpMethod;
    }

    @Override
    public String processFormFieldValue(final HttpServletRequest request, final String name, final String value, final String type) {
        return "color".equals(name) ? value.toUpperCase() : value;
    }

    @Override
    public Map<String, String> getExtraHiddenFields(final HttpServletRequest request) {
        return Collections.singletonMap("_extra", "extra-value");
    }

    @Override
    public String processUrl(final HttpServletRequest request, final String url) {
        return url.replace("/marked", "/processed");
    }

}

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
package org.thymeleaf.spring6.integration.webflux;

import java.util.Collections;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

public class WebFluxPlainFeaturesTest extends AbstractWebFluxFeaturesTest {

    @Override
    protected AnnotationConfigApplicationContext newContext() {
        return new AnnotationConfigApplicationContext();
    }

    @Override
    protected Class<?> getConfigurationClass() {
        return WebFluxPlainConfiguration.class;
    }

    @Override
    protected Map<String, Object> getBaseProperties() {
        return Collections.emptyMap();
    }

    @Override
    protected WebTestClient createTemplateModesClient() {
        // The template resolver is configured with patterns for TEXT and JAVASCRIPT templates
        return client();
    }

    @Override
    protected WebTestClient createDecoupledLogicClient() {
        return client(properties(WebFluxPlainConfiguration.USE_DECOUPLED_LOGIC, "true"));
    }

}

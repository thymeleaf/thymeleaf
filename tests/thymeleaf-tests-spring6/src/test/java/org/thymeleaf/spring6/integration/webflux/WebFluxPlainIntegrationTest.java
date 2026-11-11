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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.thymeleaf.spring6.integration.IntegrationTestUtils.text;

public class WebFluxPlainIntegrationTest extends AbstractWebFluxIntegrationTest {

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
    protected Map<String, Object> getUseCodeAsDefaultMessageProperties() {
        return properties(WebFluxPlainConfiguration.USE_CODE_AS_DEFAULT_MESSAGE, "true");
    }

    @Override
    protected Map<String, Object> getMaxChunkSizeProperties(final int maxChunkSizeBytes) {
        return properties(WebFluxPlainConfiguration.MAX_CHUNK_SIZE, String.valueOf(maxChunkSizeBytes));
    }

    @Override
    protected WebTestClient createFailOnMissingMessageClient() {
        return client(properties(WebFluxPlainConfiguration.FAIL_ON_MISSING_MESSAGE, "true"));
    }

    @Override
    protected Map<String, Object> getSpringELCompilerProperties() {
        return properties(WebFluxPlainConfiguration.ENABLE_SPRING_EL_COMPILER, "true");
    }

    @Override
    protected Map<String, Object> getHiddenMarkersBeforeCheckboxesProperties() {
        return properties(WebFluxPlainConfiguration.RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES, "true");
    }

    @Override
    protected Map<String, Object> getMediaTypesProperties(final String mediaTypes) {
        return properties(WebFluxPlainConfiguration.MEDIA_TYPES, mediaTypes);
    }

    @Override
    protected Map<String, Object> getViewNamesProperties(final String viewNames, final String excludedViewNames) {
        final Map<String,Object> properties = properties();
        if (viewNames != null) {
            properties.put(WebFluxPlainConfiguration.VIEW_NAMES, viewNames);
        }
        if (excludedViewNames != null) {
            properties.put(WebFluxPlainConfiguration.EXCLUDED_VIEW_NAMES, excludedViewNames);
        }
        return properties;
    }

    @Override
    protected Map<String, Object> getRenderModeViewNamesProperties(
            final int maxChunkSizeBytes, final String fullModeViewNames, final String chunkedModeViewNames) {
        final Map<String,Object> properties = getMaxChunkSizeProperties(maxChunkSizeBytes);
        if (fullModeViewNames != null) {
            properties.put(WebFluxPlainConfiguration.FULL_MODE_VIEW_NAMES, fullModeViewNames);
        }
        if (chunkedModeViewNames != null) {
            properties.put(WebFluxPlainConfiguration.CHUNKED_MODE_VIEW_NAMES, chunkedModeViewNames);
        }
        return properties;
    }


    @Test
    public void testTemplateEngineMessageSource() {

        final String html = render(client(properties(WebFluxPlainConfiguration.TEMPLATE_ENGINE_MESSAGE_SOURCE, "true")), "/messages");
        Assertions.assertEquals("Hi from the engine message source, Messages", text(html, "m1"));
        Assertions.assertEquals("??missing.key_en_US??", text(html, "m2"));
        Assertions.assertEquals("NULL", text(html, "m3"));

    }


    @Test
    public void testExplicitMessageResolver() {

        // An explicitly set message resolver replaces the MessageSource-based one
        final String html = render(client(properties(WebFluxPlainConfiguration.STANDARD_MESSAGE_RESOLVER, "true")), "/messages");
        Assertions.assertEquals("??greeting_en_US??", text(html, "m1"));
        Assertions.assertEquals("From template", text(html, "m7"));

    }


    @Test
    public void testStaticVariables() {

        final String html = render(client(properties(WebFluxPlainConfiguration.STATIC_VARIABLE, "static-value")), "/hello");
        Assertions.assertEquals("static-value", text(html, "static"));

    }

}

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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.integration.ExtraTemplatingConfiguration;
import org.thymeleaf.spring6.integration.IntegrationTestUtils;

import static org.thymeleaf.spring6.integration.IntegrationTestUtils.text;

public class WebFluxBootIntegrationTest extends AbstractWebFluxIntegrationTest {

    @Override
    protected AnnotationConfigApplicationContext newContext() {
        return new AnnotationConfigReactiveWebApplicationContext();
    }

    @Override
    protected Class<?> getConfigurationClass() {
        return WebFluxBootConfiguration.class;
    }

    static final Map<String,Object> BOOT_BASE_PROPERTIES = properties(
            "spring.thymeleaf.prefix", IntegrationTestUtils.TEMPLATE_PREFIX,
            "spring.messages.basename", IntegrationTestUtils.MESSAGES_BASENAME,
            "spring.messages.fallback-to-system-locale", "false");


    @Override
    protected Map<String, Object> getBaseProperties() {
        return BOOT_BASE_PROPERTIES;
    }

    @Override
    protected Map<String, Object> getUseCodeAsDefaultMessageProperties() {
        return properties("spring.messages.use-code-as-default-message", "true");
    }

    @Override
    protected Map<String, Object> getMaxChunkSizeProperties(final int maxChunkSizeBytes) {
        return properties("spring.thymeleaf.reactive.max-chunk-size", maxChunkSizeBytes + "B");
    }

    @Override
    protected WebTestClient createFailOnMissingMessageClient() {
        // No Boot property for this: applications configure the auto-configured engine bean
        final AnnotationConfigApplicationContext context = createContext(properties());
        context.getBean(SpringTemplateEngine.class).setFailOnMissingMessage(true);
        return WebTestClient.bindToApplicationContext(context).build();
    }

    @Override
    protected Map<String, Object> getSpringELCompilerProperties() {
        return properties("spring.thymeleaf.enable-spring-el-compiler", "true");
    }

    @Override
    protected Map<String, Object> getHiddenMarkersBeforeCheckboxesProperties() {
        return properties("spring.thymeleaf.render-hidden-markers-before-checkboxes", "true");
    }

    @Override
    protected Map<String, Object> getMediaTypesProperties(final String mediaTypes) {
        return properties("spring.thymeleaf.reactive.media-types", mediaTypes);
    }

    @Override
    protected Map<String, Object> getViewNamesProperties(final String viewNames, final String excludedViewNames) {
        final Map<String,Object> properties = properties();
        if (viewNames != null) {
            properties.put("spring.thymeleaf.view-names", viewNames);
        }
        if (excludedViewNames != null) {
            properties.put("spring.thymeleaf.excluded-view-names", excludedViewNames);
        }
        return properties;
    }

    @Override
    protected Map<String, Object> getRenderModeViewNamesProperties(
            final int maxChunkSizeBytes, final String fullModeViewNames, final String chunkedModeViewNames) {
        final Map<String,Object> properties = getMaxChunkSizeProperties(maxChunkSizeBytes);
        if (fullModeViewNames != null) {
            properties.put("spring.thymeleaf.reactive.full-mode-view-names", fullModeViewNames);
        }
        if (chunkedModeViewNames != null) {
            properties.put("spring.thymeleaf.reactive.chunked-mode-view-names", chunkedModeViewNames);
        }
        return properties;
    }


    @Test
    public void testDialectAndTemplateResolverBeans() {

        final WebTestClient client =
                WebTestClient.bindToApplicationContext(createContext(properties(), ExtraTemplatingConfiguration.class)).build();
        Assertions.assertEquals("Greetings, X", text(render(client, "/extra"), "greeter"));
        Assertions.assertEquals("Hello, World!", text(render(client, "/hello"), "greeting"));

    }

}

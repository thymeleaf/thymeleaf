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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.thymeleaf.spring6.integration.ExtraTemplatingConfiguration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import static org.thymeleaf.spring6.integration.IntegrationTestUtils.element;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.text;

public class WebFluxBootFeaturesTest extends AbstractWebFluxFeaturesTest {

    @Override
    protected AnnotationConfigApplicationContext newContext() {
        return new AnnotationConfigReactiveWebApplicationContext();
    }

    @Override
    protected Class<?> getConfigurationClass() {
        return WebFluxBootConfiguration.class;
    }

    @Override
    protected Map<String, Object> getBaseProperties() {
        return WebFluxBootIntegrationTest.BOOT_BASE_PROPERTIES;
    }

    @Override
    protected WebTestClient createTemplateModesClient() {
        return WebTestClient.bindToApplicationContext(createContext(properties(), ExtraTemplatingConfiguration.class)).build();
    }

    @Override
    protected WebTestClient createDecoupledLogicClient() {
        // No Boot property for this: applications configure the auto-configured template resolver bean
        final AnnotationConfigApplicationContext context = createContext(properties());
        context.getBean("defaultTemplateResolver", SpringResourceTemplateResolver.class).setUseDecoupledLogic(true);
        return WebTestClient.bindToApplicationContext(context).build();
    }


    @Test
    public void testFragmentsFromOtherTemplateResolver() {

        final WebTestClient client =
                WebTestClient.bindToApplicationContext(createContext(properties(), ExtraTemplatingConfiguration.class)).build();
        Assertions.assertEquals("<p id=\"e\"><span>Greetings, F</span></p>", element(render(client, "/uses-extra"), "e"));

    }


    @Test
    public void testLayoutDialect() {

        Assertions.assertEquals(
                "<!DOCTYPE html>\n<html>\n<head>\n\n<title>Page - Site</title>\n\n</head>\n<body>\n" +
                "<header>Header</header>\n" +
                "<section><p>Layout Dialect content</p></section>\n" +
                "<footer>Hello, footer!</footer>\n" +
                "</body>\n</html>\n",
                render(client(), "/layout-dialect"));

    }


    @Test
    public void testErrorPages() {

        final EntityExchangeResult<String> notFound = client().get().uri("/missing").accept(MediaType.TEXT_HTML)
                .exchange().expectBody(String.class).returnResult();
        Assertions.assertEquals(404, notFound.getStatus().value());
        final String html = notFound.getResponseBody();
        Assertions.assertEquals("Not found page", text(html, "page"));
        Assertions.assertEquals("404", text(html, "status"));
        Assertions.assertEquals("Not Found", text(html, "error"));
        Assertions.assertEquals("/missing", text(html, "path"));

    }

}

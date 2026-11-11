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
package org.thymeleaf.integration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;

import static org.thymeleaf.integration.HttpTestClient.get;
import static org.thymeleaf.integration.IntegrationTestUtils.text;

/*
 * Engine and resolver settings that need their own server.
 */
public class WebSetupIntegrationTest {


    private static Function<IWebApplication, ITemplateEngine> engine(
            final Consumer<WebApplicationTemplateResolver> resolverConfig, final Consumer<TemplateEngine> engineConfig) {
        return webApplication -> {
            final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(webApplication);
            templateResolver.setPrefix("/WEB-INF/templates/");
            AbstractWebIntegrationTest.configureTemplateResolver(templateResolver);
            resolverConfig.accept(templateResolver);
            final TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            engineConfig.accept(templateEngine);
            return templateEngine;
        };
    }


    private static StandardMessageResolver messageResolver(final TemplateEngine templateEngine) {
        return (StandardMessageResolver) templateEngine.getMessageResolvers().iterator().next();
    }


    private static void replaceVersion(final EmbeddedTomcat server, final String version) throws Exception {
        final Path template = server.getDocBase().resolve("WEB-INF/templates/cache.html");
        final String content = new String(Files.readAllBytes(template), StandardCharsets.UTF_8);
        Files.write(template, content.replaceAll(">v\\d+<", ">" + version + "<").getBytes(StandardCharsets.UTF_8));
    }




    @Test
    public void testFailOnMissingMessage() throws Exception {
        try (final EmbeddedTomcat server =
                     EmbeddedTomcat.start(engine(r -> {}, e -> {
                         final StandardMessageResolver messageResolver = new StandardMessageResolver();
                         messageResolver.setFailOnMissingMessage(true);
                         e.setMessageResolver(messageResolver);
                     }))) {
            final HttpTestClient.Result result = get(server, "/messages");
            Assertions.assertEquals(500, result.status, result.body);
            Assertions.assertTrue(
                    result.body.contains("Could not resolve message with key \"missing.key\" for locale \"en\""), result.body);
        }
    }


    @Test
    public void testDefaultMessages() throws Exception {
        try (final EmbeddedTomcat server =
                     EmbeddedTomcat.start(engine(r -> {}, e -> {
                         final StandardMessageResolver messageResolver = new StandardMessageResolver();
                         messageResolver.addDefaultMessage("missing.key", "from defaults");
                         messageResolver.addDefaultMessage("greeting", "not used");
                         e.setMessageResolver(messageResolver);
                     }))) {
            final HttpTestClient.Result result = get(server, "/messages");
            Assertions.assertEquals(200, result.status, result.body);
            Assertions.assertEquals("from defaults", text(result.body, "missing"));
            Assertions.assertEquals("Hello, Ana!", text(result.body, "greeting"));
            Assertions.assertEquals("false", text(result.body, "ornull"));
        }
    }


    @Test
    public void testCacheableTemplatesAreNotReloaded() throws Exception {
        try (final EmbeddedTomcat server = EmbeddedTomcat.start(engine(r -> r.setCacheable(true), e -> {}))) {
            Assertions.assertEquals("v1", text(get(server, "/cache").body, "version"));
            replaceVersion(server, "v2");
            Assertions.assertEquals("v1", text(get(server, "/cache").body, "version"));
        }
    }


    @Test
    public void testNonCacheableTemplatesAreReloaded() throws Exception {
        try (final EmbeddedTomcat server = EmbeddedTomcat.start(engine(r -> r.setCacheable(false), e -> {}))) {
            Assertions.assertEquals("v1", text(get(server, "/cache").body, "version"));
            replaceVersion(server, "v2");
            Assertions.assertEquals("v2", text(get(server, "/cache").body, "version"));
        }
    }


    @Test
    public void testCacheTTL() throws Exception {
        try (final EmbeddedTomcat server =
                     EmbeddedTomcat.start(engine(r -> { r.setCacheable(true); r.setCacheTTLMs(Long.valueOf(1L)); }, e -> {}))) {
            Assertions.assertEquals("v1", text(get(server, "/cache").body, "version"));
            replaceVersion(server, "v2");
            Thread.sleep(20L);
            Assertions.assertEquals("v2", text(get(server, "/cache").body, "version"));
        }
    }


    @Test
    public void testClearTemplateCache() throws Exception {
        final TemplateEngine[] engineHolder = new TemplateEngine[1];
        try (final EmbeddedTomcat server =
                     EmbeddedTomcat.start(engine(r -> r.setCacheable(true), e -> engineHolder[0] = e))) {
            Assertions.assertEquals("v1", text(get(server, "/cache").body, "version"));
            replaceVersion(server, "v2");
            engineHolder[0].clearTemplateCacheFor("cache");
            Assertions.assertEquals("v2", text(get(server, "/cache").body, "version"));
        }
    }


    @Test
    public void testDecoupledLogic() throws Exception {
        try (final EmbeddedTomcat server = EmbeddedTomcat.start(engine(r -> r.setUseDecoupledLogic(true), e -> {}))) {
            Assertions.assertEquals("Ana", text(get(server, "/decoupled").body, "d"));
            Assertions.assertEquals("Hello, World!", text(get(server, "/hello").body, "hello"));
            // Decoupled logic can add th:inline to a script block, which is then processed in JAVASCRIPT mode
            final HttpTestClient.Result js = get(server, "/decoupled-js");
            Assertions.assertEquals(200, js.status, js.body);
            Assertions.assertEquals("Ana", text(js.body, "d"));
            Assertions.assertEquals("\nvar name = \"Ana\";\n\nvar v1 = 1;\nvar v2 = 2;\n\n", text(js.body, "s"));
            Assertions.assertEquals("var coupled = \"Ana\";", text(js.body, "c"));
        }
    }


    @Test
    public void testDecoupledLogicWithTextTemplateModes() throws Exception {
        // The flag is ignored for templates that are not resolved in a markup template mode
        try (final EmbeddedTomcat server = EmbeddedTomcat.start(engine(r -> r.setUseDecoupledLogic(true), e -> {}))) {
            final HttpTestClient.Result text = get(server, "/report.txt");
            Assertions.assertEquals(200, text.status, text.body);
            Assertions.assertEquals("Ana=34\nBea=28\nCarl=45\n", text.body);
            final HttpTestClient.Result js = get(server, "/app.js");
            Assertions.assertEquals(200, js.status, js.body);
            Assertions.assertEquals("var names = [\"Ana\",\"Bea\",\"Carl\"];\n", js.body);
            Assertions.assertEquals("Ana", text(get(server, "/decoupled").body, "d"));
        }
    }


    @Test
    public void testMissingTemplate() throws Exception {
        try (final EmbeddedTomcat server = EmbeddedTomcat.start(engine(r -> {}, e -> {}))) {
            final HttpTestClient.Result result = get(server, "/missing");
            Assertions.assertEquals(500, result.status, result.body);
            Assertions.assertTrue(result.body.contains("/WEB-INF/templates/does-not-exist.html"), result.body);
            Assertions.assertTrue(result.body.contains("java.io.FileNotFoundException"), result.body);
        }
    }


    @Test
    public void testResolverChain() throws Exception {
        try (final EmbeddedTomcat server =
                     EmbeddedTomcat.start(engine(r -> { r.setCheckExistence(true); r.setOrder(Integer.valueOf(1)); }, e -> {
                         final ClassLoaderTemplateResolver second = new ClassLoaderTemplateResolver();
                         second.setPrefix("integration/second/");
                         second.setSuffix(".html");
                         second.setCharacterEncoding("UTF-8");
                         second.setCheckExistence(true);
                         second.setOrder(Integer.valueOf(2));
                         e.addTemplateResolver(second);
                     }))) {
            Assertions.assertEquals("Hello, World!", text(get(server, "/hello").body, "hello"));
            Assertions.assertEquals("from second resolver", text(get(server, "/second").body, "second"));
            final HttpTestClient.Result missing = get(server, "/missing");
            Assertions.assertEquals(500, missing.status, missing.body);
            Assertions.assertTrue(missing.body.contains("Error resolving template [does-not-exist]"), missing.body);
        }
    }

}

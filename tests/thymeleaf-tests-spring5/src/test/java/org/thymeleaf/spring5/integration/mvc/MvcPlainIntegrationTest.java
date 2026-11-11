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
package org.thymeleaf.spring5.integration.mvc;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.thymeleaf.spring5.integration.IntegrationTestUtils.text;

public class MvcPlainIntegrationTest extends AbstractMvcIntegrationTest {

    @Override
    protected Class<?> getConfigurationClass() {
        return MvcPlainConfiguration.class;
    }

    @Override
    protected Map<String, Object> getBaseProperties() {
        return Collections.emptyMap();
    }

    @Override
    protected Map<String, Object> getUseCodeAsDefaultMessageProperties() {
        return properties(MvcPlainConfiguration.USE_CODE_AS_DEFAULT_MESSAGE, "true");
    }

    @Override
    protected MockMvc createFailOnMissingMessageMockMvc() {
        return mockMvc(properties(MvcPlainConfiguration.FAIL_ON_MISSING_MESSAGE, "true"));
    }

    @Override
    protected Map<String, Object> getSpringELCompilerProperties() {
        return properties(MvcPlainConfiguration.ENABLE_SPRING_EL_COMPILER, "true");
    }

    @Override
    protected Map<String, Object> getHiddenMarkersBeforeCheckboxesProperties() {
        return properties(MvcPlainConfiguration.RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES, "true");
    }

    @Override
    protected Map<String, Object> getContentTypeProperties(final String contentType) {
        return properties(MvcPlainConfiguration.CONTENT_TYPE, contentType);
    }

    @Override
    protected Map<String, Object> getViewNamesProperties(final String viewNames, final String excludedViewNames) {
        final Map<String,Object> properties = properties();
        if (viewNames != null) {
            properties.put(MvcPlainConfiguration.VIEW_NAMES, viewNames);
        }
        if (excludedViewNames != null) {
            properties.put(MvcPlainConfiguration.EXCLUDED_VIEW_NAMES, excludedViewNames);
        }
        return properties;
    }

    @Override
    protected void assertNotResolvedByThymeleaf(final MockMvc mockMvc, final String uri) {
        // No other view resolver is configured
        final Exception e = Assertions.assertThrows(Exception.class, () -> perform(mockMvc, get(uri)));
        Assertions.assertTrue(e.getMessage().contains("Could not resolve view with name '" + uri.substring(1) + "'"), e.getMessage());
    }


    @Test
    public void testTemplateEngineMessageSource() throws Exception {

        final String html = render(mockMvc(properties(MvcPlainConfiguration.TEMPLATE_ENGINE_MESSAGE_SOURCE, "true")),
                get("/messages").header("Accept-Language", "en-US"));
        Assertions.assertEquals("Hi from the engine message source, Messages", text(html, "m1"));
        Assertions.assertEquals("??missing.key_en_US??", text(html, "m2"));
        Assertions.assertEquals("NULL", text(html, "m3"));

    }


    @Test
    public void testExplicitMessageResolver() throws Exception {

        // An explicitly set message resolver replaces the MessageSource-based one
        final String html = render(mockMvc(properties(MvcPlainConfiguration.STANDARD_MESSAGE_RESOLVER, "true")),
                get("/messages").header("Accept-Language", "en-US"));
        Assertions.assertEquals("??greeting_en_US??", text(html, "m1"));
        Assertions.assertEquals("From template", text(html, "m7"));

    }


    @Test
    public void testStaticVariables() throws Exception {

        final String html = render(mockMvc(properties(MvcPlainConfiguration.STATIC_VARIABLE, "static-value")), get("/hello"));
        Assertions.assertEquals("static-value", text(html, "static"));

    }


    @Test
    public void testRedirectAndForwardWithViewNames() throws Exception {

        // By default, redirects and forwards are processed even if not included in viewNames
        final MockMvc mockMvc = mockMvc(properties(MvcPlainConfiguration.VIEW_NAMES, "hello"));
        Assertions.assertEquals("/hello", perform(mockMvc, get("/redirect")).getRedirectedUrl());
        Assertions.assertEquals("/hello", perform(mockMvc, get("/forward")).getForwardedUrl());

        final MockMvc notProcessedMockMvc = mockMvc(properties(
                MvcPlainConfiguration.VIEW_NAMES, "hello", MvcPlainConfiguration.ALWAYS_PROCESS_REDIRECT_AND_FORWARD, "false"));
        final Exception e = Assertions.assertThrows(Exception.class, () -> perform(notProcessedMockMvc, get("/redirect")));
        Assertions.assertTrue(e.getMessage().contains("Could not resolve view with name 'redirect:/hello'"), e.getMessage());
        final MockMvc processedMockMvc = mockMvc(properties(
                MvcPlainConfiguration.VIEW_NAMES, "hello,redirect:*", MvcPlainConfiguration.ALWAYS_PROCESS_REDIRECT_AND_FORWARD, "false"));
        Assertions.assertEquals("/hello", perform(processedMockMvc, get("/redirect")).getRedirectedUrl());

    }


    @Test
    public void testWebApplicationTemplateLocation() throws Exception {

        // Templates resolved as resources of the web application (ServletContext), as in WAR deployments
        final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(createContext(
                new MockServletContext("classpath:integration/webapp"),
                properties(MvcPlainConfiguration.TEMPLATE_PREFIX, "/WEB-INF/templates/"))).build();
        Assertions.assertEquals("From the web application: World", text(render(mockMvc, get("/hello")), "greeting"));
        final Exception e = Assertions.assertThrows(Exception.class, () -> perform(mockMvc, get("/messages")));
        // Existence is not checked by default, so a missing template is reported when it is read
        Assertions.assertTrue(findCause(e, FileNotFoundException.class).getMessage().contains("/WEB-INF/templates/messages.html"));

    }

}

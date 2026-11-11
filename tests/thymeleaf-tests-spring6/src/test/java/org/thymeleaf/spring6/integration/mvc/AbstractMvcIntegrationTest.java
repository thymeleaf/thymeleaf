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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.exceptions.TemplateProcessingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.element;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.text;

public abstract class AbstractMvcIntegrationTest extends AbstractMvcTest {


    @Test
    public void testRender() throws Exception {

        final MockHttpServletResponse response =
                perform(mockMvc(), get("/app/hello").contextPath("/app").header("Accept-Language", "en-US"));

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("text/html;charset=UTF-8", response.getContentType());
        final String html = response.getContentAsString();
        Assertions.assertEquals("Hello, World!", text(html, "greeting"));
        Assertions.assertEquals("en_US", text(html, "locale"));
        Assertions.assertEquals("[abc]", text(html, "code"));
        Assertions.assertEquals("bean-value", text(html, "bean"));
        Assertions.assertEquals("<a id=\"link\" href=\"/app/form?p=World\">link</a>", element(html, "link"));
        Assertions.assertEquals("<a id=\"marked\" href=\"/app/processed\">marked</a>", element(html, "marked"));
        Assertions.assertTrue(html.contains("<ul><li>one</li><li>two</li></ul>"), html);
        Assertions.assertEquals("none", text(html, "static"));
        Assertions.assertFalse(html.contains("th:"), html);

    }


    @Test
    public void testRenderLocale() throws Exception {

        final MockMvc mockMvc = mockMvc();
        final String es = render(mockMvc, get("/hello").header("Accept-Language", "es-ES"));
        Assertions.assertEquals("Hola, World", text(es, "greeting"));
        Assertions.assertEquals("es_ES", text(es, "locale"));
        final String fr = render(mockMvc, get("/hello").header("Accept-Language", "fr-FR"));
        Assertions.assertEquals("Hello, World!", text(fr, "greeting"));
        Assertions.assertEquals("fr_FR", text(fr, "locale"));

    }


    @Test
    public void testMessages() throws Exception {

        final String html = render(mockMvc(), get("/messages").header("Accept-Language", "en-US"));
        Assertions.assertEquals("Hello, Messages!", text(html, "m1"));
        Assertions.assertEquals("??missing.key_en_US??", text(html, "m2"));
        Assertions.assertEquals("NULL", text(html, "m3"));
        Assertions.assertEquals("Hello, X!", text(html, "m4"));
        Assertions.assertEquals("??missing.key_en_US??", text(html, "m5"));
        Assertions.assertEquals("Hello, Y!", text(html, "m6"));
        // Template-based message files (next to the template) are not used in Spring: only the MessageSource is
        Assertions.assertEquals("??origin.key_en_US??", text(html, "m7"));
        Assertions.assertEquals("<b>bold</b>", text(html, "m8"));

    }


    @Test
    public void testMessagesUseCodeAsDefaultMessage() throws Exception {

        final String html = render(mockMvc(getUseCodeAsDefaultMessageProperties()), get("/messages").header("Accept-Language", "en-US"));
        Assertions.assertEquals("Hello, Messages!", text(html, "m1"));
        Assertions.assertEquals("missing.key", text(html, "m2"));
        Assertions.assertEquals("missing.key", text(html, "m3"));
        Assertions.assertEquals("missing.key", text(html, "m5"));
        Assertions.assertEquals("origin.key", text(html, "m7"));

    }

    protected abstract Map<String,Object> getUseCodeAsDefaultMessageProperties();


    @Test
    public void testFailOnMissingMessage() throws Exception {

        final MockMvc mockMvc = createFailOnMissingMessageMockMvc();
        final Exception e = Assertions.assertThrows(Exception.class,
                () -> perform(mockMvc, get("/messages").header("Accept-Language", "en-US")));
        final Throwable cause = findCause(e, TemplateProcessingException.class);
        Assertions.assertTrue(
                cause.getMessage().contains("Could not resolve message with key \"missing.key\" for locale \"en_US\""),
                cause.getMessage());
        // Templates with no missing messages are not affected
        Assertions.assertEquals("Hello, World!", text(render(mockMvc, get("/hello").header("Accept-Language", "en-US")), "greeting"));

    }

    protected abstract MockMvc createFailOnMissingMessageMockMvc();


    @Test
    public void testForm() throws Exception {

        final String html = render(mockMvc(), get("/form").header("Accept-Language", "en-US"));
        Assertions.assertEquals(
                "<form id=\"f\" action=\"/form?rdvp=post\" method=\"post\">" +
                "<input type=\"hidden\" name=\"_extra\" value=\"extra-value\"/>",
                html.substring(html.indexOf("<form"), html.indexOf("<input type=\"text\"")).replace("\n", ""));
        Assertions.assertTrue(html.contains("<input type=\"text\" id=\"name\" name=\"name\" value=\"Ann\">"), html);
        Assertions.assertTrue(html.contains("<input type=\"text\" id=\"age\" name=\"age\" value=\"30\">"), html);
        Assertions.assertTrue(html.contains("<input type=\"text\" id=\"code\" name=\"code\" value=\"[abc]\">"), html);
        Assertions.assertTrue(html.contains(
                "<input type=\"checkbox\" id=\"active1\" name=\"active\" value=\"true\" checked=\"checked\">" +
                "<input type=\"hidden\" name=\"_active\" value=\"on\"/>"), html);
        Assertions.assertTrue(html.contains(
                "<select id=\"color\" name=\"color\">" +
                "<option value=\"RED\">red</option>" +
                "<option value=\"GREEN\" selected=\"selected\">green</option>" +
                "<option value=\"BLUE\">blue</option></select>"), html);
        Assertions.assertEquals("false", text(html, "haserrors"));
        Assertions.assertEquals("<ul id=\"errors\"></ul>", element(html, "errors"));
        Assertions.assertFalse(html.contains("ageerrors"), html);
        Assertions.assertFalse(html.contains("nameerrors"), html);
        Assertions.assertEquals("items0.name", text(html, "idfromname"));

    }


    @Test
    public void testFormBindingErrors() throws Exception {

        final String html = render(mockMvc(),
                post("/form").header("Accept-Language", "en-US")
                        .param("name", "").param("age", "abc").param("code", "bad")
                        .param("_active", "on").param("color", "blue"));
        Assertions.assertTrue(html.contains("<input type=\"text\" id=\"name\" name=\"name\" value=\"\">"), html);
        Assertions.assertTrue(html.contains("<input type=\"text\" id=\"age\" name=\"age\" value=\"abc\">"), html);
        Assertions.assertTrue(html.contains("<input type=\"text\" id=\"code\" name=\"code\" value=\"bad\">"), html);
        Assertions.assertTrue(html.contains("<input type=\"checkbox\" id=\"active1\" name=\"active\" value=\"true\">"), html);
        Assertions.assertTrue(html.contains("<option value=\"BLUE\" selected=\"selected\">blue</option>"), html);
        Assertions.assertEquals("true", text(html, "haserrors"));
        Assertions.assertEquals(
                "<ul id=\"errors\">" +
                "<li>age:typeMismatch:Age must be a number</li>" +
                "<li>code:typeMismatch:Invalid code</li>" +
                "<li>name:required:Required</li></ul>",
                element(html, "errors"));
        Assertions.assertEquals("<p id=\"ageerrors\">Age must be a number</p>", element(html, "ageerrors"));
        Assertions.assertEquals("<p id=\"nameerrors\" class=\"e err\">Required</p>", element(html, "nameerrors"));

    }


    @Test
    public void testFormSubmitted() throws Exception {

        final MockHttpServletResponse response = perform(mockMvc(),
                post("/form").param("name", "Bob").param("age", "40").param("code", "[xyz]").param("color", "red"));
        Assertions.assertEquals(302, response.getStatus());
        Assertions.assertEquals("/form?saved=Bob", response.getRedirectedUrl());

    }


    @Test
    public void testRedirectAndForward() throws Exception {

        final MockMvc mockMvc = mockMvc();
        final MockHttpServletResponse redirect = perform(mockMvc, get("/app/redirect").contextPath("/app"));
        Assertions.assertEquals(302, redirect.getStatus());
        Assertions.assertEquals("/app/hello", redirect.getRedirectedUrl());
        final MockHttpServletResponse forward = perform(mockMvc, get("/app/forward").contextPath("/app"));
        Assertions.assertEquals(200, forward.getStatus());
        Assertions.assertEquals("/hello", forward.getForwardedUrl());

    }


    @Test
    public void testFragmentViewNames() throws Exception {

        final MockMvc mockMvc = mockMvc();
        for (final String path : new String[] { "/fragment", "/fragment-expression", "/fragment-selector?f=main" }) {
            final String html = render(mockMvc, get(path));
            Assertions.assertEquals("<div><p id=\"frag\">Fragment F</p></div>", html.trim(), path);
        }

    }


    @Test
    public void testFragmentViewNameFromRequest() throws Exception {

        final MockMvc mockMvc = mockMvc();
        final Exception e = Assertions.assertThrows(Exception.class,
                () -> perform(mockMvc, get("/fragment-selector").param("f", "${fragName}")));
        final Throwable cause = findCause(e, TemplateProcessingException.class);
        Assertions.assertTrue(cause.getMessage().contains("View name contains an expression"), cause.getMessage());

    }


    @Test
    public void testWebAttributes() throws Exception {

        final String html = render(mockMvc(), get("/app/attributes").contextPath("/app").param("p", "param-value"));
        Assertions.assertEquals("session-value", text(html, "session"));
        Assertions.assertEquals("param-value", text(html, "param"));
        Assertions.assertEquals("request-value", text(html, "request"));
        Assertions.assertEquals("/app/hello", text(html, "url"));

    }


    protected abstract Map<String,Object> getSpringELCompilerProperties();

    protected abstract Map<String,Object> getHiddenMarkersBeforeCheckboxesProperties();

    protected abstract Map<String,Object> getContentTypeProperties(String contentType);

    protected abstract Map<String,Object> getViewNamesProperties(String viewNames, String excludedViewNames);

    protected abstract void assertNotResolvedByThymeleaf(MockMvc mockMvc, String uri) throws Exception;


    @Test
    public void testSpringELCompiler() throws Exception {

        final MockMvc interpreted = mockMvc();
        final MockMvc compiled = mockMvc(getSpringELCompilerProperties());
        for (final String uri : new String[] { "/hello", "/messages", "/form", "/fragment-expression", "/attributes?p=x" }) {
            final String expected = render(interpreted, get(uri).header("Accept-Language", "en-US"));
            // Repeated executions, so that expressions are evaluated again once compiled
            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(expected, render(compiled, get(uri).header("Accept-Language", "en-US")), uri);
            }
        }
        final String expectedErrors = render(interpreted, post("/form").param("age", "abc").param("code", "bad"));
        for (int i = 0; i < 3; i++) {
            Assertions.assertEquals(expectedErrors, render(compiled, post("/form").param("age", "abc").param("code", "bad")));
        }

    }


    @Test
    public void testHiddenMarkersBeforeCheckboxes() throws Exception {

        final String html = render(mockMvc(getHiddenMarkersBeforeCheckboxesProperties()), get("/form"));
        Assertions.assertTrue(html.contains(
                "<input type=\"hidden\" name=\"_active\" value=\"on\"/>" +
                "<input type=\"checkbox\" id=\"active1\" name=\"active\" value=\"true\" checked=\"checked\">"), html);

    }


    @Test
    public void testContentType() throws Exception {

        final MockHttpServletResponse response =
                perform(mockMvc(getContentTypeProperties("application/xhtml+xml")), get("/hello"));
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("application/xhtml+xml;charset=UTF-8", response.getContentType());

    }


    @Test
    public void testViewNames() throws Exception {

        final MockMvc mockMvc = mockMvc(getViewNamesProperties("hello,fragments*", null));
        Assertions.assertEquals("Hello, World!", text(render(mockMvc, get("/hello").header("Accept-Language", "en-US")), "greeting"));
        Assertions.assertEquals("Fragment F", text(render(mockMvc, get("/fragment")), "frag"));
        assertNotResolvedByThymeleaf(mockMvc, "/messages");

        final MockMvc excludedMockMvc = mockMvc(getViewNamesProperties(null, "mess*"));
        Assertions.assertEquals("Hello, World!", text(render(excludedMockMvc, get("/hello").header("Accept-Language", "en-US")), "greeting"));
        assertNotResolvedByThymeleaf(excludedMockMvc, "/messages");

    }

}

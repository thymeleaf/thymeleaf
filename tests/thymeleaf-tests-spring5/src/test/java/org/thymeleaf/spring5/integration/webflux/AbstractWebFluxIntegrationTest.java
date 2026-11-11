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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.thymeleaf.spring5.integration.IntegrationTestUtils.element;
import static org.thymeleaf.spring5.integration.IntegrationTestUtils.text;

public abstract class AbstractWebFluxIntegrationTest extends AbstractWebFluxTest {


    protected abstract Map<String,Object> getUseCodeAsDefaultMessageProperties();

    protected abstract Map<String,Object> getMaxChunkSizeProperties(int maxChunkSizeBytes);

    protected abstract WebTestClient createFailOnMissingMessageClient();




    @Test
    public void testRender() {

        final EntityExchangeResult<String> result = get(client(), "/hello", "en-US");
        Assertions.assertEquals(200, result.getStatus().value());
        // The default media types of ThymeleafReactiveViewResolver include no charset (output is UTF-8 anyway)
        Assertions.assertEquals(MediaType.TEXT_HTML, result.getResponseHeaders().getContentType());
        final String html = result.getResponseBody();
        Assertions.assertEquals("Hello, World!", text(html, "greeting"));
        Assertions.assertEquals("en_US", text(html, "locale"));
        Assertions.assertEquals("[abc]", text(html, "code"));
        Assertions.assertEquals("bean-value", text(html, "bean"));
        Assertions.assertEquals("<a id=\"link\" href=\"/form?p=World\">link</a>", element(html, "link"));
        Assertions.assertEquals("<a id=\"marked\" href=\"/processed\">marked</a>", element(html, "marked"));
        Assertions.assertTrue(html.contains("<ul><li>one</li><li>two</li></ul>"), html);
        Assertions.assertEquals("none", text(html, "static"));
        Assertions.assertFalse(html.contains("th:"), html);

    }


    @Test
    public void testRenderAsyncModelAttributes() {

        final WebTestClient client = client();
        Assertions.assertEquals(render(client, "/hello"), render(client, "/hello-async"));

    }


    @Test
    public void testRenderLocale() {

        final WebTestClient client = client();
        final String es = get(client, "/hello", "es-ES").getResponseBody();
        Assertions.assertEquals("Hola, World", text(es, "greeting"));
        Assertions.assertEquals("es_ES", text(es, "locale"));
        final String fr = get(client, "/hello", "fr-FR").getResponseBody();
        Assertions.assertEquals("Hello, World!", text(fr, "greeting"));
        Assertions.assertEquals("fr_FR", text(fr, "locale"));

    }


    @Test
    public void testMessages() {

        final String html = render(client(), "/messages");
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
    public void testMessagesUseCodeAsDefaultMessage() {

        final String html = render(client(getUseCodeAsDefaultMessageProperties()), "/messages");
        Assertions.assertEquals("Hello, Messages!", text(html, "m1"));
        Assertions.assertEquals("missing.key", text(html, "m2"));
        Assertions.assertEquals("missing.key", text(html, "m3"));
        Assertions.assertEquals("missing.key", text(html, "m5"));
        Assertions.assertEquals("origin.key", text(html, "m7"));

    }


    @Test
    public void testFailOnMissingMessage() {

        final WebTestClient client = createFailOnMissingMessageClient();
        assertFailure(get(client, "/messages", "en-US"),
                "Could not resolve message with key \"missing.key\" for locale \"en_US\"");
        // Templates with no missing messages are not affected
        Assertions.assertEquals("Hello, World!", text(render(client, "/hello"), "greeting"));

    }


    @Test
    public void testForm() {

        final String html = render(client(), "/form");
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


    private static EntityExchangeResult<String> postForm(final WebTestClient client, final String... namesAndValues) {
        final MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        for (int i = 0; i < namesAndValues.length; i += 2) {
            formData.add(namesAndValues[i], namesAndValues[i + 1]);
        }
        return client.post().uri("/form").header(HttpHeaders.ACCEPT_LANGUAGE, "en-US")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(formData))
                .exchange().expectBody(String.class).returnResult();
    }


    @Test
    public void testFormBindingErrors() {

        final EntityExchangeResult<String> result =
                postForm(client(), "name", "", "age", "abc", "code", "bad", "_active", "on", "color", "blue");
        Assertions.assertEquals(200, result.getStatus().value(), result.getResponseBody());
        final String html = result.getResponseBody();
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
    public void testFormSubmitted() {

        final EntityExchangeResult<String> result =
                postForm(client(), "name", "Bob", "age", "40", "code", "[xyz]", "color", "red");
        Assertions.assertEquals(303, result.getStatus().value());
        Assertions.assertEquals("/form?saved=Bob", result.getResponseHeaders().getLocation().toString());

    }


    @Test
    public void testRedirect() {

        final EntityExchangeResult<String> result = get(client(), "/redirect", "en-US");
        Assertions.assertEquals(303, result.getStatus().value());
        Assertions.assertEquals("/hello", result.getResponseHeaders().getLocation().toString());

    }


    @Test
    public void testFragmentViewNames() {

        final WebTestClient client = client();
        for (final String path : new String[] { "/fragment", "/fragment-expression", "/fragment-selector?f=main" }) {
            Assertions.assertEquals("<div><p id=\"frag\">Fragment F</p></div>", render(client, path).trim(), path);
        }

    }


    @Test
    public void testFragmentViewNameFromRequest() {

        final WebTestClient client = client();
        final EntityExchangeResult<String> result = client.get()
                .uri(builder -> builder.path("/fragment-selector").queryParam("f", "{f}").build("${fragName}"))
                .exchange().expectBody(String.class).returnResult();
        assertFailure(result, "View name contains an expression");

    }


    @Test
    public void testFragmentViewNameFromRequestHeader() {

        final WebTestClient client = client();
        final EntityExchangeResult<String> refused = client.get().uri("/fragment-expression")
                .header("X-Test", "first", "${fragName}").exchange().expectBody(String.class).returnResult();
        assertFailure(refused, "View name contains an expression");
        final EntityExchangeResult<String> allowed = client.get().uri("/fragment-expression")
                .header("X-Test", "${other}").exchange().expectBody(String.class).returnResult();
        Assertions.assertEquals(200, allowed.getStatus().value(), allowed.getResponseBody());

    }


    @Test
    public void testWebAttributes() {

        final String html = render(client(), "/attributes?p=param-value");
        Assertions.assertEquals("session-value", text(html, "session"));
        Assertions.assertEquals("param-value", text(html, "param"));
        Assertions.assertEquals("request-value", text(html, "request"));

    }


    @Test
    public void testDataDriver() {

        final WebTestClient client = client();
        final String html = render(client, "/datadriver");
        Assertions.assertTrue(html.contains(
                "<ul><li>0:item1</li><li>1:item2</li><li>2:item3</li><li>3:item4</li><li>4:item5</li></ul>"), html);
        Assertions.assertEquals("end", text(html, "end"));
        Assertions.assertTrue(render(client, "/datadriver?n=0").contains("<ul></ul>"));
        // Output is sent as the data stream produces it, one chunk per buffer of elements
        final List<String> chunks = renderChunks(client, "/datadriver");
        Assertions.assertTrue(chunks.size() >= 3, chunks.toString());
        Assertions.assertEquals(html, String.join("", chunks));

    }


    @Test
    public void testChunkedMode() {

        final List<String> full = renderChunks(client(), "/hello");
        Assertions.assertEquals(1, full.size(), full.toString());
        final List<String> chunked = renderChunks(client(getMaxChunkSizeProperties(64)), "/hello");
        Assertions.assertTrue(chunked.size() > 1, chunked.toString());
        for (final String chunk : chunked) {
            Assertions.assertTrue(chunk.getBytes(StandardCharsets.UTF_8).length <= 64, chunk);
        }
        Assertions.assertEquals(full.get(0), String.join("", chunked));

    }


    @Test
    public void testServerSentEvents() {

        final EntityExchangeResult<String> result = client().get().uri("/datadriver?n=3")
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en-US").accept(MediaType.TEXT_EVENT_STREAM)
                .exchange().expectBody(String.class).returnResult();
        Assertions.assertEquals(200, result.getStatus().value());
        Assertions.assertTrue(MediaType.TEXT_EVENT_STREAM.isCompatibleWith(result.getResponseHeaders().getContentType()));
        Assertions.assertEquals(
                "event: head\nid: 0\ndata: <!DOCTYPE html>\ndata: <html>\ndata: <body>\ndata: <ul>\n\n" +
                "event: message\nid: 1\ndata: <li>0:item1</li>\n\n" +
                "event: message\nid: 2\ndata: <li>1:item2</li>\n\n" +
                "event: message\nid: 3\ndata: <li>2:item3</li>\n\n" +
                "event: tail\nid: 4\ndata: </ul>\ndata: <p id=\"end\">end</p>\ndata: </body>\ndata: </html>\ndata: \n\n",
                result.getResponseBody());

    }


    protected abstract Map<String,Object> getSpringELCompilerProperties();

    protected abstract Map<String,Object> getHiddenMarkersBeforeCheckboxesProperties();

    protected abstract Map<String,Object> getMediaTypesProperties(String mediaTypes);

    protected abstract Map<String,Object> getViewNamesProperties(String viewNames, String excludedViewNames);

    protected abstract Map<String,Object> getRenderModeViewNamesProperties(
            int maxChunkSizeBytes, String fullModeViewNames, String chunkedModeViewNames);


    @Test
    public void testSpringELCompiler() {

        final WebTestClient interpreted = client();
        final WebTestClient compiled = client(getSpringELCompilerProperties());
        for (final String uri : new String[] { "/hello", "/messages", "/form", "/fragment-expression", "/attributes?p=x", "/datadriver" }) {
            final String expected = render(interpreted, uri);
            // Repeated executions, so that expressions are evaluated again once compiled
            for (int i = 0; i < 3; i++) {
                Assertions.assertEquals(expected, render(compiled, uri), uri);
            }
        }

    }


    @Test
    public void testHiddenMarkersBeforeCheckboxes() {

        final String html = render(client(getHiddenMarkersBeforeCheckboxesProperties()), "/form");
        Assertions.assertTrue(html.contains(
                "<input type=\"hidden\" name=\"_active\" value=\"on\"/>" +
                "<input type=\"checkbox\" id=\"active1\" name=\"active\" value=\"true\" checked=\"checked\">"), html);

    }


    @Test
    public void testMediaTypes() {

        final EntityExchangeResult<String> result =
                get(client(getMediaTypesProperties("text/html;charset=UTF-8")), "/hello", "en-US");
        Assertions.assertEquals(200, result.getStatus().value());
        Assertions.assertEquals(MediaType.parseMediaType("text/html;charset=UTF-8"), result.getResponseHeaders().getContentType());

    }


    @Test
    public void testViewNames() {

        final WebTestClient client = client(getViewNamesProperties("hello,fragments*", null));
        Assertions.assertEquals("Hello, World!", text(render(client, "/hello"), "greeting"));
        Assertions.assertEquals("Fragment F", text(render(client, "/fragment"), "frag"));
        assertFailure(get(client, "/messages", "en-US"), "Could not resolve view with name 'messages'");

        final WebTestClient excludedClient = client(getViewNamesProperties(null, "mess*"));
        Assertions.assertEquals("Hello, World!", text(render(excludedClient, "/hello"), "greeting"));
        assertFailure(get(excludedClient, "/messages", "en-US"), "Could not resolve view with name 'messages'");

    }


    @Test
    public void testRenderModeViewNames() {

        final WebTestClient fullClient = client(getRenderModeViewNamesProperties(64, "hello", null));
        Assertions.assertEquals(1, renderChunks(fullClient, "/hello").size());
        Assertions.assertTrue(renderChunks(fullClient, "/messages").size() > 1);

        final WebTestClient chunkedClient = client(getRenderModeViewNamesProperties(64, null, "messages"));
        Assertions.assertEquals(1, renderChunks(chunkedClient, "/hello").size());
        Assertions.assertTrue(renderChunks(chunkedClient, "/messages").size() > 1);

    }

}

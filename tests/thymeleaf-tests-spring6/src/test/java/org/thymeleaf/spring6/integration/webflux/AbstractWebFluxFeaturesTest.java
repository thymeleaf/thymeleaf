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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import static org.thymeleaf.spring6.integration.IntegrationTestUtils.element;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.tag;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.text;

public abstract class AbstractWebFluxFeaturesTest extends AbstractWebFluxTest {


    // Context with additional template resolvers for TEXT and JAVASCRIPT templates, if the setup needs them
    protected abstract WebTestClient createTemplateModesClient();

    protected abstract WebTestClient createDecoupledLogicClient();


    private static EntityExchangeResult<String> postForm(final WebTestClient client, final String uri, final String... namesAndValues) {
        final MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        for (int i = 0; i < namesAndValues.length; i += 2) {
            formData.add(namesAndValues[i], namesAndValues[i + 1]);
        }
        return client.post().uri(uri).header(HttpHeaders.ACCEPT_LANGUAGE, "en-US")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(BodyInserters.fromFormData(formData))
                .exchange().expectBody(String.class).returnResult();
    }




    @Test
    public void testLinks() {

        final String html = render(client(), "/app/links");
        Assertions.assertEquals("<a id=\"context\" href=\"/app/x\">", tag(html, "context"));
        Assertions.assertEquals("<a id=\"page\" href=\"x/y\">", tag(html, "page"));
        Assertions.assertEquals("<a id=\"server\" href=\"/other/x\">", tag(html, "server"));
        Assertions.assertEquals("<a id=\"absolute\" href=\"http://example.test/a?q=a%20b%26c\">", tag(html, "absolute"));
        Assertions.assertEquals("<a id=\"protocol\" href=\"//cdn.example.test/lib.js\">", tag(html, "protocol"));
        Assertions.assertEquals("<a id=\"pathvar\" href=\"/app/o/12/d?q=a%20b%26c\">", tag(html, "pathvar"));
        Assertions.assertEquals("<a id=\"pathvarescaped\" href=\"/app/o/a%20b&amp;c\">", tag(html, "pathvarescaped"));
        Assertions.assertEquals("<a id=\"anchor\" href=\"/app/x?p=1#sec\">", tag(html, "anchor"));
        Assertions.assertEquals("<a id=\"dynamic\" href=\"/app/dyn/path?p=1\">", tag(html, "dynamic"));
        Assertions.assertEquals("<a id=\"multi\" href=\"/app/x?a=1&amp;a=2&amp;b=&amp;c=1&amp;c=2\">", tag(html, "multi"));
        // th:action and th:formaction go through processAction (as Spring's form tag does), th:href and th:src
        // through processUrl
        Assertions.assertEquals("<img id=\"img\" src=\"/app/img/processed.png\">", tag(html, "img"));
        Assertions.assertEquals("<form id=\"form\" action=\"/app/marked?rdvp=post\" method=\"post\">", tag(html, "form"));
        Assertions.assertEquals("<button id=\"formaction\" formaction=\"/app/marked/other?rdvp=GET\">", tag(html, "formaction"));
        Assertions.assertEquals("<button id=\"formactionpost\" formmethod=\"post\" formaction=\"/app/marked/other?rdvp=post\">", tag(html, "formactionpost"));
        Assertions.assertEquals("<button id=\"formactionempty\">", tag(html, "formactionempty"));
        Assertions.assertEquals("<button id=\"formactionthmethod1\" formmethod=\"post\" formaction=\"/app/marked/other?rdvp=post\">", tag(html, "formactionthmethod1"));
        Assertions.assertEquals("<button id=\"formactionthmethod2\" formaction=\"/app/marked/other?rdvp=post\" formmethod=\"post\">", tag(html, "formactionthmethod2"));
        Assertions.assertEquals("/app/marked?p=a%20b%26c", text(html, "inlined"));
        Assertions.assertEquals("/app/marked", text(html, "text"));
        Assertions.assertEquals("var u = \"\\/app\\/marked?p=a%20b%26c\";", text(html, "js"));
        Assertions.assertEquals("a%20b%2Fc", text(html, "pathsegment"));
        Assertions.assertEquals("a%20b%26c%3Dd", text(html, "queryparam"));

    }


    @Test
    public void testLinksTransformedByExchange() {

        // All links built by link expressions are passed to the exchange's URL transformers
        final String html = client().get().uri("/app/links").header(TestWebFilter.TRANSFORM_HEADER, "true")
                .exchange().expectBody(String.class).returnResult().getResponseBody();
        for (final String id : new String[] {
                "context", "page", "server", "absolute", "protocol", "pathvar", "anchor", "dynamic", "multi" }) {
            Assertions.assertTrue(tag(html, id).endsWith("~tx\">"), tag(html, id));
        }
        Assertions.assertEquals("<a id=\"anchor\" href=\"/app/x?p=1#sec~tx\">", tag(html, "anchor"));
        Assertions.assertEquals("<img id=\"img\" src=\"/app/img/processed.png~tx\">", tag(html, "img"));
        Assertions.assertEquals("/app/marked?p=a%20b%26c~tx", text(html, "inlined"));

    }


    @Test
    public void testLinkParametersInRestrictedContexts() {

        final WebTestClient client = client();
        // Link parameters in th:href are allowed to access request parameters...
        Assertions.assertEquals("<a id=\"p\" href=\"/x?p=v\">", tag(render(client, "/links-href-param?p=v"), "p"));
        // ...but not when the link expression is used in a restricted attribute
        assertFailure(get(client, "/links-utext-param?p=v", "en-US"),
                "access to static classes or parameters is forbidden in this context");

    }


    @Test
    public void testLayout() {

        final WebTestClient client = client();
        Assertions.assertEquals(
                "<!DOCTYPE html>\n<html>\n<head>\n<title>Page title</title>\n</head>\n<body>\n" +
                "<header><h1>Site</h1></header>\n" +
                "<main><p>Layout content</p></main>\n" +
                "<aside><p>default aside</p></aside>\n" +
                "<footer><p>Hello, footer!</p></footer>\n" +
                "</body>\n</html>\n",
                render(client, "/layout-page"));
        final String empty = render(client, "/layout-page-empty");
        Assertions.assertFalse(empty.contains("aside"), empty);
        Assertions.assertTrue(empty.contains("<main><p>Layout content</p></main>"), empty);

    }


    @Test
    public void testFragmentSelectors() {

        final String html = render(client(), "/fragments-misc");
        Assertions.assertEquals("<section id=\"s1\"><div><p>plain</p></div></section>", element(html, "s1"));
        Assertions.assertEquals("<section id=\"s2\"><div id=\"byid\"><p>by id</p></div></section>", element(html, "s2"));
        Assertions.assertEquals("<section id=\"s3\"><div class=\"byclass\"><p>by class</p></div></section>", element(html, "s3"));
        Assertions.assertEquals("<section id=\"s4\"><div><span>local</span></div></section>", element(html, "s4"));
        Assertions.assertEquals("<section id=\"s5\"><div><span>local</span></div></section>", element(html, "s5"));
        Assertions.assertEquals("<section id=\"s6\"><span>snippet N</span>\n</section>", element(html, "s6"));
        Assertions.assertEquals("<section id=\"s7\"><div><p>plain</p></div></section>", element(html, "s7"));
        Assertions.assertEquals("<section id=\"s8\"><div><p>plain</p></div></section>", element(html, "s8"));
        Assertions.assertEquals("<section id=\"s9\"><div><p>plain</p></div></section>", element(html, "s9"));
        Assertions.assertEquals("<section id=\"s10\"></section>", element(html, "s10"));
        Assertions.assertEquals("<section id=\"s11\"><p>kept</p></section>", element(html, "s11"));
        Assertions.assertEquals("<section id=\"s12\"><header><h1>N</h1></header></section>", element(html, "s12"));

    }


    @Test
    public void testFragmentViewNames() {

        final WebTestClient client = client();
        Assertions.assertEquals("<header><h1>Param title</h1></header>", render(client, "/fragment-params").trim());
        Assertions.assertEquals("<main><p>Section</p></main>", render(client, "/fragment-section").trim());
        // Different fragments of the same template, alternating (template cache)
        for (int i = 0; i < 3; i++) {
            Assertions.assertEquals("<div><p>plain</p></div>", render(client, "/fragment-select?which=a").trim());
            Assertions.assertEquals("<div id=\"byid\"><p>by id</p></div>", render(client, "/fragment-select?which=b").trim());
        }

    }


    @Test
    public void testDecoupledLogic() {

        Assertions.assertEquals("static", text(render(client(), "/decoupled"), "d"));
        Assertions.assertEquals("From decoupled logic", text(render(createDecoupledLogicClient(), "/decoupled"), "d"));

    }


    @Test
    public void testCollectionsForm() {

        final WebTestClient client = client();
        final String html = render(client, "/order");
        Assertions.assertTrue(html.contains(
                "<div><input type=\"text\" id=\"items0.name\" name=\"items[0].name\" value=\"pen\">" +
                "<input type=\"text\" id=\"items0.quantity\" name=\"items[0].quantity\" value=\"2\"></div>\n" +
                "<div><input type=\"text\" id=\"items1.name\" name=\"items[1].name\" value=\"ink\">" +
                "<input type=\"text\" id=\"items1.quantity\" name=\"items[1].quantity\" value=\"1\"></div>"), html);
        Assertions.assertTrue(html.contains(
                "<span><input type=\"checkbox\" value=\"fast\" id=\"features1\" name=\"features\" checked=\"checked\">" +
                "<input type=\"hidden\" name=\"_features\" value=\"on\"/><label for=\"features1\">fast</label></span>" +
                "<span><input type=\"checkbox\" value=\"cheap\" id=\"features2\" name=\"features\">" +
                "<input type=\"hidden\" name=\"_features\" value=\"on\"/><label for=\"features2\">cheap</label></span>"), html);
        Assertions.assertTrue(html.contains(
                "<span><input type=\"radio\" value=\"S\" id=\"size1\" name=\"size\"></span>" +
                "<span><input type=\"radio\" value=\"M\" id=\"size2\" name=\"size\" checked=\"checked\"></span>"), html);
        Assertions.assertEquals("<ul id=\"allerrors\"></ul>", element(html, "allerrors"));
        Assertions.assertFalse(html.contains("globalerrors") || html.contains("itemerrors"), html);

        final EntityExchangeResult<String> errorsResult = postForm(client, "/order",
                "items[0].name", "", "items[0].quantity", "many", "items[1].name", "ink", "items[1].quantity", "3",
                "_features", "on", "size", "L");
        Assertions.assertEquals(200, errorsResult.getStatus().value(), errorsResult.getResponseBody());
        final String errors = errorsResult.getResponseBody();
        Assertions.assertTrue(errors.contains("value=\"many\""), errors);
        Assertions.assertTrue(errors.contains("<input type=\"radio\" value=\"L\" id=\"size3\" name=\"size\" checked=\"checked\">"), errors);
        Assertions.assertFalse(errors.contains("checked=\"checked\"><input type=\"hidden\" name=\"_features\""), errors);
        Assertions.assertEquals(
                "<ul id=\"allerrors\"><li>Quantity must be a number</li><li>Choose at least one feature</li><li>Required</li></ul>",
                element(errors, "allerrors"));
        Assertions.assertEquals(
                "<ul id=\"globalerrors\"><li>Choose at least one feature</li></ul>", element(errors, "globalerrors"));
        Assertions.assertEquals("<p id=\"itemerrors\">Quantity must be a number</p>", element(errors, "itemerrors"));

        final EntityExchangeResult<String> saved = postForm(client, "/order",
                "items[0].name", "pen", "items[0].quantity", "5", "features", "cheap", "features", "green", "size", "S");
        Assertions.assertEquals(303, saved.getStatus().value());
        Assertions.assertEquals("/order?saved", saved.getResponseHeaders().getLocation().toString());

    }


    @Test
    public void testSpringEL() {

        final String html = render(client(), "/spel");
        Assertions.assertEquals("[4, 5, 6]", text(html, "selection"));
        Assertions.assertEquals("[Ann, Bob]", text(html, "projection"));
        Assertions.assertEquals("3", text(html, "first"));
        Assertions.assertEquals("none", text(html, "safe"));
        Assertions.assertEquals("default", text(html, "elvis"));
        Assertions.assertEquals("ANN", text(html, "method"));
        Assertions.assertEquals("many", text(html, "ternary"));
        Assertions.assertEquals("43", text(html, "convert"));
        Assertions.assertEquals("v v", text(html, "map"));
        Assertions.assertEquals("6", text(html, "lists"));
        Assertions.assertEquals("2026-11-11", text(html, "temporals"));
        Assertions.assertEquals("BOB", text(html, "strings"));
        Assertions.assertEquals("21", text(html, "aggregates"));

    }


    @Test
    public void testTemplateModes() {

        final WebTestClient client = createTemplateModesClient();
        final EntityExchangeResult<String> text = client.get().uri("/report.txt").accept(MediaType.TEXT_PLAIN)
                .exchange().expectBody(String.class).returnResult();
        Assertions.assertEquals(200, text.getStatus().value(), text.getResponseBody());
        Assertions.assertEquals(MediaType.TEXT_PLAIN, text.getResponseHeaders().getContentType());
        Assertions.assertEquals("Hello World!\n- one\n- two\nDone.\n", text.getResponseBody());
        final MediaType javaScriptMediaType = MediaType.parseMediaType("application/javascript");
        final EntityExchangeResult<String> javascript = client.get().uri("/app.js").accept(javaScriptMediaType)
                .exchange().expectBody(String.class).returnResult();
        Assertions.assertEquals(200, javascript.getStatus().value(), javascript.getResponseBody());
        Assertions.assertEquals(javaScriptMediaType, javascript.getResponseHeaders().getContentType());
        Assertions.assertEquals("var name = \"World\";\nvar items = [\"one\",\"two\"];\n", javascript.getResponseBody());
        // Unlike in Spring MVC, the content type is not derived from the request path, only negotiated
        Assertions.assertEquals(MediaType.TEXT_HTML, get(client, "/report.txt", "en-US").getResponseHeaders().getContentType());
        // HTML templates are not affected
        Assertions.assertTrue(render(client, "/hello").contains("<ul><li>one</li><li>two</li></ul>"));

    }


    @Test
    public void testJavaScriptInlining() {

        final String html = render(client(), "/inline-js");
        Assertions.assertTrue(html.contains(
                "var person = {\"name\":\"Ann \\u003Cx\\u003E\",\"age\":30};\n" +
                "var date = \"2026-11-11\";\n" +
                "var text = \"a\\u003C\\/script\\u003Eb\\u0060\\u0024{c}\";\n"), html);

    }

}

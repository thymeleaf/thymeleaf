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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring6.integration.IntegrationModels;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.element;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.tag;
import static org.thymeleaf.spring6.integration.IntegrationTestUtils.text;

public abstract class AbstractMvcFeaturesTest extends AbstractMvcTest {


    // Context with additional template resolvers for TEXT and JAVASCRIPT templates, if the setup needs them
    protected abstract MockMvc createTemplateModesMockMvc();

    protected abstract MockMvc createDecoupledLogicMockMvc();


    private static String links(final MockMvc mockMvc) throws Exception {
        return render(mockMvc, get("/app/links").contextPath("/app"));
    }




    @Test
    public void testLinks() throws Exception {

        final String html = links(mockMvc());
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
        // RequestDataValueProcessor
        Assertions.assertEquals("<img id=\"img\" src=\"/app/img/processed.png\">", tag(html, "img"));
        // th:action and th:formaction go through processAction (as Spring's form tag does), th:href and th:src
        // through processUrl
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
    public void testLinksEncodedByResponse() throws Exception {

        // All links built by link expressions are passed to HttpServletResponse.encodeURL (e.g. session ids)
        final String html = render(mockMvc(Collections.emptyMap(), new EncodeUrlTestFilter()), get("/app/links").contextPath("/app"));
        for (final String id : new String[] {
                "context", "page", "server", "absolute", "protocol", "pathvar", "anchor", "dynamic", "multi" }) {
            Assertions.assertTrue(tag(html, id).endsWith("~enc\">"), tag(html, id));
        }
        Assertions.assertEquals("<a id=\"anchor\" href=\"/app/x?p=1#sec~enc\">", tag(html, "anchor"));
        Assertions.assertEquals("<img id=\"img\" src=\"/app/img/processed.png~enc\">", tag(html, "img"));
        Assertions.assertEquals("/app/marked?p=a%20b%26c~enc", text(html, "inlined"));

    }


    @Test
    public void testMvcUrlWithArguments() throws Exception {

        final String html = render(mockMvc(), get("/app/attributes").contextPath("/app"));
        Assertions.assertEquals("/app/links/a%20b", text(html, "urlarg"));

    }


    @Test
    public void testLinkParametersInRestrictedContexts() throws Exception {

        final MockMvc mockMvc = mockMvc();
        // Link parameters in th:href are allowed to access request parameters...
        Assertions.assertEquals("<a id=\"p\" href=\"/x?p=v\">", tag(render(mockMvc, get("/links-href-param").param("p", "v")), "p"));
        // ...but not when the link expression is used in a restricted attribute
        final Exception e = Assertions.assertThrows(Exception.class,
                () -> perform(mockMvc, get("/links-utext-param").param("p", "v")));
        Assertions.assertTrue(findCause(e, TemplateProcessingException.class).getMessage()
                .contains("access to static classes or parameters is forbidden in this context"), e.getMessage());

    }


    @Test
    public void testLayout() throws Exception {

        final MockMvc mockMvc = mockMvc();
        Assertions.assertEquals(
                "<!DOCTYPE html>\n<html>\n<head>\n<title>Page title</title>\n</head>\n<body>\n" +
                "<header><h1>Site</h1></header>\n" +
                "<main><p>Layout content</p></main>\n" +
                "<aside><p>default aside</p></aside>\n" +
                "<footer><p>Hello, footer!</p></footer>\n" +
                "</body>\n</html>\n",
                render(mockMvc, get("/layout-page").header("Accept-Language", "en-US")));
        final String empty = render(mockMvc, get("/layout-page-empty").header("Accept-Language", "en-US"));
        Assertions.assertFalse(empty.contains("aside"), empty);
        Assertions.assertTrue(empty.contains("<main><p>Layout content</p></main>"), empty);

    }


    @Test
    public void testFragmentSelectors() throws Exception {

        final String html = render(mockMvc(), get("/fragments-misc"));
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
    public void testFragmentViewNames() throws Exception {

        final MockMvc mockMvc = mockMvc();
        Assertions.assertEquals("<header><h1>Param title</h1></header>", render(mockMvc, get("/fragment-params")).trim());
        Assertions.assertEquals("<main><p>Section</p></main>", render(mockMvc, get("/fragment-section")).trim());
        // Different fragments of the same template, alternating (template cache)
        for (int i = 0; i < 3; i++) {
            Assertions.assertEquals("<div><p>plain</p></div>", render(mockMvc, get("/fragment-select").param("which", "a")).trim());
            Assertions.assertEquals("<div id=\"byid\"><p>by id</p></div>", render(mockMvc, get("/fragment-select").param("which", "b")).trim());
        }

    }


    @Test
    public void testDecoupledLogic() throws Exception {

        Assertions.assertEquals("static", text(render(mockMvc(), get("/decoupled")), "d"));
        Assertions.assertEquals("From decoupled logic", text(render(createDecoupledLogicMockMvc(), get("/decoupled")), "d"));

    }


    @Test
    public void testCollectionsForm() throws Exception {

        final MockMvc mockMvc = mockMvc();
        final String html = render(mockMvc, get("/order"));
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

        final String errors = render(mockMvc, post("/order").header("Accept-Language", "en-US")
                .param("items[0].name", "").param("items[0].quantity", "many")
                .param("items[1].name", "ink").param("items[1].quantity", "3")
                .param("_features", "on").param("size", "L"));
        Assertions.assertTrue(errors.contains("value=\"many\""), errors);
        Assertions.assertTrue(errors.contains("<input type=\"radio\" value=\"L\" id=\"size3\" name=\"size\" checked=\"checked\">"), errors);
        Assertions.assertFalse(errors.contains("checked=\"checked\"><input type=\"hidden\" name=\"_features\""), errors);
        Assertions.assertEquals(
                "<ul id=\"allerrors\"><li>Quantity must be a number</li><li>Choose at least one feature</li><li>Required</li></ul>",
                element(errors, "allerrors"));
        Assertions.assertEquals(
                "<ul id=\"globalerrors\"><li>Choose at least one feature</li></ul>", element(errors, "globalerrors"));
        Assertions.assertEquals("<p id=\"itemerrors\">Quantity must be a number</p>", element(errors, "itemerrors"));

        final MockHttpServletResponse saved = perform(mockMvc, post("/order")
                .param("items[0].name", "pen").param("items[0].quantity", "5").param("features", "cheap", "green").param("size", "S"));
        Assertions.assertEquals("/order?saved", saved.getRedirectedUrl());

    }


    @Test
    public void testSpringEL() throws Exception {

        final String html = render(mockMvc(), get("/spel"));
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
    public void testTemplateModes() throws Exception {

        final MockMvc mockMvc = createTemplateModesMockMvc();
        final MockHttpServletResponse text = perform(mockMvc, get("/report.txt"));
        Assertions.assertEquals(200, text.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", text.getContentType());
        Assertions.assertEquals("Hello World!\n- one\n- two\nDone.\n", text.getContentAsString());
        final MockHttpServletResponse javascript = perform(mockMvc, get("/app.js"));
        Assertions.assertEquals(200, javascript.getStatus());
        Assertions.assertEquals("application/javascript;charset=UTF-8", javascript.getContentType());
        Assertions.assertEquals("var name = \"World\";\nvar items = [\"one\",\"two\"];\n", javascript.getContentAsString());
        // HTML templates are not affected
        Assertions.assertTrue(render(mockMvc, get("/hello")).contains("<ul><li>one</li><li>two</li></ul>"));

    }


    @Test
    public void testJavaScriptInlining() throws Exception {

        final String html = render(mockMvc(), get("/inline-js"));
        Assertions.assertTrue(html.contains(
                "var person = {\"name\":\"Ann \\u003Cx\\u003E\",\"age\":30};\n" +
                "var date = \"2026-11-11\";\n" +
                "var text = \"a\\u003C\\/script\\u003Eb\\u0060\\u0024{c}\";\n"), html);

    }


    @Test
    public void testRenderingOutsideWebRequests() throws Exception {

        final AnnotationConfigWebApplicationContext applicationContext = createContext(Collections.emptyMap());
        final SpringTemplateEngine templateEngine = applicationContext.getBean(SpringTemplateEngine.class);
        final Map<String,Object> variables = new HashMap<>();
        variables.put("name", "Ann");
        variables.put("date", IntegrationModels.DATE);

        final String en = templateEngine.process("mail", new Context(Locale.US, variables));
        Assertions.assertEquals("Hello, Ann!", text(en, "greeting"));
        Assertions.assertEquals("11 November 2026", text(en, "date"));
        Assertions.assertEquals("<a id=\"link\" href=\"https://example.test/x?p=Ann\">", tag(en, "link"));
        final String es = templateEngine.process("mail", new Context(new Locale("es", "ES"), variables));
        Assertions.assertEquals("Hola, Ann", text(es, "greeting"));
        Assertions.assertEquals("11 noviembre 2026", text(es, "date"));

        // Beans are only available if an evaluation context is provided
        Assertions.assertThrows(TemplateProcessingException.class, () -> templateEngine.process("mail-beans", new Context(Locale.US)));
        final Context beansContext = new Context(Locale.US);
        beansContext.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(applicationContext, applicationContext.getBean(ConversionService.class)));
        Assertions.assertEquals("bean-value", text(templateEngine.process("mail-beans", beansContext), "bean"));

        // Context-relative links need a web context
        final TemplateProcessingException e = Assertions.assertThrows(TemplateProcessingException.class,
                () -> templateEngine.process("mail-link", new Context(Locale.US)));
        Assertions.assertTrue(e.getMessage().contains("cannot be context relative"), e.getMessage());

    }

}

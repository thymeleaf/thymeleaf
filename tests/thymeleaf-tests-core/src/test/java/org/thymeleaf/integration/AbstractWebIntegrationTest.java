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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.web.IWebApplication;

import static org.thymeleaf.integration.HttpTestClient.get;
import static org.thymeleaf.integration.HttpTestClient.post;
import static org.thymeleaf.integration.IntegrationTestUtils.element;
import static org.thymeleaf.integration.IntegrationTestUtils.tag;
import static org.thymeleaf.integration.IntegrationTestUtils.text;

/*
 * Requests sent over HTTP to an embedded Tomcat running a plain (non-Spring) Thymeleaf application, so that
 * the web application/exchange, template resolution, message resolution (StandardMessageResolver) and
 * OGNL evaluation are exercised the way applications use them.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractWebIntegrationTest {

    private EmbeddedTomcat server;


    protected abstract AbstractConfigurableTemplateResolver createTemplateResolver(IWebApplication webApplication);


    static void configureTemplateResolver(final AbstractConfigurableTemplateResolver templateResolver) {
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTextTemplateModePatterns(java.util.Collections.singleton("*.txt"));
        templateResolver.setJavaScriptTemplateModePatterns(java.util.Collections.singleton("*.js"));
    }


    private ITemplateEngine createTemplateEngine(final IWebApplication webApplication) {
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(createTemplateResolver(webApplication));
        return templateEngine;
    }


    @BeforeAll
    public void startServer() throws Exception {
        this.server = EmbeddedTomcat.start(this::createTemplateEngine);
    }


    @AfterAll
    public void stopServer() throws Exception {
        if (this.server != null) {
            this.server.close();
        }
    }


    protected EmbeddedTomcat server() {
        return this.server;
    }


    private String html(final String path, final String... headers) throws Exception {
        final HttpTestClient.Result result = get(this.server, path, headers);
        Assertions.assertEquals(200, result.status, result.body);
        return result.body;
    }


    private String error(final String path, final String... headers) throws Exception {
        final HttpTestClient.Result result = get(this.server, path, headers);
        Assertions.assertEquals(500, result.status, result.body);
        return result.body;
    }




    @Test
    public void testHello() throws Exception {
        final HttpTestClient.Result result = get(this.server, "/hello");
        Assertions.assertEquals(200, result.status);
        Assertions.assertEquals("text/html;charset=UTF-8", result.contentType);
        Assertions.assertEquals("Hello, World!", text(result.body, "hello"));
    }


    @Test
    public void testNotFoundRoute() throws Exception {
        Assertions.assertEquals(404, get(this.server, "/nothing-here").status);
    }


    @Test
    public void testMessagesFromTemplateFiles() throws Exception {
        final String html = html("/messages");
        Assertions.assertEquals("Hello, Ana!", text(html, "greeting"));
        Assertions.assertEquals("Page only", text(html, "pageonly"));
        Assertions.assertEquals("??missing.key_en??", text(html, "missing"));
        Assertions.assertEquals("true", text(html, "ornull"));
        Assertions.assertEquals("en", text(html, "locale"));
    }


    @Test
    public void testMessagesLocaleFromRequest() throws Exception {
        final String es = html("/messages", "Accept-Language", "es-ES,es;q=0.9");
        Assertions.assertEquals("\u00A1Hola, Ana!", text(es, "greeting"));
        Assertions.assertEquals("S\u00F3lo p\u00E1gina", text(es, "pageonly"));
        Assertions.assertEquals("es_ES", text(es, "locale"));
        final String de = html("/messages", "Accept-Language", "de");
        Assertions.assertEquals("Hello, Ana!", text(de, "greeting"));
        Assertions.assertEquals("??missing.key_de??", text(de, "missing"));
    }


    @Test
    public void testMessagesExplicitExchangeLocale() throws Exception {
        final String html = html("/locale/explicit", "Accept-Language", "es");
        Assertions.assertEquals("Ola, Ana!", text(html, "greeting"));
        Assertions.assertEquals("Page only", text(html, "pageonly"));
        Assertions.assertEquals("gl", text(html, "locale"));
    }


    @Test
    public void testMessagesInFragments() throws Exception {
        // Messages of the fragment's template are used, but the calling template's messages take precedence
        final String html = html("/messages");
        final String frag = element(html, "frag");
        Assertions.assertTrue(frag.contains("<p class=\"own\">Footer from fragment</p>"), frag);
        Assertions.assertTrue(frag.contains("<p class=\"shared\">shared from page</p>"), frag);
    }


    @Test
    public void testWebVariables() throws Exception {
        final String html = html("/variables?p=a%20b&m=x&m=y");
        Assertions.assertEquals("from-request", text(html, "reqattr"));
        Assertions.assertEquals("from-context", text(html, "ctxvar"));
        Assertions.assertEquals("a b", text(html, "param"));
        Assertions.assertEquals("true", text(html, "paramnone"));
        Assertions.assertEquals("[x, y]", text(html, "parammulti"));
        Assertions.assertEquals("x", text(html, "parammultifirst"));
        Assertions.assertEquals("<li>x</li><li>y</li>", text(html, "parameach"));
        Assertions.assertEquals("true", text(html, "session"));
        Assertions.assertEquals(IntegrationServlet.APPLICATION_NAME, text(html, "application"));
    }


    @Test
    public void testSessionAndUrlRewriting() throws Exception {

        // New session, no cookie sent yet: links are rewritten with the session id
        final HttpTestClient.Result created = get(this.server, "/session/set?user=ana");
        Assertions.assertEquals(200, created.status);
        final String sessionId = created.sessionId();
        Assertions.assertNotNull(sessionId, String.valueOf(created.setCookie));
        Assertions.assertEquals("ana", text(created.body, "user"));
        Assertions.assertEquals(
                "<a id=\"link\" href=\"/app/x;jsessionid=" + sessionId + "?p=1\">", tag(created.body, "link"));

        // Session id sent as cookie: no rewriting
        final String byCookie = html("/session/show", "Cookie", "JSESSIONID=" + sessionId);
        Assertions.assertEquals("ana", text(byCookie, "user"));
        Assertions.assertEquals("<a id=\"link\" href=\"/app/x?p=1\">", tag(byCookie, "link"));

        // Session id sent in the URL: rewriting continues
        final String byUrl = html("/session/show;jsessionid=" + sessionId);
        Assertions.assertEquals("ana", text(byUrl, "user"));
        Assertions.assertEquals(
                "<a id=\"link\" href=\"/app/x;jsessionid=" + sessionId + "?p=1\">", tag(byUrl, "link"));

        // No session
        final String none = html("/session/show");
        Assertions.assertEquals("", text(none, "user"));
        Assertions.assertEquals("<a id=\"link\" href=\"/app/x?p=1\">", tag(none, "link"));

    }


    @Test
    public void testSessionChangedThroughNativeRequest() throws Exception {

        final HttpTestClient.Result created = get(this.server, "/session/native?user=bea");
        Assertions.assertEquals(200, created.status, created.body);
        final String sessionId = created.sessionId();
        Assertions.assertNotNull(sessionId, String.valueOf(created.setCookie));
        Assertions.assertEquals("bea", text(created.body, "user"));
        Assertions.assertEquals(
                "<a id=\"link\" href=\"/app/x;jsessionid=" + sessionId + "?p=1\">", tag(created.body, "link"));

        final String invalidated = html("/session/invalidate", "Cookie", "JSESSIONID=" + sessionId);
        Assertions.assertEquals("", text(invalidated, "user"));
        Assertions.assertEquals("<a id=\"link\" href=\"/app/x?p=1\">", tag(invalidated, "link"));
        Assertions.assertEquals("", text(html("/session/show", "Cookie", "JSESSIONID=" + sessionId), "user"));

    }


    @Test
    public void testFormPost() throws Exception {
        final Map<String, String[]> form = new LinkedHashMap<>();
        form.put("name", new String[] { "Jos\u00E9 <b>" });
        form.put("tags", new String[] { "a", "\u00F1" });
        final HttpTestClient.Result result = post(this.server, "/form", form);
        Assertions.assertEquals(200, result.status, result.body);
        Assertions.assertEquals("Jos\u00E9 &lt;b&gt;", text(result.body, "echo"));
        Assertions.assertEquals(
                "<input id=\"name\" type=\"text\" name=\"name\" value=\"Jos\u00E9 &lt;b&gt;\">", tag(result.body, "name"));
        Assertions.assertEquals("<li>a</li><li>\u00F1</li>", text(result.body, "tags"));
        Assertions.assertEquals("<form id=\"form\" action=\"/app/form\" method=\"post\">", tag(result.body, "form"));
    }


    @Test
    public void testLinks() throws Exception {
        final String html = html("/links");
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
        Assertions.assertEquals("<img id=\"img\" src=\"/app/img/a.png\">", tag(html, "img"));
        Assertions.assertEquals("<form id=\"form\" action=\"/app/save\" method=\"post\">", tag(html, "form"));
        Assertions.assertEquals("<button id=\"formaction\" formaction=\"/app/other\">", tag(html, "formaction"));
        Assertions.assertEquals("/app/x?p=a%20b%26c", text(html, "inlined"));
        Assertions.assertEquals("var u = \"\\/app\\/x?p=a%20b%26c\";", text(html, "js"));
    }


    @Test
    public void testOgnlExpressions() throws Exception {
        final String html = html("/ognl");
        Assertions.assertEquals("Ana", text(html, "property"));
        Assertions.assertEquals("Lugo", text(html, "nested"));
        Assertions.assertEquals("true", text(html, "safe"));
        Assertions.assertEquals("none", text(html, "elvis"));
        Assertions.assertEquals("Hi, Ana", text(html, "method"));
        Assertions.assertEquals("[Ana, Bea, Carl]", text(html, "projection"));
        Assertions.assertEquals("2", text(html, "selection"));
        Assertions.assertEquals("107", text(html, "aggregate"));
        Assertions.assertEquals("true", text(html, "logic"));
        Assertions.assertEquals("v", text(html, "map"));
        Assertions.assertEquals("2026-11-11", text(html, "temporals"));
        Assertions.assertEquals("1,234.50", text(html, "numbers"));
        Assertions.assertEquals("ANA", text(html, "strings"));
        Assertions.assertEquals("<span>Ana</span>", text(html, "object"));
        Assertions.assertEquals("<li>1:Ana</li><li>2:Bea</li><li>3:Carl</li>", text(html, "each"));
    }


    @Test
    public void testRemovedWebExpressionObjects() throws Exception {
        Assertions.assertTrue(error("/removed-object").contains("expression utility objects are no longer available"));
    }


    @Test
    public void testRestrictedRequestParameters() throws Exception {

        Assertions.assertEquals("a&lt;b", text(html("/restricted/text-param?p=a%3Cb"), "r"));
        Assertions.assertEquals("<a id=\"r\" href=\"/app/x?p=a%3Cb\">", tag(html("/restricted/href-param?p=a%3Cb"), "r"));

        for (final String name : new String[] { "utext-param", "utext-link-param", "preprocessing-param", "data-param", "fragment-param" }) {
            final String body = error("/restricted/" + name + "?p=x");
            Assertions.assertTrue(body.contains("is forbidden in this context"), body);
        }

        final String unsafe = error("/restricted/onclick-text");
        Assertions.assertTrue(unsafe.contains("Only variable expressions returning numbers or booleans are allowed in this context"), unsafe);
        Assertions.assertEquals("<button id=\"r\" onclick=\"3\">", tag(html("/restricted/onclick-number"), "r"));

    }


    @Test
    public void testFragments() throws Exception {
        final String html = html("/fragments");
        Assertions.assertEquals("<div><p>plain</p></div>", text(html, "s1"));
        Assertions.assertEquals("<div id=\"byid\"><p>by id</p></div>", text(html, "s2"));
        Assertions.assertEquals("<div class=\"byclass\"><p>by class</p></div>", text(html, "s3"));
        Assertions.assertEquals("<div><span>local</span></div>", text(html, "s4"));
        Assertions.assertEquals("<div><span>local</span></div>", text(html, "s5"));
        Assertions.assertEquals("<span>snippet Ana</span>\n", text(html, "s6"));
        Assertions.assertEquals("<div><p>plain</p></div>", text(html, "s7"));
        Assertions.assertEquals("<div><p>plain</p></div>", text(html, "s8"));
        Assertions.assertEquals("<div><p>plain</p></div>", text(html, "s9"));
        Assertions.assertEquals("", text(html, "s10"));
        Assertions.assertEquals("<p>kept</p>", text(html, "s11"));
        Assertions.assertEquals("<header><h1>Ana</h1></header>", text(html, "s12"));
    }


    @Test
    public void testLayouts() throws Exception {
        // '_' as parameter keeps the layout's default content, '~{}' removes it
        final String page = html("/layout-page");
        Assertions.assertTrue(page.contains("<title>Page title</title>"), page);
        Assertions.assertTrue(page.contains("<header><h1>Site</h1></header>"), page);
        Assertions.assertTrue(page.contains("<main><p>Ana</p></main>"), page);
        Assertions.assertTrue(page.contains("<aside><p>default aside</p></aside>"), page);
        Assertions.assertTrue(page.contains("<p class=\"own\">Footer from fragment</p>"), page);
        Assertions.assertTrue(page.contains("<p class=\"shared\">shared from fragment</p>"), page);
        final String empty = html("/layout-page-empty");
        Assertions.assertTrue(empty.contains("<main><p>Ana</p></main>"), empty);
        Assertions.assertFalse(empty.contains("<aside>"), empty);
    }


    @Test
    public void testTextAndJavaScriptTemplates() throws Exception {
        final HttpTestClient.Result text = get(this.server, "/report.txt");
        Assertions.assertEquals(200, text.status, text.body);
        Assertions.assertEquals("text/plain;charset=UTF-8", text.contentType);
        Assertions.assertEquals("Ana=34\nBea=28\nCarl=45\n", text.body);
        final HttpTestClient.Result js = get(this.server, "/app.js");
        Assertions.assertEquals(200, js.status, js.body);
        Assertions.assertEquals("text/javascript;charset=UTF-8", js.contentType);
        Assertions.assertEquals("var names = [\"Ana\",\"Bea\",\"Carl\"];\n", js.body);
    }


    @Test
    public void testJavaScriptInlining() throws Exception {
        final String js = text(html("/inline-js"), "js");
        Assertions.assertTrue(js.contains("var person = {\"name\":\"Ana\",\"age\":34,\"address\":{\"city\":\"Lugo\"}};"), js);
        Assertions.assertTrue(js.contains("var text = \"\\u003C\\/script\\u003E\\u003Cb\\u003E\\u0026'\\\"\";"), js);
        Assertions.assertTrue(js.contains("var map = {\"k\":\"v\"};"), js);
    }


    @Test
    public void testLargeOutput() throws Exception {
        final int count = 100000;
        final String html = html("/big?n=" + count);
        Assertions.assertTrue(html.endsWith("</html>\n"), html.substring(Math.max(0, html.length() - 100)));
        final Matcher matcher = Pattern.compile("<li>(\\d+)</li>").matcher(html);
        int expected = 1;
        while (matcher.find()) {
            Assertions.assertEquals(expected++, Integer.parseInt(matcher.group(1)));
        }
        Assertions.assertEquals(count + 1, expected);
    }


    @Test
    public void testConcurrentRequests() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                final int n = i;
                futures.add(executor.submit(() -> {
                    switch (n % 3) {
                        case 0:
                            Assertions.assertEquals("\u00A1Hola, Ana!", text(html("/messages", "Accept-Language", "es"), "greeting"));
                            break;
                        case 1:
                            Assertions.assertEquals("Ola, Ana!", text(html("/locale/explicit"), "greeting"));
                            break;
                        default:
                            Assertions.assertEquals("v" + n, text(html("/variables?m=z&p=v" + n), "param"));
                    }
                    return null;
                }));
            }
            for (final Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdownNow();
        }
    }

}

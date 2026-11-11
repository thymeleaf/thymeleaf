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
package org.thymeleaf.spring5.web.webflux;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.spring5.util.SpringRequestUtils;

public class SpringWebFluxWebExchangeTest {


    private static ISpringWebFluxWebExchange buildExchange(final MockServerWebExchange exchange) {
        return SpringWebFluxWebApplication.buildApplication(ReactiveAdapterRegistry.getSharedInstance())
                .buildExchange(exchange, Locale.FRANCE, MediaType.TEXT_HTML, StandardCharsets.ISO_8859_1);
    }

    private static ISpringWebFluxWebRequest buildRequest(final MockServerHttpRequest request) {
        return buildExchange(MockServerWebExchange.from(request)).getRequest();
    }




    @Test
    public void testRequestPathAndURI() {

        final ISpringWebFluxWebRequest request =
                buildRequest(MockServerHttpRequest.method(HttpMethod.POST, URI.create("https://example.test:8443/app/one/two?a=1&b=x%20y")).contextPath("/app").build());

        Assertions.assertEquals("POST", request.getMethod());
        Assertions.assertEquals("https", request.getScheme());
        Assertions.assertEquals("example.test", request.getServerName());
        Assertions.assertEquals(Integer.valueOf(8443), request.getServerPort());
        Assertions.assertEquals("/app", request.getApplicationPath());
        Assertions.assertEquals("/one/two", request.getPathWithinApplication());
        Assertions.assertEquals("a=1&b=x%20y", request.getQueryString());
        Assertions.assertEquals("https://example.test:8443/app/one/two?a=1&b=x%20y", request.getRequestURL());
        Assertions.assertTrue(request.getNativeRequestObject() instanceof MockServerHttpRequest);

        final ISpringWebFluxWebRequest noContext = buildRequest(MockServerHttpRequest.get("/one").build());
        Assertions.assertEquals("", noContext.getApplicationPath());
        Assertions.assertEquals("/one", noContext.getPathWithinApplication());
        Assertions.assertNull(noContext.getQueryString());

    }


    @Test
    public void testHeaders() {

        final ISpringWebFluxWebRequest request =
                buildRequest(MockServerHttpRequest.get("/one")
                        .header("X-Test", "v1", "v2")
                        .header("Accept-Language", "fr-FR")
                        .build());

        Assertions.assertTrue(request.containsHeader("X-Test"));
        Assertions.assertTrue(request.containsHeader("x-test"));
        Assertions.assertFalse(request.containsHeader("X-Missing"));
        Assertions.assertEquals("v1", request.getHeaderValue("X-Test"));
        Assertions.assertEquals("v1", request.getHeaderValue("x-TEST"));
        Assertions.assertArrayEquals(new String[] { "v1", "v2" }, request.getHeaderValues("x-test"));
        Assertions.assertNull(request.getHeaderValue("X-Missing"));
        Assertions.assertArrayEquals(new String[0], request.getHeaderValues("X-Missing"));
        Assertions.assertEquals(2, request.getHeaderCount());
        Assertions.assertEquals(new HashSet<>(Arrays.asList("X-Test", "Accept-Language")), request.getAllHeaderNames());

        final Map<String,String[]> headerMap = request.getHeaderMap();
        Assertions.assertEquals(2, headerMap.size());
        Assertions.assertArrayEquals(new String[] { "v1", "v2" }, headerMap.get("X-Test"));
        Assertions.assertArrayEquals(new String[] { "fr-FR" }, headerMap.get("Accept-Language"));

        final MultiValueMap<String,String> headerMultiValueMap = request.getHeaderMultiValueMap();
        Assertions.assertEquals(Arrays.asList("v1", "v2"), headerMultiValueMap.get("X-Test"));

        final ISpringWebFluxWebRequest noHeaders = buildRequest(MockServerHttpRequest.get("/one").build());
        Assertions.assertEquals(0, noHeaders.getHeaderCount());
        Assertions.assertTrue(noHeaders.getHeaderMap().isEmpty());
        Assertions.assertTrue(noHeaders.getAllHeaderNames().isEmpty());

    }


    @Test
    public void testParameters() {

        final ISpringWebFluxWebRequest request =
                buildRequest(MockServerHttpRequest.method(HttpMethod.GET, URI.create("/one?p=1&p=2&q=x%20y&empty=")).build());

        Assertions.assertTrue(request.containsParameter("p"));
        Assertions.assertFalse(request.containsParameter("P"));
        Assertions.assertEquals("1", request.getParameterValue("p"));
        Assertions.assertArrayEquals(new String[] { "1", "2" }, request.getParameterValues("p"));
        Assertions.assertEquals("x y", request.getParameterValue("q"));
        Assertions.assertEquals("", request.getParameterValue("empty"));
        Assertions.assertNull(request.getParameterValue("missing"));
        Assertions.assertArrayEquals(new String[0], request.getParameterValues("missing"));
        Assertions.assertEquals(3, request.getParameterCount());
        Assertions.assertEquals(new HashSet<>(Arrays.asList("p", "q", "empty")), request.getAllParameterNames());
        Assertions.assertArrayEquals(new String[] { "1", "2" }, request.getParameterMap().get("p"));

    }


    @Test
    public void testCookies() {

        final ISpringWebFluxWebRequest request =
                buildRequest(MockServerHttpRequest.get("/one")
                        .cookie(new HttpCookie("c", "1"), new HttpCookie("c", "2"), new HttpCookie("d", "x"))
                        .build());

        Assertions.assertTrue(request.containsCookie("c"));
        Assertions.assertFalse(request.containsCookie("missing"));
        Assertions.assertEquals("1", request.getCookieValue("c"));
        Assertions.assertArrayEquals(new String[] { "1", "2" }, request.getCookieValues("c"));
        Assertions.assertNull(request.getCookieValue("missing"));
        Assertions.assertArrayEquals(new String[0], request.getCookieValues("missing"));
        Assertions.assertEquals(2, request.getCookieCount());
        Assertions.assertEquals(new HashSet<>(Arrays.asList("c", "d")), request.getAllCookieNames());
        Assertions.assertArrayEquals(new String[] { "x" }, request.getCookieMap().get("d"));

    }


    @Test
    public void testExchange() {

        final MockServerWebExchange mockExchange = MockServerWebExchange.from(MockServerHttpRequest.get("/one").build());
        mockExchange.addUrlTransformer(url -> url + ";t");
        final ISpringWebFluxWebExchange exchange = buildExchange(mockExchange);

        Assertions.assertEquals(Locale.FRANCE, exchange.getLocale());
        Assertions.assertEquals("text/html", exchange.getContentType());
        Assertions.assertEquals("ISO-8859-1", exchange.getCharacterEncoding());
        Assertions.assertSame(mockExchange, exchange.getNativeExchangeObject());
        Assertions.assertEquals("/one;t", exchange.transformURL("/one"));

        exchange.setAttributeValue("a", "one");
        Assertions.assertEquals("one", mockExchange.getAttribute("a"));
        Assertions.assertEquals("one", exchange.getAttributeValue("a"));
        Assertions.assertTrue(exchange.containsAttribute("a"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> exchange.getAttributes().put("b", "two"));
        exchange.setAttributeValue("a", null);
        Assertions.assertFalse(mockExchange.getAttributes().containsKey("a"));
        mockExchange.getAttributes().put("c", "three");
        Assertions.assertEquals("three", exchange.getAttributeValue("c"));
        exchange.removeAttribute("c");
        Assertions.assertNull(mockExchange.getAttribute("c"));

        Assertions.assertTrue(exchange.getApplication().getAttributes().isEmpty());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> exchange.getApplication().setAttributeValue("x", "y"));
        Assertions.assertSame(ReactiveAdapterRegistry.getSharedInstance(), exchange.getApplication().getReactiveAdapterRegistry());

    }


    @Test
    public void testSessionAndPrincipal() {

        final MockServerWebExchange mockExchange = MockServerWebExchange.from(MockServerHttpRequest.get("/one").build());
        final ISpringWebFluxWebExchange exchange = buildExchange(mockExchange);

        // Session and principal are only available once they have been resolved and set as exchange attributes
        Assertions.assertNull(exchange.getSession());
        Assertions.assertNull(exchange.getPrincipal());

        final MockWebSession webSession = new MockWebSession();
        webSession.getAttributes().put("s", "one");
        final Principal principal = () -> "user";
        mockExchange.getAttributes().put(SpringContextUtils.WEB_SESSION_ATTRIBUTE_NAME, webSession);
        mockExchange.getAttributes().put(SpringContextUtils.WEB_EXCHANGE_PRINCIPAL_ATTRIBUTE_NAME, principal);

        Assertions.assertNotNull(exchange.getSession());
        Assertions.assertSame(exchange.getSession(), exchange.getSession());
        Assertions.assertEquals("one", exchange.getSession().getAttributeValue("s"));
        exchange.getSession().setAttributeValue("t", "two");
        Assertions.assertEquals("two", webSession.getAttribute("t"));
        Assertions.assertSame(webSession, exchange.getSession().getNativeSessionObject());
        Assertions.assertSame(principal, exchange.getPrincipal());

    }


    @Test
    public void testViewNameNotInRequest() {

        final String viewName = "main :: content(${one})";

        // Each request part containing the expression of the view name must cause it to be refused
        assertViewNameInRequest(viewName, MockServerHttpRequest.get("/one").header("X-Test", "${one}").build());
        assertViewNameInRequest(viewName, MockServerHttpRequest.get("/one").header("X-Test", "a", "${one}").build());
        assertViewNameInRequest(viewName, MockServerHttpRequest.get("/one").header("x-test", "a").header("X-TEST", "${one}").build());
        assertViewNameInRequest(viewName,
                MockServerHttpRequest.get("/one").header("A", "x").header("B", "y", "z").header("X-Last", "${one}").build());
        assertViewNameInRequest(viewName, MockServerHttpRequest.get("/one").cookie(new HttpCookie("c", "${one}")).build());
        assertViewNameInRequest(viewName, MockServerHttpRequest.get("/one?p={p}", "${one}").build());
        assertViewNameInRequest(viewName, MockServerHttpRequest.get("/one/{p}", "${one}").build());

        final MockServerHttpRequest unrelated =
                MockServerHttpRequest.get("/one?p=x").header("X-Test", "${two}").cookie(new HttpCookie("c", "x")).build();
        Assertions.assertDoesNotThrow(() -> SpringRequestUtils.checkViewNameNotInRequest(viewName, buildRequest(unrelated)));
        Assertions.assertDoesNotThrow(() -> SpringRequestUtils.checkViewNameNotInRequest("main :: content", buildRequest(unrelated)));

        final MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        formData.put("f", Collections.singletonList("${one}"));
        final TemplateProcessingException e = Assertions.assertThrows(TemplateProcessingException.class,
                () -> SpringRequestUtils.checkViewNameNotInRequest(viewName, buildRequest(unrelated), formData));
        Assertions.assertTrue(e.getMessage().contains("View name contains an expression"), e.getMessage());

    }


    private static void assertViewNameInRequest(final String viewName, final MockServerHttpRequest request) {
        final TemplateProcessingException e = Assertions.assertThrows(TemplateProcessingException.class,
                () -> SpringRequestUtils.checkViewNameNotInRequest(viewName, buildRequest(request)));
        Assertions.assertTrue(e.getMessage().contains("View name contains an expression"), e.getMessage());
    }


}

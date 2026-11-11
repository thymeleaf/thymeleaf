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
package org.thymeleaf.extras.springsecurity6.integration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public abstract class AbstractSecurityIntegrationTest {

    public static final String TEMPLATE_PREFIX = "classpath:/integration/templates/";

    private static final Pattern CSRF_FIELD = Pattern.compile("<input type=\"hidden\" name=\"_csrf\" value=\"([^\"]+)\"/>");


    protected abstract Class<?> getConfigurationClass();

    protected abstract Map<String,Object> getProperties();


    protected MockMvc mockMvc() {
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", getProperties()));
        context.register(getConfigurationClass());
        context.refresh();
        return MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
                .build();
    }


    protected static MockHttpServletRequestBuilder as(final MockHttpServletRequestBuilder request, final String user) {
        final String credentials = user + ":" + user + "password";
        return request.header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
    }


    private static String render(final MockMvc mockMvc, final MockHttpServletRequestBuilder request) throws Exception {
        final MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        Assertions.assertEquals(200, response.getStatus());
        return response.getContentAsString();
    }


    private static void assertShown(final String html, final String id, final boolean shown) {
        Assertions.assertEquals(shown, html.contains("id=\"" + id + "\""), id + " in:\n" + html);
    }


    private static String text(final String html, final String id) {
        final Matcher matcher = Pattern.compile("<p id=\"" + id + "\">(.*?)</p>").matcher(html);
        Assertions.assertTrue(matcher.find(), id + " in:\n" + html);
        return matcher.group(1);
    }




    @Test
    public void testAnonymous() throws Exception {

        final String html = render(mockMvc(), get("/page"));
        assertShown(html, "auth", false);
        assertShown(html, "anon", true);
        assertShown(html, "admin", false);
        assertShown(html, "url", false);
        Assertions.assertEquals("anonymousUser", text(html, "name"));
        Assertions.assertEquals("false", text(html, "user"));
        Assertions.assertEquals("anonymousUser", text(html, "authname"));
        Assertions.assertFalse(html.contains("sec:") || html.contains("th:"), html);

    }


    @Test
    public void testUser() throws Exception {

        final String html = render(mockMvc(), as(get("/page"), "bob"));
        assertShown(html, "auth", true);
        assertShown(html, "anon", false);
        assertShown(html, "admin", false);
        assertShown(html, "url", false);
        Assertions.assertEquals("bob", text(html, "name"));
        Assertions.assertEquals("true", text(html, "user"));
        Assertions.assertEquals("bob", text(html, "authname"));

    }


    @Test
    public void testAdmin() throws Exception {

        final String html = render(mockMvc(), as(get("/admin"), "jim"));
        assertShown(html, "auth", true);
        assertShown(html, "anon", false);
        assertShown(html, "admin", true);
        assertShown(html, "url", true);
        Assertions.assertEquals("jim", text(html, "name"));
        Assertions.assertEquals("true", text(html, "user"));

    }


    @Test
    public void testAccessDenied() throws Exception {

        final MockMvc mockMvc = mockMvc();
        Assertions.assertEquals(401, mockMvc.perform(get("/admin")).andReturn().getResponse().getStatus());
        Assertions.assertEquals(403, mockMvc.perform(as(get("/admin"), "bob")).andReturn().getResponse().getStatus());

    }


    @Test
    public void testCsrf() throws Exception {

        final MockMvc mockMvc = mockMvc();
        final MvcResult result = mockMvc.perform(get("/page")).andReturn();
        final String html = result.getResponse().getContentAsString();
        Assertions.assertTrue(html.contains("<form id=\"f\" action=\"/save\" method=\"post\">"), html);
        final Matcher matcher = CSRF_FIELD.matcher(html);
        Assertions.assertTrue(matcher.find(), html);
        final MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        Assertions.assertNotNull(session);

        Assertions.assertEquals(403, mockMvc.perform(post("/save").session(session)).andReturn().getResponse().getStatus());
        final MockHttpServletResponse response =
                mockMvc.perform(post("/save").session(session).param("_csrf", matcher.group(1))).andReturn().getResponse();
        Assertions.assertEquals(302, response.getStatus());
        Assertions.assertEquals("/page?saved", response.getRedirectedUrl());

    }

}

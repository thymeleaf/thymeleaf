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

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.thymeleaf.spring5.integration.ExtraTemplatingConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.thymeleaf.spring5.integration.IntegrationTestUtils.element;
import static org.thymeleaf.spring5.integration.IntegrationTestUtils.tag;
import static org.thymeleaf.spring5.integration.IntegrationTestUtils.text;

public class MvcBootFeaturesTest extends AbstractMvcFeaturesTest {

    @Override
    protected Class<?> getConfigurationClass() {
        return MvcBootConfiguration.class;
    }

    @Override
    protected Map<String, Object> getBaseProperties() {
        return MvcBootIntegrationTest.BOOT_BASE_PROPERTIES;
    }

    @Override
    protected MockMvc createTemplateModesMockMvc() {
        return MockMvcBuilders.webAppContextSetup(createContext(properties(), ExtraTemplatingConfiguration.class)).build();
    }

    @Override
    protected MockMvc createDecoupledLogicMockMvc() {
        // No Boot property for this: applications configure the auto-configured template resolver bean
        final AnnotationConfigWebApplicationContext context = createContext(properties());
        context.getBean("defaultTemplateResolver", SpringResourceTemplateResolver.class).setUseDecoupledLogic(true);
        return MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    public void testFragmentsFromOtherTemplateResolver() throws Exception {

        final MockMvc mockMvc =
                MockMvcBuilders.webAppContextSetup(createContext(properties(), ExtraTemplatingConfiguration.class)).build();
        Assertions.assertEquals("<p id=\"e\"><span>Greetings, F</span></p>",
                element(render(mockMvc, get("/uses-extra")), "e"));

    }


    @Test
    public void testLayoutDialect() throws Exception {

        final String html = render(mockMvc(), get("/layout-dialect").header("Accept-Language", "en-US"));
        Assertions.assertEquals(
                "<!DOCTYPE html>\n<html>\n<head>\n\n<title>Page - Site</title>\n\n</head>\n<body>\n" +
                "<header>Header</header>\n" +
                "<section><p>Layout Dialect content</p></section>\n" +
                "<footer>Hello, footer!</footer>\n" +
                "</body>\n</html>\n",
                html);

    }


    @Test
    public void testErrorPages() throws Exception {

        final MockMvc mockMvc = mockMvc();
        final MockHttpServletResponse notFound = perform(mockMvc, get("/error").accept("text/html")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(404))
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/missing"));
        Assertions.assertEquals(404, notFound.getStatus());
        final String notFoundHtml = notFound.getContentAsString();
        Assertions.assertEquals("Not found page", text(notFoundHtml, "page"));
        Assertions.assertEquals("404", text(notFoundHtml, "status"));
        Assertions.assertEquals("Not Found", text(notFoundHtml, "error"));
        Assertions.assertEquals("/missing", text(notFoundHtml, "path"));

        final MockHttpServletResponse serverError = perform(mockMvc, get("/error").accept("text/html")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(500))
                .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/failing"));
        Assertions.assertEquals(500, serverError.getStatus());
        Assertions.assertEquals("Generic error page", text(serverError.getContentAsString(), "page"));
        Assertions.assertEquals("500", text(serverError.getContentAsString(), "status"));

    }


    @Test
    public void testVersionedResourceLinks() throws Exception {

        final AnnotationConfigWebApplicationContext context = createContext(properties(
                "spring.web.resources.static-locations", "classpath:/integration/static/",
                "spring.web.resources.chain.strategy.content.enabled", "true",
                "spring.web.resources.chain.strategy.content.paths", "/**"));
        // Boot registers the filter for the servlet container, which MockMvc does not use
        Filter resourceUrlEncodingFilter = null;
        for (final FilterRegistrationBean<?> registration : context.getBeansOfType(FilterRegistrationBean.class).values()) {
            if (registration.getFilter() instanceof ResourceUrlEncodingFilter) {
                resourceUrlEncodingFilter = registration.getFilter();
            }
        }
        Assertions.assertNotNull(resourceUrlEncodingFilter);
        final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(resourceUrlEncodingFilter).build();

        final String hash = DigestUtils.md5DigestAsHex(StreamUtils.copyToByteArray(
                new ClassPathResource("integration/static/css/app.css").getInputStream()));
        final String html = render(mockMvc, get("/static-links"));
        Assertions.assertEquals("<a id=\"css\" href=\"/css/app-" + hash + ".css\">", tag(html, "css"));
        Assertions.assertEquals("<a id=\"other\" href=\"/x\">", tag(html, "other"));
        Assertions.assertEquals("body { color: black; }\n",
                new String(perform(mockMvc, get("/css/app-" + hash + ".css")).getContentAsByteArray(), StandardCharsets.UTF_8));

    }

}

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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.integration.ExtraTemplatingConfiguration;
import org.thymeleaf.spring5.integration.IntegrationTestUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.thymeleaf.spring5.integration.IntegrationTestUtils.text;

public class MvcBootIntegrationTest extends AbstractMvcIntegrationTest {

    @Override
    protected Class<?> getConfigurationClass() {
        return MvcBootConfiguration.class;
    }

    static final Map<String,Object> BOOT_BASE_PROPERTIES = properties(
            "spring.thymeleaf.prefix", IntegrationTestUtils.TEMPLATE_PREFIX,
            "spring.messages.basename", IntegrationTestUtils.MESSAGES_BASENAME,
            "spring.messages.fallback-to-system-locale", "false");


    @Override
    protected Map<String, Object> getBaseProperties() {
        return BOOT_BASE_PROPERTIES;
    }

    @Override
    protected Map<String, Object> getUseCodeAsDefaultMessageProperties() {
        return properties("spring.messages.use-code-as-default-message", "true");
    }

    @Override
    protected MockMvc createFailOnMissingMessageMockMvc() {
        // No Boot property for this: applications configure the auto-configured engine bean
        final AnnotationConfigWebApplicationContext context = createContext(properties());
        context.getBean(SpringTemplateEngine.class).setFailOnMissingMessage(true);
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Override
    protected Map<String, Object> getSpringELCompilerProperties() {
        return properties("spring.thymeleaf.enable-spring-el-compiler", "true");
    }

    @Override
    protected Map<String, Object> getHiddenMarkersBeforeCheckboxesProperties() {
        return properties("spring.thymeleaf.render-hidden-markers-before-checkboxes", "true");
    }

    @Override
    protected Map<String, Object> getContentTypeProperties(final String contentType) {
        return properties("spring.thymeleaf.servlet.content-type", contentType);
    }

    @Override
    protected Map<String, Object> getViewNamesProperties(final String viewNames, final String excludedViewNames) {
        // Views not handled by Thymeleaf are taken by Boot's InternalResourceViewResolver, as in apps that also use JSP
        final Map<String,Object> properties = properties("spring.mvc.view.suffix", ".jsp");
        if (viewNames != null) {
            properties.put("spring.thymeleaf.view-names", viewNames);
        }
        if (excludedViewNames != null) {
            properties.put("spring.thymeleaf.excluded-view-names", excludedViewNames);
        }
        return properties;
    }

    @Override
    protected void assertNotResolvedByThymeleaf(final MockMvc mockMvc, final String uri) throws Exception {
        final MockHttpServletResponse response = perform(mockMvc, get(uri));
        Assertions.assertEquals(uri.substring(1) + ".jsp", response.getForwardedUrl());
        Assertions.assertEquals("", response.getContentAsString());
    }


    @Test
    public void testDialectAndTemplateResolverBeans() throws Exception {

        final MockMvc mockMvc =
                MockMvcBuilders.webAppContextSetup(createContext(properties(), ExtraTemplatingConfiguration.class)).build();
        Assertions.assertEquals("Greetings, X", text(render(mockMvc, get("/extra")), "greeter"));
        Assertions.assertEquals("Hello, World!", text(render(mockMvc, get("/hello").header("Accept-Language", "en-US")), "greeting"));

    }

}

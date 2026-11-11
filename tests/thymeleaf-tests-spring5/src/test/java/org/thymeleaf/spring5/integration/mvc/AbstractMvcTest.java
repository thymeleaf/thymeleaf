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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public abstract class AbstractMvcTest {



    protected abstract Class<?> getConfigurationClass();

    protected abstract Map<String,Object> getBaseProperties();


    protected static Map<String,Object> properties(final String... namesAndValues) {
        final Map<String,Object> properties = new HashMap<>();
        for (int i = 0; i < namesAndValues.length; i += 2) {
            properties.put(namesAndValues[i], namesAndValues[i + 1]);
        }
        return properties;
    }


    protected AnnotationConfigWebApplicationContext createContext(
            final Map<String,Object> properties, final Class<?>... additionalConfigurationClasses) {
        return createContext(new MockServletContext(), properties, additionalConfigurationClasses);
    }


    protected AnnotationConfigWebApplicationContext createContext(
            final MockServletContext servletContext, final Map<String,Object> properties,
            final Class<?>... additionalConfigurationClasses) {
        final Map<String,Object> allProperties = new HashMap<>(getBaseProperties());
        allProperties.putAll(properties);
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", allProperties));
        context.register(getConfigurationClass());
        if (additionalConfigurationClasses.length > 0) {
            context.register(additionalConfigurationClasses);
        }
        context.refresh();
        return context;
    }


    protected MockMvc mockMvc(final Map<String,Object> properties) {
        return MockMvcBuilders.webAppContextSetup(createContext(properties)).build();
    }


    protected MockMvc mockMvc() {
        return mockMvc(Collections.emptyMap());
    }


    protected MockMvc mockMvc(final Map<String,Object> properties, final Filter... filters) {
        return MockMvcBuilders.webAppContextSetup(createContext(properties)).addFilters(filters).build();
    }


    protected static MockHttpServletResponse perform(final MockMvc mockMvc, final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request).andReturn().getResponse();
    }


    protected static String render(final MockMvc mockMvc, final MockHttpServletRequestBuilder request) throws Exception {
        final MockHttpServletResponse response = perform(mockMvc, request);
        Assertions.assertEquals(200, response.getStatus());
        return response.getContentAsString();
    }


    protected static Throwable findCause(final Throwable throwable, final Class<? extends Throwable> type) {
        Throwable t = throwable;
        while (t != null && !type.isInstance(t)) {
            t = t.getCause();
        }
        Assertions.assertNotNull(t, "No " + type.getSimpleName() + " found in the cause chain of " + throwable);
        return t;
    }

}

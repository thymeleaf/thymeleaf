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
import java.util.Map;

import org.springframework.test.web.servlet.MockMvc;

public class MvcPlainFeaturesTest extends AbstractMvcFeaturesTest {

    @Override
    protected Class<?> getConfigurationClass() {
        return MvcPlainConfiguration.class;
    }

    @Override
    protected Map<String, Object> getBaseProperties() {
        return Collections.emptyMap();
    }

    @Override
    protected MockMvc createTemplateModesMockMvc() {
        // The template resolver is configured with patterns for TEXT and JAVASCRIPT templates
        return mockMvc();
    }

    @Override
    protected MockMvc createDecoupledLogicMockMvc() {
        return mockMvc(properties(MvcPlainConfiguration.USE_DECOUPLED_LOGIC, "true"));
    }

}

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

import java.util.Collections;
import java.util.Map;

public class SecurityBootIntegrationTest extends AbstractSecurityIntegrationTest {

    @Override
    protected Class<?> getConfigurationClass() {
        return SecurityBootConfiguration.class;
    }

    @Override
    protected Map<String, Object> getProperties() {
        return Collections.singletonMap("spring.thymeleaf.prefix", TEMPLATE_PREFIX);
    }

}

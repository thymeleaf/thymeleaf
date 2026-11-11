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
package org.thymeleaf.spring5.integration.webflux;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.thymeleaf.spring5.integration.CodeFormatter;
import org.thymeleaf.spring5.integration.IntegrationBean;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        PropertyPlaceholderAutoConfiguration.class, MessageSourceAutoConfiguration.class,
        WebFluxAutoConfiguration.class, ErrorWebFluxAutoConfiguration.class, ThymeleafAutoConfiguration.class })
@Import(WebFluxIntegrationController.class)
public class WebFluxBootConfiguration {

    @Bean
    public CodeFormatter codeFormatter() {
        return new CodeFormatter();
    }

    @Bean
    public IntegrationBean integrationBean() {
        return new IntegrationBean();
    }

    @Bean
    public TestReactiveRequestDataValueProcessor requestDataValueProcessor() {
        return new TestReactiveRequestDataValueProcessor();
    }

    @Bean
    public TestWebFilter testWebFilter() {
        return new TestWebFilter();
    }

    @Bean
    public ExceptionMessageWebExceptionHandler exceptionMessageWebExceptionHandler() {
        return new ExceptionMessageWebExceptionHandler();
    }

}

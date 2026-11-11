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
package org.thymeleaf.spring6.integration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

// Additional dialect and template resolver beans (also for TEXT and JAVASCRIPT templates), as applications declare them for Spring Boot to pick up
@Configuration(proxyBeanMethods = false)
public class ExtraTemplatingConfiguration {

    @Bean
    public GreeterDialect greeterDialect() {
        return new GreeterDialect();
    }

    @Bean
    public ClassLoaderTemplateResolver extraTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("integration/extra/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setResolvablePatterns(Collections.singleton("extra-*"));
        templateResolver.setCheckExistence(true);
        templateResolver.setOrder(Integer.valueOf(1));
        return templateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver textTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("integration/templates/");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setResolvablePatterns(new HashSet<>(Arrays.asList("*.txt", "*.js")));
        templateResolver.setTextTemplateModePatterns(Collections.singleton("*.txt"));
        templateResolver.setJavaScriptTemplateModePatterns(Collections.singleton("*.js"));
        templateResolver.setCheckExistence(true);
        templateResolver.setOrder(Integer.valueOf(2));
        return templateResolver;
    }

}

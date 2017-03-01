/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafsandbox.stsm;


import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import thymeleafsandbox.stsm.web.conversion.DateFormatter;
import thymeleafsandbox.stsm.web.conversion.VarietyFormatter;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
public class StsmFluxWebConfig {

    // TODO * Once there is a Spring Boot starter for thymeleaf-spring5, there would be no need to have
    // TODO   that @EnableConfigurationProperties annotation or use it for declaring the beans down in the
    // TODO   "thymeleaf" section below.


    private ApplicationContext applicationContext;
    private ThymeleafProperties thymeleafProperties;



    public StsmFluxWebConfig(
            final ApplicationContext applicationContext,
            final ThymeleafProperties thymeleafProperties) {
        super();
        this.applicationContext = applicationContext;
        this.thymeleafProperties = thymeleafProperties;
    }



    /*
     * --------------------------------------
     * FORMATTERS
     * --------------------------------------
     */

    @Bean
    public VarietyFormatter varietyFormatter() {
        return new VarietyFormatter();
    }

    @Bean
    public DateFormatter dateFormatter() {
        return new DateFormatter();
    }



    /*
     * --------------------------------------
     * THYMELEAF CONFIGURATION
     * --------------------------------------
     */

    // TODO * If there was a Spring Boot starter for thymeleaf-spring5 most probably some or all of these
    // TODO   resolver and engine beans would not need to be specifically declared here.

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver() {

        final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(this.thymeleafProperties.getPrefix());
        resolver.setSuffix(this.thymeleafProperties.getSuffix());
        resolver.setTemplateMode(this.thymeleafProperties.getMode());
        if (this.thymeleafProperties.getEncoding() != null) {
            resolver.setCharacterEncoding(this.thymeleafProperties.getEncoding().name());
        }
        resolver.setCacheable(this.thymeleafProperties.isCache());
        final Integer order = this.thymeleafProperties.getTemplateResolverOrder();
        if (order != null) {
            resolver.setOrder(order);
        }
        resolver.setCheckExistence(this.thymeleafProperties.isCheckTemplate());

        return resolver;

    }


    @Bean
    public ISpringWebFluxTemplateEngine thymeleafTemplateEngine(){
        final SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver());
        return templateEngine;
    }


    @Bean
    public ThymeleafReactiveViewResolver thymeleafChunkedAndDataDrivenViewResolver(){
        final ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
        viewResolver.setTemplateEngine(thymeleafTemplateEngine());
        // We are setting a size limit on the output buffers, which means thymeleaf will be able to
        // work in CHUNKED or DATA-DRIVEN modes
        viewResolver.setResponseMaxChunkSizeBytes(16384);
        return viewResolver;
    }

}

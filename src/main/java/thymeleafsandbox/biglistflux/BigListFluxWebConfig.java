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
package thymeleafsandbox.biglistflux;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerViewResolver;
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;

@Configuration
public class BigListFluxWebConfig {


    private ApplicationContext applicationContext;



    public BigListFluxWebConfig(final ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }




    /*
     * --------------------------------------
     * FREEMARKER CONFIGURATION
     * --------------------------------------
     */


    @Bean
    public FreeMarkerConfigurer freeMarkerConfig() {
        // Note this is the reactive version of FreeMarker's configuration, so there is no auto-configuration yet.
        final FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setPreTemplateLoaders(new SpringTemplateLoader(this.applicationContext, "/templates/"));
        return freeMarkerConfigurer;
    }

    /*
     * ViewResolver for FreeMarker templates executing in NORMAL mode (only mode available for FreeMarker)
     * No limit to output buffer size, all data fully resolved in context.
     */
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        final FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver("", ".ftl");
        freeMarkerViewResolver.setOrder(4);
        // TODO * Apparently no way to specify which views can be handled by this ViewResolver (viewNames property)
        return freeMarkerViewResolver;
    }





    /*
     * --------------------------------------
     * THYMELEAF CONFIGURATION
     * --------------------------------------
     */


    /*
     * ViewResolver for Thymeleaf templates executing in FULL mode.
     * No limit to output chunk size, non-data-driven (all data fully resolved in context).
     *
     * NOTE the "thymeleafReactiveViewResolver" bean name is being used here in order to override the
     * ViewResolver auto-configured at the ThymeleafAutoConfiguration class from the spring-boot-starter-thymeleaf
     */
    @Bean
    public ThymeleafReactiveViewResolver thymeleafReactiveViewResolver(final ISpringWebFluxTemplateEngine templateEngine){
        final ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(2);
        viewResolver.setViewNames(new String[] {"thymeleaf/*"});
        return viewResolver;
    }

    /*
     * ViewResolver for Thymeleaf templates executing in BUFFERED or DATA-DRIVEN mode.
     *
     * CHUNKED: non-data-driven (all data fully resolved in context) but with an established limit to output chunk size.
     *
     * DATA-DRIVEN: the "dataSource" variable can be a Publisher<X>, in which case it will drive the execution of
     *              the engine and Thymeleaf will be executed as a part of the data flow.
     */
    @Bean
    public ThymeleafReactiveViewResolver thymeleafChunkedAndDataDrivenViewResolver(final ISpringWebFluxTemplateEngine templateEngine){
        final ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(1);
        viewResolver.setViewNames(new String[] {"thymeleaf/*chunked*", "thymeleaf/*datadriven*"});
        viewResolver.setResponseMaxChunkSizeBytes(8192); // OUTPUT BUFFER size limit
        return viewResolver;
    }

}

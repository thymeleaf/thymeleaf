package thymeleafexamples.springmail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import static thymeleafexamples.springmail.config.SpringWebInitializer.ENCODING;

/**
 * Spring MVC and Thymeleaf configuration.
 */
@Configuration
@ComponentScan("thymeleafexamples.springmail")
@EnableWebMvc
public class ThymeleafConfig extends WebMvcConfigurerAdapter {

    /**
     * THYMELEAF: View Resolver - implementation of Spring's ViewResolver interface.
     * (we would not need this if our app was not web)
     */
	@Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding(ENCODING);
        return viewResolver;
    }

    /**
     * THYMELEAF: Template Engine (Spring4-specific version).
     */
	@Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(emailTemplateResolver());
        templateEngine.addTemplateResolver(webTemplateResolver());
        return templateEngine;
    }

    /**
     * THYMELEAF: Template Resolver for email templates.
     */
    private TemplateResolver emailTemplateResolver() {
        TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/mail/");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding(ENCODING);
        templateResolver.setOrder(1);
        // Template cache is true by default. Set to false if you want 
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(false);
        return templateResolver;
    }
    
    /**
     * THYMELEAF: Template Resolver for webapp pages.
     * (we would not need this if our app was not web)
     */
    private TemplateResolver webTemplateResolver() {
        TemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding(ENCODING);
        templateResolver.setOrder(2);
        // Template cache is true by default. Set to false if you want 
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use Spring servlet to serve static resources
	    registry.addResourceHandler("/images/**").addResourceLocations("/images/");
	    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
	    registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	    registry.addResourceHandler("/swf/**").addResourceLocations("/swf/");
    }

    @Bean
    public IMessageResolver messageResolver() {
        return new StandardMessageResolver();
    }
}

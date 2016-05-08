package thymeleafexamples.springmail.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import static thymeleafexamples.springmail.config.SpringWebInitializer.ENCODING;

/**
 * Spring MVC and Thymeleaf configuration.
 */
@Configuration
@ComponentScan("thymeleafexamples.springmail")
@EnableWebMvc
public class ThymeleafConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ApplicationContext applicationContext;


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
        templateEngine.setEnableSpringELCompiler(true); // Compiled SpringEL should speed up executions
        templateEngine.addTemplateResolver(emailTemplateResolver());
        templateEngine.addTemplateResolver(webTemplateResolver());
        templateEngine.addTemplateResolver(stringTemplateResolver());
        templateEngine.setMessageResolver(messageResolver());
        return templateEngine;
    }

    /**
     * THYMELEAF: Template Resolver for email templates.
     */
    private ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/mail/");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(ENCODING);
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }
    
    /**
     * THYMELEAF: Template Resolver for webapp pages.
     * (we would not need this if our app was not web)
     */
    private ITemplateResolver webTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(ENCODING);
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    /**
     * THYMELEAF: Template Resolver for String templates.
     * (template will be a passed String -- for editable templates)
     */
    private ITemplateResolver stringTemplateResolver() {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(Integer.valueOf(3));
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    private IMessageResolver messageResolver() {
        // For convenience We will override the Spring one used by SpringTemplateEngine
        return new StandardMessageResolver();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use Spring servlet to serve static resources
	    registry.addResourceHandler("/images/**").addResourceLocations("/images/");
	    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
	    registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	    registry.addResourceHandler("/swf/**").addResourceLocations("/swf/");
    }


}

package thymeleafexamples.springmail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 *  Spring beans configuration.
 */
@Configuration
public class MultipartConfig {
    
    private static final long maxUploadSize = 10485760; // 10 Mb

    @Bean
	public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(maxUploadSize);
		return multipartResolver;
    }
}

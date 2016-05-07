package thymeleafexamples.springmail.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 *  Mail configuration.
 */
@Configuration
public class MailConfig {
    
    private static final String PROPERTIES_FILE = "configuration.properties";
    private static final String JAVA_MAIL_FILE = "javamail.properties";
    
    private static final String HOST = "mail.server.host";
    private static final String PORT = "mail.server.port";
    private static final String PROTOCOL = "mail.server.protocol";
    private static final String USERNAME = "mail.server.username";
    private static final String PASSWORD = "mail.server.password";
    
    @Bean
	public JavaMailSender mailSender() throws IOException {
        Properties properties = configProperties();
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(properties.getProperty(HOST));
		mailSender.setPort(Integer.parseInt(properties.getProperty(PORT)));
		mailSender.setProtocol(properties.getProperty(PROTOCOL));
		mailSender.setUsername(properties.getProperty(USERNAME));
		mailSender.setPassword(properties.getProperty(PASSWORD));
        mailSender.setJavaMailProperties(javaMailProperties());
		return mailSender;
    }

    private Properties configProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new ClassPathResource(PROPERTIES_FILE).getInputStream());
        return properties;
	}
    
    private Properties javaMailProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new ClassPathResource(JAVA_MAIL_FILE).getInputStream());
        return properties;
	}
}

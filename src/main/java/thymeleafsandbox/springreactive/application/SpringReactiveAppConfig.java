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
package thymeleafsandbox.springreactive.application;


import java.io.IOException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan("thymeleafsandbox.springreactive.business")
public class SpringReactiveAppConfig implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringReactiveAppConfig.class);


    private ApplicationContext applicationContext;


    public SpringReactiveAppConfig() {
        super();
    }


    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Bean
    public DataSource dataSource() {
        try {
            final Resource dbResource = this.applicationContext.getResource("classpath:data/chinook.sqlite");
            logger.debug("Database path: " + dbResource.getURL().getPath());
            final BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("org.sqlite.JDBC");
            dataSource.setUrl("jdbc:sqlite:" + dbResource.getURL().getPath());
            return dataSource;
        } catch (final IOException e) {
            throw new ApplicationContextException("Error initializing database", e);
        }
    }


    @Bean
    public JdbcTemplate jdbcTemplate() {
        final JdbcTemplate template = new JdbcTemplate(dataSource());
        return template;
    }

}

/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.examples.springsecurity6.websecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    public SpringSecurityConfig() {
        super();
    }


    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            .formLogin(formLogin -> formLogin
                    .loginPage("/login.html")
                    .failureUrl("/login-error.html"))
            .logout(logout -> logout
                    .logoutSuccessUrl("/index.html"))
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(
                            new AntPathRequestMatcher("/"),
                            new AntPathRequestMatcher("/index.html"),
                            new AntPathRequestMatcher("/login.html"),
                            new AntPathRequestMatcher("/css/**"),
                            new AntPathRequestMatcher("/favicon.ico")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                    .requestMatchers(new AntPathRequestMatcher("/user/**")).hasRole("USER")
                    .requestMatchers(new AntPathRequestMatcher("/shared/**")).hasAnyRole("USER","ADMIN")
                    .anyRequest().authenticated())
            .exceptionHandling(handling -> handling
                    .accessDeniedPage("/403.html"));
        return http.build();
    }


    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("jim").password("{noop}demo").roles("ADMIN").build(),
                User.withUsername("bob").password("{noop}demo").roles("USER").build(),
                User.withUsername("ted").password("{noop}demo").roles("USER","ADMIN").build());
    }


}

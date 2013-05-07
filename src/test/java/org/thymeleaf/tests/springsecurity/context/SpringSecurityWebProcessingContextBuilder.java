/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.tests.springsecurity.context;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.context.web.SpringWebProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;



public class SpringSecurityWebProcessingContextBuilder extends SpringWebProcessingContextBuilder {


    
    public SpringSecurityWebProcessingContextBuilder() {
        super();
    }

    @Override
    protected WebApplicationContext createApplicationContext(
            final ITestContext testContext, final ITestMessages testMessages,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final Locale locale,
            final Map<String, Object> variables) {

        final XmlWebApplicationContext appCtx = new XmlWebApplicationContext();
        
        appCtx.setServletContext(servletContext);
        appCtx.setConfigLocation("classpath:org/thymeleaf/tests/springsecurity/context/applicationContext-security.xml");
        
        appCtx.refresh();

        
        final AuthenticationManager authenticationManager = (AuthenticationManager) appCtx.getBean("authenticationManager");
        
        final UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("jim", "demo");
        final Authentication authentication = authenticationManager.authenticate(token);
        
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        
        final HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        
        return appCtx;
        
    }

    
    
    
}

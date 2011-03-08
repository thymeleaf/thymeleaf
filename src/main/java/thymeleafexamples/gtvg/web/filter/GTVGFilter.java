/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package thymeleafexamples.gtvg.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import thymeleafexamples.gtvg.business.entities.User;
import thymeleafexamples.gtvg.web.application.GTVGApplication;
import thymeleafexamples.gtvg.web.controller.IGTVGController;


public class GTVGFilter implements Filter {

    private static final long serialVersionUID = -2976485624834964853L;

    
    
    public GTVGFilter() {
        super();
    }
    
    
    
    private static void addUserToSession(final HttpServletRequest request) {
        // Simulate a real user session by adding a user object
        request.getSession(true).setAttribute("user", new User("John", "Apricot", "Antarctica", null));
    }




    public void init(final FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }




    public void doFilter(final ServletRequest request, final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        addUserToSession((HttpServletRequest)request);
        if (!process((HttpServletRequest)request, (HttpServletResponse)response)) {
            chain.doFilter(request, response);
        }
    }




    public void destroy() {
        // nothing to do
    }

    


    private boolean process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        try {
            
            /*
             * Query controller/URL mapping and obtain the controller
             * that will process the request.
             */
            IGTVGController controller = GTVGApplication.resolveControllerForRequest(request);
            if (controller == null) {
                return false;
            }

            /*
             * Obtain the TemplateEngine instance.
             */
            TemplateEngine templateEngine = GTVGApplication.getTemplateEngine();

            /*
             * Execute the controller and process view template,
             * obtaining an HTML String. 
             */
            String result = controller.process(request, response, templateEngine);

            /*
             * Write the response
             */
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            
            response.getWriter().write(result);
            
            return true;
            
        } catch (Exception e) {
            throw new ServletException(e);
        }
        
    }
    
    
    
}

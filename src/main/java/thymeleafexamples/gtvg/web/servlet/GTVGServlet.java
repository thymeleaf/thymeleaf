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
package thymeleafexamples.gtvg.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;

import thymeleafexamples.gtvg.web.application.GTVGApplication;
import thymeleafexamples.gtvg.web.controller.IGTVGController;


public class GTVGServlet extends HttpServlet {

    private static final long serialVersionUID = -2976485624834964853L;

    
    
    public GTVGServlet() {
        super();
    }
    
    
    
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        process(req, resp);
    }





    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        process(req, resp);
    }





    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        try {
            
            /*
             * Query controller/URL mapping and obtain the controller
             * that will process the request.
             */
            IGTVGController controller = GTVGApplication.resolveControllerForRequest(request);

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
            
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
        
    }
    
    
    
}

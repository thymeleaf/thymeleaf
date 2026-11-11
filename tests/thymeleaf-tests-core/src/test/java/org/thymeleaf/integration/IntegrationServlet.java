/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.integration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.function.Function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

/*
 * Entry point of the test application, structured the way a non-Spring application usually integrates
 * Thymeleaf: web application built once from the ServletContext, one exchange and WebContext per request,
 * content type set by the application, output written to the response writer. Exceptions are reported
 * as a 500 response containing the cause chain, so that tests can check them.
 */
public final class IntegrationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String APPLICATION_NAME = "integration-app";
    public static final Locale EXPLICIT_LOCALE = new Locale("gl");

    private final transient Function<IWebApplication, ITemplateEngine> engineFactory;
    private transient JakartaServletWebApplication application;
    private transient ITemplateEngine templateEngine;


    public IntegrationServlet(final Function<IWebApplication, ITemplateEngine> engineFactory) {
        super();
        this.engineFactory = engineFactory;
    }


    @Override
    public void init() throws ServletException {
        getServletContext().setAttribute("appName", APPLICATION_NAME);
        this.application = JakartaServletWebApplication.buildApplication(getServletContext());
        this.templateEngine = this.engineFactory.apply(this.application);
    }


    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        final String path = (request.getPathInfo() == null ? "/" : request.getPathInfo());

        final IServletWebExchange webExchange =
                (IntegrationRoutes.EXPLICIT_LOCALE_PATH.equals(path) ?
                        this.application.buildExchange(request, response, EXPLICIT_LOCALE) :
                        this.application.buildExchange(request, response));
        final WebContext context = new WebContext(webExchange, webExchange.getLocale());

        final String template = IntegrationRoutes.route(path, request, context);
        if (template == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(contentType(template));
        response.setCharacterEncoding("UTF-8");

        final PrintWriter writer = response.getWriter();
        try {
            this.templateEngine.process(template, context, writer);
        } catch (final RuntimeException e) {
            if (response.isCommitted()) {
                throw e;
            }
            response.resetBuffer();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            Throwable t = e;
            while (t != null) {
                writer.println(t.getClass().getName() + ": " + t.getMessage());
                t = t.getCause();
            }
        }

    }


    private static String contentType(final String template) {
        if (template.endsWith(".txt")) {
            return "text/plain";
        }
        if (template.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

}

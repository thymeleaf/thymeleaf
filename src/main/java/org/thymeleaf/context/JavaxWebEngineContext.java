/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Basic <b>web</b> implementation of the {@link IEngineContext} interface, based on the Servlet API.
 * </p>
 * <p>
 *   This is the context implementation that will be used by default for web processing. Note that <b>this is an
 *   internal implementation, and there is no reason for users' code to directly reference or use it instead
 *   of its implemented interfaces</b>.
 * </p>
 * <p>
 *   This class is NOT thread-safe. Thread-safety is not a requirement for context implementations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public class JavaxWebEngineContext extends AbstractWebEngineContext implements IEngineContext, IJavaxWebContext {


    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final ServletContext servletContext;

    private final AbstractRequestAttributesEngineContext requestAttributesVariablesEngineContext;
    private final Map<String,Object> requestParametersVariablesMap;
    private final Map<String,Object> sessionAttributesVariablesMap;
    private final Map<String,Object> applicationAttributesVariablesMap;




    /**
     * <p>
     *   Creates a new instance of this {@link IEngineContext} implementation binding engine execution to
     *   the Servlet API.
     * </p>
     * <p>
     *   Note that implementations of {@link IEngineContext} are not meant to be used in order to call
     *   the template engine (use implementations of {@link IContext} such as {@link Context} or {@link JavaxWebContext}
     *   instead). This is therefore mostly an <b>internal</b> implementation, and users should have no reason
     *   to ever call this constructor except in very specific integration/extension scenarios.
     * </p>
     *
     * @param configuration the configuration instance being used.
     * @param templateData the template data for the template to be processed.
     * @param templateResolutionAttributes the template resolution attributes.
     * @param request the servlet request object.
     * @param response the servlet response object.
     * @param servletContext the servlet context object.
     * @param locale the locale.
     * @param variables the context variables, probably coming from another {@link IContext} implementation.
     */
    public JavaxWebEngineContext(
            final IEngineConfiguration configuration,
            final TemplateData templateData,
            final Map<String,Object> templateResolutionAttributes,
            final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext,
            final Locale locale,
            final Map<String, Object> variables) {

        super(configuration, templateResolutionAttributes, locale);

        Validate.notNull(request, "Request cannot be null in web variables map");
        Validate.notNull(response, "Response cannot be null in web variables map");
        Validate.notNull(servletContext, "Servlet Context cannot be null in web variables map");

        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
        this.servletContext = servletContext;

        this.requestAttributesVariablesEngineContext =
                new JavaxRequestAttributesEngineContext(configuration, templateData, templateResolutionAttributes,locale, variables,  this.request);
        this.requestParametersVariablesMap = new JavaxRequestParametersMap(this.request);
        this.applicationAttributesVariablesMap = new ServletContextAttributesMap(this.servletContext);
        this.sessionAttributesVariablesMap = new SessionAttributesMap(this.session);

    }

    @Override
    protected IEngineContext getRequestAttributesEngineContext() {
        return this.requestAttributesVariablesEngineContext;
    }

    public String getStringRepresentationByLevel() {
        return this.requestAttributesVariablesEngineContext.getStringRepresentationByLevel();
    }

    @Override
    public Map<String, Object> getRequestParametersVariablesMap() {
        return this.requestParametersVariablesMap;
    }

    @Override
    public Map<String, Object> getApplicationAttributesVariablesMap() {
        return this.applicationAttributesVariablesMap;
    }

    @Override
    public Map<String, Object> getSessionAttributesVariablesMap() {
        return this.sessionAttributesVariablesMap;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }


    public HttpServletResponse getResponse() {
        return this.response;
    }


    public HttpSession getSession() {
        return this.session;
    }


    public ServletContext getServletContext() {
        return this.servletContext;
    }






    @Override
    public String toString() {
        // Request parameters, session and servlet context can be safely ignored here
        return getRequestAttributesEngineContext().toString();
    }






    private static final class SessionAttributesMap extends AbstractAttributesMap {

        private final HttpSession session;

        SessionAttributesMap(final HttpSession session) {
            super();
            this.session = session;
        }

        boolean attributesNotDefined() {
            return this.session == null;
        }

        Enumeration<String> getAttributeNames() {
            return this.session.getAttributeNames();
        }

        Object getAttribute(String attributeName) {
            return this.session.getAttribute(attributeName);
        }

    }




    private static final class ServletContextAttributesMap extends AbstractAttributesMap {

        private final ServletContext servletContext;

        ServletContextAttributesMap(final ServletContext servletContext) {
            super();
            this.servletContext = servletContext;
        }


        @Override
        boolean attributesNotDefined() {
            return this.servletContext == null;
        }

        @Override
        Enumeration<String> getAttributeNames() {
            return this.servletContext.getAttributeNames();
        }

        @Override
        Object getAttribute(String attributeName) {
            return this.servletContext.getAttribute(attributeName);
        }

    }




    private static final class JavaxRequestParametersMap extends AbstractRequestParametersMap {

        private final HttpServletRequest request;

        JavaxRequestParametersMap(final HttpServletRequest request) {
            super();
            this.request = request;
        }


        @Override
        Map<String, Object> getParameterMap() {
            return this.request.getParameterMap();
        }

        @Override
        String[] getParameterValues(String parameterName) {
            return this.request.getParameterValues(parameterName);
        }
    }



    private static final class JavaxRequestAttributesEngineContext extends AbstractRequestAttributesEngineContext<HttpServletRequest> {

        JavaxRequestAttributesEngineContext(
            final IEngineConfiguration configuration,
            final TemplateData templateData,
            final Map<String,Object> templateResolutionAttributes,
            final Locale locale,
            final Map<String, Object> variables,
            final HttpServletRequest request) {
            super(configuration, templateData, templateResolutionAttributes, request, locale, variables);
        }

        @Override
        Enumeration<String> getAttributeNames() {
            return getRequest().getAttributeNames();
        }

        @Override
        Object getAttribute(String attributeName) {
            return getRequest().getAttribute(attributeName);
        }

        @Override
        void setAttribute(String attributeName, Object attributeValue) {
            getRequest().setAttribute(attributeName, attributeValue);
        }
    }





}

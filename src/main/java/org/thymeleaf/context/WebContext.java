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
package org.thymeleaf.context;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.thymeleaf.util.Validate;

/**
 * <p>
 *   Standard implementation for the {@link IWebContext} interface.
 * </p>
 * <p>
 *   This {@link IContext} implementation uses a {@link WebContextExecutionInfo} object as its
 *   {@link IContextExecutionInfo} implementation.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @author Josh Long
 * @since 1.0
 */
public class WebContext
        extends AbstractContext
        implements IWebContext {

    /**
     * <p>
     *   Name of the context variable that contains the request parameters.
     * </p>
     */
    public static final String PARAM_VARIABLE_NAME = "param";

    /**
     * <p>
     *   Name of the context variable that contains the session attributes.
     * </p>
     */
    public static final String SESSION_VARIABLE_NAME = "session";

    /**
     * <p>
     *   Name of the context variable that contains the application (servlet context)
     *   attributes.
     * </p>
     */
    public static final String APPLICATION_VARIABLE_NAME = "application";

    private final HttpServletRequest httpServletRequest;
    private final HttpSession httpSession;
    private final ServletContext servletContext;

    private final VariablesMap<String, String[]> requestParameters;
    private final VariablesMap<String, Object> requestAttributes;
    private final VariablesMap<String, Object> sessionAttributes;
    private final VariablesMap<String, Object> applicationAttributes;



    
    
    
    /**
     * <p>
     *   Create an instance without specifying a locale. Using this constructor,
     *   the default locale (<tt>Locale.getDefault()</tt>) will be used.
     * </p>
     *
     * @since 1.1.3
     * @param request the {@link HttpServletRequest} that this context will be related to.
     * @param servletContext the servlet context object
     */
    public WebContext(final HttpServletRequest request, final ServletContext servletContext) {
        this(request, servletContext, Locale.getDefault());
    }

    
    /**
     * <p>
     *   Create an instance specifying a locale.
     * </p>
     *
     * @since 1.1.3
     * @param request the {@link HttpServletRequest} that this context will be related to.
     * @param servletContext the servlet context object
     * @param locale  the locale to be used.
     */
    public WebContext(final HttpServletRequest request, final ServletContext servletContext, final Locale locale) {
        this(request, servletContext, locale, new HashMap<String, Object>());
    }


    /**
     * <p>
     *   Create an instance specifying a locale and an initial set of context
     *   variables.
     * </p>
     *
     * @since 1.1.3
     * @param request   the {@link HttpServletRequest} that this context will be related to.
     * @param servletContext the servlet context object
     * @param locale    the locale to be used.
     * @param variables the initial set of context variables.
     */
    public WebContext(final HttpServletRequest request,
                      final ServletContext servletContext,
                      final Locale locale, final Map<String, ?> variables) {

        super(locale, variables);

        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(request, "Request cannot be null");

        final HttpSession session = request.getSession(false);

        this.httpServletRequest = request;
        this.httpSession = session;

        this.servletContext = servletContext;


        final Map<String, Object> totalVariables = new HashMap<String, Object>();

        final VariablesMap<String, Object> requestAttributesMap = new VariablesMap<String, Object>();
        final Enumeration<?> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String attributeName = (String) attributeNames.nextElement();
            requestAttributesMap.put(attributeName, request.getAttribute(attributeName));
        }
        totalVariables.putAll(requestAttributesMap);


        final VariablesMap<String, String[]> requestParametersMap = new VariablesMap<String, String[]>();
        final Enumeration<?> requestParameterNames = request.getParameterNames();
        while (requestParameterNames.hasMoreElements()) {
            final String requestParameterName = (String) requestParameterNames.nextElement();
            final String[] requestParameterValues = request.getParameterValues(requestParameterName);
            requestParametersMap.put(requestParameterName, requestParameterValues);
        }
        totalVariables.put(PARAM_VARIABLE_NAME, requestParametersMap);


        final VariablesMap<String, Object> sessionAttributesMap = new VariablesMap<String, Object>();
        if (null != session) {
            final Enumeration<?> sessionAttributeNames = session.getAttributeNames();
            while (sessionAttributeNames.hasMoreElements()) {
                final String sessionAttributeName = (String) sessionAttributeNames.nextElement();
                sessionAttributesMap.put(sessionAttributeName, session.getAttribute(sessionAttributeName));
            }
        }
        totalVariables.put(SESSION_VARIABLE_NAME, sessionAttributesMap);

        final VariablesMap<String, Object> applicationAttributesMap = new VariablesMap<String, Object>();
        final Enumeration<?> applicationAttributeNames = this.servletContext.getAttributeNames();
        while (applicationAttributeNames.hasMoreElements()) {
            final String applicationAttributeName = (String) applicationAttributeNames.nextElement();
            applicationAttributesMap.put(applicationAttributeName, this.servletContext.getAttribute(applicationAttributeName));
        }
        totalVariables.put(APPLICATION_VARIABLE_NAME, applicationAttributesMap);

        setVariables(totalVariables);
        this.requestParameters = requestParametersMap;
        this.requestAttributes = requestAttributesMap;
        this.sessionAttributes = sessionAttributesMap;
        this.applicationAttributes = applicationAttributesMap;

    }


    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    public HttpSession getHttpSession() {
        return this.httpSession;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }


    public VariablesMap<String, String[]> getRequestParameters() {
        return this.requestParameters;
    }


    public VariablesMap<String, Object> getRequestAttributes() {
        return this.requestAttributes;
    }


    public VariablesMap<String, Object> getSessionAttributes() {
        return this.sessionAttributes;
    }


    public VariablesMap<String, Object> getApplicationAttributes() {
        return this.applicationAttributes;
    }


    @Override
    protected IContextExecutionInfo buildContextExecutionInfo(final String templateName) {
        final Calendar now = Calendar.getInstance();
        return new WebContextExecutionInfo(templateName, now);
    }


}

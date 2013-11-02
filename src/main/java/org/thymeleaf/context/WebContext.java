/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    
    
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final ServletContext servletContext;

    
    
    
    /**
     * <p>
     *   Create an instance without specifying a locale. Using this constructor,
     *   the default locale (<tt>Locale.getDefault()</tt>) will be used.
     * </p>
     *
     * @since 2.0.9
     * @param request the {@link HttpServletRequest} that this context will be related to.
     * @param response   the {@link HttpServletResponse} that this context will be related to.
     * @param servletContext the servlet context object
     */
    public WebContext(final HttpServletRequest request, final HttpServletResponse response, 
            final ServletContext servletContext) {
        this(request, response, servletContext, Locale.getDefault());
    }

    
    /**
     * <p>
     *   Create an instance specifying a locale.
     * </p>
     *
     * @since 2.0.9
     * @param request the {@link HttpServletRequest} that this context will be related to.
     * @param response   the {@link HttpServletResponse} that this context will be related to.
     * @param servletContext the servlet context object
     * @param locale  the locale to be used.
     */
    public WebContext(final HttpServletRequest request, final HttpServletResponse response,
            final ServletContext servletContext, final Locale locale) {
        this(request, response, servletContext, locale, null);
    }


    /**
     * <p>
     *   Create an instance specifying a locale and an initial set of context
     *   variables.
     * </p>
     *
     * @since 2.0.9
     * @param request   the {@link HttpServletRequest} that this context will be related to.
     * @param response   the {@link HttpServletResponse} that this context will be related to.
     * @param servletContext the servlet context object
     * @param locale    the locale to be used.
     * @param variables the initial set of context variables. Can be null if no variables are to added.
     */
    public WebContext(final HttpServletRequest request,
                      final HttpServletResponse response,
                      final ServletContext servletContext,
                      final Locale locale, final Map<String, ?> variables) {

        super(locale, new WebVariablesMap(request, servletContext, variables));

        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(request, "Request cannot be null");
        // "response" can be null for legacy compatibility reasons (deprecated methods)
        
        this.httpServletRequest = request;
        this.httpServletResponse = response;
        this.servletContext = servletContext;
        
    }


    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return this.httpServletResponse;
    }

    public HttpSession getHttpSession() {
        return this.httpServletRequest.getSession(false);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }


    /**
     * @deprecated Get parameters from the HttpServletRequest object directly instead. Will be removed in 3.0.
     */
    @Deprecated
    public VariablesMap<String, String[]> getRequestParameters() {
        return getWebVariablesMap().getRequestParamsVariablesMap();
    }


    /**
     * @deprecated Get attributes from the HttpServletRequest object directly instead. Will be removed in 3.0.
     */
    @Deprecated
    public VariablesMap<String, Object> getRequestAttributes() {
        return getWebVariablesMap();
    }


    /**
     * @deprecated Get attributes from the HttpSession object directly instead. Will be removed in 3.0.
     */
    @Deprecated
    public VariablesMap<String, Object> getSessionAttributes() {
        return getWebVariablesMap().getSessionVariablesMap();
    }


    /**
     * @deprecated Get attributes from the ServletContext object directly instead. Will be removed in 3.0.
     */
    @Deprecated
    public VariablesMap<String, Object> getApplicationAttributes() {
        return getWebVariablesMap().getServletContextVariablesMap();
    }


    /**
     * @deprecated Get variables map from the {@link #getVariables()} method directly. Will be removed in 3.0.
     */
    @Deprecated
    WebVariablesMap getWebVariablesMap() {
        return (WebVariablesMap) getVariables();
    }
    

    @Override
    protected IContextExecutionInfo buildContextExecutionInfo(final String templateName) {
        final Calendar now = Calendar.getInstance();
        return new WebContextExecutionInfo(templateName, now);
    }


}

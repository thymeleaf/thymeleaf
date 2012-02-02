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
package org.thymeleaf.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;




/**
 * <p>
 *   Subinterface of {@link IContext} for implementations that can be
 *   used for creating HTML/XHTML interfaces in web applications.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface IWebContext extends IContext {

    /**
     * <p>
     *   Returns the {@link HttpServletRequest} object associated with the
     *   request this context has been created for.
     * </p>
     * 
     * @since 1.1.2
     * @return the HTTP servlet request.
     */
    public HttpServletRequest getHttpServletRequest();

    /**
     * <p>
     *   Returns the {@link HttpSession} object associated with the
     *   request this context has been created for.
     * </p>
     * 
     * @since 1.1.2
     * @return the HTTP session.
     */
    public HttpSession getHttpSession();
    

    /**
     * <p>
     *   Returns the {@link ServletContext} object associated with the
     *   web application.
     * </p>
     * 
     * @return the servlet context.
     */
    public ServletContext getServletContext();


    /**
     * <p>
     *   Returns a {@link VariablesMap} object with all the parameters associated
     *   with the {@link javax.servlet.http.HttpServletRequest} used for creating the context.
     * </p>
     * 
     * @return the maps of request parameters.
     */
    public VariablesMap<String,String[]> getRequestParameters();
    
    /**
     * <p>
     *   Returns a {@link VariablesMap} object with all the attributes associated
     *   with the {@link javax.servlet.http.HttpServletRequest} used for creating the context.
     * </p>
     * 
     * @return the maps of request attributes.
     */
    public VariablesMap<String,Object> getRequestAttributes();
    
    /**
     * <p>
     *   Returns a {@link VariablesMap} object with all the attributes associated
     *   with the {@link javax.servlet.http.HttpSession} object linked to the
     *   {@link javax.servlet.http.HttpServletRequest} used for creating the context.
     * </p>
     * 
     * @return the maps of session attributes.
     */
    public VariablesMap<String,Object> getSessionAttributes();
    
    /**
     * <p>
     *   Returns a {@link VariablesMap} object with all the attributes associated
     *   with the {@link ServletContext} object linked to the
     *   {@link javax.servlet.http.HttpServletRequest} used for creating the context.
     * </p>
     * 
     * @return the maps of application attributes.
     */
    public VariablesMap<String,Object> getApplicationAttributes();
    
}

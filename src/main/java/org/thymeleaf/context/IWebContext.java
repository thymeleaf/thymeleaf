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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *   Specialization of the {@link IContext} interface to be implemented by contexts used for template
 *   processing in web environments.
 * </p>
 * <p>
 *   Objects implementing this interface add to the usual {@link IContext} data the Servlet-API-related
 *   artifacts needed to perform web-oriented functions such as URL rewriting or request/session access.
 * </p>
 * <p>
 *   Note a class with this name existed since 1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface IWebContext extends IContext {

    /**
     * <p>
     *   Returns the {@link HttpServletRequest} object associated with the template execution.
     * </p>
     *
     * @return the request object.
     */
    public HttpServletRequest getRequest();

    /**
     * <p>
     *   Returns the {@link HttpServletResponse} object associated with the template execution.
     * </p>
     *
     * @return the response object.
     */
    public HttpServletResponse getResponse();

    /**
     * <p>
     *   Returns the {@link HttpSession} object associated with the template execution, or null if
     *   there is no session.
     * </p>
     *
     * @return the session object. Might be null if no session has been created.
     */
    public HttpSession getSession();

    /**
     * <p>
     *   Returns the {@link ServletContext} object associated with the template execution.
     * </p>
     *
     * @return the servlet context object.
     */
    public ServletContext getServletContext();

}

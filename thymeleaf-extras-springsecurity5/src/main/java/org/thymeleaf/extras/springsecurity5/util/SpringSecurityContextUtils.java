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
package org.thymeleaf.extras.springsecurity5.util;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.1
 *
 */
public final class SpringSecurityContextUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityContextUtils.class);

    /**
     * This is the name of the model attribute that will hold the (asychronously resolved)
     * {@code SecurityContext} object.
     */
    public static final String SECURITY_CONTEXT_MODEL_ATTRIBUTE_NAME = "thymeleafSpringSecurityContext";




    private SpringSecurityContextUtils() {
        super();
    }



    public static ApplicationContext getApplicationContext(final IContext context) {

        if (SpringVersionSpecificUtils.isWebMvcContext(context)) {
            return SpringSecurityWebMvcApplicationContextUtils.findRequiredWebApplicationContext(context);
        }

        if (SpringVersionSpecificUtils.isWebFluxContext(context)) {
            return SpringSecurityWebFluxApplicationContextUtils.findRequiredApplicationContext(context);
        }

        throw new IllegalStateException(
                "No ApplicationContext found for Spring Security: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }



    public static Object getRequestAttribute(final IContext context, final String attributeName) {

        if (SpringVersionSpecificUtils.isWebMvcContext(context)) {
            return SpringSecurityWebMvcApplicationContextUtils.getRequestAttribute(context, attributeName);
        }

        if (SpringVersionSpecificUtils.isWebFluxContext(context)) {
            return SpringSecurityWebFluxApplicationContextUtils.getRequestAttribute(context, attributeName);
        }

        throw new IllegalStateException(
                "Could not obtain request attributes: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }



    public static String getContextPath(final IContext context) {

        if (SpringVersionSpecificUtils.isWebMvcContext(context)) {
            return SpringSecurityWebMvcApplicationContextUtils.getContextPath(context);
        }

        if (SpringVersionSpecificUtils.isWebFluxContext(context)) {
            return SpringSecurityWebFluxApplicationContextUtils.getContextPath(context);
        }

        throw new IllegalStateException(
                "Could not obtain context path: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }


    public static Authentication getAuthenticationObject(final IContext context) {

        if (SpringVersionSpecificUtils.isWebMvcContext(context)) {
            return SpringSecurityWebMvcApplicationContextUtils.getAuthenticationObject();
        }

        if (SpringVersionSpecificUtils.isWebFluxContext(context)) {
            return SpringSecurityWebFluxApplicationContextUtils.getAuthenticationObject(context);
        }

        throw new IllegalStateException(
                "Could not obtain authentication object: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }



    private static final class SpringSecurityWebMvcApplicationContextUtils {


        static Object getRequestAttribute(final IContext context, final String attributeName) {
            final javax.servlet.http.HttpServletRequest request = ((IWebContext)context).getRequest();
            return request.getAttribute(attributeName);
        }


        static String getContextPath(final IContext context) {
            final javax.servlet.http.HttpServletRequest request = ((IWebContext)context).getRequest();
            return request.getContextPath();
        }


        /*
         * This method mimics the behaviour in
         * org.springframework.security.web.context.support.SecurityWebApplicationContextUtils#findRequiredWebApplicationContext(sc),
         * which provides a default mechanism for looking for the WebApplicationContext as an attribute of the
         * ServletContext that might have been declared with a non-standard name (if it had, it would be
         * detected by WebApplicationContextUtils.getWebApplicationContext(sc). This is a behaviour directly
         * supported in Spring Framework >= 4.2 thanks to
         * org.springframework.web.context.support.WebApplicationContextUtils#findWebApplicationContext(sc).
         *
         * Unfortunately, the org.springframework.security.web.context.support.SecurityWebApplicationContextUtils class is
         * only available since Spring Security 4.1, so we cannot simply call it if we want to support Spring Security 4.0.
         * That's why this method basically mimics its behaviour.
         */
        static ApplicationContext findRequiredWebApplicationContext(final IContext context) {

            final javax.servlet.ServletContext servletContext = ((IWebContext)context).getServletContext();

            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            if (wac == null) {
                final Enumeration<String> attrNames = servletContext.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    final String attrName = attrNames.nextElement();
                    final Object attrValue = servletContext.getAttribute(attrName);
                    if (attrValue instanceof WebApplicationContext) {
                        if (wac != null) {
                            throw new IllegalStateException("No unique WebApplicationContext found: more than one " +
                                    "DispatcherServlet registered with publishContext=true?");
                        }
                        wac = (WebApplicationContext) attrValue;
                    }
                }
            }

            if (wac == null) {
                throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
            }

            return wac;

        }


        static Authentication getAuthenticationObject() {
            final SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] No security context found, no authentication object returned.",
                            new Object[] {TemplateEngine.threadIndex()});
                }
                return null;
            }
            return securityContext.getAuthentication();
        }


    }




    private static final class SpringSecurityWebFluxApplicationContextUtils {

        static Object getRequestAttribute(final IContext context, final String attributeName) {
            final ServerWebExchange exchange = SpringVersionSpecificUtils.getServerWebExchange(context);
            return exchange.getAttribute(attributeName);
        }


        static String getContextPath(final IContext context) {
            final ServerWebExchange exchange = SpringVersionSpecificUtils.getServerWebExchange(context);
            return exchange.getRequest().getPath().contextPath().value();
        }


        static ApplicationContext findRequiredApplicationContext(final IContext context) {

            final ServerWebExchange exchange = SpringVersionSpecificUtils.getServerWebExchange(context);
            final ApplicationContext applicationContext = exchange.getApplicationContext();

            if (applicationContext == null) {
                throw new IllegalStateException("No ApplicationContext found in context for WebFlux application");
            }

            return applicationContext;

        }


        static Authentication getAuthenticationObject(final IContext context) {
            final SecurityContext securityContext = (SecurityContext) context.getVariable(SECURITY_CONTEXT_MODEL_ATTRIBUTE_NAME);
            if (securityContext == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] No security context found, no authentication object returned.",
                            new Object[] {TemplateEngine.threadIndex()});
                }
                return null;
            }
            return securityContext.getAuthentication();
        }


    }


}

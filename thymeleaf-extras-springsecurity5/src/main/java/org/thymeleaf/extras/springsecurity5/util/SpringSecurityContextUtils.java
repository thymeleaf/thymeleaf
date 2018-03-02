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

import java.security.Principal;
import java.util.Enumeration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.1
 *
 */
public final class SpringSecurityContextUtils {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityContextUtils.class);

    // This constant is used for caching the Authentication object as a ServerWebExchange attribute the
    // first time it is computed (by blocking on the stream that obtains it from the SecurityContext repository).
    private static final String SERVER_WEB_EXCHANGE_ATTRIBUTE_AUTHENTICATION =
            SpringSecurityDialect.class.getName() + ".AUTHENTICATION";


    private SpringSecurityContextUtils() {
        super();
    }



    public static ApplicationContext getApplicationContext(final IContext context) {

        if (context instanceof IWebContext) {
            return SpringSecurityWebMvcApplicationContextUtils.findRequiredWebApplicationContext((IWebContext) context);
        }

        if (context instanceof ISpringWebFluxContext) {
            return SpringSecurityWebFluxApplicationContextUtils.findRequiredApplicationContext((ISpringWebFluxContext)context);
        }

        throw new IllegalStateException(
                "No ApplicationContext found for Spring Security: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }



    public static Object getRequestAttribute(final IContext context, final String attributeName) {

        if (context instanceof IWebContext) {
            return SpringSecurityWebMvcApplicationContextUtils.getRequestAttribute((IWebContext) context, attributeName);
        }

        if (context instanceof ISpringWebFluxContext) {
            return SpringSecurityWebFluxApplicationContextUtils.getRequestAttribute((ISpringWebFluxContext)context, attributeName);
        }

        throw new IllegalStateException(
                "Could not obtain request attributes: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }



    public static String getContextPath(final IContext context) {

        if (context instanceof IWebContext) {
            return SpringSecurityWebMvcApplicationContextUtils.getContextPath((IWebContext) context);
        }

        if (context instanceof ISpringWebFluxContext) {
            return SpringSecurityWebFluxApplicationContextUtils.getContextPath((ISpringWebFluxContext)context);
        }

        throw new IllegalStateException(
                "Could not obtain context path: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }


    public static Authentication getAuthenticationObject(final IContext context) {

        if (context instanceof IWebContext) {
            return SpringSecurityWebMvcApplicationContextUtils.getAuthenticationObject();
        }

        if (context instanceof ISpringWebFluxContext) {
            return SpringSecurityWebFluxApplicationContextUtils.getAuthenticationObject((ISpringWebFluxContext)context);
        }

        throw new IllegalStateException(
                "Could not obtain authentication object: Thymeleaf context is neither an implementation of " +
                "IWebContext (for Spring MVC apps) nor ISpringWebFluxContext (for Spring WebFlux apps). " +
                "Thymeleaf's Spring Security support can only be used in web applications.");

    }



    private static final class SpringSecurityWebMvcApplicationContextUtils {


        static Object getRequestAttribute(final IWebContext context, final String attributeName) {
            final javax.servlet.http.HttpServletRequest request = context.getRequest();
            return request.getAttribute(attributeName);
        }


        static String getContextPath(final IWebContext context) {
            final javax.servlet.http.HttpServletRequest request = context.getRequest();
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
        static ApplicationContext findRequiredWebApplicationContext(final IWebContext context) {

            final javax.servlet.ServletContext servletContext = context.getServletContext();

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

        static Object getRequestAttribute(final ISpringWebFluxContext context, final String attributeName) {
            final ServerWebExchange exchange = context.getExchange();
            return exchange.getAttribute(attributeName);
        }


        static String getContextPath(final ISpringWebFluxContext context) {
            final ServerWebExchange exchange = context.getExchange();
            return exchange.getRequest().getPath().contextPath().value();
        }


        static ApplicationContext findRequiredApplicationContext(final ISpringWebFluxContext context) {

            final ServerWebExchange exchange = context.getExchange();
            final ApplicationContext applicationContext = exchange.getApplicationContext();

            if (applicationContext == null) {
                throw new IllegalStateException("No ApplicationContext found in context for WebFlux application");
            }

            return applicationContext;

        }


        static Authentication getAuthenticationObject(final ISpringWebFluxContext context) {

            final ServerWebExchange exchange = context.getExchange();
            final Optional<Authentication> cachedAuthentication =
                    exchange.getAttribute(SERVER_WEB_EXCHANGE_ATTRIBUTE_AUTHENTICATION);
            if (cachedAuthentication != null) {
                return cachedAuthentication.orElse(null);
            }

            final Principal auth2 = exchange.getPrincipal().block();
            final Mono<SecurityContext> securityContextStream = ReactiveSecurityContextHolder.getContext();
            final SecurityContext securityContext = securityContextStream.block();
            final Mono<Authentication> authenticationStream = securityContextStream.map(sc -> sc.getAuthentication());

            // Given Thymeleaf 3.0 does not allow resolution of reactive variables on-the-fly (i.e. on the middle
            // of template execution), there is no alternative here but to block.
            // NOTE however that this should only be noticeable if the SecurityContext is retrieved
            // from a repository that could actually need to block (i.e. a persisted WebSession).
            final Authentication authentication = authenticationStream.block();

            // Cache it for the next time
            exchange.getAttributes().put(SERVER_WEB_EXCHANGE_ATTRIBUTE_AUTHENTICATION, Optional.ofNullable(authentication));

            return authentication;

        }


    }


}

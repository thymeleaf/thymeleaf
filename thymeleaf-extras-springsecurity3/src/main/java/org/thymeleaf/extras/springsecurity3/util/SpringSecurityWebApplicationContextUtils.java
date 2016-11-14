/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.extras.springsecurity3.util;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.1
 *
 */
public final class SpringSecurityWebApplicationContextUtils {


    private SpringSecurityWebApplicationContextUtils() {
        super();
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
    public static WebApplicationContext findRequiredWebApplicationContext(final ServletContext servletContext) {

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


}

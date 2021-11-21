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
package org.thymeleaf.spring3.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.unbescape.uri.UriEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.12
 *
 */
public final class SpringRequestUtils {



    public static void checkViewNameNotInRequest(final String viewName, final HttpServletRequest request) {

        final String vn = StringUtils.pack(viewName);

        final String requestURI = StringUtils.pack(UriEscape.unescapeUriPath(request.getRequestURI()));

        boolean found = (requestURI != null && requestURI.contains(vn));
        if (!found) {
            final Enumeration<String> paramNames = request.getParameterNames();
            String[] paramValues;
            String paramValue;
            while (!found && paramNames.hasMoreElements()) {
                paramValues = request.getParameterValues(paramNames.nextElement());
                for (int i = 0; !found && i < paramValues.length; i++) {
                    paramValue = StringUtils.pack(paramValues[i]);
                    if (paramValue.contains(vn)) {
                        found = true;
                    }
                }
            }
        }

        if (found) {
            throw new TemplateProcessingException(
                    "View name is an executable expression, and it is present in a literal manner in " +
                    "request path or parameters, which is forbidden for security reasons.");
        }

    }




    private SpringRequestUtils() {
        super();
    }




}

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

        if (!containsExpression(vn)) {
            // We are only worried about expressions coming from user input, so if the view name contains no
            // expression at all, we should be safe at this stage.
            return;
        }

        boolean found = false;

        final String requestURI = StringUtils.pack(UriEscape.unescapeUriPath(request.getRequestURI()));
        if (requestURI != null && containsExpression(requestURI)) {
            // View name contains an expression, and it seems the path does too. This is too dangerous.
            found = true;
        }

        if (!found) {
            final Enumeration<String> paramNames = request.getParameterNames();
            String[] paramValues;
            String paramValue;
            while (!found && paramNames.hasMoreElements()) {
                paramValues = request.getParameterValues(paramNames.nextElement());
                for (int i = 0; !found && i < paramValues.length; i++) {
                    paramValue = StringUtils.pack(paramValues[i]);
                    if (paramValue != null && containsExpression(paramValue) && vn.contains(paramValue)) {
                        // Request parameter contains an expression, and it is contained in the view name. Too dangerous.
                        found = true;
                    }
                }
            }
        }

        if (found) {
            throw new TemplateProcessingException(
                    "View name contains an expression and so does either the URL path or one of the request " +
                    "parameters. This is forbidden in order to reduce the possibilities that direct user input " +
                    "is executed as a part of the view name.");
        }

    }


    private static boolean containsExpression(final String text) {
        final int textLen = text.length();
        char c;
        boolean expInit = false;
        for (int i = 0; i < textLen; i++) {
            c = text.charAt(i);
            if (!expInit) {
                if (c == '$' || c == '*' || c == '#' || c == '@' || c == '~') {
                    expInit = true;
                }
            } else {
                if (c == '{') {
                    return true;
                } else if (!Character.isWhitespace(c)) {
                    expInit = false;
                }
            }
        }
        return false;
    }




    private SpringRequestUtils() {
        super();
    }




}

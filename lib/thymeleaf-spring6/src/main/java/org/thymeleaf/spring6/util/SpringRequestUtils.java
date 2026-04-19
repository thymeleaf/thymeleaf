/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring6.util;

import java.util.Map;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.web.IWebRequest;
import org.unbescape.uri.UriEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.12
 *
 */
public final class SpringRequestUtils {



    public static void checkViewNameNotInRequest(final String viewName, final IWebRequest request) {

        final String vn = StringUtils.pack(viewName);

        if (!containsExpression(vn)) {
            // We are only worried about expressions coming from user input, so if the view name contains no
            // expression at all, we should be safe at this stage.
            return;
        }

        boolean found = false;

        final String pathWithinApplication =
                StringUtils.pack(UriEscape.unescapeUriPath(request.getPathWithinApplication()));
        if (pathWithinApplication != null && containsExpression(pathWithinApplication)) {
            // View name contains an expression, and it seems the path does too. This is too dangerous.
            found = true;
        }

        if (!found) {
            final Map<String,String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                for (final String[] parameterValues : parameterMap.values()) {
                    if (parameterValues == null) {
                        continue;
                    }
                    for (int i = 0; !found && i < parameterValues.length; i++) {
                        final String parameterValue = StringUtils.pack(parameterValues[i]);
                        if (parameterValue != null && containsExpression(parameterValue) && vn.contains(parameterValue)) {
                            // Request parameter contains an expression, and it is contained in the view name. Too dangerous.
                            found = true;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        if (!found) {
            final Map<String,String[]> cookieMap = request.getCookieMap();
            if (cookieMap != null && !cookieMap.isEmpty()) {
                for (final String[] cookieValues : cookieMap.values()) {
                    if (cookieValues == null) {
                        continue;
                    }
                    for (int i = 0; !found && i < cookieValues.length; i++) {
                        final String cookieValue = StringUtils.pack(cookieValues[i]);
                        if (cookieValue != null && containsExpression(cookieValue) && vn.contains(cookieValue)) {
                            // Cookie contains an expression, and it is contained in the view name. Too dangerous.
                            found = true;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        if (!found) {
            final Map<String,String[]> headerMap = request.getHeaderMap();
            if (headerMap != null && !headerMap.isEmpty()) {
                for (final String[] headerValues : headerMap.values()) {
                    if (headerValues == null) {
                        continue;
                    }
                    for (int i = 0; !found && i < headerValues.length; i++) {
                        final String headerValue = StringUtils.pack(headerValues[i]);
                        if (headerValue != null && containsExpression(headerValue) && vn.contains(headerValue)) {
                            // Header contains an expression, and it is contained in the view name. Too dangerous.
                            found = true;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        if (found) {
            throw new TemplateProcessingException(
                    "View name contains an expression and so does either the URL path or the request parameters " +
                    "or contents. This is forbidden in order to reduce the possibilities that direct user input " +
                    "is executed as a part of the view name.");
        }

    }


    private static boolean containsExpression(final String text) {
        final int textLen = text.length();
        char c;
        boolean expInit = false;
        boolean preInit = false;
        boolean lsubInit = false;
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
            if (c == '_' && (i + 1) < textLen && text.charAt(i + 1) == '_') {
                if (!preInit) {
                    preInit = true;
                    i++;
                } else {
                    return true;
                }
            }
            if (c == '|') {
                if (!lsubInit) {
                    lsubInit = true;
                } else {
                    return true;
                }
            }
        }
        return false;
    }



    private SpringRequestUtils() {
        super();
    }




}

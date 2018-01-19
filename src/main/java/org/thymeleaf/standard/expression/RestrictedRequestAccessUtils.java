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
package org.thymeleaf.standard.expression;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.thymeleaf.exceptions.TemplateProcessingException;

/**
 * <p>
 *   Utility class that wraps {@link HttpServletRequest} objects in order to restrict access to
 *   request parameters in specific restricted expression execution environments.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.9
 *
 */
public final class RestrictedRequestAccessUtils {


    public static Object wrapRequestObject(final Object obj) {
        if (obj == null || !(obj instanceof HttpServletRequest)) {
            return obj;
        }
        return new RestrictedRequestWrapper((HttpServletRequest)obj);
    }


    private RestrictedRequestAccessUtils() {
        super();
    }


    /*
     * This internal class allows the wrapping of the request in RESTRICTED execution environments (like certain
     * attribute processors) so that no direct access is given to (non-validated) user input like request parameters.
     */
    private static class RestrictedRequestWrapper extends HttpServletRequestWrapper {

        public RestrictedRequestWrapper(final HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(final String name) {
            throw createRestrictedParameterAccessException();
        }

        @Override
        public Map getParameterMap() {
            throw createRestrictedParameterAccessException();
        }

        @Override
        public String[] getParameterValues(final String name) {
            throw createRestrictedParameterAccessException();
        }

        @Override
        public String getQueryString() {
            throw createRestrictedParameterAccessException();
        }

        private static TemplateProcessingException createRestrictedParameterAccessException() {
            return new TemplateProcessingException(
                    "Access to request parameters is forbidden in this context. Note some restrictions apply to " +
                    "variable access. For example, direct access to request parameters is forbidden in preprocessing and " +
                    "unescaped expressions, in TEXT template mode, in fragment insertion specifications and " +
                    "in some specific attribute processors.");
        }

    }

}

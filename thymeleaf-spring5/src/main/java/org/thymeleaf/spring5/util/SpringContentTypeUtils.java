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
package org.thymeleaf.spring5.util;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.thymeleaf.util.ContentTypeUtils;


/**
 * <p>
 *   Utility class containing methods for computing content type-related data.
 * </p>
 * <p>
 *   This class is <strong>internal</strong> and should not be used from users code.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.6
 *
 */
public final class SpringContentTypeUtils {


    public static String computeViewContentType(
            final HttpServletRequest request, final String defaultContentType, final Charset defaultCharset) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }


        // First we will check if there is a content type already resolved by Spring's own content negotiation
        // mechanism (see ContentNegotiatingViewResolver, which is autoconfigured in Spring Boot)
        final MediaType negotiatedMediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
        if (negotiatedMediaType != null && negotiatedMediaType.isConcrete()) {
            final Charset negotiatedCharset = negotiatedMediaType.getCharset();
            if (negotiatedCharset != null) {
                return negotiatedMediaType.toString();
            } else {
                return ContentTypeUtils.combineContentTypeAndCharset(negotiatedMediaType.toString(), defaultCharset);
            }
        }


        // We will apply the default charset here because, after all, we are in an HTTP environment, and
        // the way charset is specified in HTTP is as a parameter in the same Content-Type HTTP header.
        final String combinedContentType =
                ContentTypeUtils.combineContentTypeAndCharset(defaultContentType, defaultCharset);

        // Maybe there is no value for 'defaultValue', but anyway we might want to preserve the charset
        // from the defaultContentType into the viewName-computed one
        final Charset combinedCharset =
                ContentTypeUtils.computeCharsetFromContentType(combinedContentType);

        // If the request path offers clues on the content type that would be more appropriate (because it
        // ends in ".html", ".xml", ".js", etc.), just use it
        final String requestPathContentType =
                ContentTypeUtils.computeContentTypeForRequestPath(request.getRequestURI(), combinedCharset);
        if (requestPathContentType != null) {
            return requestPathContentType;
        }

        // No way to determine a better/more specific content-type, so just return the (adequately combined) defaults
        return combinedContentType;

    }



    private SpringContentTypeUtils() {
        super();
    }




}

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
package org.thymeleaf.spring5.requestdata;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestDataValueProcessor;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
final class RequestDataValueProcessor4Delegate implements IRequestDataValueProcessorDelegate {



    public RequestDataValueProcessor4Delegate() {
        super();
    }



    public String processAction(
            final RequestContext requestContext, final HttpServletRequest request,
            final String action, final String httpMethod) {

        final RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
        if (processor == null) {
            return action;
        }

        // The "httpMethod" argument is ignored as of 3.1 and 3.2, but will be used in Spring 4.0
        return processor.processAction(request, action, httpMethod);

    }



    public String processFormFieldValue(
            final RequestContext requestContext, final HttpServletRequest request,
            final String name, final String value, final String type) {

        final RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
        if (processor == null) {
            return value;
        }

        return processor.processFormFieldValue(request, name, value, type);

    }



    public Map<String, String> getExtraHiddenFields(
            final RequestContext requestContext, final HttpServletRequest request) {

        final RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
        if (processor == null) {
            return null;
        }

        return processor.getExtraHiddenFields(request);

    }



    public String processUrl(
            final RequestContext requestContext, final HttpServletRequest request,
            final String url) {

        final RequestDataValueProcessor processor = requestContext.getRequestDataValueProcessor();
        if (processor == null) {
            return url;
        }

        return processor.processUrl(request, url);

    }


	
}

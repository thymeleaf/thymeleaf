/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.requestdata;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContext;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
interface IRequestDataValueProcessorDelegate {

    public String processAction(
            final RequestContext requestContext, final HttpServletRequest request,
            final String action, final String httpMethod);

    public String processFormFieldValue(
            final RequestContext requestContext, final HttpServletRequest request,
            final String name, final String value, final String type);

    public Map<String, String> getExtraHiddenFields(
            final RequestContext requestContext, final HttpServletRequest request);

    public String processUrl(
            final RequestContext requestContext, final HttpServletRequest request,
            final String url);

}

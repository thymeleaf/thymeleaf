/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.context.web;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.util.JakartaServletMockUtils;
import org.thymeleaf.testing.templateengine.util.TestNamingUtils;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;


public class JakartaServletTestWebExchangeBuilder implements ITestWebExchangeBuilder {


    private String method = JakartaServletMockUtils.DEFAULT_METHOD;
    private String scheme = JakartaServletMockUtils.DEFAULT_SCHEME;
    private String serverName = JakartaServletMockUtils.DEFAULT_SERVER_NAME;
    private int port = JakartaServletMockUtils.DEFAULT_SERVER_PORT;
    private String contextPath = JakartaServletMockUtils.DEFAULT_CONTEXT_PATH;
    private String contentType = JakartaServletMockUtils.DEFAULT_CONTENT_TYPE;
    private String characterEncoding = JakartaServletMockUtils.DEFAULT_CHARACTER_ENCODING;
    private Function<String,String> transformUrlFunction = JakartaServletMockUtils.DEFAULT_TRANSFORM_URL_FUNCTION;


    private JakartaServletTestWebExchangeBuilder() {
        super();
    }

    public static JakartaServletTestWebExchangeBuilder create() {
        return new JakartaServletTestWebExchangeBuilder();
    }


    public JakartaServletTestWebExchangeBuilder method(final String method) {
        Validate.notNull(method, "Method cannot be null");
        this.method = method;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder scheme(final String scheme) {
        Validate.notNull(scheme, "Scheme cannot be null");
        this.scheme = scheme;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder serverName(final String serverName) {
        Validate.notNull(serverName, "Server Name cannot be null");
        this.serverName = serverName;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder port(final int port) {
        this.port = port;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder contextPath(final String contextPath) {
        Validate.notNull(contextPath, "Context Path cannot be null");
        this.contextPath = contextPath;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder contentType(final String contentType) {
        Validate.notNull(contentType, "Content Type cannot be null");
        this.contentType = contentType;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder characterEncoding(final String characterEncoding) {
        Validate.notNull(characterEncoding, "Character Encoding cannot be null");
        this.characterEncoding = characterEncoding;
        return this;
    }

    public JakartaServletTestWebExchangeBuilder transformUrlFunction(final Function<String, String> transformUrlFunction) {
        Validate.notNull(transformUrlFunction, "transformUrl Function cannot be null");
        this.transformUrlFunction = transformUrlFunction;
        return this;
    }


    @Override
    public IWebExchange buildExchange(final ITest test,
                                      final Map<String, Object> exchangeAttributes,
                                      final Map<String, String[]> requestParameters,
                                      final Map<String, Object> sessionAttributes,
                                      final Map<String, Object> applicationAttributes,
                                      final Locale locale) {

        Validate.notNull(test, "Test cannot be null");
        Validate.notNull(exchangeAttributes, "Exchange attribute map cannot be null");
        Validate.notNull(requestParameters, "Request parameter map cannot be null");
        // session attribute map can be null
        Validate.notNull(applicationAttributes, "Application attribute map cannot be null");
        Validate.notNull(locale, "Locale cannot be null");

        final ServletContext servletContext =
                JakartaServletMockUtils.buildServletContext()
                        .attributeMap(applicationAttributes)
                        .build();

        final HttpSession httpSession =
                (sessionAttributes != null && !sessionAttributes.isEmpty())?
                        JakartaServletMockUtils.buildSession(servletContext)
                                .attributeMap(sessionAttributes)
                                .build()
                        : null;

        final HttpServletResponse httpServletResponse =
                JakartaServletMockUtils.buildResponse()
                        .transformUrlFunction(this.transformUrlFunction)
                        .build();

        final HttpServletRequest httpServletRequest =
                JakartaServletMockUtils.buildRequest(servletContext, TestNamingUtils.normalizeTestName(test.getName()))
                        .session(httpSession)
                        .attributeMap(exchangeAttributes)
                        .parameterMap(requestParameters)
                        .method(this.method)
                        .scheme(this.scheme)
                        .serverName(this.serverName)
                        .port(this.port)
                        .contextPath(this.contextPath)
                        .contentType(this.contentType)
                        .characterEncoding(this.characterEncoding)
                        .locale(locale)
                        .build();

        return JakartaServletWebApplication.
                    buildApplication(servletContext).buildExchange(httpServletRequest, httpServletResponse);

    }



}

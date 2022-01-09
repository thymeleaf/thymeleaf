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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.util.JavaxServletMockUtils;
import org.thymeleaf.util.Validate;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;


public class JavaxServletTestWebExchangeBuilder implements ITestWebExchangeBuilder {

    private static final String DEFAULT_METHOD = "GET";
    private static final String DEFAULT_SCHEME = "http";
    private static final String DEFAULT_SERVER_NAME = "testing-server";
    private static final int DEFAULT_SERVER_PORT = 80;
    private static final String DEFAULT_CONTEXT_PATH = "/testing";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    private static final Function<String,String> DEFAULT_TRANSFORM_URL_FUNCTION = (url) -> url;


    private String method = DEFAULT_METHOD;
    private String scheme = DEFAULT_SCHEME;
    private String serverName = DEFAULT_SERVER_NAME;
    private int port = DEFAULT_SERVER_PORT;
    private String contextPath = DEFAULT_CONTEXT_PATH;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private String characterEncoding = DEFAULT_CHARACTER_ENCODING;
    private Function<String,String> transformUrlFunction = DEFAULT_TRANSFORM_URL_FUNCTION;


    private JavaxServletTestWebExchangeBuilder() {
        super();
    }

    public static JavaxServletTestWebExchangeBuilder create() {
        return new JavaxServletTestWebExchangeBuilder();
    }


    public JavaxServletTestWebExchangeBuilder method(final String method) {
        Validate.notNull(method, "Method cannot be null");
        this.method = method;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder scheme(final String scheme) {
        Validate.notNull(scheme, "Scheme cannot be null");
        this.scheme = scheme;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder serverName(final String serverName) {
        Validate.notNull(serverName, "Server Name cannot be null");
        this.serverName = serverName;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder port(final int port) {
        this.port = port;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder contextPath(final String contextPath) {
        Validate.notNull(contextPath, "Context Path cannot be null");
        this.contextPath = contextPath;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder contentType(final String contentType) {
        Validate.notNull(contentType, "Content Type cannot be null");
        this.contentType = contentType;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder characterEncoding(final String characterEncoding) {
        Validate.notNull(characterEncoding, "Character Encoding cannot be null");
        this.characterEncoding = characterEncoding;
        return this;
    }

    public JavaxServletTestWebExchangeBuilder transformUrlFunction(final Function<String, String> transformUrlFunction) {
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
                JavaxServletMockUtils.createMockServletContext(applicationAttributes, this.contextPath);
        final HttpSession httpSession =
                (sessionAttributes != null && !sessionAttributes.isEmpty())?
                        JavaxServletMockUtils.createMockHttpSession(servletContext, sessionAttributes) : null;
        final HttpServletResponse httpServletResponse =
                JavaxServletMockUtils.createMockHttpServletResponse(this.transformUrlFunction);
        final HttpServletRequest httpServletRequest =
                JavaxServletMockUtils.createMockHttpServletRequest(
                        test.getName(), httpSession, exchangeAttributes, requestParameters,
                        this.method, this.scheme, this.serverName, this.port, this.contextPath,
                        this.contentType, this.characterEncoding, locale);

        return JavaxServletWebApplication.
                    buildApplication(servletContext).buildExchange(httpServletRequest, httpServletResponse);

    }



}

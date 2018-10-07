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
package org.thymeleaf.extras.springsecurity5.dialect;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.extras.springsecurity5.dialect.expression.SpringSecurityExpressionObjectFactory;
import org.thymeleaf.extras.springsecurity5.dialect.processor.AuthenticationAttrProcessor;
import org.thymeleaf.extras.springsecurity5.dialect.processor.AuthorizeAclAttrProcessor;
import org.thymeleaf.extras.springsecurity5.dialect.processor.AuthorizeAttrProcessor;
import org.thymeleaf.extras.springsecurity5.dialect.processor.AuthorizeUrlAttrProcessor;
import org.thymeleaf.extras.springsecurity5.util.SpringSecurityContextUtils;
import org.thymeleaf.extras.springsecurity5.util.SpringVersionUtils;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import reactor.core.publisher.Mono;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class SpringSecurityDialect
        extends AbstractDialect
        implements IProcessorDialect, IExpressionObjectDialect, IExecutionAttributeDialect {

    public static final String NAME = "SpringSecurity";
    public static final String DEFAULT_PREFIX = "sec";
    public static final int PROCESSOR_PRECEDENCE = 800;

    
    private static final IExpressionObjectFactory EXPRESSION_OBJECT_FACTORY = new SpringSecurityExpressionObjectFactory();
    private static final Map<String,Object> EXECUTION_ATTRIBUTES;

    // These execution attributes will force the asynchronous resolution of the SecurityContext and the CsrfToken. This
    // will avoid the need to block in order to obtain any of them during SpringSecurity-enabled execution. This will
    // also mean both objects will be always resolved for every template execution even if not needed, but there is
    // no way Thymeleaf could know beforehand if they will be needed for a template or not, so any alternative to this
    // would involve blocking.
    // NOTE here we are not using the constant from the ReactiveThymeleafView class (instead we replicate its same
    // value "ThymeleafReactiveModelAdditions:" so that we don't create a hard dependency on the thymeleaf-spring5
    // package, so that this class could be used in the future with, for example, a
    // thymeleaf-spring6 integration package if needed.
    private static final String SECURITY_CONTEXT_EXECUTION_ATTRIBUTE_NAME =
            "ThymeleafReactiveModelAdditions:" + SpringSecurityContextUtils.SECURITY_CONTEXT_MODEL_ATTRIBUTE_NAME;
    private static final String CSRF_EXECUTION_ATTRIBUTE_NAME =
            "ThymeleafReactiveModelAdditions:" + CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME;


    static {

        if (!SpringVersionUtils.isSpringWebFluxPresent()) {

            EXECUTION_ATTRIBUTES = null;

        } else {

            // Returns Mono<SecurityContext>, but we will specify Object in order not to bind this class to Mono at compile time
            final Function<ServerWebExchange, Object> secCtxInitializer =
                        (exchange) -> ReactiveSecurityContextHolder.getContext();

            // Returns Mono<SecurityContext>, but we will specify Object in order not to bind this class to Mono at compile time
            final Function<ServerWebExchange, Object> csrfTokenInitializer =
                    (exchange) -> {
                        final Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                        if (csrfToken == null) {
                            return Mono.empty();
                        }
                        // We need to put it into the exchange attributes manually here because async resolution
                        // will only set the result into the MODEL, but RequestDataValueProcessor is expecing this
                        // specifically as an exchange attribute instead of looking into the model or context.
                        return csrfToken.doOnSuccess(
                                token -> exchange.getAttributes().put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
                    };


            EXECUTION_ATTRIBUTES = new HashMap<>(3, 1.0f);
            EXECUTION_ATTRIBUTES.put(SECURITY_CONTEXT_EXECUTION_ATTRIBUTE_NAME, secCtxInitializer);
            EXECUTION_ATTRIBUTES.put(CSRF_EXECUTION_ATTRIBUTE_NAME, csrfTokenInitializer);

        }

    }



    public SpringSecurityDialect() {
        super(NAME);
    }

    
    
    public String getPrefix() {
        return DEFAULT_PREFIX;
    }




    public int getDialectProcessorPrecedence() {
        return PROCESSOR_PRECEDENCE;
    }




    public Set<IProcessor> getProcessors(final String dialectPrefix) {

        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();

        final TemplateMode[] templateModes =
                new TemplateMode[] {
                        TemplateMode.HTML, TemplateMode.XML,
                        TemplateMode.TEXT, TemplateMode.JAVASCRIPT, TemplateMode.CSS };

        for (final TemplateMode templateMode : templateModes) {

            processors.add(new AuthenticationAttrProcessor(templateMode, dialectPrefix));
            // synonym (sec:authorize = sec:authorize-expr) for similarity with
            // "authorize-url" and "autorize-acl"
            processors.add(new AuthorizeAttrProcessor(templateMode, dialectPrefix, AuthorizeAttrProcessor.ATTR_NAME));
            processors.add(new AuthorizeAttrProcessor(templateMode, dialectPrefix, AuthorizeAttrProcessor.ATTR_NAME_EXPR));
            processors.add(new AuthorizeUrlAttrProcessor(templateMode, dialectPrefix));
            processors.add(new AuthorizeAclAttrProcessor(templateMode, dialectPrefix));
            processors.add(new StandardXmlNsTagProcessor(templateMode, dialectPrefix));

        }

        return processors;

    }





    public IExpressionObjectFactory getExpressionObjectFactory() {
        return EXPRESSION_OBJECT_FACTORY;
    }




    @Override
    public Map<String, Object> getExecutionAttributes() {
        return EXECUTION_ATTRIBUTES;
    }



}

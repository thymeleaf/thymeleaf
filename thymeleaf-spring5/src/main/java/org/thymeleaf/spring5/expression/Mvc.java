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
package org.thymeleaf.spring5.expression;

import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;

/**
 * <p>
 *   Expression object in charge of the creation of URLs using the controller-based mechanism in Spring MVC 4.1.
 * </p>
 * <p>
 *   This mimics the {@code s:mvcUrl} behaviour explained at
 *   http://docs.spring.io/spring/docs/4.1.0.RELEASE/spring-framework-reference/html/mvc.html#mvc-links-to-controllers-from-views
 *   using the same method (function) names in the Spring JSP tag library.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.3
 *
 */
public class Mvc {

    private static final MvcUriComponentsBuilderDelegate mvcUriComponentsBuilderDelegate;
    private static final String SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME = Mvc.class.getName() + "$Spring41MvcUriComponentsBuilderDelegate";
    private static final String NON_SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME = Mvc.class.getName() + "$NonSpring41MvcUriComponentsBuilderDelegate";


    static {

        final String delegateClassName =
                (SpringVersionUtils.isSpring41AtLeast()?
                        SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME :
                        NON_SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME);

        try {
            final Class<?> implClass = ClassLoaderUtils.loadClass(delegateClassName);
            mvcUriComponentsBuilderDelegate = (MvcUriComponentsBuilderDelegate) implClass.newInstance();
        } catch (final Exception e) {
            throw new ExceptionInInitializerError(
                    new ConfigurationException(
                        "Thymeleaf could not initialize a delegate of class \"" + delegateClassName + "\" for taking " +
                        "care of the " + SpringStandardExpressionObjectFactory.MVC_EXPRESSION_OBJECT_NAME + " expression utility object", e));
        }

    }


    public MethodArgumentBuilderWrapper url(final String mappingName) {
        return mvcUriComponentsBuilderDelegate.fromMappingName(mappingName);
    }



    static interface MvcUriComponentsBuilderDelegate {

        public MethodArgumentBuilderWrapper fromMappingName(String mappingName);

    }


    static class Spring41MvcUriComponentsBuilderDelegate implements MvcUriComponentsBuilderDelegate {

        Spring41MvcUriComponentsBuilderDelegate() {
            super();
        }

        public MethodArgumentBuilderWrapper fromMappingName(final String mappingName) {
            return new Spring41MethodArgumentBuilderWrapper(org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMappingName(mappingName));
        }

    }


    static class NonSpring41MvcUriComponentsBuilderDelegate implements MvcUriComponentsBuilderDelegate {

        NonSpring41MvcUriComponentsBuilderDelegate() {
            super();
        }

        public MethodArgumentBuilderWrapper fromMappingName(final String mappingName) {
            throw new UnsupportedOperationException(
                    "MVC URI component building is only supported in Spring versions 4.1 or newer");
        }

    }



    public static interface MethodArgumentBuilderWrapper {

        public MethodArgumentBuilderWrapper arg(int index, Object value);
        public String build();
        public String buildAndExpand(Object... uriVariables);

    }


    static class Spring41MethodArgumentBuilderWrapper implements MethodArgumentBuilderWrapper {

        private final org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.MethodArgumentBuilder builder;


        private Spring41MethodArgumentBuilderWrapper(
                final org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.MethodArgumentBuilder builder) {
            super();
            this.builder = builder;
        }

        public MethodArgumentBuilderWrapper arg(final int index, final Object value) {
            return new Spring41MethodArgumentBuilderWrapper(this.builder.arg(index, value));
        }

        public String build() {
            return this.builder.build();
        }

        public String buildAndExpand(final Object... uriVariables) {
            return this.builder.buildAndExpand(uriVariables);
        }

    }


}

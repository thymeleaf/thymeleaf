/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2023, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.thymeleaf.TemplateEngine;

public class StandardExpressionClassAccessEvaluatorTest {
    
    
    public StandardExpressionClassAccessEvaluatorTest() {
        super();
    }

    @Test
    public void typeBlockedForAllPurposesTest() {
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("org.thymeleaf.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("org.springframework.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("org.springframework.cglib.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("org.springframework.aot.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("org.springframework.javapoet.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("net.bytebuddy.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("es.whatever.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("de.whatever.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("net.whatever.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("org.whatever.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("java.lang.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("java.lang.Runtime"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("java.time.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("javax.servlet.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("jakarta.servlet.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("com.whatever.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("com.sun.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForAllPurposes("jdk.X"));
    }

    @Test
    public void typeBlockedForTypeReferenceTest() {
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("org.thymeleaf.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("org.springframework.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("org.springframework.cglib.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("org.springframework.aot.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("org.springframework.javapoet.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("net.bytebuddy.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("es.whatever.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("de.whatever.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("net.whatever.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("org.whatever.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("java.lang.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("java.lang.Runtime"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("java.time.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("javax.servlet.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("jakarta.servlet.X"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("com.whatever.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("com.sun.X"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isTypeBlockedForTypeReference("jdk.X"));
    }

    @Test
    public void typeAllowedTest() {
        StandardExpressionClassAccessEvaluator expressionClassAccessEvaluator = new StandardExpressionClassAccessEvaluator();
        
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("org.thymeleaf.X"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("org.springframework.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("org.springframework.cglib.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("org.springframework.aot.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("org.springframework.javapoet.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("net.bytebuddy.X"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("es.whatever.X"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("de.whatever.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("java.lang.X"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("java.time.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("javax.servlet.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("jakarta.servlet.X"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("com.whatever.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("com.sun.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("jdk.X"));
        Assertions.assertFalse(expressionClassAccessEvaluator.isTypeAllowed("java.lang.Runtime"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("java.lang.Integer"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("java.util.Collection"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("java.util.stream.Stream"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("java.util.Calendar"));
        Assertions.assertTrue(expressionClassAccessEvaluator.isTypeAllowed("java.util.Map"));
    }

    @Test
    public void memberAllowedForTypeTest() {
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(TemplateEngine.class, "someMethod"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(createTestProxy().getClass(), "someMethod"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Integer.class, "someMethod"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Temporal.class, "someMethod"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(javax.servlet.ServletContext.class, "someMethod"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(jakarta.servlet.ServletContext.class, "someMethod"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Runtime.class, "someMethod"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Collection.class, "iterator"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Stream.class, "collect"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Calendar.class, "getInstance"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Map.class, "get"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Collection.class, "toString"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Stream.class, "toString"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Calendar.class, "toString"));
        Assertions.assertFalse(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(Map.class, "toString"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(ArrayList.class, "toString"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(GregorianCalendar.class, "toString"));
        Assertions.assertTrue(StandardExpressionClassAccessEvaluator.isMemberAllowedForInstanceOfType(LinkedHashMap.class, "toString"));
    }
    
    static TestProxied createTestProxy() {
        return (TestProxied) Proxy.newProxyInstance(
                ClassLoaderUtils.getDefaultClassLoader(),
                new Class[] { TestProxied.class },
                new ListInvocationHandler());
    }


    static class ListInvocationHandler implements InvocationHandler {

        private final TestProxied proxied = new TestProxiedImpl(10);

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return method.invoke(this.proxied, args);
        }

    }


    interface TestProxied {
        int getValue();
    }

    static class TestProxiedImpl implements TestProxied {

        private final int value;

        public TestProxiedImpl(final int value) {
            super();
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }

}

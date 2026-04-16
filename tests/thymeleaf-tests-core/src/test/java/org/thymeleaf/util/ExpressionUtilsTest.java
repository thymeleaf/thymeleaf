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
package org.thymeleaf.util;

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
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.thymeleaf.TemplateEngine;

import static org.thymeleaf.util.ExpressionUtils.*;


public final class ExpressionUtilsTest {



    @Test
    public void typeBlockedForAllPurposesTest() {
        Assertions.assertFalse(isTypeBlockedForAllPurposes("org.thymeleaf.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("org.springframework.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("org.springframework.cglib.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("org.springframework.aot.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("org.springframework.javapoet.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("net.bytebuddy.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("es.whatever.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("de.whatever.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("net.whatever.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("org.whatever.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("java.lang.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("java.lang.Runtime"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("java.time.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("javax.servlet.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("jakarta.servlet.X"));
        Assertions.assertFalse(isTypeBlockedForAllPurposes("com.whatever.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("com.sun.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("sun.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("jdk.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("org.ietf.jgss.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("org.omg.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("org.w3c.dom.X"));
        Assertions.assertTrue(isTypeBlockedForAllPurposes("org.xml.sax.X"));
    }

    @Test
    public void typeBlockedForTypeReferenceTest() {
        Assertions.assertFalse(isTypeBlockedForTypeReference("org.thymeleaf.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.springframework.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.springframework.cglib.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.springframework.aot.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.springframework.javapoet.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("net.bytebuddy.X"));
        Assertions.assertFalse(isTypeBlockedForTypeReference("es.whatever.X"));
        Assertions.assertFalse(isTypeBlockedForTypeReference("de.whatever.X"));
        Assertions.assertFalse(isTypeBlockedForTypeReference("net.whatever.X"));
        Assertions.assertFalse(isTypeBlockedForTypeReference("org.whatever.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("java.lang.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("java.lang.Runtime"));
        Assertions.assertFalse(isTypeBlockedForTypeReference("java.time.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("javax.servlet.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("jakarta.servlet.X"));
        Assertions.assertFalse(isTypeBlockedForTypeReference("com.whatever.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("com.sun.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("sun.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("jdk.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.ietf.jgss.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.omg.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.w3c.dom.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.xml.sax.X"));

        Assertions.assertTrue(isTypeBlockedForTypeReference("com.squareup.javapoet.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("com.zaxxer.hikari.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("com.fasterxml.jackson.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("tools.jackson.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("groovy.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("io.netty.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("javassist.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("javax0.geci.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("kotlin.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("net.sf.cglib.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.apache.tomcat.jdbc.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.apache.commons.dbcp2.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.apache.commons.lang.reflect.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.apache.commons.lang3.reflect.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.apache.bcel.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.apache.logging.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.aspectj.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.codehaus.groovy.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.eclipse.jetty.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.glassfish.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.javassist.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.jboss.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.jetbrains.kotlin.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.jruby.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.junit.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.mockito.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.mortbay.jetty.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.objectweb.asm.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.objenesis.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("org.python.X"));
        Assertions.assertTrue(isTypeBlockedForTypeReference("scala.X"));
    }

    @Test
    public void typeAllowedTest() {
        Assertions.assertTrue(!isTypeForbidden("org.thymeleaf.X"));
        Assertions.assertFalse(!isTypeForbidden("org.springframework.X"));
        Assertions.assertFalse(!isTypeForbidden("org.springframework.cglib.X"));
        Assertions.assertFalse(!isTypeForbidden("org.springframework.aot.X"));
        Assertions.assertFalse(!isTypeForbidden("org.springframework.javapoet.X"));
        Assertions.assertFalse(!isTypeForbidden("net.bytebuddy.X"));
        Assertions.assertTrue(!isTypeForbidden("es.whatever.X"));
        Assertions.assertTrue(!isTypeForbidden("de.whatever.X"));
        Assertions.assertFalse(!isTypeForbidden("java.lang.X"));
        Assertions.assertTrue(!isTypeForbidden("java.time.X"));
        Assertions.assertFalse(!isTypeForbidden("javax.servlet.X"));
        Assertions.assertFalse(!isTypeForbidden("jakarta.servlet.X"));
        Assertions.assertTrue(!isTypeForbidden("com.whatever.X"));
        Assertions.assertFalse(!isTypeForbidden("com.sun.X"));
        Assertions.assertFalse(!isTypeForbidden("jdk.X"));
        Assertions.assertFalse(!isTypeForbidden("java.lang.Runtime"));
        Assertions.assertTrue(!isTypeForbidden("java.lang.Integer"));
        Assertions.assertTrue(!isTypeForbidden("java.util.Collection"));
        Assertions.assertTrue(!isTypeForbidden("java.util.stream.Stream"));
        Assertions.assertTrue(!isTypeForbidden("java.util.Calendar"));
        Assertions.assertTrue(!isTypeForbidden("java.util.Map"));
        Assertions.assertTrue(!isTypeForbidden("java.util.concurrent.atomic.AtomicInteger"));
        Assertions.assertTrue(!isTypeForbidden("java.math.BigDecimal"));
        Assertions.assertTrue(!isTypeForbidden("java.sql.Timestamp"));
        Assertions.assertTrue(!isTypeForbidden("java.util.Optional"));
    }

    @Test
    public void memberAllowedForTypeTest() {
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(TemplateEngine.class, "someMethod"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(createTestProxy().getClass(), "someMethod"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Integer.class, "someMethod"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Temporal.class, "someMethod"));
        Assertions.assertFalse(!isMemberForbiddenForInstanceOfType(javax.servlet.ServletContext.class, "someMethod"));
        Assertions.assertFalse(!isMemberForbiddenForInstanceOfType(jakarta.servlet.ServletContext.class, "someMethod"));
        Assertions.assertFalse(!isMemberForbiddenForInstanceOfType(Runtime.class, "someMethod"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Collection.class, "iterator"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Stream.class, "collect"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Calendar.class, "getInstance"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Map.class, "get"));
        Assertions.assertFalse(!isMemberForbiddenForInstanceOfType(Collection.class, "toString"));
        Assertions.assertFalse(!isMemberForbiddenForInstanceOfType(Stream.class, "toString"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(Calendar.class, "toString"));
        Assertions.assertFalse(!isMemberForbiddenForInstanceOfType(Map.class, "toString"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(ArrayList.class, "toString"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(GregorianCalendar.class, "toString"));
        Assertions.assertTrue(!isMemberForbiddenForInstanceOfType(LinkedHashMap.class, "toString"));
        Assertions.assertTrue(isMemberForbiddenForInstanceOfType(ClassLoader.class, "loadClass"));
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

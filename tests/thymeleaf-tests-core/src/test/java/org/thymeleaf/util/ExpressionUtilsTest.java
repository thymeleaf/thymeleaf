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
        Assertions.assertFalse(isPackageBlockedForAllPurposes("org.thymeleaf.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("org.springframework.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("org.springframework.cglib.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("org.springframework.aot.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("org.springframework.javapoet.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("net.bytebuddy.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("es.whatever.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("de.whatever.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("net.whatever.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("org.whatever.X"));
        Assertions.assertTrue(isPackageBlockedForAllPurposes("java.lang.X"));
        Assertions.assertTrue(isPackageBlockedForAllPurposes("java.lang.Runtime"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("java.time.X"));
        Assertions.assertTrue(isPackageBlockedForAllPurposes("javax.servlet.X"));
        Assertions.assertTrue(isPackageBlockedForAllPurposes("jakarta.servlet.X"));
        Assertions.assertFalse(isPackageBlockedForAllPurposes("com.whatever.X"));
        Assertions.assertTrue(isPackageBlockedForAllPurposes("com.sun.X"));
        Assertions.assertTrue(isPackageBlockedForAllPurposes("jdk.X"));
    }

    @Test
    public void typeBlockedForTypeReferenceTest() {
        Assertions.assertFalse(isPackageBlockedForTypeReference("org.thymeleaf.X"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("org.springframework.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("org.springframework.cglib.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("org.springframework.aot.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("org.springframework.javapoet.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("net.bytebuddy.X"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("es.whatever.X"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("de.whatever.X"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("net.whatever.X"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("org.whatever.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("java.lang.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("java.lang.Runtime"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("java.time.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("javax.servlet.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("jakarta.servlet.X"));
        Assertions.assertFalse(isPackageBlockedForTypeReference("com.whatever.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("com.sun.X"));
        Assertions.assertTrue(isPackageBlockedForTypeReference("jdk.X"));
    }

    @Test
    public void typeAllowedTest() {
        Assertions.assertTrue(isTypeAllowed("org.thymeleaf.X"));
        Assertions.assertTrue(isTypeAllowed("org.springframework.X"));
        Assertions.assertFalse(isTypeAllowed("org.springframework.cglib.X"));
        Assertions.assertFalse(isTypeAllowed("org.springframework.aot.X"));
        Assertions.assertFalse(isTypeAllowed("org.springframework.javapoet.X"));
        Assertions.assertFalse(isTypeAllowed("net.bytebuddy.X"));
        Assertions.assertTrue(isTypeAllowed("es.whatever.X"));
        Assertions.assertTrue(isTypeAllowed("de.whatever.X"));
        Assertions.assertFalse(isTypeAllowed("java.lang.X"));
        Assertions.assertTrue(isTypeAllowed("java.time.X"));
        Assertions.assertFalse(isTypeAllowed("javax.servlet.X"));
        Assertions.assertFalse(isTypeAllowed("jakarta.servlet.X"));
        Assertions.assertTrue(isTypeAllowed("com.whatever.X"));
        Assertions.assertFalse(isTypeAllowed("com.sun.X"));
        Assertions.assertFalse(isTypeAllowed("jdk.X"));
        Assertions.assertFalse(isTypeAllowed("java.lang.Runtime"));
        Assertions.assertTrue(isTypeAllowed("java.lang.Integer"));
        Assertions.assertTrue(isTypeAllowed("java.util.Collection"));
        Assertions.assertTrue(isTypeAllowed("java.util.stream.Stream"));
        Assertions.assertTrue(isTypeAllowed("java.util.Calendar"));
        Assertions.assertTrue(isTypeAllowed("java.util.Map"));
    }

    @Test
    public void memberAllowedForTypeTest() {
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(TemplateEngine.class, "someMethod"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(createTestProxy().getClass(), "someMethod"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Integer.class, "someMethod"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Temporal.class, "someMethod"));
        Assertions.assertFalse(isMemberAllowedForInstanceOfType(javax.servlet.ServletContext.class, "someMethod"));
        Assertions.assertFalse(isMemberAllowedForInstanceOfType(jakarta.servlet.ServletContext.class, "someMethod"));
        Assertions.assertFalse(isMemberAllowedForInstanceOfType(Runtime.class, "someMethod"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Collection.class, "iterator"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Stream.class, "collect"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Calendar.class, "getInstance"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Map.class, "get"));
        Assertions.assertFalse(isMemberAllowedForInstanceOfType(Collection.class, "toString"));
        Assertions.assertFalse(isMemberAllowedForInstanceOfType(Stream.class, "toString"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(Calendar.class, "toString"));
        Assertions.assertFalse(isMemberAllowedForInstanceOfType(Map.class, "toString"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(ArrayList.class, "toString"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(GregorianCalendar.class, "toString"));
        Assertions.assertTrue(isMemberAllowedForInstanceOfType(LinkedHashMap.class, "toString"));
    }


    @Test
    public void testNormalizeExpression() {
        Assertions.assertNull(normalizeExpression(null));
        Assertions.assertEquals("", normalizeExpression(""));
        final String exp00 = "${something}";
        Assertions.assertSame(exp00, normalizeExpression(exp00));
        Assertions.assertEquals("${some thing}", normalizeExpression("${some thing}"));
        Assertions.assertEquals("${some \nthing}", normalizeExpression("${some \nthing}"));
        Assertions.assertEquals("${some thing}", normalizeExpression("${some \0thing}"));
        Assertions.assertEquals("${some thing}", normalizeExpression("${some \tthing}"));
        Assertions.assertEquals("${some thing}", normalizeExpression("${some t\t\thing}"));
        Assertions.assertEquals("${some thing}", normalizeExpression("\t${some t\t\thing}"));
        Assertions.assertEquals("${some thing}", normalizeExpression("\t${some thing}"));
        Assertions.assertEquals("${some thing}", normalizeExpression("\t${some t\t\thing}\t"));
        Assertions.assertEquals("${some thing}", normalizeExpression("\t${some thing}\t"));
        Assertions.assertEquals("${some thing}", normalizeExpression("${some t\t\thing}\t"));
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

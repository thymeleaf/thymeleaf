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
package org.thymeleaf.templateengine.features;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.platform.commons.util.ClassLoaderUtils;

public class TestProxyFactory {

    private TestProxyFactory() {
        super();
    }


    public static TestProxied createTestProxy() {
        return (TestProxied) Proxy.newProxyInstance(
                    ClassLoaderUtils.getDefaultClassLoader(),
                    new Class[] { TestProxied.class },
                    new ListInvocationHandler());
    }


    public static class ListInvocationHandler implements InvocationHandler {

        private final TestProxied proxied = new TestProxiedImpl(10);

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            return method.invoke(this.proxied, args);
        }

    }


    public interface TestProxied {
        int getValue();
    }

    public static class TestProxiedImpl implements TestProxied {

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

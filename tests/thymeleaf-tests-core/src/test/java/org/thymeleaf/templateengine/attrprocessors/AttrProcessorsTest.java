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
package org.thymeleaf.templateengine.attrprocessors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;
import org.thymeleaf.util.ThrottleArgumentsProvider;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class AttrProcessorsTest {




    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testRemove(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/remove");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testIf(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/if");

        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testUnless(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/unless");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInline(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/inline");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInclude(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/include");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testInsert(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/insert");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testReplace(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/replace");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testEach(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/each");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testObject(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/object");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testAttr(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/attr");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testSimpleValue(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/simplevalue");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testDoubleValue(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/doublevalue");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testAppendPrepend(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/appendprepend");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testFixedValue(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/fixedvalue");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testSwitch(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/switch");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testWith(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/with");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testDOMEvent(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/domevent");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testAssert(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/assert");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottleArgumentsProvider.class)
    public void testDefault(final int throttleStep) throws Exception {

        final TestExecutor executor = TestExecutorFactory.createJakartaWebTestExecutor();
        executor.setThrottleStep(throttleStep);
        executor.execute("classpath:templateengine/attrprocessors/default");

        Assertions.assertTrue(executor.isAllOK());

    }


    
}

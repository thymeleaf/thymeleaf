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
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.util.ThrottledWebTestExecutorArgumentsProvider;


public class AttrProcessorsTest {




    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testRemove(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/remove");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testIf(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/if");

        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testUnless(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/unless");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testInline(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/inline");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testInclude(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/include");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testInsert(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/insert");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testReplace(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/replace");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testEach(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/each");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testObject(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/object");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testAttr(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/attr");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testSimpleValue(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/simplevalue");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDoubleValue(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/doublevalue");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testAppendPrepend(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/appendprepend");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testFixedValue(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/fixedvalue");
        
        Assertions.assertTrue(executor.isAllOK());
        
    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testSwitch(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/switch");

        Assertions.assertTrue(executor.isAllOK());

    }


    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testWith(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/with");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDOMEvent(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/domevent");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testAssert(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/assert");

        Assertions.assertTrue(executor.isAllOK());

    }

    @ParameterizedTest
    @ArgumentsSource(ThrottledWebTestExecutorArgumentsProvider.class)
    public void testDefault(final TestExecutor executor) throws Exception {

        executor.execute("classpath:templateengine/attrprocessors/default");

        Assertions.assertTrue(executor.isAllOK());

    }


    
}

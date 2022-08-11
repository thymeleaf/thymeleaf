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
package org.thymeleaf.templateengine.dataprefix.attrprocessors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class DataPrefixAttrProcessorsTest {


    public DataPrefixAttrProcessorsTest() {
        super();
    }






    @Test
    public void testRemove() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/remove");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testIf() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/if");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testUnless() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/unless");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testInline() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/inline");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testInclude() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/include");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testSubstituteby() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/substituteby");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testReplace() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/replace");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testEach() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/each");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testObject() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/object");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testAttr() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/attr");

        Assertions.assertTrue(executor.isAllOK());

    }

    @Test
    public void testSimpleValue() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/simplevalue");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testDoubleValue() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/doublevalue");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testAppendPrepend() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/appendprepend");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testFixedValue() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/fixedvalue");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testSwitch() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/switch");

        Assertions.assertTrue(executor.isAllOK());

    }


    @Test
    public void testWith() throws Exception {

        final TestExecutor executor = TestExecutorFactory.createTestExecutor();
        executor.execute("classpath:templateengine/dataprefix/attrprocessors/with");

        Assertions.assertTrue(executor.isAllOK());

    }


}

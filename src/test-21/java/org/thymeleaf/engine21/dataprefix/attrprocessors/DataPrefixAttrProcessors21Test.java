/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine21.dataprefix.attrprocessors;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class DataPrefixAttrProcessors21Test {


    public DataPrefixAttrProcessors21Test() {
        super();
    }






    @Test
    public void testRemove() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/remove");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testIf() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/if");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testUnless() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/unless");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testInline() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/inline");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testInclude() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/include");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testSubstituteby() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/substituteby");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testReplace() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/replace");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testEach() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/each");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testObject() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/object");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testAttr() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/attr");

        Assert.assertTrue(executor.isAllOK());

    }

    @Test
    public void testSimpleValue() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/simplevalue");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testDoubleValue() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/doublevalue");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testAppendPrepend() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/appendprepend");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testFixedValue() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/fixedvalue");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testSwitch() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/switch");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testWith() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/dataprefix/attrprocessors/with");

        Assert.assertTrue(executor.isAllOK());

    }


}

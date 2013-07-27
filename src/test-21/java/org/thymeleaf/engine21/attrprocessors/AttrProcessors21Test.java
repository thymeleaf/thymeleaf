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
package org.thymeleaf.engine21.attrprocessors;

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;


public class AttrProcessors21Test {


    public AttrProcessors21Test() {
        super();
    }






    @Test
    public void testAssert() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/attrprocessors/assert");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testWith() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/attrprocessors/with");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testInclude() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/attrprocessors/include");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testSubstituteby() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/attrprocessors/substituteby");

        Assert.assertTrue(executor.isAllOK());

    }


    @Test
    public void testReplace() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.execute("classpath:engine21/attrprocessors/replace");

        Assert.assertTrue(executor.isAllOK());

    }


}

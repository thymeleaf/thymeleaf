/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.engine;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.junit.Test;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpecs;
import org.thymeleaf.testing.templateengine.standard.config.test.IStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestDocumentData;
import org.thymeleaf.testing.templateengine.standard.util.StandardTestDocumentResolutionUtils;
import org.thymeleaf.testing.templateengine.standard.util.StandardTestIOUtils;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;





public class TestExecutorTest {
    
    
    
    
    public TestExecutorTest() {
        super();
    }
    
    
    
    
    
    
    
    @Test
    public void testExecutor() throws Exception {
        
        try {
            
            
            final String text =
                    "%NAME_RESOLVER org.thymeleaf.testing.templateengine.engine.TestExecutorTest$OneNameStandardDirectiveResolver\n" +
                    "%NAME this is a sample test!\n" +
                    "%INPUT \n<!DOCTYPE html>\n<html>\n  <body>\n    <h1>Hello!</h1>\n  </body>\n</html>";
            
            final Reader reader = new StringReader(text);
            
            final StandardTestDocumentData data = 
                    StandardTestIOUtils.readTestDocument("001", "testf", reader);
            
            System.out.println(data.getAllDirectiveValues());
            
            final Map<String,Object> values =
                    StandardTestDocumentResolutionUtils.resolveTestDocumentData("001", data, StandardTestDirectiveSpecs.STANDARD_DIRECTIVES_SET_SPEC);
            
            System.out.println(values);
            System.out.println(((ITestResource)values.get(StandardTestDirectiveSpecs.INPUT_DIRECTIVE_SPEC.getName())).read());
            
            
//            final ITestResource res0 = new StringTestResource("hello!");
//            final ITestResource res1 = new StringTestResource("goodbye!");
//            final ITestResource res2 = new StringTestResource("<span th:text=\"${'hey!'}\">cucu</span>");
//            final ITestResource res3 = new StringTestResource("<span>hey!</span>");
//            
//            final ITest test0 = new SuccessExpectedTest(res1, true, res0);
//            final ITest test1 = new SuccessExpectedTest(res2, false, res0);
//            final ITest test2 = new SuccessExpectedTest(res2, false, res3);
//            
//            final ITestIterator iter2 = new TestIterator(test2, 2);
//            
////            final ITestSuite testSuite = new TestSuite("testing01", test0, test2, test1, test2, iter2);
//            final ITestSuite testSuite = new TestSuite(iter2);
//
//            
//            TestExecutor.execute(testSuite);
            
            
            
        } catch (final Throwable t) {
            t.printStackTrace();
        }
        
        
    }
    
    
    
    public static class OneNameStandardDirectiveResolver implements IStandardDirectiveResolver<String> {

        public Class<String> getValueClass() {
            return String.class;
        }

        public String getValue(String executionId,
                StandardTestDocumentData data, String directiveName) {
            return "LALEIRO";
        }
        
    }
    
    
}

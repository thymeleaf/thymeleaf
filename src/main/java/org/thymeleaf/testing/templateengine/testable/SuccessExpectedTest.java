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
package org.thymeleaf.testing.templateengine.testable;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.util.ResultCompareUtils;
import org.thymeleaf.util.Validate;





public class SuccessExpectedTest 
        extends AbstractTest {

    
    private ITestResource output;
    
    
    
    public SuccessExpectedTest() {
        super();
    }


    
    
    public ITestResource getOutput() {
        return this.output;
    }

    public void setOutput(final ITestResource output) {
        this.output = output;
    }


    

    public ITestResult evalResult(final String executionId, final String testName, final String result) {
        
        if (result == null) {
            TestResult.error(testName, "Result is null");
        }
        
        if (this.output == null) {
            throw new TestEngineExecutionException(
                    executionId, 
                    "Test \"" + testName + "\" does not specify an output, but success-expected " +
            		"tests should always specify one");
        }
        
        final String outputStr = this.output.read();
        
        if (outputStr == null) {
            throw new TestEngineExecutionException(
                    executionId, "Cannot execute: Test with name \"" + testName + "\" resolved its output resource as null");
        }
        
        if (outputStr.equals(result)) {
            return TestResult.ok(testName);
        }
        
        return TestResult.error(testName, ResultCompareUtils.explainComparison(outputStr, result));
        
    }


    public ITestResult evalResult(final String executionId, final String testName, final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return TestResult.error(testName, t);
    }
    
    
}

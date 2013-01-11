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
package org.thymeleaf.testing.templateengine.test;

import org.thymeleaf.testing.templateengine.util.ResultCompareUtils;
import org.thymeleaf.util.Validate;





public class SuccessExpectedTest 
        extends AbstractTest {

    
    private final String output;
    
    
    
    public SuccessExpectedTest(final String input, final String output) {
        super(input);
        Validate.notNull(output, "Output cannot be null");
        this.output = output;
    }


    
    
    public String getOutput() {
        return this.output;
    }




    public ITestResult evalResult(final String result) {
        
        if (result == null) {
            TestResult.error(getInput(), result, "Result is null");
        }
        
        if (this.output.equals(result)) {
            return TestResult.ok(getInput(), result);
        }
     
        return TestResult.error(getInput(), result, ResultCompareUtils.explainComparison(this.output, result));
        
    }


    public ITestResult evalResult(final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return TestResult.error(getInput(), t);
    }
    
    
}

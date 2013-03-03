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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;





public class FailExpectedTest 
        extends AbstractTest {


    private Class<? extends Throwable> outputThrowableClass;
    private String outputThrowableMessagePattern;
    private Pattern outputThrowableMessagePatternObject;
    
    
    
    
    public FailExpectedTest() {
        super();
    }

    

    
    public Class<? extends Throwable> getOutputThrowableClass() {
        return this.outputThrowableClass;
    }
    
    public void setOutputThrowableClass(final Class<? extends Throwable> outputThrowableClass) {
        this.outputThrowableClass = outputThrowableClass;
    }
    

    
    public String getOutputThrowableMessagePattern() {
        return this.outputThrowableMessagePattern;
    }
    
    public void setOutputThrowableMessagePattern(final String outputThrowableMessagePattern) {
        this.outputThrowableMessagePattern = outputThrowableMessagePattern;
        if (this.outputThrowableMessagePattern != null) {
            this.outputThrowableMessagePatternObject = Pattern.compile(this.outputThrowableMessagePattern);
        } else {
            this.outputThrowableMessagePatternObject = null;
        }
    }




    public ITestResult evalResult(final String executionId, final String testName, final String result) {
        return TestResult.error(testName, "An exception of class " + this.outputThrowableClass.getName() + " was expected");
    }


    public ITestResult evalResult(final String executionId, final String testName, final Throwable t) {
        
        Validate.notNull(t, "Throwable cannot be null");
        
        if (this.outputThrowableClass == null) {
            throw new TestEngineExecutionException(
                    executionId, 
                    "Test \"" + testName + "\" does not specify an output throwable, but fail-expected " +
                    "tests should always specify one");
        }
        
        if (this.outputThrowableClass.isAssignableFrom(t.getClass())) {
            
            if (this.outputThrowableMessagePatternObject != null) {
                
                final String throwableMessage = t.getMessage();
                if (throwableMessage != null) {
                    final Matcher matcher = this.outputThrowableMessagePatternObject.matcher(throwableMessage);
                    if (matcher.matches()) {
                        return TestResult.ok(testName, t);
                    }
                }
                
                return TestResult.error(testName, 
                        "An exception of class " + t.getClass() + " was raised as expected, " +
                		"but its message does not match pattern \"" + throwableMessage + "\"", t);
                
            }
            
            return TestResult.ok(testName, t);
            
        }
        
        return TestResult.error(testName, 
                "An exception of class " + t.getClass() + " was raised, but " + this.outputThrowableClass.getName() + " was expected instead", t);
        
    }
    
    
}

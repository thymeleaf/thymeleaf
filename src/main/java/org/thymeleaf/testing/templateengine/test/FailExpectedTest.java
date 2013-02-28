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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;





public class FailExpectedTest 
        extends AbstractTest {


    private final Class<? extends Throwable> outputThrowableClass;
    private final String outputThrowableMessagePattern;
    private final Pattern outputThrowableMessagePatternObject;
    
    
    
    
    public FailExpectedTest(final Map<String,ITestResource> inputs, final boolean inputCacheable, 
            final Class<? extends Throwable> outputThrowableClass, final String outputThrowableMessagePattern) {
        super(inputs, inputCacheable);
        Validate.notNull(outputThrowableClass, "Output throwable class cannot be null");
        this.outputThrowableClass = outputThrowableClass;
        this.outputThrowableMessagePattern = outputThrowableMessagePattern;
        this.outputThrowableMessagePatternObject = Pattern.compile(this.outputThrowableMessagePattern);
    }

    

    
    public Class<? extends Throwable> getOutputThrowableClass() {
        return this.outputThrowableClass;
    }

    public String getOutputThrowableMessagePattern() {
        return this.outputThrowableMessagePattern;
    }




    public ITestResult evalResult(final String testName, final String result) {
        return TestResult.error(testName, "An exception of class " + this.outputThrowableClass.getName() + " was expected");
    }


    public ITestResult evalResult(final String testName, final Throwable t) {
        
        Validate.notNull(t, "Throwable cannot be null");
        
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

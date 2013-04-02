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
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.util.ResultCompareUtils;
import org.thymeleaf.testing.templateengine.util.ResultCompareUtils.ResultComparison;
import org.thymeleaf.testing.templateengine.util.TestNamingUtils;
import org.thymeleaf.util.Validate;





public class Test 
        extends AbstractTest {

    
    private ITestResource output;

    private Class<? extends Throwable> outputThrowableClass;
    private String outputThrowableMessagePattern;

    
    
    
    public Test() {
        super();
    }


    
    
    public ITestResource getOutput() {
        return this.output;
    }

    public void setOutput(final ITestResource output) {
        this.output = output;
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
    }


    
    
    public void initializeFrom(final ITest test) {
        setName(test.getName());
        setContext(test.getContext());
        setFragmentSpec(test.getFragmentSpec());
        setTemplateMode(test.getTemplateMode());
        setInput(test.getInput());
        setAdditionalInputs(test.getAdditionalInputs());
        setInputCacheable(test.isInputCacheable());
        if (test instanceof Test) {
            final Test specificTest = (Test) test;
            setOutput(specificTest.getOutput());
            setOutputThrowableClass(specificTest.getOutputThrowableClass());
            setOutputThrowableMessagePattern(specificTest.getOutputThrowableMessagePattern());
        }
    }
    
    
    
    
    
    public boolean isSuccessExpected() {
        return getOutputThrowableClass() == null;
    }
    
    
    
    
    private void validateTestOutput() {
        
        if (getOutput() != null) {
            if (getOutputThrowableClass() != null || getOutputThrowableMessagePattern() != null) {
                throw new TestEngineExecutionException(
                        "Test \"" + TestNamingUtils.nameTest(this) + "\" specifies both an output " +
                        "(as if success was expected) and an exception or exception pattern (as if fail " +
                        "was expected). Only one is allowed.");
            }
        } else {
            if (getOutputThrowableClass() == null) {
                throw new TestEngineExecutionException(
                        "Test \"" + TestNamingUtils.nameTest(this) + "\" specifies neither an output " +
                        "(as if success was expected) nor an exception or exception pattern (as if fail " +
                        "was expected).");
            }
        }
        
    }
    
    
    

    public final ITestResult evalResult(final String executionId, final String testName, final String result) {
        validateTestOutput();
        if (!isSuccessExpected()) {
            return evalResultFailExpected(executionId, testName, result);
        }
        return evalResultSuccessExpected(executionId, testName, result);
    }

    
    public final ITestResult evalResult(final String executionId, final String testName, final Throwable t) {
        validateTestOutput();
        if (!isSuccessExpected()) {
            return evalResultFailExpected(executionId, testName, t);
        }
        return evalResultSuccessExpected(executionId, testName, t);
    }
    
    
    

    
    protected ITestResult evalResultSuccessExpected(
            @SuppressWarnings("unused") final String executionId, final String testName, final String result) {
        
        if (result == null) {
            TestResult.error(testName, "Result is null");
        }
        
        final ITestResource outputEval = getOutput();
        if (outputEval == null) {
            throw new TestEngineExecutionException(
                    "Test \"" + testName + "\" does not specify an output, but success-expected " +
                    "tests should always specify one");
        }
        
        final String outputStr = outputEval.read();
        
        if (outputStr == null) {
            throw new TestEngineExecutionException(
                    "Cannot execute: Test with name \"" + testName + "\" resolved its output resource as null");
        }

        final ResultComparison comparison = ResultCompareUtils.compareResults(outputStr,result);
        
        if (comparison.getResult()) {
            return TestResult.ok(testName);
        }
        
        return TestResult.error(testName, comparison.getExplanation());
        
    }
 

    
    
    protected ITestResult evalResultFailExpected(
            @SuppressWarnings("unused") final String executionId, final String testName, 
            @SuppressWarnings("unused") final String result) {
        return TestResult.error(testName, "An exception of class " + getOutputThrowableClass().getName() + " was expected");
    }

    
    
    protected ITestResult evalResultSuccessExpected(
            @SuppressWarnings("unused") final String executionId, final String testName, final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return TestResult.error(testName, t);
    }
 

    
    protected ITestResult evalResultFailExpected(
            @SuppressWarnings("unused") final String executionId, final String testName, final Throwable t) {
        
        Validate.notNull(t, "Throwable cannot be null");

        final Class<? extends Throwable> outputThrowableClassEval = getOutputThrowableClass();
        
        if (outputThrowableClassEval == null) {
            throw new TestEngineExecutionException(
                    "Test \"" + testName + "\" does not specify an output throwable, but fail-expected " +
                    "tests should always specify one");
        }
        
        if (outputThrowableClassEval.isAssignableFrom(t.getClass())) {
            
            final String outputThrowableMessagePatternEval = getOutputThrowableMessagePattern();
            
            if (outputThrowableMessagePatternEval != null) {

                // Cannot cache this Pattern object because the value returned by
                // getOutputThrowableMessagePattern() could be overridden by a subclass
                final Pattern outputThrowableMessagePatternObjectEval = 
                        Pattern.compile(outputThrowableMessagePatternEval);
                
                final String throwableMessage = t.getMessage();
                if (throwableMessage != null) {
                    final Matcher matcher = outputThrowableMessagePatternObjectEval.matcher(throwableMessage);
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

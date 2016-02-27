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
package org.thymeleaf.testing.templateengine.testable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceItem;
import org.thymeleaf.testing.templateengine.util.ResultCompareUtils;
import org.thymeleaf.testing.templateengine.util.ResultCompareUtils.ResultComparison;
import org.thymeleaf.util.Validate;





public class Test extends AbstractTest {

    public static boolean DEFAULT_EXACT_MATCH = false;
    
    
    private ITestResource output;

    private Class<? extends Throwable> outputThrowableClass;
    private String outputThrowableMessagePattern;
    
    private boolean exactMatch = DEFAULT_EXACT_MATCH;

    
    
    
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

    
    

    public boolean isExactMatch() {
        return this.exactMatch;
    }

    public void setExactMatch(final boolean exactMatch) {
        this.exactMatch = exactMatch;
    }



    
    
    public boolean isSuccessExpected() {
        return getOutputThrowableClass() == null;
    }
    
    
    
    
    private void validateTestOutput() {
        
        if (getOutput() != null) {
            if (getOutputThrowableClass() != null || getOutputThrowableMessagePattern() != null) {
                throw new TestEngineExecutionException(
                        "Test \"" + TestExecutor.getThreadTestName() + "\" specifies both an output " +
                        "(as if success was expected) and an exception or exception pattern (as if fail " +
                        "was expected). Only one is allowed.");
            }
        } else {
            if (getOutputThrowableClass() == null) {
                throw new TestEngineExecutionException(
                        "Test \"" + TestExecutor.getThreadTestName() + "\" specifies neither an output " +
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
        if (!(outputEval instanceof ITestResourceItem)) {
            throw new TestEngineExecutionException(
                    "Test \"" + testName + "\" specifies an output which is a container, not an item " +
                    "(maybe a folder?)");
        }
        
        final String outputStr = ((ITestResourceItem)outputEval).readAsText();
        
        if (outputStr == null) {
            throw new TestEngineExecutionException(
                    "Cannot execute: Test with name \"" + testName + "\" resolved its output resource as null");
        }

        final TemplateMode templateMode = getTemplateMode();

        final ResultComparison comparison =
                templateMode.isText()? ResultCompareUtils.compareTextResults(outputStr, result) :
                                       ResultCompareUtils.compareMarkupResults(outputStr,result, !this.exactMatch);
        
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
        
        if (throwableClassMatches(outputThrowableClassEval, t)) {
            
            final String outputThrowableMessagePatternEval = getOutputThrowableMessagePattern();
            
            if (outputThrowableMessagePatternEval != null) {

                // Cannot cache this Pattern object because the value returned by
                // getOutputThrowableMessagePattern() could be overridden by a subclass
                final Pattern outputThrowableMessagePatternObjectEval = 
                        Pattern.compile(outputThrowableMessagePatternEval);
                
                if (throwableMessageMatches(outputThrowableMessagePatternObjectEval, t)) {
                    return TestResult.ok(testName, t);
                }
                
                return TestResult.error(testName, 
                        "An exception of class " + t.getClass() + " was raised as expected, " +
                        "but its message does not match pattern \"" + outputThrowableMessagePatternEval + "\"", t);
                
            }
            
            return TestResult.ok(testName, t);
            
        }
        
        return TestResult.error(testName, 
                "An exception of class " + t.getClass() + " was raised, but " + this.outputThrowableClass.getName() + " was expected instead", t);
        
    }

    
    
    private static boolean throwableClassMatches(
            final Class<? extends Throwable> outputThrowableClass, final Throwable throwable) {
        
        if (outputThrowableClass.isAssignableFrom(throwable.getClass())) {
            return true;
        }
        if (throwable.getCause() != null) {
            return throwableClassMatches(outputThrowableClass, throwable.getCause());
        }
        return false;
        
    }
    
    
    private static boolean throwableMessageMatches(
            final Pattern throwableMessagePattern, final Throwable throwable) {

        final String throwableMessage = throwable.getMessage();
        if (throwableMessage != null) {
            final Matcher matcher = throwableMessagePattern.matcher(throwableMessage);
            if (matcher.matches()) {
                return true;
            }
        }
        
        if (throwable.getCause() != null) {
            return throwableMessageMatches(throwableMessagePattern, throwable.getCause());
        }
        
        return false;
        
    }
    
}

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

import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;



public class TestResult implements ITestResult {

    private final String testName;
    private final ITestResource input;
    private final String result;
    private final boolean resultSet;
    private final boolean ok;
    private final String message;
    private final Throwable throwable;
    
    
    public static TestResult ok(final String testName, final ITestResource input, final String result) {
        return new TestResult(testName, input, result, true, true, null, null);
    }
    
    public static TestResult ok(final String testName, final ITestResource input, final String result, final String message) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        return new TestResult(testName, input, result, true, true, message, null);
    }
    
    public static TestResult ok(final String testName, final ITestResource input, final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, input, null, false, true, null, t);
    }
    
    public static TestResult ok(final String testName, final ITestResource input, final String message, final Throwable t) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, input, null, false, true, message, t);
    }
    
    public static TestResult error(final String testName, final ITestResource input, final String result, final String message) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        return new TestResult(testName, input, result, true, false, message, null);
    }
    
    public static TestResult error(final String testName, final ITestResource input, final String message, final Throwable t) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, input, null, false, false, message, t);
    }
    
    public static TestResult error(final String testName, final ITestResource input, final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, input, null, false, false, t.getMessage(), t);
    }
    
    
    
    protected TestResult(
            final String testName, final ITestResource input, final String result, final boolean resultSet, 
            final boolean ok, final String message, final Throwable throwable) {
        super();
        Validate.notNull(testName, "Test name cannot be null. Remember this must be the context-registered name.");
        Validate.notNull(input, "Input cannot be null");
        this.testName = testName;
        this.input = input;
        this.result = result;
        this.resultSet = resultSet;
        this.ok = ok;
        this.message = message;
        this.throwable = throwable;
    }
    
    
    public String getTestName() {
        return this.testName;
    }
    
    public ITestResource getInput() {
        return this.input;
    }
    
    public boolean hasResult() {
        return this.resultSet;
    }
    
    public String getResult() {
        return this.result;
    }
    
    public boolean isOK() {
        return this.ok;
    }

    public boolean hasMessage() {
        return this.message != null;
    }
    
    public String getMessage() {
        return this.message;
    }

    public boolean hasThrowable() {
        return this.throwable != null;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }

    
}

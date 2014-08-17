/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.util.Validate;



public class TestResult implements ITestResult {

    private final String testName;
    private final boolean ok;
    private final String message;
    private final Throwable throwable;
    
    
    public static TestResult ok(final String testName) {
        return new TestResult(testName, true, null, null);
    }
    
    public static TestResult ok(final String testName, final String message) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        return new TestResult(testName, true, message, null);
    }
    
    public static TestResult ok(final String testName, final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, true, null, t);
    }
    
    public static TestResult ok(final String testName, final String message, final Throwable t) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, true, message, t);
    }
    
    public static TestResult error(final String testName, final String message) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        return new TestResult(testName, false, message, null);
    }
    
    public static TestResult error(final String testName, final String message, final Throwable t) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, false, message, t);
    }
    
    public static TestResult error(final String testName, final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(testName, false, t.getMessage(), t);
    }
    
    
    
    protected TestResult(
            final String testName, final boolean ok, final String message, final Throwable throwable) {
        super();
        Validate.notNull(testName, "Test name cannot be null. Remember this must be the context-registered name.");
        this.testName = testName;
        this.ok = ok;
        this.message = message;
        this.throwable = throwable;
    }
    
    
    public String getTestName() {
        return this.testName;
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

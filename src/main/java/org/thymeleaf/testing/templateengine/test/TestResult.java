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

import org.thymeleaf.util.Validate;



public class TestResult implements ITestResult {

    private static final TestResult OK = new TestResult(true, null, null); 
    
    private final boolean ok;
    private final String message;
    private final Throwable throwable;
    
    
    public static TestResult ok() {
        return OK;
    }
    
    public static TestResult ok(final String message) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        return new TestResult(true, message, null);
    }
    
    public static TestResult error(final String message) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        return new TestResult(true, message, null);
    }
    
    public static TestResult error(final String message, final Throwable t) {
        Validate.notEmpty(message, "Message cannot be null or empty");
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(true, message, t);
    }
    
    public static TestResult error(final Throwable t) {
        Validate.notNull(t, "Throwable cannot be null");
        return new TestResult(true, t.getMessage(), t);
    }
    
    
    
    protected TestResult(final boolean ok, final String message, final Throwable throwable) {
        super();
        this.ok = ok;
        this.message = message;
        this.throwable = throwable;
    }
    
    
    public boolean isOK() {
        return this.ok;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    
}

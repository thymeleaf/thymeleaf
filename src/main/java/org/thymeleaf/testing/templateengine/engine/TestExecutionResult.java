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
package org.thymeleaf.testing.templateengine.engine;






final class TestExecutionResult {
    

    private int totalTestsExecuted = 0;
    private int totalTestsOk = 0;
    private long totalTimeNanos = 0L;

    

    TestExecutionResult() {
        super();
    }
    
    
    
    
    synchronized void addTestResult(final boolean ok, final long timeNanos) {
        this.totalTestsExecuted++;
        if (ok) {
            this.totalTestsOk++;
        }
        this.totalTimeNanos += timeNanos;
    }
    
    
    synchronized void addResult(final TestExecutionResult result) {
        this.totalTestsExecuted += result.getTotalTestsExecuted();
        this.totalTestsOk += result.getTotalTestsOk();
        this.totalTimeNanos += result.getTotalTimeNanos();
    }
    
    
    public int getTotalTestsOk() {
        return this.totalTestsOk;
    }
    
    public int getTotalTestsExecuted() {
        return this.totalTestsExecuted;
    }
    
    public long getTotalTimeNanos() {
        return this.totalTimeNanos;
    }
    
    
    
}

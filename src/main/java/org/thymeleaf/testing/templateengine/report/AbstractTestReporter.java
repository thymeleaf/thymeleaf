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
package org.thymeleaf.testing.templateengine.report;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestResult;




public abstract class AbstractTestReporter implements ITestReporter {
    
    
    private final Map<String,ITestResult> resultByTestName = new LinkedHashMap<String,ITestResult>();
    private final Map<String,Long> executionTimeMsByTestName = new LinkedHashMap<String,Long>();
    private boolean allOK = true;
    private long totalExecutionTimeMs = 0L;
    
    
    protected AbstractTestReporter() {
        super();
    }

    
    
    
    public final void testStart(final String executionId, final int nestingLevel, final ITest test, final String testName) {
        reportTestStart(executionId, nestingLevel, test, testName);
    }
    
    
    protected abstract void reportTestStart(final String executionId, final int nestingLevel, final ITest test, final String testName);
    

    
    
    public final void testEnd(final String executionId, final int nestingLevel, final ITest test, 
            final String testName, final ITestResult result, final long executionTimeNanos) {

        synchronized (this) {
            this.resultByTestName.put(testName, result);
            this.executionTimeMsByTestName.put(testName, Long.valueOf(executionTimeNanos));
            this.allOK = (this.allOK && result.isOK());
            this.totalExecutionTimeMs += executionTimeNanos;
        }
        
        reportTestEnd(executionId, nestingLevel, test, testName, result, executionTimeNanos);
        
    }
    
    
    protected abstract void reportTestEnd(final String executionId, final int nestingLevel, final ITest test, 
            final String testName, final ITestResult result, final long executionTimeNanos);




    
    public synchronized boolean isAllOK() {
        return this.allOK;
    }


    public synchronized long getTotalExecutionTimeMs() {
        return this.totalExecutionTimeMs;
    }

    

    public synchronized Set<String> getAllTestNames() {
        return this.resultByTestName.keySet();
    }
    
    
    public synchronized ITestResult getResultByTestName(final String testName) {
        return this.resultByTestName.get(testName);
    }
    

    public synchronized long getExecutionTimeMsByTestName(final String testName) {
        final Long value = this.executionTimeMsByTestName.get(testName);
        if (value == null) {
            return -1;
        }
        return value.longValue();
    }
    
    
    public synchronized void reset() {
        this.resultByTestName.clear();
        this.executionTimeMsByTestName.clear();
        this.allOK = true;
        this.totalExecutionTimeMs = 0L;
    }

    
}

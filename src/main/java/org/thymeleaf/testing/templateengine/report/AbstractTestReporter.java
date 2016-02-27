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
package org.thymeleaf.testing.templateengine.report;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestResult;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;


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
    

    /*
     * This method exists only in order to compensate for the existence of "reportTestEnd", which is needed in order
     * to count the amount of ok/failed tests, execution times, etc.
     */
    protected void reportTestStart(final String executionId, final int nestingLevel, final ITest test, final String testName) {
        // Nothing here, meant to be overriden
    }
    

    
    
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
    
    
    protected void reportTestEnd(final String executionId, final int nestingLevel, final ITest test,
            final String testName, final ITestResult result, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }


    
    public synchronized final boolean isAllOK() {
        return this.allOK;
    }


    public synchronized final long getTotalExecutionTimeMs() {
        return this.totalExecutionTimeMs;
    }

    

    public synchronized final Set<String> getAllTestNames() {
        return this.resultByTestName.keySet();
    }
    
    
    public synchronized final ITestResult getResultByTestName(final String testName) {
        return this.resultByTestName.get(testName);
    }
    

    public synchronized final long getExecutionTimeMsByTestName(final String testName) {
        final Long value = this.executionTimeMsByTestName.get(testName);
        if (value == null) {
            return -1;
        }
        return value.longValue();
    }
    
    
    public synchronized final void reset() {
        this.resultByTestName.clear();
        this.executionTimeMsByTestName.clear();
        this.allOK = true;
        this.totalExecutionTimeMs = 0L;
    }

    public void executionStart(final String executionId) {
        // Nothing here, meant to be overriden
    }

    public void executionEnd(final String executionId, final int okTests, final int totalTests, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }

    public void sequenceStart(final String executionId, final int nestingLevel, final ITestSequence sequence) {
        // Nothing here, meant to be overriden
    }

    public void sequenceEnd(final String executionId, final int nestingLevel, final ITestSequence sequence,
                            final int okTests, final int totalTests, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }

    public void iteratorStart(final String executionId, final int nestingLevel, final ITestIterator iterator) {
        // Nothing here, meant to be overriden
    }

    public void iteratorEnd(final String executionId, final int nestingLevel, final ITestIterator iterator,
                            final int okTests, final int totalTests, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }

    public void iterationStart(final String executionId, final int nestingLevel, final ITestIterator iterator, final int iterationNumber) {
        // Nothing here, meant to be overriden
    }

    public void iterationEnd(final String executionId, final int nestingLevel, final ITestIterator iterator,
                             final int iterationNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }

    public void parallelizerStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer) {
        // Nothing here, meant to be overriden
    }

    public void parallelizerEnd(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer,
                                final int okTests, final int totalTests, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }

    public void parallelThreadStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer,
                                    final int threadNumber) {
        // Nothing here, meant to be overriden
    }

    public void parallelThreadEnd(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer,
                                  final int threadNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        // Nothing here, meant to be overriden
    }

}

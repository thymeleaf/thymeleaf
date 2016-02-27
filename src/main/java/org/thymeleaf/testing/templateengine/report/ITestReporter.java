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

import java.util.Set;

import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestResult;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;



public interface ITestReporter {


    public void executionStart(final String executionId);
    public void executionEnd(final String executionId, final int okTests, final int totalTests, final long executionTimeNanos);

    public void sequenceStart(final String executionId, final int nestingLevel, final ITestSequence sequence);
    public void sequenceEnd(final String executionId, final int nestingLevel, final ITestSequence sequence, final int okTests, final int totalTests, final long executionTimeNanos);
    
    public void iteratorStart(final String executionId, final int nestingLevel, final ITestIterator iterator);
    public void iteratorEnd(final String executionId, final int nestingLevel, final ITestIterator iterator, final int okTests, final int totalTests, final long executionTimeNanos);
    public void iterationStart(final String executionId, final int nestingLevel, final ITestIterator iterator, final int iterationNumber);
    public void iterationEnd(final String executionId, final int nestingLevel, final ITestIterator iterator, final int iterationNumber, final int okTests, final int totalTests, final long executionTimeNanos);
    
    public void parallelizerStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer);
    public void parallelizerEnd(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer, final int okTests, final int totalTests, final long executionTimeNanos);
    public void parallelThreadStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer, final int threadNumber);
    public void parallelThreadEnd(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer, final int threadNumber, final int okTests, final int totalTests, final long executionTimeNanos);

    /*
     * "testExecutionName" is needed instead of using test.getName() because the same ITest instance could be
     * used in different parts of a test set, and it should be identifiable by its name, even if it is null 
     * from test definition (one is synthetically created for it in such case).
     */
    public void testStart(final String executionId, final int nestingLevel, final ITest test, final String testName);
    public void testEnd(final String executionId, final int nestingLevel, final ITest test, final String testName, final ITestResult result, final long executionTimeNanos);
    
    
    
    public boolean isAllOK();
    public long getTotalExecutionTimeMs();
    public Set<String> getAllTestNames();
    public ITestResult getResultByTestName(final String testName);
    public long getExecutionTimeMsByTestName(final String testName);
    public void reset();
    
}

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
package org.thymeleaf.testing.templateengine.test.report;

import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestIterator;
import org.thymeleaf.testing.templateengine.test.ITestParallelizer;
import org.thymeleaf.testing.templateengine.test.ITestResult;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;



public interface ITestReporter {

    public void suiteStart(final String executionId, final int nestingLevel, final ITestSuite suite);
    public void suiteEnd(final String executionId, final int nestingLevel, final ITestSuite suite, final int totalTestsExecuted, final int totalTestsOk, final long executionTimeNanos);
    
    public void sequenceStart(final String executionId, final int nestingLevel, final ITestSequence sequence);
    public void sequenceEnd(final String executionId, final int nestingLevel, final ITestSequence sequence, final long executionTimeNanos);
    
    public void iteratorStart(final String executionId, final int nestingLevel, final ITestIterator iterator);
    public void iteratorEnd(final String executionId, final int nestingLevel, final ITestIterator iterator, final long executionTimeNanos);
    public void iterationStart(final String executionId, final int nestingLevel, final ITestIterator iterator, final int iterationNumber);
    public void iterationEnd(final String executionId, final int nestingLevel, final ITestIterator iterator, final int iterationNumber, final long executionTimeNanos);
    
    public void parallelizerStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer);
    public void parallelizerEnd(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer, final long executionTimeNanos);
    public void parallelThreadStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer, final int threadNumber);
    public void parallelThreadEnd(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer, final int threadNumber, final long executionTimeNanos);

    /*
     * "testExecutionName" is needed instead of using test.getName() because the same ITest instance could be
     * used in different parts of a suite's sequence, and the TestExecutionContext ensures the same name is always used
     * for it, even if it is null from test definition.
     */
    public void testStart(final String executionId, final int nestingLevel, final ITest test, final String testExecutionName);
    public void testEnd(final String executionId, final int nestingLevel, final ITest test, final String testExecutionName, final ITestResult result, final long executionTimeNanos);
    
}

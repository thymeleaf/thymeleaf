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
import org.thymeleaf.testing.templateengine.test.ITestResult;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;



public interface ITestReporter {

    public void suiteStart(final String executionId, final ITestSuite suite);
    public void suiteEnd(final String executionId, final ITestSuite suite, final int totalTestsExecuted, final int totalTestsOk, final long executionTimeNanos);
    
    public void sequenceStart(final String executionId, final ITestSequence sequence, final int nestingLevel);
    public void sequenceEnd(final String executionId, final ITestSequence sequence, final int nestingLevel, final long executionTimeNanos);
    
    public void iteratorStart(final String executionId, final ITestIterator iterator, final int nestingLevel);
    public void iteratorEnd(final String executionId, final ITestIterator iterator, final int nestingLevel, final long executionTimeNanos);
    public void iterationStart(final String executionId, final ITestIterator iterator, final int iteration, final int nestingLevel);
    public void iterationEnd(final String executionId, final ITestIterator iterator, final int iteration, final int nestingLevel, final long executionTimeNanos);
    
    public void testStart(final String executionId, final ITest test, final String testName, final int nestingLevel);
    public void testEnd(final String executionId, final ITest test, final String testName, final int nestingLevel, final long executionTimeNanos, final ITestResult result);
    
}

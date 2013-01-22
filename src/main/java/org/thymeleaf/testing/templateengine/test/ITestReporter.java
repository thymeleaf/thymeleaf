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



public interface ITestReporter {

    public void suiteStart(final ITestSuite suite);
    public void suiteEnd(final ITestSuite suite, final int totalTestsExecuted, final int totalTestsOk, final long executionTimeNanos);
    
    public void sequenceStart(final ITestSequence sequence, final int nestingLevel);
    public void sequenceEnd(final ITestSequence sequence, final int nestingLevel, final long executionTimeNanos);
    
    public void iteratorStart(final ITestIterator iterator, final int nestingLevel);
    public void iteratorEnd(final ITestIterator iterator, final int nestingLevel, final long executionTimeNanos);
    public void iterationStart(final ITestIterator iterator, final int iteration, final int nestingLevel);
    public void iterationEnd(final ITestIterator iterator, final int iteration, final int nestingLevel, final long executionTimeNanos);
    
    public void testStart(final ITest test, final String testName, final int nestingLevel);
    public void testEnd(final ITest test, final String testName, final int nestingLevel, final long executionTimeNanos, final ITestResult result);
    
}

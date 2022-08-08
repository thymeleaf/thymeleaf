/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2017, The THYMELEAF team (http://www.thymeleaf.org)
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


import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestResult;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;

public class MinimalConsoleTestReporter extends ConsoleTestReporter {



    public MinimalConsoleTestReporter() {
        super();
    }
    


    @Override
    public String msgSequenceStart(final ITestSequence sequence) {
        return null;
    }

    @Override
    public String msgIteratorStart(final ITestIterator iterator) {
        return null;
    }

    @Override
    public String msgIteratorEnd(final ITestIterator iterator, final int okTests, final int totalTests, final long executionTimeNanos) {
        return null;
    }

    @Override
    public String msgIterationStart(final ITestIterator iterator, final int iterationNumber) {
        return null;
    }

    @Override
    public String msgIterationEnd(final ITestIterator iterator, final int iterationNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        return null;
    }

    @Override
    public String msgParallelizerStart(final ITestParallelizer parallelizer) {
        return null;
    }

    @Override
    public String msgParallelizerEnd(final ITestParallelizer parallelizer, final int okTests, final int totalTests, final long executionTimeNanos) {
        return null;
    }

    @Override
    public String msgParallelThreadStart(final ITestParallelizer parallelizer, final int threadNumber) {
        return null;
    }

    @Override
    public String msgParallelThreadEnd(final ITestParallelizer parallelizer, final int threadNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        return null;
    }

    @Override
    public String msgTestStart(final ITest test, final String testName) {
        return null;
    }

    @Override
    public String msgTestEnd(final ITest test, final String testName, final ITestResult result, final long executionTimeNanos) {
        // Only output individual tests if they fail
        if (result.isOK()) {
            return null;
        }
        return super.msgTestEnd(test, testName, result, executionTimeNanos);
    }

}

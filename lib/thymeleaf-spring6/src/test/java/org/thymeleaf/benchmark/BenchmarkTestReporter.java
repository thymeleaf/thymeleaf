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
package org.thymeleaf.benchmark;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.thymeleaf.testing.templateengine.report.AbstractTestReporter;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestResult;


public final class BenchmarkTestReporter extends AbstractTestReporter {


    public BenchmarkTestReporter() {
        super();
    }



    @Override
    protected void reportTestEnd(final String executionId, final int nestingLevel,
                                 final ITest test, final String testName, final ITestResult result,
                                 final long executionTimeNanos) {

        if (!result.isOK()) {

            final StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("[test:end]");
            strBuilder.append("[" + testName + "]");
            strBuilder.append("[KO]");
            strBuilder.append(' ');
            strBuilder.append("Test FAILED");
            if (result.hasMessage()) {
                strBuilder.append(": " + result.getMessage());
            } else {
                strBuilder.append(". ");
            }

            if (result.hasThrowable()) {

                if (result.isOK()) {
                    // If result is OK, we just inform about the exception (correctly) obtained

                    strBuilder.append(" [Exception thrown: " +  result.getThrowable().getClass().getName() + ": " + result.getThrowable().getMessage() + "]");

                } else {

                    final Throwable throwable = result.getThrowable();
                    final StringWriter writer = new StringWriter();
                    final PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);

                    strBuilder.append("\n" + writer.toString());

                }

            }

            System.out.println(strBuilder.toString());

        }

    }



    public void executionEnd(final String executionId, final int okTests, final int totalTests, final long executionTimeNanos) {
        if (okTests == totalTests) {
            final long nanos = executionTimeNanos;
            final long millis = nanos / 1000000;
            System.out.println("[THYMELEAF][" + nanos + "][" + millis + "] BENCHMARK EXECUTED IN " + nanos + "ns (" + millis + "ms)");
        } else {
            System.out.println("[THYMELEAF] ERRORS DURING THE EXECUTION OF BENCHMARK: " + (totalTests - okTests) + " ERRORS IN " + totalTests + " TESTS");
        }
    }


}

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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestIterator;
import org.thymeleaf.testing.templateengine.test.ITestResult;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.util.Validate;



public abstract class AbstractTextualTestReporter implements ITestReporter {

    private static final String NOW_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(NOW_FORMAT);
    private static final BigInteger NANOS_IN_MILLIS = BigInteger.valueOf(1000000);
    
    
    private String reportName;
    
    
    
    protected AbstractTextualTestReporter(final String reportName) {
        super();
        Validate.notEmpty(reportName, "Report name cannot be null or empty");
        this.reportName = reportName;
    }
    
    
    
    public final String getReportName() {
        return this.reportName;
    }
    
    

    
    public final void suiteStart(final String executionId, final int nestingLevel, final ITestSuite suite) {
        outputMessage(executionId, msgSuiteStart(suite), nestingLevel, false);
    }
    
    public String msgSuiteStart(final ITestSuite suite) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[suite:begin]");
        if (suite.hasName()) {
            strBuilder.append("[" + suite.getName() + "]");
        }
        return strBuilder.toString();
    }


    
    
    public final void suiteEnd(final String executionId, final int nestingLevel, final ITestSuite suite, 
            final int totalTestsExecuted, final int totalTestsOk, final long executionTimeNanos) {
        outputMessage(executionId, msgSuiteEnd(suite, totalTestsExecuted, totalTestsOk, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgSuiteEnd(final ITestSuite suite, 
            final int totalTestsExecuted, final int totalTestsOk, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[suite:end]");
        if (suite.hasName()) {
            strBuilder.append("[" + suite.getName() + "]");
        }
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append("[" + totalTestsExecuted + "]");
        strBuilder.append("[" + totalTestsOk + "]");
        strBuilder.append("[" + (totalTestsExecuted - totalTestsOk) + "]");
        strBuilder.append(" Suite executed in " + duration(executionTimeNanos) + ".");
        strBuilder.append(" Tests executed: " + totalTestsExecuted + ". OK: " + totalTestsOk + ", FAILED: " + (totalTestsExecuted - totalTestsOk) + ".");
        return strBuilder.toString();
    }

    
    
    
    public final void sequenceStart(final String executionId, final int nestingLevel, final ITestSequence sequence) {
        outputMessage(executionId, msgSequenceStart(sequence), nestingLevel, false);
    }
    
    public String msgSequenceStart(final ITestSequence sequence) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[sequence:start]");
        if (sequence.hasName()) {
            strBuilder.append("[" + sequence.getName() + "]");
        }
        return strBuilder.toString();
    }
    
    

    
    public final void sequenceEnd(final String executionId, final int nestingLevel,  
            final ITestSequence sequence, final long executionTimeNanos) {
        outputMessage(executionId, msgSequenceEnd(sequence, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgSequenceEnd(final ITestSequence sequence, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[sequence:end]");
        if (sequence.hasName()) {
            strBuilder.append("[" + sequence.getName() + "]");
        }
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Sequence executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }

    
    
    
    public final void iteratorStart(final String executionId, final int nestingLevel, final ITestIterator iterator) {
        outputMessage(executionId, msgIteratorStart(iterator), nestingLevel, false);
    }
    
    public String msgIteratorStart(final ITestIterator iterator) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iterator:begin]");
        strBuilder.append("[" + iterator.getIterations() + "]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        return strBuilder.toString();
    }

    
    
    
    public final void iteratorEnd(final String executionId, final int nestingLevel, 
            final ITestIterator iterator, final long executionTimeNanos) {
        outputMessage(executionId, msgIteratorEnd(iterator, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgIteratorEnd(final ITestIterator iterator, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iterator:end]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        strBuilder.append("[" + iterator.getIterations() + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Iterator executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }

    
    
    
    public final void iterationStart(final String executionId, final int nestingLevel,  
            final ITestIterator iterator, final int iteration) {
        outputMessage(executionId, msgIterationStart(iterator, iteration), nestingLevel, false);
    }
    
    public String msgIterationStart(final ITestIterator iterator, final int iteration) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iteration:begin]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        strBuilder.append("[" + iteration + "]");
        strBuilder.append("[" + iterator.getIterations() + "]");
        return strBuilder.toString();
    }

    
    
    
    public final void iterationEnd(final String executionId, final int nestingLevel,  
            final ITestIterator iterator, final int iteration, final long executionTimeNanos) {
        outputMessage(executionId, msgIterationEnd(iterator, iteration, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgIterationEnd(final ITestIterator iterator, 
            final int iteration, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iteration:end]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        strBuilder.append("[" + iteration + "]");
        strBuilder.append("[" + iterator.getIterations() + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Iteration executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }

    
    
    
    public final void testStart(final String executionId, final int nestingLevel, final ITest test, final String testExecutionName) {
        outputMessage(executionId, msgTestStart(test, testExecutionName), nestingLevel, false);
    }
    
    @SuppressWarnings("unused")
    public String msgTestStart(final ITest test, final String testExecutionName) {
        // The standard implementation of this method is not outputting anything for test start events.
        return null;
    }

    
    
    
    public final void testEnd(final String executionId, final int nestingLevel, final ITest test, 
            final String testExecutionName, final ITestResult result, final long executionTimeNanos) {
        outputMessage(executionId, msgTestEnd(test, testExecutionName, result, executionTimeNanos), nestingLevel, !result.isOK());
    }

    @SuppressWarnings("unused")
    public String msgTestEnd(final ITest test, final String testExecutionName, final ITestResult result, final long executionTimeNanos) {
        
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[test:end]");
        strBuilder.append("[" + testExecutionName + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        if (result.isOK()) {
            strBuilder.append("[OK]");
        } else {
            strBuilder.append("[KO]");
        }
        strBuilder.append(' ');
        
        if (result.isOK()) {
            strBuilder.append("Test executed OK");
        } else {
            strBuilder.append("Test FAILED");
        }
        if (result.hasMessage()) {
            strBuilder.append(": " + result.getMessage());
        } else {
            strBuilder.append(". ");
        }

        strBuilder.append(" Time: " + duration(executionTimeNanos) + ".");
        
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
        
        return strBuilder.toString();
        
    }
    
    
    private final void outputMessage(final String executionId, final String message, final int nestingLevel, final boolean error) {
        if (message != null) {
            output(formatLine(executionId, message, nestingLevel), error);
        }
    }


    
    protected String formatLine(final String executionId, final String message, final int nestingLevel) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[" + now() + "][" + this.reportName + "][" + executionId + "] ");
        for (int i = 0; i < nestingLevel; i++) {
            strBuilder.append("  ");
        }
        strBuilder.append(message);
        return strBuilder.toString();
    }
    
    
    protected abstract void output(final String line, final boolean error);
    
    
    private String now() {
        final Calendar cal = Calendar.getInstance();
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.format(cal.getTime());
        }
    }
    
    
    private String duration(final long nanos) {
        final BigInteger nanosBI = BigInteger.valueOf(nanos);
        return nanosBI.toString() + "ns (" + nanosBI.divide(NANOS_IN_MILLIS) + "ms)";
    }
    
    
}

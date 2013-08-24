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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestResult;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;



public abstract class AbstractTextualTestReporter extends AbstractTestReporter {

    private static final String NOW_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(NOW_FORMAT);
    private static final BigInteger NANOS_IN_MILLIS = BigInteger.valueOf(1000000);
    
    
    
    
    protected AbstractTextualTestReporter() {
        super();
    }


    

    @Override
    public final void executionStart(final String executionId) {
        outputMessage(executionId, msgExecutionStart(), 0, false);
    }

    public String msgExecutionStart() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[EXECUTION:START]");
        return strBuilder.toString();
    }



    @Override
    public final void executionEnd(final String executionId, final int okTests, final int totalTests, final long executionTimeNanos) {
        outputMessage(executionId, msgExecutionEnd(okTests, totalTests, executionTimeNanos), 0, false);
    }

    public String msgExecutionEnd(final int okTests, final int totalTests, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[EXECUTION:END]");
        strBuilder.append("[" + okTests + "]");
        strBuilder.append("[" + totalTests + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Tests OK: " + okTests + " of " + totalTests +
                (okTests < totalTests ? (" (FAILED: " + (totalTests - okTests) + ")") : "") + ".");
        strBuilder.append(" Sequence executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }





    @Override
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




    @Override
    public final void sequenceEnd(final String executionId, final int nestingLevel,
            final ITestSequence sequence, final int okTests, final int totalTests, final long executionTimeNanos) {
        outputMessage(executionId, msgSequenceEnd(sequence, okTests, totalTests, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgSequenceEnd(final ITestSequence sequence, final int okTests, final int totalTests, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[sequence:end]");
        if (sequence.hasName()) {
            strBuilder.append("[" + sequence.getName() + "]");
        }
        strBuilder.append("[" + okTests + "]");
        strBuilder.append("[" + totalTests + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Tests OK: " + okTests + " of " + totalTests + 
                (okTests < totalTests ? (" (FAILED: " + (totalTests - okTests) + ")") : "") + ".");
        strBuilder.append(" Sequence executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }




    @Override
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




    @Override
    public final void iteratorEnd(final String executionId, final int nestingLevel,
            final ITestIterator iterator, final int okTests, final int totalTests, final long executionTimeNanos) {
        outputMessage(executionId, msgIteratorEnd(iterator, okTests, totalTests, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgIteratorEnd(final ITestIterator iterator, final int okTests, final int totalTests, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iterator:end]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        strBuilder.append("[" + iterator.getIterations() + "]");
        strBuilder.append("[" + okTests + "]");
        strBuilder.append("[" + totalTests + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Tests OK: " + okTests + " of " + totalTests + 
                (okTests < totalTests ? (" (FAILED: " + (totalTests - okTests) + ")") : "") + ".");
        strBuilder.append(" Iterator executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }




    @Override
    public final void iterationStart(final String executionId, final int nestingLevel,
            final ITestIterator iterator, final int iterationNumber) {
        outputMessage(executionId, msgIterationStart(iterator, iterationNumber), nestingLevel, false);
    }
    
    public String msgIterationStart(final ITestIterator iterator, final int iterationNumber) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iteration:begin]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        strBuilder.append("[" + iterationNumber + "]");
        strBuilder.append("[" + iterator.getIterations() + "]");
        return strBuilder.toString();
    }




    @Override
    public final void iterationEnd(final String executionId, final int nestingLevel,
            final ITestIterator iterator, final int iterationNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        outputMessage(executionId, msgIterationEnd(iterator, iterationNumber, okTests, totalTests, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgIterationEnd(final ITestIterator iterator, 
            final int iterationNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[iteration:end]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        }
        strBuilder.append("[" + iterationNumber + "]");
        strBuilder.append("[" + iterator.getIterations() + "]");
        strBuilder.append("[" + okTests + "]");
        strBuilder.append("[" + totalTests + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Tests OK: " + okTests + " of " + totalTests + 
                (okTests < totalTests ? (" (FAILED: " + (totalTests - okTests) + ")") : "") + ".");
        strBuilder.append(" Iteration executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }







    @Override
    public final void parallelizerStart(final String executionId, final int nestingLevel, final ITestParallelizer parallelizer) {
        outputMessage(executionId, msgParallelizerStart(parallelizer), nestingLevel, false);
    }
    
    public String msgParallelizerStart(final ITestParallelizer parallelizer) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[parallelizer:begin]");
        strBuilder.append("[" + parallelizer.getNumThreads() + "]");
        if (parallelizer.hasName()) {
            strBuilder.append("[" + parallelizer.getName() + "]");
        }
        return strBuilder.toString();
    }




    @Override
    public final void parallelizerEnd(final String executionId, final int nestingLevel,
            final ITestParallelizer parallelizer, final int okTests, final int totalTests, final long executionTimeNanos) {
        outputMessage(executionId, msgParallelizerEnd(parallelizer, okTests, totalTests, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgParallelizerEnd(final ITestParallelizer parallelizer, final int okTests, final int totalTests, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[parallelizer:end]");
        if (parallelizer.hasName()) {
            strBuilder.append("[" + parallelizer.getName() + "]");
        }
        strBuilder.append("[" + parallelizer.getNumThreads() + "]");
        strBuilder.append("[" + okTests + "]");
        strBuilder.append("[" + totalTests + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Tests OK: " + okTests + " of " + totalTests + 
                (okTests < totalTests ? (" (FAILED: " + (totalTests - okTests) + ")") : "") + ".");
        strBuilder.append(" Parallelizer executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }




    @Override
    public final void parallelThreadStart(final String executionId, final int nestingLevel,
            final ITestParallelizer parallelizer, final int threadNumber) {
        outputMessage(executionId, msgParallelThreadStart(parallelizer, threadNumber), nestingLevel, false);
    }
    
    public String msgParallelThreadStart(final ITestParallelizer parallelizer, final int threadNumber) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[parallelthread:begin]");
        if (parallelizer.hasName()) {
            strBuilder.append("[" + parallelizer.getName() + "]");
        }
        strBuilder.append("[" + threadNumber + "]");
        strBuilder.append("[" + parallelizer.getNumThreads() + "]");
        return strBuilder.toString();
    }




    @Override
    public final void parallelThreadEnd(final String executionId, final int nestingLevel,
            final ITestParallelizer parallelizer, final int threadNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        outputMessage(executionId, msgParallelThreadEnd(parallelizer, threadNumber, okTests, totalTests, executionTimeNanos), nestingLevel, false);
    }
    
    public String msgParallelThreadEnd(final ITestParallelizer parallelizer, 
            final int threadNumber, final int okTests, final int totalTests, final long executionTimeNanos) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[parallelthread:end]");
        if (parallelizer.hasName()) {
            strBuilder.append("[" + parallelizer.getName() + "]");
        }
        strBuilder.append("[" + threadNumber + "]");
        strBuilder.append("[" + parallelizer.getNumThreads() + "]");
        strBuilder.append("[" + okTests + "]");
        strBuilder.append("[" + totalTests + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Tests OK: " + okTests + " of " + totalTests + 
                (okTests < totalTests ? (" (FAILED: " + (totalTests - okTests) + ")") : "") + ".");
        strBuilder.append(" Parallel thread executed in " + duration(executionTimeNanos));
        return strBuilder.toString();
    }
    
    
    
    
    
    @Override
    public final void reportTestStart(final String executionId, final int nestingLevel, final ITest test, final String testName) {
        outputMessage(executionId, msgTestStart(test, testName), nestingLevel, false);
    }
    
    @SuppressWarnings("unused")
    public String msgTestStart(final ITest test, final String testName) {
        // The standard implementation of this method is not outputting anything for test start events.
        return null;
    }

    
    
    
    @Override
    public final void reportTestEnd(final String executionId, final int nestingLevel, final ITest test,
            final String testName, final ITestResult result, final long executionTimeNanos) {
        outputMessage(executionId, msgTestEnd(test, testName, result, executionTimeNanos), nestingLevel, !result.isOK());
    }

    
    @SuppressWarnings("unused")
    public String msgTestEnd(final ITest test, final String testName, final ITestResult result, final long executionTimeNanos) {
        
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[test:end]");
        strBuilder.append("[" + testName + "]");
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
    
    
    private void outputMessage(final String executionId, final String message, final int nestingLevel, final boolean error) {
        if (message != null) {
            output(formatLine(executionId, message, nestingLevel), error);
        }
    }


    
    protected String formatLine(final String executionId, final String message, final int nestingLevel) {
        final String threadName = Thread.currentThread().getName();
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[" + now() + "][" + executionId + "][" + threadName + "] ");
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

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

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.thymeleaf.util.Validate;



public class ConsoleTestReporter implements ITestReporter {

    private static final String NOW_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(NOW_FORMAT);
    private static final BigInteger NANOS_IN_MILLIS = BigInteger.valueOf(1000000);
    
    
    private String reportName;
    
    
    
    public ConsoleTestReporter(final String reportName) {
        super();
        Validate.notEmpty(reportName, "Report name cannot be null or empty");
        this.reportName = reportName;
    }
    
    
    
    public String getReportName() {
        return this.reportName;
    }
    
    

    
    public void suiteStart(final ITestSuite suite) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[SUITESTART]");
            strBuilder.append("[" + suite.getName() + "]");
        output(strBuilder.toString());
    }

    
    public void suiteEnd(final ITestSuite suite, final long executionTimeNanos) {
        
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[SUITEEND  ]");
        strBuilder.append("[" + suite.getName() + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Suite executed in " + duration(executionTimeNanos));
        
        output(strBuilder.toString());
        
    }

    
    
    
    public void sequenceStart(final ITestSequence sequence) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[SEQSTART  ]");
        if (sequence.hasName()) {
            strBuilder.append("[" + sequence.getName() + "]");
        } else {
            strBuilder.append("[ ]");
        }
        output(strBuilder.toString());
    }
    
    
    public void sequenceEnd(final ITestSequence sequence, final long executionTimeNanos) {
        
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[SEQEND    ]");
        if (sequence.hasName()) {
            strBuilder.append("[" + sequence.getName() + "]");
        } else {
            strBuilder.append("[ ]");
        }
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Sequence executed in " + duration(executionTimeNanos));
        
        output(strBuilder.toString());
        
    }

    
    
    
    public void iteratorStart(final ITestIterator iterator) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[ITERSTART ]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        } else {
            strBuilder.append("[ ]");
        }
        strBuilder.append("[" + iterator.getIterations() + "]");
        output(strBuilder.toString());
    }
    
    
    public void iteratorEnd(final ITestIterator iterator, final long executionTimeNanos) {
        
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[ITEREND   ]");
        if (iterator.hasName()) {
            strBuilder.append("[" + iterator.getName() + "]");
        } else {
            strBuilder.append("[ ]");
        }
        strBuilder.append("[" + iterator.getIterations() + "]");
        strBuilder.append("[" + executionTimeNanos + "]");
        strBuilder.append(" Iterator executed in " + duration(executionTimeNanos));
        
        output(strBuilder.toString());
    }

    
    
    
    public void testStart(final ITest test, final String testName) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[TESTSTART ]");
        strBuilder.append("[" + testName + "]");
        output(strBuilder.toString());
    }
    
    
    public void testEnd(final ITest test, final String testName, 
            final long executionTimeNanos, final ITestResult result) {
        
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[TESTEND   ]");
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
        if (result.hasThrowable()) {
            strBuilder.append(" [Exception thrown: " +  result.getThrowable().getClass().getName() + ": " + result.getThrowable().getMessage() + "]");
        }

        strBuilder.append(" - Test executed in " + duration(executionTimeNanos));
        
        output(strBuilder.toString());
        
    }


    
    private void output(final String message) {
        System.out.println("[" + now() + "][" + this.reportName + "] " + message);
    }
    
    
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

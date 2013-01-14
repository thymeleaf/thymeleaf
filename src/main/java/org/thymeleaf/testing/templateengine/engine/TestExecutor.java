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
package org.thymeleaf.testing.templateengine.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestIterator;
import org.thymeleaf.testing.templateengine.test.ITestReporter;
import org.thymeleaf.testing.templateengine.test.ITestResult;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.util.Validate;





public class TestExecutor {
    
    
    
    private static void execute(final ITestSuite suite) {
        try {
            executeSuite(suite);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(e);
        }
    }
    
 
    
    private static long executeSuite(final ITestSuite suite) {

        validateSuite(suite);
        
        final ITestSequence sequence = suite.getSequence(); 
        final ITestReporter reporter = suite.getReporter();
        
        
        final TemplateEngine templateEngine = null;
        
        reporter.suiteStart(suite);
        
        final long executionTimeNanos = 
                executeSequence(sequence, suite.getReporter(), templateEngine);
        
        reporter.suiteEnd(suite, executionTimeNanos);
        
        return executionTimeNanos;
        
    }


    
    private static long executeSequence(final ITestSequence sequence, final ITestReporter reporter, final TemplateEngine templateEngine) {

        reporter.sequenceStart(sequence);
        
        long totalTimeNanos = 0L;
        
        final List<ITestable> elements = sequence.getElements();
        
        for (final ITestable element : elements) {
            
            if (element instanceof ITestSequence) {
                final long elementExecTimeNanos = 
                        executeSequence((ITestSequence)element, reporter, templateEngine);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestIterator) {
                final long elementExecTimeNanos = 
                        executeIterator((ITestIterator)element, reporter, templateEngine);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITest) {
                final long elementExecTimeNanos = 
                        executeTest((ITest)element, reporter, templateEngine);
                totalTimeNanos += elementExecTimeNanos;
            } else {
                // Should never happen
                throw new TestEngineExecutionException(
                        "ITestable implementation \"" + element.getClass() + "\" is not recognized");
            }
            
        }
        
        reporter.sequenceEnd(sequence, totalTimeNanos);
        
        return totalTimeNanos;
        
    }
    
    
    
    private static long executeIterator(final ITestIterator iterator, final ITestReporter reporter, final TemplateEngine templateEngine) {

        reporter.iteratorStart(iterator);
        
        final int iterations = iterator.getIterations();
        final ITestable element = iterator.getIteratedElement();
        
        long totalTimeNanos = 0L;
        
        for (int i = 0; i < iterations; i++) {
            
            if (element instanceof ITestSequence) {
                final long elementExecTimeNanos = 
                        executeSequence((ITestSequence)element, reporter, templateEngine);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestIterator) {
                final long elementExecTimeNanos = 
                        executeIterator((ITestIterator)element, reporter, templateEngine);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITest) {
                final long elementExecTimeNanos = 
                        executeTest((ITest)element, reporter, templateEngine);
                totalTimeNanos += elementExecTimeNanos;
            } else {
                // Should never happen
                throw new TestEngineExecutionException(
                        "ITestable implementation \"" + element.getClass() + "\" is not recognized");
            }
            
        }
        
        reporter.iteratorEnd(iterator, totalTimeNanos);
        
        return totalTimeNanos;
        
    }
    
    
    
    private static long executeTest(final ITest test, final ITestReporter reporter, final TemplateEngine templateEngine) {

        reporter.testStart(test);
        
        long totalTimeNanos = 0L;

        final String input = test.getInput();
        
//        templateEngine.pro
//        
//        reporter.test(test, result, executionTimeNanos)
        
        final ITestResult result = null;
        
        reporter.testEnd(test, totalTimeNanos, result);
        
        return totalTimeNanos;
        
    }
    
  
    
    
    
    
    private static void validateSuite(final ITestSuite suite) {
        Validate.notNull(suite, "Suite cannot be null");
        validateSequence(suite.getSequence(), new HashMap<String,ITestable>());
    }
    
    
    
    private static void validateSequence(final ITestSequence sequence, final Map<String,ITestable> testablesByName) {
        Validate.notNull(sequence, "Sequence cannot be null");
         if (sequence.hasName()) {
            final String name = sequence.getName();
            final ITestable testable = 
         }
    }
    
    
    
    
    private TestExecutor() {
        super();
    }
    
}

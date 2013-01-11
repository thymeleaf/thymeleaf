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

import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestReporter;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.util.Validate;





public class TestExecutor {
    
    
    
    private static void execute(final ITestSuite suite) {
        Validate.notNull(suite, "Suite cannot be null");
        try {
            executeSuite(suite);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(e);
        }
    }
    
 
    
    private static long executeSuite(final ITestSuite suite) {
        
        final ITestReporter reporter = suite.getReporter();
        
        final TemplateEngine templateEngine = null;
        
        reporter.suiteStart();
        
        final long executionTimeNanos = 
                executeSequence(suite.getSequence(), suite.getReporter(), templateEngine);
        
        reporter.suiteEnd(executionTimeNanos);
        
        return executionTimeNanos;
        
    }


    
    private static long executeSequence(final ITestSequence sequence, final ITestReporter reporter, final TemplateEngine templateEngine) {

        reporter.sequenceStart(sequence);
        
        long totalTimeNanos = 0L;
        
        final int iterations = sequence.getIterations();
        final List<ITestable> elements = sequence.getElements();
        
        for (int i = 0; i < iterations; i++) {
            for (final ITestable element : elements) {
                
                if (element instanceof ITestSequence) {
                    final long seqExecTimeNanos = 
                            executeSequence((ITestSequence)element, reporter, templateEngine);
                    totalTimeNanos += seqExecTimeNanos;
                } else if (element instanceof ITest) {
                    executeTest((ITest)element, reporter, templateEngine);
                } else {
                    // Should never happen
                    throw new TestEngineExecutionException(
                            "ITestable implementation \"" + element.getClass() + "\" is not recognized");
                }
                
            }
        }
        
        reporter.sequenceEnd(sequence, totalTimeNanos);
        
        return totalTimeNanos;
        
    }
    
    
    
    private static long executeTest(final ITest test, final ITestReporter reporter, final TemplateEngine templateEngine) {

        final String input = test.getInput();
        
//        templateEngine.pro
//        
//        reporter.test(test, result, executionTimeNanos)
        return 1L;
    }
    
  
    
    
    
    
    private TestExecutor() {
        super();
    }
    
}

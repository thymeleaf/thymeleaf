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

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ProcessingContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.builder.ITestSuiteBuilder;
import org.thymeleaf.testing.templateengine.context.ContextNaming;
import org.thymeleaf.testing.templateengine.engine.resolver.TestTemplateResolver;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestIterator;
import org.thymeleaf.testing.templateengine.test.ITestParallelizer;
import org.thymeleaf.testing.templateengine.test.ITestResult;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.testing.templateengine.test.report.ITestReporter;
import org.thymeleaf.util.Validate;





public class TestExecutor {
    
    
    public TestExecutor() {
        super();
    }
    
    
    
    public void execute(final ITestSuiteBuilder testSuiteBuilder) {
        final String executionId = TestExecutionContext.generateExecutionId();
        try {
            final ITestSuite suite = testSuiteBuilder.build(executionId);
            execute(executionId, suite);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(executionId, e);
        }
    }
    
 
    
    public void execute(final ITestSuite suite) {
        final String executionId = TestExecutionContext.generateExecutionId();
        try {
            execute(executionId, suite);
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException(executionId, e);
        }
    }

    
    
    protected long execute(final String executionId, final ITestSuite suite) {

        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(suite, "Suite cannot be null");
        
        final TestTemplateResolver templateResolver = new TestTemplateResolver();
        final List<IDialect> dialects = suite.getDialects();
        
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setDialects(new HashSet<IDialect>(dialects));
        
        final TestExecutionContext context = 
                new TestExecutionContext(executionId, suite, suite.getReporter(), templateEngine);
        
        final long executionTimeNanos = executeSuite(suite, 0, context);
        
        return executionTimeNanos;
        
    }

    
    protected long executeSuite(final ITestSuite suite, final int nestingLevel, 
            final TestExecutionContext context) {

        Validate.notNull(suite, "Suite cannot be null");
        Validate.isTrue(nestingLevel >= 0, "Nesting level must be equal or greater than zero");
        Validate.notNull(context, "Test execution context cannot be null");
        
        final ITestReporter reporter = suite.getReporter();
        
        final ITestSequence sequence = suite.getSequence();
        
        reporter.suiteStart(context.getExecutionId(), nestingLevel, suite);
        
        final long executionTimeNanos = executeSequence(sequence, nestingLevel + 1, context);
        
        reporter.suiteEnd(
                context.getExecutionId(), 
                nestingLevel,
                suite, 
                context.getTotalTestsExecuted(), 
                context.getTotalTestsOk(), 
                executionTimeNanos);
        
        return executionTimeNanos;
        
    }

    
    protected long executeSequence(final ITestSequence sequence, final int nestingLevel, 
            final TestExecutionContext context) {

        Validate.notNull(sequence, "Sequence cannot be null");
        Validate.isTrue(nestingLevel >= 0, "Nesting level must be equal or greater than zero");
        Validate.notNull(context, "Test execution context cannot be null");

        final ITestReporter reporter = context.getReporter();
        
        reporter.sequenceStart(context.getExecutionId(), nestingLevel, sequence);
        
        long totalTimeNanos = 0L;
        
        final List<ITestable> elements = sequence.getElements();
        final int childNestingLevel = nestingLevel + 1;
        
        for (final ITestable element : elements) {
            
            if (element instanceof ITestSequence) {
                final long elementExecTimeNanos = 
                        executeSequence((ITestSequence)element, childNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestIterator) {
                final long elementExecTimeNanos = 
                        executeIterator((ITestIterator)element, childNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestParallelizer) {
                final long elementExecTimeNanos = 
                        executeParallelizer((ITestParallelizer)element, childNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITest) {
                final long elementExecTimeNanos = 
                        executeTest((ITest)element, childNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else {
                // Should never happen
                throw new TestEngineExecutionException(
                        "ITestable implementation \"" + element.getClass() + "\" is not recognized");
            }
            
        }
        
        reporter.sequenceEnd(context.getExecutionId(), nestingLevel, sequence, totalTimeNanos);
        
        return totalTimeNanos;
        
    }
    
    
    
    protected long executeIterator(final ITestIterator iterator, final int nestingLevel, 
            final TestExecutionContext context) {

        Validate.notNull(iterator, "Iterator cannot be null");
        Validate.isTrue(nestingLevel >= 0, "Nesting level must be equal or greater than zero");
        Validate.notNull(context, "Test execution context cannot be null");

        final ITestReporter reporter = context.getReporter();
        
        reporter.iteratorStart(context.getExecutionId(), nestingLevel, iterator);
        
        final int iterations = iterator.getIterations();
        final ITestable element = iterator.getIteratedElement();
        final int iterationNestingLevel = nestingLevel + 1;
        final int iterationContentNestingLevel = nestingLevel + 2;
        
        long totalTimeNanos = 0L;
        
        for (int i = 0; i < iterations; i++) {
            
            reporter.iterationStart(context.getExecutionId(), iterationNestingLevel, iterator, (i + 1));
            
            long elementExecTimeNanos = -1L;
            
            if (element instanceof ITestSequence) {
                elementExecTimeNanos = 
                        executeSequence((ITestSequence)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestIterator) {
                elementExecTimeNanos = 
                        executeIterator((ITestIterator)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestParallelizer) {
                elementExecTimeNanos = 
                        executeParallelizer((ITestParallelizer)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITest) {
                elementExecTimeNanos = 
                        executeTest((ITest)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else {
                // Should never happen
                throw new TestEngineExecutionException(
                        "ITestable implementation \"" + element.getClass() + "\" is not recognized");
            }
            
            reporter.iterationEnd(context.getExecutionId(), iterationNestingLevel, iterator, (i + 1), elementExecTimeNanos);
            
        }
        
        reporter.iteratorEnd(context.getExecutionId(), nestingLevel, iterator, totalTimeNanos);
        
        return totalTimeNanos;
        
    }
    
    
    
    protected long executeParallelizer(final ITestParallelizer parallelizer, final int nestingLevel, 
            final TestExecutionContext context) {

        Validate.notNull(parallelizer, "Parallelizer cannot be null");
        Validate.isTrue(nestingLevel >= 0, "Nesting level must be equal or greater than zero");
        Validate.notNull(context, "Test execution context cannot be null");

        final ITestReporter reporter = context.getReporter();
        
        reporter.parallelizerStart(context.getExecutionId(), nestingLevel, parallelizer);
        
        final int numThreads = parallelizer.getNumThreads();
        final ITestable element = parallelizer.getParallelizedElement();
        final int iterationNestingLevel = nestingLevel + 1;
        final int iterationContentNestingLevel = nestingLevel + 2;
        
        long totalTimeNanos = 0L;
        
        for (int i = 0; i < numThreads; i++) {
            
            reporter.parallelThreadStart(context.getExecutionId(), iterationNestingLevel, parallelizer, (i + 1));
            
            long elementExecTimeNanos = -1L;
            
            if (element instanceof ITestSequence) {
                elementExecTimeNanos = 
                        executeSequence((ITestSequence)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestIterator) {
                elementExecTimeNanos = 
                        executeIterator((ITestIterator)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITestParallelizer) {
                elementExecTimeNanos = 
                        executeParallelizer((ITestParallelizer)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else if (element instanceof ITest) {
                elementExecTimeNanos = 
                        executeTest((ITest)element, iterationContentNestingLevel, context);
                totalTimeNanos += elementExecTimeNanos;
            } else {
                // Should never happen
                throw new TestEngineExecutionException(
                        "ITestable implementation \"" + element.getClass() + "\" is not recognized");
            }
            
            reporter.parallelThreadEnd(context.getExecutionId(), iterationNestingLevel, parallelizer, (i + 1), elementExecTimeNanos);
            
        }
        
        reporter.parallelizerEnd(context.getExecutionId(), nestingLevel, parallelizer, totalTimeNanos);
        
        return totalTimeNanos;
        
    }
    
    
    
    protected long executeTest(final ITest test, final int nestingLevel, 
            final TestExecutionContext context) {

        Validate.notNull(test, "Test cannot be null");
        Validate.isTrue(nestingLevel >= 0, "Nesting level must be equal or greater than zero");
        Validate.notNull(context, "Test execution context cannot be null");
        
        final ITestReporter reporter = context.getReporter();
        final TemplateEngine templateEngine = context.getTemplateEngine();
        
        final String testExecutionName = context.registerTest(test);
        
        reporter.testStart(context.getExecutionId(), nestingLevel, test, testExecutionName);
        
        final IContext ctx = test.getContext();
        
        final Map<String,Object> localVariables = 
                Collections.singletonMap(
                        ContextNaming.TEST_EXECUTION_CONTEXT_VARIABLE_NAME, (Object)context);
        
        final ProcessingContext processingContext = new ProcessingContext(ctx, localVariables);
        
        final IFragmentSpec fragmentSpec = test.getFragmentSpec();

        final StringWriter writer = new StringWriter();

        ITestResult testResult = null;
        
        long startTimeNanos = System.nanoTime();
        long endTimeNanos;
        try {
            
            templateEngine.process(testExecutionName, processingContext, fragmentSpec, writer);
            endTimeNanos = System.nanoTime();
            
            final String result = writer.toString();
            testResult = test.evalResult(testExecutionName, result);
            
        } catch (final Throwable t) {
            endTimeNanos = System.nanoTime();
            testResult = test.evalResult(testExecutionName, t);
        }
        
        final long totalTimeNanos = (endTimeNanos - startTimeNanos);
        
        reporter.testEnd(context.getExecutionId(), nestingLevel, test, testExecutionName, testResult, totalTimeNanos);
        
        context.registerResult(testResult.isOK());
        
        return totalTimeNanos;
        
    }
    
    
    
    
    
}

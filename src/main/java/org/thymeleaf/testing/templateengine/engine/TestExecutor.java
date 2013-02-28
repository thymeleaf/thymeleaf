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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ProcessingContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.context.ContextNaming;
import org.thymeleaf.testing.templateengine.engine.resolver.TestTemplateResolver;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.standard.builder.StandardTestBuilder;
import org.thymeleaf.testing.templateengine.standard.resolver.StandardClassPathTestableResolver;
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
    
    private ITestableResolver testableResolver = null;
    
    
    
    public TestExecutor() {
        super();
        this.testableResolver = new StandardClassPathTestableResolver(new StandardTestBuilder());
    }

    
    
    public ITestableResolver getTestableResolver() {
        return this.testableResolver;
    }

    public void setTestableResolver(final ITestableResolver testableResolver) {
        this.testableResolver = testableResolver;
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

        final ITestReporter reporter = suite.getReporter();
        final String testableName = suite.getTestableName();

        final ITestable testable = this.testableResolver.resolve(executionId, testableName);
        if (testable == null) {
            throw new TestEngineExecutionException(executionId, "Main testable element resolved as null");
        }
        
        reporter.suiteStart(context.getExecutionId(), 0, suite);
        
        final long executionTimeNanos = executeTestable(testable, 1, context);
        
        reporter.suiteEnd(
                context.getExecutionId(), 
                0,
                suite, 
                context.getTotalTestsExecuted(), 
                context.getTotalTestsOk(), 
                executionTimeNanos);
        
        return executionTimeNanos;
        
    }

    
    
    protected long executeTestable(final ITestable testable, final int nestingLevel, 
            final TestExecutionContext context) {

        Validate.notNull(testable, "Testable cannot be null");
        
        if (testable instanceof ITestSequence) {
            return executeSequence((ITestSequence)testable, nestingLevel, context);
        } else if (testable instanceof ITestIterator) {
            return executeIterator((ITestIterator)testable, nestingLevel, context);
        } else if (testable instanceof ITestParallelizer) {
            return executeParallelizer((ITestParallelizer)testable, nestingLevel, context);
        } else if (testable instanceof ITest) {
            return executeTest((ITest)testable, nestingLevel, context);
        } else {
            // Should never happen
            throw new TestEngineExecutionException(
                    "ITestable implementation \"" + testable.getClass() + "\" is not recognized");
        }
        
        
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
            totalTimeNanos += executeTestable(element, childNestingLevel, context);
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
            
            final long elementExecTimeNanos =
                    executeTestable(element, iterationContentNestingLevel, context);
            totalTimeNanos += elementExecTimeNanos; 
            
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
        
        final int numThreads = parallelizer.getNumThreads();
        final List<FutureTask<Long>> tasks = new ArrayList<FutureTask<Long>>();
        
        long totalTimeNanos = 0L;
        
        final ThreadPoolExecutor threadExecutor = 
                new ThreadPoolExecutor(numThreads, numThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new SynchronousQueue<Runnable>());
        
        reporter.parallelizerStart(context.getExecutionId(), nestingLevel, parallelizer);
        
        for (int i = 0; i < numThreads; i++) {
            final ExecutorTask task = 
                    new ExecutorTask(this, context, reporter, parallelizer, (i + 1), nestingLevel);
            final FutureTask<Long> futureTask = new FutureTask<Long>(task);
            tasks.add(futureTask);
            threadExecutor.execute(futureTask);
        }

        for (final FutureTask<Long> futureTask : tasks) {
            try {
                final Long time = futureTask.get();
                totalTimeNanos += time.longValue();
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
        
        threadExecutor.shutdown();
        
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
        
        final Map<String,Object> localVariables = new HashMap<String, Object>();
        localVariables.put(
                ContextNaming.TEST_EXECUTION_CONTEXT_VARIABLE_NAME, context);
        localVariables.put(
                ContextNaming.TEST_EXECUTION_NAME_VARIABLE_NAME, testExecutionName);
        
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
    
    
    
    
    
    static class ExecutorTask implements Callable<Long> {

        private final TestExecutor executor;
        private final TestExecutionContext context;
        private final ITestReporter reporter;
        private final ITestParallelizer parallelizer;
        private final int threadNumber;
        private final int nestingLevel;
        

                
        ExecutorTask(final TestExecutor executor,
                final TestExecutionContext context, final ITestReporter reporter,
                final ITestParallelizer parallelizer, final int threadNumber,
                final int nestingLevel) {
            super();
            this.executor = executor;
            this.context = context;
            this.reporter = reporter;
            this.parallelizer = parallelizer;
            this.threadNumber = threadNumber;
            this.nestingLevel = nestingLevel;
        }



        public Long call() {

            final int parallelizerNestingLevel = this.nestingLevel + 1;
            final int parallelizedElementNestingLevel = this.nestingLevel + 2;
            
            final ITestable parallelizedElement = this.parallelizer.getParallelizedElement();
            
            this.reporter.parallelThreadStart(this.context.getExecutionId(), parallelizerNestingLevel, this.parallelizer, this.threadNumber);
            
            final long execTimeNanos =
                    this.executor.executeTestable(parallelizedElement, parallelizedElementNestingLevel, this.context);
            
            this.reporter.parallelThreadEnd(this.context.getExecutionId(), parallelizerNestingLevel, this.parallelizer, this.threadNumber, execTimeNanos);

            return Long.valueOf(execTimeNanos);
            
        }
        
        
    }
    
    
}

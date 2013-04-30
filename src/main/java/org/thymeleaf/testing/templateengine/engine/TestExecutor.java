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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.engine.cache.TestCacheManager;
import org.thymeleaf.testing.templateengine.engine.resolver.TestMessageResolver;
import org.thymeleaf.testing.templateengine.engine.resolver.TestTemplateResolver;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.report.ConsoleTestReporter;
import org.thymeleaf.testing.templateengine.report.ITestReporter;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.standard.resolver.StandardClassPathTestableResolver;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestResult;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;
import org.thymeleaf.testing.templateengine.testable.ITestable;
import org.thymeleaf.testing.templateengine.util.TestContextResolutionUtils;
import org.thymeleaf.util.Validate;





public final class TestExecutor {

    public static final ITestableResolver DEFAULT_TESTABLE_RESOLVER =
            new StandardClassPathTestableResolver();
    public static final List<IDialect> DEFAULT_DIALECTS = 
            Collections.singletonList((IDialect)new StandardDialect());
    public static final ITestReporter DEFAULT_TEST_REPORTER = new ConsoleTestReporter();
    
    
    private ITestableResolver testableResolver = DEFAULT_TESTABLE_RESOLVER;
    private List<IDialect> dialects = DEFAULT_DIALECTS;
    protected ITestReporter reporter = DEFAULT_TEST_REPORTER;
    
    
    private static ThreadLocal<String> threadExecutionId = new ThreadLocal<String>();
    private static ThreadLocal<String> threadTestName = new ThreadLocal<String>();
    private static ThreadLocal<ITest> threadTest = new ThreadLocal<ITest>();
    
    
    
    public static String getThreadExecutionId() {
        return threadExecutionId.get();
    }
    
    public static String getThreadTestName() {
        return threadTestName.get();
    }
    
    public static ITest getThreadTest() {
        return threadTest.get();
    }
    
    // protected in order to be accessed from parallelizer threads
    protected static void setThreadExecutionId(final String executionId) {
        threadExecutionId.set(executionId);
    }
    
    private static void setThreadTestName(final String testName) {
        threadTestName.set(testName);
    }
    
    private static void setThreadTest(final ITest test) {
        threadTest.set(test);
    }
    
    
    
    
    public TestExecutor() {
        super();
    }

    
    
    
    public ITestableResolver getTestableResolver() {
        return this.testableResolver;
    }

    public void setTestableResolver(final ITestableResolver testableResolver) {
        this.testableResolver = testableResolver;
    }

    


    
    
    public void setDialects(final List<? extends IDialect> dialects) {
        this.dialects = new ArrayList<IDialect>();
        this.dialects.addAll(dialects);
        this.dialects = Collections.unmodifiableList(dialects);
    }
    
    public List<IDialect> getDialects() {
        return this.dialects;
    }

    
    

    


    public void setReporter(final ITestReporter reporter) {
        Validate.notNull(reporter, "Reporter cannot be null");
        this.reporter = reporter;
    }
    
    public ITestReporter getReporter() {
        return this.reporter;
    }

    
    

    
    

    public void execute(final String testableName) {
        
        final TestExecutionContext context = new TestExecutionContext();
        final String executionId = context.getExecutionId();
        
        TestExecutor.setThreadExecutionId(executionId);
        
        try {
            
            final ITestable testable = this.testableResolver.resolve(executionId, testableName);
            if (testable == null) {
                throw new TestEngineExecutionException("Main testable element resolved as null");
            }
            
            execute(testable, context);
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Exception e) {
            throw new TestEngineExecutionException("Error executing testable \"" + testableName + "\"", e);
        }
        
    }

    
    
    private void execute(final ITestable testable, final TestExecutionContext context) {

        Validate.notNull(testable, "Testable cannot be null");
        Validate.notNull(context, "Test execution context cannot be null");
        
        final TestTemplateResolver templateResolver = new TestTemplateResolver();
        final TestMessageResolver messageResolver = new TestMessageResolver();
        final TestCacheManager cacheManager = new TestCacheManager();
        
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(messageResolver);
        templateEngine.setDialects(new HashSet<IDialect>(this.dialects));
        templateEngine.setCacheManager(cacheManager);
        
        context.setTemplateEngine(templateEngine);
        
        executeTestable(testable, context);
        
    }

    
    // protected in order to be accessed from parallelizer threads
    protected TestExecutionResult executeTestable(final ITestable testable, final TestExecutionContext context) {

        Validate.notNull(testable, "Testable cannot be null");
        
        if (testable instanceof ITestSequence) {
            return executeSequence((ITestSequence)testable, context);
        } else if (testable instanceof ITestIterator) {
            return executeIterator((ITestIterator)testable, context);
        } else if (testable instanceof ITestParallelizer) {
            return executeParallelizer((ITestParallelizer)testable, context);
        } else if (testable instanceof ITest) {
            return executeTest((ITest)testable, context);
        } else {
            // Should never happen
            throw new TestEngineExecutionException(
                    "ITestable implementation \"" + testable.getClass() + "\" is not recognized");
        }
        
        
    }
    
    
    private TestExecutionResult executeSequence(final ITestSequence sequence, final TestExecutionContext context) {

        Validate.notNull(sequence, "Sequence cannot be null");
        Validate.notNull(context, "Test execution context cannot be null");
        
        this.reporter.sequenceStart(context.getExecutionId(), context.getNestingLevel(), sequence);
        
        final TestExecutionResult result = new TestExecutionResult();
        
        final List<ITestable> elements = sequence.getElements();
        
        for (final ITestable element : elements) {
            result.addResult(executeTestable(element, context.nest()));
        }
        
        this.reporter.sequenceEnd(
                context.getExecutionId(), context.getNestingLevel(), sequence,
                result.getTotalTestsOk(), result.getTotalTestsExecuted(), 
                result.getTotalTimeNanos());
        
        return result;
        
    }
    
    
    
    private TestExecutionResult executeIterator(final ITestIterator iterator, final TestExecutionContext context) {

        Validate.notNull(iterator, "Iterator cannot be null");
        Validate.notNull(context, "Test execution context cannot be null");

        this.reporter.iteratorStart(context.getExecutionId(), context.getNestingLevel(), iterator);
        
        final int iterations = iterator.getIterations();
        final ITestable element = iterator.getIteratedElement();
        
        final TestExecutionResult result = new TestExecutionResult();
        final TestExecutionContext iterationContext = context.nest();
        
        for (int i = 1; i <= iterations; i++) {
            
            this.reporter.iterationStart(context.getExecutionId(), iterationContext.getNestingLevel(), iterator, i);

            final TestExecutionResult elementResult = executeTestable(element, iterationContext.nest());
            
            this.reporter.iterationEnd(
                    context.getExecutionId(), iterationContext.getNestingLevel(), iterator, i,
                    elementResult.getTotalTestsOk(), elementResult.getTotalTestsExecuted(), 
                    elementResult.getTotalTimeNanos());
            
            result.addResult(elementResult);
            
        }
        
        this.reporter.iteratorEnd(
                context.getExecutionId(), context.getNestingLevel(), iterator,
                result.getTotalTestsOk(), result.getTotalTestsExecuted(), 
                result.getTotalTimeNanos());
        
        return result;
        
    }
    
    
    
    private TestExecutionResult executeParallelizer(final ITestParallelizer parallelizer, final TestExecutionContext context) {

        Validate.notNull(parallelizer, "Parallelizer cannot be null");
        Validate.notNull(context, "Test execution context cannot be null");

        final int numThreads = parallelizer.getNumThreads();
        final List<FutureTask<TestExecutionResult>> tasks = new ArrayList<FutureTask<TestExecutionResult>>();
        
        final TestExecutionResult result = new TestExecutionResult();
        
        final ThreadPoolExecutor threadExecutor = 
                new ThreadPoolExecutor(numThreads, numThreads, Long.MAX_VALUE, TimeUnit.NANOSECONDS, new SynchronousQueue<Runnable>());
        
        this.reporter.parallelizerStart(context.getExecutionId(), context.getNestingLevel(), parallelizer);
        
        for (int i = 1; i <= numThreads; i++) {
            final ExecutorTask task =  new ExecutorTask(this, parallelizer, context, i);
            final FutureTask<TestExecutionResult> futureTask = new FutureTask<TestExecutionResult>(task);
            tasks.add(futureTask);
            threadExecutor.execute(futureTask);
        }

        for (final FutureTask<TestExecutionResult> futureTask : tasks) {
            try {
                result.addResult(futureTask.get());
            } catch (final Throwable t) {
                t.printStackTrace();
            }
        }
        
        threadExecutor.shutdown();
        
        this.reporter.parallelizerEnd(
                context.getExecutionId(), context.getNestingLevel(), parallelizer,
                result.getTotalTestsOk(), result.getTotalTestsExecuted(), 
                result.getTotalTimeNanos());
        
        return result;
        
    }
    
    
    
    private TestExecutionResult executeTest(final ITest test, final TestExecutionContext context) {

        Validate.notNull(test, "Test cannot be null");
        Validate.notNull(context, "Test execution context cannot be null");

        final String executionId = context.getExecutionId();
        final String testName = context.getTestNamer().nameTest(test);
        final TemplateEngine templateEngine = context.getTemplateEngine();
        
        setThreadTest(test);
        setThreadTestName(testName);
        
        this.reporter.testStart(executionId, context.getNestingLevel(), test, testName);
        
        final IFragmentSpec fragmentSpec = test.getFragmentSpec();
        
        final ITestContext testContext = test.getContext();
        final IContext ctx = TestContextResolutionUtils.resolveTestContext(testContext);
        if (ctx == null) {
            throw new TestEngineExecutionException("Resolved context is null for test \"" + testName + "\"");
        }
        
        final ProcessingContext processingContext = new ProcessingContext(ctx);
        
        final StringWriter writer = new StringWriter();

        ITestResult testResult = null;
        
        long startTimeNanos = System.nanoTime();
        long endTimeNanos;
        try {
            
            templateEngine.process(testName, processingContext, fragmentSpec, writer);
            endTimeNanos = System.nanoTime();
            
            final String result = writer.toString();
            testResult = test.evalResult(executionId, testName, result);
            
        } catch (final Throwable t) {
            endTimeNanos = System.nanoTime();
            testResult = test.evalResult(executionId, testName, t);
        }
        
        final long totalTimeNanos = (endTimeNanos - startTimeNanos);
        
        this.reporter.testEnd(executionId, context.getNestingLevel(), test, testName, testResult, totalTimeNanos);
        
        final TestExecutionResult result = new TestExecutionResult();
        result.addTestResult(testResult.isOK(), totalTimeNanos);

        setThreadTestName(null);
        setThreadTest(null);
        
        return result;
        
    }
    

    
    
    
    
    
    
    static class ExecutorTask implements Callable<TestExecutionResult> {

        private final TestExecutor executor;
        private final TestExecutionContext context;
        private final ITestParallelizer parallelizer;
        private final int threadNumber;
        

                
        ExecutorTask(final TestExecutor executor,
                final ITestParallelizer parallelizer,  final TestExecutionContext context,
                final int threadNumber) {
            super();
            this.executor = executor;
            this.context = context;
            this.parallelizer = parallelizer;
            this.threadNumber = threadNumber;
        }



        public TestExecutionResult call() {

            final TestExecutionContext threadExecutionContext = this.context.nest();
            
            TestExecutor.setThreadExecutionId(threadExecutionContext.getExecutionId());
            
            final ITestable parallelizedElement = this.parallelizer.getParallelizedElement();
            
            this.executor.reporter.parallelThreadStart(
                    this.context.getExecutionId(), threadExecutionContext.getNestingLevel(), this.parallelizer, this.threadNumber);
            
            final TestExecutionResult result =
                    this.executor.executeTestable(parallelizedElement, threadExecutionContext.nest());
            
            this.executor.reporter.parallelThreadEnd(
                    this.context.getExecutionId(), threadExecutionContext.getNestingLevel(), this.parallelizer, this.threadNumber,
                    result.getTotalTestsOk(), result.getTotalTestsExecuted(),
                    result.getTotalTimeNanos());

            return result;
            
        }
        
        
    }
    
    
}

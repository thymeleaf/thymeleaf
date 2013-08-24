/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.junit.Assert;
import org.junit.Test;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.report.AbstractTestReporter;


public class BenchmarkTest {


    public BenchmarkTest() {
        super();
    }
    
    
    
    
    @Test
    public void testBenchmark() throws Exception {

        final TestExecutor executor = new TestExecutor();
        executor.setReporter(new BenchmarkTestReporter());
        executor.execute("classpath:benchmark/benchmark.thindex");

        Assert.assertTrue(executor.isAllOK());
        
    }



    private static class BenchmarkTestReporter extends AbstractTestReporter {

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

    
}

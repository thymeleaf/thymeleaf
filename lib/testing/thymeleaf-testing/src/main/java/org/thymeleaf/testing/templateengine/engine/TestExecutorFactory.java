/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2017, The THYMELEAF team (http://www.thymeleaf.org)
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


import org.thymeleaf.testing.templateengine.context.IProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.context.web.JakartaServletTestWebExchangeBuilder;
import org.thymeleaf.testing.templateengine.context.web.JavaxServletTestWebExchangeBuilder;
import org.thymeleaf.testing.templateengine.context.web.WebProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.report.ITestReporter;
import org.thymeleaf.testing.templateengine.report.MinimalConsoleTestReporter;

public final class TestExecutorFactory {

    private static final ITestReporter MINIMAL_TEST_REPORTER = new MinimalConsoleTestReporter();


    public static TestExecutor createTestExecutor(final IProcessingContextBuilder processingContextBuilder) {
        final TestExecutor testExecutor = new TestExecutor(processingContextBuilder);
        testExecutor.setReporter(MINIMAL_TEST_REPORTER);
        return testExecutor;
    }

    public static TestExecutor createJakartaWebTestExecutor() {
        return createTestExecutor(new WebProcessingContextBuilder(JakartaServletTestWebExchangeBuilder.create()));
    }

    public static TestExecutor createJavaxWebTestExecutor() {
        return createTestExecutor(new WebProcessingContextBuilder(JavaxServletTestWebExchangeBuilder.create()));
    }



    private TestExecutorFactory() {
        super();
    }


}

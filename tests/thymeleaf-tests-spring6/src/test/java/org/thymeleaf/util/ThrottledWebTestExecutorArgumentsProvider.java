/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.engine.TestExecutorFactory;


public class ThrottledWebTestExecutorArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext extensionContext) throws Exception {
        return Arrays.stream(ThrottleArgumentsProvider.THROTTLE_STEPS)
                .mapToObj(Integer::valueOf)
                .map(ThrottledWebTestExecutorArgumentsProvider::createTestExecutorStream)
                .map(Arguments::of);
    }


    private static TestExecutor createTestExecutorStream(final int throttleStep) {
        final TestExecutor testExecutor = TestExecutorFactory.createJakartaWebTestExecutor();
        testExecutor.setThrottleStep(throttleStep);
        testExecutor.setDialects(Arrays.asList(new IDialect[]{SpringStandardDialectUtils.createSpringStandardDialectInstance()}));
        return testExecutor;
    }

}

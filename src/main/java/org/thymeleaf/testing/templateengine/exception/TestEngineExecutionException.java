/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.exception;

import org.thymeleaf.testing.templateengine.engine.TestExecutor;






public class TestEngineExecutionException extends RuntimeException {

    
    private static final long serialVersionUID = -341588084846304479L;

    
    
    public TestEngineExecutionException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public TestEngineExecutionException(final String message) {
        super(message);
    }

    public TestEngineExecutionException(final Throwable throwable) {
        super(throwable);
    }


    @Override
    public String getLocalizedMessage() {
        final String executionId = TestExecutor.getThreadExecutionId();
        if (executionId != null) {
            return "[" + executionId + "] " + super.getLocalizedMessage();
        }
        return super.getLocalizedMessage();
    }


    @Override
    public String getMessage() {
        final String executionId = TestExecutor.getThreadExecutionId();
        if (executionId != null) {
            return "[" + executionId + "] " + super.getMessage();
        }
        return super.getMessage();
    }
    
    
}

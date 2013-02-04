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
package org.thymeleaf.testing.templateengine.exception;






public class TestEngineExecutionException extends RuntimeException {

    
    private static final long serialVersionUID = -341588084846304479L;
    
    private final String executionId;

    
    
    public TestEngineExecutionException(final String executionId) {
        super();
        this.executionId = executionId;
    }

    
    public TestEngineExecutionException(final String executionId, final String message, final Throwable throwable) {
        super(message, throwable);
        this.executionId = executionId;
    }

    public TestEngineExecutionException(final String executionId, final String message) {
        super(message);
        this.executionId = executionId;
    }

    public TestEngineExecutionException(final String executionId, final Throwable throwable) {
        super(throwable);
        this.executionId = executionId;
    }


    @Override
    public String getLocalizedMessage() {
        return "[" + this.executionId + "] " + super.getLocalizedMessage();
    }


    @Override
    public String getMessage() {
        return "[" + this.executionId + "] " + super.getMessage();
    }
    
    
}

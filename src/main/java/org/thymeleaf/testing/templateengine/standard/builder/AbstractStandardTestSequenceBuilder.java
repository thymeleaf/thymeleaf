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
package org.thymeleaf.testing.templateengine.standard.builder;

import java.util.List;

import org.thymeleaf.testing.templateengine.builder.ITestSequenceBuilder;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestable;
import org.thymeleaf.testing.templateengine.test.TestSequence;
import org.thymeleaf.util.Validate;





public abstract class AbstractStandardTestSequenceBuilder implements ITestSequenceBuilder {
    
    private final String sequenceName;
    
    
    protected AbstractStandardTestSequenceBuilder(final String sequenceName) {
        super();
        this.sequenceName = sequenceName;
    }
    
    
    public String getSequenceName() {
        return this.sequenceName;
    }

    
    
    protected abstract List<ITestable> getSequenceContent(final String executionId);
    
    
    public final ITestSequence build(final String executionId) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");

        final TestSequence testSequence = new TestSequence();
        if (this.sequenceName != null) {
            testSequence.setName(this.sequenceName);
        }
        
        final List<ITestable> sequenceContent = getSequenceContent(executionId);
        
        if (sequenceContent == null) {
            return testSequence;
        }

        for (final ITestable testable : sequenceContent) {
            if (testable == null) {
                throw new TestEngineExecutionException(
                        executionId, "Test sequence content contains null object");
            }
            testSequence.addElement(testable);
        }
        
        return testSequence;
        
    }
    
}

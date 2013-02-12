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

import java.io.Reader;
import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.builder.ITestBuilder;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpecs;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestDocumentData;
import org.thymeleaf.testing.templateengine.standard.util.StandardTestDocumentResolutionUtils;
import org.thymeleaf.testing.templateengine.standard.util.StandardTestIOUtils;
import org.thymeleaf.testing.templateengine.test.FailExpectedTest;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.SuccessExpectedTest;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;





public abstract class AbstractStandardTestBuilder implements ITestBuilder {
    
    protected AbstractStandardTestBuilder() {
        super();
    }
    

    
    protected abstract String getDocumentName(final String executionId);
    protected abstract Reader getDocumentReader(final String executionId);
    
    
    @SuppressWarnings("unchecked")
    public final ITest build(final String executionId) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        
        final String documentName = getDocumentName(executionId);
        final Reader documentReader = getDocumentReader(executionId);
        
        final StandardTestDocumentData data = 
                StandardTestIOUtils.readTestDocument(executionId, documentName, documentReader);

        final Map<String,Object> values =
                StandardTestDocumentResolutionUtils.resolveTestDocumentData(
                        executionId, data, StandardTestDirectiveSpecs.STANDARD_DIRECTIVES_SET_SPEC);
        
        // input, cache, context, and template mode are required, cannot be null at this point 
        final ITestResource input = (ITestResource) values.get(StandardTestDirectiveSpecs.INPUT_DIRECTIVE_SPEC.getName());
        final Boolean cache = (Boolean) values.get(StandardTestDirectiveSpecs.CACHE_DIRECTIVE_SPEC.getName());
        final IContext ctx = (IContext) values.get(StandardTestDirectiveSpecs.CONTEXT_DIRECTIVE_SPEC.getName());
        final String templateMode = (String) values.get(StandardTestDirectiveSpecs.TEMPLATE_MODE_DIRECTIVE_SPEC.getName());
        
        // name and fragmentspec are optional, might be null at this point
        final String name = (String) values.get(StandardTestDirectiveSpecs.TEST_NAME_DIRECTIVE_SPEC.getName());
        final IFragmentSpec fragmentSpec = (IFragmentSpec) values.get(StandardTestDirectiveSpecs.FRAGMENT_DIRECTIVE_SPEC.getName());

        // The presence of output or exception will determine whether this is a success- or a fail-expected test
        final ITestResource output = (ITestResource) values.get(StandardTestDirectiveSpecs.OUTPUT_DIRECTIVE_SPEC.getName()); 
        final Class<? extends Throwable> exception = (Class<? extends Throwable>) values.get(StandardTestDirectiveSpecs.EXCEPTION_DIRECTIVE_SPEC.getName());
        
        if (output == null && exception == null) {
            throw new TestEngineExecutionException(
                    executionId, "Neither output nor exception have been specified for test in document " +
                    		     "\"" + documentName + "\". At least one of these must be specified.");
        }

        if (output != null) {
            
            final SuccessExpectedTest test = 
                    new SuccessExpectedTest(input, cache.booleanValue(), output);
            test.setName(name);
            test.setTemplateMode(templateMode);
            test.setContext(ctx);
            test.setFragmentSpec(fragmentSpec);
            
            return test;
            
        }
            
        final String exceptionMessagePattern = (String) values.get(StandardTestDirectiveSpecs.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC.getName());

        final FailExpectedTest test = 
                new FailExpectedTest(input, cache.booleanValue(), exception, exceptionMessagePattern);
        test.setName(name);
        test.setTemplateMode(templateMode);
        test.setContext(ctx);
        test.setFragmentSpec(fragmentSpec);

        return test;
        
    }
    
}

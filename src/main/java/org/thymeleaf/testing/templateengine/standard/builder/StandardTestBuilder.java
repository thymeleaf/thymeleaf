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

import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpec;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpecs;
import org.thymeleaf.testing.templateengine.standard.util.DirectiveUtils;
import org.thymeleaf.testing.templateengine.test.FailExpectedTest;
import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.SuccessExpectedTest;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;





public class StandardTestBuilder implements IStandardTestBuilder {

    
    public StandardTestBuilder() {
        super();
    }
    

    
    @SuppressWarnings("unchecked")
    public final ITest buildTest(final String executionId, 
            final String documentName, final Map<String,Map<String,Object>> dataByDirectiveAndQualifier) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(dataByDirectiveAndQualifier, "Data cannot be null");

        
        // Retrieve and process the map of inputs 
        final Map<String,ITestResource> inputs = 
                (Map<String,ITestResource>)(Map<?,?>) dataByDirectiveAndQualifier.get(StandardTestDirectiveSpecs.INPUT_DIRECTIVE_SPEC.getName());
        
        
        // cache, context, and template mode are required, cannot be null at this point 
        final Boolean cache = (Boolean) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.CACHE_DIRECTIVE_SPEC);
        final IContext ctx = (IContext) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.CONTEXT_DIRECTIVE_SPEC);
        final String templateMode = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.TEMPLATE_MODE_DIRECTIVE_SPEC);
        
        // name and fragmentspec are optional, might be null at this point
        final String name = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.TEST_NAME_DIRECTIVE_SPEC);
        final IFragmentSpec fragmentSpec = (IFragmentSpec) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.FRAGMENT_DIRECTIVE_SPEC);

        // The presence of output or exception will determine whether this is a success- or a fail-expected test
        final ITestResource output = (ITestResource) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.OUTPUT_DIRECTIVE_SPEC); 
        final Class<? extends Throwable> exception = (Class<? extends Throwable>) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.EXCEPTION_DIRECTIVE_SPEC);
        
        if (output == null && exception == null) {
            throw new TestEngineExecutionException(
                    executionId, "Neither output nor exception have been specified for test in document " +
                    		     "\"" + documentName + "\". At least one of these must be specified.");
        }

        if (output != null) {
            
            final SuccessExpectedTest test = 
                    new SuccessExpectedTest(inputs, cache.booleanValue(), output);
            test.setName(name);
            test.setTemplateMode(templateMode);
            test.setContext(ctx);
            test.setFragmentSpec(fragmentSpec);
            
            return test;
            
        }
            
        final String exceptionMessagePattern = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpecs.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC);

        final FailExpectedTest test = 
                new FailExpectedTest(inputs, cache.booleanValue(), exception, exceptionMessagePattern);
        test.setName(name);
        test.setTemplateMode(templateMode);
        test.setContext(ctx);
        test.setFragmentSpec(fragmentSpec);

        return test;
        
    }
    
    
    
    
    private static Object getMainDirectiveValue(final Map<String,Map<String,Object>> values, final StandardTestDirectiveSpec<?> directiveSpec) {
        
        final Map<String,Object> directiveValuesByQualifier = values.get(directiveSpec.getName());
        if (directiveValuesByQualifier == null) {
            return null;
        }
        return directiveValuesByQualifier.get(DirectiveUtils.MAIN_DIRECTIVE_QUALIFIER);
    }
    
    
    
}

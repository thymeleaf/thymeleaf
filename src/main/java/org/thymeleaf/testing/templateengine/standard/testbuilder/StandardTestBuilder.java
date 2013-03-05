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
package org.thymeleaf.testing.templateengine.standard.testbuilder;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.directive.StandardTestDirectiveSpec;
import org.thymeleaf.testing.templateengine.standard.directive.StandardTestDirectiveUtils;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestable;
import org.thymeleaf.testing.templateengine.testable.Test;
import org.thymeleaf.util.Validate;





public class StandardTestBuilder implements IStandardTestBuilder {

    
    private ITestableResolver testableResolver = null;
    
    

    public StandardTestBuilder() {
        super();
    }

    
    public StandardTestBuilder(final ITestableResolver testableResolver) {
        super();
        setTestableResolver(testableResolver);
    }
    
    

    
    public ITestableResolver getTestableResolver() {
        return this.testableResolver;
    }

    public void setTestableResolver(final ITestableResolver testableResolver) {
        this.testableResolver = testableResolver;
    }






    @SuppressWarnings("unchecked")
    public final ITest buildTest(final String executionId, 
            final String documentName, final Map<String,Map<String,Object>> dataByDirectiveAndQualifier) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(dataByDirectiveAndQualifier, "Data cannot be null");
        
        
        
        // Retrieve and process the map of inputs 
        final Map<String,ITestResource> additionalInputs =
                new HashMap<String, ITestResource>(
                        (Map<String,ITestResource>)(Map<?,?>) dataByDirectiveAndQualifier.get(StandardTestDirectiveSpec.INPUT_DIRECTIVE_SPEC.getName()));
        final ITestResource input = additionalInputs.get(null);
        additionalInputs.remove(null);
        
        
        // cache, context, and template mode are required, cannot be null at this point 
        final Boolean cache = (Boolean) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.CACHE_DIRECTIVE_SPEC);
        final IContext ctx = (IContext) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.CONTEXT_DIRECTIVE_SPEC);
        final String templateMode = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.TEMPLATE_MODE_DIRECTIVE_SPEC);
        
        // name and fragmentspec are optional, might be null at this point
        final String name = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.TEST_NAME_DIRECTIVE_SPEC);
        final IFragmentSpec fragmentSpec = (IFragmentSpec) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.FRAGMENT_DIRECTIVE_SPEC);

        // The presence of output or exception will determine whether this is a success- or a fail-expected test
        final ITestResource output = (ITestResource) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.OUTPUT_DIRECTIVE_SPEC); 
        final Class<? extends Throwable> exception = (Class<? extends Throwable>) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.EXCEPTION_DIRECTIVE_SPEC);
        
        final String exceptionMessagePattern = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC);


        /*
         * Initialize the test object
         */

        final Test test =  new Test();
        
        // If this test is supposed to extend another one, get the parent test 
        final String extendsValue = (String) getMainDirectiveValue(dataByDirectiveAndQualifier,StandardTestDirectiveSpec.EXTENDS_DIRECTIVE_SPEC);
        if (extendsValue != null) {
            
            if (this.testableResolver == null) {
                throw new TestEngineExecutionException(executionId, 
                        "Cannot execute \"" + StandardTestDirectiveSpec.EXTENDS_DIRECTIVE_SPEC.getName() + "\" " +
                		"directive: no " + ITestableResolver.class.getSimpleName() + " has been specified at the test builder.");
            }
            
            final ITestable parentTestable = 
                    this.testableResolver.resolve(executionId, extendsValue);
            if (parentTestable == null) {
                throw new TestEngineExecutionException(executionId, 
                        "Cannot execute \"" + StandardTestDirectiveSpec.EXTENDS_DIRECTIVE_SPEC.getName() + "\" " +
                        "directive: \"" + extendsValue + "\" resolved as null.");
            }
            if (!(parentTestable instanceof ITest)) {
                throw new TestEngineExecutionException(executionId, 
                        "Cannot execute \"" + StandardTestDirectiveSpec.EXTENDS_DIRECTIVE_SPEC.getName() + "\" " +
                        "directive: \"" + extendsValue + "\" resolved as a " + parentTestable.getClass().getName() + 
                        " object instead of an " + ITest.class.getName() + " implementation.");
            }
            final ITest testParent = (ITest) parentTestable;
            test.initializeFrom(testParent);
            
        }
        
        if (input != null) {
            test.setInput(input);
        }
        if (additionalInputs.size() > 0) {
            for (final Map.Entry<String,ITestResource> additionalInputEntry : additionalInputs.entrySet()) {
                test.setAdditionalInput(additionalInputEntry.getKey(), additionalInputEntry.getValue());
            }
        }
        if (cache != null) {
            test.setInputCacheable(cache.booleanValue());
        }
        if (output != null) {
            test.setOutput(output);
        }
        if (exception != null) {
            test.setOutputThrowableClass(exception);
        }
        if (exceptionMessagePattern != null) {
            test.setOutputThrowableMessagePattern(exceptionMessagePattern);
        }
        if (name != null) {
            test.setName(name);
        }
        if (templateMode != null) {
            test.setTemplateMode(templateMode);
        }
        if (ctx != null) {
            test.setContext(ctx);
        }
        if (fragmentSpec != null) {
            test.setFragmentSpec(fragmentSpec);
        }
        
        return test;
        
    }
    
    
    
    
    private static Object getMainDirectiveValue(final Map<String,Map<String,Object>> values, final StandardTestDirectiveSpec directiveSpec) {
        
        final Map<String,Object> directiveValuesByQualifier = values.get(directiveSpec.getName());
        if (directiveValuesByQualifier == null) {
            return null;
        }
        return directiveValuesByQualifier.get(StandardTestDirectiveUtils.MAIN_DIRECTIVE_QUALIFIER);
    }
    
    
    
}

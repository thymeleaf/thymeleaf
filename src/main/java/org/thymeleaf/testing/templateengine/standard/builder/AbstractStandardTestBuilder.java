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
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpec;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpecs;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestDocumentData;
import org.thymeleaf.testing.templateengine.standard.util.DirectiveUtils;
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

        final Map<String,Map<String,Object>> values =
                StandardTestDocumentResolutionUtils.resolveTestDocumentData(
                        executionId, data, StandardTestDirectiveSpecs.STANDARD_DIRECTIVES_SET_SPEC);
        
System.out.println(values);        
        // input, cache, context, and template mode are required, cannot be null at this point 
        final ITestResource input = (ITestResource) getMainDirectiveValue(values,StandardTestDirectiveSpecs.INPUT_DIRECTIVE_SPEC);
        final Boolean cache = (Boolean) getMainDirectiveValue(values,StandardTestDirectiveSpecs.CACHE_DIRECTIVE_SPEC);
        final IContext ctx = (IContext) getMainDirectiveValue(values,StandardTestDirectiveSpecs.CONTEXT_DIRECTIVE_SPEC);
        final String templateMode = (String) getMainDirectiveValue(values,StandardTestDirectiveSpecs.TEMPLATE_MODE_DIRECTIVE_SPEC);
        
        // name and fragmentspec are optional, might be null at this point
        final String name = (String) getMainDirectiveValue(values,StandardTestDirectiveSpecs.TEST_NAME_DIRECTIVE_SPEC);
        final IFragmentSpec fragmentSpec = (IFragmentSpec) getMainDirectiveValue(values,StandardTestDirectiveSpecs.FRAGMENT_DIRECTIVE_SPEC);

        // The presence of output or exception will determine whether this is a success- or a fail-expected test
        final ITestResource output = (ITestResource) getMainDirectiveValue(values,StandardTestDirectiveSpecs.OUTPUT_DIRECTIVE_SPEC); 
        final Class<? extends Throwable> exception = (Class<? extends Throwable>) getMainDirectiveValue(values,StandardTestDirectiveSpecs.EXCEPTION_DIRECTIVE_SPEC);
        
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
            
        final String exceptionMessagePattern = (String) getMainDirectiveValue(values,StandardTestDirectiveSpecs.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC);

        final FailExpectedTest test = 
                new FailExpectedTest(input, cache.booleanValue(), exception, exceptionMessagePattern);
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

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
package org.thymeleaf.testing.templateengine.standard.test.builder;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resolver.ITestableResolver;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.test.StandardTest;
import org.thymeleaf.testing.templateengine.standard.test.StandardTestValueType;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedData;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestFieldNaming;
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
    public final ITest buildTest(final String executionId, final StandardTestEvaluatedData data) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(data, "Data cannot be null");
        

        /*
         * Obtain general test data
         */

        final StandardTestEvaluatedField name = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_NAME);
        final StandardTestEvaluatedField cache = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_CACHE);
        final StandardTestEvaluatedField context = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_CONTEXT); 
        final StandardTestEvaluatedField templateMode = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_TEMPLATE_MODE); 
        final StandardTestEvaluatedField fragmentSpec = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_FRAGMENT);
        final Map<String,StandardTestEvaluatedField> inputs = data.getValuesByQualifierForField(StandardTestFieldNaming.FIELD_NAME_INPUT);
        final StandardTestEvaluatedField output = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_OUTPUT);
        final StandardTestEvaluatedField exception = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_EXCEPTION);
        final StandardTestEvaluatedField exceptionMessagePattern = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_EXCEPTION_MESSAGE_PATTERN);
        final StandardTestEvaluatedField extendsTest = getFieldValueForMainQualifier(data, StandardTestFieldNaming.FIELD_NAME_EXTENDS);

        
        /*
         * Organize inputs
         */
        final StandardTestEvaluatedField mainInput = 
                (inputs != null ? inputs.get(StandardTestFieldNaming.FIELD_QUALIFIER_MAIN) : null);
        final Map<String,StandardTestEvaluatedField> additionalInputs = 
                (inputs != null ? new HashMap<String, StandardTestEvaluatedField>(inputs) : new HashMap<String, StandardTestEvaluatedField>());
        additionalInputs.remove(StandardTestFieldNaming.FIELD_QUALIFIER_MAIN);


        ITest parentTest = null;
        if (extendsTest != null && extendsTest.hasNotNullValue()) {
            
            if (this.testableResolver == null) {
                throw new TestEngineExecutionException( 
                        "Cannot resolve \"" + StandardTestFieldNaming.FIELD_NAME_EXTENDS + "\" " +
                		"field: no " + ITestableResolver.class.getSimpleName() + " has been specified at the test builder.");
            }
            
            final String extendsValue = (String) extendsTest.getValue();
            
            final ITestable parentTestable = this.testableResolver.resolve(executionId, extendsValue);
            if (parentTestable == null) {
                throw new TestEngineExecutionException( 
                        "Cannot resolve \"" + StandardTestFieldNaming.FIELD_NAME_EXTENDS + "\" " +
                        "field: \"" + extendsValue + "\" resolved as null.");
            }
            if (!(parentTestable instanceof ITest)) {
                throw new TestEngineExecutionException( 
                        "Cannot resolve \"" + StandardTestFieldNaming.FIELD_NAME_EXTENDS + "\" " +
                        "field: \"" + extendsValue + "\" resolved as a " + parentTestable.getClass().getName() + 
                        " object instead of an " + ITest.class.getName() + " implementation.");
            }
            
            parentTest = (ITest) parentTestable;
            
        }


        /*
         * Initialize the test object
         */

        final StandardTest test =  createTestInstance();
        
        if (name != null && name.hasValue()) {
            test.setName((String)name.getValue(), name.getValueType());
        }
        if (templateMode != null && templateMode.hasValue()) {
            test.setTemplateMode((String)templateMode.getValue(), templateMode.getValueType());
        }
        if (context != null && context.hasValue()) {
            test.setContext((IContext)context.getValue(), context.getValueType());
        }
        if (cache != null && cache.hasNotNullValue()) {
            test.setInputCacheable(((Boolean)cache.getValue()).booleanValue(), cache.getValueType());
        }
        if (fragmentSpec != null && fragmentSpec.hasValue()) {
            test.setFragmentSpec((IFragmentSpec)fragmentSpec.getValue(), fragmentSpec.getValueType());
        }
        if (mainInput != null && mainInput.hasValue()) {
            test.setInput((ITestResource)mainInput.getValue(), mainInput.getValueType());
        }
        for (final Map.Entry<String,StandardTestEvaluatedField> additionalInputEntry : additionalInputs.entrySet()) {
            final StandardTestEvaluatedField additionalInputField = additionalInputEntry.getValue();
            if (additionalInputField != null) {
                test.setAdditionalInput(additionalInputEntry.getKey(), (ITestResource)additionalInputField.getValue(), additionalInputField.getValueType());
            }
        }
        if (output != null && output.hasValue()) {
            test.setOutput((ITestResource)output.getValue(), output.getValueType());
        }
        if (exception != null && exception.hasValue()) {
            test.setOutputThrowableClass((Class<? extends Throwable>)exception.getValue(), exception.getValueType());
        }
        if (exceptionMessagePattern != null && exceptionMessagePattern.hasValue()) {
            test.setOutputThrowableMessagePattern((String)exceptionMessagePattern.getValue(), exceptionMessagePattern.getValueType());
        }

        
        
        if (parentTest != null) {
            
            /*
             * Values are set from parent this way:
             * 
             *   - If a value for the test being built has been directly specified, just use it.
             *   - If the value used for the test being built is a default:
             *       - If the parent test is not a StandardTest (and therefore we have no info about
             *         the nature of is stored value), just set it from the parent.
             *       - If the parent test is a StandardTest, set it from the parent if the parent's
             *         value was directly specified (i.e. is not a default itself).
             *   - If there is no value for the test being built, just set whatever the parent has.
             *   
             */
        
            final StandardTest standardParentTest = 
                    (parentTest instanceof StandardTest ? (StandardTest)parentTest : null);
            
            if (shouldSetValueFromParent(test.getNameValueType(), (standardParentTest != null? standardParentTest.getNameValueType() : null))) {
                test.setName(parentTest.getName(), StandardTestValueType.SPECIFIED);
            }
            
            if (shouldSetValueFromParent(test.getTemplateModeValueType(), (standardParentTest != null? standardParentTest.getTemplateModeValueType() : null))) {
                test.setTemplateMode(parentTest.getTemplateMode(), StandardTestValueType.SPECIFIED);
            }
            
            if (shouldSetValueFromParent(test.getContextValueType(), (standardParentTest != null? standardParentTest.getContextValueType() : null))) {
                test.setContext(parentTest.getContext(), StandardTestValueType.SPECIFIED);
            }
            
            if (shouldSetValueFromParent(test.getCacheValueType(), (standardParentTest != null? standardParentTest.getCacheValueType() : null))) {
                test.setInputCacheable(parentTest.isInputCacheable(), StandardTestValueType.SPECIFIED);
            }
            
            if (shouldSetValueFromParent(test.getFragmentValueType(), (standardParentTest != null? standardParentTest.getFragmentValueType() : null))) {
                test.setFragmentSpec(parentTest.getFragmentSpec(), StandardTestValueType.SPECIFIED);
            }
            
            if (shouldSetValueFromParent(test.getInputValueType(), (standardParentTest != null? standardParentTest.getInputValueType() : null))) {
                test.setInput(parentTest.getInput(), StandardTestValueType.SPECIFIED);
            }

            for (final Map.Entry<String,ITestResource> additionalInputEntry : parentTest.getAdditionalInputs().entrySet()) {
                
                final String inputName = additionalInputEntry.getKey();
                final ITestResource inputTestResource = additionalInputEntry.getValue();
                
                // Might be null if input does not exist in test being built
                final StandardTestValueType additionalInputValueType = test.getAdditionalInputsValueTypes(inputName);
                
                final StandardTestValueType parentAdditionalInputValueType =
                        (standardParentTest != null? standardParentTest.getAdditionalInputsValueTypes(inputName) : null);
                
                if (additionalInputValueType == null 
                        || shouldSetValueFromParent(additionalInputValueType, parentAdditionalInputValueType)) {
                    test.setAdditionalInput(inputName, inputTestResource, StandardTestValueType.SPECIFIED);
                }
                
            }
            
            if (parentTest instanceof Test) {
            
                if (shouldSetValueFromParent(test.getOutputValueType(), (standardParentTest != null? standardParentTest.getOutputValueType() : null))) {
                    test.setOutput(((Test)parentTest).getOutput(), StandardTestValueType.SPECIFIED);
                }
                
                if (shouldSetValueFromParent(test.getOutputThrowableClassValueType(), (standardParentTest != null? standardParentTest.getOutputThrowableClassValueType() : null))) {
                    test.setOutputThrowableClass(((Test)parentTest).getOutputThrowableClass(), StandardTestValueType.SPECIFIED);
                }
                
                if (shouldSetValueFromParent(test.getOutputThrowableMessagePatternValueType(), (standardParentTest != null? standardParentTest.getOutputThrowableMessagePatternValueType() : null))) {
                    test.setOutputThrowableMessagePattern(((Test)parentTest).getOutputThrowableMessagePattern(), StandardTestValueType.SPECIFIED);
                }
                
            }
            
        }

        additionalInitialization(test, parentTest, data);
        
        return test;
        
    }
    
    
    
    
    private static StandardTestEvaluatedField getFieldValueForMainQualifier(
            final StandardTestEvaluatedData data, final String fieldName) {
        final Map<String,StandardTestEvaluatedField> valuesByQualifierForField = data.getValuesByQualifierForField(fieldName);
        if (valuesByQualifierForField == null) {
            return null;
        }
        return valuesByQualifierForField.get(StandardTestFieldNaming.FIELD_QUALIFIER_MAIN);
    }
    
    
    
    
    private static boolean shouldSetValueFromParent(
            final StandardTestValueType valueType, final StandardTestValueType parentValueType) {

        if (valueType.equals(StandardTestValueType.NO_VALUE)) {
            return true;
        }
        
        if (valueType.equals(StandardTestValueType.DEFAULT)) {
            if (parentValueType != null) {
                return parentValueType.equals(StandardTestValueType.SPECIFIED);
            }
            return true;
        }
        
        return false;
        
    }
    
    
    
    protected StandardTest createTestInstance() {
        return new StandardTest();
    }
    

    @SuppressWarnings("unused")
    protected void additionalInitialization(final StandardTest test, final ITest parentTest, final StandardTestEvaluatedData data) {
        // nothing to do here, meant to be overridden
    }
    
    
}

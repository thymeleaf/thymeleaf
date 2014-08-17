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
package org.thymeleaf.testing.templateengine.standard.test.evaluator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.resolver.StandardTestableResolver;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedData;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestFieldNaming;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestRawData;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.IStandardTestFieldEvaluator;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.StandardTestFieldEvaluationSpec;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.Validate;






public class StandardTestEvaluator implements IStandardTestEvaluator {
    

    public static final String EVALUATOR_FIELD_QUALIFIER = "evaluator";

    
    
    
    public StandardTestEvaluator() {
        super();
    }
    
    
    
    
    public StandardTestEvaluatedData evaluateTestData(
            final String executionId, final StandardTestRawData rawData, 
            final StandardTestableResolver resolver) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        Validate.notNull(rawData, "Data cannot be null");

        final StandardTestEvaluatedData data = new StandardTestEvaluatedData(rawData.getTestResource());

        final Set<StandardTestFieldEvaluationSpec> fieldSpecSet = getFieldSpecSet();
        
        final Map<String,StandardTestFieldEvaluationSpec> resolvedFieldSpecs = resolveAndValidateFieldSpecs(rawData, fieldSpecSet);        
        
        for (final Map.Entry<String,StandardTestFieldEvaluationSpec> fieldEntry : resolvedFieldSpecs.entrySet()) {

            final String fieldName = fieldEntry.getKey();
            final StandardTestFieldEvaluationSpec fieldSpec = fieldEntry.getValue();
            final IStandardTestFieldEvaluator evaluator = fieldSpec.getEvaluator();

            final Set<String> fieldQualifiers = 
                    new HashSet<String>(rawData.getQualifiersForField(fieldName));
            if (!fieldQualifiers.contains(StandardTestFieldNaming.FIELD_QUALIFIER_MAIN)) {
                // If the main qualifier has not been specified, we add it manually so that
                // we make sure all fields in the spec have at least one value (even if it is
                // the default one).
                fieldQualifiers.add(StandardTestFieldNaming.FIELD_QUALIFIER_MAIN);
            }
            
            
            for (final String fieldQualifier : fieldQualifiers) {

                if (fieldQualifier == null || !EVALUATOR_FIELD_QUALIFIER.equals(fieldQualifier)) {
                    
                    final StandardTestEvaluatedField evaluation = 
                            evaluator.getValue(executionId, rawData, resolver.getTestResourceResolver(), fieldName, fieldQualifier);
    
                    if (evaluation != null && evaluation.hasNotNullValue()) {
                        
                        final Object value = evaluation.getValue();
                            
                        final Class<?> valueClass = fieldSpec.getEvaluator().getValueClass();
                        if (!valueClass.isAssignableFrom(value.getClass())) {
                            // Value returned is not of the correct class
                            throw new TestEngineExecutionException(
                                    "A value of class \"" + value.getClass().getName() + "\" resulted from evaluation " +
                                    "of field \"" + fieldName + "\" in " +
                                    "\"" + data.getTestResource().getName() + "\", but value was expected to be of class " +
                                    "\"" + valueClass.getName() + "\"");
                        }
                        
                    }
                    
                    data.setValue(fieldName, fieldQualifier, evaluation);
                    
                }
                
            }

        }
        
        return data;
        
    }
    
    
    
    
    
    private static Map<String,StandardTestFieldEvaluationSpec> resolveAndValidateFieldSpecs(
            final StandardTestRawData data, final Set<StandardTestFieldEvaluationSpec> fieldSpecSet) {
        
        final Map<String,StandardTestFieldEvaluationSpec> resolvedFieldSpecs = new HashMap<String,StandardTestFieldEvaluationSpec>();
        
        /*
         * We add to the map all the specs defined at the spec set.
         */
        for (final StandardTestFieldEvaluationSpec spec : fieldSpecSet) {
            resolvedFieldSpecs.put(spec.getName(), spec);
        }
        
        /*
         * The data coming from reading the file is processed in order to find
         * possible substitutes to already-configured evaluators.
         */
        final Set<String> fieldNames = data.getFieldNames();
        for (final String fieldName : fieldNames) {
            
            if (!resolvedFieldSpecs.containsKey(fieldName)) {
                throw new TestEngineExecutionException(
                            "A field called \"" + fieldName +"\" " +
                            "has been found in \"" + data.getTestResource().getName() + "\", " +
                            "but test field specifications do not allow field \"" + fieldName + "\"");
            }

            final String fieldEvaluatorValue = 
                    data.getValueForFieldAndQualifier(fieldName, StandardTestEvaluator.EVALUATOR_FIELD_QUALIFIER);

            if (fieldEvaluatorValue != null) {
                
                final StandardTestFieldEvaluationSpec newSpec =
                        initializeFieldSpec(data.getTestResource(), fieldName, fieldEvaluatorValue);
                resolvedFieldSpecs.put(fieldName, newSpec);
                
            }
            
        }
        
        return resolvedFieldSpecs;
        
        
    }
    
    

    
    
    private static StandardTestFieldEvaluationSpec initializeFieldSpec(
            final ITestResource resource, final String fieldName, final String fieldValue){
        
        final String className = fieldValue.trim();
        try {
            
            final ClassLoader classLoader =
                    ClassLoaderUtils.getClassLoader(StandardTestEvaluator.class);
            final Class<?> evaluatorClass = classLoader.loadClass(className);
            
            if (!IStandardTestFieldEvaluator.class.isAssignableFrom(evaluatorClass)) {
                throw new TestEngineExecutionException(
                        "Specification found for field \"" + fieldName + "\" in " +
                        "\"" + resource.getName() + "\" selects class \"" + className + "\" as evaluator " +
                        "implementation. But this class does not implement the " + 
                        IStandardTestFieldEvaluator.class.getName() + " interface");
            }
            
            final IStandardTestFieldEvaluator evaluatorResolver = 
                    (IStandardTestFieldEvaluator) evaluatorClass.newInstance();
            
            final StandardTestFieldEvaluationSpec newSpec =
                    new StandardTestFieldEvaluationSpec(fieldName, evaluatorResolver);
            
            return newSpec;
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    "Error while initializing evaluator for field \"" + fieldName + "\" in " +
                    "\"" + resource.getName() + "\"", t);
        }
        
        
    }
    
    
    
    
    
    protected Set<StandardTestFieldEvaluationSpec> getFieldSpecSet() {
        return StandardTestFieldEvaluationSpec.STANDARD_TEST_FIELD_SPECS;
    }
    
    
    
}

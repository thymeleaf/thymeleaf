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
package org.thymeleaf.engine21.domselector;

import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceResolver;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators.AbstractStandardTestFieldEvaluator;


public class TestFragmentTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {


    public static final IFragmentSpec DEFAULT_VALUE = WholeFragmentSpec.INSTANCE;


    public TestFragmentTestFieldEvaluator() {
        super(IFragmentSpec.class);
    }


    @Override
    protected StandardTestEvaluatedField getValue(final String executionId, final ITestResource resource, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {

        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forDefaultValue(DEFAULT_VALUE);
        }

        final IFragmentSpec fragmentSpec =
                new DOMSelectorFragmentSpec(fieldValue.trim(), new TestNodeReferenceChecker());

        return StandardTestEvaluatedField.forSpecifiedValue(fragmentSpec);
        
    }
    
    
}

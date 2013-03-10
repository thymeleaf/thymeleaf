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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;





public class DefaultCacheStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    
    public static final DefaultCacheStandardTestFieldEvaluator INSTANCE = 
            new DefaultCacheStandardTestFieldEvaluator();
    public static final Boolean DEFAULT_VALUE = Boolean.TRUE; 
    
    private DefaultCacheStandardTestFieldEvaluator() {
        super(Boolean.class);
    }


    @Override
    public StandardTestEvaluatedField getValue(final String executionId, final String documentName, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {
        
        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forDefaultValue(DEFAULT_VALUE);
        }
        
        final String value = fieldValue.trim().toLowerCase();
        final int valueLen = value.length();
        
        switch (valueLen) {
            case 2: return StandardTestEvaluatedField.forSpecifiedValue(Boolean.valueOf(value.equals("on")));
            case 3: return StandardTestEvaluatedField.forSpecifiedValue(Boolean.valueOf(value.equals("yes")));
            case 4: return StandardTestEvaluatedField.forSpecifiedValue(Boolean.valueOf(value.equals("true")));
        }

        return StandardTestEvaluatedField.forSpecifiedValue(Boolean.FALSE);
        
    }

    
}

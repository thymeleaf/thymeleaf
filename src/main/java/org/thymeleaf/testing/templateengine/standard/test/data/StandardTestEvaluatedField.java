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
package org.thymeleaf.testing.templateengine.standard.test.data;

import org.thymeleaf.testing.templateengine.standard.test.StandardTestValueType;
import org.thymeleaf.util.Validate;






public class StandardTestEvaluatedField {

    private static StandardTestEvaluatedField FOR_NO_VALUE = 
            new StandardTestEvaluatedField(StandardTestValueType.NO_VALUE, null);
    
    private final StandardTestValueType valueType;
    private final Object value;
    
    
    
    public static StandardTestEvaluatedField forNoValue() {
        return FOR_NO_VALUE;
    }
    
    public static StandardTestEvaluatedField forDefaultValue(final Object value) {
        return new StandardTestEvaluatedField(StandardTestValueType.DEFAULT, value);
    }
    
    public static StandardTestEvaluatedField forSpecifiedValue(final Object value) {
        return new StandardTestEvaluatedField(StandardTestValueType.SPECIFIED, value);
    }
    
    
    
    private StandardTestEvaluatedField(final StandardTestValueType valueType, final Object value) {
        super();
        Validate.notNull(valueType, "Value type cannot be null");
        this.valueType = valueType;
        this.value = value;
    }


    public boolean hasValue() {
        return !this.valueType.equals(StandardTestValueType.NO_VALUE);
    }
    
    public boolean hasNotNullValue() {
        return hasValue() && this.value != null;
    }

    public boolean isDefault() {
        return hasValue() && this.valueType.equals(StandardTestValueType.DEFAULT);
    }

    public boolean isSpecified() {
        return hasValue() && this.valueType.equals(StandardTestValueType.SPECIFIED);
    }
    
    
    public StandardTestValueType getValueType() {
        return this.valueType;
    }

    public Object getValue() {
        return this.value;
    }
    
    
}

/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.util.Validate;




public class StandardTestRawData {

    private final ITestResource resource;
    private final Map<String,Map<String,String>> valuesByFieldAndQualifier;
    
    
    public StandardTestRawData(final ITestResource resource) {
        super();
        Validate.notNull(resource, "Resource cannot be null");
        this.resource = resource;
        this.valuesByFieldAndQualifier = new HashMap<String, Map<String,String>>();
    }

    
    public ITestResource getTestResource() {
        return this.resource;
    }
    
    
    public Set<String> getFieldNames() {
        return this.valuesByFieldAndQualifier.keySet();
    }
    
    
    
    public Set<String> getQualifiersForField(final String fieldName) {
        
        Validate.notNull(fieldName, "Field name cannot be null");
        
        final Map<String,String> valuesByQualifierForField = 
                getValuesByQualifierForField(fieldName);

        if (valuesByQualifierForField == null) {
            return Collections.emptySet();
        }
        
        return valuesByQualifierForField.keySet();
        
    }
    
    
    
    public String getValueForFieldAndQualifier(final String fieldName, final String fieldQualifier) {
        
        Validate.notNull(fieldName, "Field name cannot be null");
        
        final Map<String,String> valuesByQualifierForField = 
                getValuesByQualifierForField(fieldName);

        if (valuesByQualifierForField == null) {
            return null;
        }
        
        return valuesByQualifierForField.get(fieldQualifier);
        
    }
    

    
    public Map<String,String> getValuesByQualifierForField(final String fieldName) {
        
        Validate.notNull(fieldName, "Field name cannot be null");
        
        final Map<String,String> valuesByQualifierForField = this.valuesByFieldAndQualifier.get(fieldName);

        if (valuesByQualifierForField == null) {
            return Collections.emptyMap();
        }
        
        return Collections.unmodifiableMap(valuesByQualifierForField);
        
    }


    
    public void setValue(final String fieldName, final String fieldQualifier, final String value) {
        
        Validate.notNull(fieldName, "Field name cannot be null");
            
        Map<String,String> valuesByQualifierForField = this.valuesByFieldAndQualifier.get(fieldName);
        if (valuesByQualifierForField == null) {
            valuesByQualifierForField = new HashMap<String,String>();
            this.valuesByFieldAndQualifier.put(fieldName, valuesByQualifierForField);
        }
        valuesByQualifierForField.put(fieldQualifier, value);
        
    }
    
    
}

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
package org.thymeleaf.testing.templateengine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;







public final class MultiValueProperties {

    private final Map<String,List<String>> values = new LinkedHashMap<String, List<String>>();
    
    
    
    public MultiValueProperties() {
        super();
    }
    

    public void load(final InputStream inputStream) throws IOException {
        final PropertiesSupport support = new PropertiesSupport(this.values);
        support.load(inputStream);
    }
 
    
    
    public boolean hasProperty(final String propertyName) {
        return this.values.get(propertyName) != null;
    }
    
    public List<String> getProperty(final String propertyName) {
        return this.values.get(propertyName);
    }
    
    
    public Set<Map.Entry<String,List<String>>> entrySet() {
        return this.values.entrySet();
    }
    
    
    
    
    private static class PropertiesSupport extends Properties {
        
        private static final long serialVersionUID = 6978654753874130829L;
        
        private final Map<String,List<String>> values;
        
        
        public PropertiesSupport(final Map<String,List<String>> values) {
            super();
            this.values = values;
        }


        @Override
        public synchronized Object put(final Object key, final Object value) {
            if (key == null || !(key instanceof String) || ((String)key).trim().equals("")) {
                throw new IllegalArgumentException("Cannot add key " + key + " to MultiValueProperties object");
            }
            final String keyStr = (String) key;
            List<String> valuesForKey = this.values.get(keyStr);
            if (valuesForKey == null) {
                valuesForKey = new ArrayList<String>();
                this.values.put(keyStr, valuesForKey);
            }
            valuesForKey.add(value == null? null : value.toString());
            return null;
        }
        
        
        
    }
    
}

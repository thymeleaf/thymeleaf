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
package org.thymeleaf.testing.templateengine.engine;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.Validate;






final class TestNamer {

    
    private final Map<ITest,String> namesByTest = new HashMap<ITest,String>();
    private final Map<String,Integer> countersByName = new HashMap<String, Integer>();

    
    
    
    TestNamer() {
        super();
    }
    
    

    public synchronized String nameTest(final ITest test) {
        
        Validate.notNull(test, "Test cannot be null");

        if (this.namesByTest.containsKey(test)) {
            return this.namesByTest.get(test);
        }
        
        if (test.hasName()) {
            
            String name = test.getName();
            Integer idForName = this.countersByName.get(name);
            if (idForName == null) {
                idForName = Integer.valueOf(1);
            } else {
                idForName = Integer.valueOf(idForName.intValue() + 1);
            }

            final String indexedName = String.format("%s-%03d", name, idForName);
            
            this.namesByTest.put(test, indexedName);
            this.countersByName.put(name, idForName); // we don't use the indexed name, but the original one
            
            return indexedName;
            
        }
            
        final String className = test.getClass().getSimpleName();
        Integer idForName = this.countersByName.get(className);
        if (idForName == null) {
            idForName = Integer.valueOf(1);
        } else {
            idForName = Integer.valueOf(idForName.intValue() + 1);
        }
        
        final String indexedName = String.format("%s-%03d", className, idForName);
        
        this.namesByTest.put(test, indexedName);
        this.countersByName.put(className, idForName); // we don't use the indexed name, but the original one
        
        return indexedName;
            
    }
    
    
    
    
    
}

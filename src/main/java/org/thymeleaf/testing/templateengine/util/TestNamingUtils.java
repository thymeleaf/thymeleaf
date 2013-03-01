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
package org.thymeleaf.testing.templateengine.util;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.Validate;






public class TestNamingUtils {

    
    private static final Map<String,ITest> testsByName = new HashMap<String,ITest>();
    private static final Map<ITest,String> namesByTest = new HashMap<ITest,String>();
    
    private static final Map<String,Integer> counterByClassName = new HashMap<String,Integer>();

    
    
    

    public static synchronized String nameTest(final ITest test) {
        
        Validate.notNull(test, "Test cannot be null");

        if (namesByTest.containsKey(test)) {
            return namesByTest.get(test);
        }
        
        if (test.hasName()) {
            
            final String name = test.getName();

            if (testsByName.containsKey(name)) {
                throw new TestEngineExecutionException(
                        "Duplicate test names: two or more different tests with the same name \"" + name + "\" exist");
            }
            
            testsByName.put(name, test);
            namesByTest.put(test, name);
            
            return name;
            
        }
            
        final String className = test.getClass().getSimpleName();
        Integer counter = counterByClassName.get(className);
        if (counter == null) {
            counter = Integer.valueOf(1);
        }
        
        final String name = String.format("%s-%05d", className, counter);
        
        counterByClassName.put(className, Integer.valueOf(counter.intValue() + 1));
        
        testsByName.put(name, test);
        namesByTest.put(test, name);
        
        return name;
            
    }
    
    
    
    
    
    private TestNamingUtils() {
        super();
    }
    
    
    
    
}

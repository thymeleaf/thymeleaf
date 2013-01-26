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
package org.thymeleaf.testing.templateengine.engine;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.test.ITest;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.util.Validate;





public final class TestExecutionContext {
    

    private final ITestSuite suite;
    
    private final Map<String,ITest> testsByName = new HashMap<String,ITest>();
    private final Map<ITest,String> namesByTest = new HashMap<ITest,String>();
    
    private final Map<String,Integer> counterByClassName = new HashMap<String,Integer>();
    
    private int totalTestsExecuted = 0;
    private int totalTestsOk = 0;
    

    TestExecutionContext(final ITestSuite suite) {
        super();
        this.suite = suite;
    }
    

    
    synchronized String registerTest(final ITest test) {
        
        Validate.notNull(test, "Test cannot be null");

        if (this.namesByTest.containsKey(test)) {
            return this.namesByTest.get(test);
        }
        
        if (test.hasName()) {
            
            final String name = test.getName();

            if (this.testsByName.containsKey(name)) {
                throw new TestEngineExecutionException(
                        "Duplicate test names: two or more tests with the same name \"" + name + "\" exist " +
                		"in suite" + (this.suite.hasName()? (" \"" + this.suite.getName() + "\"") : ""));
            }
            
            this.testsByName.put(name, test);
            this.namesByTest.put(test, name);
            
            return name;
            
        }
            
        final String className = test.getClass().getSimpleName();
        Integer counter = this.counterByClassName.get(className);
        if (counter == null) {
            counter = Integer.valueOf(1);
        }
        
        final String name = String.format("%s-%05d", className, counter);
        
        this.counterByClassName.put(className, Integer.valueOf(counter.intValue() + 1));
        
        this.testsByName.put(name, test);
        this.namesByTest.put(test, name);
        
        return name;
            
    }
    

    
    synchronized void registerResult(final boolean ok) {
        this.totalTestsExecuted++;
        if (ok) {
            this.totalTestsOk++;
        }
    }
    
    
    public int getTotalTestsOk() {
        return this.totalTestsOk;
    }
    
    public int getTotalTestsExecuted() {
        return this.totalTestsExecuted;
    }
    
    
    
    public ITestSuite getTestSuite() {
        return this.suite;
    }
    
    
    public synchronized ITest getTestByName(final String name) {
        Validate.notNull(name, "Test name cannot be null");
        return this.testsByName.get(name);
    }
    
    
    
}

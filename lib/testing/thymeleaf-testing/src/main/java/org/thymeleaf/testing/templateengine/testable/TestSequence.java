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
package org.thymeleaf.testing.templateengine.testable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.thymeleaf.util.Validate;




public class TestSequence 
        extends AbstractTestable 
        implements ITestSequence {
    

    private List<ITestable> elements = new ArrayList<ITestable>();

    
    
    public TestSequence() {
        super();
    }
    

    public TestSequence(final ITestable... testables) {
        super();
        Validate.notNull(testables, "Testable object assignation cannot be null");
        for (int i = 0; i< testables.length; i++) {
            addElement(testables[i]);
        }
    }

    
    
    public void addElement(final ITestable testable) {
        Validate.notNull(testable, "Testable object cannot be null");
        this.elements.add(testable);
    }
    
    
    public void addElements(final Collection<? extends ITestable> testables) {
        Validate.notNull(testables, "Testable collection cannot be null");
        Validate.containsNoNulls(testables, "Testable collection cannot contain nulls");
        this.elements.addAll(testables);
    }
    
    
    public void clearElements() {
        this.elements.clear();
    }
    
    
    public int getSize() {
        return this.elements.size();
    }
    
    
    public List<ITestable> getElements() {
        return Collections.unmodifiableList(this.elements);
    }
    
}

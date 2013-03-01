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
package org.thymeleaf.testing.templateengine.testable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.util.Validate;





public abstract class AbstractTest 
        extends AbstractTestable
        implements ITest {

    
    public static String DEFAULT_TEMPLATE_MODE = "HTML5";
    public static IFragmentSpec DEFAULT_FRAGMENT_SPEC = null;
    private static final String DEFAULT_MAIN_INPUT = "main";
    

    private IContext context = new Context(Locale.ENGLISH);
    private String templateMode = DEFAULT_TEMPLATE_MODE; 
    private IFragmentSpec fragmentSpec = DEFAULT_FRAGMENT_SPEC;
    private String mainInputName = DEFAULT_MAIN_INPUT;
    
    private final Map<String,ITestResource> inputs;
    private final boolean inputCacheable;

    private final Map<String,Object> extraData;
    
    
    
    protected AbstractTest(final Map<String,ITestResource> inputs, final boolean inputCacheable) {
        super();
        if (inputs == null) {
            this.inputs = Collections.emptyMap();
        } else {
            this.inputs = new HashMap<String,ITestResource>(inputs);
        }
        this.inputCacheable = inputCacheable;
        this.extraData = new HashMap<String,Object>();
    }


    

    
    public void setContext(final IContext context) {
        this.context = context;
    }
    
    public IContext getContext() {
        return this.context;
    }
    
    
    
    public void setTemplateMode(final String templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        this.templateMode = templateMode;
    }
    
    public String getTemplateMode() {
        return this.templateMode;
    }
    

    
    public boolean hasFragmentSpec() {
        return this.fragmentSpec != null;
    }
    
    public void setFragmentSpec(final IFragmentSpec fragmentSpec) {
        this.fragmentSpec = fragmentSpec;
    }
    
    public IFragmentSpec getFragmentSpec() {
        return this.fragmentSpec;
    }

    

    public String getMainInputName() {
        return this.mainInputName;
    }
    
    public void setMainInputName(final String mainInputName) {
        this.mainInputName = mainInputName;
    }
    
    
    

    public Set<String> getInputNames() {
        final Set<String> inputNames = new HashSet<String>(this.inputs.keySet());
        inputNames.remove(null);
        inputNames.add(getMainInputName());
        return Collections.unmodifiableSet(inputNames);
    }

    
    public Map<String,ITestResource> getAllInputs() {
        final Map<String,ITestResource> allInputs = new HashMap<String, ITestResource>(this.inputs);
        final ITestResource mainInput = allInputs.get(null);
        allInputs.remove(null);
        allInputs.put(getMainInputName(), mainInput);
        return Collections.unmodifiableMap(allInputs);
    }
    
    
    public ITestResource getInput(final String inputName) {
        Validate.notNull(inputName, "Input name cannot be null");
        if (inputName.equals(getMainInputName())) {
            return this.inputs.get(null);
        }
        return this.inputs.get(inputName);
    }

    
    public boolean isInputCacheable() {
        return this.inputCacheable;
    }



    
    public void addExtraData(final String name, final String value) {
        this.extraData.put(name, value);
    }
    
    public void addExtraData(final Map<String,Object> newExtraData) {
        this.extraData.putAll(newExtraData);
    }
    
    public Object getExtraData(final String name) {
        return this.extraData.get(name);
    }

    public Map<String,Object> getAllExtraData() {
        return Collections.unmodifiableMap(new HashMap<String,Object>(this.extraData));
    }
    
    
}

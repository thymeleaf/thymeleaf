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
package org.thymeleaf.testing.templateengine.testable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.context.TestContext;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;
import org.thymeleaf.testing.templateengine.messages.TestMessages;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.util.Validate;





public abstract class AbstractTest 
        extends AbstractTestable
        implements ITest {
    
    public static String DEFAULT_TEMPLATE_MODE = "HTML5";
    public static String DEFAULT_FRAGMENT_SPEC = null;
    public static boolean DEFAULT_INPUT_CACHEABLE = true;
    

    private ITestContext context = new TestContext();
    private ITestMessages messages = new TestMessages();
    private String templateMode = DEFAULT_TEMPLATE_MODE; 
    private String fragmentSpec = DEFAULT_FRAGMENT_SPEC;
    private ITestResource input = null;
    private boolean inputCacheable = DEFAULT_INPUT_CACHEABLE;

    private Map<String,ITestResource> additionalInputs = new HashMap<String, ITestResource>();
    
    
    
    protected AbstractTest() {
        super();
    }


    

    
    public void setContext(final ITestContext context) {
        Validate.notNull(context, "Context cannot be null");
        this.context = context;
    }
    
    public ITestContext getContext() {
        return this.context;
    }
    
    
    
    public void setMessages(final ITestMessages messages) {
        Validate.notNull(messages, "Messages cannot be null");
        this.messages = messages;
    }
    
    public ITestMessages getMessages() {
        return this.messages;
    }
    
    
    
    public void setTemplateMode(final String templateMode) {
        this.templateMode = templateMode;
    }
    
    public String getTemplateMode() {
        return this.templateMode;
    }
    

    
    public boolean hasFragmentSpec() {
        return this.fragmentSpec != null;
    }
    
    public void setFragmentSpec(final String fragmentSpec) {
        this.fragmentSpec = fragmentSpec;
    }
    
    public String getFragmentSpec() {
        return this.fragmentSpec;
    }

    


    
    public ITestResource getInput() {
        return this.input;
    }
    
    public void setInput(final ITestResource input) {
        this.input = input;
    }

    
        
    public Map<String,ITestResource> getAdditionalInputs() {
        return Collections.unmodifiableMap(this.additionalInputs);
    }

    public void setAdditionalInputs(final Map<String,ITestResource> additionalInputs) {
        this.additionalInputs = new HashMap<String,ITestResource>(additionalInputs);
    }
    
    public void setAdditionalInput(final String name, final ITestResource resource) {
        this.additionalInputs.put(name, resource);
    }
    
    
    
    
    public boolean isInputCacheable() {
        return this.inputCacheable;
    }

    public void setInputCacheable(final boolean inputCacheale) {
        this.inputCacheable = inputCacheale;
    }
    
    
    
}

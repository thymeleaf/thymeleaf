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
package org.thymeleaf.testing.templateengine.test;

import java.util.Locale;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;
import org.thymeleaf.util.Validate;





public abstract class AbstractTest 
        extends AbstractTestable
        implements ITest {

    
    public static String DEFAULT_TEMPLATE_MODE = "HTML5";
    public static IFragmentSpec DEFAULT_FRAGMENT_SPEC = null;
    

    private IContext context = new Context(Locale.ENGLISH);
    private String templateMode = DEFAULT_TEMPLATE_MODE; 
    private IFragmentSpec fragmentSpec = DEFAULT_FRAGMENT_SPEC; 
    
    private final ITestResource input;
    private final boolean inputCacheable;

    
    
    
    protected AbstractTest(final ITestResource input, final boolean inputCacheable) {
        super();
        this.input = input;
        this.inputCacheable = inputCacheable;
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

    

    public ITestResource getInput() {
        return this.input;
    }

    public boolean isInputCacheable() {
        return this.inputCacheable;
    }

    
    
}

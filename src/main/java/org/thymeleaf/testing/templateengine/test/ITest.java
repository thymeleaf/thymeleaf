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

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;


public interface ITest extends ITestable {
    
    public IContext getContext();
    
    public boolean hasFragmentSpec();
    public IFragmentSpec getFragmentSpec();
    
    public String getTemplateMode();
    
    public ITestResource getInput();
    public boolean isInputCacheable();
    
    public ITestResult evalResult(final String testName, final String result);
    public ITestResult evalResult(final String testName, final Throwable t);
    
}

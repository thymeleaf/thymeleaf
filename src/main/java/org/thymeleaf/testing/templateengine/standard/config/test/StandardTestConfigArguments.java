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
package org.thymeleaf.testing.templateengine.standard.config.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.ITestable;




public class StandardTestConfigArguments {


    private final ITestSuite suite;
    private final List<ITestable> path;
    private final String fileName;
    private final Map<String,String> directiveValues;
    
    
    public StandardTestConfigArguments(
            final ITestSuite suite, final List<ITestable> path, final String fileName,
            final Map<String,String> directiveValues) {
        super();
        this.suite = suite;
        this.path = Collections.unmodifiableList(new ArrayList<ITestable>(path));
        this.fileName = fileName;
        this.directiveValues = Collections.unmodifiableMap(new HashMap<String,String>(directiveValues));
    }


    public ITestSuite getSuite() {
        return this.suite;
    }

    public List<ITestable> getPath() {
        return this.path;
    }

    public String getFileName() {
        return this.fileName;
    }
    
    public Map<String,String> getAllDirectives() {
        return this.directiveValues;
    }
    
}

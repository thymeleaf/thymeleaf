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
package org.thymeleaf.testing.templateengine.standard.testfile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;






public final class StandardTestFileNaming {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char DIRECTIVE_PREFIX_CHAR = '%';
    public static final String DIRECTIVE_TEMPLATE_MODE = "MODE";
    public static final String DIRECTIVE_CACHE = "CACHE";
    public static final String DIRECTIVE_CONTEXT = "CONTEXT";
    public static final String DIRECTIVE_FRAGMENT = "FRAGMENT";
    public static final String DIRECTIVE_INPUT = "INPUT";
    public static final String DIRECTIVE_OUTPUT = "OUTPUT";
    
    
    public static final Set<String> DIRECTIVES_ALL;
    public static final Set<String> DIRECTIVES_ALL_BUT_INPUT_OUTPUT;
    public static final Set<String> DIRECTIVES_INPUT;
    public static final Set<String> DIRECTIVES_OUTPUT;
    
    
    
    static {
        
        final Set<String> allButInputOutput = new HashSet<String>();
        allButInputOutput.add(DIRECTIVE_TEMPLATE_MODE);
        allButInputOutput.add(DIRECTIVE_CACHE);
        allButInputOutput.add(DIRECTIVE_CONTEXT);
        allButInputOutput.add(DIRECTIVE_FRAGMENT);
        
        final Set<String> input = new HashSet<String>();
        input.add(DIRECTIVE_INPUT);
        
        final Set<String> output = new HashSet<String>();
        output.add(DIRECTIVE_OUTPUT);
        
        final Set<String> all = new HashSet<String>();
        all.addAll(allButInputOutput);
        all.addAll(input);
        all.addAll(output);
        
        DIRECTIVES_ALL = Collections.unmodifiableSet(all);
        DIRECTIVES_ALL_BUT_INPUT_OUTPUT = Collections.unmodifiableSet(allButInputOutput);
        DIRECTIVES_INPUT = Collections.unmodifiableSet(input);
        DIRECTIVES_OUTPUT = Collections.unmodifiableSet(output);
        
    }
    

    
    
    private StandardTestFileNaming() {
        super();
    }
    
}

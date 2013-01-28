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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;






public final class StandardTestFileDirectives {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char DIRECTIVE_PREFIX_CHAR = '%';
    
    public static final String TEMPLATE_NAME = "NAME";
    public static final Class<String> TEMPLATE_NAME_CLASS = String.class;
    
    public static final String TEMPLATE_MODE = "MODE";
    public static final Class<String> TEMPLATE_MODE_CLASS = String.class;
    
    public static final String CACHE = "CACHE";
    public static final Class<Boolean> CACHE_CLASS = Boolean.class;
    
    public static final String CONTEXT = "CONTEXT";
    public static final Class<IContext> CONTEXT_CLASS = IContext.class;
    
    public static final String FRAGMENT = "FRAGMENT";
    public static final Class<IFragmentSpec> FRAGMENT_CLASS = IFragmentSpec.class;
    
    public static final String INPUT = "INPUT";
    public static final Class<ITestResource> INPUT_CLASS = ITestResource.class;
    
    public static final String OUTPUT = "OUTPUT";
    public static final Class<ITestResource> OUTPUT_CLASS = ITestResource.class;
    
    public static final String EXCEPTION = "EXCEPTION";
    public static final Class<Throwable> EXCEPTION_CLASS = Throwable.class;
    
    public static final String EXCEPTION_MESSAGE_PATTERN = "EXCEPTION_MESSAGE_PATTERN";
    public static final Class<String> EXCEPTION_MESSAGE_PATTERN_CLASS = String.class;

    
    public static final Map<String,Class<?>> CLASSES_BY_DIRECTIVE;
    
    public static final Set<String> DIRECTIVES_ALL;
    public static final Set<String> DIRECTIVES_ALL_BUT_INPUT_OUTPUT;
    public static final Set<String> DIRECTIVES_INPUT;
    public static final Set<String> DIRECTIVES_OUTPUT;
    
    
    
    static {

        final Map<String,Class<?>> classesByDirective = new HashMap<String, Class<?>>();
        final Set<String> allButInputOutput = new HashSet<String>();
        final Set<String> input = new HashSet<String>();
        final Set<String> output = new HashSet<String>();
        
        allButInputOutput.add(TEMPLATE_NAME);
        classesByDirective.put(TEMPLATE_NAME, TEMPLATE_NAME_CLASS);
        
        allButInputOutput.add(TEMPLATE_MODE);
        classesByDirective.put(TEMPLATE_MODE, TEMPLATE_MODE_CLASS);
        
        allButInputOutput.add(CACHE);
        classesByDirective.put(CACHE, CACHE_CLASS);
        
        allButInputOutput.add(CONTEXT);
        classesByDirective.put(CONTEXT, CONTEXT_CLASS);
        
        allButInputOutput.add(FRAGMENT);
        classesByDirective.put(FRAGMENT, FRAGMENT_CLASS);
        
        input.add(INPUT);
        classesByDirective.put(INPUT, INPUT_CLASS);
        
        output.add(OUTPUT);
        classesByDirective.put(OUTPUT, OUTPUT_CLASS);
        
        output.add(EXCEPTION);
        classesByDirective.put(EXCEPTION, EXCEPTION_CLASS);
        
        output.add(EXCEPTION_MESSAGE_PATTERN);
        classesByDirective.put(EXCEPTION_MESSAGE_PATTERN, EXCEPTION_MESSAGE_PATTERN_CLASS);
        
        
        final Set<String> all = new HashSet<String>();
        all.addAll(allButInputOutput);
        all.addAll(input);
        all.addAll(output);
        
        DIRECTIVES_ALL = Collections.unmodifiableSet(all);
        DIRECTIVES_ALL_BUT_INPUT_OUTPUT = Collections.unmodifiableSet(allButInputOutput);
        DIRECTIVES_INPUT = Collections.unmodifiableSet(input);
        DIRECTIVES_OUTPUT = Collections.unmodifiableSet(output);
        CLASSES_BY_DIRECTIVE = Collections.unmodifiableMap(classesByDirective);
        
    }
    

    
    
    private StandardTestFileDirectives() {
        super();
    }
    
}

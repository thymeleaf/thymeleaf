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
package org.thymeleaf.testing.templateengine.standard.config.directive;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultCacheStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultContextStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultFragmentStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultTemplateModeStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultTestNameStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;






public final class StandardTestFileDirectives {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char DIRECTIVE_PREFIX_CHAR = '%';

    
    public static final StandardTestFileDirectiveSpec<String> TEST_NAME_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<String>("NAME", String.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<String> TEMPLATE_MODE_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<String>("MODE", String.class, DefaultTemplateModeStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<Boolean> CACHE_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<Boolean>("CACHE", Boolean.class, DefaultCacheStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<IContext> CONTEXT_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<IContext>("CONTEXT", IContext.class, DefaultContextStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<IFragmentSpec> FRAGMENT_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<IFragmentSpec>("FRAGMENT", IFragmentSpec.class, DefaultFragmentStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<ITestResource> INPUT_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<ITestResource>("INPUT", ITestResource.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<ITestResource> OUTPUT_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<ITestResource>("OUTPUT", ITestResource.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<Throwable> EXCEPTION_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<Throwable>("EXCEPTION", Throwable.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<String> EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_NAME = 
            new StandardTestFileDirectiveSpec<String>("EXCEPTION_MESSAGE_PATTERN", String.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);

    
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
        
        allButInputOutput.add(TEST_NAME_NAME);
        classesByDirective.put(TEST_NAME_NAME, TEST_NAME_EXPECTED_CLASS);
        
        allButInputOutput.add(TEMPLATE_MODE_NAME);
        classesByDirective.put(TEMPLATE_MODE_NAME, TEMPLATE_MODE_EXPECTED_CLASS);
        
        allButInputOutput.add(CACHE_NAME);
        classesByDirective.put(CACHE_NAME, CACHE_EXPECTED_CLASS);
        
        allButInputOutput.add(CONTEXT_NAME);
        classesByDirective.put(CONTEXT_NAME, CONTEXT_EXPECTED_CLASS);
        
        allButInputOutput.add(FRAGMENT_NAME);
        classesByDirective.put(FRAGMENT_NAME, FRAGMENT_EXPECTED_CLASS);
        
        input.add(INPUT_NAME);
        classesByDirective.put(INPUT_NAME, INPUT_EXPECTED_CLASS);
        
        output.add(OUTPUT_NAME);
        classesByDirective.put(OUTPUT_NAME, OUTPUT_EXPECTED_CLASS);
        
        output.add(EXCEPTION_NAME);
        classesByDirective.put(EXCEPTION_NAME, EXCEPTION_EXPECTED_CLASS);
        
        output.add(EXCEPTION_MESSAGE_PATTERN_NAME);
        classesByDirective.put(EXCEPTION_MESSAGE_PATTERN_NAME, EXCEPTION_MESSAGE_PATTERN_EXPECTED_CLASS);
        
        
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

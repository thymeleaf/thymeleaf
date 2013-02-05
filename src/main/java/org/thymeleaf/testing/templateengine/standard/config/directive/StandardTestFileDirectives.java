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
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultCacheStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultContextStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultExceptionDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultExceptionMessagePatternDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultFragmentStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultInputStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultOutputStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultTemplateModeStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.DefaultTestNameStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.test.resource.ITestResource;






public final class StandardTestFileDirectives {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char DIRECTIVE_PREFIX_CHAR = '%';

    
    public static final StandardTestFileDirectiveSpec<String> TEST_NAME_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<String>("NAME", String.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<String> TEMPLATE_MODE_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<String>("MODE", String.class, DefaultTemplateModeStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<Boolean> CACHE_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<Boolean>("CACHE", Boolean.class, DefaultCacheStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<IContext> CONTEXT_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<IContext>("CONTEXT", IContext.class, DefaultContextStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<IFragmentSpec> FRAGMENT_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<IFragmentSpec>("FRAGMENT", IFragmentSpec.class, DefaultFragmentStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<ITestResource> INPUT_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<ITestResource>("INPUT", ITestResource.class, DefaultInputStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<ITestResource> OUTPUT_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<ITestResource>("OUTPUT", ITestResource.class, DefaultOutputStandardDirectiveResolver.INSTANCE);
    
    @SuppressWarnings("unchecked")
    public static final StandardTestFileDirectiveSpec<Class<? extends Throwable>> EXCEPTION_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<Class<? extends Throwable>>("EXCEPTION", (Class<Class<? extends Throwable>>)(Class<?>)Class.class, DefaultExceptionDirectiveResolver.INSTANCE);
    
    public static final StandardTestFileDirectiveSpec<Pattern> EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC = 
            new StandardTestFileDirectiveSpec<Pattern>("EXCEPTION_MESSAGE_PATTERN", Pattern.class, DefaultExceptionMessagePatternDirectiveResolver.INSTANCE);

    
    public static final Set<StandardTestFileDirectiveSpec<?>> ALL_DIRECTIVE_SPECS;
    
    
    
    static {

        final Set<StandardTestFileDirectiveSpec<?>> all = new HashSet<StandardTestFileDirectiveSpec<?>>();
        
        all.add(TEST_NAME_DIRECTIVE_SPEC);
        all.add(TEMPLATE_MODE_DIRECTIVE_SPEC);
        all.add(CACHE_DIRECTIVE_SPEC);
        all.add(CONTEXT_DIRECTIVE_SPEC);
        all.add(INPUT_DIRECTIVE_SPEC);
        all.add(OUTPUT_DIRECTIVE_SPEC);
        all.add(EXCEPTION_DIRECTIVE_SPEC);
        all.add(EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC);
        
        ALL_DIRECTIVE_SPECS = Collections.unmodifiableSet(all);
        
    }
    

    
    
    private StandardTestFileDirectives() {
        super();
    }
    
}

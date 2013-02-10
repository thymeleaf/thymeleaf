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

import java.util.HashMap;
import java.util.Map;
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






public final class StandardTestDirectiveSpecs {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char DIRECTIVE_PREFIX_CHAR = '%';

    
    public static final StandardTestDirectiveSpec<String> TEST_NAME_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<String>("NAME", String.class, DefaultTestNameStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<String> TEMPLATE_MODE_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<String>("MODE", String.class, DefaultTemplateModeStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<Boolean> CACHE_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<Boolean>("CACHE", Boolean.class, DefaultCacheStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<IContext> CONTEXT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<IContext>("CONTEXT", IContext.class, DefaultContextStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<IFragmentSpec> FRAGMENT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<IFragmentSpec>("FRAGMENT", IFragmentSpec.class, DefaultFragmentStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<ITestResource> INPUT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<ITestResource>("INPUT", ITestResource.class, DefaultInputStandardDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<ITestResource> OUTPUT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<ITestResource>("OUTPUT", ITestResource.class, DefaultOutputStandardDirectiveResolver.INSTANCE);
    
    @SuppressWarnings("unchecked")
    public static final StandardTestDirectiveSpec<Class<? extends Throwable>> EXCEPTION_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<Class<? extends Throwable>>("EXCEPTION", (Class<Class<? extends Throwable>>)(Class<?>)Class.class, DefaultExceptionDirectiveResolver.INSTANCE);
    
    public static final StandardTestDirectiveSpec<Pattern> EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec<Pattern>("EXCEPTION_MESSAGE_PATTERN", Pattern.class, DefaultExceptionMessagePatternDirectiveResolver.INSTANCE);

    
    public static final StandardTestDirectiveSetSpec STANDARD_DIRECTIVES_SET_SPEC;
    
    
    
    static {

        final Map<String,StandardTestDirectiveSpec<?>> required = new HashMap<String,StandardTestDirectiveSpec<?>>();
        final Map<String,StandardTestDirectiveSpec<?>> optional = new HashMap<String,StandardTestDirectiveSpec<?>>();
        
        optional.put(TEST_NAME_DIRECTIVE_SPEC.getName(), TEST_NAME_DIRECTIVE_SPEC);
        optional.put(TEMPLATE_MODE_DIRECTIVE_SPEC.getName(), TEMPLATE_MODE_DIRECTIVE_SPEC);
        optional.put(CACHE_DIRECTIVE_SPEC.getName(), CACHE_DIRECTIVE_SPEC);
        required.put(CONTEXT_DIRECTIVE_SPEC.getName(), CONTEXT_DIRECTIVE_SPEC);
        required.put(INPUT_DIRECTIVE_SPEC.getName(), INPUT_DIRECTIVE_SPEC);
        optional.put(OUTPUT_DIRECTIVE_SPEC.getName(), OUTPUT_DIRECTIVE_SPEC);
        optional.put(EXCEPTION_DIRECTIVE_SPEC.getName(), EXCEPTION_DIRECTIVE_SPEC);
        optional.put(EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC.getName(), EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC);
        
        STANDARD_DIRECTIVES_SET_SPEC = new StandardTestDirectiveSetSpec(required, optional);
        
    }
    

    
    
    private StandardTestDirectiveSpecs() {
        super();
    }
    
}

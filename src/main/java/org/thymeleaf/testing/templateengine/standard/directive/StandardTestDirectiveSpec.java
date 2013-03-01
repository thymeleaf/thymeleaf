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
package org.thymeleaf.testing.templateengine.standard.directive;

import java.util.regex.Pattern;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultCacheStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultContextStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultExceptionDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultExceptionMessagePatternDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultFragmentStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultInputStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultOutputStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultTemplateModeStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultTestNameStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.IStandardDirectiveResolver;
import org.thymeleaf.util.Validate;






public final class StandardTestDirectiveSpec<T> {

    
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

    

    
    
    private final String name;
    private final Class<T> valueClass;
    private final IStandardDirectiveResolver<? extends T> resolver;

    
    
    
    public StandardTestDirectiveSpec(
            final String name, final Class<T> valueClass, IStandardDirectiveResolver<? extends T> resolver) {
        super();
        Validate.notNull(name, "Directive name cannot null");
        Validate.notNull(valueClass, "Directive value class cannot be null");
        Validate.notNull(resolver, "Directive resolver cannot be null");
        this.name = name;
        this.valueClass = valueClass;
        this.resolver = resolver;
    }


    public String getName() {
        return this.name;
    }


    public Class<T> getValueClass() {
        return this.valueClass;
    }


    public IStandardDirectiveResolver<? extends T> getResolver() {
        return this.resolver;
    }
    
    
}

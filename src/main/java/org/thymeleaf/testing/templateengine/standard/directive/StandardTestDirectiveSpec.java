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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultCacheStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultContextStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultExceptionDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultExceptionMessagePatternDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultExtendsStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultFragmentStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultInputStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultOutputStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultTemplateModeStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.DefaultTestNameStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.IStandardDirectiveResolver;
import org.thymeleaf.util.Validate;






public final class StandardTestDirectiveSpec {

    
    public static final char COMMENT_PREFIX_CHAR = '#';
    public static final char DIRECTIVE_PREFIX_CHAR = '%';

    
    public static final StandardTestDirectiveSpec TEST_NAME_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("NAME", DefaultTestNameStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec TEMPLATE_MODE_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("MODE", DefaultTemplateModeStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec CACHE_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("CACHE", DefaultCacheStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec CONTEXT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("CONTEXT", DefaultContextStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec FRAGMENT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("FRAGMENT", DefaultFragmentStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec INPUT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("INPUT", DefaultInputStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec OUTPUT_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("OUTPUT", DefaultOutputStandardDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec EXCEPTION_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("EXCEPTION", DefaultExceptionDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("EXCEPTION_MESSAGE_PATTERN", DefaultExceptionMessagePatternDirectiveResolver.INSTANCE);
    public static final StandardTestDirectiveSpec EXTENDS_DIRECTIVE_SPEC = 
            new StandardTestDirectiveSpec("EXTENDS", DefaultExtendsStandardDirectiveResolver.INSTANCE);


    public static final Set<StandardTestDirectiveSpec> STANDARD_DIRECTIVES =
            Collections.unmodifiableSet(
                    new HashSet<StandardTestDirectiveSpec>(Arrays.asList(
                            new StandardTestDirectiveSpec[] {
                                    StandardTestDirectiveSpec.TEST_NAME_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.TEMPLATE_MODE_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.CACHE_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.CONTEXT_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.INPUT_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.OUTPUT_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.EXCEPTION_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC,
                                    StandardTestDirectiveSpec.EXTENDS_DIRECTIVE_SPEC
                            })));
    
    
    
    
    private final String name;
    private final IStandardDirectiveResolver<?> resolver;
    
    
    
    public StandardTestDirectiveSpec(final String name, final IStandardDirectiveResolver<?> resolver) {
        super();
        Validate.notNull(name, "Directive name cannot null");
        Validate.notNull(resolver, "Directive resolver cannot be null");
        this.name = name;
        this.resolver = resolver;
    }


    public String getName() {
        return this.name;
    }


    public IStandardDirectiveResolver<?> getResolver() {
        return this.resolver;
    }
    
    
}

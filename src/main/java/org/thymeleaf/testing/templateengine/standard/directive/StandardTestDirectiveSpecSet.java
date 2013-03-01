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
 *   Unless requiredDirectives by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.testing.templateengine.standard.directive;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;






public final class StandardTestDirectiveSpecSet {

    
    public static final StandardTestDirectiveSpecSet STANDARD_DIRECTIVES_SPEC_SET;
    
    
    
    private final Map<String,StandardTestDirectiveSpec<?>> requiredDirectives;
    private final Map<String,StandardTestDirectiveSpec<?>> optionalDirectives;
    private final Set<String> allDirectiveNames;
    

    
    
    
    static {

        final Map<String,StandardTestDirectiveSpec<?>> required = new HashMap<String,StandardTestDirectiveSpec<?>>();
        final Map<String,StandardTestDirectiveSpec<?>> optional = new HashMap<String,StandardTestDirectiveSpec<?>>();
        
        optional.put(StandardTestDirectiveSpec.TEST_NAME_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.TEST_NAME_DIRECTIVE_SPEC);
        required.put(StandardTestDirectiveSpec.TEMPLATE_MODE_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.TEMPLATE_MODE_DIRECTIVE_SPEC);
        required.put(StandardTestDirectiveSpec.CACHE_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.CACHE_DIRECTIVE_SPEC);
        required.put(StandardTestDirectiveSpec.CONTEXT_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.CONTEXT_DIRECTIVE_SPEC);
        required.put(StandardTestDirectiveSpec.INPUT_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.INPUT_DIRECTIVE_SPEC);
        optional.put(StandardTestDirectiveSpec.OUTPUT_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.OUTPUT_DIRECTIVE_SPEC);
        optional.put(StandardTestDirectiveSpec.EXCEPTION_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.EXCEPTION_DIRECTIVE_SPEC);
        optional.put(StandardTestDirectiveSpec.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC.getName(), StandardTestDirectiveSpec.EXCEPTION_MESSAGE_PATTERN_DIRECTIVE_SPEC);
        
        STANDARD_DIRECTIVES_SPEC_SET = new StandardTestDirectiveSpecSet(required, optional);
        
    }
    

    

    public StandardTestDirectiveSpecSet(
            final Map<String, StandardTestDirectiveSpec<?>> requiredDirectives,
            final Map<String, StandardTestDirectiveSpec<?>> optionalDirectives) {
        
        super();
        
        Validate.notNull(requiredDirectives, "Required directives map cannot be null");
        Validate.notNull(optionalDirectives, "Optional directives map cannot be null");
        
        this.requiredDirectives = Collections.unmodifiableMap(new HashMap<String, StandardTestDirectiveSpec<?>>(requiredDirectives));
        this.optionalDirectives = Collections.unmodifiableMap(new HashMap<String, StandardTestDirectiveSpec<?>>(optionalDirectives));
        
        final Set<String> allDirectiveNamesObj = new HashSet<String>();
        allDirectiveNamesObj.addAll(this.requiredDirectives.keySet());
        allDirectiveNamesObj.addAll(this.optionalDirectives.keySet());
        this.allDirectiveNames = Collections.unmodifiableSet(allDirectiveNamesObj);
        
    }



    public Map<String, StandardTestDirectiveSpec<?>> getRequiredDirectives() {
        return this.requiredDirectives;
    }

    public Map<String, StandardTestDirectiveSpec<?>> getOptionalDirectives() {
        return this.optionalDirectives;
    }
    
    
    public Set<String> getAllDirectiveNames() {
        return this.allDirectiveNames;
    }
    
    
    public boolean contains(final String directiveName) {
        return this.requiredDirectives.containsKey(directiveName) ||
               this.optionalDirectives.containsKey(directiveName);
    }

    public boolean isRequired(final String directiveName) {
        return this.requiredDirectives.containsKey(directiveName);
    }

    public boolean isOptional(final String directiveName) {
        return this.optionalDirectives.containsKey(directiveName);
    }

    
    public StandardTestDirectiveSpec<?> getDirective(final String directiveName) {
        if (isRequired(directiveName)) {
            return this.requiredDirectives.get(directiveName);
        }
        return this.optionalDirectives.get(directiveName);
    }
    
}

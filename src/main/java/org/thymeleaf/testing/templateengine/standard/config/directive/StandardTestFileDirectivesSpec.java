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
package org.thymeleaf.testing.templateengine.standard.config.directive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.util.Validate;






public final class StandardTestFileDirectivesSpec<T> {

    
    private final Map<String,StandardTestFileDirectiveSpec<?>> requiredDirectives;
    private final Map<String,StandardTestFileDirectiveSpec<?>> optionalDirectives;

    


    public StandardTestFileDirectivesSpec(
            final Map<String, StandardTestFileDirectiveSpec<?>> requiredDirectives,
            final Map<String, StandardTestFileDirectiveSpec<?>> optionalDirectives) {
        
        super();
        
        Validate.notNull(requiredDirectives, "Required directives map cannot be null");
        Validate.notNull(optionalDirectives, "Optional directives map cannot be null");
        
        this.requiredDirectives = Collections.unmodifiableMap(new HashMap<String, StandardTestFileDirectiveSpec<?>>(requiredDirectives));
        this.optionalDirectives = Collections.unmodifiableMap(new HashMap<String, StandardTestFileDirectiveSpec<?>>(optionalDirectives));
        
    }



    public Map<String, StandardTestFileDirectiveSpec<?>> getRequiredDirectives() {
        return this.requiredDirectives;
    }

    public Map<String, StandardTestFileDirectiveSpec<?>> getOptionalDirectives() {
        return this.optionalDirectives;
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

    
    public StandardTestFileDirectiveSpec<?> getDirective(final String directiveName) {
        if (isRequired(directiveName)) {
            return this.requiredDirectives.get(directiveName);
        }
        return this.optionalDirectives.get(directiveName);
    }
    
}

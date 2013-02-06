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
package org.thymeleaf.testing.templateengine.standard.util;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestFileDirectiveSpec;
import org.thymeleaf.testing.templateengine.standard.config.test.IStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestFileData;






public final class StandardTestFileResolutionUtils {

    public static final String CONFIG_RESOLVER_DIRECTIVE_SUFFIX = "_RESOLVER";
    

    
    public static Map<String,Object> resolveTestFile(final String executionId, final String fileName, 
            final StandardTestFileData data, final Map<String,StandardTestFileDirectiveSpec<?>> directiveSpecs) {

        final Map<String,String> directiveValues = data.getAllDirectiveValues();
        final Map<String,StandardTestFileDirectiveSpec<?>> resolvedDirectiveSpecs =
                resolveDirectiveSpecs(executionId, fileName, data, directiveSpecs);
        
        
        for (final Map.Entry<String,String> directiveValueEntry : directiveValues.entrySet()) {
            
            final String directiveName = directiveValueEntry.getKey();
            final String directiveValue = directiveValueEntry.getValue();
            
            if (!isResolverDirective(directiveName)) {
                
                final StandardTestFileDirectiveSpec<?> spec = resolvedDirectiveSpecs.get(directiveName);
                final IStandardDirectiveResolver<?> resolver = spec.getResolver();
                
                final Object value = resolver.getValue(suite, path, fileName, directiveName, data);
                
            }
            
            
        }
        
        return null;
        
    }
    
    
    
    private static Map<String,StandardTestFileDirectiveSpec<?>> resolveDirectiveSpecs(
            final String executionId, final String fileName, 
            final StandardTestFileData data, final Map<String,StandardTestFileDirectiveSpec<?>> directiveSpecs) {
        
        final Map<String,StandardTestFileDirectiveSpec<?>> resolvedDirectiveSpecs =
                new HashMap<String, StandardTestFileDirectiveSpec<?>>(directiveSpecs);
        
        final Map<String,String> directiveValues = data.getAllDirectiveValues();
        
        for (final Map.Entry<String,String> directiveValueEntry : directiveValues.entrySet()) {
            
            final String directiveName = directiveValueEntry.getKey();
            if (isResolverDirective(directiveName)) {
                // TODO implement this!
                // Obtain the resolver class with CLass.forName, then create instance, then replace in resolvedDirectiveSpecs map
                throw new RuntimeException("To be implemented!!");
            } else {
                if (!directiveSpecs.containsKey(directiveName)) {
                    throw new TestEngineExecutionException(
                            executionId, "No specification found for directive \"" + directiveName + "\" at file " +
                            "\"" + fileName + "\"");
                }
            }
            
        }

        return resolvedDirectiveSpecs;
        
        
    }
    
    
    
    private static boolean isResolverDirective(final String directiveName) {
        return directiveName != null && directiveName.endsWith(CONFIG_RESOLVER_DIRECTIVE_SUFFIX);
    }
    

    
    
    private StandardTestFileResolutionUtils() {
        super();
    }
    
    
}

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSetSpec;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpec;
import org.thymeleaf.testing.templateengine.standard.config.test.IStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestDocumentData;
import org.thymeleaf.util.ClassLoaderUtils;






public final class StandardTestDocumentResolutionUtils {

    

    
    public static Map<String,Map<String,Object>> resolveTestDocumentData(
            final String executionId, final StandardTestDocumentData data, 
            final StandardTestDirectiveSetSpec directiveSetSpec) {

        final Map<String,Map<String,Object>> values = new HashMap<String, Map<String,Object>>();

        final Map<String,StandardTestDirectiveSpec<?>> resolvedDirectiveSpecs =
                resolveAndValidateDirectiveSpecs(executionId, data, directiveSetSpec);        
        
        for (final Map.Entry<String,StandardTestDirectiveSpec<?>> directiveEntry : resolvedDirectiveSpecs.entrySet()) {

            final String directiveName = directiveEntry.getKey();
            final StandardTestDirectiveSpec<?> spec = directiveEntry.getValue();
            final IStandardDirectiveResolver<?> resolver = spec.getResolver();

            Map<String,Object> valuesByDirectiveForName = values.get(directiveName);
            if (valuesByDirectiveForName == null) {
                valuesByDirectiveForName = new HashMap<String, Object>();
                values.put(directiveName, valuesByDirectiveForName);
            }
            
            final Set<String> directiveQualifiers = 
                    new HashSet<String>(data.getAllDirectiveQualifiersForName(directiveName));
            if (!directiveQualifiers.contains(DirectiveUtils.MAIN_DIRECTIVE_QUALIFIER)) {
                // If the main qualifier has not been specified, we add it manually so that
                // we make sure all directives in the spec have at least one value (even if it is
                // the default one).
                directiveQualifiers.add(DirectiveUtils.MAIN_DIRECTIVE_QUALIFIER);
            }
            
            
            for (final String directiveQualifier : directiveQualifiers) {
                
                final Object value = 
                        resolver.getValue(executionId, data, directiveName, directiveQualifier);
                
                if (value == null) {
                    
                    if (directiveSetSpec.isRequired(directiveName)) {
                        // This directive is required, but we have resolved no value for it. 
                        throw new TestEngineExecutionException(
                                executionId, "No (or null) value resolved for required directive \"" + directiveName + "\" in document " +
                                "\"" + data.getDocumentName() + "\"");
                    }
                    
                } else {
                    
                    final Class<?> valueClass = spec.getValueClass();
                    if (!valueClass.isAssignableFrom(value.getClass())) {
                        // Value returned is not of the correct class
                        throw new TestEngineExecutionException(
                                executionId, "Value of class \"" + value.getClass().getName() + "\" resolved " +
                                "for directive \"" + directiveName + "\" in document " +
                                "\"" + data.getDocumentName() + "\", but value was expected to be of class " +
                                "\"" + valueClass.getName() + "\"");
                    }
                    
                }
                
                
                valuesByDirectiveForName.put(directiveQualifier, value);
            }

        }
        
        return values;
        
    }
    
    
    
    
    
    private static Map<String,StandardTestDirectiveSpec<?>> resolveAndValidateDirectiveSpecs(
            final String executionId, final StandardTestDocumentData data, 
            final StandardTestDirectiveSetSpec directiveSetSpec) {
        
        
        final Map<String,StandardTestDirectiveSpec<?>> resolvedDirectiveSpecs =
                new HashMap<String, StandardTestDirectiveSpec<?>>();
        
        /*
         * We add to the map all the specs defined at the spec set.
         */
        resolvedDirectiveSpecs.putAll(directiveSetSpec.getRequiredDirectives());
        resolvedDirectiveSpecs.putAll(directiveSetSpec.getOptionalDirectives());
        
        /*
         * The data coming from reading the file is processed in order to find
         * possible substitutes to already-configured resolvers.
         */
        final Set<String> directiveNames = data.getAllDirectiveNames();
        for (final String directiveName : directiveNames) {
            
            if (!directiveSetSpec.contains(directiveName)) {
                throw new TestEngineExecutionException(
                        executionId, "A directive called \"" + directiveName +"\" " +
                                    "has been found in document \"" + data.getDocumentName() + "\", " +
                                    "but test specification does not allow directive \"" + directiveName + "\"");
            }

            final String directiveResolverValue = 
                    data.getDirectiveValue(directiveName, DirectiveUtils.RESOLVER_DIRECTIVE_QUALIFIER);

            if (directiveResolverValue != null) {
                
                final StandardTestDirectiveSpec<?> newSpec =
                        initializeDirectiveResolver(executionId, data.getDocumentName(), directiveName, directiveResolverValue);
                resolvedDirectiveSpecs.put(directiveName, newSpec);
                
            }
            
        }
        
        return resolvedDirectiveSpecs;
        
        
    }
    
    

    
    
    @SuppressWarnings("unchecked")
    private static StandardTestDirectiveSpec<?> initializeDirectiveResolver(
            final String executionId, final String documentName, 
            final String directiveName, final String directiveValue){
        
        final String className = directiveValue.trim();
        try {
            
            final ClassLoader classLoader =
                    ClassLoaderUtils.getClassLoader(StandardTestDocumentResolutionUtils.class);
            final Class<?> resolverClass = classLoader.loadClass(className);
            
            if (!IStandardDirectiveResolver.class.isAssignableFrom(resolverClass)) {
                throw new TestEngineExecutionException(
                        executionId, "No specification found for directive \"" + directiveName + "\" at file " +
                        "\"" + documentName + "\" selects class \"" + className + "\" as a directive resolver " +
                        "implementation. But this class does not implement the " + 
                        IStandardDirectiveResolver.class.getName() + " interface");
            }
            
            final IStandardDirectiveResolver<?> directiveResolver = 
                    (IStandardDirectiveResolver<?>) resolverClass.newInstance();
            final Class<?> directiveValueClass = directiveResolver.getValueClass();
            
            final StandardTestDirectiveSpec<?> newSpec =
                    new StandardTestDirectiveSpec<Object>(
                            directiveName, (Class<Object>) directiveValueClass, directiveResolver);
            
            return newSpec;
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    executionId, "Error while initializing resolver for directive \"" + directiveName + "\" at file " +
                    "\"" + documentName + "\"", t);
        }
        
        
    }
    
    

    
    
    private StandardTestDocumentResolutionUtils() {
        super();
    }
    
    
}

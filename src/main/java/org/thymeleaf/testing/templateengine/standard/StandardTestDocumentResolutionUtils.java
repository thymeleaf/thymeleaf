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
package org.thymeleaf.testing.templateengine.standard;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSetSpec;
import org.thymeleaf.testing.templateengine.standard.config.directive.StandardTestDirectiveSpec;
import org.thymeleaf.testing.templateengine.standard.config.test.IStandardDirectiveResolver;
import org.thymeleaf.testing.templateengine.standard.config.test.StandardTestDocumentData;
import org.thymeleaf.util.ClassLoaderUtils;






public final class StandardTestDocumentResolutionUtils {

    public static final String CONFIG_RESOLVER_DIRECTIVE_SUFFIX = "_RESOLVER";
    

    
    public static Map<String,Object> resolveTestDocumentData(
            final String executionId, final StandardTestDocumentData data, 
            final StandardTestDirectiveSetSpec directiveSetSpec) {

        final Map<String,Object> values = new HashMap<String, Object>();

        final Map<String,StandardTestDirectiveSpec<?>> resolvedDirectiveSpecs =
                resolveAndValidateDirectiveSpecs(executionId, data, directiveSetSpec);        
        
        for (final Map.Entry<String,StandardTestDirectiveSpec<?>> directiveEntry : resolvedDirectiveSpecs.entrySet()) {

            final String directiveName = directiveEntry.getKey();
            final StandardTestDirectiveSpec<?> spec = directiveEntry.getValue();
                
            if (!isResolverDirective(directiveName)) {
                
                final IStandardDirectiveResolver<?> resolver = spec.getResolver();
                
                final Object value = resolver.getValue(executionId, data, directiveName);
                
                if (value == null) {
                    if (directiveSetSpec.isRequired(directiveName)) {
                        // This directive is required, but we have resolved no value for it. 
                        throw new TestEngineExecutionException(
                                executionId, "No (or null) value resolved for required directive \"" + directiveName + "\" in document " +
                                "\"" + data.getDocumentName() + "\" in document \"" + data.getDocumentName() + "\"");
                    }
                }
                
                values.put(directiveName, value);

            }

        }
        
        return values;
        
    }
    
    
    
    private static Map<String,StandardTestDirectiveSpec<?>> resolveAndValidateDirectiveSpecs(
            final String executionId, final StandardTestDocumentData data, 
            final StandardTestDirectiveSetSpec directiveSetSpec) {
        
        final Map<String,StandardTestDirectiveSpec<?>> resolvedDirectiveSpecs =
                new HashMap<String, StandardTestDirectiveSpec<?>>();
        resolvedDirectiveSpecs.putAll(directiveSetSpec.getRequiredDirectives());
        resolvedDirectiveSpecs.putAll(directiveSetSpec.getOptionalDirectives());
        
        final Map<String,String> directiveValues = data.getAllDirectiveValues();
        for (final Map.Entry<String,String> directiveValueEntry : directiveValues.entrySet()) {
            
            final String directiveName = directiveValueEntry.getKey();
            final String directiveValue = directiveValueEntry.getValue();
            
            if (isResolverDirective(directiveName)) {
                
                final String targetDirectiveSpec =
                        getDirectiveNameFromResolverDirectiveName(directiveName);
                
                if (!directiveSetSpec.contains(targetDirectiveSpec)) {
                    throw new TestEngineExecutionException(
                            executionId, "A resolver-configuration directive called \"" + directiveName +"\" " +
                    		            "has been found in document \"" + data.getDocumentName() + "\", " +
                                        "but test specification does not allow directive \"" + targetDirectiveSpec + "\"");
                }
                
                final StandardTestDirectiveSpec<?> newSpec =
                        initializeDirectiveResolver(executionId, data.getDocumentName(), directiveName, directiveValue);
                resolvedDirectiveSpecs.put(targetDirectiveSpec, newSpec);
                
            } else {
                
                if (!directiveSetSpec.contains(directiveName)) {
                    throw new TestEngineExecutionException(
                            executionId, "No specification found for directive \"" + directiveName + "\" in document " +
                            "\"" + data.getDocumentName() + "\"");
                }
                
            }
            
        }
        
        return resolvedDirectiveSpecs;
        
        
    }
    
    
    
    private static boolean isResolverDirective(final String directiveName) {
        return directiveName != null && directiveName.endsWith(CONFIG_RESOLVER_DIRECTIVE_SUFFIX);
    }
    
    private static String getDirectiveNameFromResolverDirectiveName(final String resolverDirectiveName) {
        return resolverDirectiveName.substring(0,resolverDirectiveName.indexOf(CONFIG_RESOLVER_DIRECTIVE_SUFFIX));
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
                            getDirectiveNameFromResolverDirectiveName(directiveName), 
                            (Class<Object>) directiveValueClass, 
                            directiveResolver);
            
            return newSpec;
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    executionId, "Error while processing directive \"" + directiveName + "\" at file " +
                    "\"" + documentName + "\"", t);
        }
        
        
    }
    
    

    
    
    private StandardTestDocumentResolutionUtils() {
        super();
    }
    
    
}

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.data.StandardTestDocumentData;
import org.thymeleaf.testing.templateengine.standard.directive.resolver.IStandardDirectiveResolver;
import org.thymeleaf.util.ClassLoaderUtils;






public final class StandardTestDirectiveUtils {

    private static final Pattern DIRECTIVE_PATTERN =
            Pattern.compile("(\\p{Alnum}*)(\\[(\\S*)\\])?");
    private static final int DIRECTIVE_NAME_GROUP = 1;
    private static final int DIRECTIVE_QUALIFIER_GROUP = 3;
    
    
    public static final String RESOLVER_DIRECTIVE_QUALIFIER = "resolver";
    public static final String MAIN_DIRECTIVE_QUALIFIER = null;

    
    

    public static boolean isDirective(final String name) {
        if (name == null) {
            return false;
        }
        final Matcher m = DIRECTIVE_PATTERN.matcher(name);
        return m.matches();
    }
    

    
    public static String extractDirectiveName(final String directive) {
        if (directive == null) {
            return null;
        }
        final Matcher m = DIRECTIVE_PATTERN.matcher(directive);
        if (!m.matches()) {
            return null;
        }
        final String name = m.group(DIRECTIVE_NAME_GROUP);
        if (name == null || name.trim().equals("")) {
            return null;
        }
        return name.trim();
    }
    

    
    public static String extractDirectiveQualifier(final String directive) {
        if (directive == null) {
            return null;
        }
        final Matcher m = DIRECTIVE_PATTERN.matcher(directive);
        if (!m.matches()) {
            return null;
        }
        final String qualifier = m.group(DIRECTIVE_QUALIFIER_GROUP);
        if (qualifier == null || qualifier.trim().equals("")) {
            return null;
        }
        return qualifier.trim();
    }
    

    
    public static boolean hasQualifier(final String directive) {
        return extractDirectiveQualifier(directive) != null;
    }
    
    
    
    public static boolean isResolverQualifier(final String directiveQualifier) {
        return directiveQualifier != null && directiveQualifier.equals(RESOLVER_DIRECTIVE_QUALIFIER);
    }
    
    
    public static String buildResolverDirective(final String directive) {
        if (directive == null) {
            return null;
        }
        if (hasQualifier(directive)) {
            final String directiveName = extractDirectiveName(directive);
            return directiveName + "[" + RESOLVER_DIRECTIVE_QUALIFIER + "]";
        }
        return directive + "[" + RESOLVER_DIRECTIVE_QUALIFIER + "]";
    }
    
    
    
    
    
    
    
    public static Map<String,Map<String,Object>> resolveDirectiveValues(
            final String executionId, final StandardTestDocumentData data, 
            final Set<StandardTestDirectiveSpec> directiveSpecSet) {

        final Map<String,Map<String,Object>> values = new HashMap<String, Map<String,Object>>();

        final Map<String,StandardTestDirectiveSpec> resolvedDirectiveSpecs =
                resolveAndValidateDirectiveSpecs(executionId, data, directiveSpecSet);        
        
        for (final Map.Entry<String,StandardTestDirectiveSpec> directiveEntry : resolvedDirectiveSpecs.entrySet()) {

            final String directiveName = directiveEntry.getKey();
            final StandardTestDirectiveSpec spec = directiveEntry.getValue();
            final IStandardDirectiveResolver<?> resolver = spec.getResolver();

            Map<String,Object> valuesByDirectiveForName = values.get(directiveName);
            if (valuesByDirectiveForName == null) {
                valuesByDirectiveForName = new HashMap<String, Object>();
                values.put(directiveName, valuesByDirectiveForName);
            }
            
            final Set<String> directiveQualifiers = 
                    new HashSet<String>(data.getAllDirectiveQualifiersForName(directiveName));
            if (!directiveQualifiers.contains(StandardTestDirectiveUtils.MAIN_DIRECTIVE_QUALIFIER)) {
                // If the main qualifier has not been specified, we add it manually so that
                // we make sure all directives in the spec have at least one value (even if it is
                // the default one).
                directiveQualifiers.add(StandardTestDirectiveUtils.MAIN_DIRECTIVE_QUALIFIER);
            }
            
            
            for (final String directiveQualifier : directiveQualifiers) {
                
                final Object value = 
                        resolver.getValue(executionId, data, directiveName, directiveQualifier);
                
                if (value != null) {
                    
                    final Class<?> valueClass = spec.getResolver().getValueClass();
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
    
    
    
    
    
    private static Map<String,StandardTestDirectiveSpec> resolveAndValidateDirectiveSpecs(
            final String executionId, final StandardTestDocumentData data, 
            final Set<StandardTestDirectiveSpec> directiveSpecSet) {
        
        
        final Map<String,StandardTestDirectiveSpec> resolvedDirectiveSpecs =
                new HashMap<String, StandardTestDirectiveSpec>();
        
        /*
         * We add to the map all the specs defined at the spec set.
         */
        for (final StandardTestDirectiveSpec spec : directiveSpecSet) {
            resolvedDirectiveSpecs.put(spec.getName(), spec);
        }
        
        /*
         * The data coming from reading the file is processed in order to find
         * possible substitutes to already-configured resolvers.
         */
        final Set<String> directiveNames = data.getAllDirectiveNames();
        for (final String directiveName : directiveNames) {
            
            if (!resolvedDirectiveSpecs.containsKey(directiveName)) {
                throw new TestEngineExecutionException(
                        executionId, "A directive called \"" + directiveName +"\" " +
                                    "has been found in document \"" + data.getDocumentName() + "\", " +
                                    "but test specification does not allow directive \"" + directiveName + "\"");
            }

            final String directiveResolverValue = 
                    data.getDirectiveValue(directiveName, StandardTestDirectiveUtils.RESOLVER_DIRECTIVE_QUALIFIER);

            if (directiveResolverValue != null) {
                
                final StandardTestDirectiveSpec newSpec =
                        initializeDirectiveResolver(executionId, data.getDocumentName(), directiveName, directiveResolverValue);
                resolvedDirectiveSpecs.put(directiveName, newSpec);
                
            }
            
        }
        
        return resolvedDirectiveSpecs;
        
        
    }
    
    

    
    
    private static StandardTestDirectiveSpec initializeDirectiveResolver(
            final String executionId, final String documentName, 
            final String directiveName, final String directiveValue){
        
        final String className = directiveValue.trim();
        try {
            
            final ClassLoader classLoader =
                    ClassLoaderUtils.getClassLoader(StandardTestDirectiveUtils.class);
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
            
            final StandardTestDirectiveSpec newSpec =
                    new StandardTestDirectiveSpec(directiveName, directiveResolver);
            
            return newSpec;
            
        } catch (final TestEngineExecutionException e) {
            throw e;
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    executionId, "Error while initializing resolver for directive \"" + directiveName + "\" at file " +
                    "\"" + documentName + "\"", t);
        }
        
        
    }
    
    
    
    
    
    
    
    private StandardTestDirectiveUtils() {
        super();
    }
    
    
}

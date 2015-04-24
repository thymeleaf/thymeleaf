/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
final class ExpressionCache {

    private static final String EXPRESSION_CACHE_PREFIX = "{expression}";
    private static final String ASSIGNATION_SEQUENCE_CACHE_PREFIX = "{assignation_sequence}";
    private static final String EXPRESSION_SEQUENCE_CACHE_PREFIX = "{expression_sequence}";
    private static final String EACH_CACHE_PREFIX = "{each}";
    private static final String FRAGMENT_SELECTION_CACHE_PREFIX = "{fragment_selection}";
    private static final String FRAGMENT_SIGNATURE_CACHE_PREFIX = "{fragment_signature}";

    
    
    private ExpressionCache() {
        super();
    }
    

    
    private static Object getFromCache(final IEngineConfiguration configuration, final String input, final String prefix) {
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            final ICache<String,Object> cache = cacheManager.getExpressionCache();
            if (cache != null) {
                return cache.get(prefix + input);
            }
        }
        return null;
    }

    
    private static <V> void putIntoCache(final IEngineConfiguration configuration, final String input, final V value, final String prefix) {
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            final ICache<String,Object> cache = cacheManager.getExpressionCache();
            if (cache != null) {
                cache.put(prefix + input, value);
            }
        }
    }
    
    
    
    
    
    static IStandardExpression getExpressionFromCache(final IEngineConfiguration configuration, final String input) {
        return (IStandardExpression) getFromCache(configuration, input, EXPRESSION_CACHE_PREFIX);
    }

    static void putExpressionIntoCache(final IEngineConfiguration configuration, final String input, final IStandardExpression value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_PREFIX);
    }



    static AssignationSequence getAssignationSequenceFromCache(final IEngineConfiguration configuration, final String input) {
        return (AssignationSequence) getFromCache(configuration, input, ASSIGNATION_SEQUENCE_CACHE_PREFIX);
    }

    static void putAssignationSequenceIntoCache(final IEngineConfiguration configuration, final String input, final AssignationSequence value) {
        putIntoCache(configuration, input, value, ASSIGNATION_SEQUENCE_CACHE_PREFIX);
    }



    static ExpressionSequence getExpressionSequenceFromCache(final IEngineConfiguration configuration, final String input) {
        return (ExpressionSequence) getFromCache(configuration, input, EXPRESSION_SEQUENCE_CACHE_PREFIX);
    }

    static void putExpressionSequenceIntoCache(final IEngineConfiguration configuration, final String input, final ExpressionSequence value) {
        putIntoCache(configuration, input, value, EXPRESSION_SEQUENCE_CACHE_PREFIX);
    }



    static Each getEachFromCache(final IEngineConfiguration configuration, final String input) {
        return (Each) getFromCache(configuration, input, EACH_CACHE_PREFIX);
    }

    static void putEachIntoCache(final IEngineConfiguration configuration, final String input, final Each value) {
        putIntoCache(configuration, input, value, EACH_CACHE_PREFIX);
    }



    static FragmentSelection getFragmentSelectionFromCache(final IEngineConfiguration configuration, final String input) {
        return (FragmentSelection) getFromCache(configuration, input, FRAGMENT_SELECTION_CACHE_PREFIX);
    }

    static void putFragmentSelectionIntoCache(final IEngineConfiguration configuration, final String input, final FragmentSelection value) {
        putIntoCache(configuration, input, value, FRAGMENT_SELECTION_CACHE_PREFIX);
    }



    static FragmentSignature getFragmentSignatureFromCache(final IEngineConfiguration configuration, final String input) {
        return (FragmentSignature) getFromCache(configuration, input, FRAGMENT_SIGNATURE_CACHE_PREFIX);
    }

    static void putFragmentSignatureIntoCache(final IEngineConfiguration configuration, final String input, final FragmentSignature value) {
        putIntoCache(configuration, input, value, FRAGMENT_SIGNATURE_CACHE_PREFIX);
    }

}

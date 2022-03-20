/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.cache.ExpressionCacheKey;
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

    private static final String EXPRESSION_CACHE_TYPE_STANDARD_EXPRESSION = "expr";
    private static final String EXPRESSION_CACHE_TYPE_ASSIGNATION_SEQUENCE = "aseq";
    private static final String EXPRESSION_CACHE_TYPE_EXPRESSION_SEQUENCE = "eseq";
    private static final String EXPRESSION_CACHE_TYPE_EACH = "each";
    private static final String EXPRESSION_CACHE_TYPE_FRAGMENT_SIGNATURE = "fsig";

    
    
    private ExpressionCache() {
        super();
    }
    

    
    static Object getFromCache(final IEngineConfiguration configuration, final String input, final String type) {
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            final ICache<ExpressionCacheKey,Object> cache = cacheManager.getExpressionCache();
            if (cache != null) {
                return cache.get(new ExpressionCacheKey(type,input));
            }
        }
        return null;
    }

    
    static <V> void putIntoCache(final IEngineConfiguration configuration, final String input, final V value, final String type) {
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            final ICache<ExpressionCacheKey,Object> cache = cacheManager.getExpressionCache();
            if (cache != null) {
                cache.put(new ExpressionCacheKey(type,input), value);
            }
        }
    }


    static <V> void removeFromCache(final IEngineConfiguration configuration, final String input, final String type) {
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            final ICache<ExpressionCacheKey,Object> cache = cacheManager.getExpressionCache();
            if (cache != null) {
                cache.clearKey(new ExpressionCacheKey(type,input));
            }
        }
    }

    
    
    
    
    static IStandardExpression getExpressionFromCache(final IEngineConfiguration configuration, final String input) {
        return (IStandardExpression) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_STANDARD_EXPRESSION);
    }

    static void putExpressionIntoCache(final IEngineConfiguration configuration, final String input, final IStandardExpression value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_STANDARD_EXPRESSION);
    }



    static AssignationSequence getAssignationSequenceFromCache(final IEngineConfiguration configuration, final String input) {
        return (AssignationSequence) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_ASSIGNATION_SEQUENCE);
    }

    static void putAssignationSequenceIntoCache(final IEngineConfiguration configuration, final String input, final AssignationSequence value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_ASSIGNATION_SEQUENCE);
    }



    static ExpressionSequence getExpressionSequenceFromCache(final IEngineConfiguration configuration, final String input) {
        return (ExpressionSequence) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_EXPRESSION_SEQUENCE);
    }

    static void putExpressionSequenceIntoCache(final IEngineConfiguration configuration, final String input, final ExpressionSequence value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_EXPRESSION_SEQUENCE);
    }



    static Each getEachFromCache(final IEngineConfiguration configuration, final String input) {
        return (Each) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_EACH);
    }

    static void putEachIntoCache(final IEngineConfiguration configuration, final String input, final Each value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_EACH);
    }



    static FragmentSignature getFragmentSignatureFromCache(final IEngineConfiguration configuration, final String input) {
        return (FragmentSignature) getFromCache(configuration, input, EXPRESSION_CACHE_TYPE_FRAGMENT_SIGNATURE);
    }

    static void putFragmentSignatureIntoCache(final IEngineConfiguration configuration, final String input, final FragmentSignature value) {
        putIntoCache(configuration, input, value, EXPRESSION_CACHE_TYPE_FRAGMENT_SIGNATURE);
    }

}

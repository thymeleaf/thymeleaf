/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.util.CacheMap;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
final class ExpressionCache {
    
    
    private static final int EXPRESSION_CACHE_SIZE = 500;
    private static final int ASSIGNATION_SEQUENCE_CACHE_SIZE = 100;
    private static final int EXPRESSION_SEQUENCE_CACHE_SIZE = 100;
    private static final int EACH_CACHE_SIZE = 100;
    private static final int FRAGMENT_SELECTION_CACHE_SIZE = 200;

    
    private final CacheMap<String, Expression> expressionCache = 
            new CacheMap<String, Expression>("ExpressionCache.expressionCache", true, 100, EXPRESSION_CACHE_SIZE, false, null);

    private final CacheMap<String, AssignationSequence> assignationSequenceCache = 
            new CacheMap<String, AssignationSequence>("ExpressionCache.assignationSequenceCache", true, 100, ASSIGNATION_SEQUENCE_CACHE_SIZE, false, null);

    private final CacheMap<String, ExpressionSequence> expressionSequenceCache = 
            new CacheMap<String, ExpressionSequence>("ExpressionCache.expressionSequenceCache", true, 100, EXPRESSION_SEQUENCE_CACHE_SIZE, false, null);

    private final CacheMap<String, Each> eachCache = 
            new CacheMap<String, Each>("ExpressionCache.eachCache", true, 100, EACH_CACHE_SIZE, false, null);

    private final CacheMap<String, FragmentSelection> fragmentSelectionCache = 
            new CacheMap<String, FragmentSelection>("ExpressionCache.fragmentSelectionCache", true, 100, FRAGMENT_SELECTION_CACHE_SIZE, false, null);


    
    
    ExpressionCache() {
        super();
    }
    
    
    
    
    Expression getExpressionFromCache(final String input) {
        return this.expressionCache.get(input);
    }
    
    void putExpressionIntoCache(final String input, final Expression value) {
        this.expressionCache.put(input, value);
    }

    AssignationSequence getAssignationSequenceFromCache(final String input) {
        return this.assignationSequenceCache.get(input);
    }
    
    void putAssignationSequenceIntoCache(final String input, final AssignationSequence value) {
        this.assignationSequenceCache.put(input, value);
    }
    
    
    
    ExpressionSequence getExpressionSequenceFromCache(final String input) {
        return this.expressionSequenceCache.get(input);
    }
    
    void putExpressionSequenceIntoCache(final String input, final ExpressionSequence value) {
        this.expressionSequenceCache.put(input, value);
    }
    

    Each getEachFromCache(final String input) {
        return this.eachCache.get(input);
    }
    
    void putEachIntoCache(final String input, final Each value) {
        this.eachCache.put(input, value);
    }
    

    FragmentSelection getFragmentSelectionFromCache(final String input) {
        return this.fragmentSelectionCache.get(input);
    }
    
    void putFragmentSelectionIntoCache(final String input, final FragmentSelection value) {
        this.fragmentSelectionCache.put(input, value);
    }

}

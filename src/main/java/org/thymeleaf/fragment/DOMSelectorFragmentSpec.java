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
package org.thymeleaf.fragment;

import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.dom.Node;
import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class DOMSelectorFragmentSpec implements IFragmentSpec {

    
    private static final String DOM_SELECTOR_EXPRESSION_PREFIX = "{dom_selector}";
    
    private final String selectorExpression;

    
    
    public DOMSelectorFragmentSpec(final String selectorExpression) {
        super();
        Validate.notEmpty(selectorExpression, "DOM selector expression cannot be null or empty");
        this.selectorExpression = selectorExpression;
    }
    
    
    
    public String getSelectorExpression() {
        return this.selectorExpression;
    }
    

    

    public final List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {

        DOMSelector selector = null;
        ICache<String,Object> expressionCache = null;
        
        final ICacheManager cacheManager = configuration.getCacheManager();
        if (cacheManager != null) {
            expressionCache = cacheManager.getExpressionCache();
            if (expressionCache != null) {
                selector = 
                        (DOMSelector) expressionCache.get(DOM_SELECTOR_EXPRESSION_PREFIX + this.selectorExpression);
            }
        }
        
        if (selector == null) {
            selector = new DOMSelector(this.selectorExpression);
            if (expressionCache != null) {
                expressionCache.put(DOM_SELECTOR_EXPRESSION_PREFIX + this.selectorExpression, selector);
            }
        }
        
        final List<Node> selectedNodes = selector.select(nodes);
        if (selectedNodes == null || selectedNodes.size() == 0) {
            return null;
        }
            
        return selectedNodes;
        
    }

    
    

    
    
    @Override
    public String toString() {
        return "(DOMSELECTOR: " + this.selectorExpression +")";
    }
    
    
}


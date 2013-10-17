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
 * <p>
 *  Implementation of the {@link IFragmentSpec} interface that extracts fragments
 *  of DOM trees using a {@link DOMSelector} object.
 * </p>
 * <p>
 *  The DOM selector instances used by these fragment specs are stored at the
 *  <i>expression cache</i> (see {@link ICacheManager#getExpressionCache()}) using
 *  as key {@link #DOM_SELECTOR_EXPRESSION_PREFIX} + <tt>selectorExpression</tt>.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class DOMSelectorFragmentSpec implements IFragmentSpec {

    /**
     * <p>
     *   Prefix to be used for keys when storing selector expressions at the
     *   <i>expression cache</i>.
     * </p>
     */
    public static final String DOM_SELECTOR_EXPRESSION_PREFIX = "{dom_selector}";
    
    private final String selectorExpression;
    private final DOMSelector.INodeReferenceChecker referenceChecker;



    /**
     * <p>
     *   Creates a new instance, specifying the expression to be used for a
     *   {@link DOMSelector} object to be created internally.
     * </p>
     *
     * @param selectorExpression the expression to be used for the DOM selector.
     */
    public DOMSelectorFragmentSpec(final String selectorExpression) {
        this(selectorExpression, null);
    }


    /**
     * <p>
     *   Creates a new instance, specifying the expression to be used for a
     *   {@link DOMSelector} object to be created internally and also a flag indicating
     *   whether the selected element itself (or selected elements if more than
     *   one) must be returned or only its/their children.
     * </p>
     * <p>
     *   This constructor allows the specification of an {@link org.thymeleaf.dom.DOMSelector.INodeReferenceChecker}
     *   that will be applied during the executing of the contained {@link DOMSelector} object.
     * </p>
     *
     * @param selectorExpression the expression to be used for the DOM selector.
     * @param referenceChecker the reference checker to be used. Might be null.
     *
     * @since 2.1.0
     */
    public DOMSelectorFragmentSpec(
            final String selectorExpression, final DOMSelector.INodeReferenceChecker referenceChecker) {

        super();

        Validate.notEmpty(selectorExpression, "DOM selector expression cannot be null or empty");

        this.selectorExpression = selectorExpression;
        this.referenceChecker = referenceChecker;

    }

    


    public String getSelectorExpression() {
        return this.selectorExpression;
    }


    /**
     * <p>
     *   Returns the reference checker (implementation of {@link org.thymeleaf.dom.DOMSelector.INodeReferenceChecker}
     *   being used for executing the contained DOM Selector. Might be null if no reference checker is to be used.
     * </p>
     *
     * @return the reference checker to be used, or null if none.
     *
     * @since 2.1.0
     */
    public DOMSelector.INodeReferenceChecker getReferenceChecker() {
        return this.referenceChecker;
    }




    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {

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
        
        final List<Node> extraction = selector.select(nodes, this.referenceChecker);
        if (extraction == null || extraction.size() == 0) {
            return null;
        }
            
        
        return extraction;

    }



    
    
    @Override
    public String toString() {
        return "(DOMSELECTOR: " + this.selectorExpression +")";
    }
    
    
}


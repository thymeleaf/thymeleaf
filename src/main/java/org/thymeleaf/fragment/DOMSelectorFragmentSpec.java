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

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
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
    private final boolean returnOnlyChildren;

    

    /**
     * <p>
     *   Creates a new instance, specifying the expression to be used for a
     *   {@link DOMSelector} object to be created internally.
     * </p>
     * 
     * @param selectorExpression the expression to be used for the DOM selector.
     */
    public DOMSelectorFragmentSpec(final String selectorExpression) {
        this(selectorExpression, false);
    }
    

    /**
     * <p>
     *   Creates a new instance, specifying the expression to be used for a
     *   {@link DOMSelector} object to be created internally and also a flag indicating
     *   whether the selected element itself (or selected elements if more than
     *   one) must be returned or only its/their children.
     * </p>
     * <p>
     *   If <tt>returnOnlyChildren</tt> is true, the element with the specified name 
     *   and/or containing the specified attribute will be discarded, and only its/their
     *   children will be returned.
     * </p>
     * 
     * @param selectorExpression the expression to be used for the DOM selector.
     * @param returnOnlyChildren whether the selected elements should be returned (false),
     *        or only their children (true).
     * @since 2.0.12
     */
    public DOMSelectorFragmentSpec(final String selectorExpression, final boolean returnOnlyChildren) {
        super();
        Validate.notEmpty(selectorExpression, "DOM selector expression cannot be null or empty");
        this.selectorExpression = selectorExpression;
        this.returnOnlyChildren = returnOnlyChildren;
    }
    
    
    
    public String getSelectorExpression() {
        return this.selectorExpression;
    }
    
    
    /**
     * <p>
     *   Returns whether this spec should only return the children of the selected nodes
     *   (<tt>true</tt>) or the selected nodes themselves (<tt>false</tt>, default).
     * </p>
     * 
     * @return whether this spec should only return the children of the selected nodes
     *         or not (default: false).
     * @since 2.0.12
     */
    public boolean isReturnOnlyChildren() {
        return this.returnOnlyChildren;
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
        
        final List<Node> extraction = selector.select(nodes);
        if (extraction == null || extraction.size() == 0) {
            return null;
        }
            
        
        if (!this.returnOnlyChildren) {
            return extraction;
        }
        
        final List<Node> extractionChildren = new ArrayList<Node>(); 
        for (final Node extractionNode : extraction) {
            
            if (extractionNode == null) {
                continue;
            }
            
            if (!(extractionNode instanceof NestableNode)) {
                throw new TemplateProcessingException(
                        "Cannot correctly retrieve children of node selected by fragment spec " +
                        "with DOM selector \"" + this.selectorExpression + "\". Node is not a " +
                		"nestable node (" + extractionNode.getClass().getSimpleName() + ").");
            }
            
            extractionChildren.addAll(((NestableNode)extractionNode).getChildren());
            
        }
        
        return extractionChildren;
        
    }

    
    

    
    
    @Override
    public String toString() {
        return "(DOMSELECTOR: " + this.selectorExpression +")";
    }
    
    
}


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
package org.thymeleaf.standard.fragment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.thymeleaf.Configuration;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardFragmentAttrProcessor;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *  Implementation of the {@link org.thymeleaf.fragment.IFragmentSpec} interface that extracts fragments
 *  of DOM trees using a {@link org.thymeleaf.dom.DOMSelector} object and applying a
 *  {@link StandardFragmentSignatureNodeReferenceChecker} reference checker for looking for
 *  <tt>th:fragment</tt>-based references.
 * </p>
 * <p>
 *  The DOM selector instances used by these fragment specs are stored at the
 *  <i>expression cache</i> (see {@link org.thymeleaf.cache.ICacheManager#getExpressionCache()}) using
 *  as key {@link org.thymeleaf.fragment.DOMSelectorFragmentSpec#DOM_SELECTOR_EXPRESSION_PREFIX} + <tt>selectorExpression</tt>.
 * </p>
 * <p>
 *   Objects of this class are <b>thread-safe</b>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class StandardDOMSelectorFragmentSpec implements IFragmentSpec {

    private static final ConcurrentHashMap<Configuration,DOMSelector.INodeReferenceChecker> REFERENCE_CHECKERS_BY_CONFIGURATION =
            new ConcurrentHashMap<Configuration, DOMSelector.INodeReferenceChecker>(3);

    private final String selectorExpression;
    private final String domSelectorCacheKey;



    /**
     * <p>
     *   Creates a new instance, specifying the expression to be used for a
     *   {@link org.thymeleaf.dom.DOMSelector} object to be created internally.
     * </p>
     *
     * @param selectorExpression the expression to be used for the DOM selector.
     *
     * @since 2.1.0
     */
    public StandardDOMSelectorFragmentSpec(final String selectorExpression) {

        super();

        Validate.notEmpty(selectorExpression, "DOM selector expression cannot be null or empty");

        this.selectorExpression = selectorExpression;
        this.domSelectorCacheKey = generateDOMSelectorCacheKey(this.selectorExpression);

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
                selector = (DOMSelector) expressionCache.get(this.domSelectorCacheKey);
            }
        }
        
        if (selector == null) {
            selector = new DOMSelector(this.selectorExpression);
            if (expressionCache != null) {
                expressionCache.put(this.domSelectorCacheKey, selector);
            }
        }

        final DOMSelector.INodeReferenceChecker referenceChecker = getReferenceChecker(configuration);

        final List<Node> extraction = selector.select(nodes, referenceChecker);
        if (extraction == null || extraction.size() == 0) {
            return null;
        }
            
        
        return extraction;

    }





    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("(STANDARDDOMSELECTOR: ");
        strBuilder.append(this.selectorExpression);
        strBuilder.append(")");
        return strBuilder.toString();
    }



    private static String generateDOMSelectorCacheKey(final String selectorExpression) {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(DOMSelectorFragmentSpec.DOM_SELECTOR_EXPRESSION_PREFIX);
        strBuilder.append(selectorExpression);
        return strBuilder.toString();
    }



    private static DOMSelector.INodeReferenceChecker getReferenceChecker(final Configuration configuration) {

        final DOMSelector.INodeReferenceChecker referenceChecker =
                REFERENCE_CHECKERS_BY_CONFIGURATION.get(configuration);
        if (referenceChecker != null) {
            return referenceChecker;
        }

        final String dialectPrefix = getStandardDialectPrefix(configuration);

        final StandardFragmentSignatureNodeReferenceChecker newReferenceChecker =
                new StandardFragmentSignatureNodeReferenceChecker(
                        configuration, dialectPrefix, StandardFragmentAttrProcessor.ATTR_NAME);

        REFERENCE_CHECKERS_BY_CONFIGURATION.put(configuration, newReferenceChecker);

        return newReferenceChecker;

    }



    private static String getStandardDialectPrefix(final Configuration configuration) {

        for (final Map.Entry<String,IDialect> dialectByPrefix : configuration.getDialects().entrySet()) {
            final IDialect dialect = dialectByPrefix.getValue();
            if (StandardDialect.class.isAssignableFrom(dialect.getClass())) {
                return dialectByPrefix.getKey();
            }
        }

        throw new ConfigurationException(
                "A Thymeleaf Standard Dialect has not been found in the current configuration, but it is " +
                "required in order to use " + StandardDOMSelectorFragmentSpec.class.getName());

    }


}


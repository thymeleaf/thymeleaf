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

import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
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
public final class DOMSelectorFragmentSpec extends AbstractFragmentSpec {

    
    private static final String DOM_SELECTOR_EXPRESSION_PREFIX = "{dom_selector}";
    
    private final String selectorExpression;

    
    
    public DOMSelectorFragmentSpec(final String stringRepresentation, 
            final String templateName, final String selectorExpression) {
        super(stringRepresentation, templateName);
        Validate.notEmpty(selectorExpression, "DOM selector expression cannot be null or empty");
        this.selectorExpression = selectorExpression;
    }
    
    
    
    public String getSelectorExpression() {
        return this.selectorExpression;
    }
    

    

    public final Node extractFragment(final Arguments arguments) {

        final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(
                        arguments.getConfiguration(), getTemplateName(), arguments.getContext());
        
        final Template parsedFragmentTemplate = 
                arguments.getTemplateRepository().getTemplate(fragmentTemplateProcessingParameters);

        DOMSelector selector = null;
        ICache<String,Object> expressionCache = null;
        
        final ICacheManager cacheManager = arguments.getConfiguration().getCacheManager();
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
        
        final List<Node> selectedNodes = selector.select(parsedFragmentTemplate.getDocument().getChildren());
        if (selectedNodes == null || selectedNodes.size() == 0) {
            return null;
        }
            
        return selectedNodes.get(0);
        
    }

    
    
}


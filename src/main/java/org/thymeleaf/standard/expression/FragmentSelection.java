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
package org.thymeleaf.standard.expression;

import java.io.Serializable;

import org.thymeleaf.util.Validate;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class FragmentSelection implements Serializable {

    
    private static final long serialVersionUID = -5310313871594922690L;
    
    
    private static final String FRAGMENT_SEPARATOR = "::";
    private static final char FRAGMENT_SELECTOR_XPATH_START = '[';
    private static final char FRAGMENT_SELECTOR_XPATH_END = ']';

    
    private final Expression templateName;
    private final Expression fragmentSelector;
    private final boolean isXPath;
    
    
    
    private FragmentSelection(final Expression templateName) {
        super();
        Validate.notNull(templateName, "Template name cannot be null");
        this.templateName = templateName;
        this.fragmentSelector = null;
        this.isXPath = false;
    }
    
    
    private FragmentSelection(final Expression templateName, final Expression fragmentSelector,
            final boolean isXPath) {
        super();
        Validate.notNull(templateName, "Template name cannot be null");
        Validate.notNull(fragmentSelector, "Fragment selector cannot be null");
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.isXPath = isXPath;
    }

    
    
    public Expression getTemplateName() {
        return this.templateName;
    }

    public Expression getFragmentSelector() {
        return this.fragmentSelector;
    }
    
    public boolean hasFragmentSelector() {
        return this.fragmentSelector != null;
    }
    
    public boolean isXPath() {
        return this.isXPath;
    }

    
    public String getStringRepresentation() {
        if (this.fragmentSelector == null) {
            return this.templateName.getStringRepresentation();
        }
        if (this.isXPath) {
            return this.templateName.getStringRepresentation() + " " + 
                FRAGMENT_SEPARATOR + " " +
                String.valueOf(FRAGMENT_SELECTOR_XPATH_START) + 
                this.fragmentSelector.getStringRepresentation() + 
                String.valueOf(FRAGMENT_SELECTOR_XPATH_END);
        }
        return this.templateName.getStringRepresentation() + " " + 
                FRAGMENT_SEPARATOR + " " +
                this.fragmentSelector.getStringRepresentation();
    }
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
    
    
    static FragmentSelection parse(final String input) {
        
        if (input == null || input.trim().equals("")) {
            return null;
        }
        
        final String trimmedInput = input.trim();
        
        final int separatorPos = trimmedInput.lastIndexOf(FRAGMENT_SEPARATOR);
        if (separatorPos == -1) {
            final Expression templateNameExpr = getExpressionDefaultToLiteral(trimmedInput);
            if (templateNameExpr == null) {
                return null;
            }
            final FragmentSelection fragmentSelection = new FragmentSelection(templateNameExpr);
            return fragmentSelection;
        }
        
        final String templateName = trimmedInput.substring(0,separatorPos).trim();
        String fragmentSelector = trimmedInput.substring(separatorPos + 2).trim();
        
        if (fragmentSelector.equals("") || templateName.equals("")) {
            return null;
        }
        
        boolean xpath = false;
        
        if (fragmentSelector.length() > 2 && 
            fragmentSelector.charAt(0) == FRAGMENT_SELECTOR_XPATH_START &&
            fragmentSelector.charAt(fragmentSelector.length() - 1) == FRAGMENT_SELECTOR_XPATH_END) {

            fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1).trim();
            xpath = true;
            
        }
        
        final Expression templateNameExpr = getExpressionDefaultToLiteral(templateName);
        final Expression fragmentSelectorExpr = getExpressionDefaultToLiteral(fragmentSelector);
        if (templateNameExpr == null || fragmentSelectorExpr == null) {
            return null;
        }
        
        final FragmentSelection fragmentSelection = new FragmentSelection(templateNameExpr, fragmentSelectorExpr, xpath);

        return fragmentSelection;
        
    }


    
    
    private static Expression getExpressionDefaultToLiteral(final String valueStr) {
        final Expression expr = Expression.parse(valueStr);
        if (expr == null) {
            return TextLiteralExpression.parseTextLiteral(valueStr);
        }
        return expr;
    }
    
    
}

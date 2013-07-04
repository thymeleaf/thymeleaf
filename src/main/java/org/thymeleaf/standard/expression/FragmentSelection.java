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

import org.thymeleaf.util.StringUtils;
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
    
    private static final String CURRENT_TEMPLATE_NAME = "this";
    private static final String FRAGMENT_SEPARATOR = "::";
    private static final char FRAGMENT_SELECTOR_XPATH_START = '[';
    private static final char FRAGMENT_SELECTOR_XPATH_END = ']';
    private static final char FRAGMENT_SELECTOR_PARAMETERS_START = '(';
    private static final char FRAGMENT_SELECTOR_PARAMETERS_END = ')';

    private static final String UNNAMED_PARAMETERS_PREFIX = "_arg";

    
    private final Expression templateName;
    private final Expression fragmentSelector;
    private final AssignationSequence parameters;
    private final boolean isXPath;
    
    
    
    private FragmentSelection(final Expression templateName, final Expression fragmentSelector,
            final AssignationSequence parameters, final boolean isXPath) {
        super();
        // templateName can be null if fragment is to be executed on the current template
        Validate.notNull(fragmentSelector, "Fragment selector cannot be null");
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.parameters = parameters;
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

    public AssignationSequence getParameters() {
        return this.parameters;
    }

    public boolean hasParameters() {
        return this.parameters != null && this.parameters.size() > 0;
    }
    
    public boolean isXPath() {
        return this.isXPath;
    }

    
    public String getStringRepresentation() {

        final String templateNameStringRepresentation =
                (this.templateName != null? this.templateName.getStringRepresentation() : "");

        if (this.fragmentSelector == null) {
            return templateNameStringRepresentation;
        }
        if (this.isXPath) {
            return templateNameStringRepresentation + " " +
                FRAGMENT_SEPARATOR + " " +
                String.valueOf(FRAGMENT_SELECTOR_XPATH_START) + 
                this.fragmentSelector.getStringRepresentation() + 
                String.valueOf(FRAGMENT_SELECTOR_XPATH_END);
        }
        return templateNameStringRepresentation + " " +
                FRAGMENT_SEPARATOR + " " +
                this.fragmentSelector.getStringRepresentation();
    }
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }
    
    
    
    
    static FragmentSelection parse(final String input) {
        
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        
        final String trimmedInput = input.trim();
        
        final int separatorPos = trimmedInput.lastIndexOf(FRAGMENT_SEPARATOR);
        if (separatorPos == -1) {
            final Expression templateNameExpr = getExpressionDefaultToLiteral(trimmedInput);
            if (templateNameExpr == null) {
                return null;
            }
            final FragmentSelection fragmentSelection = new FragmentSelection(templateNameExpr, null, null, false);
            return fragmentSelection;
        }
        
        final String templateName = trimmedInput.substring(0,separatorPos).trim();
        String fragmentSelector = trimmedInput.substring(separatorPos + 2).trim();
        
        if (fragmentSelector.length() == 0) {
            return null;
        }
        
        boolean xpath = false;
        
        if (fragmentSelector.length() > 2 && 
            fragmentSelector.charAt(0) == FRAGMENT_SELECTOR_XPATH_START &&
            fragmentSelector.charAt(fragmentSelector.length() - 1) == FRAGMENT_SELECTOR_XPATH_END) {

            fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1).trim();
            xpath = true;
            
        }

        final Expression templateNameExpr;
        if (!StringUtils.isEmptyOrWhitespace(templateName) && !CURRENT_TEMPLATE_NAME.equalsIgnoreCase(templateName)) {
            templateNameExpr = getExpressionDefaultToLiteral(templateName);
            if (templateNameExpr == null) {
                return null;
            }
        } else {
            templateNameExpr = null;
        }
        final Expression fragmentSelectorExpr = getExpressionDefaultToLiteral(fragmentSelector);
        if (fragmentSelectorExpr == null) {
            return null;
        }
        
        final FragmentSelection fragmentSelection =
                new FragmentSelection(templateNameExpr, fragmentSelectorExpr, null, xpath);

        return fragmentSelection;
        
    }


    
    
    private static Expression getExpressionDefaultToLiteral(final String valueStr) {
        final Expression expr = Expression.parse(valueStr);
        if (expr == null) {
            return TextLiteralExpression.parseTextLiteral(valueStr);
        }
        return expr;
    }
    


    private static boolean isAssignationNamed(final AssignationSequence assignationSequence) {

        // An assignation sequence will be considered "unnamed" if all variable names
        // start by "_arg", followed by a number. This will mean they have been automatically
        // assigned when parsed because no names were assigned.

        for (final Assignation assignation : assignationSequence.getAssignations()) {
            final Token token = assignation.getLeft();
            final String variableName = token.getValue();
            if (variableName == null) {
                return true;
            }
            if (!variableName.startsWith(UNNAMED_PARAMETERS_PREFIX)) {
                return true;
            }
            final int variableNameLen = variableName.length();
            for (int i = UNNAMED_PARAMETERS_PREFIX.length(); i < variableNameLen; i++) {
                final char c = variableName.charAt(i);
                if (!Character.isDigit(c)) {
                    return true;
                }
            }
        }

        return false;

    }


}

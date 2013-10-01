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


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class FragmentSelection implements Serializable {

    
    private static final long serialVersionUID = -5310313871594922690L;

    
    private final IStandardExpression templateName;
    private final IStandardExpression fragmentSelector;
    private final AssignationSequence parameters;

    
    
    public FragmentSelection(
            final IStandardExpression templateName, final IStandardExpression fragmentSelector,
            final AssignationSequence parameters) {
        super();
        // templateName can be null if fragment is to be executed on the current template
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.parameters = parameters;
    }

    
    
    public IStandardExpression getTemplateName() {
        return this.templateName;
    }

    public IStandardExpression getFragmentSelector() {
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


    public String getStringRepresentation() {

        final String templateNameStringRepresentation =
                (this.templateName != null? this.templateName.getStringRepresentation() : "");

        final String templateSelectionParameters;
        if (this.parameters == null || this.parameters.size() > 0) {
            templateSelectionParameters = "";
        } else {
            final StringBuilder paramBuilder = new StringBuilder();
            paramBuilder.append(' ');
            paramBuilder.append('(');
            paramBuilder.append(StringUtils.join(this.parameters.getAssignations(), ','));
            paramBuilder.append(')');
            templateSelectionParameters = paramBuilder.toString();
        }

        if (this.fragmentSelector == null) {
            return templateNameStringRepresentation + templateSelectionParameters;
        }
        return templateNameStringRepresentation + " :: " +
                this.fragmentSelector.getStringRepresentation() + templateSelectionParameters;
    }



    @Override
    public String toString() {
        return getStringRepresentation();
    }


}

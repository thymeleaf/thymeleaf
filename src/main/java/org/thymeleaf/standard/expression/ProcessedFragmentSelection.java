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
package org.thymeleaf.standard.expression;

import java.util.Map;

import org.thymeleaf.util.StringUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class ProcessedFragmentSelection {



    private final String templateName;
    private final String fragmentSelector;
    private final Map<String,Object> fragmentParameters;


    public ProcessedFragmentSelection(
            final String templateName, final String fragmentSelector, final Map<String, Object> fragmentParameters) {
        this.templateName = templateName;
        this.fragmentSelector = fragmentSelector;
        this.fragmentParameters = fragmentParameters;
    }


    public String getTemplateName() {
        return templateName;
    }

    public boolean hasFragmentSelector() {
        return this.fragmentSelector != null && this.fragmentSelector.length() > 0;
    }

    public String getFragmentSelector() {
        return fragmentSelector;
    }

    public Map<String, Object> getFragmentParameters() {
        return fragmentParameters;
    }

    public boolean hasParameters() {
        return this.fragmentParameters != null && this.fragmentParameters.size() > 0;
    }


    public String getStringRepresentation() {

        final String templateNameStringRepresentation =
                (this.templateName != null? this.templateName : "");

        final String templateSelectionParameters;
        if (this.fragmentParameters == null || this.fragmentParameters.size() > 0) {
            templateSelectionParameters = "";
        } else {
            final StringBuilder paramBuilder = new StringBuilder();
            paramBuilder.append(' ');
            paramBuilder.append('(');
            paramBuilder.append(StringUtils.join(this.fragmentParameters.entrySet(), ','));
            paramBuilder.append(')');
            templateSelectionParameters = paramBuilder.toString();
        }

        if (this.fragmentSelector == null) {
            return templateNameStringRepresentation + templateSelectionParameters;
        }
        return templateNameStringRepresentation + " :: " +
                this.fragmentSelector + templateSelectionParameters;
    }



    @Override
    public String toString() {
        return getStringRepresentation();
    }


}

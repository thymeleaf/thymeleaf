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

import java.util.Collections;
import java.util.Map;

import org.thymeleaf.engine.TemplateModel;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class Fragment {

    private final TemplateModel templateModel;
    private final Map<String,Object> parameters;
    private final boolean syntheticParameters;


    public Fragment(final TemplateModel templateModel, final Map<String, Object> parameters, final boolean syntheticParameters) {
        super();
        this.templateModel = templateModel;
        this.parameters = parameters != null ? Collections.unmodifiableMap(parameters) : null;
        this.syntheticParameters = (this.parameters != null && this.parameters.size() > 0 && syntheticParameters);
    }


    public TemplateModel getTemplateModel() {
        return this.templateModel;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public boolean hasSyntheticParameters() {
        return this.syntheticParameters;
    }

}

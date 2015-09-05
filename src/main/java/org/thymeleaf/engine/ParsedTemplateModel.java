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
package org.thymeleaf.engine;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.Validate;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public class ParsedTemplateModel extends ImmutableModel {

    private TemplateResolution templateResolution;



    // Package-protected constructor, because we don't want anyone creating these objects from outside the engine.
    // Specifically, they will only be created from the TemplateManager.
    // If a processor (be it standard or custom-made) wants to create a piece of model, that should be a Model
    // object, not this.
    ParsedTemplateModel(final IEngineConfiguration configuration, final TemplateResolution templateResolution) {
        super(configuration, (templateResolution == null? null : templateResolution.getTemplateMode()));
        Validate.notNull(templateResolution, "Template Resolution cannot be null");
        this.templateResolution = templateResolution;
    }


    public final TemplateResolution getTemplateResolution() {
        return this.templateResolution;
    }


}
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
import org.thymeleaf.cache.ICacheEntryValidity;
import org.thymeleaf.templatemode.TemplateMode;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ParsedFragmentMarkup extends ImmutableMarkup {

    private final ICacheEntryValidity validity;


    // Package-protected constructor, because we don't want anyone creating these objects from outside the engine.
    // Specifically, there will only be created from the TemplateProcessor.
    // If a processor (be it standard or custom-made) wants to create a piece of markup, that should be a Markup
    // object, not this.
    ParsedFragmentMarkup(final IEngineConfiguration configuration, final TemplateMode templateMode,
                         final ICacheEntryValidity validity) {
        super(configuration, templateMode);
        // Validity CAN be null
        this.validity = validity;
    }


    public final ICacheEntryValidity getValidity() {
        return this.validity;
    }

    
}
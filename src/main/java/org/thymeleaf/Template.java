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
package org.thymeleaf;

import org.thymeleaf.dom.Document;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Template implements Cloneable {

    private final String templateName;
    private final TemplateResolution templateResolution;
    private final Document document;
    
    Template(
            final String templateName,
            final TemplateResolution templateResolution, 
            final Document document) {
        super();
        this.templateName = templateName;
        this.templateResolution = templateResolution;
        this.document = document;
    }

    public String getTemplateName() {
        return this.templateName;
    }
    
    public TemplateResolution getTemplateResolution() {
        return this.templateResolution;
    }

    public Document getDocument() {
        return this.document;
    }
    
    
    @Override
    public Template clone() {
        return new Template(this.templateName, this.templateResolution, this.document.clone(true));
    }
    
}

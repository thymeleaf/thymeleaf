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
package org.thymeleaf;

import org.thymeleaf.dom.Document;
import org.thymeleaf.templateresolver.TemplateResolution;

/**
 * <p>
 *   A read and parsed template.
 * </p>
 * <p>
 *   <tt>Template</tt> objects model templates as they are needed by the {@link TemplateEngine},
 *   including its name, {@link TemplateResolution} objects and DOM tree.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Template {

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


    public Template createDuplicate() {
        // clone() would not be comfortable to use here because the "document" property would need to be assigned
        // after calling super.clone(), and it is final (so no assignation would be possible).
        return new Template(this.templateName, this.templateResolution, this.document.clone(true));
    }

}

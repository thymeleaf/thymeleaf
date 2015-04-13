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
package org.thymeleaf.aurora.engine;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.aurora.model.IAutoOpenElementTag;
import org.thymeleaf.aurora.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class AutoOpenElementTag extends AbstractProcessableElementTag implements IAutoOpenElementTag {


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    AutoOpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super(templateMode, elementDefinitions, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    AutoOpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName) {
        super(templateMode, elementDefinitions, attributeDefinitions, elementName);
    }



    // Meant to be called only from the cloneElementTag method
    private AutoOpenElementTag() {
        super();
    }




    // Meant to be called only from within the engine
    void setAutoOpenElementTag(
            final String elementName,
            final int line, final int col) {

        resetProcessableTag(elementName, line, col);

    }



    // Meant to be called only from within the engine
    void setFromAutoOpenElementTag(final IAutoOpenElementTag tag) {

        resetProcessableTag(tag.getElementName(), tag.getLine(), tag.getCol());
        this.elementAttributes.copyFrom(tag.getAttributes());

    }



    public void write(final Writer writer) throws IOException {
        // Nothing to be written... balanced elements were not present at the original template!
    }




    public AutoOpenElementTag cloneElementTag() {
        final AutoOpenElementTag clone = new AutoOpenElementTag();
        initializeProcessableElementTagClone(clone);
        return clone;
    }

}

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

import org.thymeleaf.aurora.model.ICloseElementTag;
import org.thymeleaf.aurora.model.IStandaloneElementTag;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class CloseElementTag
        extends AbstractElementTag implements ICloseElementTag {


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions) {
        super(templateMode, elementDefinitions);
    }



    // Meant to be called only from the model factory
    CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final String elementName) {
        super(templateMode, elementDefinitions, elementName);
    }



    // Meant to be called only from the cloneElementTag method
    protected CloseElementTag() {
        super();
    }




    // Meant to be called only from within the engine
    void setCloseElementTag(
            final String elementName,
            final int line, final int col) {
        resetElementTag(elementName, line, col);
    }



    // Meant to be called only from within the engine
    final void setFromStandaloneElementTag(final IStandaloneElementTag tag) {
        resetElementTag(tag.getElementName(), tag.getLine(), tag.getCol());
    }



    // Meant to be called only from within the engine
    void setFromCloseElementTag(final ICloseElementTag tag) {
        resetElementTag(tag.getElementName(), tag.getLine(), tag.getCol());
    }





    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        writer.write("</");
        writer.write(this.elementName);
        writer.write('>');
    }





    public CloseElementTag cloneElementTag() {
        final CloseElementTag clone = new CloseElementTag();
        initializeElementTagClone(clone);
        return clone;
    }

}

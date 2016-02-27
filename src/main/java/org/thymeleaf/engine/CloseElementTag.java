/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class CloseElementTag
            extends AbstractElementTag
            implements ICloseElementTag, IEngineTemplateEvent {


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    private boolean unmatched;


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
            final String elementName,
            final boolean synthetic,
            final boolean unmatched) {
        super(templateMode, elementDefinitions, elementName, synthetic);
        this.unmatched = unmatched;
    }



    // Meant to be called only from the cloneElementTag method
    protected CloseElementTag() {
        super();
    }



    public boolean isUnmatched() {
        return this.unmatched;
    }


    // Meant to be called only from within the engine
    void reset(final String elementName, final boolean synthetic, final boolean unmatched,
               final String templateName, final int line, final int col) {
        resetElementTag(elementName, synthetic, templateName, line, col);
        this.unmatched = unmatched;
    }







    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        if (this.synthetic) {
            // Nothing to be written... synthetic elements were not present at the original template!
            return;
        }
        // NOTE that being unmatched or not doesn't have an influence in how the tag is represented in output
        Validate.notNull(writer, "Writer cannot be null");
        if (this.templateMode.isText()) {
            writer.write("[/");
            writer.write(this.elementName);
            writer.write("]");
            return;
        }
        writer.write("</");
        writer.write(this.elementName);
        writer.write('>');
    }





    public CloseElementTag cloneEvent() {
        final CloseElementTag clone = new CloseElementTag();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final CloseElementTag original) {
        super.resetAsCloneOfElementTag(original);
        this.unmatched = original.unmatched;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final StandaloneElementTag original) {
        // It's exactly the same as with open tags - even the processors, because processors don't apply depending on
        // whether the tag is open or standalone...
        super.resetAsCloneOfElementTag(original);
        this.unmatched = false;
    }


    // Meant to be called only from within the engine
    static CloseElementTag asEngineCloseElementTag(
            final TemplateMode templateMode, final IEngineConfiguration configuration,
            final ICloseElementTag closeElementTag, final boolean cloneAlways) {

        if (closeElementTag instanceof CloseElementTag) {
            if (cloneAlways) {
                return ((CloseElementTag) closeElementTag).cloneEvent();
            }
            return (CloseElementTag) closeElementTag;
        }

        final CloseElementTag newInstance = new CloseElementTag(templateMode, configuration.getElementDefinitions());
        newInstance.reset(closeElementTag.getElementName(), closeElementTag.isSynthetic(), closeElementTag.isUnmatched(), closeElementTag.getTemplateName(), closeElementTag.getLine(), closeElementTag.getCol());
        return newInstance;

    }


}

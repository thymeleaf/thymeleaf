/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class CloseElementTag
            extends AbstractElementTag
            implements ICloseElementTag, IEngineTemplateEvent {


    final String trailingWhiteSpace; // can be null if there is none
    final boolean unmatched;




    CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final String trailingWhiteSpace,
            final boolean synthetic,
            final boolean unmatched) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic);
        this.trailingWhiteSpace = trailingWhiteSpace;
        this.unmatched = unmatched;
    }


    CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final String trailingWhiteSpace,
            final boolean synthetic,
            final boolean unmatched,
            final String templateName,
            final int line,
            final int col) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic, templateName, line, col);
        this.trailingWhiteSpace = trailingWhiteSpace;
        this.unmatched = unmatched;
    }




    public boolean isUnmatched() {
        return this.unmatched;
    }


    // ------------
    // NO GETTER for trailingWhiteSpace, as it is an internal-only property with no interest outside the engine
    // ------------




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        if (this.synthetic) {
            // Nothing to be written... synthetic elements were not present at the original template!
            return;
        }
        // NOTE that being unmatched or not doesn't have an influence in how the tag is represented in output
        if (this.templateMode.isText()) {
            writer.write("[/");
            writer.write(this.elementCompleteName);
            if (this.trailingWhiteSpace != null) {
                writer.write(this.trailingWhiteSpace);
            }
            writer.write("]");
            return;
        }
        writer.write("</");
        writer.write(this.elementCompleteName);
        if (this.trailingWhiteSpace != null) {
            writer.write(this.trailingWhiteSpace);
        }
        writer.write('>');
    }




    // Meant to be called only from within the engine
    static CloseElementTag asEngineCloseElementTag(final ICloseElementTag closeElementTag) {

        if (closeElementTag instanceof CloseElementTag) {
            return (CloseElementTag) closeElementTag;
        }

        return new CloseElementTag(
                closeElementTag.getTemplateMode(), closeElementTag.getElementDefinition(), closeElementTag.getElementCompleteName(),
                null, closeElementTag.isSynthetic(), closeElementTag.isUnmatched(),
                closeElementTag.getTemplateName(), closeElementTag.getLine(), closeElementTag.getCol());

    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleCloseElement(this);
    }


}

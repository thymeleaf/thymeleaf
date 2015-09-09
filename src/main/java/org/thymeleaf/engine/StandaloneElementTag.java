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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class StandaloneElementTag
            extends AbstractProcessableElementTag
            implements IStandaloneElementTag, IEngineTemplateEvent {

    private boolean minimized;



    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    StandaloneElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super(templateMode, elementDefinitions, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    StandaloneElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName,
            final boolean synthetic,
            final boolean minimized) {
        super(templateMode, elementDefinitions, attributeDefinitions, elementName, synthetic);
        this.minimized = minimized;
    }



    // Meant to be called only from the cloneElementTag method
    private StandaloneElementTag() {
        super();
    }




    public boolean isMinimized() {
        return this.minimized;
    }


    public void setMinimized(final boolean minimized) {
        if (this.templateMode != TemplateMode.HTML && !minimized) {
            throw new IllegalArgumentException("Standalone tag can only be non-minimized when in HTML template mode. (mode is " + this.templateMode + ")");
        }
        this.minimized = minimized; // No need to do anything else
    }




    // Meant to be called only from within the engine
    void reset(final String elementName, final boolean synthetic,
               final boolean minimized,
               final String templateName, final int line, final int col) {

        resetProcessableElementTag(elementName, synthetic, templateName, line, col);
        this.minimized = minimized;

    }





    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        if (this.synthetic) {
            // Nothing to be written... synthetic elements were not present at the original template!
            return;
        }
        Validate.notNull(writer, "Writer cannot be null");
        if (this.templateMode.isText()) {
            writer.write("[#");
            writer.write(this.elementName);
            this.elementAttributes.write(writer);
            if (this.minimized) {
                writer.write("/]");
            } else {
                writer.write("]");
            }
            return;
        }
        writer.write('<');
        writer.write(this.elementName);
        this.elementAttributes.write(writer);
        if (this.minimized) {
            writer.write("/>");
        } else {
            writer.write('>');
        }
    }





    public StandaloneElementTag cloneEvent() {
        final StandaloneElementTag clone = new StandaloneElementTag();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final StandaloneElementTag original) {
        super.resetAsCloneOfProcessableElementTag(original);
        this.minimized = original.minimized;
    }


    // Meant to be called only from within the engine
    static StandaloneElementTag asEngineStandaloneElementTag(
            final TemplateMode templateMode, final IEngineConfiguration configuration,
            final IStandaloneElementTag standaloneElementTag, final boolean cloneAlways) {

        if (standaloneElementTag instanceof StandaloneElementTag) {
            if (cloneAlways) {
                return ((StandaloneElementTag) standaloneElementTag).cloneEvent();
            }
            return (StandaloneElementTag) standaloneElementTag;
        }

        final StandaloneElementTag newInstance = new StandaloneElementTag(templateMode, configuration.getElementDefinitions(), configuration.getAttributeDefinitions());

        newInstance.reset(standaloneElementTag.getElementName(), standaloneElementTag.isSynthetic(), standaloneElementTag.isMinimized(), standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());

        final IElementAttributes attributes = standaloneElementTag.getAttributes();
        if (attributes != null) {
            // We have to do this by iterating because we don't know the specific instance of the tag's attributes, and
            // in fact we also don't want a complete clone (versioning, etc.). We are just copying the attributes
            final List<String> attributeCompleteNames = attributes.getAllCompleteNames();
            for (final String attributeCompleteName : attributeCompleteNames) {
                newInstance.elementAttributes.setAttribute(
                        attributeCompleteName, attributes.getValue(attributeCompleteName), attributes.getValueQuotes(attributeCompleteName));
            }
        }

        return newInstance;

    }

}

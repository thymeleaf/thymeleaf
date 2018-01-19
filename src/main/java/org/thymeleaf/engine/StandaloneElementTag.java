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
import java.util.Arrays;

import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
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

    final boolean minimized;



    StandaloneElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final Attributes attributes,
            final boolean synthetic,
            final boolean minimized) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic);
        Validate.isTrue(minimized || templateMode == TemplateMode.HTML, "Not-minimized standalone elements are only allowed in HTML template mode (is " + templateMode + ")");
        this.minimized = minimized;
    }


    StandaloneElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final Attributes attributes,
            final boolean synthetic,
            final boolean minimized,
            final String templateName,
            final int line,
            final int col) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic, templateName, line, col);
        Validate.isTrue(minimized || templateMode == TemplateMode.HTML, "Not-minimized standalone elements are only allowed in HTML template mode (is " + templateMode + ")");
        this.minimized = minimized;
    }




    public boolean isMinimized() {
        return this.minimized;
    }




    StandaloneElementTag setAttribute(
            final AttributeDefinitions attributeDefinitions,
            final AttributeDefinition attributeDefinition, final String completeName,
            final String value, final AttributeValueQuotes valueQuotes) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes =
                oldAttributes.setAttribute(attributeDefinitions, this.templateMode, attributeDefinition, completeName, value, valueQuotes);
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }




    StandaloneElementTag replaceAttribute(
            final AttributeDefinitions attributeDefinitions,
            final AttributeName oldName, final AttributeDefinition newAttributeDefinition, final String completeNewName,
            final String value, final AttributeValueQuotes valueQuotes) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes =
                oldAttributes.replaceAttribute(attributeDefinitions, this.templateMode, oldName, newAttributeDefinition, completeNewName, value, valueQuotes);
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }




    StandaloneElementTag removeAttribute(final String prefix, final String name) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, prefix, name);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }


    StandaloneElementTag removeAttribute(final String completeName) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, completeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }


    StandaloneElementTag removeAttribute(final AttributeName attributeName) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes = oldAttributes.removeAttribute(attributeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        if (this.synthetic) {
            // Nothing to be written... synthetic elements were not present at the original template!
            return;
        }
        if (this.templateMode.isText()) {
            writer.write("[#");
            writer.write(this.elementCompleteName);
            if (this.attributes != null) {
                this.attributes.write(writer);
            }
            if (this.minimized) {
                writer.write("/]");
            } else {
                writer.write("]");
            }
            return;
        }
        writer.write('<');
        writer.write(this.elementCompleteName);
        if (this.attributes != null) {
            this.attributes.write(writer);
        }
        if (this.minimized) {
            writer.write("/>");
        } else {
            writer.write('>');
        }
    }




    // Meant to be called only from within the engine
    static StandaloneElementTag asEngineStandaloneElementTag(final IStandaloneElementTag standaloneElementTag) {

        if (standaloneElementTag instanceof StandaloneElementTag) {
            return (StandaloneElementTag) standaloneElementTag;
        }


        final IAttribute[] originalAttributeArray = standaloneElementTag.getAllAttributes();

        final Attributes attributes;
        if (originalAttributeArray == null || originalAttributeArray.length == 0) {
            attributes = null;
        } else {
            // We will perform a deep cloning of the attributes into objects of the Attribute class, so that
            // we make sure absolutely all Attributes in the new event are under the engine's control
            final Attribute[] newAttributeArray = new Attribute[originalAttributeArray.length];
            for (int i = 0; i < originalAttributeArray.length; i++) {
                final IAttribute originalAttribute = originalAttributeArray[i];
                newAttributeArray[i] =
                        new Attribute(
                                originalAttribute.getAttributeDefinition(), originalAttribute.getAttributeCompleteName(),
                                originalAttribute.getOperator(), originalAttribute.getValue(), originalAttribute.getValueQuotes(),
                                originalAttribute.getTemplateName(), originalAttribute.getLine(), originalAttribute.getCol());
            }
            final String[] newInnerWhiteSpaces;
            if (newAttributeArray.length == 1) {
                newInnerWhiteSpaces = Attributes.DEFAULT_WHITE_SPACE_ARRAY;
            } else {
                newInnerWhiteSpaces = new String[newAttributeArray.length];
                Arrays.fill(newInnerWhiteSpaces, Attributes.DEFAULT_WHITE_SPACE);
            }
            attributes = new Attributes(newAttributeArray, newInnerWhiteSpaces);
        }

        return new StandaloneElementTag(
                standaloneElementTag.getTemplateMode(), standaloneElementTag.getElementDefinition(), standaloneElementTag.getElementCompleteName(),
                attributes, standaloneElementTag.isSynthetic(), standaloneElementTag.isMinimized(),
                standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());

    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleStandaloneElement(this);
    }

}

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
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class OpenElementTag
            extends AbstractProcessableElementTag
            implements IOpenElementTag, IEngineTemplateEvent {




    OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final Attributes attributes,
            final boolean synthetic) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic);
    }


    OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final Attributes attributes,
            final boolean synthetic,
            final String templateName,
            final int line,
            final int col) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic, templateName, line, col);
    }




    OpenElementTag setAttribute(
            final AttributeDefinitions attributeDefinitions,
            final AttributeDefinition attributeDefinition, final String completeName,
            final String value, final AttributeValueQuotes valueQuotes) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes =
                oldAttributes.setAttribute(attributeDefinitions, this.templateMode, attributeDefinition, completeName, value, valueQuotes);
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }




    OpenElementTag replaceAttribute(
            final AttributeDefinitions attributeDefinitions,
            final AttributeName oldName, final AttributeDefinition newAttributeDefinition, final String completeNewName,
            final String value, final AttributeValueQuotes valueQuotes) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes =
                oldAttributes.replaceAttribute(attributeDefinitions, this.templateMode, oldName, newAttributeDefinition, completeNewName, value, valueQuotes);
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }




    OpenElementTag removeAttribute(final String prefix, final String name) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, prefix, name);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }


    OpenElementTag removeAttribute(final String completeName) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, completeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }


    OpenElementTag removeAttribute(final AttributeName attributeName) {
        final Attributes oldAttributes = (this.attributes != null? this.attributes : Attributes.EMPTY_ATTRIBUTES);
        final Attributes newAttributes = oldAttributes.removeAttribute(attributeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
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
            writer.write("]");
            return;
        }
        writer.write('<');
        writer.write(this.elementCompleteName);
        if (this.attributes != null) {
            this.attributes.write(writer);
        }
        writer.write('>');
    }




    // Meant to be called only from within the engine
    static OpenElementTag asEngineOpenElementTag(final IOpenElementTag openElementTag) {

        if (openElementTag instanceof OpenElementTag) {
            return (OpenElementTag) openElementTag;
        }


        final IAttribute[] originalAttributeArray = openElementTag.getAllAttributes();

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

        return new OpenElementTag(
                openElementTag.getTemplateMode(), openElementTag.getElementDefinition(), openElementTag.getElementCompleteName(),
                attributes, openElementTag.isSynthetic(),
                openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol());

    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleOpenElement(this);
    }



}

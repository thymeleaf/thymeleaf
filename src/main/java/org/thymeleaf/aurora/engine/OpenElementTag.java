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
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.thymeleaf.aurora.processor.IProcessor;
import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class OpenElementTag implements IOpenElementTag {

    private final TemplateMode templateMode;
    private final ElementDefinitions elementDefinitions;

    private final ElementAttributes elementAttributes;

    // Should actually be a set, but given we will need to sort it very often, a list is more handy. Dialect constraints
    // ensure anyway that we will never have duplicates here, because the same processor can never be applied to more than
    // one attribute.
    private List<IProcessor> applicableProcessors = null;

    private ElementDefinition elementDefinition;
    private String elementName;
    private int line;
    private int col;


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
        this.elementAttributes = new ElementAttributes(this.templateMode, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
        this.elementAttributes = new ElementAttributes(this.templateMode, attributeDefinitions);
        initializeFromOpenElementTag(elementName, true);
    }



    // Meant to be called only from the cloneTag method
    private OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line,
            final int col) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
        this.elementDefinition = elementDefinition;
        this.elementName = elementName;
        this.elementAttributes = elementAttributes;
        this.line = line;
        this.col = col;
    }



    public ElementDefinition getElementDefinition() {
        return this.elementDefinition;
    }


    public String getElementName() {
        return this.elementName;
    }


    public IElementAttributes getAttributes() {
        return this.elementAttributes;
    }



    public void setElementName(final String elementName) {
        initializeFromOpenElementTag(elementName, false);
    }




    // Meant to be called only from within the engine
    void setOpenElementTag(
            final String elementName,
            final int line, final int col) {

        this.elementName = elementName;
        this.elementDefinition =
                (this.templateMode.isHTML()?
                    this.elementDefinitions.forHTMLName(elementName) : this.elementDefinitions.forXMLName(elementName));

        this.elementAttributes.clearAll();

        this.elementAttributes.setContainerElementName(this.elementDefinition.elementName);

        this.line = line;
        this.col = col;

    }



    private void initializeFromOpenElementTag(
            final String elementName, final boolean clearAttributes) {

        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }

        if (this.templateMode.isHTML()) {
            final HTMLElementDefinition newHTMLElementDefinition = this.elementDefinitions.forHTMLName(elementName);
            if (newHTMLElementDefinition.getType().isVoid()) {
                throw new IllegalArgumentException(
                        "Specified HTML element name \"" + elementName + "\" is void, which cannot " +
                        "be contained in an OPEN element tag");
            }
            this.elementDefinition = newHTMLElementDefinition;
        } else {
            this.elementDefinition = this.elementDefinitions.forXMLName(elementName);
        }

        this.elementName = elementName;

        if (clearAttributes) {
            this.elementAttributes.clearAll();
        }

        this.elementAttributes.setContainerElementName(this.elementDefinition.elementName);

        this.line = -1;
        this.col = -1;

    }






    public boolean hasLocation() {
        return (this.line != -1 && this.col != -1);
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }





    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        writer.write('<');
        writer.write(this.elementName);
        this.elementAttributes.write(writer);
        writer.write('>');
    }



    public String toString() {
        final StringWriter stringWriter = new StringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception while creating String representation of model entity", e);
        }
        return stringWriter.toString();
    }






    public OpenElementTag cloneElementTag() {
        return new OpenElementTag(
                        this.templateMode, this.elementDefinitions,
                        this.elementDefinition, this.elementName, this.elementAttributes.cloneElementAttributes(),
                        this.line, this.col);
    }

}

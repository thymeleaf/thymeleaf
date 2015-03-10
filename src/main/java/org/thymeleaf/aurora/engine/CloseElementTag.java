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

import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class CloseElementTag implements ICloseElementTag {

    private final TemplateMode templateMode;
    private final ElementDefinitions elementDefinitions;

    private ElementDefinition elementDefinition;
    private String elementName;
    private int line;
    private int col;


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
    }



    // Meant to be called only from the model factory
    CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final String elementName) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
        initializeFromCloseElementTag(elementName);
    }



    // Meant to be called only from the cloneTag method
    private CloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line,
            final int col) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
        this.elementDefinition = elementDefinition;
        this.elementName = elementName;
        this.line = line;
        this.col = col;
    }



    public ElementDefinition getElementDefinition() {
        return this.elementDefinition;
    }


    public String getElementName() {
        return this.elementName;
    }



    public void setElementName(final String elementName) {
        initializeFromCloseElementTag(elementName);
    }




    // Meant to be called only from within the engine
    void setCloseElementTag(
            final String elementName,
            final int line, final int col) {

        this.elementName = elementName;
        this.elementDefinition =
                (this.templateMode.isHTML()?
                    this.elementDefinitions.forHTMLName(elementName) : this.elementDefinitions.forXMLName(elementName));

        this.line = line;
        this.col = col;

    }



    private void initializeFromCloseElementTag(
            final String elementName) {

        if (elementName == null || elementName.trim().length() == 0) {
            throw new IllegalArgumentException("Element name cannot be null or empty");
        }

        this.elementName = elementName;
        this.elementDefinition =
                (this.templateMode.isHTML()?
                    this.elementDefinitions.forHTMLName(elementName) : this.elementDefinitions.forXMLName(elementName));

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
        writer.write("</");
        writer.write(this.elementName);
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






    public CloseElementTag cloneElementTag() {
        return new CloseElementTag(
                        this.templateMode, this.elementDefinitions,
                        this.elementDefinition, this.elementName,
                        this.line, this.col);
    }

}

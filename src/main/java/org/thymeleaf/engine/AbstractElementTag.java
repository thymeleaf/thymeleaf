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
import java.io.StringWriter;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
abstract class AbstractElementTag implements IElementTag {

    protected TemplateMode templateMode;
    protected ElementDefinitions elementDefinitions;

    protected ElementDefinition elementDefinition;
    protected String elementName;

    private String templateName;
    private int line;
    private int col;




    // Meant to be called only from the template handler adapter
    protected AbstractElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions) {
        super();
        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;
    }



    // Meant to be called only from the model factory
    protected AbstractElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final String elementName) {

        super();

        Validate.notEmpty(elementName, "Element name cannot be null or empty");

        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;

        resetElementTag(elementName, null, -1, -1);

    }



    // Meant to be called only from the cloning infrastructure
    protected AbstractElementTag() {
        super();
    }



    public final ElementDefinition getElementDefinition() {
        return this.elementDefinition;
    }


    public final String getElementName() {
        return this.elementName;
    }




    protected void resetElementTag(
            final String elementName,
            final String templateName, final int line, final int col) {

        this.elementName = elementName;
        this.elementDefinition =
                (this.templateMode.isHTML()?
                    this.elementDefinitions.forHTMLName(this.elementName) : this.elementDefinitions.forXMLName(this.elementName));

        this.templateName = templateName;
        this.line = line;
        this.col = col;

    }






    public final boolean hasLocation() {
        return (this.templateName != null && this.line != -1 && this.col != -1);
    }

    public final String getTemplateName() {
        return this.templateName;
    }

    public final int getLine() {
        return this.line;
    }

    public final int getCol() {
        return this.col;
    }





    public final String toString() {
        final StringWriter stringWriter = new StringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception while creating String representation of model entity", e);
        }
        return stringWriter.toString();
    }




    protected void resetAsCloneOfElementTag(final AbstractElementTag original) {
        this.templateMode = original.templateMode;
        this.templateMode = original.templateMode;
        this.elementDefinitions = original.elementDefinitions;
        this.elementDefinition = original.elementDefinition;
        this.elementName = original.elementName;
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;
    }

}

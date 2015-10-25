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
abstract class AbstractElementTag extends AbstractTemplateEvent implements IElementTag {

    protected TemplateMode templateMode;
    protected ElementDefinitions elementDefinitions;

    protected ElementDefinition elementDefinition;
    protected String elementName;

    protected boolean synthetic;




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
            final String elementName,
            final boolean synthetic) {

        super();

        Validate.notEmpty(elementName, "Element name cannot be null or empty");

        this.templateMode = templateMode;
        this.elementDefinitions = elementDefinitions;

        resetElementTag(elementName, synthetic, null, -1, -1);

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


    public final boolean isSynthetic() {
        return this.synthetic;
    }



    protected void resetElementTag(
            final String elementName, final boolean synthetic,
            final String templateName, final int line, final int col) {

        super.resetTemplateEvent(templateName, line, col);

        this.elementName = elementName;
        this.elementDefinition = computeElementDefinition();

        this.synthetic = synthetic;

    }


    private ElementDefinition computeElementDefinition() {
        switch (this.templateMode) {
            case HTML:
                return this.elementDefinitions.forHTMLName(this.elementName);
            case XML:
                return this.elementDefinitions.forXMLName(this.elementName);
            case TEXT:
                return this.elementDefinitions.forTextName(this.elementName);
            case JAVASCRIPT:
                return this.elementDefinitions.forJavaScriptName(this.elementName);
            case CSS:
                return this.elementDefinitions.forCSSName(this.elementName);
            default:
                throw new IllegalArgumentException("Unknown template mode: " + this.templateMode);
        }
    }




    protected final void resetAsCloneOfElementTag(final AbstractElementTag original) {
        super.resetAsCloneOfTemplateEvent(original);
        this.templateMode = original.templateMode;
        this.templateMode = original.templateMode;
        this.elementDefinitions = original.elementDefinitions;
        this.elementDefinition = original.elementDefinition;
        this.elementName = original.elementName;
        this.synthetic = original.synthetic;
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

}

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
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class OpenElementTag
            extends AbstractProcessableElementTag
            implements IOpenElementTag, IEngineTemplateHandlerEvent {


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super(templateMode, elementDefinitions, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    OpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName) {
        super(templateMode, elementDefinitions, attributeDefinitions, elementName);
    }



    // Meant to be called only from the cloneElementTag method
    protected OpenElementTag() {
        super();
    }




    // Meant to be called only from within the engine
    void reset(final String elementName,
               final int line, final int col) {
        resetProcessableElementTag(elementName, line, col);
    }




    public void write(final Writer writer) throws IOException {
        Validate.notNull(writer, "Writer cannot be null");
        writer.write('<');
        writer.write(this.elementName);
        this.elementAttributes.write(writer);
        writer.write('>');
    }




    public OpenElementTag cloneElementTag() {
        final OpenElementTag clone = new OpenElementTag();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final OpenElementTag original) {
        super.resetAsCloneOfProcessableElementTag(original);
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final StandaloneElementTag original) {
        // It's exactly the same as with open tags - even the processors, because processors don't apply depending on
        // whether the tag is open or standalone...
        super.resetAsCloneOfProcessableElementTag(original);
    }


    // Meant to be called only from within the engine
    static OpenElementTag asEngineOpenElementTag(
            final TemplateMode templateMode, final IEngineConfiguration configuration,
            final IOpenElementTag openElementTag, final boolean cloneAlways) {

        if (openElementTag instanceof OpenElementTag) {
            if (cloneAlways) {
                return ((OpenElementTag) openElementTag).cloneElementTag();
            }
            return (OpenElementTag) openElementTag;
        }

        final OpenElementTag newInstance = new OpenElementTag(templateMode, configuration.getElementDefinitions(), configuration.getAttributeDefinitions());

        newInstance.reset(openElementTag.getElementName(), openElementTag.getLine(), openElementTag.getCol());

        final IElementAttributes attributes = openElementTag.getAttributes();
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

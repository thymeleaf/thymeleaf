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
import java.io.Writer;
import java.util.List;

import org.thymeleaf.aurora.IEngineConfiguration;
import org.thymeleaf.aurora.model.IAutoOpenElementTag;
import org.thymeleaf.aurora.model.IElementAttributes;
import org.thymeleaf.aurora.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class AutoOpenElementTag
            extends AbstractProcessableElementTag
            implements IAutoOpenElementTag, IEngineTemplateHandlerEvent {


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    AutoOpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions) {
        super(templateMode, elementDefinitions, attributeDefinitions);
    }



    // Meant to be called only from the model factory
    AutoOpenElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final AttributeDefinitions attributeDefinitions,
            final String elementName) {
        super(templateMode, elementDefinitions, attributeDefinitions, elementName);
    }



    // Meant to be called only from the cloneElementTag method
    private AutoOpenElementTag() {
        super();
    }




    // Meant to be called only from within the engine
    void reset(final String elementName,
               final int line, final int col) {

        resetProcessableElementTag(elementName, line, col);

    }



    public void write(final Writer writer) throws IOException {
        // Nothing to be written... balanced elements were not present at the original template!
    }




    public AutoOpenElementTag cloneElementTag() {
        final AutoOpenElementTag clone = new AutoOpenElementTag();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final AutoOpenElementTag original) {
        super.resetAsCloneOfProcessableElementTag(original);
    }


    // Meant to be called only from within the engine
    static AutoOpenElementTag asEngineAutoOpenElementTag(
            final TemplateMode templateMode, final IEngineConfiguration configuration,
            final IAutoOpenElementTag autoOpenElementTag, final boolean cloneAlways) {

        if (autoOpenElementTag instanceof AutoOpenElementTag) {
            if (cloneAlways) {
                return ((AutoOpenElementTag) autoOpenElementTag).cloneElementTag();
            }
            return (AutoOpenElementTag) autoOpenElementTag;
        }

        final AutoOpenElementTag newInstance = new AutoOpenElementTag(templateMode, configuration.getElementDefinitions(), configuration.getAttributeDefinitions());

        newInstance.reset(autoOpenElementTag.getElementName(), autoOpenElementTag.getLine(), autoOpenElementTag.getCol());

        final IElementAttributes attributes = autoOpenElementTag.getAttributes();
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

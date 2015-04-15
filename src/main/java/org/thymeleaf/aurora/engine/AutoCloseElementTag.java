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

import org.thymeleaf.aurora.ITemplateEngineConfiguration;
import org.thymeleaf.aurora.model.IAutoCloseElementTag;
import org.thymeleaf.aurora.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class AutoCloseElementTag
            extends AbstractElementTag
            implements IAutoCloseElementTag, IEngineTemplateHandlerEvent {


    /*
     * Objects of this class are meant to both be reused by the engine and also created fresh by the processors. This
     * should allow reducing the number of instances of this class to the minimum.
     */


    // Meant to be called only from the template handler adapter
    AutoCloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions) {
        super(templateMode, elementDefinitions);
    }



    // Meant to be called only from the model factory
    AutoCloseElementTag(
            final TemplateMode templateMode,
            final ElementDefinitions elementDefinitions,
            final String elementName) {
        super(templateMode, elementDefinitions, elementName);
    }



    // Meant to be called only from the cloneElementTag method
    private AutoCloseElementTag() {
        super();
    }




    // Meant to be called only from within the engine
    void reset(final String elementName,
               final int line, final int col) {
        resetElementTag(elementName, line, col);
    }





    public void write(final Writer writer) throws IOException {
        // Nothing to be written... balanced elements were not present at the original template!
    }




    public AutoCloseElementTag cloneElementTag() {
        final AutoCloseElementTag clone = new AutoCloseElementTag();
        clone.resetAsCloneOf(this);
        return clone;
    }



    // Meant to be called only from within the engine
    void resetAsCloneOf(final AutoCloseElementTag original) {
        super.resetAsCloneOfElementTag(original);
    }



    // Meant to be called only from within the engine
    static AutoCloseElementTag asEngineAutoCloseElementTag(
            final TemplateMode templateMode, final ITemplateEngineConfiguration configuration,
            final IAutoCloseElementTag autoCloseElementTag, final boolean cloneAlways) {

        if (autoCloseElementTag instanceof AutoCloseElementTag) {
            if (cloneAlways) {
                return ((AutoCloseElementTag) autoCloseElementTag).cloneElementTag();
            }
            return (AutoCloseElementTag) autoCloseElementTag;
        }

        final AutoCloseElementTag newInstance = new AutoCloseElementTag(templateMode, configuration.getElementDefinitions());
        newInstance.reset(autoCloseElementTag.getElementName(), autoCloseElementTag.getLine(), autoCloseElementTag.getCol());
        return newInstance;

    }

}

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

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
abstract class AbstractElementTag extends AbstractTemplateEvent implements IElementTag {

    final TemplateMode templateMode;
    final ElementDefinition elementDefinition;
    final String elementCompleteName;
    final boolean synthetic;




    protected AbstractElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final boolean synthetic) {
        super();
        this.templateMode = templateMode;
        this.elementDefinition = elementDefinition;
        this.elementCompleteName = elementCompleteName;
        this.synthetic = synthetic;
    }


    protected AbstractElementTag(
            final TemplateMode templateMode,
            final ElementDefinition elementDefinition,
            final String elementCompleteName,
            final boolean synthetic,
            final String templateName, final int line, final int col) {
        super(templateName, line, col);
        this.templateMode = templateMode;
        this.elementDefinition = elementDefinition;
        this.elementCompleteName = elementCompleteName;
        this.synthetic = synthetic;
    }




    public final TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public final String getElementCompleteName() {
        return this.elementCompleteName;
    }

    public final ElementDefinition getElementDefinition() {
        return this.elementDefinition;
    }

    public final boolean isSynthetic() {
        return this.synthetic;
    }




    @Override
    public final String toString() {
        final Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
        } catch (final IOException e) {
            throw new TemplateProcessingException("Exception while creating String representation of model entity", e);
        }
        return stringWriter.toString();
    }

}

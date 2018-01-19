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

import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.ITemplateStart;

/*
 * Engine implementation of ITemplateStart.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TemplateStart extends AbstractTemplateEvent implements ITemplateStart, IEngineTemplateEvent {

    final static TemplateStart TEMPLATE_START_INSTANCE = new TemplateStart();




    private TemplateStart() {
        super();
    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        // Nothing to be done here -- template start events are not writable to output!
    }




    // Meant to be called only from within the engine
    static TemplateStart asEngineTemplateStart(final ITemplateStart templateStart) {
        return TEMPLATE_START_INSTANCE;
    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleTemplateStart(this);
    }




    @Override
    public final String toString() {
        return "";
    }


}

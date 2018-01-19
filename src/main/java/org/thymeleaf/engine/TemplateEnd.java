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
import org.thymeleaf.model.ITemplateEnd;

/*
 * Engine implementation of ITemplateEnd.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TemplateEnd extends AbstractTemplateEvent implements ITemplateEnd, IEngineTemplateEvent {

    final static TemplateEnd TEMPLATE_END_INSTANCE = new TemplateEnd();




    private TemplateEnd() {
        super();
    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        // Nothing to be done here -- template end events are not writable to output!
    }




    // Meant to be called only from within the engine
    static TemplateEnd asEngineTemplateEnd(final ITemplateEnd templateEnd) {
        return TEMPLATE_END_INSTANCE;
    }




    @Override
    public void beHandled(final ITemplateHandler handler) {
        handler.handleTemplateEnd(this);
    }




    @Override
    public final String toString() {
        return "";
    }


}

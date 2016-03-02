/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class TemplateStart extends AbstractTemplateEvent implements ITemplateStart, IEngineTemplateEvent {

    private long startTimeNanos;



    // Meant to be called only from the template handler adapter
    TemplateStart() {
        super();
    }




    public EventType getEventType() {
        return EventType.TEMPLATE_START;
    }




    public long getStartTimeNanos() {
        return this.startTimeNanos;
    }




    void reset(final long startTimeNanos,
               final String templateName, final int line, final int col) {

        super.resetTemplateEvent(templateName, line, col);
        this.startTimeNanos = startTimeNanos;

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        // Nothing to be done here -- template start events are not writable to output!
    }


    public TemplateStart cloneEvent() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final TemplateStart clone = new TemplateStart();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final TemplateStart original) {

        super.resetAsCloneOfTemplateEvent(original);
        this.startTimeNanos = original.startTimeNanos;

    }


    // Meant to be called only from within the engine
    static TemplateStart asEngineTemplateStart(final ITemplateStart templateStart, final boolean cloneAlways) {

        if (templateStart instanceof TemplateStart) {
            if (cloneAlways) {
                return ((TemplateStart) templateStart).cloneEvent();
            }
            return (TemplateStart) templateStart;
        }

        final TemplateStart newInstance = new TemplateStart();
        newInstance.startTimeNanos = templateStart.getStartTimeNanos();
        newInstance.resetTemplateEvent(templateStart.getTemplateName(), templateStart.getLine(), templateStart.getCol());
        return newInstance;

    }





    @Override
    public final String toString() {
        return "";
    }


}

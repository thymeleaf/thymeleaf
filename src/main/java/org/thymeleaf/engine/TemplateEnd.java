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

import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.ITemplateEnd;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class TemplateEnd extends AbstractTemplateEvent implements ITemplateEnd, IEngineTemplateEvent {

    private long endTimeNanos;
    private long totalTimeNanos;



    // Meant to be called only from the template handler adapter
    TemplateEnd() {
        super();
    }



    public long getEndTimeNanos() {
        return this.endTimeNanos;
    }

    public long getTotalTimeNanos() {
        return this.totalTimeNanos;
    }




    void reset(final long endTimeNanos, final long totalTimeNanos,
               final String templateName, final int line, final int col) {

        super.resetTemplateEvent(templateName, line, col);

        this.endTimeNanos = endTimeNanos;
        this.totalTimeNanos = totalTimeNanos;

    }




    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }


    public void write(final Writer writer) throws IOException {
        // Nothing to be done here -- template end events are not writable to output!
    }


    public TemplateEnd cloneEvent() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final TemplateEnd clone = new TemplateEnd();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final TemplateEnd original) {

        super.resetAsCloneOfTemplateEvent(original);

        this.endTimeNanos = original.endTimeNanos;
        this.totalTimeNanos = original.totalTimeNanos;

    }


    // Meant to be called only from within the engine
    static TemplateEnd asEngineTemplateEnd(final ITemplateEnd templateEnd, final boolean cloneAlways) {

        if (templateEnd instanceof TemplateEnd) {
            if (cloneAlways) {
                return ((TemplateEnd) templateEnd).cloneEvent();
            }
            return (TemplateEnd) templateEnd;
        }

        final TemplateEnd newInstance = new TemplateEnd();
        newInstance.endTimeNanos = templateEnd.getEndTimeNanos();
        newInstance.totalTimeNanos = templateEnd.getTotalTimeNanos();
        newInstance.resetTemplateEvent(templateEnd.getTemplateName(), templateEnd.getLine(), templateEnd.getCol());
        return newInstance;

    }





    @Override
    public final String toString() {
        return "";
    }


}

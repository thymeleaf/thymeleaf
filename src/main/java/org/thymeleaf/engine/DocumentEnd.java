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

import org.thymeleaf.model.IDocumentEnd;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class DocumentEnd
            implements IDocumentEnd, IEngineTemplateHandlerEvent {

    private long endTimeNanos;
    private long totalTimeNanos;

    private String templateName;
    private int line;
    private int col;



    // Meant to be called only from the template handler adapter
    DocumentEnd() {
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

        this.endTimeNanos = endTimeNanos;
        this.totalTimeNanos = totalTimeNanos;

        this.templateName = templateName;
        this.line = line;
        this.col = col;

    }




    public boolean hasLocation() {
        return (this.templateName != null && this.line != -1 && this.col != -1);
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLine() {
        return this.line;
    }

    public int getCol() {
        return this.col;
    }



    public String toString() {
        return "{documentend}";
    }



    public DocumentEnd cloneEvent() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final DocumentEnd clone = new DocumentEnd();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final DocumentEnd original) {

        this.endTimeNanos = original.endTimeNanos;
        this.totalTimeNanos = original.totalTimeNanos;
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;

    }


    // Meant to be called only from within the engine
    static DocumentEnd asEngineDocumentEnd(final IDocumentEnd documentEnd, final boolean cloneAlways) {

        if (documentEnd instanceof DocumentEnd) {
            if (cloneAlways) {
                return ((DocumentEnd) documentEnd).cloneEvent();
            }
            return (DocumentEnd) documentEnd;
        }

        final DocumentEnd newInstance = new DocumentEnd();
        newInstance.endTimeNanos = documentEnd.getEndTimeNanos();
        newInstance.totalTimeNanos = documentEnd.getTotalTimeNanos();
        newInstance.templateName = documentEnd.getTemplateName();
        newInstance.line = documentEnd.getLine();
        newInstance.col = documentEnd.getCol();
        return newInstance;

    }


}

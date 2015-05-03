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

import org.thymeleaf.model.IDocumentStart;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class DocumentStart
            implements IDocumentStart, IEngineTemplateHandlerEvent {

    private long startTimeNanos;

    private String templateName;
    private int line;
    private int col;



    // Meant to be called only from the template handler adapter
    DocumentStart() {
        super();
    }



    public long getStartTimeNanos() {
        return this.startTimeNanos;
    }




    void reset(final long startTimeNanos,
               final String templateName, final int line, final int col) {

        this.startTimeNanos = startTimeNanos;

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
        return "{documentstart}";
    }



    public DocumentStart cloneEvent() {
        // When cloning we will protect the buffer as only the instances used themselves as buffers in the 'engine'
        // package should reference a buffer.
        final DocumentStart clone = new DocumentStart();
        clone.resetAsCloneOf(this);
        return clone;
    }


    // Meant to be called only from within the engine
    void resetAsCloneOf(final DocumentStart original) {

        this.startTimeNanos = original.startTimeNanos;
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;

    }


    // Meant to be called only from within the engine
    static DocumentStart asEngineDocumentStart(final IDocumentStart documentStart, final boolean cloneAlways) {

        if (documentStart instanceof DocumentStart) {
            if (cloneAlways) {
                return ((DocumentStart) documentStart).cloneEvent();
            }
            return (DocumentStart) documentStart;
        }

        final DocumentStart newInstance = new DocumentStart();
        newInstance.startTimeNanos = documentStart.getStartTimeNanos();
        newInstance.templateName = documentStart.getTemplateName();
        newInstance.line = documentStart.getLine();
        newInstance.col = documentStart.getCol();
        return newInstance;

    }


}

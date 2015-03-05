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


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AbstractTemplateHandler implements ITemplateHandler {


    private ITemplateHandler next = null;


    /**
     * <p>
     *   Create a new instance of this handler, specifying the handler that will be used as next step in the
     *   chain.
     * </p>
     *
     * @param next the next step in the chain.
     */
    protected AbstractTemplateHandler(final ITemplateHandler next) {
        super();
        this.next = next;
    }


    /**
     * <p>
     *   Create a new instance of this handler, not specifying the 'next' handler.
     * </p>
     */
    protected AbstractTemplateHandler() {
        super();
    }


    /**
     * <p>
     *   Set the next handler in the chain, so that events can be (optionally) delegated to it.
     * </p>
     *
     * @param next the next handler in the chain.
     */
    public final void setNext(final ITemplateHandler next) {
        this.next = next;
    }


    /**
     * <p>
     *   Return the next handler in the chain, so that events can be delegated to it.
     * </p>
     *
     * @return the next handler in the chain.
     */
    protected final ITemplateHandler getNext() {
        return this.next;
    }





    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleDocumentStart(startTimeNanos, line, col);

    }


    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);

    }



    public void handleXmlDeclaration(final XMLDeclaration xmlDeclaration) {

        if (this.next == null) {
            return;
        }

        this.next.handleXmlDeclaration(xmlDeclaration);

    }



    public void handleDocType(final DocType docType) {

        if (this.next == null) {
            return;
        }

        this.next.handleDocType(docType);

    }



    public void handleCDATASection(final CDATASection cdataSection) {

        if (this.next == null) {
            return;
        }

        this.next.handleCDATASection(cdataSection);

    }



    public void handleComment(final Comment comment) {

        if (this.next == null) {
            return;
        }

        this.next.handleComment(comment);

    }



    public void handleText(final Text text) {

        if (this.next == null) {
            return;
        }

        this.next.handleText(text);

    }


    public void handleStandaloneElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final boolean minimized,
            final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleStandaloneElement(elementDefinition, elementName, elementAttributes, minimized, line, col);

    }


    public void handleOpenElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleOpenElement(elementDefinition, elementName, elementAttributes, line, col);

    }


    public void handleAutoOpenElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleAutoOpenElement(elementDefinition, elementName, elementAttributes, line, col);

    }


    public void handleCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleCloseElement(elementDefinition, elementName, line, col);

    }


    public void handleAutoCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleAutoCloseElement(elementDefinition, elementName, line, col);

    }


    public void handleUnmatchedCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        if (this.next == null) {
            return;
        }

        this.next.handleUnmatchedCloseElement(elementDefinition, elementName, line, col);

    }



    public void handleProcessingInstruction(final ProcessingInstruction processingInstruction) {

        if (this.next == null) {
            return;
        }

        this.next.handleProcessingInstruction(processingInstruction);

    }


}
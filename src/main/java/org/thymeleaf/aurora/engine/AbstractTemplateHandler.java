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


import org.thymeleaf.aurora.context.ITemplateProcessingContext;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AbstractTemplateHandler implements ITemplateHandler {


    private ITemplateHandler next = null;
    private ITemplateProcessingContext templateProcessingContext = null;


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
     * <p>
     *   This method is called when the chain of template handlers is conformed, always before
     *   starting the parsing and processing of a template.
     * </p>
     *
     * @param next the next handler in the chain.
     */
    public final void setNext(final ITemplateHandler next) {
        this.next = next;
    }


    /**
     * <p>
     *   Set the processing context to be used, including template name, context, and also all
     *   kinds of template engine configuration (template engine context).
     * </p>
     * <p>
     *   This method is called always before starting the parsing and processing of a template.
     * </p>
     *
     * @param templateProcessingContext the next handler in the chain.
     */
    public final void setTemplateProcessingContext(final ITemplateProcessingContext templateProcessingContext) {
        Validate.notNull(templateProcessingContext, "Template Processing Context cannot be null");
        this.templateProcessingContext = templateProcessingContext;
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


    /**
     * <p>
     *   Return the template processing context corresponding to the template execution for
     *   which the template handler instance has been created.
     * </p>
     *
     * @return the template processing context
     */
    protected final ITemplateProcessingContext getTemplateProcessingContext() {
        return this.templateProcessingContext;
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



    public void handleXmlDeclaration(final IXMLDeclaration xmlDeclaration) {

        if (this.next == null) {
            return;
        }

        this.next.handleXmlDeclaration(xmlDeclaration);

    }



    public void handleDocType(final IDocType docType) {

        if (this.next == null) {
            return;
        }

        this.next.handleDocType(docType);

    }



    public void handleCDATASection(final ICDATASection cdataSection) {

        if (this.next == null) {
            return;
        }

        this.next.handleCDATASection(cdataSection);

    }



    public void handleComment(final IComment comment) {

        if (this.next == null) {
            return;
        }

        this.next.handleComment(comment);

    }



    public void handleText(final IText text) {

        if (this.next == null) {
            return;
        }

        this.next.handleText(text);

    }


    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleStandaloneElement(standaloneElementTag);

    }


    public void handleOpenElement(final IOpenElementTag openElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleOpenElement(openElementTag);

    }


    public void handleAutoOpenElement(final IOpenElementTag openElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleAutoOpenElement(openElementTag);

    }


    public void handleCloseElement(final ICloseElementTag closeElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleCloseElement(closeElementTag);

    }


    public void handleAutoCloseElement(final ICloseElementTag closeElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleAutoCloseElement(closeElementTag);

    }


    public void handleUnmatchedCloseElement(final ICloseElementTag closeElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleUnmatchedCloseElement(closeElementTag);

    }



    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {

        if (this.next == null) {
            return;
        }

        this.next.handleProcessingInstruction(processingInstruction);

    }


}
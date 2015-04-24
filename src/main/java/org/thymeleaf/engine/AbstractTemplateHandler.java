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


import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.aurora.model.IAutoCloseElementTag;
import org.thymeleaf.aurora.model.IAutoOpenElementTag;
import org.thymeleaf.aurora.model.ICDATASection;
import org.thymeleaf.aurora.model.ICloseElementTag;
import org.thymeleaf.aurora.model.IComment;
import org.thymeleaf.aurora.model.IDocType;
import org.thymeleaf.aurora.model.IOpenElementTag;
import org.thymeleaf.aurora.model.IProcessingInstruction;
import org.thymeleaf.aurora.model.IStandaloneElementTag;
import org.thymeleaf.aurora.model.IText;
import org.thymeleaf.aurora.model.IUnmatchedCloseElementTag;
import org.thymeleaf.aurora.model.IXMLDeclaration;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AbstractTemplateHandler implements ITemplateHandler {


    private ITemplateHandler next = null;
    private ITemplateProcessingContext processingContext = null;


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
    public void setNext(final ITemplateHandler next) {
        this.next = next;
    }


    /**
     * <p>
     *   Set the processing context to be used, including template name, context, and also the
     *   engine configuration.
     * </p>
     * <p>
     *   This method is called always before starting the parsing and processing of a template.
     * </p>
     *
     * @param processingContext the processing context.
     */
    public void setProcessingContext(final ITemplateProcessingContext processingContext) {
        Validate.notNull(processingContext, "Processing Context cannot be null");
        this.processingContext = processingContext;
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
     *   Return the processing context corresponding to the template execution for
     *   which the template handler instance has been created.
     * </p>
     *
     * @return the processing context
     */
    protected final ITemplateProcessingContext getProcessingContext() {
        return this.processingContext;
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



    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration) {

        if (this.next == null) {
            return;
        }

        this.next.handleXMLDeclaration(xmlDeclaration);

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


    public void handleAutoOpenElement(final IAutoOpenElementTag autoOpenElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleAutoOpenElement(autoOpenElementTag);

    }


    public void handleCloseElement(final ICloseElementTag closeElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleCloseElement(closeElementTag);

    }


    public void handleAutoCloseElement(final IAutoCloseElementTag autoCloseElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleAutoCloseElement(autoCloseElementTag);

    }


    public void handleUnmatchedCloseElement(final IUnmatchedCloseElementTag unmatchedCloseElementTag) {

        if (this.next == null) {
            return;
        }

        this.next.handleUnmatchedCloseElement(unmatchedCloseElementTag);

    }



    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {

        if (this.next == null) {
            return;
        }

        this.next.handleProcessingInstruction(processingInstruction);

    }


}
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
public abstract class AbstractChainedTemplateHandler
            extends AbstractTemplateHandler {


    private final ITemplateHandler next;


    /**
     * <p>
     *   Create a new instance of this handler, specifying the handler that will be used as next step in the
     *   chain.
     * </p>
     *
     * @param next the next step in the chain.
     */
    protected AbstractChainedTemplateHandler(final ITemplateHandler next) {
        super();
        if (next == null) {
            throw new IllegalArgumentException("Next handler cannot be null");
        }
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
        this.next.handleDocumentStart(startTimeNanos, line, col);
    }


    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col) {
        this.next.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }



    public void handleXmlDeclaration(
            final String xmlDeclaration,
            final String keyword,
            final String version,
            final String encoding,
            final String standalone,
            final int line, final int col) {
        this.next.handleXmlDeclaration(
                xmlDeclaration, keyword, version, encoding, standalone, line, col);
    }



    public void handleDocType(
            final String docType,
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset,
            final int line, final int col) {
        this.next.handleDocType(
                docType, keyword, elementName, type, publicId, systemId, internalSubset, line, col);
    }



    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        this.next.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }



    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        this.next.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }



    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col) {
        this.next.handleText(buffer, offset, len, line, col);
    }


    public void handleStandaloneElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final boolean minimized, final int line, final int col) {
        this.next.handleStandaloneElementStart(elementDefinition, name, minimized, line, col);
    }

    public void handleStandaloneElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final boolean minimized, final int line, final int col) {
        this.next.handleStandaloneElementEnd(elementDefinition, name, minimized, line, col);
    }

    

    public void handleOpenElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleOpenElementStart(elementDefinition, name, line, col);
    }

    public void handleOpenElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleOpenElementEnd(elementDefinition, name, line, col);
    }



    public void handleAutoOpenElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleAutoOpenElementStart(elementDefinition, name, line, col);
    }

    public void handleAutoOpenElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleAutoOpenElementEnd(elementDefinition, name, line, col);
    }


    
    public void handleCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleCloseElementStart(elementDefinition, name, line, col);
    }

    public void handleCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleCloseElementEnd(elementDefinition, name, line, col);
    }


    
    public void handleAutoCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleAutoCloseElementStart(elementDefinition, name, line, col);
    }

    public void handleAutoCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleAutoCloseElementEnd(elementDefinition, name, line, col);
    }
    

    
    public void handleUnmatchedCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleUnmatchedCloseElementStart(elementDefinition, name, line, col);
    }


    public void handleUnmatchedCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        this.next.handleUnmatchedCloseElementEnd(elementDefinition, name, line, col);
    }


    
    public void handleAttribute(
            final AttributeDefinition attributeDefinition,
            final String name,
            final String operator,
            final String value,
            final AttributeValueQuoting quoting,
            final int line, final int col) {
        this.next.handleAttribute(attributeDefinition, name, operator, value, quoting, line, col);
    }


    
    public void handleInnerWhiteSpace(
            final String whiteSpace,
            final int line, final int col) {
        this.next.handleInnerWhiteSpace(whiteSpace, line, col);
    }



    public void handleProcessingInstruction(
            final String processingInstruction,
            final String target,
            final String content,
            final int line, final int col) {
        this.next.handleProcessingInstruction(
                processingInstruction, target, content, line, col);
    }


}
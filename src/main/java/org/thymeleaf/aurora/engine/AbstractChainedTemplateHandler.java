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
            final char[] buffer,
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int versionOffset, final int versionLen,
            final int versionLine, final int versionCol,
            final int encodingOffset, final int encodingLen,
            final int encodingLine, final int encodingCol,
            final int standaloneOffset, final int standaloneLen,
            final int standaloneLine, final int standaloneCol,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        this.next.handleXmlDeclaration(
                buffer,
                keywordOffset, keywordLen, keywordLine, keywordCol,
                versionOffset, versionLen, versionLine, versionCol,
                encodingOffset, encodingLen, encodingLine, encodingCol,
                standaloneOffset, standaloneLen, standaloneLine, standaloneCol,
                outerOffset, outerLen, line, col);
    }



    public void handleDocType(
            final char[] buffer,
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int elementNameOffset, final int elementNameLen,
            final int elementNameLine, final int elementNameCol,
            final int typeOffset, final int typeLen,
            final int typeLine, final int typeCol,
            final int publicIdOffset, final int publicIdLen,
            final int publicIdLine, final int publicIdCol,
            final int systemIdOffset, final int systemIdLen,
            final int systemIdLine, final int systemIdCol,
            final int internalSubsetOffset, final int internalSubsetLen,
            final int internalSubsetLine, final int internalSubsetCol,
            final int outerOffset, final int outerLen,
            final int outerLine, final int outerCol) {
        this.next.handleDocType(
                buffer,
                keywordOffset, keywordLen, keywordLine, keywordCol,
                elementNameOffset, elementNameLen, elementNameLine, elementNameCol,
                typeOffset, typeLen, typeLine, typeCol,
                publicIdOffset, publicIdLen, publicIdLine, publicIdCol,
                systemIdOffset, systemIdLen, systemIdLine, systemIdCol,
                internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol,
                outerOffset, outerLen, outerLine, outerCol);
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
            final char[] buffer,
            final int targetOffset, final int targetLen,
            final int targetLine, final int targetCol,
            final int contentOffset, final int contentLen,
            final int contentLine, final int contentCol,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        this.next.handleProcessingInstruction(
                buffer,
                targetOffset, targetLen, targetLine, targetCol,
                contentOffset, contentLen, contentLine, contentCol,
                outerOffset, outerLen, line, col);
    }


}
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
package org.thymeleaf.aurora.parser;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.aurora.engine.AttributeDefinition;
import org.thymeleaf.aurora.engine.AttributeDefinitions;
import org.thymeleaf.aurora.engine.ElementDefinition;
import org.thymeleaf.aurora.engine.ElementDefinitions;
import org.thymeleaf.aurora.engine.ITemplateHandler;
import org.thymeleaf.aurora.text.ITextRepository;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
final class HtmlTemplateAdapterMarkupHandler extends AbstractMarkupHandler {

    private final ITemplateHandler templateHandler;
    private final ITextRepository textRepository;

    private ElementDefinition elementDefinition;
    private AttributeDefinition attributeDefinition;

    
    HtmlTemplateAdapterMarkupHandler(final ITemplateHandler templateHandler, final ITextRepository textRepository) {
        super();
        this.templateHandler = templateHandler;
        this.textRepository = textRepository;
    }



    @Override
    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col)
            throws ParseException {
        this.templateHandler.handleDocumentStart(startTimeNanos, line, col);
    }


    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws ParseException {
        this.templateHandler.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }



    @Override
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
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleXmlDeclaration(
                buffer,
                keywordOffset, keywordLen, keywordLine, keywordCol,
                versionOffset, versionLen, versionLine, versionCol,
                encodingOffset, encodingLen, encodingLine, encodingCol,
                standaloneOffset, standaloneLen, standaloneLine, standaloneCol,
                outerOffset, outerLen, line, col);
    }



    @Override
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
            final int outerLine, final int outerCol)
            throws ParseException {
        this.templateHandler.handleDocType(
                buffer,
                keywordOffset, keywordLen, keywordLine, keywordCol,
                elementNameOffset, elementNameLen, elementNameLine, elementNameCol,
                typeOffset, typeLen, typeLine, typeCol,
                publicIdOffset, publicIdLen, publicIdLine, publicIdCol,
                systemIdOffset, systemIdLen, systemIdLine, systemIdCol,
                internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol,
                outerOffset, outerLen, outerLine, outerCol);
    }



    @Override
    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }



    @Override
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleText(buffer, offset, len, line, col);
    }


    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        this.elementDefinition = ElementDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleStandaloneElementStart(this.elementDefinition, buffer, nameOffset, nameLen, minimized, line, col);

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        this.templateHandler.handleStandaloneElementEnd(this.elementDefinition, buffer, nameOffset, nameLen, minimized, line, col);

    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = ElementDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleOpenElementStart(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }

    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.templateHandler.handleOpenElementEnd(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }



    @Override
    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = ElementDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleAutoOpenElementStart(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }

    @Override
    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.templateHandler.handleAutoOpenElementEnd(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = ElementDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleCloseElementStart(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.templateHandler.handleCloseElementEnd(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }



    @Override
    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = ElementDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleAutoCloseElementStart(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }

    @Override
    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.templateHandler.handleAutoCloseElementEnd(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }



    @Override
    public void handleUnmatchedCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = ElementDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleUnmatchedCloseElementStart(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }


    @Override
    public void handleUnmatchedCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.templateHandler.handleUnmatchedCloseElementEnd(this.elementDefinition, buffer, nameOffset, nameLen, line, col);

    }



    @Override
    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws ParseException {

        this.attributeDefinition = AttributeDefinitions.forHtmlName(buffer, nameOffset, nameLen);
        this.templateHandler.handleAttribute(
                this.attributeDefinition,
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen,
                valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }



    @Override
    public void handleInnerWhiteSpace(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleInnerWhiteSpace(buffer, offset, len, line, col);
    }



    @Override
    public void handleProcessingInstruction(
            final char[] buffer,
            final int targetOffset, final int targetLen,
            final int targetLine, final int targetCol,
            final int contentOffset, final int contentLen,
            final int contentLine, final int contentCol,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleProcessingInstruction(
                buffer,
                targetOffset, targetLen, targetLine, targetCol,
                contentOffset, contentLen, contentLine, contentCol,
                outerOffset, outerLen, line, col);
    }
    
    

}

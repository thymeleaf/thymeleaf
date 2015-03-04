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

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.aurora.text.ITextRepository;
import org.thymeleaf.aurora.text.TextRepositories;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class XmlTemplateHandlerAdapterMarkupHandler extends AbstractMarkupHandler {

    private static final String ATTRIBUTE_EQUALS_OPERATOR = "=";

    private final ITemplateHandler templateHandler;
    private final ITextRepository textRepository;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;

    private ElementDefinition elementDefinition = null;
    private String elementName = null;

    private boolean elementMinimized = false;
    private int elementLine = -1;
    private int elementCol = -1;

    private final Text text;
    private final Comment comment;
    private final CDATASection cdataSection;
    private final DocType docType;
    private final ProcessingInstruction processingInstruction;
    private final XmlDeclaration xmlDeclaration;

    private final ElementAttributes elementAttributes;


    public XmlTemplateHandlerAdapterMarkupHandler(final ITemplateHandler templateHandler,
                                                  final ITextRepository textRepository,
                                                  final ElementDefinitions elementDefinitions,
                                                  final AttributeDefinitions attributeDefinitions) {
        super();

        if (templateHandler == null) {
            throw new IllegalArgumentException("Template handler cannot be null");
        }
        if (elementDefinitions == null) {
            throw new IllegalArgumentException("Element Definitions repository cannot be null");
        }
        if (attributeDefinitions == null) {
            throw new IllegalArgumentException("Attribute Definitions repository cannot be null");
        }

        this.templateHandler = templateHandler;

        // We will default the text repository to a no-cache implementation
        this.textRepository = (textRepository != null? textRepository : TextRepositories.createNoCacheRepository());

        // These cannot be null
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;

        // We will be using these as objectual buffers in order to avoid creating too many objects
        this.text = new Text(this.textRepository);
        this.comment = new Comment(this.textRepository);
        this.cdataSection = new CDATASection(this.textRepository);
        this.docType = new DocType();
        this.processingInstruction = new ProcessingInstruction();
        this.xmlDeclaration = new XmlDeclaration();
        this.elementAttributes = new ElementAttributes(false, this.attributeDefinitions);
        
        
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

        final String fullXmlDeclaration = this.textRepository.getText(buffer, outerOffset, outerLen);
        final String keyword = this.textRepository.getText(buffer, keywordOffset, keywordLen);
        final String version =
                (versionLen == 0? null : this.textRepository.getText(buffer, versionOffset, versionLen));
        final String encoding =
                (encodingLen == 0? null : this.textRepository.getText(buffer, encodingOffset, encodingLen));
        final String standalone =
                (standaloneLen == 0? null : this.textRepository.getText(buffer, standaloneOffset, standaloneLen));

        this.xmlDeclaration.setXmlDeclaration(fullXmlDeclaration, keyword, version, encoding, standalone, line, col);

        this.templateHandler.handleXmlDeclaration(this.xmlDeclaration);

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

        final String fullDocType = this.textRepository.getText(buffer, outerOffset, outerLen);
        final String keyword = this.textRepository.getText(buffer, keywordOffset, keywordLen);
        final String rootElementName = this.textRepository.getText(buffer, elementNameOffset, elementNameLen);
        final String type =
                (typeLen == 0? null : this.textRepository.getText(buffer, typeOffset, typeLen));
        final String publicId =
                (publicIdLen == 0? null : this.textRepository.getText(buffer, publicIdOffset, publicIdLen));
        final String systemId =
                (systemIdLen == 0? null : this.textRepository.getText(buffer, systemIdOffset, systemIdLen));
        final String internalSubset =
                (internalSubsetLen == 0? null : this.textRepository.getText(buffer, internalSubsetOffset, internalSubsetLen));

        this.docType.setDocType(fullDocType, keyword, rootElementName, type, publicId, systemId, internalSubset, outerLine, outerCol);

        this.templateHandler.handleDocType(this.docType);

    }



    @Override
    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.cdataSection.setCDATASection(buffer, outerOffset, outerLen, line, col);
        this.templateHandler.handleCDATASection(this.cdataSection);
    }



    @Override
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.comment.setComment(buffer, outerOffset, outerLen, line, col);
        this.templateHandler.handleComment(this.comment);
    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {
        this.text.setText(buffer, offset, len, line, col);
        this.templateHandler.handleText(this.text);
    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        this.elementDefinition = this.elementDefinitions.forXmlName(buffer, nameOffset, nameLen);
        this.elementName = this.textRepository.getText(buffer, nameOffset,nameLen);
        this.elementAttributes.clearAll();
        this.elementMinimized = minimized;
        this.elementLine = line;
        this.elementCol = col;

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleStandaloneElement(
                this.elementDefinition, this.elementName, this.elementAttributes, this.elementMinimized, this.elementLine, this.elementCol);

        // We could just do nothing else, but we better clean the element buffer so that we make sure no one uses its
        // data in the non-element events that are fired between elements

        this.elementDefinition = null;
        this.elementName = null;
        this.elementAttributes.clearAll();
        this.elementMinimized = false;
        this.elementLine = -1;
        this.elementCol = -1;

    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = this.elementDefinitions.forXmlName(buffer, nameOffset, nameLen);
        this.elementName = this.textRepository.getText(buffer, nameOffset,nameLen);
        this.elementAttributes.clearAll();
        this.elementMinimized = false; // does not apply
        this.elementLine = line;
        this.elementCol = col;

    }

    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleOpenElement(
                this.elementDefinition, this.elementName, this.elementAttributes, this.elementLine, this.elementCol);

        // We could just do nothing else, but we better clean the element buffer so that we make sure no one uses its
        // data in the non-element events that are fired between elements

        this.elementDefinition = null;
        this.elementName = null;
        this.elementAttributes.clearAll();
        this.elementMinimized = false;
        this.elementLine = -1;
        this.elementCol = -1;

    }



    @Override
    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = this.elementDefinitions.forXmlName(buffer, nameOffset, nameLen);
        this.elementName = this.textRepository.getText(buffer, nameOffset,nameLen);
        this.elementAttributes.clearAll();
        this.elementMinimized = false; // does not apply
        this.elementLine = line;
        this.elementCol = col;

    }

    @Override
    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleAutoOpenElement(
                this.elementDefinition, this.elementName, this.elementAttributes, this.elementLine, this.elementCol);

        // We could just do nothing else, but we better clean the element buffer so that we make sure no one uses its
        // data in the non-element events that are fired between elements

        this.elementDefinition = null;
        this.elementName = null;
        this.elementAttributes.clearAll();
        this.elementMinimized = false;
        this.elementLine = -1;
        this.elementCol = -1;

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = this.elementDefinitions.forXmlName(buffer, nameOffset, nameLen);
        this.elementName = this.textRepository.getText(buffer, nameOffset,nameLen);
        this.elementAttributes.clearAll(); // does not apply
        this.elementMinimized = false; // does not apply
        this.elementLine = line;
        this.elementCol = col;

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleCloseElement(
                this.elementDefinition, this.elementName, this.elementLine, this.elementCol);

        // We could just do nothing else, but we better clean the element buffer so that we make sure no one uses its
        // data in the non-element events that are fired between elements

        this.elementDefinition = null;
        this.elementName = null;
        this.elementAttributes.clearAll();
        this.elementMinimized = false;
        this.elementLine = -1;
        this.elementCol = -1;

    }



    @Override
    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = this.elementDefinitions.forXmlName(buffer, nameOffset, nameLen);
        this.elementName = this.textRepository.getText(buffer, nameOffset,nameLen);
        this.elementAttributes.clearAll(); // does not apply
        this.elementMinimized = false; // does not apply
        this.elementLine = line;
        this.elementCol = col;

    }

    @Override
    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleAutoCloseElement(
                this.elementDefinition, this.elementName, this.elementLine, this.elementCol);

        // We could just do nothing else, but we better clean the element buffer so that we make sure no one uses its
        // data in the non-element events that are fired between elements

        this.elementDefinition = null;
        this.elementName = null;
        this.elementAttributes.clearAll();
        this.elementMinimized = false;
        this.elementLine = -1;
        this.elementCol = -1;

    }



    @Override
    public void handleUnmatchedCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.elementDefinition = this.elementDefinitions.forXmlName(buffer, nameOffset, nameLen);
        this.elementName = this.textRepository.getText(buffer, nameOffset,nameLen);
        this.elementAttributes.clearAll(); // does not apply
        this.elementMinimized = false; // does not apply
        this.elementLine = line;
        this.elementCol = col;

    }


    @Override
    public void handleUnmatchedCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleUnmatchedCloseElement(
                this.elementDefinition, this.elementName, this.elementLine, this.elementCol);

        // We could just do nothing else, but we better clean the element buffer so that we make sure no one uses its
        // data in the non-element events that are fired between elements

        this.elementDefinition = null;
        this.elementName = null;
        this.elementAttributes.clearAll();
        this.elementMinimized = false;
        this.elementLine = -1;
        this.elementCol = -1;

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

        final String attributeName = this.textRepository.getText(buffer, nameOffset, nameLen);

        final String attributeOperator =
                (operatorLen > 0 ?
                        (operatorLen == 1 && buffer[operatorOffset] == '=' ?
                                ATTRIBUTE_EQUALS_OPERATOR : // Shortcut for the most common case
                                this.textRepository.getText(buffer, operatorOffset, operatorLen)) :
                        null);

        final String value =
                (attributeOperator != null ?
                        this.textRepository.getText(buffer, valueContentOffset, valueContentLen) :
                        null);

        final ElementAttributes.ValueQuotes valueQuotes;
        if (value == null) {
            valueQuotes = null;
        } else if (valueOuterOffset == valueContentOffset) {
            valueQuotes = ElementAttributes.ValueQuotes.NONE;
        } else if (buffer[valueOuterOffset] == '"') {
            valueQuotes = ElementAttributes.ValueQuotes.DOUBLE;
        } else if (buffer[valueOuterOffset] == '\'') {
            valueQuotes = ElementAttributes.ValueQuotes.SINGLE;
        } else {
            valueQuotes = ElementAttributes.ValueQuotes.NONE;
        }

        this.elementAttributes.setAttribute(attributeName, attributeOperator, value, valueQuotes, nameLine, nameCol, false);

    }



    @Override
    public void handleInnerWhiteSpace(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {

        final String elementWhiteSpace = this.textRepository.getText(buffer, offset,len);

        this.elementAttributes.addInnerWhiteSpace(elementWhiteSpace); // line and col are discarded for white spaces

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

        final String fullProcessingInstruction = this.textRepository.getText(buffer, outerOffset, outerLen);
        final String target = this.textRepository.getText(buffer, targetOffset, targetLen);
        final String content =
                (contentLen == 0? null : this.textRepository.getText(buffer, contentOffset, contentLen));

        this.processingInstruction.setProcessingInstruction(fullProcessingInstruction, target, content, line, col);
        this.templateHandler.handleProcessingInstruction(this.processingInstruction);

    }
    


}

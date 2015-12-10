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

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.text.ITextRepository;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class TemplateHandlerAdapterMarkupHandler extends AbstractMarkupHandler {

    private static final String ATTRIBUTE_EQUALS_OPERATOR = "=";

    private final String templateName;
    private final ITemplateHandler templateHandler;
    private final ITextRepository textRepository;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final TemplateMode templateMode;
    private final int lineOffset;
    private final int colOffset;

    private final TemplateStart templateStart;
    private final TemplateEnd templateEnd;

    private final Text text;
    private final Comment comment;
    private final CDATASection cdataSection;
    private final DocType docType;
    private final ProcessingInstruction processingInstruction;
    private final XMLDeclaration xmlDeclaration;

    private final OpenElementTag openElementTag;
    private final StandaloneElementTag standaloneElementTag;
    private final CloseElementTag closeElementTag;

    private ElementAttributes currentElementAttributes;

    
    public TemplateHandlerAdapterMarkupHandler(final String templateName,
                                               final ITemplateHandler templateHandler,
                                               final ITextRepository textRepository,
                                               final ElementDefinitions elementDefinitions,
                                               final AttributeDefinitions attributeDefinitions,
                                               final TemplateMode templateMode,
                                               final int lineOffset, final int colOffset) {
        super();

        Validate.notNull(templateHandler, "Template handler cannot be null");
        Validate.notNull(textRepository, "Text Repository cannot be null");
        Validate.notNull(elementDefinitions, "Element Definitions repository cannot be null");
        Validate.notNull(attributeDefinitions, "Attribute Definitions repository cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateName = templateName;

        this.templateHandler = templateHandler;

        // We will default the text repository to a no-cache implementation
        this.textRepository = textRepository;

        // These cannot be null
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateMode = templateMode;
        this.lineOffset = (lineOffset > 0 ? lineOffset - 1 : lineOffset); // line n for offset will be line 1 for the newly parsed template
        this.colOffset = (colOffset > 0 ? colOffset - 1 : colOffset); // line n for offset will be line 1 for the newly parsed template

        // We will be using these as objectual buffers in order to avoid creating too many objects
        this.templateStart = new TemplateStart();
        this.templateEnd = new TemplateEnd();

        this.text = new Text(this.textRepository);
        this.comment = new Comment(this.textRepository);
        this.cdataSection = new CDATASection(this.textRepository);
        this.docType = new DocType(this.textRepository);
        this.processingInstruction = new ProcessingInstruction(this.textRepository);
        this.xmlDeclaration = new XMLDeclaration(this.textRepository);

        this.openElementTag = new OpenElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
        this.standaloneElementTag = new StandaloneElementTag(this.templateMode, this.elementDefinitions, this.attributeDefinitions);
        this.closeElementTag = new CloseElementTag(this.templateMode, this.elementDefinitions);

        this.currentElementAttributes = null; // Will change as soon as we start processing an open or standalone tag
        
        
    }



    @Override
    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col)
            throws ParseException {

        this.templateStart.reset(startTimeNanos, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col);
        this.templateHandler.handleTemplateStart(this.templateStart);

    }


    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws ParseException {

        this.templateEnd.reset(endTimeNanos, totalTimeNanos, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col);
        this.templateHandler.handleTemplateEnd(this.templateEnd);

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

        this.xmlDeclaration.reset(fullXmlDeclaration, keyword, version, encoding, standalone, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);

        this.templateHandler.handleXMLDeclaration(this.xmlDeclaration);

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

        this.docType.reset(fullDocType, keyword, rootElementName, type, publicId, systemId, internalSubset, this.templateName, this.lineOffset + outerLine, (outerLine == 1? this.colOffset : 0) + outerCol);

        this.templateHandler.handleDocType(this.docType);

    }



    @Override
    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.cdataSection.reset(buffer, outerOffset, outerLen, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.cdataSection.computeContentFlags();
        this.templateHandler.handleCDATASection(this.cdataSection);
    }



    @Override
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {
        this.comment.reset(buffer, outerOffset, outerLen, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.comment.computeContentFlags();
        this.templateHandler.handleComment(this.comment);
    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {
        this.text.reset(buffer, offset, len, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col);
        this.text.computeContentFlags();
        this.templateHandler.handleText(this.text);
    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        this.standaloneElementTag.reset(
                this.textRepository.getText(buffer, nameOffset, nameLen), false, minimized, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.currentElementAttributes = (ElementAttributes) this.standaloneElementTag.getAttributes();

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        // Precompute the associated processors - this might help performance, especially when using an event cache
        this.standaloneElementTag.precomputeAssociatedProcessors();
        // Call the template handler method with the gathered info
        this.templateHandler.handleStandaloneElement(this.standaloneElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.openElementTag.reset(
                this.textRepository.getText(buffer, nameOffset, nameLen), false, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.currentElementAttributes = (ElementAttributes) this.openElementTag.getAttributes();

    }

    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Precompute the associated processors - this might help performance, especially when using an event cache
        this.openElementTag.precomputeAssociatedProcessors();
        // Call the template handler method with the gathered info
        this.templateHandler.handleOpenElement(this.openElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.openElementTag.reset(
                this.textRepository.getText(buffer, nameOffset, nameLen), true, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.currentElementAttributes = (ElementAttributes) this.openElementTag.getAttributes();

    }

    @Override
    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Precompute the associated processors - this might help performance, especially when using an event cache
        this.openElementTag.precomputeAssociatedProcessors();
        // Call the template handler method with the gathered info
        this.templateHandler.handleOpenElement(this.openElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.closeElementTag.reset(this.textRepository.getText(buffer, nameOffset, nameLen), false, false, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.currentElementAttributes = null;

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleCloseElement(this.closeElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.closeElementTag.reset(this.textRepository.getText(buffer, nameOffset, nameLen), true, false, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.currentElementAttributes = null;

    }

    @Override
    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleCloseElement(this.closeElementTag);
        this.currentElementAttributes = null;

    }



    @Override
    public void handleUnmatchedCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.closeElementTag.reset(this.textRepository.getText(buffer, nameOffset, nameLen), false, true, this.templateName, this.lineOffset + line, (line == 1 ? this.colOffset : 0) + col);
        this.currentElementAttributes = null;

    }


    @Override
    public void handleUnmatchedCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        // Call the template handler method with the gathered info
        this.templateHandler.handleCloseElement(this.closeElementTag);
        this.currentElementAttributes = null;

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

        if (this.currentElementAttributes == null) {
            throw new TemplateProcessingException(
                    "Cannot process: attribute is not related to an open/standalone tag", this.templateName, this.lineOffset + nameLine, (nameLine == 1? this.colOffset : 0) + nameCol);
        }

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

        final IElementAttributes.ValueQuotes valueQuotes;
        if (value == null) {
            valueQuotes = null;
        } else if (valueOuterOffset == valueContentOffset) {
            valueQuotes = IElementAttributes.ValueQuotes.NONE;
        } else if (buffer[valueOuterOffset] == '"') {
            valueQuotes = IElementAttributes.ValueQuotes.DOUBLE;
        } else if (buffer[valueOuterOffset] == '\'') {
            valueQuotes = IElementAttributes.ValueQuotes.SINGLE;
        } else {
            valueQuotes = IElementAttributes.ValueQuotes.NONE;
        }

        // Set the attribute, not allowing auto-whitespace given the parser should be creating the corresponding events
        // for adequately specifying whitespace
        this.currentElementAttributes.setAttribute(
                attributeName, attributeOperator, value, valueQuotes, this.lineOffset + nameLine, (nameLine == 1? this.colOffset : 0) + nameCol, false);

    }



    @Override
    public void handleInnerWhiteSpace(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {

        if (this.currentElementAttributes == null) {
            throw new TemplateProcessingException(
                    "Cannot process: inner whitespace is not related to an open/standalone tag", this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        }

        final String elementWhiteSpace = this.textRepository.getText(buffer, offset, len);

        // We can safely cast here, because we know the specific implementation classes we are using
        // Also note line and col are discarded for white spaces
        this.currentElementAttributes.addInnerWhiteSpace(elementWhiteSpace);

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

        this.processingInstruction.reset(fullProcessingInstruction, target, content, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col);
        this.templateHandler.handleProcessingInstruction(this.processingInstruction);

    }
    


}

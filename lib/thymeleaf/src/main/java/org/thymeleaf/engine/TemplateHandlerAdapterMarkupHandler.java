/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.ArrayList;
import java.util.List;

import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class TemplateHandlerAdapterMarkupHandler extends AbstractMarkupHandler {


    private final String templateName;
    private final ITemplateHandler templateHandler;
    private final ElementDefinitions elementDefinitions;
    private final AttributeDefinitions attributeDefinitions;
    private final TemplateMode templateMode;
    private final int lineOffset;
    private final int colOffset;

    private int currentElementLine = -1;
    private int currentElementCol = -1;
    private final List<Attribute> currentElementAttributes;
    private final List<String> currentElementInnerWhiteSpaces;


    
    public TemplateHandlerAdapterMarkupHandler(final String templateName,
                                               final ITemplateHandler templateHandler,
                                               final ElementDefinitions elementDefinitions,
                                               final AttributeDefinitions attributeDefinitions,
                                               final TemplateMode templateMode,
                                               final int lineOffset, final int colOffset) {
        super();

        Validate.notNull(templateHandler, "Template handler cannot be null");
        Validate.notNull(elementDefinitions, "Element Definitions repository cannot be null");
        Validate.notNull(attributeDefinitions, "Attribute Definitions repository cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");

        this.templateName = templateName;

        this.templateHandler = templateHandler;

        // These cannot be null
        this.elementDefinitions = elementDefinitions;
        this.attributeDefinitions = attributeDefinitions;
        this.templateMode = templateMode;
        this.lineOffset = (lineOffset > 0 ? lineOffset - 1 : lineOffset); // line n for offset will be line 1 for the newly parsed template
        this.colOffset = (colOffset > 0 ? colOffset - 1 : colOffset); // line n for offset will be line 1 for the newly parsed template

        // We will use these for gathering the attributes and/or inner white spaces of elements
        this.currentElementAttributes = new ArrayList<Attribute>(10);
        this.currentElementInnerWhiteSpaces = new ArrayList<String>(10);

    }



    @Override
    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col)
            throws ParseException {
        // The reported times refer to parsing times, and processing a template is more complex, so we'll just ignore the info
        this.templateHandler.handleTemplateStart(TemplateStart.TEMPLATE_START_INSTANCE);
    }


    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws ParseException {
        // The reported times refer to parsing times, and processing a template is more complex, so we'll just ignore the info
        this.templateHandler.handleTemplateEnd(TemplateEnd.TEMPLATE_END_INSTANCE);
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

        final String fullXmlDeclaration = new String(buffer, outerOffset, outerLen);
        final String keyword = new String(buffer, keywordOffset, keywordLen);
        final String version =
                (versionLen == 0? null : new String(buffer, versionOffset, versionLen));
        final String encoding =
                (encodingLen == 0? null : new String(buffer, encodingOffset, encodingLen));
        final String standalone =
                (standaloneLen == 0? null : new String(buffer, standaloneOffset, standaloneLen));

        this.templateHandler.handleXMLDeclaration(
                new XMLDeclaration(
                        fullXmlDeclaration, keyword, version, encoding, standalone,
                        this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));

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

        final String fullDocType = new String(buffer, outerOffset, outerLen);
        final String keyword = new String(buffer, keywordOffset, keywordLen);
        final String rootElementName = new String(buffer, elementNameOffset, elementNameLen);
        final String publicId = (publicIdLen == 0? null : new String(buffer, publicIdOffset, publicIdLen));
        final String systemId = (systemIdLen == 0? null : new String(buffer, systemIdOffset, systemIdLen));
        final String internalSubset = (internalSubsetLen == 0? null : new String(buffer, internalSubsetOffset, internalSubsetLen));

        this.templateHandler.handleDocType(
                new DocType(
                        fullDocType, keyword, rootElementName, publicId, systemId, internalSubset,
                        this.templateName, this.lineOffset + outerLine, (outerLine == 1? this.colOffset : 0) + outerCol));

    }



    @Override
    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {

        final String prefix = new String(buffer, outerOffset, (contentOffset - outerOffset));
        final String content = new String(buffer, contentOffset, contentLen);
        final String suffix = new String(buffer, contentOffset + contentLen, (outerOffset + outerLen) - (contentOffset + contentLen));

        this.templateHandler.handleCDATASection(
                new CDATASection(prefix, content, suffix, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));

    }



    @Override
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws ParseException {

        final String prefix = new String(buffer, outerOffset, (contentOffset - outerOffset));
        final String content = new String(buffer, contentOffset, contentLen);
        final String suffix = new String(buffer, contentOffset + contentLen, (outerOffset + outerLen) - (contentOffset + contentLen));

        this.templateHandler.handleComment(
                new Comment(prefix, content, suffix, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));

    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {
        this.templateHandler.handleText(
                new Text(new String(buffer, offset, len), this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));
    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws ParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final Attributes attributes;
        if (this.currentElementAttributes.isEmpty() && this.currentElementInnerWhiteSpaces.isEmpty()) {
            attributes = null;
        } else {
            final Attribute[] attributesArr =
                    (this.currentElementAttributes.isEmpty()?
                            Attributes.EMPTY_ATTRIBUTE_ARRAY : this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]));
            final String[] innerWhiteSpaces = this.currentElementInnerWhiteSpaces.toArray(new String[this.currentElementInnerWhiteSpaces.size()]);
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }

        this.templateHandler.handleStandaloneElement(
                new StandaloneElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, attributes, false, minimized,
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

    }



    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();

    }

    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final Attributes attributes;
        if (this.currentElementAttributes.isEmpty() && this.currentElementInnerWhiteSpaces.isEmpty()) {
            attributes = null;
        } else {
            final Attribute[] attributesArr =
                    (this.currentElementAttributes.isEmpty()?
                            Attributes.EMPTY_ATTRIBUTE_ARRAY : this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]));
            final String[] innerWhiteSpaces = this.currentElementInnerWhiteSpaces.toArray(new String[this.currentElementInnerWhiteSpaces.size()]);
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }

        this.templateHandler.handleOpenElement(
                new OpenElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, attributes, false,
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

    }



    @Override
    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();

    }

    @Override
    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final Attributes attributes;
        if (this.currentElementAttributes.isEmpty() && this.currentElementInnerWhiteSpaces.isEmpty()) {
            attributes = null;
        } else {
            final Attribute[] attributesArr =
                    (this.currentElementAttributes.isEmpty()?
                            Attributes.EMPTY_ATTRIBUTE_ARRAY : this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]));
            final String[] innerWhiteSpaces = this.currentElementInnerWhiteSpaces.toArray(new String[this.currentElementInnerWhiteSpaces.size()]);
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }

        this.templateHandler.handleOpenElement(
                new OpenElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, attributes, true, // synthetic = true
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final String trailingWhiteSpace;
        if (this.currentElementInnerWhiteSpaces.isEmpty()) {
            trailingWhiteSpace = null;
        } else {
            trailingWhiteSpace = this.currentElementInnerWhiteSpaces.get(0);
        }

        this.templateHandler.handleCloseElement(
                new CloseElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, trailingWhiteSpace, false, false,
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

    }



    @Override
    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();

    }

    @Override
    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final String trailingWhiteSpace;
        if (this.currentElementInnerWhiteSpaces.isEmpty()) {
            trailingWhiteSpace = null;
        } else {
            trailingWhiteSpace = this.currentElementInnerWhiteSpaces.get(0);
        }

        this.templateHandler.handleCloseElement(
                new CloseElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, trailingWhiteSpace, true, false,
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

    }



    @Override
    public void handleUnmatchedCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();
        this.currentElementInnerWhiteSpaces.clear();

    }


    @Override
    public void handleUnmatchedCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final String trailingWhiteSpace;
        if (this.currentElementInnerWhiteSpaces.isEmpty()) {
            trailingWhiteSpace = null;
        } else {
            trailingWhiteSpace = this.currentElementInnerWhiteSpaces.get(0);
        }

        this.templateHandler.handleCloseElement(
                new CloseElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, trailingWhiteSpace, false, true,
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

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

        final String attributeName = new String(buffer, nameOffset, nameLen);

        final AttributeDefinition attributeDefinition = this.attributeDefinitions.forName(this.templateMode, attributeName);

        final String attributeOperator =
                (operatorLen > 0 ?
                        (operatorLen == 1 && buffer[operatorOffset] == '=' ?
                                Attribute.DEFAULT_OPERATOR : // Shortcut for the most common case
                                new String(buffer, operatorOffset, operatorLen)) :
                        null);

        final String value =
                (attributeOperator != null ? new String(buffer, valueContentOffset, valueContentLen) : null);

        final AttributeValueQuotes valueQuotes;
        if (value == null) {
            valueQuotes = null;
        } else if (valueOuterOffset == valueContentOffset) {
            valueQuotes = AttributeValueQuotes.NONE;
        } else if (buffer[valueOuterOffset] == '"') {
            valueQuotes = AttributeValueQuotes.DOUBLE;
        } else if (buffer[valueOuterOffset] == '\'') {
            valueQuotes = AttributeValueQuotes.SINGLE;
        } else {
            valueQuotes = AttributeValueQuotes.NONE;
        }

        final Attribute newAttribute =
                new Attribute(
                        attributeDefinition, attributeName, attributeOperator, value, valueQuotes,
                        this.templateName, this.lineOffset + nameLine, (nameLine == 1? this.colOffset : 0) + nameCol);

        this.currentElementAttributes.add(newAttribute);

        // Just in case we are adding attributes without an inner whitespace in between, we will synthetically add it here
        if (this.currentElementInnerWhiteSpaces.size() < this.currentElementAttributes.size()) {
            this.currentElementInnerWhiteSpaces.add("");
        }

    }



    @Override
    public void handleInnerWhiteSpace(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {

        final String elementWhiteSpace;
        if (len == 1 && buffer[offset] == ' ') {
            elementWhiteSpace = Attributes.DEFAULT_WHITE_SPACE;
        } else {
            elementWhiteSpace = new String(buffer, offset, len);
        }

        this.currentElementInnerWhiteSpaces.add(elementWhiteSpace);

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

        final String fullProcessingInstruction = new String(buffer, outerOffset, outerLen);
        final String target = new String(buffer, targetOffset, targetLen);
        final String content = (contentLen == 0? null : new String(buffer, contentOffset, contentLen));

        this.templateHandler.handleProcessingInstruction(
                new ProcessingInstruction(fullProcessingInstruction, target, content, this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));

    }



}

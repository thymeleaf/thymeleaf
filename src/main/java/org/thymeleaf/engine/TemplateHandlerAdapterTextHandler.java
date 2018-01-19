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
import java.util.Arrays;
import java.util.List;

import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.text.AbstractTextHandler;
import org.thymeleaf.templateparser.text.TextParseException;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class TemplateHandlerAdapterTextHandler extends AbstractTextHandler {

    private static final String[][] SYNTHETIC_INNER_WHITESPACES = new String[][] {
            new String[0],
            Attributes.DEFAULT_WHITE_SPACE_ARRAY,
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE },
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE },
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE },
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE }
    };

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


    public TemplateHandlerAdapterTextHandler(final String templateName,
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

    }



    @Override
    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col)
            throws TextParseException {
        // The reported times refer to parsing times, and processing a template is more complex, so we'll just ignore the info
        this.templateHandler.handleTemplateStart(TemplateStart.TEMPLATE_START_INSTANCE);
    }


    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws TextParseException {
        // The reported times refer to parsing times, and processing a template is more complex, so we'll just ignore the info
        this.templateHandler.handleTemplateEnd(TemplateEnd.TEMPLATE_END_INSTANCE);
    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {
        this.templateHandler.handleText(
                new Text(new String(buffer, offset, len), this.templateName, this.lineOffset + line, (line == 1? this.colOffset : 0) + col));
    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws TextParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();

    }

    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws TextParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final Attributes attributes;
        if (this.currentElementAttributes.isEmpty()) {
            attributes = null;
        } else {
            final Attribute[] attributesArr =
                    (this.currentElementAttributes.isEmpty()?
                            Attributes.EMPTY_ATTRIBUTE_ARRAY : this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]));
            final String[] innerWhiteSpaces;
            if (attributesArr.length < SYNTHETIC_INNER_WHITESPACES.length) {
                innerWhiteSpaces = SYNTHETIC_INNER_WHITESPACES[attributesArr.length];
            } else {
                innerWhiteSpaces = new String[attributesArr.length];
                Arrays.fill(innerWhiteSpaces, Attributes.DEFAULT_WHITE_SPACE);
            }
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
            throws TextParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();

    }

    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        final Attributes attributes;
        if (this.currentElementAttributes.isEmpty()) {
            attributes = null;
        } else {
            final Attribute[] attributesArr =
                    (this.currentElementAttributes.isEmpty()?
                            Attributes.EMPTY_ATTRIBUTE_ARRAY : this.currentElementAttributes.toArray(new Attribute[this.currentElementAttributes.size()]));
            final String[] innerWhiteSpaces;
            if (attributesArr.length < SYNTHETIC_INNER_WHITESPACES.length) {
                innerWhiteSpaces = SYNTHETIC_INNER_WHITESPACES[attributesArr.length];
            } else {
                innerWhiteSpaces = new String[attributesArr.length];
                Arrays.fill(innerWhiteSpaces, Attributes.DEFAULT_WHITE_SPACE);
            }
            attributes = new Attributes(attributesArr, innerWhiteSpaces);
        }

        this.templateHandler.handleOpenElement(
                new OpenElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, attributes, false,
                        this.templateName, this.lineOffset + this.currentElementLine, (this.currentElementLine == 1? this.colOffset : 0) + this.currentElementCol));

    }



    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.currentElementLine = line;
        this.currentElementCol = col;
        this.currentElementAttributes.clear();

    }

    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        final String elementCompleteName = new String(buffer, nameOffset, nameLen);
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementCompleteName);

        this.templateHandler.handleCloseElement(
                new CloseElementTag(
                        this.templateMode, elementDefinition, elementCompleteName, null, false, false,
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
            throws TextParseException {

        final String attributeName = new String(buffer, nameOffset, nameLen);

        final AttributeDefinition attributeDefinition = this.attributeDefinitions.forName(this.templateMode, attributeName);

        final String attributeOperator =
                (operatorLen > 0 ?
                        (operatorLen == 1 && buffer[operatorOffset] == '=' ?
                                Attribute.DEFAULT_OPERATOR : // Shortcut for the most common case
                                new String(buffer, operatorOffset, operatorLen)) :
                        null);

        final String value =
                (attributeOperator != null ?
                        new String(buffer, valueContentOffset, valueContentLen) :
                        null);

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

    }



}

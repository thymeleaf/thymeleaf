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
package org.thymeleaf.engine.markup.handler;

import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
final class SelectorElementBuffer {

    private static final int DEFAULT_ELEMENT_NAME_SIZE = 10;
    private static final int DEFAULT_ATTRIBUTES_SIZE = 8;
    private static final int DEFAULT_ATTRIBUTES_INC = 4;
    private static final int DEFAULT_ATTRIBUTE_BUFFER_SIZE = 40;
    private static final int DEFAULT_INNER_WHITE_SPACE_BUFFER_SIZE = 1;

    String documentName;

    boolean standalone;
    boolean minimized;

    String normalizedElementName;
    char[] elementName;
    int elementNameLen;

    int elementNameLine;
    int elementNameCol;

    int elementEndLine;
    int elementEndCol;

    int attributeCount;

    char[][] attributeBuffers;

    int[] attributeNameLens;
    int[] attributeOperatorLens;
    int[] attributeValueContentOffsets;
    int[] attributeValueContentLens;
    int[] attributeValueOuterLens;

    int[] attributeNameLines;
    int[] attributeNameCols;

    int[] attributeOperatorLines;
    int[] attributeOperatorCols;

    int[] attributeValueLines;
    int[] attributeValueCols;

    int elementInnerWhiteSpaceCount;

    char[][] elementInnerWhiteSpaceBuffers;
    int[] elementInnerWhiteSpaceLens;

    int[] elementInnerWhiteSpaceLines;
    int[] elementInnerWhiteSpaceCols;


    SelectorElementBuffer() {

        super();


        this.documentName = null;

        this.standalone = false;
        this.minimized = false;

        this.normalizedElementName = null;
        this.elementName = new char[DEFAULT_ELEMENT_NAME_SIZE];
        this.elementNameLen = 0;

        this.elementNameLine = 0;
        this.elementNameCol = 0;

        this.elementEndLine = 0;
        this.elementEndCol = 0;


        this.attributeCount = 0;

        this.attributeBuffers = new char[DEFAULT_ATTRIBUTES_SIZE][];
        Arrays.fill(this.attributeBuffers, null);

        this.attributeNameLens = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeNameLens, 0);

        this.attributeOperatorLens = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeOperatorLens, 0);

        this.attributeValueContentOffsets = new int[DEFAULT_ATTRIBUTES_SIZE];
        this.attributeValueContentLens = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeValueContentOffsets, 0);
        Arrays.fill(this.attributeValueContentLens, 0);

        this.attributeValueOuterLens = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeValueOuterLens, 0);

        this.attributeNameLines = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeNameLines, 0);
        this.attributeNameCols = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeNameCols, 0);

        this.attributeOperatorLines = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeOperatorLines, 0);
        this.attributeOperatorCols = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeOperatorCols, 0);

        this.attributeValueLines = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeValueLines, 0);
        this.attributeValueCols = new int[DEFAULT_ATTRIBUTES_SIZE];
        Arrays.fill(this.attributeValueCols, 0);


        this.elementInnerWhiteSpaceCount = 0;

        this.elementInnerWhiteSpaceBuffers = new char[DEFAULT_ATTRIBUTES_SIZE + 1][];
        Arrays.fill(this.elementInnerWhiteSpaceBuffers, null);

        this.elementInnerWhiteSpaceLens = new int[DEFAULT_ATTRIBUTES_SIZE + 1];
        Arrays.fill(this.elementInnerWhiteSpaceLens, 0);

        this.elementInnerWhiteSpaceLines = new int[DEFAULT_ATTRIBUTES_SIZE + 1];
        Arrays.fill(this.elementInnerWhiteSpaceLines, 0);
        this.elementInnerWhiteSpaceCols = new int[DEFAULT_ATTRIBUTES_SIZE + 1];
        Arrays.fill(this.elementInnerWhiteSpaceCols, 0);

    }


    void bufferElementStart(final String normalizedName, final char[] buffer, final int offset, final int len,
                            final String documentName, final int line, final int col,
                            final boolean standalone, final boolean minimized) {

        this.documentName = documentName;
        this.normalizedElementName = normalizedName;

        if (len > this.elementName.length) {
            this.elementName = new char[len]; // We just discarding the old char[] is fine
        }
        System.arraycopy(buffer, offset, this.elementName, 0, len);
        this.elementNameLen = len;

        this.elementNameLine = line;
        this.elementNameCol = col;

        this.elementEndLine = 0;
        this.elementEndCol = 0;

        this.standalone = standalone;
        this.minimized = minimized;

        this.attributeCount = 0;
        this.elementInnerWhiteSpaceCount = 0;

    }


    void bufferAttribute(final char[] buffer,
                         final int nameOffset, final int nameLen,
                         final int nameLine, final int nameCol,
                         final int operatorOffset, final int operatorLen,
                         final int operatorLine, final int operatorCol,
                         final int valueContentOffset, final int valueContentLen,
                         final int valueOuterOffset, final int valueOuterLen,
                         final int valueLine, final int valueCol, final String documentName) {

        if (this.attributeCount >= this.attributeBuffers.length) {
            // We've reached the max number of attributes currently allowed in the structure, so we must grow

            final char[][] newAttributeBuffers = new char[this.attributeCount + DEFAULT_ATTRIBUTES_INC][];
            Arrays.fill(newAttributeBuffers, null);
            System.arraycopy(this.attributeBuffers, 0, newAttributeBuffers, 0, this.attributeCount);
            this.attributeBuffers = newAttributeBuffers;

            final int[] newAttributeNameLens = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            Arrays.fill(newAttributeNameLens, 0);
            System.arraycopy(this.attributeNameLens, 0, newAttributeNameLens, 0, this.attributeCount);
            this.attributeNameLens = newAttributeNameLens;

            final int[] newAttributeOperatorLens = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            Arrays.fill(newAttributeOperatorLens, 0);
            System.arraycopy(this.attributeOperatorLens, 0, newAttributeOperatorLens, 0, this.attributeCount);
            this.attributeOperatorLens = newAttributeOperatorLens;

            final int[] newAttributeValueContentOffsets = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            final int[] newAttributeValueContentLens = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            Arrays.fill(newAttributeValueContentOffsets, 0);
            Arrays.fill(newAttributeValueContentLens, 0);
            System.arraycopy(this.attributeValueContentOffsets, 0, newAttributeValueContentOffsets, 0, this.attributeCount);
            System.arraycopy(this.attributeValueContentLens, 0, newAttributeValueContentLens, 0, this.attributeCount);
            this.attributeValueContentOffsets = newAttributeValueContentOffsets;
            this.attributeValueContentLens = newAttributeValueContentLens;

            final int[] newAttributeValueOuterLens = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            Arrays.fill(newAttributeValueOuterLens, 0);
            System.arraycopy(this.attributeValueOuterLens, 0, newAttributeValueOuterLens, 0, this.attributeCount);
            this.attributeValueOuterLens = newAttributeValueOuterLens;

            final int[] newAttributeNameLines = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            final int[] newAttributeNameCols = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            System.arraycopy(this.attributeNameLines, 0, newAttributeNameLines, 0, this.attributeCount);
            System.arraycopy(this.attributeNameCols, 0, newAttributeNameCols, 0, this.attributeCount);
            this.attributeNameLines = newAttributeNameLines;
            this.attributeNameCols = newAttributeNameCols;

            final int[] newAttributeOperatorLines = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            final int[] newAttributeOperatorCols = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            System.arraycopy(this.attributeOperatorLines, 0, newAttributeOperatorLines, 0, this.attributeCount);
            System.arraycopy(this.attributeOperatorCols, 0, newAttributeOperatorCols, 0, this.attributeCount);
            this.attributeOperatorLines = newAttributeOperatorLines;
            this.attributeOperatorCols = newAttributeOperatorCols;

            final int[] newAttributeValueLines = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            final int[] newAttributeValueCols = new int[this.attributeCount + DEFAULT_ATTRIBUTES_INC];
            System.arraycopy(this.attributeValueLines, 0, newAttributeValueLines, 0, this.attributeCount);
            System.arraycopy(this.attributeValueCols, 0, newAttributeValueCols, 0, this.attributeCount);
            this.attributeValueLines = newAttributeValueLines;
            this.attributeValueCols = newAttributeValueCols;

        }

        final int requiredLen = nameLen + operatorLen + valueOuterLen;

        if (this.attributeBuffers[this.attributeCount] == null ||
                this.attributeBuffers[this.attributeCount].length < requiredLen) {
            // The current buffer for attribute texts hasn't been created yet, or is too small
            this.attributeBuffers[this.attributeCount] = new char[Math.max(requiredLen, DEFAULT_ATTRIBUTE_BUFFER_SIZE)];
        }


        // We check if the entire attribute (name, operator, value) comes in the buffer as a whole block
        final boolean isContinuous =
                (nameOffset + nameLen == operatorOffset) &&
                (operatorOffset + operatorLen == valueOuterOffset) &&
                (valueOuterOffset <= valueContentOffset && (valueOuterOffset + valueOuterLen) >= (valueContentOffset + valueContentLen));

        if (isContinuous) {
            System.arraycopy(buffer, nameOffset,       this.attributeBuffers[this.attributeCount], 0,                     requiredLen);
        } else {
            System.arraycopy(buffer, nameOffset,       this.attributeBuffers[this.attributeCount], 0,                     nameLen);
            System.arraycopy(buffer, operatorOffset,   this.attributeBuffers[this.attributeCount], nameLen,               operatorLen);
            System.arraycopy(buffer, valueOuterOffset, this.attributeBuffers[this.attributeCount], nameLen + operatorLen, valueOuterLen);
        }

        this.attributeNameLens[this.attributeCount] = nameLen;
        this.attributeOperatorLens[this.attributeCount] = operatorLen;
        // valueContentOffset is computed for the structure buffer (not the original buffer that came from parsing)
        this.attributeValueContentOffsets[this.attributeCount] = (nameLen + operatorLen) + (valueContentOffset - valueOuterOffset);
        this.attributeValueContentLens[this.attributeCount] = valueContentLen;
        this.attributeValueOuterLens[this.attributeCount] = valueOuterLen;

        this.attributeNameLines[this.attributeCount] = nameLine;
        this.attributeNameCols[this.attributeCount] = nameCol;

        this.attributeOperatorLines[this.attributeCount] = operatorLine;
        this.attributeOperatorCols[this.attributeCount] = operatorCol;

        this.attributeValueLines[this.attributeCount] = valueLine;
        this.attributeValueCols[this.attributeCount] = valueCol;

        this.attributeCount++;

    }


    void bufferElementEnd(final String normalizedName, final char[] buffer, final int offset, final int len,
                          final String documentName, final int line, final int col) {

        this.elementEndLine = line;
        this.elementEndCol = col;

    }


    void bufferElementInnerWhiteSpace(final char[] buffer, final int offset, final int len,
                                      final String documentName, final int line, final int col) {

        if (this.elementInnerWhiteSpaceCount >= this.elementInnerWhiteSpaceBuffers.length) {
            // We've reached the max number of whitespaces currently allowed in the structure, so we must grow

            final char[][] newElementInnerWhiteSpaceBuffers = new char[this.elementInnerWhiteSpaceCount + DEFAULT_ATTRIBUTES_INC][];
            Arrays.fill(newElementInnerWhiteSpaceBuffers, null);
            System.arraycopy(this.elementInnerWhiteSpaceBuffers, 0, newElementInnerWhiteSpaceBuffers, 0, this.elementInnerWhiteSpaceCount);
            this.elementInnerWhiteSpaceBuffers = newElementInnerWhiteSpaceBuffers;

            final int[] newElementInnerWhiteSpaceLines = new int[this.elementInnerWhiteSpaceCount + DEFAULT_ATTRIBUTES_INC];
            final int[] newElementInnerWhiteSpaceCols = new int[this.elementInnerWhiteSpaceCount + DEFAULT_ATTRIBUTES_INC];
            System.arraycopy(this.elementInnerWhiteSpaceLines, 0, newElementInnerWhiteSpaceLines, 0, this.elementInnerWhiteSpaceCount);
            System.arraycopy(this.elementInnerWhiteSpaceCols, 0, newElementInnerWhiteSpaceCols, 0, this.elementInnerWhiteSpaceCount);
            this.elementInnerWhiteSpaceLines = newElementInnerWhiteSpaceLines;
            this.elementInnerWhiteSpaceCols = newElementInnerWhiteSpaceCols;

        }


        if (this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount] == null ||
                this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount].length < len) {
            // The current buffer for attribute texts hasn't been created yet, or is too small
            this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount] = new char[Math.max(len, DEFAULT_INNER_WHITE_SPACE_BUFFER_SIZE)];
        }

        System.arraycopy(buffer, offset, this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount], 0, len);

        this.elementInnerWhiteSpaceLens[this.elementInnerWhiteSpaceCount] = len;

        this.elementInnerWhiteSpaceLines[this.elementInnerWhiteSpaceCount] = line;
        this.elementInnerWhiteSpaceCols[this.elementInnerWhiteSpaceCount] = col;

        this.elementInnerWhiteSpaceCount++;

    }



    void flushSelectedBuffer(
            final ISelectedSelectorEventHandler handler, final IMarkupHandler markupHandler,
            final String[] selectors, final boolean[] selectorMatches) {

        if (this.standalone) {
            handler.onSelectedStandaloneElementStart(
                    selectors, selectorMatches,
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.minimized, this.documentName, this.elementNameLine, this.elementNameCol, markupHandler);
        } else {
            handler.onSelectedOpenElementStart(
                    selectors, selectorMatches,
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.documentName, this.elementNameLine, this.elementNameCol, markupHandler);
        }

        for (int i = 0; i < this.attributeCount; i++) {

            handler.onSelectedElementInnerWhiteSpace(
                    selectors, selectorMatches,
                    this.elementInnerWhiteSpaceBuffers[i],
                    0, this.elementInnerWhiteSpaceLens[i],
                    this.documentName, this.elementInnerWhiteSpaceLines[i], this.elementInnerWhiteSpaceCols[i],
                    markupHandler);

            handler.onSelectedAttribute(
                    selectors, selectorMatches,
                    this.attributeBuffers[i],
                    0, this.attributeNameLens[i], this.attributeNameLines[i], this.attributeNameCols[i],
                    this.attributeNameLens[i], this.attributeOperatorLens[i], this.attributeOperatorLines[i], this.attributeOperatorCols[i],
                    this.attributeValueContentOffsets[i], this.attributeValueContentLens[i],
                    this.attributeNameLens[i] + this.attributeOperatorLens[i], this.attributeValueOuterLens[i],
                    this.attributeValueLines[i], this.attributeValueCols[i],
                    this.documentName,
                    markupHandler);
        }

        if (this.elementInnerWhiteSpaceCount - this.attributeCount > 0) {

            for (int i = this.attributeCount; i < this.elementInnerWhiteSpaceCount; i++) {

                handler.onSelectedElementInnerWhiteSpace(
                        selectors, selectorMatches,
                        this.elementInnerWhiteSpaceBuffers[i],
                        0, this.elementInnerWhiteSpaceLens[i],
                        this.documentName, this.elementInnerWhiteSpaceLines[i], this.elementInnerWhiteSpaceCols[i],
                        markupHandler);

            }

        }

        if (this.standalone) {
            handler.onSelectedStandaloneElementEnd(
                    selectors, selectorMatches,
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.minimized, this.documentName, this.elementEndLine, this.elementEndCol, markupHandler);
        } else {
            handler.onSelectedOpenElementEnd(
                    selectors, selectorMatches,
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.documentName, this.elementEndLine, this.elementEndCol, markupHandler);
        }

    }



    void flushNonSelectedBuffer(final INonSelectedSelectorEventHandler handler, final IMarkupHandler markupHandler) {

        if (this.standalone) {
            handler.onNonSelectedStandaloneElementStart(
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.minimized, this.documentName, this.elementNameLine, this.elementNameCol,
                    markupHandler);
        } else {
            handler.onNonSelectedOpenElementStart(
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.documentName, this.elementNameLine, this.elementNameCol,
                    markupHandler);
        }

        for (int i = 0; i < this.attributeCount; i++) {

            handler.onNonSelectedElementInnerWhiteSpace(
                    this.elementInnerWhiteSpaceBuffers[i],
                    0, this.elementInnerWhiteSpaceLens[i],
                    this.documentName, this.elementInnerWhiteSpaceLines[i], this.elementInnerWhiteSpaceCols[i],
                    markupHandler);

            handler.onNonSelectedAttribute(
                    this.attributeBuffers[i],
                    0, this.attributeNameLens[i], this.attributeNameLines[i], this.attributeNameCols[i],
                    this.attributeNameLens[i], this.attributeOperatorLens[i], this.attributeOperatorLines[i], this.attributeOperatorCols[i],
                    this.attributeValueContentOffsets[i], this.attributeValueContentLens[i],
                    this.attributeNameLens[i] + this.attributeOperatorLens[i], this.attributeValueOuterLens[i],
                    this.attributeValueLines[i], this.attributeValueCols[i],
                    this.documentName,
                    markupHandler);
        }

        if (this.elementInnerWhiteSpaceCount - this.attributeCount > 0) {

            for (int i = this.attributeCount; i < this.elementInnerWhiteSpaceCount; i++) {

                handler.onNonSelectedElementInnerWhiteSpace(
                        this.elementInnerWhiteSpaceBuffers[i],
                        0, this.elementInnerWhiteSpaceLens[i],
                        this.documentName, this.elementInnerWhiteSpaceLines[i], this.elementInnerWhiteSpaceCols[i],
                        markupHandler);

            }

        }

        if (this.standalone) {
            handler.onNonSelectedStandaloneElementEnd(
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.minimized, this.documentName, this.elementEndLine, this.elementEndCol,
                    markupHandler);
        } else {
            handler.onNonSelectedOpenElementEnd(
                    this.normalizedElementName, this.elementName, 0, this.elementNameLen, this.documentName, this.elementEndLine, this.elementEndCol,
                    markupHandler);
        }

    }



}

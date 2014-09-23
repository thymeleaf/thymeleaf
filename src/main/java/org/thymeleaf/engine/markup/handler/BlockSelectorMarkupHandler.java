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
import java.util.List;

import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class BlockSelectorMarkupHandler extends AbstractMarkupHandler {


    private final MarkupSelectorFilter filter;
    private final IMarkupHandler handler;
    private final ElementBuffer elementBuffer;

    private int markupLevel;
    private boolean matching;
    private int matchingMarkupLevel;

    private static final int MARKUP_BLOCKS_LEN = 10;
    private int[] markupBlocks;
    private int markupBlockIndex;



    public BlockSelectorMarkupHandler(final IMarkupHandler handler, final String blockSelector, final boolean caseSensitive,
                                      final IMarkupSelectorReferenceResolver referenceResolver) {

        super();

        Validate.notNull(handler, "Handler cannot be null");
        Validate.notEmpty(blockSelector, "Block selector cannot be null or empty");

        this.handler = handler;

        this.markupLevel = 0;
        this.matching = false;
        this.matchingMarkupLevel = Integer.MAX_VALUE;

        final List<IMarkupSelectorItem> blockSelectorItems =
                MarkupSelectorItems.forSelector(caseSensitive, blockSelector, referenceResolver);

        this.filter = new MarkupSelectorFilter(null, blockSelectorItems.get(0));
        MarkupSelectorFilter last = this.filter;
        for (int i = 1; i < blockSelectorItems.size(); i++) {
            last = new MarkupSelectorFilter(last, blockSelectorItems.get(i));
        }

        this.elementBuffer = new ElementBuffer();

        this.markupBlockIndex = 0;
        this.markupBlocks = new int[MARKUP_BLOCKS_LEN];
        this.markupBlocks[this.markupLevel] = this.markupBlockIndex;

    }





    /*
     * ---------------
     * Document events
     * ---------------
     */

    @Override
    public void onDocumentStart(final long startTimeNanos, final String documentName) {
        this.handler.onDocumentStart(startTimeNanos, documentName);
    }



    @Override
    public void onDocumentEnd(final long endTimeNanos, final long totalTimeNanos, final String documentName) {
        this.handler.onDocumentEnd(endTimeNanos, totalTimeNanos, documentName);
    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    @Override
    public void onXmlDeclaration(
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone,
            final String documentName, final int line, final int col) {

        if (!this.matching &&
                !this.filter.matchXmlDeclaration(this.markupLevel, this.markupBlocks[this.markupLevel], xmlDeclaration, version, encoding, standalone)) {
            // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
            return;
        }

        this.handler.onXmlDeclaration(xmlDeclaration, version, encoding, standalone, documentName, line, col);

    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    @Override
    public void onDocTypeClause(
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId,
            final String documentName, final int line, final int col) {

        if (!this.matching &&
                !this.filter.matchDocTypeClause(this.markupLevel, this.markupBlocks[this.markupLevel], docTypeClause, rootElementName, publicId, systemId)) {
            // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
            return;
        }

        this.handler.onDocTypeClause(docTypeClause, rootElementName, publicId, systemId, documentName, line, col);

    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    @Override
    public void onCDATASection(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching &&
                !this.filter.matchCDATASection(this.markupLevel, this.markupBlocks[this.markupLevel], buffer, offset, len)) {
            // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
            return;
        }

        this.handler.onCDATASection(buffer, offset, len, documentName, line, col);

    }





    /*
     * -----------
     * Text events
     * -----------
     */

    @Override
    public void onText(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching &&
                !this.filter.matchText(this.markupLevel, this.markupBlocks[this.markupLevel], buffer, offset, len)) {
            // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
            return;
        }

        this.handler.onText(buffer, offset, len, documentName, line, col);

    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    @Override
    public void onComment(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching ||
                !this.filter.matchComment(this.markupLevel, this.markupBlocks[this.markupLevel], buffer, offset, len)) {
            // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
            return;
        }

        this.handler.onComment(buffer, offset, len, documentName, line, col);

    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */

    @Override
    public void onAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol, final String documentName) {


        if (!this.matching) {
            // We are not in a matching block, so let's put this attribute into the buffer just in case it matches
            this.elementBuffer.bufferNewAttribute(
                    buffer,
                    nameOffset, nameLen, nameLine, nameCol,
                    operatorOffset, operatorLen, operatorLine, operatorCol,
                    valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol, documentName);
            return;
        }
        

        this.handler.onAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol, documentName);

    }



    @Override
    public void onStandaloneElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized, final String documentName, final int line, final int col) {


        if (!this.matching) {
            // We are not in a matching block, so let's put this element into the buffer just in case it matches
            this.elementBuffer.bufferNewElement(normalizedName, buffer, offset, len, documentName, line, col, true, minimized);
            return;
        }

        this.handler.onStandaloneElementStart(normalizedName, buffer, offset, len, minimized, documentName, line, col);

    }



    @Override
    public void onStandaloneElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized, final String documentName, final int line, final int col) {

        if (!this.matching) {

            if (this.filter.matchStandaloneElement(this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer)) {

                // The element matched! Flush the buffer then, calling all the delayed events...
                this.elementBuffer.flushBuffer(this.handler);

            } else {

                // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
                return;

            }

        }

        this.handler.onStandaloneElementEnd(normalizedName, buffer, offset, len, minimized, documentName, line, col);

    }



    @Override
    public void onOpenElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {
            // We are not in a matching block, so let's put this element into the buffer just in case it matches
            this.elementBuffer.bufferNewElement(normalizedName, buffer, offset, len, documentName, line, col, false, false);
            return;
        }

        this.handler.onOpenElementStart(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onOpenElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {

            if (this.filter.matchOpenElement(this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer)) {

                this.matching = true;
                this.matchingMarkupLevel = this.markupLevel;

                // The element matched! Flush the buffer then, calling all the delayed events...
                this.elementBuffer.flushBuffer(this.handler);

            } else {

                this.markupLevel++;

                checkMarkupLevel(this.markupLevel);
                this.markupBlocks[this.markupLevel] = ++this.markupBlockIndex;

                // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
                return;

            }

        }

        this.markupLevel++;

        checkMarkupLevel(this.markupLevel);
        this.markupBlocks[this.markupLevel] = ++this.markupBlockIndex;

        this.handler.onOpenElementEnd(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.markupLevel--;
        this.filter.removeMatchesForLevel(this.markupLevel);

        if (!this.matching) {
            // If we are not currently matching anything, this event is of no further use.
            return;
        }

        this.handler.onCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {
            // If we are not currently matching anything, this event is of no use.
            return;
        }

        if (this.matchingMarkupLevel == this.markupLevel) {
            this.matching = false;
            this.matchingMarkupLevel = Integer.MAX_VALUE;
        }

        this.handler.onCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onAutoCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.markupLevel--;
        this.filter.removeMatchesForLevel(this.markupLevel);

        if (!this.matching) {
            // If we are not currently matching anything, this event is of no further use.
            return;
        }

        this.handler.onAutoCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onAutoCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {
            // If we are not currently matching anything, this event is of no use.
            return;
        }

        if (this.matchingMarkupLevel == this.markupLevel) {
            this.matching = false;
            this.matchingMarkupLevel = Integer.MAX_VALUE;
        }

        this.handler.onAutoCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onUnmatchedCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {
            // If we are not currently matching anything, this event is of no use.
            return;
        }

        this.handler.onUnmatchedCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onUnmatchedCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {
            // If we are not currently matching anything, this event is of no use.
            return;
        }

        this.handler.onUnmatchedCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col);

    }



    @Override
    public void onElementInnerWhiteSpace(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        if (!this.matching) {
            // We are not in a matching block, so let's put this whitespace into the buffer just in case it matches
            this.elementBuffer.bufferNewElementInnerWhiteSpace(buffer, offset, len, documentName, line, col);
            return;
        }

        this.handler.onElementInnerWhiteSpace(buffer, offset, len, documentName, line, col);

    }





    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    public void onProcessingInstruction(
            final String processingInstruction,
            final String target, final String content,
            final String documentName, final int line, final int col) {

        if (!this.matching &&
                !this.filter.matchProcessingInstruction(this.markupLevel, this.markupBlocks[this.markupLevel], processingInstruction, target, content)) {
            // Nothing to do with this event: it's not in a matching block, and it doesn't match. Just ignore.
            return;
        }

        this.handler.onProcessingInstruction(processingInstruction, target, content, documentName, line, col);

    }



    /*
     * -------------------------------
     * Markup block and level handling
     * -------------------------------
     */

    private void checkMarkupLevel(final int markupLevel) {
        if (markupLevel >= this.markupBlocks.length) {
            final int newLen = Math.max(markupLevel + 1, this.markupBlocks.length + MARKUP_BLOCKS_LEN);
            final int[] newMarkupBlocks = new int[newLen];
            Arrays.fill(newMarkupBlocks, 0);
            System.arraycopy(this.markupBlocks, 0, newMarkupBlocks, 0, this.markupBlocks.length);
            this.markupBlocks = newMarkupBlocks;
        }
    }


}

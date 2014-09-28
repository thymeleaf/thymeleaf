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
public final class NodeSelectorMarkupHandler extends AbstractMarkupHandler {


    private final IMarkupHandler handler;
    private final ISelectedSelectorEventHandler selectedHandler;
    private final INonSelectedSelectorEventHandler nonSelectedHandler;

    private final SelectorElementBuffer elementBuffer;

    private final int selectorsLen;
    private final String[] selectors;
    private final boolean[] selectorMatches;
    private final MarkupSelectorFilter[] selectorFilters;

    private final int[][] matchingMarkupLevelsPerSelector;

    private boolean someSelectorsMatch;


    private int markupLevel;

    private static final int MARKUP_BLOCKS_LEN = 10;
    private int[] markupBlocks;
    private int markupBlockIndex;






    public NodeSelectorMarkupHandler(final IMarkupHandler handler,
                                     final ISelectedSelectorEventHandler selectedEventHandler,
                                     final INonSelectedSelectorEventHandler nonSelectedEventHandler,
                                     final String selector, final MarkupSelectorMode mode) {
        this(handler, selectedEventHandler, nonSelectedEventHandler, new String[] {selector}, mode, null);
    }



    public NodeSelectorMarkupHandler(final IMarkupHandler handler,
                                     final ISelectedSelectorEventHandler selectedEventHandler,
                                     final INonSelectedSelectorEventHandler nonSelectedEventHandler,
                                     final String[] selectors, final MarkupSelectorMode mode) {
        this(handler, selectedEventHandler, nonSelectedEventHandler, selectors, mode, null);
    }



    public NodeSelectorMarkupHandler(final IMarkupHandler handler,
                                     final ISelectedSelectorEventHandler selectedEventHandler,
                                     final INonSelectedSelectorEventHandler nonSelectedEventHandler,
                                     final String selector, final MarkupSelectorMode mode,
                                     final IMarkupSelectorReferenceResolver referenceResolver) {
        this(handler, selectedEventHandler, nonSelectedEventHandler, new String[] {selector}, mode, referenceResolver);
    }



    public NodeSelectorMarkupHandler(final IMarkupHandler handler,
                                     final ISelectedSelectorEventHandler selectedEventHandler,
                                     final INonSelectedSelectorEventHandler nonSelectedEventHandler,
                                     final String[] selectors, final MarkupSelectorMode mode,
                                     final IMarkupSelectorReferenceResolver referenceResolver) {

        super();

        Validate.notNull(handler, "Handler cannot be null");
        Validate.notEmpty(selectors, "Block selectors array cannot be null or empty");
        for (final String selector : selectors) {
            Validate.notEmpty(selector, "Block selectors array contains at least one null or empty item, which is forbidden");
        }

        this.handler = handler;
        this.selectedHandler = selectedEventHandler;
        this.nonSelectedHandler = nonSelectedEventHandler;

        this.selectors = selectors;
        this.selectorsLen = selectors.length;

        // Note this variable is defined basically in order to be reused in different events, but will not be dealt with as "state"
        this.selectorMatches = new boolean[this.selectors.length];
        Arrays.fill(this.selectorMatches, false);

        // Note this variable is defined basically in order to be reused in different events, but will not be dealt with as "state"
        this.someSelectorsMatch = false;

        this.selectorFilters = new MarkupSelectorFilter[this.selectorsLen];
        for (int i = 0; i < this.selectorsLen; i++) {

            final List<IMarkupSelectorItem> selectorItems =
                    MarkupSelectorItems.forSelector(mode, selectors[i], referenceResolver);

            this.selectorFilters[i] = new MarkupSelectorFilter(null, selectorItems.get(0));
            MarkupSelectorFilter last = this.selectorFilters[i];
            for (int j = 1; j < selectorItems.size(); j++) {
                last = new MarkupSelectorFilter(last, selectorItems.get(j));
            }

        }

        this.elementBuffer = new SelectorElementBuffer();

        this.matchingMarkupLevelsPerSelector = new int[this.selectorsLen][];
        Arrays.fill(this.matchingMarkupLevelsPerSelector, null);

        this.markupLevel = 0;

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

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchXmlDeclaration(false, this.markupLevel, this.markupBlocks[this.markupLevel], xmlDeclaration, version, encoding, standalone);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedXmlDeclaration(this.selectors, this.selectorMatches, xmlDeclaration, version, encoding, standalone, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedXmlDeclaration(xmlDeclaration, version, encoding, standalone, documentName, line, col, this.handler);

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

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchDocTypeClause(false, this.markupLevel, this.markupBlocks[this.markupLevel], docTypeClause, rootElementName, publicId, systemId);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedDocTypeClause(this.selectors, this.selectorMatches, docTypeClause, rootElementName, publicId, systemId, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedDocTypeClause(docTypeClause, rootElementName, publicId, systemId, documentName, line, col, this.handler);

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

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchCDATASection(false, this.markupLevel, this.markupBlocks[this.markupLevel], buffer, offset, len);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedCDATASection(this.selectors, this.selectorMatches, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedCDATASection(buffer, offset, len, documentName, line, col, this.handler);

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

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchText(false, this.markupLevel, this.markupBlocks[this.markupLevel], buffer, offset, len);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedText(this.selectors, this.selectorMatches, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedText(buffer, offset, len, documentName, line, col, this.handler);

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

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchComment(false, this.markupLevel, this.markupBlocks[this.markupLevel], buffer, offset, len);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedComment(this.selectors, this.selectorMatches, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedComment(buffer, offset, len, documentName, line, col, this.handler);

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


        this.elementBuffer.bufferAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol, documentName);

    }



    @Override
    public void onStandaloneElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized, final String documentName, final int line, final int col) {

        this.elementBuffer.bufferElementStart(normalizedName, buffer, offset, len, documentName, line, col, true, minimized);

    }



    @Override
    public void onStandaloneElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized, final String documentName, final int line, final int col) {

        this.elementBuffer.bufferElementEnd(normalizedName, buffer, offset, len, documentName, line, col);

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchStandaloneElement(false, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.elementBuffer.flushSelectedBuffer(this.selectedHandler, this.handler, this.selectors, this.selectorMatches);
            return;
        }

        this.elementBuffer.flushNonSelectedBuffer(this.nonSelectedHandler, this.handler);

    }



    @Override
    public void onOpenElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.elementBuffer.bufferElementStart(normalizedName, buffer, offset, len, documentName, line, col, false, false);

    }



    @Override
    public void onOpenElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.elementBuffer.bufferElementEnd(normalizedName, buffer, offset, len, documentName, line, col);

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] =
                    this.selectorFilters[i].matchOpenElement(false, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                addMatchingMarkupLevel(i, this.markupLevel);
            }
        }

        this.markupLevel++;

        checkSizeOfMarkupBlocksStructure(this.markupLevel);
        this.markupBlocks[this.markupLevel] = ++this.markupBlockIndex;

        if (this.someSelectorsMatch) {
            this.elementBuffer.flushSelectedBuffer(this.selectedHandler, this.handler, this.selectors, this.selectorMatches);
            return;
        }

        this.elementBuffer.flushNonSelectedBuffer(this.nonSelectedHandler, this.handler);

    }



    @Override
    public void onCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.markupLevel--;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorFilters[i].removeMatchesForLevel(this.markupLevel);
        }

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            // We use the flags indicating past matches to recompute new ones
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }
        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedCloseElementStart(this.selectors, this.selectorMatches, normalizedName, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col, this.handler);

    }



    @Override
    public void onCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            // We use the flags indicating past matches to recompute new ones
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                removeMatchingMarkupLevel(i, this.markupLevel);
            }
        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedCloseElementEnd(this.selectors, this.selectorMatches, normalizedName, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col, this.handler);

    }



    @Override
    public void onAutoCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.markupLevel--;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorFilters[i].removeMatchesForLevel(this.markupLevel);
        }

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            // We use the flags indicating past matches to recompute new ones
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }
        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedAutoCloseElementStart(this.selectors, this.selectorMatches, normalizedName, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedAutoCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col, this.handler);

    }



    @Override
    public void onAutoCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            // We use the flags indicating past matches to recompute new ones
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                removeMatchingMarkupLevel(i, this.markupLevel);
            }
        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedAutoCloseElementEnd(this.selectors, this.selectorMatches, normalizedName, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedAutoCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col, this.handler);

    }



    @Override
    public void onUnmatchedCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            // We use the flags indicating past matches to recompute new ones
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }
        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedUnmatchedCloseElementStart(this.selectors, this.selectorMatches, normalizedName, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedUnmatchedCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col, this.handler);

    }



    @Override
    public void onUnmatchedCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            // We use the flags indicating past matches to recompute new ones
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }
        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedUnmatchedCloseElementEnd(this.selectors, this.selectorMatches, normalizedName, buffer, offset, len, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedUnmatchedCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col, this.handler);

    }



    @Override
    public void onElementInnerWhiteSpace(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {

        this.elementBuffer.bufferElementInnerWhiteSpace(buffer, offset, len, documentName, line, col);

    }





    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    @Override
    public void onProcessingInstruction(
            final String processingInstruction,
            final String target, final String content,
            final String documentName, final int line, final int col) {

        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {

            this.selectorMatches[i] =
                    this.selectorFilters[i].matchProcessingInstruction(false, this.markupLevel, this.markupBlocks[this.markupLevel], processingInstruction, target, content);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
            }

        }

        if (this.someSelectorsMatch) {
            this.selectedHandler.onSelectedProcessingInstruction(this.selectors, this.selectorMatches, processingInstruction, target, content, documentName, line, col, this.handler);
            return;
        }

        this.nonSelectedHandler.onNonSelectedProcessingInstruction(processingInstruction, target, content, documentName, line, col, this.handler);

    }



    /*
     * -------------------------------
     * Markup block and level handling
     * -------------------------------
     */

    private void checkSizeOfMarkupBlocksStructure(final int markupLevel) {
        if (markupLevel >= this.markupBlocks.length) {
            final int newLen = Math.max(markupLevel + 1, this.markupBlocks.length + MARKUP_BLOCKS_LEN);
            final int[] newMarkupBlocks = new int[newLen];
            Arrays.fill(newMarkupBlocks, 0);
            System.arraycopy(this.markupBlocks, 0, newMarkupBlocks, 0, this.markupBlocks.length);
            this.markupBlocks = newMarkupBlocks;
        }
    }


    private void addMatchingMarkupLevel(final int selector, final int markupLevel) {

        if (this.matchingMarkupLevelsPerSelector[selector] == null) {
            // Structure for this selector didn't exist before: create it
            this.matchingMarkupLevelsPerSelector[selector] = new int[2];
            Arrays.fill(this.matchingMarkupLevelsPerSelector[selector], Integer.MAX_VALUE);
        }

        for (int i = 0; i < this.matchingMarkupLevelsPerSelector[selector].length; i++) {
            if (this.matchingMarkupLevelsPerSelector[selector][i] == Integer.MAX_VALUE) {
                this.matchingMarkupLevelsPerSelector[selector][i] = markupLevel;
                return;
            }
        }

        // Not found an available place: grow the structure
        int[] newMatchingMarkupLevelsPerSelector = new int[this.matchingMarkupLevelsPerSelector[selector].length + 2];
        Arrays.fill(newMatchingMarkupLevelsPerSelector, Integer.MAX_VALUE);
        System.arraycopy(this.matchingMarkupLevelsPerSelector[selector],0,newMatchingMarkupLevelsPerSelector,0,this.matchingMarkupLevelsPerSelector[selector].length);

        newMatchingMarkupLevelsPerSelector[this.matchingMarkupLevelsPerSelector[selector].length] = markupLevel;
        this.matchingMarkupLevelsPerSelector[selector] = newMatchingMarkupLevelsPerSelector;

    }


    private boolean isMatchingMarkupLevel(final int selector, final int markupLevel) {
        if (this.matchingMarkupLevelsPerSelector[selector] == null) {
            return false;
        }
        for (int i = 0; i < this.matchingMarkupLevelsPerSelector[selector].length; i++) {
            if (this.matchingMarkupLevelsPerSelector[selector][i] == markupLevel) {
                return true;
            }
        }
        return false;
    }


    private void removeMatchingMarkupLevel(final int selector, final int markupLevel) {
        for (int i = 0; i < this.matchingMarkupLevelsPerSelector[selector].length; i++) {
            if (this.matchingMarkupLevelsPerSelector[selector][i] == markupLevel) {
                this.matchingMarkupLevelsPerSelector[selector][i] = Integer.MAX_VALUE;
                return;
            }
        }
    }



}

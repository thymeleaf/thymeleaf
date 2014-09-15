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
package org.thymeleaf.engine.markup;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class BlockSelectorMarkupHandler extends AbstractMarkupHandler {


    private final BlockSelectorFilter filter;
    private final IMarkupHandler handler;

    private int markupLevel;
    private boolean matching;
    private int matchingMarkupLevel;



    public BlockSelectorMarkupHandler(final IMarkupHandler handler, final String... elementNames) {

        super();

        this.handler = handler;

        this.filter = new BlockSelectorFilter(null, elementNames[0]);
        BlockSelectorFilter last = this.filter;
        for (int i = 1; i < elementNames.length; i++) {
            last = new BlockSelectorFilter(last, elementNames[i]);
        }

        this.markupLevel = 0;
        this.matching = false;
        this.matchingMarkupLevel = Integer.MAX_VALUE;
        
    }





    /*
     * ---------------
     * Document events
     * ---------------
     */

    @Override
    public void onDocumentStart(final long startTimeNanos) {

        this.handler.onDocumentStart(startTimeNanos);

    }



    @Override
    public void onDocumentEnd(final long endTimeNanos, final long totalTimeNanos) {

        this.handler.onDocumentEnd(endTimeNanos, totalTimeNanos);

    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    @Override
    public void onXmlDeclaration (
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone,
            final int line, final int col) {

        if (this.matching ||
                this.filter.matchXmlDeclaration(0, this.markupLevel, xmlDeclaration, version, encoding, standalone)) {

            this.handler.onXmlDeclaration(xmlDeclaration, version, encoding, standalone, line, col);

        }

    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    @Override
    public void onDocTypeClause (
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId,
            final int line, final int col) {

        if (this.matching ||
                this.filter.matchDocTypeClause(0, this.markupLevel, docTypeClause, rootElementName, publicId, systemId)) {

            this.handler.onDocTypeClause(docTypeClause, rootElementName, publicId, systemId, line, col);

        }

    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    @Override
    public void onCDATASection (
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {

        if (this.matching ||
                this.filter.matchCDATASection(0, this.markupLevel, buffer, offset, len)) {

            this.handler.onCDATASection(buffer, offset, len, line, col);

        }

    }





    /*
     * -----------
     * Text events
     * -----------
     */

    @Override
    public void onText (
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {

        if (this.matching ||
                this.filter.matchText(0, this.markupLevel, buffer, offset, len)) {

            this.handler.onText(buffer, offset, len, line, col);

        }

    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    @Override
    public void onComment (
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {

        if (this.matching ||
                this.filter.matchComment(0, this.markupLevel, buffer, offset, len)) {

            this.handler.onComment(buffer, offset, len, line, col);

        }

    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */

    @Override
    public void onAttribute (
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol) {

        if (this.matching) {

            this.handler.onAttribute(
                    buffer,
                    nameOffset, nameLen, nameLine, nameCol,
                    operatorOffset, operatorLen, operatorLine, operatorCol,
                    valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

        }

    }



    @Override
    public void onStandaloneElementStart(
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {
            this.handler.onStandaloneElementStart(buffer, offset, len, normalizedName, line, col);
            return;
        }

        if (this.filter.matchStandaloneElement(0, this.markupLevel, normalizedName)) {

            this.matching = true;
            this.matchingMarkupLevel = this.markupLevel;

            this.handler.onStandaloneElementStart(buffer, offset, len, normalizedName, line, col);

        }

    }



    @Override
    public void onStandaloneElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {

            this.handler.onStandaloneElementEnd(buffer, offset, len, normalizedName, line, col);

            if (this.matchingMarkupLevel == this.markupLevel) {
                this.matching = false;
                this.matchingMarkupLevel = Integer.MAX_VALUE;
            }

        }

    }



    @Override
    public void onOpenElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {
            this.handler.onOpenElementStart(buffer, offset, len, normalizedName, line, col);
            return;
        }

        if (this.filter.matchOpenElement(0, this.markupLevel, normalizedName)) {

            this.matching = true;
            this.matchingMarkupLevel = this.markupLevel;

            this.handler.onOpenElementStart(buffer, offset, len, normalizedName, line, col);

        }

    }



    @Override
    public void onOpenElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {

            this.handler.onOpenElementEnd(buffer, offset, len, normalizedName, line, col);

        }


        this.markupLevel++;

    }



    @Override
    public void onCloseElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.markupLevel--;
        this.filter.removeMatchesForLevel(this.markupLevel);

        if (this.matching) {
            this.handler.onCloseElementStart(buffer, offset, len, normalizedName, line, col);
        }

    }



    @Override
    public void onCloseElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {

            this.handler.onCloseElementEnd(buffer, offset, len, normalizedName, line, col);

            if (this.matchingMarkupLevel == this.markupLevel) {
                this.matching = false;
                this.matchingMarkupLevel = Integer.MAX_VALUE;
            }

        }

    }



    @Override
    public void onAutoCloseElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.markupLevel--;
        this.filter.removeMatchesForLevel(this.markupLevel);

        if (this.matching) {
            this.handler.onAutoCloseElementStart(buffer, offset, len, normalizedName, line, col);
        }

    }



    @Override
    public void onAutoCloseElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {

            this.handler.onAutoCloseElementEnd(buffer, offset, len, normalizedName, line, col);

            if (this.matchingMarkupLevel == this.markupLevel) {
                this.matching = false;
                this.matchingMarkupLevel = Integer.MAX_VALUE;
            }

        }

    }



    @Override
    public void onUnmatchedCloseElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {
            this.handler.onUnmatchedCloseElementStart(buffer, offset, len, normalizedName, line, col);
        }

    }



    @Override
    public void onUnmatchedCloseElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        if (this.matching) {
            this.handler.onUnmatchedCloseElementEnd(buffer, offset, len, normalizedName, line, col);
        }

    }



    @Override
    public void onElementInnerWhiteSpace (
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {

        if (this.matching) {
            this.handler.onElementInnerWhiteSpace(buffer, offset, len, line, col);
        }

    }





    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    public void onProcessingInstruction (
            final String processingInstruction,
            final String target, final String content,
            final int line, final int col) {

        if (this.matching ||
                this.filter.matchProcessingInstruction(0, this.markupLevel, processingInstruction, target, content)) {

            this.handler.onProcessingInstruction(processingInstruction, target, content, line, col);

        }

    }




}

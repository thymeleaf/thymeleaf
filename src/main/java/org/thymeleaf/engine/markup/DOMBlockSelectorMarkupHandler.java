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

import java.util.Arrays;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class DOMBlockSelectorMarkupHandler extends AbstractMarkupHandler {


    private final IMarkupHandler delegate;

    private IDOMBlockSelectorItem[][] selectorItems;
    private int currentSelectorItemLevel;
    private boolean[][] selectorItemMatchers;


    private int currentMarkupLevel = 0;


    public DOMBlockSelectorMarkupHandler(final IMarkupHandler delegate, final IDOMBlockSelectorItem[][] selectorItems) {

        super();

        this.delegate = delegate;

        this.selectorItems = selectorItems;
        this.currentSelectorItemLevel = 0;

        this.selectorItemMatchers = new boolean[this.selectorItems.length][];
        for (int i = 0; i < this.selectorItemMatchers.length; i++) {
            this.selectorItemMatchers[i] = new boolean[this.selectorItems[i].length];
            Arrays.fill(this.selectorItemMatchers[i],false);
        }

    }





    /*
     * ---------------
     * Document events
     * ---------------
     */

    @Override
    public void onDocumentStart(final long startTimeNanos) {

        this.delegate.onDocumentStart(startTimeNanos);

    }



    @Override
    public void onDocumentEnd(final long endTimeNanos, final long totalTimeNanos) {

        this.delegate.onDocumentEnd(endTimeNanos, totalTimeNanos);

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

        this.delegate.onXmlDeclaration(xmlDeclaration, version, encoding, standalone, line, col);

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

        this.delegate.onDocTypeClause(docTypeClause, rootElementName, publicId, systemId, line, col);

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

        this.delegate.onCDATASection(buffer, offset, len, line, col);

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

        this.delegate.onText(buffer, offset, len, line, col);

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

        this.delegate.onComment(buffer, offset, len, line, col);

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

        this.delegate.onAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }



    @Override
    public void onStandaloneElementStart(
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onStandaloneElementStart(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onStandaloneElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onStandaloneElementEnd(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onOpenElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onOpenElementStart(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onOpenElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onOpenElementEnd(buffer, offset, len, normalizedName, line, col);
        this.currentMarkupLevel++;

    }



    @Override
    public void onCloseElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.currentMarkupLevel--;
        this.delegate.onCloseElementStart(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onCloseElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onCloseElementEnd(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onAutoCloseElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.currentMarkupLevel--;
        this.delegate.onAutoCloseElementStart(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onAutoCloseElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onAutoCloseElementEnd(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onUnmatchedCloseElementStart (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onUnmatchedCloseElementStart(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onUnmatchedCloseElementEnd (
            final char[] buffer, final int offset, final int len, final String normalizedName,
            final int line, final int col) {

        this.delegate.onUnmatchedCloseElementEnd(buffer, offset, len, normalizedName, line, col);

    }



    @Override
    public void onElementInnerWhiteSpace (
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {

        this.delegate.onElementInnerWhiteSpace(buffer, offset, len, line, col);

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

        this.delegate.onProcessingInstruction(processingInstruction, target, content, line, col);

    }




    /*
     *   //h2[2]
     *   //div/p
     *
     *   <div>
     *       <h2>...</h2>
     *       <p>...</p>
     *       <h2>...</h2>
     *       <div>
     *           <h2>...</h2>
     *           <p>...</p>
     *           <h2>...</h2>
     *       </div>
     *   </div>
     */


    static interface IDOMBlockSelectorItem {


        boolean isAnyLevel();
        boolean isAnyElement();
        boolean isText();

        boolean matchElement(final String normalizedElementName);
        boolean matchAttribute(final String attributeName, final String attributeValue);
        boolean matchText();

        boolean isMatched();
        void reset();


    }


    static final class DOMBlockSelectorAnyElementItem implements IDOMBlockSelectorItem {

    }

    static final class DOMBlockSelectorAnyElementAnyLevelItem implements IDOMBlockSelectorItem {

    }

    static final class DOMBlockSelectorSpecificElementAnyLevelItem implements IDOMBlockSelectorItem {

    }

    static final class DOMBlockSelectorTextItem implements IDOMBlockSelectorItem {

    }



}

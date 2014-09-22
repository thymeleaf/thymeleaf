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
final class MarkupSelectorFilter {


    private final MarkupSelectorFilter prev;
    private MarkupSelectorFilter next;

    private final MarkupSelectorItem markupSelectorItem;

    private static final int MATCHED_MARKUP_LEVELS_LEN = 10;
    private boolean[] matchedMarkupLevels;


    private final MarkupBlockMatchingCounter markupBlockMatchingCounter = new MarkupBlockMatchingCounter();
    static final class MarkupBlockMatchingCounter {
        static final int DEFAULT_COUNTER_SIZE = 4;
        int[] indexes = null;
        int[] counters = null;
    }



    MarkupSelectorFilter(final MarkupSelectorFilter prev, final MarkupSelectorItem markupSelectorItem) {
        
        super();

        this.prev = prev;
        if (this.prev != null) {
            this.prev.next = this;
        }

        this.matchedMarkupLevels = new boolean[MATCHED_MARKUP_LEVELS_LEN];
        Arrays.fill(this.matchedMarkupLevels, false);

        this.markupSelectorItem = markupSelectorItem;

    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    boolean matchXmlDeclaration(
            final int markupLevel, final int markupBlockIndex,
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone) {

        checkMarkupLevel(markupLevel);

        if (!matchesLevel(markupLevel)) {
            return false;
        }

        if (this.next == null) {
            return true;
        }

        return this.next.matchXmlDeclaration(markupLevel, markupBlockIndex, xmlDeclaration, version, encoding, standalone);

    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    boolean matchDocTypeClause(
            final int markupLevel, final int markupBlockIndex,
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId) {

        checkMarkupLevel(markupLevel);

        if (!matchesLevel(markupLevel)) {
            return false;
        }

        if (this.next == null) {
            return true;
        }

        return this.next.matchDocTypeClause(markupLevel, markupBlockIndex, docTypeClause, rootElementName, publicId, systemId);

    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    boolean matchCDATASection(
            final int markupLevel, final int markupBlockIndex,
            final char[] buffer, final int offset, final int len) {

        checkMarkupLevel(markupLevel);

        if (!matchesLevel(markupLevel)) {
            return false;
        }

        if (this.next == null) {
            return true;
        }

        return this.next.matchCDATASection(markupLevel, markupBlockIndex, buffer, offset, len);

    }





    /*
     * -----------
     * Text events
     * -----------
     */

    boolean matchText(
            final int markupLevel, final int markupBlockIndex,
            final char[] buffer, final int offset, final int len) {

        checkMarkupLevel(markupLevel);

        if (!matchesLevel(markupLevel)) {
            if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
                if (this.markupSelectorItem.matchesText()) {
                    // Matching consumes the element, so there is no way we can have a "next" after matching a text
                    return (this.next == null);
                }
            }
            return false;
        }

        if (this.next == null) {
            return true;
        }

        return this.next.matchText(markupLevel, markupBlockIndex, buffer, offset, len);

    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    boolean matchComment(
            final int markupLevel, final int markupBlockIndex,
            final char[] buffer, final int offset, final int len) {

        checkMarkupLevel(markupLevel);

        if (!matchesLevel(markupLevel)) {
            return false;
        }

        if (this.next == null) {
            return true;
        }

        return this.next.matchComment(markupLevel, markupBlockIndex, buffer, offset, len);

    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */



    boolean matchStandaloneElement(final int markupLevel, final int markupBlockIndex, final ElementBuffer elementBuffer) {

        checkMarkupLevel(markupLevel);

        if (matchesLevel(markupLevel)) {
            // This filter was already matched by a previous level (through an "open" event), so just delegate to next.

            if (this.next != null) {
                return this.next.matchStandaloneElement(markupLevel, markupBlockIndex, elementBuffer);
            }
            return true;

        }

        if (this.next != null) {
            // Matching means "consuming" the element, but this is a standalone element, so there would be no
            // room for more matching!
            return false;
        }

        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            // This element has not matched yet, but might match, so we should check

            if (this.markupSelectorItem.matches(markupBlockIndex, elementBuffer, this.markupBlockMatchingCounter)) {
                return true;
            }

        }

        // This element cannot match this level, and did not match before. So it is an impossible match.
        return false;

    }



    boolean matchOpenElement(final int markupLevel, final int markupBlockIndex, final ElementBuffer elementBuffer) {

        checkMarkupLevel(markupLevel);

        if (this.markupSelectorItem.anyLevel() || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            // This filter could match this level, so we must not lose the opportunity to compute whether it does or not.
            // BUT we must only consider matching "done" for this level (and therefore consume the element) if
            // this is the first time we match this filter. If not, we should delegate to next.

            final boolean matchesThisLevel = this.markupSelectorItem.matches(markupBlockIndex, elementBuffer, this.markupBlockMatchingCounter);

            if (matchesLevel(markupLevel)) {
                // This filter was already matched before. So the fact that it matches now or not is useful information,
                // but we should not directly return a result without first delegating to next (if there is next).
                // The reason this is useful information is because the next filters in chain might end up not matching
                // this piece of markup, and we still need to be able to re-initiate the matching process from
                // here if possible.

                this.matchedMarkupLevels[markupLevel] = matchesThisLevel;

                if (this.next != null) {
                    return this.next.matchOpenElement(markupLevel, markupBlockIndex, elementBuffer);
                }
                return true;

            } else if (matchesThisLevel) {
                // This filter was not matched before. So the fact that it matches now means we need to consume it,
                // therefore not delegating.

                this.matchedMarkupLevels[markupLevel] = true;
                return (this.next == null);

            }

        } else if (matchesLevel(markupLevel)) {
            // This filter cannot match this level, but it did match before in a previous level, so we are happy
            // delegating to next if it exists.
            if (this.next != null) {
                return this.next.matchOpenElement(markupLevel, markupBlockIndex, elementBuffer);
            }
            return true;
        }

        // This element cannot match this level, and did not match before. So it is an impossible match.
        return false;

    }




    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    boolean matchProcessingInstruction(
            final int markupLevel, final int markupBlockIndex,
            final String processingInstruction,
            final String target, final String content) {

        checkMarkupLevel(markupLevel);

        if (!matchesLevel(markupLevel)) {
            return false;
        }

        if (this.next == null) {
            return true;
        }

        return this.next.matchProcessingInstruction(markupLevel, markupBlockIndex, processingInstruction, target, content);

    }




    /*
     * --------------
     * Level handling
     * --------------
     */

    private void checkMarkupLevel(final int markupLevel) {
        if (markupLevel >= this.matchedMarkupLevels.length) {
            final int newLen = Math.max(markupLevel + 1, this.matchedMarkupLevels.length + MATCHED_MARKUP_LEVELS_LEN);
            final boolean[] newMatchedMarkupLevels = new boolean[newLen];
            Arrays.fill(newMatchedMarkupLevels, false);
            System.arraycopy(this.matchedMarkupLevels, 0, newMatchedMarkupLevels, 0, this.matchedMarkupLevels.length);
            this.matchedMarkupLevels = newMatchedMarkupLevels;
        }
    }



    void removeMatchesForLevel(final int markupLevel) {

        if (this.matchedMarkupLevels.length > markupLevel) {
            this.matchedMarkupLevels[markupLevel] = false;
        }

        if (this.next == null) {
            return;
        }

        this.next.removeMatchesForLevel(markupLevel);

    }


    private boolean matchesLevel(final int markupLevel) {
        int i = markupLevel; 
        while (i >= 0 && !this.matchedMarkupLevels[i]) { i--; }
        return (i >= 0);
    }




}

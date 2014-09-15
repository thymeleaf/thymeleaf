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
final class BlockSelectorFilter {


    private BlockSelectorFilter previous;
    private BlockSelectorFilter next;
    private final String matchedElementName;
    private final boolean matchAnyLevel;

    private int[] matchingMarkupLevels;




    public BlockSelectorFilter(
            final String normalizedMatchedElementName) {
        
        super();

        if (normalizedMatchedElementName.startsWith("//")) {
            this.matchedElementName = normalizedMatchedElementName.substring(2);
            this.matchAnyLevel = true;
        } else if (normalizedMatchedElementName.startsWith("/")) {
            this.matchedElementName = normalizedMatchedElementName.substring(1);
            this.matchAnyLevel = false;
        } else {
            this.matchedElementName = normalizedMatchedElementName;
            this.matchAnyLevel = true;
        }

        this.matchingMarkupLevels = new int[10];
        Arrays.fill(this.matchingMarkupLevels, Integer.MAX_VALUE);
        
    }



    public void setPrevious(final BlockSelectorFilter previous) {
        this.previous = previous;
    }

    public void setNext(final BlockSelectorFilter next) {
        this.next = next;
    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    public boolean matchXmlDeclaration(
            final int execLevel, final int markupLevel,
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {
            if (this.next != null) {
                return this.next.matchXmlDeclaration(execLevel, markupLevel, xmlDeclaration, version, encoding, standalone);
            }
            return true;
        }
        return false;

    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    public boolean matchDocTypeClause(
            final int execLevel, final int markupLevel,
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {
            if (this.next != null) {
                return this.next.matchDocTypeClause(execLevel, markupLevel, docTypeClause, rootElementName, publicId, systemId);
            }
            return true;
        }
        return false;

    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    public boolean matchCDATASection(
            final int execLevel, final int markupLevel,
            final char[] buffer, final int offset, final int len) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {
            if (this.next != null) {
                return this.next.matchCDATASection(execLevel, markupLevel, buffer, offset, len);
            }
            return true;
        }
        return false;

    }





    /*
     * -----------
     * Text events
     * -----------
     */

    public boolean matchText(
            final int execLevel, final int markupLevel,
            final char[] buffer, final int offset, final int len) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {
            if (this.next != null) {
                return this.next.matchText(execLevel, markupLevel, buffer, offset, len);
            }
            return true;
        }
        return false;

    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    public boolean matchComment(
            final int execLevel, final int markupLevel,
            final char[] buffer, final int offset, final int len) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {
            if (this.next != null) {
                return this.next.matchComment(execLevel, markupLevel, buffer, offset, len);
            }
            return true;
        }
        return false;

    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */



    public boolean matchStandaloneElement(
            final int execLevel, final int markupLevel,
            final String normalizedName) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {

            if (this.next != null) {
                return this.next.matchStandaloneElement(execLevel, markupLevel, normalizedName);
            }
            return true;

        }

        if (this.next != null) {
            // Matching means "consuming" the element, bu this is a standalone element, so there is no room for more matching!
            return false;
        }

        if (this.matchAnyLevel || markupLevel == 0 || (this.previous != null && this.previous.matchingMarkupLevels[execLevel] == markupLevel - 1)) {

            if (normalizedName.equals(this.matchedElementName)) {
                return true;
            }

        }

        if (this.previous != null) {
            return this.previous.matchStandaloneElement(execLevel + 1, markupLevel, normalizedName);
        }
        return false;

    }



    public boolean matchOpenElement(
            final int execLevel, final int markupLevel,
            final String normalizedName) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {

            if (this.next != null) {
                return this.next.matchOpenElement(execLevel, markupLevel, normalizedName);
            }
            return true;

        }

        if (this.matchAnyLevel || markupLevel == 0 || (this.previous != null && this.previous.matchingMarkupLevels[execLevel] == markupLevel - 1)) {

            if (normalizedName.equals(this.matchedElementName)) {

                this.matchingMarkupLevels[execLevel] = markupLevel;
                // Matching means "consuming" the element for matching, so we will only consider matching done if we are last in the chain
                return (this.next == null);

            }

        }

        if (this.previous != null) {
            return this.previous.matchOpenElement(execLevel + 1, markupLevel, normalizedName);
        }
        return false;

    }



    public void removeMatchesForLevel(final int markupLevel) {

        if (this.next != null) {
            this.next.removeMatchesForLevel(markupLevel);
        }

        for (int i = this.matchingMarkupLevels.length - 1; i >= 0; i--) {
            if (this.matchingMarkupLevels[i] == markupLevel) {
                this.matchingMarkupLevels[i] = Integer.MAX_VALUE;
            }
        }

    }




    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    public boolean matchProcessingInstruction(
            final int execLevel, final int markupLevel,
            final String processingInstruction,
            final String target, final String content) {

        if (this.matchingMarkupLevels[execLevel] <= markupLevel) {
            if (this.next != null) {
                return this.next.matchProcessingInstruction(execLevel, markupLevel, processingInstruction, target, content);
            }
            return true;
        }
        return false;

    }


    
    


}

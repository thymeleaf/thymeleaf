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

import org.thymeleaf.engine.util.TextUtil;
import org.thymeleaf.util.StringUtils;

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

    private static final int MARKUP_BLOCK_MATCHING_COUNTERS_LEN = 4;
    private int[] markupBlockMatchingIndexes = null;
    private int[] markupBlockMatchingCounters = null;



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
            if (this.markupSelectorItem.anyLevel || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
                if (matchesText()) {
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

        if (this.markupSelectorItem.anyLevel || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            // This element has not matched yet, but might match, so we should check

            if (matches(markupBlockIndex, elementBuffer)) {
                return true;
            }

        }

        // This element cannot match this level, and did not match before. So it is an impossible match.
        return false;

    }



    boolean matchOpenElement(final int markupLevel, final int markupBlockIndex, final ElementBuffer elementBuffer) {

        checkMarkupLevel(markupLevel);

        if (this.markupSelectorItem.anyLevel || markupLevel == 0 || (this.prev != null && this.prev.matchedMarkupLevels[markupLevel - 1])) {
            // This filter could match this level, so we must not lose the opportunity to compute whether it does or not.
            // BUT we must only consider matching "done" for this level (and therefore consume the element) if
            // this is the first time we match this filter. If not, we should delegate to next.

            final boolean matchesThisLevel = matches(markupBlockIndex, elementBuffer);

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






    /*
     * -------------------
     * Matching operations
     * -------------------
     */

    private boolean matchesText() {
        return this.markupSelectorItem.textSelector;
    }


    private boolean matches(final int markupBlockIndex, final ElementBuffer elementBuffer) {

        if (this.markupSelectorItem.textSelector) {
            return false;
        }

        // Quick check on attributes: if selector needs at least one and this element has none (very common case),
        // we know matching will be false.
        if (this.markupSelectorItem.requiresAttributesInElement && elementBuffer.attributeCount == 0) {
            return false;
        }

        // Check the element name. No need to check the "caseSensitive" flag here, because we are checking
        // a normalized element name (which will be already lower cased if the nature of the element requires it,
        // i.e. it comes from HTML parsing), and the element name in a markup selector item, which will have already
        // been created lower-cased if the item was created with the case-sensitive flag set to false.
        if (this.markupSelectorItem.elementName != null &&
                !elementBuffer.normalizedElementName.equals(this.markupSelectorItem.elementName)) {
            return false;
        }

        // Check the attribute values and their operators
        if (this.markupSelectorItem.attributeConditions != null &&
                !this.markupSelectorItem.attributeConditions.isEmpty()) {

            final int attributeConditionsLen = this.markupSelectorItem.attributeConditions.size();
            for (int i = 0; i < attributeConditionsLen; i++) {

                final String attrName = this.markupSelectorItem.attributeConditions.get(i).getName();
                final MarkupSelectorItem.AttributeCondition.Operator attrOperator =
                        this.markupSelectorItem.attributeConditions.get(i).getOperator();
                final String attrValue =
                        this.markupSelectorItem.attributeConditions.get(i).getValue();


                if (!matchesAttribute(elementBuffer, attrName, attrOperator, attrValue)) {
                    return false;
                }

            }

        }

        // Last thing to test, once we know all other things match, we should check if this selector includes an index
        // and, if it does, check the position of this matching block among all its MATCHING siblings (children of the
        // same parent) by accessing the by-block-index counters. (A block index identifies all the children of the
        // same parent).
        if (this.markupSelectorItem.index != null) {
            return matchesIndex(markupBlockIndex);
        }

        // Everything has gone right so far, so this has matched
        return true;

    }



    private boolean matchesAttribute(
            final ElementBuffer elementBuffer,
            final String attrName, final MarkupSelectorItem.AttributeCondition.Operator attrOperator, final String attrValue) {

        boolean found = false;
        for (int i = 0; i < elementBuffer.attributeCount; i++) {

            if (!TextUtil.equals(this.markupSelectorItem.caseSensitive,
                        attrName,                          0, attrName.length(),
                        elementBuffer.attributeBuffers[i], 0, elementBuffer.attributeNameLens[i])) {
                continue;
            }

            // Even if both HTML and XML forbid duplicated attributes, we are going to anyway going to allow
            // them and not consider an attribute "not-matched" just because it doesn't match in one of its
            // instances.
            found = true;

            if ("class".equals(attrName)) {

                // The attribute we are comparing is actually the "class" attribute, which requires an special treatment
                if (matchesClassAttributeValue(
                        attrOperator, attrValue,
                        elementBuffer.attributeBuffers[i], elementBuffer.attributeValueContentOffsets[i], elementBuffer.attributeValueContentLens[i])) {
                    return true;
                }

            } else {

                if (matchesAttributeValue(
                        attrOperator, attrValue,
                        elementBuffer.attributeBuffers[i], elementBuffer.attributeValueContentOffsets[i], elementBuffer.attributeValueContentLens[i])) {
                    return true;
                }

            }


        }

        if (found) {
            // The attribute existed, but it didn't match - we just checked until the end in case there were duplicates
            return false;
        }

        // Attribute was not found in element, so we will consider it a match if the operator is NOT_EXISTS
        return MarkupSelectorItem.AttributeCondition.Operator.NOT_EXISTS.equals(attrOperator);

    }




    private static boolean matchesAttributeValue(
            final MarkupSelectorItem.AttributeCondition.Operator attrOperator,
            final String attrValue,
            final char[] elementAttrValueBuffer, final int elementAttrValueOffset, final int elementAttrValueLen) {

        switch (attrOperator) {

            case EQUALS:
                // Test equality: we are testing values, so we always use case-sensitivity = true
                return TextUtil.equals(true,
                        attrValue,              0,                      attrValue.length(),
                        elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen);

            case NOT_EQUALS:
                // Test inequality: we are testing values, so we always use case-sensitivity = true
                return !TextUtil.equals(true,
                        attrValue,              0,                      attrValue.length(),
                        elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen);

            case STARTS_WITH:
                return TextUtil.startsWith(true,
                        elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen,
                        attrValue,              0,                      attrValue.length());

            case ENDS_WITH:
                return TextUtil.endsWith(true,
                        elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen,
                        attrValue,              0,                      attrValue.length());

            case CONTAINS:
                return TextUtil.contains(true,
                        elementAttrValueBuffer, elementAttrValueOffset, elementAttrValueLen,
                        attrValue,              0,                      attrValue.length());

            case EXISTS:
                // The fact that this attribute exists is enough to return true
                return true;

            case NOT_EXISTS:
                // This attribute should not exist in order to match
                return false;

            default:
                throw new IllegalArgumentException("Unknown operator: " + attrOperator);

        }

    }


    private static boolean matchesClassAttributeValue(
            final MarkupSelectorItem.AttributeCondition.Operator attrOperator,
            final String attrValue,
            final char[] elementAttrValueBuffer, final int elementAttrValueOffset, final int elementAttrValueLen) {

        if (elementAttrValueLen == 0) {
            return StringUtils.isEmptyOrWhitespace(attrValue);
        }

        int i = 0;

        while (i < elementAttrValueLen && Character.isWhitespace(elementAttrValueBuffer[elementAttrValueOffset + i])) { i++; }

        if (i == elementAttrValueLen) {
            return StringUtils.isEmptyOrWhitespace(attrValue);
        }

        while (i < elementAttrValueLen) {

            final int lastOffset = elementAttrValueOffset + i;

            while (i < elementAttrValueLen && !Character.isWhitespace(elementAttrValueBuffer[elementAttrValueOffset + i])) { i++; }

            if (matchesAttributeValue(attrOperator, attrValue, elementAttrValueBuffer, lastOffset, (elementAttrValueOffset + i) - lastOffset)) {
                return true;
            }

            while (i < elementAttrValueLen && Character.isWhitespace(elementAttrValueBuffer[elementAttrValueOffset + i])) { i++; }

        }

        return false;

    }




    private boolean matchesIndex(final int markupBlockIndex) {

        // Didn't previously exist: initialize. Given few selectors use indexes, this allows us to avoid creating
        // these array structures if not needed.
        if (this.markupBlockMatchingCounters == null) {
            this.markupBlockMatchingIndexes = new int[MARKUP_BLOCK_MATCHING_COUNTERS_LEN];
            this.markupBlockMatchingCounters = new int[MARKUP_BLOCK_MATCHING_COUNTERS_LEN];
            Arrays.fill(this.markupBlockMatchingIndexes, -1);
            Arrays.fill(this.markupBlockMatchingCounters, -1);
        }

        // Check whether we already had a counter for this current markup block index
        int i = 0;
        while (i < this.markupBlockMatchingIndexes.length
                && this.markupBlockMatchingIndexes[i] >= 0 // Will stop at the first -1
                && this.markupBlockMatchingIndexes[i] != markupBlockIndex) { i++; }

        // If no counter found and the array is already full, grow structures
        if (i == this.markupBlockMatchingIndexes.length) {
            final int[] newMarkupBlockMatchingIndexes = new int[this.markupBlockMatchingIndexes.length + MARKUP_BLOCK_MATCHING_COUNTERS_LEN];
            final int[] newMarkupBlockMatchingCounters = new int[this.markupBlockMatchingCounters.length + MARKUP_BLOCK_MATCHING_COUNTERS_LEN];
            Arrays.fill(newMarkupBlockMatchingIndexes, -1);
            Arrays.fill(newMarkupBlockMatchingCounters, -1);
            System.arraycopy(this.markupBlockMatchingIndexes, 0, newMarkupBlockMatchingIndexes, 0, this.markupBlockMatchingIndexes.length);
            System.arraycopy(this.markupBlockMatchingCounters, 0, newMarkupBlockMatchingCounters, 0, this.markupBlockMatchingCounters.length);
            this.markupBlockMatchingIndexes = newMarkupBlockMatchingIndexes;
            this.markupBlockMatchingCounters = newMarkupBlockMatchingCounters;
        }

        // If the counter is new, initialize it. If not, increase it
        if (this.markupBlockMatchingIndexes[i] == -1) {
            this.markupBlockMatchingIndexes[i] = markupBlockIndex;
            this.markupBlockMatchingCounters[i] = 0;
        } else {
            this.markupBlockMatchingCounters[i]++;
        }

        switch (this.markupSelectorItem.index.type) {
            case VALUE:
                if (this.markupSelectorItem.index.value != this.markupBlockMatchingCounters[i]) {
                    return false;
                }
                break;
            case LESS_THAN:
                if (this.markupSelectorItem.index.value <= this.markupBlockMatchingCounters[i]) {
                    return false;
                }
                break;
            case MORE_THAN:
                if (this.markupSelectorItem.index.value >= this.markupBlockMatchingCounters[i]) {
                    return false;
                }
                break;
            case EVEN:
                if (this.markupBlockMatchingCounters[i] % 2 != 0) {
                    return false;
                }
                break;
            case ODD:
                if (this.markupBlockMatchingCounters[i] % 2 == 0) {
                    return false;
                }
                break;
        }

        return true;

    }


}

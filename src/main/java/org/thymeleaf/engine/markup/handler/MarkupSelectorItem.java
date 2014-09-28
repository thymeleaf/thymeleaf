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
final class MarkupSelectorItem implements IMarkupSelectorItem {

    static final String TEXT_SELECTOR = "text()";
    static final String COMMENT_SELECTOR = "comment()";
    static final String CDATA_SECTION_SELECTOR = "cdata()";
    static final String DOC_TYPE_CLAUSE_SELECTOR = "doctype()";
    static final String XML_DECLARATION_SELECTOR = "xmldecl()";
    static final String PROCESSING_INSTRUCTION_SELECTOR = "procinstr()";
    static final String ID_MODIFIER_SEPARATOR = "#";
    static final String CLASS_MODIFIER_SEPARATOR = ".";
    static final String REFERENCE_MODIFIER_SEPARATOR = "%";

    static final String ID_ATTRIBUTE_NAME = "id";
    static final String CLASS_ATTRIBUTE_NAME = "class";

    static final String ODD_SELECTOR = "odd()";
    static final String EVEN_SELECTOR = "even()";



    private final MarkupSelectorMode mode;
    private final boolean anyLevel;
    private final boolean textSelector;
    private final boolean commentSelector;
    private final boolean cdataSectionSelector;
    private final boolean docTypeClauseSelector;
    private final boolean xmlDeclarationSelector;
    private final boolean processingInstructionSelector;
    private final String elementName;
    private final IndexCondition index;
    private final IAttributeCondition attributeCondition;
    private final boolean requiresAttributesInElement;


    // TODO Add to github issues a ticket explaining that the syntax is now extended to:
    //                * support attribute "existence" and "non-existence"
    //                * support indexes "even()" and "odd()"
    //                * support "contains" attribute value with "*="
    //                * support for > and < in indexed selectors
    //                * support for both "and" and "or" in attribute conditions
    //                * support for matching comments, CDATA, DOCTYPE, XML Declarations and Processing Instructions

    MarkupSelectorItem(
            final MarkupSelectorMode mode, final boolean anyLevel,
            final boolean textSelector, final boolean commentSelector,
            final boolean cdataSectionSelector, final boolean docTypeClauseSelector,
            final boolean xmlDeclarationSelector, final boolean processingInstructionSelector,
            final String elementName, final IndexCondition index, final IAttributeCondition attributeCondition) {

        super();

        this.mode = mode;
        this.anyLevel = anyLevel;
        this.textSelector = textSelector;
        this.commentSelector = commentSelector;
        this.cdataSectionSelector = cdataSectionSelector;
        this.docTypeClauseSelector = docTypeClauseSelector;
        this.xmlDeclarationSelector = xmlDeclarationSelector;
        this.processingInstructionSelector = processingInstructionSelector;
        this.elementName = elementName;
        this.index = index;
        this.attributeCondition = attributeCondition;

        // This is used in order to perform quick checks when matching: if this selector requires the existence
        // of at least one attribute in the element and the element has none, we know it won't match.
        this.requiresAttributesInElement = computeRequiresAttributesInElement(this.attributeCondition);

    }


    private static boolean computeRequiresAttributesInElement(final IAttributeCondition attributeCondition) {

        if (attributeCondition == null) {
            return false;
        }

        if (attributeCondition instanceof AttributeConditionRelation) {
            final AttributeConditionRelation relation = (AttributeConditionRelation) attributeCondition;
            // as long as one of the sides requires attributes, we should consider this "true"
            return computeRequiresAttributesInElement(relation.left) || computeRequiresAttributesInElement(relation.right);
        }

        final AttributeCondition attrCondition = (AttributeCondition) attributeCondition;
        return (!attrCondition.operator.equals(AttributeCondition.Operator.NOT_EQUALS) &&
                !attrCondition.operator.equals(AttributeCondition.Operator.NOT_EXISTS));

    }




    public String toString() {

        final StringBuilder strBuilder = new StringBuilder();

        if (this.anyLevel) {
            strBuilder.append("//");
        } else {
            strBuilder.append("/");
        }

        if (this.elementName != null) {
            strBuilder.append(this.elementName);
        } else if (this.textSelector) {
            strBuilder.append(TEXT_SELECTOR);
        } else if (this.commentSelector) {
            strBuilder.append(COMMENT_SELECTOR);
        } else if (this.cdataSectionSelector) {
            strBuilder.append(CDATA_SECTION_SELECTOR);
        } else if (this.docTypeClauseSelector) {
            strBuilder.append(DOC_TYPE_CLAUSE_SELECTOR);
        } else if (this.xmlDeclarationSelector) {
            strBuilder.append(XML_DECLARATION_SELECTOR);
        } else if (this.processingInstructionSelector) {
            strBuilder.append(PROCESSING_INSTRUCTION_SELECTOR);
        } else {
            strBuilder.append("*");
        }

        if (this.attributeCondition != null) {
            strBuilder.append("[");
            strBuilder.append(toStringAttributeCondition(this.attributeCondition, false));
            strBuilder.append("]");
        }

        if (this.index != null) {
            strBuilder.append("[");
            switch (this.index.type) {
                case VALUE:
                    strBuilder.append(this.index.value);
                    break;
                case LESS_THAN:
                    strBuilder.append("<").append(this.index.value);
                    break;
                case MORE_THAN:
                    strBuilder.append(">").append(this.index.value);
                    break;
                case EVEN:
                    strBuilder.append(EVEN_SELECTOR);
                    break;
                case ODD:
                    strBuilder.append(ODD_SELECTOR);
                    break;
            }
            strBuilder.append("]");
        }

        return strBuilder.toString();

    }

    private static String toStringAttributeCondition(final IAttributeCondition attributeCondition, final boolean outputParenthesis) {

        if (attributeCondition instanceof AttributeConditionRelation) {
            final AttributeConditionRelation relation = (AttributeConditionRelation) attributeCondition;
            if (outputParenthesis) {
                return "(" + toStringAttributeCondition(relation.left, true) + " " + relation.type + " " + toStringAttributeCondition(relation.right, true) + ")";
            }
            return toStringAttributeCondition(relation.left, true) + " " + relation.type + " " + toStringAttributeCondition(relation.right, true);
        }

        final AttributeCondition attrCondition = (AttributeCondition) attributeCondition;
        return attrCondition.name + attrCondition.operator.text + (attrCondition.value != null ? "'" + attrCondition.value + "'" : "");

    }







    static interface IAttributeCondition {
        // Merely a marker interface
    }

    static final class AttributeCondition implements IAttributeCondition {

        static enum Operator {

            EQUALS("="), NOT_EQUALS("!="), STARTS_WITH("^="), ENDS_WITH("$="), EXISTS("*"), NOT_EXISTS("!"), CONTAINS("*=");

            private String text;
            Operator(final String text) {
                this.text = text;
            }

        }

        final String name;
        final Operator operator;
        final String value;

        AttributeCondition(final String name, final Operator operator, final String value) {
            super();
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

    }

    static final class AttributeConditionRelation implements IAttributeCondition {

        static enum Type { AND, OR }

        final Type type;
        final IAttributeCondition left;
        final IAttributeCondition right;

        AttributeConditionRelation(final Type type, final IAttributeCondition left, final IAttributeCondition right) {
            super();
            this.type = type;
            this.left = left;
            this.right = right;
        }

    }




    static final class IndexCondition {

        static enum IndexConditionType { VALUE, LESS_THAN, MORE_THAN, EVEN, ODD }
        static IndexCondition INDEX_CONDITION_ODD = new IndexCondition(IndexConditionType.ODD, -1);
        static IndexCondition INDEX_CONDITION_EVEN = new IndexCondition(IndexConditionType.EVEN, -1);

        final IndexConditionType type;
        final int value;

        IndexCondition(final IndexConditionType type, final int value) {
            super();
            this.type = type;
            this.value = value;
        }

    }












    /*
     * -------------------
     * Matching operations
     * -------------------
     */

    public boolean anyLevel() {
        return this.anyLevel;
    }


    public boolean matchesText(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (!this.textSelector) {
            return false;
        }

        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        return true;

    }


    public boolean matchesComment(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (!this.commentSelector) {
            return false;
        }

        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        return true;

    }


    public boolean matchesCDATASection(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (!this.cdataSectionSelector) {
            return false;
        }

        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        return true;

    }


    public boolean matchesDocTypeClause(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (!this.docTypeClauseSelector) {
            return false;
        }

        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        return true;

    }


    public boolean matchesXmlDeclaration(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (!this.xmlDeclarationSelector) {
            return false;
        }

        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        return true;

    }


    public boolean matchesProcessingInstruction(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (!this.processingInstructionSelector) {
            return false;
        }

        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        return true;

    }


    public boolean matchesElement(final int markupBlockIndex, final SelectorElementBuffer elementBuffer,
                                  final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter) {

        if (this.textSelector || this.commentSelector || this.cdataSectionSelector ||
                this.docTypeClauseSelector || this.xmlDeclarationSelector || this.processingInstructionSelector) {
            return false;
        }

        // Quick check on attributes: if selector needs at least one and this element has none (very common case),
        // we know matching will be false.
        if (this.requiresAttributesInElement && elementBuffer.attributeCount == 0) {
            return false;
        }

        // Check the element name. No need to check the "caseSensitive" flag here, because we are checking
        // a normalized element name (which will be already lower cased if the nature of the element requires it,
        // i.e. it comes from HTML parsing), and the element name in a markup selector item, which will have already
        // been created lower-cased if the item was created with the case-sensitive flag set to false.
        if (this.elementName != null &&
                !elementBuffer.normalizedElementName.equals(this.elementName)) {
            return false;
        }

        // Check the attribute conditions (if any)
        if (this.attributeCondition != null &&
                !matchesAttributeCondition(this.mode, elementBuffer, this.attributeCondition)) {
            return false;
        }

        // Last thing to test, once we know all other things match, we should check if this selector includes an index
        // and, if it does, check the position of this matching block among all its MATCHING siblings (children of the
        // same parent) by accessing the by-block-index counters. (A block index identifies all the children of the
        // same parent).
        if (this.index != null &&
                !matchesIndex(markupBlockIndex, markupBlockMatchingCounter, this.index)) {
            return false;
        }

        // Everything has gone right so far, so this has matched
        return true;

    }



    private static boolean matchesAttributeCondition(
            final MarkupSelectorMode mode, final SelectorElementBuffer elementBuffer, final IAttributeCondition attributeCondition) {

        if (attributeCondition instanceof AttributeConditionRelation) {
            final AttributeConditionRelation relation = (AttributeConditionRelation) attributeCondition;
            switch (relation.type) {
                case AND:
                    return matchesAttributeCondition(mode, elementBuffer, relation.left) &&
                            matchesAttributeCondition(mode, elementBuffer, relation.right);
                case OR:
                    return matchesAttributeCondition(mode, elementBuffer, relation.left) ||
                            matchesAttributeCondition(mode, elementBuffer, relation.right);
            }
        }

        final AttributeCondition attrCondition = (AttributeCondition) attributeCondition;
        return matchesAttribute(mode, elementBuffer, attrCondition.name, attrCondition.operator, attrCondition.value);

    }



    private static boolean matchesAttribute(
            final MarkupSelectorMode mode, final SelectorElementBuffer elementBuffer,
            final String attrName, final MarkupSelectorItem.AttributeCondition.Operator attrOperator, final String attrValue) {

        boolean found = false;
        for (int i = 0; i < elementBuffer.attributeCount; i++) {

            if (!TextUtil.equals(mode.isCaseSensitive(),
                    attrName, 0, attrName.length(),
                    elementBuffer.attributeBuffers[i], 0, elementBuffer.attributeNameLens[i])) {
                continue;
            }

            // Even if both HTML and XML forbid duplicated attributes, we are going to anyway going to allow
            // them and not consider an attribute "not-matched" just because it doesn't match in one of its
            // instances.
            found = true;

            if (MarkupSelectorMode.HTML.equals(mode) && "class".equals(attrName)) {

                // The attribute we are comparing is actually the "class" attribute, which requires an special treatment
                // if we are in HTML mode.
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




    private static boolean matchesIndex(
            final int markupBlockIndex, final MarkupSelectorFilter.MarkupBlockMatchingCounter markupBlockMatchingCounter,
            final IndexCondition indexCondition) {

        // Didn't previously exist: initialize. Given few selectors use indexes, this allows us to avoid creating
        // these array structures if not needed.
        if (markupBlockMatchingCounter.counters == null) {
            markupBlockMatchingCounter.indexes = new int[MarkupSelectorFilter.MarkupBlockMatchingCounter.DEFAULT_COUNTER_SIZE];
            markupBlockMatchingCounter.counters = new int[MarkupSelectorFilter.MarkupBlockMatchingCounter.DEFAULT_COUNTER_SIZE];
            Arrays.fill(markupBlockMatchingCounter.indexes, -1);
            Arrays.fill(markupBlockMatchingCounter.counters, -1);
        }

        // Check whether we already had a counter for this current markup block index
        int i = 0;
        while (i < markupBlockMatchingCounter.indexes.length
                && markupBlockMatchingCounter.indexes[i] >= 0 // Will stop at the first -1
                && markupBlockMatchingCounter.indexes[i] != markupBlockIndex) { i++; }

        // If no counter found and the array is already full, grow structures
        if (i == markupBlockMatchingCounter.indexes.length) {
            final int[] newMarkupBlockMatchingIndexes = new int[markupBlockMatchingCounter.indexes.length + MarkupSelectorFilter.MarkupBlockMatchingCounter.DEFAULT_COUNTER_SIZE];
            final int[] newMarkupBlockMatchingCounters = new int[markupBlockMatchingCounter.counters.length + MarkupSelectorFilter.MarkupBlockMatchingCounter.DEFAULT_COUNTER_SIZE];
            Arrays.fill(newMarkupBlockMatchingIndexes, -1);
            Arrays.fill(newMarkupBlockMatchingCounters, -1);
            System.arraycopy(markupBlockMatchingCounter.indexes, 0, newMarkupBlockMatchingIndexes, 0, markupBlockMatchingCounter.indexes.length);
            System.arraycopy(markupBlockMatchingCounter.counters, 0, newMarkupBlockMatchingCounters, 0, markupBlockMatchingCounter.counters.length);
            markupBlockMatchingCounter.indexes = newMarkupBlockMatchingIndexes;
            markupBlockMatchingCounter.counters = newMarkupBlockMatchingCounters;
        }

        // If the counter is new, initialize it. If not, increase it
        if (markupBlockMatchingCounter.indexes[i] == -1) {
            markupBlockMatchingCounter.indexes[i] = markupBlockIndex;
            markupBlockMatchingCounter.counters[i] = 0;
        } else {
            markupBlockMatchingCounter.counters[i]++;
        }

        switch (indexCondition.type) {
            case VALUE:
                if (indexCondition.value != markupBlockMatchingCounter.counters[i]) {
                    return false;
                }
                break;
            case LESS_THAN:
                if (indexCondition.value <= markupBlockMatchingCounter.counters[i]) {
                    return false;
                }
                break;
            case MORE_THAN:
                if (indexCondition.value >= markupBlockMatchingCounter.counters[i]) {
                    return false;
                }
                break;
            case EVEN:
                if (markupBlockMatchingCounter.counters[i] % 2 != 0) {
                    return false;
                }
                break;
            case ODD:
                if (markupBlockMatchingCounter.counters[i] % 2 == 0) {
                    return false;
                }
                break;
        }

        return true;

    }




}

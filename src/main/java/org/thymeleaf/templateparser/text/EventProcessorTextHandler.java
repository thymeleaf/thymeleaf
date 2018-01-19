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
package org.thymeleaf.templateparser.text;

import java.util.Arrays;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;


/*
 * This class is very similar to AttoParser's org.attoparser.MarkupEventProcessorHandler.
 *
 * Its aim is to be the one to receive the parser-generated events and do the first checks required to ensure the
 * correct hierarchy and nesting of elements. It therefore maintains a stack of parsed elements that it checks
 * for each parsing event, in order to be sure that every element is adequately open and closed.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class EventProcessorTextHandler extends AbstractChainedTextHandler {

    private static final int DEFAULT_STACK_LEN = 10;
    private static final int DEFAULT_ATTRIBUTE_NAMES_LEN = 3;


    // Will be used as an element name cache in order to avoid creating a new
    // char[] object each time an element is pushed into the stack or an attribute
    // is processed to check its uniqueness.
    private StructureNamesRepository structureNamesRepository;

    private char[][] elementStack;
    private int elementStackSize;

    private char[][] currentElementAttributeNames = null;
    private int currentElementAttributeNamesSize = 0;




    EventProcessorTextHandler(final ITextHandler handler) {

        super(handler);

        this.elementStack = new char[DEFAULT_STACK_LEN][];
        this.elementStackSize = 0;

        this.structureNamesRepository = new StructureNamesRepository();

    }







    public void handleDocumentEnd(final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws TextParseException {

        if (this.elementStackSize > 0) {
            final char[] popped = popFromStack();
            throw new TextParseException(
                    "Malformed template: element " +
                    "\"" + new String(popped, 0, popped.length) + "\"" +
                    " is never closed (no closing tag at the end of document)");
        }

        super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);

    }


    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {

        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;

        // This is a standalone element, no need to put into stack

        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);

    }


    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;

        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);

        pushToStack(buffer, nameOffset, nameLen);

    }


    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        if (!checkStackForElement(buffer, nameOffset, nameLen, line, col)) {
            throw new TextParseException(
                    "Malformed text: element \"" + new String(buffer, nameOffset, nameLen) + "\" is never closed", line, col);
        }

        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;

        super.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);

    }


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

        // Check attribute name is unique in this element
        if (this.currentElementAttributeNames == null) {
            // we only create this structure if there is at least one attribute
            this.currentElementAttributeNames = new char[DEFAULT_ATTRIBUTE_NAMES_LEN][];
        }
        for (int i = 0; i < this.currentElementAttributeNamesSize; i++) {

            if (TextUtils.equals(
                    TemplateMode.TEXT.isCaseSensitive(),
                    this.currentElementAttributeNames[i], 0, this.currentElementAttributeNames[i].length,
                    buffer, nameOffset, nameLen)) {

                throw new TextParseException(
                        "Malformed text: Attribute \"" + new String(buffer, nameOffset, nameLen) + "\" appears more than once in element",
                        nameLine, nameCol);

            }

        }
        if (this.currentElementAttributeNamesSize == this.currentElementAttributeNames.length) {
            // we need to grow the array!
            final char[][] newCurrentElementAttributeNames = new char[this.currentElementAttributeNames.length + DEFAULT_ATTRIBUTE_NAMES_LEN][];
            System.arraycopy(this.currentElementAttributeNames, 0, newCurrentElementAttributeNames, 0, this.currentElementAttributeNames.length);
            this.currentElementAttributeNames = newCurrentElementAttributeNames;
        }

        this.currentElementAttributeNames[this.currentElementAttributeNamesSize] =
                this.structureNamesRepository.getStructureName(buffer, nameOffset, nameLen);

        this.currentElementAttributeNamesSize++;


        // In text parsing, we will be allowing both element attributes without quotes and without operator at all...

        super.handleAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }



    private boolean checkStackForElement(
            final char[] buffer, final int offset, final int len, final int line, final int col)
            throws TextParseException {

        char[] peek = peekFromStack();

        if (peek != null) {

            if (TextUtils.equals(TemplateMode.TEXT.isCaseSensitive(), peek, 0, peek.length, buffer, offset, len)) {
                popFromStack();
                return true;
            }

            // does not match...
            throw new TextParseException(
                    "Malformed template: " + (peek.length > 0? ("element \"" + new String(peek, 0, peek.length) + "\"") : ("unnamed element")) + " is never closed", line, col);

        }

        throw new TextParseException(
                "Malformed template: unnamed closing element is never opened", line, col);

    }



    private void pushToStack(
            final char[] buffer, final int offset, final int len) {

        if (this.elementStackSize == this.elementStack.length) {
            growStack();
        }

        this.elementStack[this.elementStackSize] =
                this.structureNamesRepository.getStructureName(buffer, offset, len);

        this.elementStackSize++;

    }


    private char[] peekFromStack() {
        if (this.elementStackSize == 0) {
            return null;
        }
        return this.elementStack[this.elementStackSize - 1];
    }


    private char[] popFromStack() {
        if (this.elementStackSize == 0) {
            return null;
        }
        final char[] popped = this.elementStack[this.elementStackSize - 1];
        this.elementStack[this.elementStackSize - 1] = null;
        this.elementStackSize--;
        return popped;
    }


    private void growStack() {

        final int newStackLen = this.elementStack.length + DEFAULT_STACK_LEN;
        final char[][] newStack = new char[newStackLen][];
        System.arraycopy(this.elementStack, 0, newStack, 0, this.elementStack.length);
        this.elementStack = newStack;

    }







    /*
     * In-instance repository for structure names (element + attribute names).
     *
     * This class is NOT thread-safe. Should only be used inside a specific handler
     * instance/thread and only during a single execution.
     */
    static final class StructureNamesRepository {

        private static final int REPOSITORY_INITIAL_LEN = 20;
        private static final int REPOSITORY_INITIAL_INC = 5;
        private char[][] repository;
        private int repositorySize;


        StructureNamesRepository() {
            super();
            this.repository = new char[REPOSITORY_INITIAL_LEN][];
            this.repositorySize = 0;
        }


        char[] getStructureName(final char[] text, final int offset, final int len) {

            // We are looking for exact matches here, disregarding the TEXT_PARSING_CASE_SENSITIVE constant value
            final int index =
                    TextUtils.binarySearch(true, this.repository, 0, this.repositorySize, text, offset, len);

            if (index >= 0) {
                return this.repository[index];
            }

            /*
             * NOT FOUND. We need to store the text
             */
            return storeStructureName(index, text, offset, len);

        }


        private char[] storeStructureName(final int index, final char[] text, final int offset, final int len) {

            if (this.repositorySize == this.repository.length) {
                // We must grow the repository!
                final char[][] newRepository = new char[this.repository.length + REPOSITORY_INITIAL_INC][];
                Arrays.fill(newRepository, null);
                System.arraycopy(this.repository, 0, newRepository, 0, this.repositorySize);
                this.repository = newRepository;
            }

            // binary search returned (-(insertion point) - 1)
            final int insertionIndex = ((index + 1) * -1);

            // Create the char[] for the structure name
            final char[] structureName = new char[len];
            System.arraycopy(text, offset, structureName, 0, len);

            // Make room and insert the new element
            System.arraycopy(this.repository, insertionIndex, this.repository, insertionIndex + 1, this.repositorySize - insertionIndex);
            this.repository[insertionIndex] = structureName;
            this.repositorySize++;

            return structureName;

        }

    }



}
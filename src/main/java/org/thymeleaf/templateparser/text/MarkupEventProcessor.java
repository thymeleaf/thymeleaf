/*
 * =============================================================================
 * 
 *   Copyright (c) 2012-2014, The ATTOPARSER team (http://www.attoparser.org)
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

import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.util.TextUtil;


/*
 * Objects of this class are the first ones to receive events from the parser, and they are in charge of transmitting
 * these events to the markup handlers.
 *
 * This MarkupEventProcessor implements logic that allows the application of several features and restrictions in
 * XML and (especially) HTML markup. For this, it builds an element stack during parsing, which it uses to reference
 * events to their specific position in the original document.
 *
 * Note that, although MarkupParser's are stateless, objects of this class are STATEFUL just like markup handlers can
 * potentially be, and therefore a new MarkupEventProcessor object will be built for each parsing operation.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class MarkupEventProcessor implements ParsingAttributeSequenceUtil.IMarkupEventAttributeSequenceProcessor {


    private static final int DEFAULT_STACK_LEN = 10;
    private static final int DEFAULT_ATTRIBUTE_NAMES_LEN = 3;

    private static final boolean caseSensitive = true;

    private final IMarkupHandler handler;

    // Will be used as an element name cache in order to avoid creating a new
    // char[] object each time an element is pushed into the stack or an attribute
    // is processed to check its uniqueness.
    private final StructureNamesRepository structureNamesRepository;

    private char[][] elementStack;
    private int elementStackSize;

    private char[][] currentElementAttributeNames = null;
    private int currentElementAttributeNamesSize = 0;




    MarkupEventProcessor(final IMarkupHandler handler) {

        super();

        this.handler = handler;

        this.elementStack = new char[DEFAULT_STACK_LEN][];
        this.elementStackSize = 0;

        this.structureNamesRepository = new StructureNamesRepository();

    }




    void processDocumentStart(final long startTimeNanos, final int line, final int col)
            throws ParseException {
        this.handler.handleDocumentStart(startTimeNanos, line, col);
    }



    void processDocumentEnd(final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws ParseException {

        if (this.elementStackSize > 0) {
            final char[] popped = popFromStack();
            throw new ParseException(
                "Malformed markup: element " +
                "\"" + new String(popped, 0, popped.length) + "\"" +
                " is never closed (no closing tag at the end of document)");
        }

        this.handler.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);

    }




    void processText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws ParseException {
        this.handler.handleText(buffer, offset, len, line, col);
    }



    void processStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws ParseException {

        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;

        // This is a standalone element, no need to put into stack

        this.handler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);

    }

    void processStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws ParseException {

        this.handler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);

    }


    void processOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;

        this.handler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);

        pushToStack(buffer, nameOffset, nameLen);

    }

    void processOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.handler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);

    }


    void processCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        checkStackForElement(buffer, nameOffset, nameLen, line, col);

        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;

        this.handler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);

    }

    void processCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws ParseException {

        this.handler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);

    }


    public void processAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws ParseException {

        // Check attribute name is unique in this element
        if (this.currentElementAttributeNames == null) {
            // we only create this structure if there is at least one attribute
            this.currentElementAttributeNames = new char[DEFAULT_ATTRIBUTE_NAMES_LEN][];
        }
        for (int i = 0; i < this.currentElementAttributeNamesSize; i++) {

            if (TextUtil.equals(
                    caseSensitive,
                    this.currentElementAttributeNames[i], 0, this.currentElementAttributeNames[i].length,
                    buffer, nameOffset, nameLen)) {

                throw new ParseException(
                        "Malformed markup: Attribute \"" + new String(buffer, nameOffset, nameLen) + "\" " +
                        "appears more than once in element",
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


        this.handler.handleAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }



    private void checkStackForElement(
            final char[] buffer, final int offset, final int len, final int line, final int col)
            throws ParseException {

        char[] peek = peekFromStack(0);

        if (peek != null) {

            if (TextUtil.equals(caseSensitive, peek, 0, peek.length, buffer, offset, len)) {
                popFromStack();
                return;
            }

            // does not match...
            throw new ParseException(
                    "Malformed markup: element " +
                    "\"" + new String(peek, 0, peek.length) + "\"" +
                    " is never closed", line, col);

        }

        // closing element at the root level
        throw new ParseException(
                "Malformed markup: closing element " +
                "\"" + new String(buffer, offset, len) + "\"" +
                " is never open", line, col);

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


    private char[] peekFromStack(final int delta) {
        if (this.elementStackSize <= delta) {
            return null;
        }
        return this.elementStack[(this.elementStackSize - 1) - delta];
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

        private static final int REPOSITORY_INITIAL_LEN = 100;
        private static final int REPOSITORY_INITIAL_INC = 20;
        private char[][] repository;
        private int repositorySize;


        StructureNamesRepository() {
            super();
            this.repository = new char[REPOSITORY_INITIAL_LEN][];
            this.repositorySize = 0;
        }


        char[] getStructureName(final char[] text, final int offset, final int len) {

            final int index =
                    TextUtil.binarySearch(true, this.repository, 0, this.repositorySize, text, offset, len);

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
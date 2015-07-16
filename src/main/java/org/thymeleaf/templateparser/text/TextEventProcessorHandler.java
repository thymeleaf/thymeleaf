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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.config.ParseConfiguration;


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
final class TextEventProcessorHandler extends AbstractChainedTextHandler {


    private static final int DEFAULT_STACK_LEN = 10;
    private static final int DEFAULT_ATTRIBUTE_NAMES_LEN = 3;

    private ParseStatus status;

    // Will be used as an element name cache in order to avoid creating a new
    // char[] object each time an element is pushed into the stack or an attribute
    // is processed to check its uniqueness.
    private StructureNamesRepository structureNamesRepository;

    private char[][] elementStack;
    private int elementStackSize;

    private boolean elementRead = false;
    private char[][] currentElementAttributeNames = null;
    private int currentElementAttributeNamesSize = 0;


    private boolean closeElementIsMatched = true;


    TextEventProcessorHandler(final ITextHandler handler) {

        super(handler);

        this.elementStack = new char[DEFAULT_STACK_LEN][];
        this.elementStackSize = 0;

        this.structureNamesRepository = new StructureNamesRepository();

    }




    @Override
    public void setParseStatus(final ParseStatus status) {
        this.status = status;
        super.setParseStatus(status);
    }




    public void handleDocumentEnd(final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws TextParseException {

        if (this.requireBalancedElements && this.elementStackSize > 0) {
            final char[] popped = popFromStack();
            throw new ParseException(
                    "Malformed markup: element " +
                            "\"" + new String(popped, 0, popped.length) + "\"" +
                            " is never closed (no closing tag at the end of document)");
        }

        if (!this.elementRead && (
                (this.validPrologDocTypeRead && this.uniqueRootElementPresence.isDependsOnPrologDoctype()) ||
                        this.uniqueRootElementPresence.isRequiredAlways())) {
            throw new ParseException(
                    "Malformed markup: no root element present");
        }

        if (this.useStack) {
            cleanStack(line, col);
        }

        getNext().handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);

    }


    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {

        if (this.useStack) {

            if (this.elementStackSize == 0) {
                checkValidRootElement(buffer, nameOffset, nameLen, line, col);
            }

            if (this.requireUniqueAttributesInElement) {
                this.currentElementAttributeNames = null;
                this.currentElementAttributeNamesSize = 0;
            }

            // This is a standalone element, no need to put into stack

        }

        /*
         * Perform the handling of the standalone element start
         * These events might require previous auto-* operations, in which case these
         * have to be performed and then the event launched again.
         */

        this.status.autoOpenCloseDone = false;
        this.status.autoOpenParents = null;
        this.status.autoOpenLimits = null;
        this.status.autoCloseRequired = null;
        this.status.autoCloseLimits = null;
        this.status.avoidStacking = true; // Default for standalone elements is avoid stacking

        getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);

        if (this.useStack) {
            if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
                if (this.status.autoCloseRequired != null) {
                    // Auto-close operations
                    autoClose(this.status.autoCloseRequired, this.status.autoCloseLimits, line, col);
                }
                if (this.status.autoOpenParents != null) {
                    // Auto-open operations
                    autoOpen(this.status.autoOpenParents, this.status.autoOpenLimits, line, col);
                }
                // Re-launching of the event
                this.status.autoOpenCloseDone = true;
                getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
            }
            if (!this.status.avoidStacking) {
                pushToStack(buffer, nameOffset, nameLen);
            }
        } else {
            if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
                // We were required to perform auto* operations, but we have no stack, so we will
                // just launch the event again
                this.status.autoOpenCloseDone = true;
                getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
            }
        }

        this.status.autoOpenCloseDone = true;


    }

    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {

        this.elementRead = true;
        getNext().handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);

    }


    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        if (this.useStack) {

            if (this.elementStackSize == 0) {
                checkValidRootElement(buffer, nameOffset, nameLen, line, col);
            }

            if (this.requireUniqueAttributesInElement) {
                this.currentElementAttributeNames = null;
                this.currentElementAttributeNamesSize = 0;
            }

        }

        /*
         * Perform the handling of the open element start
         * These events might require previous auto-* operations, in which case these
         * have to be performed and then the event launched again.
         */

        this.status.autoOpenCloseDone = false;
        this.status.autoOpenParents = null;
        this.status.autoOpenLimits = null;
        this.status.autoCloseRequired = null;
        this.status.autoCloseLimits = null;
        this.status.avoidStacking = false; // Default for open elements is not to avoid stacking

        getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);

        if (this.useStack) {
            if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
                if (this.status.autoCloseRequired != null) {
                    // Auto-close operations
                    autoClose(this.status.autoCloseRequired, this.status.autoCloseLimits, line, col);
                }
                if (this.status.autoOpenParents != null) {
                    // Auto-open operations
                    autoOpen(this.status.autoOpenParents, this.status.autoOpenLimits, line, col);
                }
                // Re-launching of the event
                this.status.autoOpenCloseDone = true;
                getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
            }
            if (!this.status.avoidStacking) {
                // Can be an HTML void element
                pushToStack(buffer, nameOffset, nameLen);
            }
        } else {
            if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
                // We were required to perform auto* operations, but we have no stack, so we will
                // just launch the event again
                this.status.autoOpenCloseDone = true;
                getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
            }
        }

    }

    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.elementRead = true;
        getNext().handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);

    }


    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        if (this.useStack) {

            this.closeElementIsMatched =
                    checkStackForElement(buffer, nameOffset, nameLen, line, col);

            if (this.requireUniqueAttributesInElement) {
                this.currentElementAttributeNames = null;
                this.currentElementAttributeNamesSize = 0;
            }

            if (this.closeElementIsMatched) {
                getNext().handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            } else {
                getNext().handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            }

        }

        getNext().handleCloseElementStart(buffer, nameOffset, nameLen, line, col);

    }

    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.elementRead = true;

        if (this.useStack && !this.closeElementIsMatched) {
            getNext().handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            return;
        }

        getNext().handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);

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

        if (this.useStack && this.requireUniqueAttributesInElement) {

            // Check attribute name is unique in this element
            if (this.currentElementAttributeNames == null) {
                // we only create this structure if there is at least one attribute
                this.currentElementAttributeNames = new char[DEFAULT_ATTRIBUTE_NAMES_LEN][];
            }
            for (int i = 0; i < this.currentElementAttributeNamesSize; i++) {

                if (TextUtil.equals(
                        this.caseSensitive,
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

        }


        if (this.requireWellFormedAttributeValues) {

            // Check there is an operator
            if (operatorLen == 0)  {
                throw new ParseException(
                        "Malformed markup: Attribute \"" + new String(buffer, nameOffset, nameLen) + "\" " +
                                "must include an equals (=) sign and a value surrounded by quotes",
                        operatorLine, operatorCol);
            }


            // Check attribute is surrounded by commas (double or single)
            if (valueOuterLen == 0 || valueOuterLen == valueContentLen)  {
                throw new ParseException(
                        "Malformed markup: Value for attribute \"" + new String(buffer, nameOffset, nameLen) + "\" " +
                                "must be surrounded by quotes",
                        valueLine, valueCol);
            }

        }

        getNext().handleAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }



    private boolean checkStackForElement(
            final char[] buffer, final int offset, final int len, final int line, final int col)
            throws TextParseException {

        int peekDelta = 0;
        char[] peek = peekFromStack(peekDelta);

        while (peek != null) {

            if (TextUtil.equals(this.caseSensitive, peek, 0, peek.length, buffer, offset, len)) {

                // We found the corresponding opening element, so we execute all pending auto-close events
                // (if needed) and return true (meaning the close element has a matching open element).

                for (int i = 0; i < peekDelta; i++) {
                    peek = popFromStack();
                    if (this.autoClose) {
                        getNext().handleAutoCloseElementStart(peek, 0, peek.length, line, col);
                        getNext().handleAutoCloseElementEnd(peek, 0, peek.length, line, col);
                    } else {
                        // fixing unclosed non-optional tags by auto closing is forbidden!
                        throw new ParseException(
                                "Malformed markup: element " +
                                        "\"" + new String(peek, 0, peek.length) + "\"" +
                                        " is never closed", line, col);
                    }
                }
                popFromStack();

                return true;

            }

            // does not match...

            if (this.requireBalancedElements) {
                throw new ParseException(
                        "Malformed markup: element " +
                                "\"" + new String(peek, 0, peek.length) + "\"" +
                                " is never closed", line, col);
            }

            peek = peekFromStack(++peekDelta);

        }

        // closing element at the root level
        if (this.requireNoUnmatchedCloseElements) {
            throw new ParseException(
                    "Malformed markup: closing element " +
                            "\"" + new String(buffer, offset, len) + "\"" +
                            " is never open", line, col);
        }

        // Return false because the close element has no matching open element
        return false;

    }




    private void cleanStack(final int line, final int col)
            throws TextParseException {

        if (this.elementStackSize > 0) {

            // When we arrive here we know that "requireBalancedElements" is
            // false. If it were true, an exception would have been raised before.

            char[] popped = popFromStack();

            while (popped != null) {

                if (this.autoClose) {
                    getNext().handleAutoCloseElementStart(popped, 0, popped.length, line, col);
                    getNext().handleAutoCloseElementEnd(popped, 0, popped.length, line, col);
                } else {
                    // fixing unclosed non-optional tags by auto closing is forbidden!
                    throw new ParseException(
                            "Malformed markup: element " +
                                    "\"" + new String(popped, 0, popped.length) + "\"" +
                                    " is never closed", line, col);
                }

                popped = popFromStack();

            }

        }

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

            // We rely on the static structure name cache, just in case it is a standard HTML structure name.
            // Note the StandardNamesRepository will create the new char[] if not found, so no need to null-check.
            final char[] structureName = StandardNamesRepository.getStructureName(text, offset, len);

            // Make room and insert the new element
            System.arraycopy(this.repository, insertionIndex, this.repository, insertionIndex + 1, this.repositorySize - insertionIndex);
            this.repository[insertionIndex] = structureName;
            this.repositorySize++;

            return structureName;

        }

    }




    /*
     *     This class is IMMUTABLE, and therefore thread-safe. Will be used in a static manner by all
     *     threads which require the use of a repository of standard names (HTML names, in this case).
     */
    static final class StandardNamesRepository {


        private static final char[][] REPOSITORY;


        static {

            final List<String> names = new ArrayList<String>(150);
            // Add all the standard HTML element (tag) names
            names.addAll(HtmlNames.ALL_STANDARD_ELEMENT_NAMES);
            // We know all standard element names are lowercase, so let's cache them uppercase too
            for (final String name : HtmlNames.ALL_STANDARD_ELEMENT_NAMES) {
                names.add(name.toUpperCase());
            }
            // Add all the standard HTML attribute names
            names.addAll(HtmlNames.ALL_STANDARD_ATTRIBUTE_NAMES);
            // We know all standard attribute names are lowercase, so let's cache them uppercase too
            for (final String name : HtmlNames.ALL_STANDARD_ATTRIBUTE_NAMES) {
                names.add(name.toUpperCase());
            }
            Collections.sort(names);

            REPOSITORY = new char[names.size()][];

            for (int i = 0; i < names.size(); i++) {
                final String name = names.get(i);
                REPOSITORY[i] = name.toCharArray();
            }

        }


        static char[] getStructureName(final char[] text, final int offset, final int len) {

            final int index = TextUtil.binarySearch(true, REPOSITORY, text, offset, len);

            if (index < 0) {
                final char[] structureName = new char[len];
                System.arraycopy(text, offset, structureName, 0, len);
                return structureName;
            }

            return REPOSITORY[index];

        }


        private StandardNamesRepository() {
            super();
        }

    }



}
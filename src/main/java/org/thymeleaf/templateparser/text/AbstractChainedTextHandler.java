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


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractChainedTextHandler extends AbstractTextHandler {


    private final ITextHandler next;


    protected AbstractChainedTextHandler(final ITextHandler next) {
        super();
        this.next = next;
    }


    protected ITextHandler getNext() {
        return this.next;
    }



    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col) throws TextParseException {
        this.next.handleDocumentStart(startTimeNanos, line, col);
    }

    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col) throws TextParseException {
        this.next.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {
        this.next.handleText(buffer, offset, len, line, col);
    }

    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws TextParseException {
        this.next.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        this.next.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        this.next.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.next.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.next.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.next.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        this.next.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
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
        this.next.handleAttribute(
                buffer,
                nameOffset, nameLen,
                nameLine, nameCol,
                operatorOffset, operatorLen,
                operatorLine, operatorCol,
                valueContentOffset, valueContentLen,
                valueOuterOffset, valueOuterLen,
                valueLine, valueCol);
    }

}

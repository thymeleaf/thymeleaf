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


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractTextHandler implements ITextHandler {



    protected AbstractTextHandler() {
        super();
    }




    public void setParseStatus(final ParseStatus status) {
        // Nothing to do. By default handlers will not be interested in using this object at all.
        // Implementations will have to explicitly override if they need to use it.
    }

    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col) throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col) throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        // Nothing to be done here, meant to be overridden if required
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
        // Nothing to be done here, meant to be overridden if required
    }

}

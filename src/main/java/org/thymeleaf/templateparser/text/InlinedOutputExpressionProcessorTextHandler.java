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
package org.thymeleaf.templateparser.text;

import java.util.Arrays;

/*
 * This class converts inlined output expressions into their equivalent element events, which makes it possible
 * to cache parsed inlined expressions.
 *
 * Some examples:
 *
 *     [[${someVar}]]            ->     [# th:text="${someVar}"/]          (decomposed into the corresponding events)
 *     [(${someVar})]            ->     [# th:utext="${someVar}"/]         (decomposed into the corresponding events)
 *
 * NOTE: The inlining mechanism is a part of the Standard Dialects, so the conversion performed by this handler
 *       on inlined output expressions should only be applied if one of the Standard Dialects has been configured.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class InlinedOutputExpressionProcessorTextHandler extends AbstractChainedTextHandler {

    private final boolean standardDialectPresent;
    private final String standardDialectPrefix;

    private int markupLevel = 0;


    InlinedOutputExpressionProcessorTextHandler(
            final ITextHandler handler, final boolean standardDialectPresent, final String standardDialectPrefix) {
        super(handler);
        this.standardDialectPresent = standardDialectPresent;
        this.standardDialectPrefix = standardDialectPrefix;
    }



    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {

        if (!this.standardDialectPresent) {
            getNext().handleText(buffer, offset, len, line, col);
            return;
        }




    }




    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {

        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);

    }


    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col)
            throws TextParseException {

        super.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);

    }




    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }


    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.markupLevel++;

        super.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);

    }




    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        this.markupLevel++;

        super.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);

    }


    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {

        super.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);

    }




    @Override
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

        super.handleAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);

    }






    private void increaseMarkupLevel() {

        this.markupLevel++;


    }


    private void decreaseMarkupLevel() {
        this.markupLevel--;
    }

}
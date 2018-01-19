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

/*
 * This class performs the processing of comments, just after they are identified by the parser. The idea
 * is that every comment that is identified as a 'commented element' should be converted to the adequate
 * 'element' events, and also that every comment that is identified as a 'commented expression' is processed by
 * removing the comment prefix/suffix and then also removing part of the following text event content (until any of
 * ';', ',' ,')', '}', ']' is found (taking care of the possible existence of literals, and of different levels of
 * object property hierarchy), or another non-text event is fired)
 *
 * Some examples (note the comment suffixes are written with a whitespace so that they don't close this comment):
 *
 *     /*[# th:each="a : ${list}"]* /        ->     [# th:each="a : ${list}"] (decomposed into the corresponding events)
 *     /*[(${someVar})]* / something;        ->     [(${someVar})];           (as a single TEXT event)
 *     /*[[${someVar}]]* / something;        ->     [[${someVar}]];           (as a single TEXT event)
 *     /*[whatever]* /                       ->     /*[whatever]* /           (as a single TEXT event)
 *     /*whatever* /                         ->     /*whatever* /             (as a single TEXT event)
 *
 * NOTE: No comment event should ever be fired by this handler. "Normal" comments, i.e. those that are not commented
 *       elements nor commented expressions, will be passed to the next handler in the chain as text events.
 * NOTE: The inlining mechanism is a part of the Standard Dialects, so the conversion performed by this handler
 *       on inlined output expressions should only be applied if one of the Standard Dialects has been configured.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class CommentProcessorTextHandler extends AbstractChainedTextHandler {

    private final boolean standardDialectPresent;

    private boolean filterTexts = false;
    private char[] filteredTextBuffer = null;
    private int filteredTextSize = 0;
    private int[] filteredTextLocator = null;


    CommentProcessorTextHandler(final boolean standardDialectPresent, final ITextHandler handler) {
        super(handler);
        this.standardDialectPresent = standardDialectPresent;
    }




    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws TextParseException {
        processFilteredTexts();
        super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }




    @Override
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws TextParseException {

        processFilteredTexts();

        /*
         * FIRST STEP: Quickly determine if we actually need to do anything with this comment. We will process
         * every comment which has any of the shapes: [#...], [/...], [(...)] or [[...]].
         * If we determine that a comment is not processable, we will output it as mere text.
         */

        if (!isCommentProcessable(buffer, contentOffset, contentLen)) {
            super.handleText(buffer, outerOffset, outerLen, line, col);
            return;
        }


        /*
         * SECOND STEP: If these comments are here just wrapping an element, unwrap such element.
         */

        final int maxi = contentOffset + contentLen;

        if (TextParsingElementUtil.isOpenElementStart(buffer, contentOffset, maxi)) {
            // This might be an open / standalone element, let's check how it ends

            if (TextParsingElementUtil.isElementEnd(buffer, maxi - 2, maxi, true)) {
                // It's a standalone element

                TextParsingElementUtil.parseStandaloneElement(buffer, contentOffset, contentLen, line, col + 2, getNext());
                return;

            } else if (TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false)) {
                // It's an open element

                TextParsingElementUtil.parseOpenElement(buffer, contentOffset, contentLen, line, col + 2, getNext());
                return;

            }

        } else if (TextParsingElementUtil.isCloseElementStart(buffer, contentOffset, maxi)) {
            // Seems we may have an element being closed here...

            if (TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false)) {
                // It's a standalone element

                TextParsingElementUtil.parseCloseElement(buffer, contentOffset, contentLen, line, col + 2, getNext());
                return;

            }

        }


        /*
         * FINAL STEP: At this point, we know it's an expression, not an element. So we will not be modifying the
         *             content of the comment (the expression itself, such as '[[${someVar}]]' or '[(${someVar})], but
         *             we will be removing the rest of the line (or more correctly the rest of the structure the
         *             comment is in).
         *
         *             NOTE This will only be performed if a Standard Dialect is present, given it's the Standard
         *             Dialects who define the output expression inlining mechanism.
         */

        if (this.standardDialectPresent) {
            getNext().handleText(buffer, contentOffset, contentLen, line, col + 2); // +2 in order to count '[[' or [('
            this.filterTexts = true;
        } else {
            // A Standard Dialect is not present, so this entire mechanism is disabled and the comment reported
            // as mere text. No need to perform any transformation
            getNext().handleText(buffer, outerOffset, outerLen, line, col);
        }

    }




    private boolean isCommentProcessable(final char[] buffer, final int contentOffset, final int contentLen) {
        final int maxi = contentOffset + contentLen;
        if (contentLen < 3 || buffer[contentOffset] != '[' || buffer[maxi - 1] != ']') {
            return false;
        }
        if (contentLen >= 4 && buffer[contentOffset + 1] == '(' && buffer[maxi - 2] == ')') {
            // That's a [(...)] commented unescaped expression
            return true;
        }
        if (contentLen >= 4 && buffer[contentOffset + 1] == '[' && buffer[maxi - 2] == ']') {
            // That's a [[...]] commented escaped expression
            return true;
        }
        // It is wrapped by brackets, but it is not a commented expression, so it will only be processable if it
        // matches the syntax of a commented element
        if (TextParsingElementUtil.isOpenElementStart(buffer, contentOffset, maxi))  {
            return TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false); // we don't mind whether it is minimized or not
        }
        if (TextParsingElementUtil.isCloseElementStart(buffer, contentOffset, maxi)) {
            return TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false);
        }
        return false;
    }




    @Override
    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col)
            throws TextParseException {

        if (this.filterTexts) {
            // We are filtering, so we will be accumulating texts until a different, non-text event comes, and
            // then start processing and see until which position we really have to filter out.
            filterText(buffer, offset, len, line, col);
            return;
        }

        super.handleText(buffer, offset, len, line, col);

    }


    private void filterText(final char[] buffer, final int offset, final int len, final int line, final int col) {

        // We need to put the filtered text into the buffer, which we might have to create or grow first
        if (this.filteredTextBuffer == null) {
            this.filteredTextBuffer = new char[Math.max(256, len)];
            this.filteredTextSize = 0;
            this.filteredTextLocator = new int[2];
        } else if (this.filteredTextSize + len > this.filteredTextBuffer.length) {
            final char[] newFilteredTextBuffer =
                    new char[Math.max(this.filteredTextBuffer.length + 256, this.filteredTextSize + len)];
            System.arraycopy(this.filteredTextBuffer, 0, newFilteredTextBuffer, 0, this.filteredTextSize);
            this.filteredTextBuffer = newFilteredTextBuffer;
        }

        System.arraycopy(buffer, offset, this.filteredTextBuffer, this.filteredTextSize, len);
        this.filteredTextSize += len;

        this.filteredTextLocator[0] = line;
        this.filteredTextLocator[1] = col;

    }


    private void processFilteredTexts() throws TextParseException {

        if (!this.filterTexts) {
            return;
        }

        final int filterOffset =
                computeFilterOffset(this.filteredTextBuffer, 0, this.filteredTextSize, this.filteredTextLocator);

        if (filterOffset < this.filteredTextSize) {
            // We filter out until filterOffset, and create a text event for the rest of the text
            super.handleText(
                    this.filteredTextBuffer,
                    filterOffset,
                    (this.filteredTextSize - filterOffset),
                    this.filteredTextLocator[0],
                    this.filteredTextLocator[1]);
        } // else need to filter out ALL the texts until the next structure

        this.filteredTextSize = 0;
        this.filterTexts = false;

    }



    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen, final boolean minimized,
            final int line, final int col)
            throws TextParseException {
        processFilteredTexts();
        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }


    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        processFilteredTexts();
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }


    @Override
    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        processFilteredTexts();
        super.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }




    private static int computeFilterOffset(final char[] buffer, final int offset, final int maxi, final int[] locator) {

        if (offset == maxi) {
            return 0;
        }

        char literalDelimiter = 0;
        int arrayLevel = 0;
        int objectLevel = 0;

        int i = offset;

        while (i < maxi) {

            final char c = buffer[i++];


            if (literalDelimiter != 0) {
                if (c == literalDelimiter && buffer[i - 2] != '\\') {
                   literalDelimiter = 0;
                }
                ParsingLocatorUtil.countChar(locator, c);
                continue;
            }

            if (c == '\'' || c == '"') {
                literalDelimiter = c;
                ParsingLocatorUtil.countChar(locator, c);
                continue;
            }

            if (c == '{') {
                objectLevel++;
                ParsingLocatorUtil.countChar(locator, c);
                continue;
            } else if (objectLevel > 0 && c == '}') {
                objectLevel--;
                ParsingLocatorUtil.countChar(locator, c);
                continue;
            } else if (c == '[') {
                arrayLevel++;
                ParsingLocatorUtil.countChar(locator, c);
                continue;
            } else if (arrayLevel > 0 && c == ']') {
                arrayLevel--;
                ParsingLocatorUtil.countChar(locator, c);
                continue;
            }

            if (arrayLevel == 0 && objectLevel == 0) {
                if (c == '\n'){
                    return i - 1; // If we find a line feed after we are sure no open array/objects left, just stop
                }
                if (c == ';' || c == ',' || c == ')' || c == '}' || c == ']') {
                    return i - 1;
                }
                if (c == '/' && i < maxi && buffer[i] == '/') { // This is a single-line comment
                    return i - 1;
                }
            }

            ParsingLocatorUtil.countChar(locator, c);

        }

        return maxi;

    }


}
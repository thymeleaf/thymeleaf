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
 * Class containing utility methods for parsing elements (tags).
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TextParsingElementUtil {


    

    
    private TextParsingElementUtil() {
        super();
    }




    public static void parseStandaloneElement(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col,
            final ITextHandler handler)
            throws TextParseException {

        if (len < 4 || !isOpenElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 2, offset + len, true)) {
            throw new TextParseException(
                    "Could not parse as a well-formed standalone element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }

        final int contentOffset = offset + 2;   // We add +2 because of '[#'
        final int contentLen = len - 4;         // -2 because of '[#' and another -2 because of '/]'

        final int maxi = contentOffset + contentLen;

        final int[] locator = new int[] {line, col + 2};

        /*
         * Extract the element name first
         */

        final int elementNameEnd =
                TextParsingUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);

        if (elementNameEnd == -1) {
            // The buffer only contains the element name (if it has a name)

            handler.handleStandaloneElementStart(
                    buffer, contentOffset, contentLen,
                    true, line, col);

            handler.handleStandaloneElementEnd(
                    buffer, contentOffset, contentLen,
                    true, locator[0], locator[1]);

            return;

        }


        handler.handleStandaloneElementStart(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                true, line, col);


        // This parseAttributeSequence will take care of calling handleInnerWhitespace when appropriate.
        TextParsingAttributeSequenceUtil.parseAttributeSequence(
                buffer, elementNameEnd, maxi - elementNameEnd, locator[0], locator[1], handler);

        // We need to forward the locator to the position corresponding with the element end (note we are discarding result)
        TextParsingUtil.findNextStructureEndAvoidQuotes(buffer, elementNameEnd, maxi, locator);

        handler.handleStandaloneElementEnd(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                true, locator[0], locator[1]);

    }




    public static void parseOpenElement(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col,
            final ITextHandler handler)
            throws TextParseException {

        if (len < 3 || !isOpenElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 1, offset + len, false)) {
            throw new TextParseException(
                    "Could not parse as a well-formed open element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }

        final int contentOffset = offset + 2;   // We add +2 because of '[#'
        final int contentLen = len - 3;         // -2 because of '[#' and another -1 because of ']'

        final int maxi = contentOffset + contentLen;

        final int[] locator = new int[] {line, col + 2};

        /*
         * Extract the element name first
         */

        final int elementNameEnd =
                TextParsingUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);

        if (elementNameEnd == -1) {
            // The buffer only contains the element name (if it has a name)

            handler.handleOpenElementStart(
                    buffer, contentOffset, contentLen,
                    line, col);

            handler.handleOpenElementEnd(
                    buffer, contentOffset, contentLen,
                    locator[0], locator[1]);

            return;

        }


        handler.handleOpenElementStart(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                line, col);


        // This parseAttributeSequence will take care of calling handleInnerWhitespace when appropriate.
        TextParsingAttributeSequenceUtil.parseAttributeSequence(
                buffer, elementNameEnd, maxi - elementNameEnd, locator[0], locator[1], handler);

        // We need to forward the locator to the position corresponding with the element end (note we are discarding result)
        TextParsingUtil.findNextStructureEndAvoidQuotes(buffer, elementNameEnd, maxi, locator);

        handler.handleOpenElementEnd(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                locator[0], locator[1]);

    }




    public static void parseCloseElement(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col,
            final ITextHandler handler)
            throws TextParseException {

        if (len < 3 || !isCloseElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 1, offset + len, false)) {
            throw new TextParseException(
                    "Could not parse as a well-formed close element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }

        final int contentOffset = offset + 2;   // We add +2 because of '[/'
        final int contentLen = len - 3;         // -2 because of '[/' and another -1 because of ']'

        final int maxi = contentOffset + contentLen;

        final int[] locator = new int[] {line, col + 2};

        /*
         * Extract the element name first
         */

        final int elementNameEnd =
                TextParsingUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);

        if (elementNameEnd == -1) {
            // The buffer only contains the element name

            handler.handleCloseElementStart(
                    buffer, contentOffset, contentLen,
                    line, col);

            handler.handleCloseElementEnd(
                    buffer, contentOffset, contentLen,
                    locator[0], locator[1]);

            return;

        }


        handler.handleCloseElementStart(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                line, col);


        final int wsEnd =
                TextParsingUtil.findNextNonWhitespaceCharWildcard(buffer, elementNameEnd, maxi, locator);

        if (wsEnd != -1) {
            // This is a close tag, so everything should be whitespace
            // until the end of the close tag
            throw new TextParseException(
                    "Could not parse as a well-formed closing element \"" + new String(buffer, offset, len) + "\": No attributes are allowed here", line, col);
        }


        handler.handleCloseElementEnd(
                buffer, contentOffset, (elementNameEnd - contentOffset),
                locator[0], locator[1]);

    }






    static boolean isOpenElementStart(final char[] buffer, final int offset, final int maxi) {

        final int len = maxi - offset;

        return (len > 2 &&
                    buffer[offset] == '[' &&
                    buffer[offset + 1] == '#' &&
                    isElementNameOrEnd(buffer, offset + 2, maxi));

    }


    static boolean isCloseElementStart(final char[] buffer, final int offset, final int maxi) {

        final int len = maxi - offset;

        return (len > 2 &&
                    buffer[offset] == '[' &&
                    buffer[offset + 1] == '/' &&
                    isElementNameOrEnd(buffer, offset + 2, maxi));

    }


    static boolean isElementEnd(final char[] buffer, final int offset, final int maxi, final boolean minimized) {

        final int len = maxi - offset;

        if (len < 1) {
            return false; // won't fit
        }

        if (minimized) {
            if (len < 2 || buffer[offset] != '/') {
                return false;
            }
            return buffer[offset + 1] == ']';
        }

        return buffer[offset] == ']';

    }




    private static boolean isElementNameOrEnd(final char[] buffer, final int offset, final int maxi) {

        if (Character.isWhitespace(buffer[offset])) {
            // We cover here the case when we don't apply an element name: [# th:something=...]
            return true;
        }

        final int len = maxi - offset;

        if (len > 1 && buffer[offset] == '/') {
            // This can still be valid if we are just closing a no-name standalone element without attributes: [#/]
            return isElementEnd(buffer, offset, maxi, true);
        }

        if (len > 0 && buffer[offset] == ']') {
            // This can still be valid if we are just closing a no-name open element without attributes: [#]
            return isElementEnd(buffer, offset, maxi, false);
        }

        // At this point the element HAS TO have a name, as we have already ruled out all possibilities that it does not.
        // The rules for an element name are basically the same, wrt allowed chars, as a normal markup (HTML,XML) element.
        // Note however that we will not be allowing a '{' just after the first # character already parsed so that we
        // don't mistake a direct output expression for an externalized message [[#{my.message}]] for an element.
        return (len > 0 &&
                buffer[offset] != '-' && buffer[offset] != '!' &&
                buffer[offset] != '/' && buffer[offset] != '?' &&
                buffer[offset] != '[' && buffer[offset] != '{' &&
                !Character.isWhitespace(buffer[offset]));

    }







    
}

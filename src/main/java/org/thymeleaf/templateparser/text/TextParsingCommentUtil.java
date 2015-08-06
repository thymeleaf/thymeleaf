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


/*
 * Class containing utility methods for parsing comments.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TextParsingCommentUtil {




    private TextParsingCommentUtil() {
        super();
    }



    public static void parseComment(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col,
            final ITextHandler handler)
            throws TextParseException {

        if (len < 4 || !isCommentStart(buffer, offset, offset + len) || !isCommentEnd(buffer, (offset + len) - 2, offset + len)) {
            throw new TextParseException(
                    "Could not parse as a well-formed Comment: \"" + new String(buffer, offset, len) + "\"", line, col);
        }

        final int contentOffset = offset + 2;
        final int contentLen = len - 4;

        handler.handleComment(
                buffer,
                contentOffset, contentLen,
                offset, len,
                line, col);

    }



    
    static boolean isCommentStart(final char[] buffer, final int offset, final int maxi) {
        return ((maxi - offset > 1) &&
                    buffer[offset] == '/' &&
                    buffer[offset + 1] == '*');
    }


    static boolean isCommentEnd(final char[] buffer, final int offset, final int maxi) {
        return ((maxi - offset > 1) &&
                buffer[offset] == '*' &&
                buffer[offset + 1] == '/');
    }

}

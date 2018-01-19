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
 * Class containing utility methods for parsing comments.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TextParsingLiteralUtil {




    private TextParsingLiteralUtil() {
        super();
    }



    static boolean isRegexLiteralStart(final char[] buffer, final int offset, final int maxi) {

        if (offset == 0 || buffer[offset] != '/') {
            return false;
        }

        // We will check that this is not one of the other structures starting with a slash (comments)

        if (TextParsingCommentUtil.isCommentBlockStart(buffer, offset, maxi)) {
            return false;
        }

        if (TextParsingCommentUtil.isCommentLineStart(buffer, offset, maxi)) {
            return false;
        }

        char c;
        int i = offset - 1;
        while (i >= 0) {
            c = buffer[i];
            if (!Character.isWhitespace(c)) {
                return c == '(' || c == '=' || c == ',';
            }
            i--;
        }

        return false;

    }





/*
    static boolean isRegexLiteralEnd(final char[] buffer, final int offset, final int maxi) {
        return ((maxi - offset > 1) &&
                buffer[offset] == '*' &&
                buffer[offset + 1] == '/');
    }
*/


}

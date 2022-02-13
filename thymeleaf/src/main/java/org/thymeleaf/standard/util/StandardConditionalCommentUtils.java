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
package org.thymeleaf.standard.util;




/**
 * <p>
 *   Utility class for processing IE conditional comments.
 * </p>
 * <p>
 *   Expected format is:
 * </p>
 * <pre><code>
 *    &lt;!--[start-condition]&gt;content&lt;![end-condition]--&gt;
 * </code></pre>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardConditionalCommentUtils {



    /**
     * <p>
     *   Tries to parse the text passed as argument as a conditional comment.
     * </p>
     * <p>
     *  Result is an object of class {@link ConditionalCommentParsingResult}, or
     *  {@code null} if text does not have the expected format for a conditional comment.
     * </p>
     *
     * @param text the text to be parsed
     * @return a {@link ConditionalCommentParsingResult} object if text could be parsed,
     *         null if format is invalid.
     */
    public static ConditionalCommentParsingResult parseConditionalComment(final CharSequence text) {

        final int len = text.length();
        int i = 4; // We start right after the "<!--"


        // discard all initial whitespace
        while (i < len && Character.isWhitespace(text.charAt(i))) { i++; }

        // check the first char after whitespace is '['
        if (i >= len || text.charAt(i++) != '[') {
            return null;
        }

        final int startExpressionOffset = i;

        // look for last position of start expression
        while (i < len && text.charAt(i) != ']') { i++; }

        if (i >= len) {
            return null;
        }

        final int startExpressionLen = (i - startExpressionOffset);

        // discard the ']' position
        i++;

        // discard all following whitespace
        while (i < len && Character.isWhitespace(text.charAt(i))) { i++; }

        // check the first non-whitespace char after ']' is '>'
        if (i >= len || text.charAt(i++) != '>') {
            return null;
        }

        final int contentOffset = i;

        // Once we've obtained all we needed from the start of the comment, switch place
        // and start looking for structures from the end.
        i = (len - 3) - 1; // we use that '-3' in order to compensate for the ending "-->"

        // discard all final whitespace
        while (i > contentOffset && Character.isWhitespace(text.charAt(i))) { i--; }

        // check the first char after whitespace is ']'
        if (i <= contentOffset || text.charAt(i--) != ']') {
            return null;
        }

        final int endExpressionLastPos = i + 1;

        // look for first char of end expression
        while (i > contentOffset && text.charAt(i) != '[') { i--; }

        if (i <= contentOffset) {
            return null;
        }

        final int endExpressionOffset = i + 1;
        final int endExpressionLen = endExpressionLastPos - endExpressionOffset;

        // discard the '[' sign we have just processed
        i--;

        // discard all following whitespace
        while (i >= contentOffset && Character.isWhitespace(text.charAt(i))) { i--; }

        // check the first non-whitespace char before '[' is '!'
        if (i <= contentOffset || text.charAt(i--) != '!') {
            return null;
        }

        // check the first char before '!' is '<'
        if (i <= contentOffset || text.charAt(i--) != '<') {
            return null;
        }

        final int contentLen = (i + 1) - contentOffset;

        if (contentLen <= 0 || startExpressionLen <= 0 || endExpressionLen <= 0) {
            return null;
        }

        return new ConditionalCommentParsingResult(
                startExpressionOffset, startExpressionLen,
                contentOffset, contentLen,
                endExpressionOffset, endExpressionLen);

    }





    private StandardConditionalCommentUtils() {
        super();
    }




    public static final class ConditionalCommentParsingResult {


        private final int startExpressionOffset;
        private final int startExpressionLen;
        private final int contentOffset;
        private final int contentLen;
        private final int endExpressionOffset;
        private final int endExpressionLen;


        public ConditionalCommentParsingResult(final int startExpressionOffset, final int startExpressionLen,
                                               final int contentOffset, final int contentLen, final int endExpressionOffset,
                                               final int endExpressionLen) {

            super();

            this.startExpressionOffset = startExpressionOffset;
            this.startExpressionLen = startExpressionLen;
            this.contentOffset = contentOffset;
            this.contentLen = contentLen;
            this.endExpressionOffset = endExpressionOffset;
            this.endExpressionLen = endExpressionLen;

        }


        public int getStartExpressionOffset() {
            return this.startExpressionOffset;
        }

        public int getStartExpressionLen() {
            return this.startExpressionLen;
        }

        public int getContentOffset() {
            return this.contentOffset;
        }

        public int getContentLen() {
            return this.contentLen;
        }

        public int getEndExpressionOffset() {
            return this.endExpressionOffset;
        }

        public int getEndExpressionLen() {
            return this.endExpressionLen;
        }

    }
    
}

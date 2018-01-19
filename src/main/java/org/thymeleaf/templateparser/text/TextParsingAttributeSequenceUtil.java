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
 * Class containing utility methods for parsing attribute sequences. This class is almost a copy of AttoParser's
 * org.attoparser.ParsingAttributeSequenceUtil, except for the fact that ITextHandler does not have an event for
 * inner whitespace (they do not have to be preserved in text parsing), so the code here is a bit simpler.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TextParsingAttributeSequenceUtil {





    private TextParsingAttributeSequenceUtil() {
        super();
    }






    public static void parseAttributeSequence(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col,
            final ITextHandler handler)
            throws TextParseException {

        // Any string will be recognized as an "attribute sequence", so this will always either return a not-null result
        // or raise an exception.

        final int maxi = offset + len;

        final int[] locator = new int[] {line, col};

        int i = offset;
        int current = i;

        int currentArtifactLine;
        int currentArtifactCol;

        while (i < maxi) {

            /*
             * STEP ONE: Look for whitespaces between attributes
             */

            final int wsEnd =
                    TextParsingUtil.findNextNonWhitespaceCharWildcard(buffer, i, maxi, locator);

            if (wsEnd == -1) {
                // Everything is whitespace until the end of the tag
                i = maxi;
                continue;
            }

            if (wsEnd > current) {
                // We avoid empty whitespace fragments
                i = wsEnd;
                current = i;
            }



            /*
             * STEP TWO: Detect the attribute name
             */


            currentArtifactLine = locator[0];
            currentArtifactCol = locator[1];

            final int attributeNameEnd =
                    TextParsingUtil.findNextOperatorCharWildcard(buffer, i, maxi, locator);

            if (attributeNameEnd == -1) {
                // This is a no-value and no-equals-sign attribute, equivalent to value = ""

                handler.handleAttribute(
                        buffer,                                                               // name
                        current, (maxi - current),                                            // name
                        currentArtifactLine, currentArtifactCol,                              // name
                        0, 0,                                                                 // operator
                        locator[0], locator[1],                                               // operator
                        0, 0, 0, 0,                                                           // value
                        locator[0], locator[1]);                                              // value

                i = maxi;
                continue;

            }

            if (attributeNameEnd <= current) {
                // This attribute name starts by an equals sign, which is forbidden
                throw new TextParseException(
                        "Bad attribute name in sequence \"" + new String(buffer, offset, len) + "\": attribute names " +
                        "cannot start with an equals sign", currentArtifactLine, currentArtifactCol);
            }


            final int attributeNameOffset = current;
            final int attributeNameLen = attributeNameEnd - current;
            final int attributeNameLine = currentArtifactLine;
            final int attributeNameCol = currentArtifactCol;
            i = attributeNameEnd;
            current = i;



            /*
             * STEP THREE: Detect the operator
             */


            currentArtifactLine = locator[0];
            currentArtifactCol = locator[1];

            final int operatorEnd =
                    TextParsingUtil.findNextNonOperatorCharWildcard(buffer, i, maxi, locator);

            if (operatorEnd == -1) {
                // This could be:
                //    1. A no-value and no-equals-sign attribute
                //    2. A no-value WITH equals sign attribute

                boolean equalsPresent = false;
                for (int j = i; j < maxi; j++) {
                    if (buffer[j] == '=') {
                        equalsPresent = true;
                        break;
                    }
                }

                if (equalsPresent) {
                    // It is a no value with equals, so we will consider everything
                    // to be an operator

                    handler.handleAttribute(
                            buffer,                                                                // name
                            attributeNameOffset, attributeNameLen,                                 // name
                            attributeNameLine, attributeNameCol,                                   // name
                            current, (maxi - current),                                             // operator
                            currentArtifactLine, currentArtifactCol,                               // operator
                            0, 0, 0, 0,                                                            // value
                            locator[0], locator[1]);                                               // value

                } else {
                    // There is no "=", so we will output the attribute with no operator and not value

                    handler.handleAttribute(
                            buffer,                                                                // name
                            attributeNameOffset, attributeNameLen,                                 // name
                            attributeNameLine, attributeNameCol,                                   // name
                            0, 0,                                                                  // operator
                            currentArtifactLine, currentArtifactCol,                               // operator
                            0, 0, 0, 0,                                                            // value
                            currentArtifactLine, currentArtifactCol);                              // value

                }

                i = maxi;
                continue;

                // end: (operatorEnd == -1)
            }


            boolean equalsPresent = false;
            for (int j = current; j < operatorEnd; j++) {
                if (buffer[j] == '=') {
                    equalsPresent = true;
                    break;
                }
            }

            if (!equalsPresent) {
                // It is not an operator, but a whitespace between this and the next attribute,
                // so we will output the attribute with no operator and no value

                handler.handleAttribute(
                        buffer,                                                                // name
                        attributeNameOffset, attributeNameLen,                                 // name
                        attributeNameLine, attributeNameCol,                                   // name
                        0, 0,                                                                  // operator
                        currentArtifactLine, currentArtifactCol,                               // operator
                        0, 0, 0, 0,                                                            // value
                        currentArtifactLine, currentArtifactCol);                              // value

                i = operatorEnd;
                current = i;
                continue;

            }


            final int operatorOffset = current;
            final int operatorLen = operatorEnd - current;
            final int operatorLine = currentArtifactLine;
            final int operatorCol = currentArtifactCol;
            i = operatorEnd;
            current = i;



            /*
             * STEP FOUR: Detect the value
             */


            currentArtifactLine = locator[0];
            currentArtifactCol = locator[1];

            final boolean attributeEndsWithQuotes = (i < maxi && (buffer[current] == '"' || buffer[current] == '\''));
            final int valueEnd =
                    (attributeEndsWithQuotes?
                            TextParsingUtil.findNextAnyCharAvoidQuotesWildcard(buffer, i, maxi, locator) :
                            TextParsingUtil.findNextWhitespaceCharWildcard(buffer, i, maxi, false, locator));

            if (valueEnd == -1) {
                // This value ends the attribute

                int valueContentOffset = current;
                int valueContentLen = (maxi - current);

                if (isValueSurroundedByCommas(buffer, current, (maxi - current))) {
                    valueContentOffset = valueContentOffset + 1;
                    valueContentLen = valueContentLen - 2;
                }

                handler.handleAttribute(
                        buffer,                                                               // name
                        attributeNameOffset, attributeNameLen,                                // name
                        attributeNameLine, attributeNameCol,                                  // name
                        operatorOffset, operatorLen,                                          // operator
                        operatorLine, operatorCol,                                            // operator
                        valueContentOffset, valueContentLen, current, (maxi - current),       // value
                        currentArtifactLine, currentArtifactCol);                             // value

                i = maxi;
                continue;

            }


            final int valueOuterOffset = current;
            final int valueOuterLen = valueEnd - current;
            int valueContentOffset = valueOuterOffset;
            int valueContentLen = valueOuterLen;

            if (isValueSurroundedByCommas(buffer, valueOuterOffset, valueOuterLen)) {
                valueContentOffset = valueOuterOffset + 1;
                valueContentLen = valueOuterLen - 2;
            }

            handler.handleAttribute(
                    buffer,                                                               // name
                    attributeNameOffset, attributeNameLen,                                // name
                    attributeNameLine, attributeNameCol,                                  // name
                    operatorOffset, operatorLen,                                          // operator
                    operatorLine, operatorCol,                                            // operator
                    valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, // value
                    currentArtifactLine, currentArtifactCol);                             // value

            i = valueEnd;
            current = i;

        }

    }




    private static boolean isValueSurroundedByCommas(final char[] buffer, final int offset, final int len) {
        return len >= 2 && ((buffer[offset] == '"' && buffer[offset + len - 1] == '"') || (buffer[offset] == '\'' && buffer[offset + len - 1] == '\''));
    }


}

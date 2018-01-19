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
 * Class containing utility methods for general text parsing.
 *
 * @author Daniel Fernandez
 * @since 3.0.0
 */
final class TextParsingUtil {


    
    private TextParsingUtil() {
        super();
    }


    
    
    

    static int findNextStructureEndAvoidQuotes(
            final char[] text, final int offset, final int maxi, 
            final int[] locator) {
        
        boolean inQuotes = false;
        boolean inApos = false;

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '"' && !inApos) {
                inQuotes = !inQuotes;
            } else if (c == '\'' && !inQuotes) {
                inApos = !inApos;
            } else if (c == ']' && !inQuotes && !inApos) {
                locator[1] += (i - colIndex);
                return i;
            }

            i++;
            
        }
            
        locator[1] += (maxi - colIndex);
        return -1;
        
    }


    static int findNextCommentBlockEnd(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (i > offset && c == '/' && text[i - 1] == '*') {
                locator[1] += (i - colIndex);
                return i;
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }


    static int findNextCommentLineEnd(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '\n') {
                locator[1] += (i - colIndex);
                return i;
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }


    static int findNextLiteralEnd(
            final char[] text, final int offset, final int maxi,
            final int[] locator, final char literalMarker) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (i > offset && c == literalMarker) {
                if (isLiteralDelimiter(text, offset, i)) {
                    locator[1] += (i - colIndex);
                    return i;
                }
            }

            i++;

        }

        locator[1] += (maxi - colIndex);
        return -1;

    }

    


    static int findNextStructureStartOrLiteralMarker(
            final char[] text, final int offset, final int maxi, 
            final int[] locator, final boolean processCommentsAndLiterals) {

        char c;

        int colIndex = offset;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];
            
            if (c == '\n') {
                colIndex = i;
                locator[1] = 0;
                locator[0]++;
            } else if (c == '[') { // '[' is for elements
                locator[1] += (i - colIndex);
                return i;
            } else if (processCommentsAndLiterals) {
                if (c == '/') { // '/' is for comments (/*...*/, //...\n) or regexp literals (/.../g)
                    locator[1] += (i - colIndex);
                    return i;
                } else if (c == '\'' || c == '"' || c == '`') { // literal markers (',") and template literals (`)
                    if (isLiteralDelimiter(text, offset, i)) { // check it is not escaped
                        locator[1] += (i - colIndex);
                        return i;
                    }
                }
            }

            i++;

        }
            
        locator[1] += (maxi - colIndex);
        return -1;
        
    }


    private static boolean isLiteralDelimiter(final char[] text, final int offset, final int i) {
        int escapes = 0;
        int j = i - 1;
        while (j >= offset && text[j--] == '\\') {
            escapes++;
        }
        return escapes % 2 == 0;
    }



    static int findNextWhitespaceCharWildcard(
            final char[] text, final int offset, final int maxi, 
            final boolean avoidQuotes, final int[] locator) {
        
        boolean inQuotes = false;
        boolean inApos = false;

        char c;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];
            
            if (avoidQuotes && !inApos && c == '"') {
                inQuotes = !inQuotes;
            } else if (avoidQuotes && !inQuotes && c == '\'') {
                inApos = !inApos;
            } else if (!inQuotes && !inApos && (c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f'
                    || c == '\u000B' || c == '\u001C' || c == '\u001D' || c == '\u001E' || c == '\u001F'
                    || (c > '\u007F' && Character.isWhitespace(c)))) {
                return i;
            }

            ParsingLocatorUtil.countChar(locator, c);

            i++;

        }

        return -1;

    }


    static int findNextNonWhitespaceCharWildcard(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        char c;
        boolean isWhitespace;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];
            isWhitespace = (c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == '\u000B'
                    || c == '\u001C' || c == '\u001D' || c == '\u001E' || c == '\u001F'
                    || (c > '\u007F' && Character.isWhitespace(c)));

            if (!isWhitespace) {
                return i;
            }

            ParsingLocatorUtil.countChar(locator, c);

            i++;

        }

        return -1;

    }


    static int findNextOperatorCharWildcard(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c == '=' || (c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == '\u000B'
                    || c == '\u001C' || c == '\u001D' || c == '\u001E' || c == '\u001F'
                    || (c > '\u007F' && Character.isWhitespace(c)))) {
                return i;
            }

            ParsingLocatorUtil.countChar(locator, c);

            i++;

        }

        return -1;

    }


    static int findNextNonOperatorCharWildcard(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        char c;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (c != '=' && !(c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == '\u000B'
                    || c == '\u001C' || c == '\u001D' || c == '\u001E' || c == '\u001F'
                    || (c > '\u007F' && Character.isWhitespace(c)))) {
                return i;
            }

            ParsingLocatorUtil.countChar(locator, c);

            i++;

        }

        return -1;

    }


    static int findNextAnyCharAvoidQuotesWildcard(
            final char[] text, final int offset, final int maxi,
            final int[] locator) {

        boolean inQuotes = false;
        boolean inApos = false;

        char c;

        int i = offset;
        int n = (maxi - offset);

        while (n-- != 0) {

            c = text[i];

            if (!inApos && c == '"') {
                if (inQuotes) {
                    ParsingLocatorUtil.countChar(locator, c);
                    i++;
                    return (i < maxi ? i : -1);
                }
                inQuotes = true;
            } else if (!inQuotes && c == '\'') {
                if (inApos) {
                    ParsingLocatorUtil.countChar(locator, c);
                    i++;
                    return (i < maxi ? i : -1);
                }
                inApos = true;
            } else if (!inQuotes && !inApos) {
                return i;
            }

            ParsingLocatorUtil.countChar(locator, c);

            i++;

        }

        return -1;

    }

}

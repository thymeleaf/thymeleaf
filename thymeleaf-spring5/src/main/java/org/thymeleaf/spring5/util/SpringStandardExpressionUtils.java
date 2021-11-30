/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2020, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.util;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.12
 *
 */
public final class SpringStandardExpressionUtils {


    private static final char[] NEW_ARRAY = "wen".toCharArray(); // Inverted "new"
    private static final int NEW_LEN = NEW_ARRAY.length;


    public static boolean containsSpELInstantiationOrStatic(final String expression) {

        /*
         * Checks whether the expression contains instantiation of objects ("new SomeClass") or makes use of
         * static methods ("T(SomeClass)") as both are forbidden in certain contexts in restricted mode.
         */

        final int explen = expression.length();
        int n = explen;
        int ni = 0; // index for computing position in the NEW_ARRAY
        int si = -1;
        char c;
        while (n-- != 0) {

            c = expression.charAt(n);

            // When checking for the "new" keyword, we need to identify that it is not a part of a larger
            // identifier, i.e. there is whitespace after it and no character that might be a part of an
            // identifier before it.
            if (ni < NEW_LEN
                    && c == NEW_ARRAY[ni]
                    && (ni > 0 || ((n + 1 < explen) && Character.isWhitespace(expression.charAt(n + 1))))) {
                ni++;
                if (ni == NEW_LEN && (n == 0 || !Character.isJavaIdentifierPart(expression.charAt(n - 1)))) {
                    return true; // we found an object instantiation
                }
                continue;
            }

            if (ni > 0) {
                // We 'restart' the matching counter just in case we had a partial match
                n += ni;
                ni = 0;
                if (si < n) {
                    // This has to be restarted too
                    si = -1;
                }
                continue;
            }

            ni = 0;

            if (c == ')') {
                si = n;
            } else if (si > n && c == '('
                        && ((n - 1 >= 0) && isPreviousStaticMarker(expression, n))) {
                return true;
            } else if (si > n && !(Character.isJavaIdentifierPart(c) || c == '.')) {
                si = -1;
            }

        }

        return false;

    }


    private static boolean isPreviousStaticMarker(final String expression, final int idx) {
        char c,c1;
        int n = idx;
        while (n-- != 0) {
            c = expression.charAt(n);
            if (c == 'T') {
                if (n == 0) {
                    return true;
                }
                c1 = expression.charAt(n - 1);
                return !((c1 >= 'A' && c1 <= 'Z') || (c1 >= 'a' && c1 <= 'z') || (c1 >= '0' && c1 <= '9') || c1 == '_');
            } else if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return false;
    }


    private SpringStandardExpressionUtils() {
        super();
    }



}

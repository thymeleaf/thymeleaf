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
package org.thymeleaf.spring6.util;

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
    private static final char[] PARAM_ARRAY = "marap".toCharArray(); // Inverted "param"
    private static final int PARAM_LEN = PARAM_ARRAY.length;


    public static boolean containsSpELInstantiationOrStaticOrParam(final String expression) {

        /*
         * Checks whether the expression contains instantiation of objects ("new SomeClass") or makes use of
         * static methods ("T(SomeClass)") as both are forbidden in certain contexts in restricted mode.
         */

        final int explen = expression.length();
        int n = explen;
        int ni = 0; // index for computing position in the NEW_ARRAY
        int pi = 0; // index for computing position in the PARAM_ARRAY
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
                if (ni == NEW_LEN && (n == 0 || !isSafeIdentifierChar(expression.charAt(n - 1)))) {
                    return true; // we found an object instantiation
                }
                continue;
            }

            if (ni > 0) {
                // We 'restart' the matching counter just in case we had a partial match
                n += ni;
                ni = 0;
                continue;
            }

            ni = 0;

            // When checking for the "param" keyword, we need to identify that it is not a part of a larger
            // identifier.
            if (pi < PARAM_LEN
                    && c == PARAM_ARRAY[pi]
                    && (pi > 0 || ((n + 1 < explen) && !isSafeIdentifierChar(expression.charAt(n + 1))))) {
                pi++;
                if (pi == PARAM_LEN && (n == 0 || !isSafeIdentifierChar(expression.charAt(n - 1)))) {
                    return true; // we found a param access
                }
                continue;
            }

            if (pi > 0) {
                // We 'restart' the matching counter just in case we had a partial match
                n += pi;
                pi = 0;
                continue;
            }

            pi = 0;

            if (c == '(' && ((n - 1 >= 0) && isPreviousStaticMarker(expression, n))) {
                return true;
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
                return !isSafeIdentifierChar(c1);
            } else if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return false;
    }


    private static boolean isSafeIdentifierChar(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_';
    }


    private SpringStandardExpressionUtils() {
        super();
    }



}

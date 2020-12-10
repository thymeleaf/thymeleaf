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
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardExpressionUtils {


    private static final char[] EXEC_INFO_ARRAY = "ofnIcexe".toCharArray(); // Inverted "execInfo"
    private static final int EXEC_INFO_LEN = EXEC_INFO_ARRAY.length;

    private static final char[] NEW_ARRAY = "wen".toCharArray(); // Inverted "new"
    private static final int NEW_LEN = NEW_ARRAY.length;


    public static boolean mightNeedExpressionObjects(final String expression) {
        /*
         * Checks for 'execInfo' here are performed since 3.0.0 because in that version the previous execInfo context
         * variable (${execInfo}) was converted into an expression object (${#execInfo}). Since 3.0.0, given execInfo is
         * only added as an expression object, we use this to detect not only when any expression objects might be
         * called (by means of looking for '#' symbols), but also to detect when the 'execInfo' context variable might
         * be called either, so that we make sure the corresponding expression object (to which we will later redirect
         * the call at the property accessor level) is included in the context.
         *
         * IMPORTANT: This 'execInfo' automatic forwarding is considered deprecated since 3.0.0, and it will be removed
         *            in Thymeleaf 3.1, so this 'execInfo' check should be removed too by then (only the '#' check
         *            should remain).
         */
        int n = expression.length();
        int ei = 0; // index for computing position in the EXEC_INFO_ARRAY
        char c;
        while (n-- != 0) {
            c = expression.charAt(n);
            if (c == '#') {
                return true;
            } else if (c == EXEC_INFO_ARRAY[ei]) {
                ei++;
                if (ei == EXEC_INFO_LEN) {
                    return true; // we found the "execInfo" keyword, so we might need expression objects
                }
            } else {
                if (ei > 0) {
                    // We 'restart' the counter (just after the first matched char) in case we had a partial match
                    n += ei;
                }
                ei = 0;
            }
        }
        return false;
    }


    /**
     *
     * @since 3.0.12
     */
    public static boolean containsOGNLInstantiationOrStatic(final String expression) {

        /*
         * Checks whether the expression contains instantiation of objects ("new SomeClass") or makes use of
         * static methods ("@SomeClass@") as both are forbidden in certain contexts in restricted mode.
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

            if (c == '@') {
                if (si > n) {
                    return true;
                }
                si = n;
            } else if (si > n && !(Character.isJavaIdentifierPart(c) || c == '.')) {
                si = -1;
            }

        }

        return false;

    }





    private StandardExpressionUtils() {
        super();
    }



}

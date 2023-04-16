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

import org.thymeleaf.util.ExpressionUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class StandardExpressionUtils {


    private static final char[] NEW_ARRAY = "wen".toCharArray(); // Inverted "new"
    private static final int NEW_LEN = NEW_ARRAY.length;
    private static final char[] PARAM_ARRAY = "marap".toCharArray(); // Inverted "param"
    private static final int PARAM_LEN = PARAM_ARRAY.length;


    public static boolean mightNeedExpressionObjects(final String expression) {
        return expression.indexOf('#') >= 0;
    }


    /*
     * @since 3.0.12
     */
    public static boolean containsOGNLInstantiationOrStaticOrParam(final String expression) {

        /*
         * Checks whether the expression contains instantiation of objects ("new SomeClass") or makes use of
         * static methods ("@SomeClass@") as both are forbidden in certain contexts in restricted mode.
         */

        final String exp = ExpressionUtils.normalize(expression);

        final int explen = exp.length();
        int n = explen;
        int ni = 0; // index for computing position in the NEW_ARRAY
        int pi = 0; // index for computing position in the PARAM_ARRAY
        int si = -1;
        char c;
        while (n-- != 0) {

            c = exp.charAt(n);

            // When checking for the "new" keyword, we need to identify that it is not a part of a larger
            // identifier, i.e. there is whitespace after it and no character that might be a part of an
            // identifier before it.
            if (ni < NEW_LEN
                    && c == NEW_ARRAY[ni]
                    && (ni > 0 || ((n + 1 < explen) && Character.isWhitespace(exp.charAt(n + 1))))) {
                ni++;
                if (ni == NEW_LEN && (n == 0 || !isSafeIdentifierChar(exp.charAt(n - 1)))) {
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

            // When checking for the "param" keyword, we need to identify that it is not a part of a larger
            // identifier.
            if (pi < PARAM_LEN
                    && c == PARAM_ARRAY[pi]
                    && (pi > 0 || ((n + 1 < explen) && !isSafeIdentifierChar(exp.charAt(n + 1))))) {
                pi++;
                if (pi == PARAM_LEN && (n == 0 || !isSafeIdentifierChar(exp.charAt(n - 1)))) {
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

            if (c == '@') {
                if (si > n) {
                    return true;
                }
                si = n;
            } else if (si > n && !(Character.isJavaIdentifierPart(c) || Character.isWhitespace(c) || c == '.')) {
                si = -1;
            }

        }

        return false;

    }


    private static boolean isSafeIdentifierChar(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_';
    }





    private StandardExpressionUtils() {
        super();
    }



}

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
                    // We 'restart' the matching counter just in case we had a partial match
                    n += ei;
                }
                ei = 0;
            }
        }
        return false;
    }






    private StandardExpressionUtils() {
        super();
    }



}

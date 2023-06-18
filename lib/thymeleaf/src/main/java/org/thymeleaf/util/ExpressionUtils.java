/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2022, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.util;

public final class ExpressionUtils {

    public static String normalize(final String expression) {
        if (expression == null) {
            return expression;
        }
        StringBuilder strBuilder = null;
        final int expLen = expression.length();
        char c;
        for (int i = 0; i < expLen; i++) {
            c = expression.charAt(i);
            if (c != '\n' && (c < '\u0020' || (c >= '\u007F' && c <= '\u009F'))) {
                if (strBuilder == null) {
                    strBuilder = new StringBuilder(expLen);
                    strBuilder.append(expression, 0, i);
                }
            } else if (strBuilder != null) {
                strBuilder.append(c);
            }
        }
        return strBuilder == null ? expression : strBuilder.toString();
    }

    private ExpressionUtils() {
        super();
    }

}

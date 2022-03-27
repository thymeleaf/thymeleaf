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

package org.thymeleaf.testing.templateengine.util;

import org.apache.commons.lang3.StringUtils;

public class TestNamingUtils {

    private TestNamingUtils() {
        super();
    }



    public static String normalizeTestName(final String testName) {

        String normalizedName = StringUtils.stripAccents(testName);
        if (normalizedName.contains("/")) {
            normalizedName = normalizedName.substring(normalizedName.lastIndexOf('/'));
        }
        if (normalizedName.contains("\\")) {
            normalizedName = normalizedName.substring(normalizedName.lastIndexOf('\\'));
        }

        if (normalizedName.endsWith(".thtest")) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 7);
        }

        final StringBuilder strBuilder = new StringBuilder();
        final int nameLen = normalizedName.length();
        for (int i = 0; i < nameLen; i++) {
            final char c = normalizedName.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }


}

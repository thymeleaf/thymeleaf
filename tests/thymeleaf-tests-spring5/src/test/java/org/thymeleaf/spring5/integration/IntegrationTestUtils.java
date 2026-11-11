/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.integration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IntegrationTestUtils {

    public static final String TEMPLATE_PREFIX = "classpath:/integration/templates/";
    public static final String MESSAGES_BASENAME = "integration/i18n/messages";


    // Returns the markup of the first element with the specified id (no nesting of same-name elements expected)
    public static String element(final String html, final String id) {
        final Matcher matcher =
                Pattern.compile("<(\\w+)[^>]*\\sid=\"" + Pattern.quote(id) + "\"[^>]*>.*?</\\1>", Pattern.DOTALL).matcher(html);
        if (!matcher.find()) {
            throw new AssertionError("No element with id \"" + id + "\" found in:\n" + html);
        }
        return matcher.group();
    }


    // Returns the start tag of the first element with the specified id
    public static String tag(final String html, final String id) {
        final Matcher matcher = Pattern.compile("<\\w+[^>]*\\sid=\"" + Pattern.quote(id) + "\"[^>]*>").matcher(html);
        if (!matcher.find()) {
            throw new AssertionError("No element with id \"" + id + "\" found in:\n" + html);
        }
        return matcher.group();
    }


    // Returns the text content of the first element with the specified id
    public static String text(final String html, final String id) {
        final String element = element(html, id);
        return element.substring(element.indexOf('>') + 1, element.lastIndexOf('<'));
    }


    private IntegrationTestUtils() {
        super();
    }

}

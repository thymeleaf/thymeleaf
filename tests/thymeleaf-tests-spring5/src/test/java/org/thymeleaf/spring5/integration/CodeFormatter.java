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

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public final class CodeFormatter implements Formatter<Code> {

    @Override
    public String print(final Code code, final Locale locale) {
        return "[" + code.getValue() + "]";
    }

    @Override
    public Code parse(final String text, final Locale locale) throws ParseException {
        if (text.length() < 2 || text.charAt(0) != '[' || text.charAt(text.length() - 1) != ']') {
            throw new ParseException("Not a code: " + text, 0);
        }
        return new Code(text.substring(1, text.length() - 1));
    }

}

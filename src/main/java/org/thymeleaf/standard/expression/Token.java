/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class Token implements Serializable {
    
    
    private static final long serialVersionUID = 1375274391088787250L;
    
    
    private static final String VALID_TOKEN_NAME_START_END_CHARS =
        "~:A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD" +
        "\\-\\.0-9/\\\\\u00B7\u0300-\u036F\u203F-\u2040#";
    private static final String VALID_TOKEN_NAME_CHARS = VALID_TOKEN_NAME_START_END_CHARS + " ";
    
    private static final String TOKEN_NAME_START_END_CHARS = "[" + VALID_TOKEN_NAME_START_END_CHARS + "]";
    private static final String TOKEN_NAME_CHARS = "[" + VALID_TOKEN_NAME_CHARS + "]";    

    private static final String TOKEN = TOKEN_NAME_START_END_CHARS + "(?:(?:" + TOKEN_NAME_CHARS + "*?)" + TOKEN_NAME_START_END_CHARS + ")?";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^" + TOKEN + "$");

    
    
    private final String value;
    
    
    
    Token(final String value) {
        super();
        Validate.notNull(value, "Value cannot be null");
        this.value = value;
    }
    
    
    public String getValue() {
        return this.value;
    }
    
    public String getStringRepresentation() {
        return this.value;
    }
    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }

    
    
    
    public static Token parse(final String input) {
        if (input == null) {
            return null;
        }
        if (TOKEN_PATTERN.matcher(input.trim()).matches()) {
            return new Token(input.trim());
        }
        return null;
    }
    
    
}

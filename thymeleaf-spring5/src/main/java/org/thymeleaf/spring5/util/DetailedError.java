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
package org.thymeleaf.spring5.util;

import org.thymeleaf.util.Validate;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class DetailedError {

    private static final String GLOBAL_FIELD_NAME = "[global]";

    private final String fieldName;
    private final String code;
    private final Object[] arguments;
    private final String message;



    public DetailedError(final String code, final Object[] arguments, final String message) {
        this(GLOBAL_FIELD_NAME, code, arguments, message);
    }

    public DetailedError(
            final String fieldName, final String code, final Object[] arguments, final String message) {
	    super();
        Validate.notNull(fieldName, "Field name cannot be null");
        Validate.notNull(message, "Message cannot be null");
        this.fieldName = fieldName;
        this.code = code;
        this.arguments = arguments;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getMessage() {
        return message;
    }

    public boolean isGlobal() {
        return GLOBAL_FIELD_NAME.equalsIgnoreCase(this.fieldName);
    }

    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.fieldName);
        strBuilder.append(":");
        strBuilder.append(this.message);
        return strBuilder.toString();
    }


}

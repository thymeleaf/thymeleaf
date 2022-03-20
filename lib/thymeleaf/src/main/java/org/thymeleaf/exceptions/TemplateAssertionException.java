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
package org.thymeleaf.exceptions;

/**
 * <p>
 *   Exception raised when a template assertion is not valid.
 * </p>
 * <p>
 *   In the Standard Dialects, this exception might be raised by the
 *   {@code th:assert} attribute.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
public class TemplateAssertionException extends RuntimeException {

    private static final long serialVersionUID = -2261382147273524844L;

    private static final String ASSERTION_MESSAGE = "Assertion '%s' not valid in template '%s'";
    private static final String ASSERTION_MESSAGE_LINE_COL = "Assertion '%s' not valid in template '%s', line %d col %d";




    public TemplateAssertionException(final String assertionExpression,
            final String templateName, final int line, final int col) {
        super(createMessage(assertionExpression, templateName, Integer.valueOf(line), Integer.valueOf(col)));
    }

    public TemplateAssertionException(final String assertionExpression,
            final String templateName) {
        super(createMessage(assertionExpression, templateName, null, null));
    }


    private static String createMessage(final String assertionExpression,
            final String templateName, final Integer line, final Integer col) {
        if (line == null || col == null) {
            return String.format(ASSERTION_MESSAGE, assertionExpression, templateName);
        }
        return String.format(ASSERTION_MESSAGE_LINE_COL, assertionExpression, templateName, line, col);
    }

}

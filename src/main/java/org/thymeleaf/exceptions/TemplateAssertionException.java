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
package org.thymeleaf.exceptions;

/**
 * <p>
 *   Exception raised when a template assertion is not valid.
 * </p>
 * <p>
 *   In the Standard Dialects, this exception might be raised by the
 *   <tt>th:assert</tt> attribute.
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
    private static final String ASSERTION_MESSAGE_LINE = "Assertion '%s' not valid in template '%s', line %d";




    public TemplateAssertionException(final String assertionExpression,
            final String templateName, final Integer line) {
        super(createMessage(assertionExpression, templateName, line));
    }

    public TemplateAssertionException(final String assertionExpression,
            final String templateName) {
        super(createMessage(assertionExpression, templateName, null));
    }


    private static String createMessage(final String assertionExpression,
            final String templateName, final Integer line) {
        if (line == null) {
            return String.format(ASSERTION_MESSAGE, assertionExpression, templateName);
        }
        return String.format(ASSERTION_MESSAGE_LINE, assertionExpression, templateName, line);
    }

}

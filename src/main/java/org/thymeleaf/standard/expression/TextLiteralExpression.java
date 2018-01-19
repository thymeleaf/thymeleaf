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
package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.Validate;



/**
 * <p>
 *   Text literal (Thymeleaf Standard Expressions)
 * </p>
 * <p>
 *   Note a class with this name existed since 1.1, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
public final class TextLiteralExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(TextLiteralExpression.class);

    
    private static final long serialVersionUID = 6511847028638506552L;

    static final char ESCAPE_PREFIX = '\\';
    static final char DELIMITER = '\'';

    
    private final LiteralValue value;

    
    public TextLiteralExpression(final String value) {
        super();
        Validate.notNull(value, "Value cannot be null");
        this.value = new LiteralValue(unwrapLiteral(value));
    }
    
    
    
    public LiteralValue getValue() {
        return this.value;
    }


    private static String unwrapLiteral(final String input) {
        // We know input is not null
        final int inputLen = input.length();
        if (inputLen > 1 && input.charAt(0) == '\'' && input.charAt(inputLen - 1) == '\'') {
            return unescapeLiteral(input.substring(1, inputLen - 1));
        }
        return input;
    }

    
    @Override
    public String getStringRepresentation() {
        return String.valueOf(DELIMITER) + 
               this.value.getValue().replace(String.valueOf(DELIMITER),("\\" + DELIMITER)) + 
               String.valueOf(DELIMITER);
    }


    
    static TextLiteralExpression parseTextLiteralExpression(final String input) {
        return new TextLiteralExpression(input);
        
    }
    

    
    static Object executeTextLiteralExpression(
            final IExpressionContext context,
            final TextLiteralExpression expression,
            final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating text literal: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }



    public static String wrapStringIntoLiteral(final String str) {

        if (str == null) {
            return null;
        }

        int n = str.length();
        while (n-- != 0) {

            if (str.charAt(n) == '\'') {

                final StringBuilder strBuilder = new StringBuilder(str.length() + 5);

                strBuilder.append('\'');
                final int strLen = str.length();
                for (int i = 0; i < strLen; i++) {
                    final char c = str.charAt(i);
                    if (c == '\'') {
                        strBuilder.append('\\');
                    }
                    strBuilder.append(c);
                }
                strBuilder.append('\'');

                return strBuilder.toString();

            }

        }

        return '\'' + str + '\'';

    }



    static boolean isDelimiterEscaped(final String input, final int pos) {
        // Only an odd number of \'s will indicate escaping
        if (pos == 0 || input.charAt(pos - 1) != '\\') {
            return false;
        }
        int i = pos - 1;
        boolean odd = false;
        while (i >= 0) {
            if (input.charAt(i) == '\\') {
                odd = !odd;
            } else {
                return odd;
            }
            i--;
        }
        return odd;
    }




    /*
     * This unescape operation will perform two transformations:
     *   \' -> '
     *   \\ -> \
     */
    private static String unescapeLiteral(final String text) {

        if (text == null) {
            return null;
        }

        StringBuilder strBuilder = null;

        final int max = text.length();

        int readOffset = 0;
        int referenceOffset = 0;

        char c;

        for (int i = 0; i < max; i++) {

            c = text.charAt(i);

            /*
             * Check the need for an unescape operation at this point
             */

            if (c != ESCAPE_PREFIX || (i + 1) >= max) {
                continue;
            }

            if (c == ESCAPE_PREFIX) {

                switch (text.charAt(i + 1)) {
                    case '\'': c = '\''; referenceOffset = i + 1; break;
                    case '\\': c = '\\'; referenceOffset = i + 1; break;
                    // We weren't able to consume any valid escape chars, just not consider it an escape operation
                    default: referenceOffset = i; break;
                }

            }


            /*
             * At this point we know for sure we will need some kind of unescape, so we
             * can increase the offset and initialize the string builder if needed, along with
             * copying to it all the contents pending up to this point.
             */

            if (strBuilder == null) {
                strBuilder = new StringBuilder(max + 5);
            }

            if (i - readOffset > 0) {
                strBuilder.append(text, readOffset, i);
            }

            i = referenceOffset;
            readOffset = i + 1;

            /*
             * Write the unescaped char
             */
            strBuilder.append(c);

        }


        /*
         * -----------------------------------------------------------------------------------------------
         * Final cleaning: return the original String object if no unescape was actually needed. Otherwise
         *                 append the remaining escaped text to the string builder and return.
         * -----------------------------------------------------------------------------------------------
         */

        if (strBuilder == null) {
            return text;
        }

        if (max - readOffset > 0) {
            strBuilder.append(text, readOffset, max);
        }

        return strBuilder.toString();

    }


}

/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public final class TextLiteralExpression extends SimpleExpression {
    
    private static final Logger logger = LoggerFactory.getLogger(TextLiteralExpression.class);

    
    private static final long serialVersionUID = 6511847028638506552L;

    static final char DELIMITER = '\'';
    
    private static final Pattern DELIMITER_ESCAPE_PATTERN = Pattern.compile("\\\\'");
    
    
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
            final String unwrappedInput = input.substring(1, inputLen - 1);
            return DELIMITER_ESCAPE_PATTERN.matcher(unwrappedInput).replaceAll("'");
        }
        return input;
    }

    
    @Override
    public String getStringRepresentation() {
        return String.valueOf(DELIMITER) + 
               this.value.getValue().replace(String.valueOf(DELIMITER),("\\" + DELIMITER)) + 
               String.valueOf(DELIMITER);
    }


    
    static TextLiteralExpression parseTextLiteral(final String input) {
        return new TextLiteralExpression(input);
        
    }
    

    
    static Object executeTextLiteral(
            @SuppressWarnings("unused") final IProcessingContext processingContext, 
            final TextLiteralExpression expression,
            @SuppressWarnings("unused") final StandardExpressionExecutionContext expContext) {

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating text literal: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        
        return expression.getValue();
        
    }



    public static String wrapStringIntoLiteral(final String str) {

        if (str == null) {
            return null;
        }

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



    static boolean isDelimiterEscaping(final String input, final int pos) {
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

    
}

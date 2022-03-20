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

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public abstract class Token extends SimpleExpression {

    
    private static final long serialVersionUID = 4357087922344497120L;
    
    
    private final Object value;

    
    protected Token(final Object value) {
        super();
        this.value = value;
    }
    
    
    public Object getValue() {
        return this.value;
    }
    
    public String getStringRepresentation() {
        return this.value.toString(); // Tokens are fine not using the conversion service
    }
    
    
    @Override
    public String toString() {
        return getStringRepresentation();
    }

    


    public static boolean isTokenChar(final String context, final int pos) {

        /*
         * TOKEN chars: A-Za-z0-9[]._ (plus '-' in some contexts)
         * (additionally, also, a series of internationalized characters: accents, other alphabets, etc.)
         *
         * '-' can also be a numeric operator, so it will only be considered a token char if:
         *    * there are immediately previous chars which we can consider a token, but not a numeric token
         *    * there are immediately following chars which we can consider a token, but not a numeric token
         *
         */

        final char c = context.charAt(pos);

        /*
         * First, the most common true's
         */
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        /*
         * The most common false's now, for failing fast
         */
        if (c == ' ' || c == '\n' || c == '(' || c == ')' || c == '\'' || c == '"' ||
                c == '<' || c == '>' || c == '{' || c == '}' ||
                c == '=' || c == ',' || c == ';' || c == ':' || c == '+' ||
                c == '*' || c == '$' || c == '%' || c == '&' || c == '#') {
            return false;
        }
        /*
         * Some more (less common) true's
         */
        if (c == '[' || c == ']' || c == '.' || c == '_') {
            return true;
        }
        /*
         * A special case: the dash
         */
        if (c == '-') {

            if (pos > 0) {
                // let's scan backwards, looking for a token char that is not a number

                for (int i = pos - 1; i >= 0; i--) {
                    if (!isTokenChar(context,i)) {
                        break;
                    }
                    final char cc = context.charAt(i);
                    if (!((cc >= '0' && cc <= '9') || cc == '.')) {
                        // It is a token, but not a digit or ., so the dash is not an operator, it is a token char
                        return true;
                    }
                }

            }

            final int contextLen = context.length();
            if (pos + 1 < contextLen) {
                // let's scan forward, looking for a token char that is not a number

                for (int i = pos + 1; i < contextLen; i++) {
                    final char cc = context.charAt(i);
                    if (cc == '-') {
                        // We need to avoid cycles (which would happen if we call "isTokenChar" again)
                        return true;
                    }
                    if (!isTokenChar(context,i)) {
                        break;
                    }
                    if (!((cc >= '0' && cc <= '9') || cc == '.')) {
                        // It is a token, but not a digit or ., so the dash is not an operator, it is a token char
                        return true;
                    }
                }

            }

            return false;

        }
        /*
         * Finally, the rest of the true's
         */
        if (c == '\u00B7') {
            return true;
        }
        if (c >= '\u00C0' && c <= '\u00D6') {
            return true;
        }
        if (c >= '\u00D8' && c <= '\u00F6') {
            return true;
        }
        if (c >= '\u00F8' && c <= '\u02FF') {
            return true;
        }
        if (c >= '\u0300' && c <= '\u036F') {
            return true;
        }
        if (c >= '\u0370' && c <= '\u037D') {
            return true;
        }
        if (c >= '\u037F' && c <= '\u1FFF') {
            return true;
        }
        if (c >= '\u200C' && c <= '\u200D') {
            return true;
        }
        if (c >= '\u203F' && c <= '\u2040') {
            return true;
        }
        if (c >= '\u2070' && c <= '\u218F') {
            return true;
        }
        if (c >= '\u2C00' && c <= '\u2FEF') {
            return true;
        }
        if (c >= '\u3001' && c <= '\uD7FF') {
            return true;
        }
        if (c >= '\uF900' && c <= '\uFDCF') {
            return true;
        }
        if (c >= '\uFDF0' && c <= '\uFFFD') {
            return true;
        }
        return (c >= '\uFDF0' && c <= '\uFFFD');
    }





    public static final class TokenParsingTracer {

        public static final char TOKEN_SUBSTITUTE = '#';

        private TokenParsingTracer() {
            super();
        }

        public static String trace(final String input) {

            final int inputLen = input.length();

            final StringBuilder strBuilder = new StringBuilder(inputLen + 1);

            for (int i = 0; i < inputLen; i++) {
                if (isTokenChar(input, i)) {
                    strBuilder.append(TOKEN_SUBSTITUTE);
                } else {
                    strBuilder.append(input.charAt(i));
                }
            }

            return strBuilder.toString();

        }

    }




}

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.context.IExpressionContext;


/**
 * <p>
 *   Expression preprocessor, in charge of executing {@code __...__} fragments in Thymeleaf Standard Expressions.
 * </p>
 * <p>
 *   Note a class with this name existed since 2.1.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 3.0.0
 *
 */
final class StandardExpressionPreprocessor {


    private static final char PREPROCESS_DELIMITER = '_';
    private static final String PREPROCESS_EVAL = "\\_\\_(.*?)\\_\\_";
    private static final Pattern PREPROCESS_EVAL_PATTERN = Pattern.compile(PREPROCESS_EVAL, Pattern.DOTALL);





    static String preprocess(
            final IExpressionContext context,
            final String input) {

        if (input.indexOf(PREPROCESS_DELIMITER) == -1) {
            // Fail quick
            return input;
        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        if (!(expressionParser instanceof StandardExpressionParser)) {
            // Preprocess will be only available for the StandardExpressionParser, because the preprocessor
            // depends on this specific implementation of the parser.
            return input;
        }

        final Matcher matcher = PREPROCESS_EVAL_PATTERN.matcher(input);
        
        if (matcher.find()) {

            final StringBuilder strBuilder = new StringBuilder(input.length() + 24);
            int curr = 0;
            
            do {
                
                final String previousText = 
                        checkPreprocessingMarkUnescaping(input.substring(curr,matcher.start(0)));
                final String expressionText = 
                        checkPreprocessingMarkUnescaping(matcher.group(1));
                        
                strBuilder.append(previousText);
                
                final IStandardExpression expression =
                        StandardExpressionParser.parseExpression(context, expressionText, false);
                if (expression == null) {
                    return null;
                }
                
                final Object result = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
                
                strBuilder.append(result);
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            final String remaining = checkPreprocessingMarkUnescaping(input.substring(curr));
            
            strBuilder.append(remaining);
            
            return strBuilder.toString().trim();
            
        }
        
        return checkPreprocessingMarkUnescaping(input);
        
    }

    
    
    private static String checkPreprocessingMarkUnescaping(final String input) {
        
        boolean structureFound = false; // for fast failing
        
        byte state = 0; // 1 = \, 2 = _, 3 = \, 4 = _
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            final char c = input.charAt(i);
            if (c == '\\' && (state == 0 || state == 2)) {
                state++;
                continue;
            }
            if (c == '_' && state == 1) {
                state++;
                continue;
            }
            if (c == '_' && state == 3) {
                structureFound = true;
                break;
            }
            state = 0;
        }

        if (!structureFound) {
            // This avoids creating a new String object in the most common case (= nothing to unescape)
            return input;
        }


        state = 0; // 1 = \, 2 = _, 3 = \, 4 = _
        final StringBuilder strBuilder = new StringBuilder(inputLen + 6);
        for (int i = 0; i < inputLen; i++) {
            final char c = input.charAt(i);
            if (c == '\\' && (state == 0 || state == 2)) {
                state++;
                strBuilder.append('\\');
            } else if (c == '_' && state == 1) {
                state++;
                strBuilder.append('_');
            } else if (c == '_' && state == 3) {
                state = 0;
                final int builderLen = strBuilder.length(); 
                strBuilder.delete(builderLen - 3, builderLen);
                strBuilder.append("__");
            } else {
                state = 0;
                strBuilder.append(c);
            }
        }
        
        return strBuilder.toString();
        
    }
    
    


    private StandardExpressionPreprocessor() {
        super();
    }

    
}

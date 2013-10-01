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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.1.0
 *
 */
final class StandardExpressionPreprocessor {


    private static final char PREPROCESS_DELIMITER = '_';
    private static final String PREPROCESS_EVAL = "\\_\\_(.*?)\\_\\_";
    private static final Pattern PREPROCESS_EVAL_PATTERN = Pattern.compile(PREPROCESS_EVAL, Pattern.DOTALL);





    static String preprocess(final Configuration configuration,
            final IProcessingContext processingContext, final String input) {

        if (input.indexOf(PREPROCESS_DELIMITER) == -1) {
            // Fail quick
            return input;
        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        if (!(expressionParser instanceof StandardExpressionParser)) {
            // Preprocess will be only available for the StandardExpressionParser, because the preprocessor
            // depends on this specific implementation of the parser.
            return input;
        }

        final Matcher matcher = PREPROCESS_EVAL_PATTERN.matcher(input);
        
        if (matcher.find()) {

            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                final String previousText = 
                        checkPreprocessingMarkUnescaping(input.substring(curr,matcher.start(0)));
                final String expressionText = 
                        checkPreprocessingMarkUnescaping(matcher.group(1));
                        
                strBuilder.append(previousText);
                
                final IStandardExpression expression =
                        StandardExpressionParser.parseExpression(configuration, processingContext, expressionText, false);
                if (expression == null) {
                    return null;
                }
                
                final Object result =
                    expression.execute(configuration, processingContext, StandardExpressionExecutionContext.PREPROCESSING);
                
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
            } else if (c == '_' && state == 3) {
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
        final StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < inputLen; i++) {
            final char c = input.charAt(i);
            if (c == '\\' && (state == 0 || state == 2)) {
                state++;
                strBuilder.append(c);
            } else if (c == '_' && state == 1) {
                state++;
                strBuilder.append(c);
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

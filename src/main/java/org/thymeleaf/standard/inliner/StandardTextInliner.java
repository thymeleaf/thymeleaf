/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.inliner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.exceptions.ExpressionParsingException;
import org.thymeleaf.inliner.ITextInliner;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class StandardTextInliner implements ITextInliner {
    
    private static final Logger logger = LoggerFactory.getLogger(StandardTextInliner.class);

    public static final StandardTextInliner INSTANCE = new StandardTextInliner();

    
    public static final String TEXT_INLINE_EVAL = "\\[\\[(.*?)\\]\\]";
    public static final Pattern TEXT_INLINE_EVAL_PATTERN = Pattern.compile(TEXT_INLINE_EVAL, Pattern.DOTALL);
   
    
    
    
    private StandardTextInliner() {
        super();
    }
    
    

    public void inline(final Arguments arguments, final AbstractTextNode text) {
        final String content = text.getContent();
        final String textContent =
            processTextInline(content, arguments);
        text.setContent(textContent);
    }
    
    

    static String processTextInline(
            final String input, final Arguments arguments) {
        
        if (input == null || input.length() == 0) {
            return input;
        }
        
        final Matcher matcher = TEXT_INLINE_EVAL_PATTERN.matcher(input);
        
        if (matcher.find()) {
            
            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                strBuilder.append(input.substring(curr,matcher.start(0)));
                
                final String match = matcher.group(1);
                
                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][{}] Applying text inline evaluation on \"{}\"", TemplateEngine.threadIndex(), match);
                }
                
                try {
                    
                    final Object result =
                        StandardExpressionProcessor.processExpression(arguments, match);
                    
                    strBuilder.append(result);
                    
                } catch (final ExpressionParsingException e) {
                    
                    strBuilder.append(match);
                    
                }
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            strBuilder.append(input.substring(curr));
            
            return strBuilder.toString();
            
        }
        
        return input;
        
    }


}

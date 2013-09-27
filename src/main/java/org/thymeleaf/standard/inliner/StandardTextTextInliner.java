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
package org.thymeleaf.standard.inliner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class StandardTextTextInliner implements IStandardTextInliner {
    
    private static final Logger logger = LoggerFactory.getLogger(StandardTextTextInliner.class);

    public static final StandardTextTextInliner INSTANCE = new StandardTextTextInliner();

    
    public static final String TEXT_INLINE_EVAL = "\\[\\[(.*?)\\]\\]";
    public static final Pattern TEXT_INLINE_EVAL_PATTERN = Pattern.compile(TEXT_INLINE_EVAL, Pattern.DOTALL);
    
    public static final String SCRIPT_INLINE_PREFIX = "[[";
    public static final String SCRIPT_INLINE_SUFFIX = "]]";
   
    
    
    
    private StandardTextTextInliner() {
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
                            StandardExpressions.processExpression(arguments.getConfiguration(), arguments, match);
                    
                    strBuilder.append(result);
                    
                } catch (final TemplateProcessingException ignored) {
                    
                    // If it is not a standard expression, just output it as original
                    strBuilder.append(SCRIPT_INLINE_PREFIX).append(match).append(SCRIPT_INLINE_SUFFIX);
                    
                }
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            strBuilder.append(input.substring(curr));
            
            return strBuilder.toString();
            
        }
        
        return input;
        
    }


}

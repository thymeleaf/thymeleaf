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
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1.2
 *
 */
public abstract class AbstractStandardScriptingTextInliner implements IStandardTextInliner {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    public static final String SCRIPT_ADD_INLINE_EVAL = "/\\*\\[\\+(.*?)\\+\\]\\*\\/";
    public static final Pattern SCRIPT_ADD_INLINE_EVAL_PATTERN = Pattern.compile(SCRIPT_ADD_INLINE_EVAL, Pattern.DOTALL);

    public static final String SCRIPT_REMOVE_INLINE_EVAL = "\\/\\*\\[\\-(.*?)\\-\\]\\*\\/";
    public static final Pattern SCRIPT_REMOVE_INLINE_EVAL_PATTERN = Pattern.compile(SCRIPT_REMOVE_INLINE_EVAL, Pattern.DOTALL);

    public static final String SCRIPT_VARIABLE_EXPRESSION_INLINE_EVAL = "\\/\\*(\\[\\[(.*?)\\]\\])\\*\\/([^\n]*?)\n";
    public static final Pattern SCRIPT_VARIABLE_EXPRESSION_INLINE_EVAL_PATTERN = Pattern.compile(SCRIPT_VARIABLE_EXPRESSION_INLINE_EVAL, Pattern.DOTALL);

    public static final String SCRIPT_INLINE_EVAL = "\\[\\[(.*?)\\]\\]";
    public static final Pattern SCRIPT_INLINE_EVAL_PATTERN = Pattern.compile(SCRIPT_INLINE_EVAL, Pattern.DOTALL);
    
    public static final String SCRIPT_INLINE_PREFIX = "[[";
    public static final String SCRIPT_INLINE_SUFFIX = "]]";
   
    
    
    
    protected AbstractStandardScriptingTextInliner() {
        super();
    }
    

    
    public final void inline(final Arguments arguments, final AbstractTextNode text) {
        
        final String content = text.getContent();
        final String javascriptContent =
            processScriptingInline(content, arguments);
        text.setContent(javascriptContent);
        
    }

    
    
    

    private String processScriptingInline(
            final String input, final Arguments arguments) {
        
        return
            processScriptingVariableInline(
                processScriptingVariableExpressionInline(
                    processScriptingAddInline(
                        processScriptingRemoveInline(
                            input)
                        )
                    ),
                arguments);
            
    }

    
    
    private String processScriptingAddInline(final String input) {
        
        final Matcher matcher = SCRIPT_ADD_INLINE_EVAL_PATTERN.matcher(input);

        if (matcher.find()) {
            
            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                strBuilder.append(input.substring(curr,matcher.start(0)));
                
                final String match = matcher.group(1);
                
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("[THYMELEAF][{}] Adding inlined javascript text \"{}\"", TemplateEngine.threadIndex(), match);
                }
                
                strBuilder.append(match);
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            strBuilder.append(input.substring(curr));
            
            return strBuilder.toString();
            
        }
        
        return input;
        
    }


    
    private String processScriptingRemoveInline(final String input) {
        
        final Matcher matcher = SCRIPT_REMOVE_INLINE_EVAL_PATTERN.matcher(input);

        if (matcher.find()) {
            
            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                strBuilder.append(input.substring(curr,matcher.start(0)));
                
                final String match = matcher.group(1);
                
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("[THYMELEAF][{}] Removing inlined javascript text \"{}\"", TemplateEngine.threadIndex(), match);
                }
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            strBuilder.append(input.substring(curr));
            
            return strBuilder.toString();
            
        }
        
        return input;
    }
    


    
    
    
    private String processScriptingVariableExpressionInline(final String input) {
        
        final Matcher matcher = SCRIPT_VARIABLE_EXPRESSION_INLINE_EVAL_PATTERN.matcher(input);
        
        if (matcher.find()) {
            
            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                strBuilder.append(input.substring(curr,matcher.start(0)));
                
                strBuilder.append(matcher.group(1));
                
                strBuilder.append(computeLineEndForInline(matcher.group(3)));
                
                strBuilder.append('\n');
                
                curr = matcher.end(0);
                
            } while (matcher.find());
            
            strBuilder.append(input.substring(curr));
            
            return strBuilder.toString();
            
        }
        
        return input;
        
    }

    
    
    private static String computeLineEndForInline(final String lineRemainder) {
        
        if (lineRemainder == null) {
            return "";
        }

        char literalDelimiter = 0;
        int arrayLevel = 0;
        int objectLevel = 0;
        final int len = lineRemainder.length();
        for (int i = 0; i < len; i++) {
            final char c = lineRemainder.charAt(i);
            if (c == '\'' || c == '"') {
                if (literalDelimiter == 0 || i == 0) {
                    literalDelimiter = c;
                } else if (c == literalDelimiter && lineRemainder.charAt(i - 1) != '\\') {
                    literalDelimiter = 0;
                }
            } else if (c == '{' && literalDelimiter == 0) {
                objectLevel++;
            } else if (c == '}' && literalDelimiter == 0) {
                objectLevel--;
            } else if (c == '[' && literalDelimiter == 0) {
                arrayLevel++;
            } else if (c == ']' && literalDelimiter == 0) {
                arrayLevel--;
            }
            if (literalDelimiter == 0 && arrayLevel == 0 && objectLevel == 0) {
                if (c == ';' || c == ',' || c == ')') {
                    return lineRemainder.substring(i);
                }
                if (c == '/' && ((i+1) < len)) {
                    final char c1 = lineRemainder.charAt(i+1);
                    if (c1 == '/' || c1 == '*') {
                        return lineRemainder.substring(i);
                    }
                }
            }
        }

        return "";
        
    }
    
    
    
    
    
    private String processScriptingVariableInline(final String input, final Arguments arguments) {
        
        final Matcher matcher = SCRIPT_INLINE_EVAL_PATTERN.matcher(input);
        
        if (matcher.find()) {
            
            final StringBuilder strBuilder = new StringBuilder();
            int curr = 0;
            
            do {
                
                strBuilder.append(input.substring(curr,matcher.start(0)));
                
                final String match = matcher.group(1);
                
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("[THYMELEAF][{}] Applying javascript variable inline evaluation on \"{}\"", TemplateEngine.threadIndex(), match);
                }
                
                
                try {
                    
                    final Object result = 
                        StandardExpressionProcessor.processExpression(arguments, match);
                    
                    strBuilder.append(formatEvaluationResult(result));
                    
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
    

    
    
    protected abstract String formatEvaluationResult(final Object result);
    
    

}

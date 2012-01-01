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
package org.thymeleaf.exceptions;

import org.thymeleaf.TemplateEngine;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 1.0
 *
 */
public class TemplateProcessingException extends TemplateEngineException {

    private static final long serialVersionUID = 5985749439214775193L;

    private final String templateName;
    private final Integer lineNumber;
    private final String expression;
    
    
    
    public TemplateProcessingException(final String message) {
        this(message, TemplateEngine.threadTemplateName(), (String)null, (Integer)null);
    }
    
    public TemplateProcessingException(final String message, final Throwable cause) {
        this(message, TemplateEngine.threadTemplateName(), null, null, cause);
    }
    
    public TemplateProcessingException(final String message, final String templateName) {
        this(message, templateName, (String)null, (Integer)null);
    }
    
    public TemplateProcessingException(final String message, final String templateName, final Throwable cause) {
        this(message, templateName, null, null, cause);
    }

    public TemplateProcessingException(final String message, final String templateName, final String expression) {
        this(message, templateName, expression, (Integer)null);
    }
    
    public TemplateProcessingException(final String message, final String templateName, final String expression, final Throwable cause) {
        this(message, templateName, expression, null, cause);
    }

    public TemplateProcessingException(final String message, final String templateName, final Integer lineNumber) {
        this(message, templateName, null, lineNumber);
    }
    
    public TemplateProcessingException(final String message, final String templateName, final Integer lineNumber, final Throwable cause) {
        this(message, templateName, null, lineNumber, cause);
    }

    public TemplateProcessingException(final String message, final String templateName, final String expression, final Integer lineNumber) {
        this(message, templateName, expression, lineNumber, null);
    }
    
    public TemplateProcessingException(
            final String message, final String templateName, final String expression, final Integer lineNumber, final Throwable cause) {
        super(message, cause);
        this.templateName = templateName;
        this.expression = expression;
        this.lineNumber = lineNumber;
    }

    

    @Override
    public String getMessage() {
        
        final StringBuilder sb = new StringBuilder();
        
        sb.append(super.getMessage());
        
        if (this.templateName != null) {
            sb.append(" [");
            if(this.expression != null || this.lineNumber != null) {
                if (this.expression != null) {
                    sb.append("evaluating \"").append(this.expression).append("\" ");
                    if (this.lineNumber != null) {
                        sb.append(" ");
                    }
                }
                if (this.lineNumber != null) {
                    sb.append("at line ").append(this.lineNumber).append(" of template ");
                } else {
                    sb.append("at template ");
                }
            }
            sb.append("\"");
            sb.append(this.templateName);
            sb.append("\"]");
        }
        
        return sb.toString();
        
    }
    
    
    
    public String getTemplateName() {
        return this.templateName;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }
    
    public String getExpression() {
        return this.expression;
    }

    
    
    
    public TemplateProcessingException specifyExpression(final String newExpression) {
        return specifyExpressionAndLineNumber(newExpression, this.lineNumber);
    }
    
    
    public TemplateProcessingException specifyLineNumber(final Integer newLineNumber) {
        return specifyExpressionAndLineNumber(this.expression, newLineNumber);
    }
    
    
    public TemplateProcessingException specifyExpressionAndLineNumber(final String newExpression, final Integer newLineNumber) {
        return new TemplateProcessingException(
                super.getMessage(), this.templateName, newExpression, newLineNumber, getCause());
    }
    
    
}

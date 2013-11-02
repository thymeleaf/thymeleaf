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
package org.thymeleaf.exceptions;



/**
 * <p>
 *   General exception for errors raised during the process of a template.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * @author Guven Demir
 * 
 * @since 1.0
 *
 */
public class TemplateProcessingException extends TemplateEngineException {

    private static final long serialVersionUID = 5985749439214775193L;

    private String templateName;
    private Integer lineNumber;
    
    
    
    public TemplateProcessingException(final String message) {
        this(message, null, (Integer)null);
    }
    
    public TemplateProcessingException(final String message, final Throwable cause) {
        this(message, null, null, cause);
    }
    
    public TemplateProcessingException(final String message, final String templateName) {
        this(message, templateName, (Integer)null);
    }
    
    public TemplateProcessingException(final String message, final String templateName, final Throwable cause) {
        this(message, templateName, null, cause);
    }
    
    public TemplateProcessingException(
            final String message, final String templateName, final Integer lineNumber) {
        super(message);
        this.templateName = templateName;
        this.lineNumber = lineNumber;
    }
    
    public TemplateProcessingException(
            final String message, final String templateName, final Integer lineNumber, final Throwable cause) {
        super(message, cause);
        this.templateName = templateName;
        this.lineNumber = lineNumber;
    }

    
    
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public boolean hasTemplateName() {
        return this.templateName != null;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }
    
    public boolean hasLineNumber() {
        return this.lineNumber != null;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setLineNumber(final Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    
    

    @Override
    public String getMessage() {
        
        final StringBuilder sb = new StringBuilder();

        sb.append(super.getMessage());
        
        if (this.templateName != null) {
            sb.append(' ');
            sb.append('(');
            sb.append(this.templateName);
            if (this.lineNumber != null) {
                sb.append(':').append(this.lineNumber);
            }
            sb.append(')');
        }
        
        return sb.toString();
        
    }
    
    
}

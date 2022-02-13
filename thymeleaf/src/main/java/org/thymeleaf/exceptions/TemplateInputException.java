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
package org.thymeleaf.exceptions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class TemplateInputException extends TemplateProcessingException {

    private static final long serialVersionUID = 1818006121265449639L;

    
    
    public TemplateInputException(final String message) {
        super(message);
    }
    
    public TemplateInputException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TemplateInputException(final String message, final String templateName, final Throwable cause) {
        super(message, templateName, cause);
    }

    /**
     *
     * @since 3.0.0
     *
     * @param message The message of the exception
     * @param templateName The name of the template for which the exception is thrown
     * @param line line position of the event that caused the exception
     * @param col columns position of the event that caused the exception
     */
    public TemplateInputException(final String message, final String templateName, final int line, final int col) {
        super(message, templateName, line, col);
    }

    /**
     *
     * @since 3.0.0
     *
     * @param message The message of the exception
     * @param templateName The name of the template for which the exception is thrown
     * @param line line position of the event that caused the exception
     * @param col columns position of the event that caused the exception
     * @param cause cause to be nested inside the exception
     */
    public TemplateInputException(final String message, final String templateName, final int line, final int col, final Throwable cause) {
        super(message, templateName, line, col, cause);
    }

    
}

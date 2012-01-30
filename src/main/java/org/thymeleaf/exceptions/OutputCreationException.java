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

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public class OutputCreationException extends TemplateProcessingException {
    
    private static final long serialVersionUID = -247484715700490790L;

    
    public OutputCreationException(final String message) {
        super(message);
    }
    
    public OutputCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public OutputCreationException(final String message, final String templateName, final Throwable cause) {
        super(message, templateName, cause);
    }

    public OutputCreationException(final String message, final String templateName) {
        super(message, templateName);
    }
    
    
}

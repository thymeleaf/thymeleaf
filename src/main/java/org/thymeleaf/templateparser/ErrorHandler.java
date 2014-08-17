/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class ErrorHandler implements org.xml.sax.ErrorHandler {
    
    public static final ErrorHandler INSTANCE = new ErrorHandler();
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    
    
    public ErrorHandler() {
        super();
    }
    
    
    public void warning(final SAXParseException exception) throws SAXException {
        logger.warn("[THYMELEAF][{}] Warning during parsing", TemplateEngine.threadIndex(), exception);
        throw exception;
    }

    public void error(final SAXParseException exception) throws SAXException {
        logger.error("[THYMELEAF][{}] Error during parsing", TemplateEngine.threadIndex(), exception);
        throw exception;
    }

    public void fatalError(final SAXParseException exception) throws SAXException {
        logger.error("[THYMELEAF][{}] Fatal error during parsing", TemplateEngine.threadIndex(), exception);
        throw exception;
    }

}

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
package org.thymeleaf.templatemode;

import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templatewriter.ITemplateWriter;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class TemplateModeHandler implements ITemplateModeHandler {
    
    private final String templateModeName;
    private final ITemplateParser templateParser;
    private final ITemplateWriter templateWriter;
    
    
    
    public TemplateModeHandler(
            final String templateModeName, final ITemplateParser templateParser, final ITemplateWriter templateWriter) {
        super();
        this.templateModeName = templateModeName;
        this.templateParser = templateParser;
        this.templateWriter = templateWriter;
    }



    public String getTemplateModeName() {
        return this.templateModeName;
    }

    public ITemplateParser getTemplateParser() {
        return this.templateParser;
    }

    public ITemplateWriter getTemplateWriter() {
        return this.templateWriter;
    }
    
    
}

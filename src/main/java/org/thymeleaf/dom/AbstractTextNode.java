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
package org.thymeleaf.dom;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.DOMUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractTextNode extends Node {

    private static final long serialVersionUID = -4406245492696671750L;

    protected char[] content;
    protected boolean escapeXml;

    
    protected AbstractTextNode(final String content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        this((content == null? null : content.toCharArray()), escapeXml, documentName, lineNumber);
    }

    protected AbstractTextNode(final char[] content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Content cannot be null");
        this.escapeXml = escapeXml;
        try {
            if (escapeXml) {
                this.content = DOMUtils.escapeXml(content, true);
            } else {
                this.content = content;
            }
        } catch (Exception e) {
            throw new TemplateProcessingException(
                    "Error creating text node for content \"" + new String(content) + "\"", e);
        }
    }
    
    
    public String getContent() {
        return new String(this.content);
    }
    
    public char[] unsafeGetContentCharArray() {
        return this.content;
    }

    
    public void setContent(final String content) {
        this.content = content.toCharArray();
    }
    
    public void setContent(final char[] content) {
        this.content = content;
    }
    

    
    @Override
    final void doAdditionalSkippableComputing(final boolean skippable) {
        // Nothing to be done here!
    }

    
    
    @Override
    final void doAdditionalPrecomputeNode(final Configuration configuration) {
        // Nothing to be done here!
    }

    
    
    @Override
    final void doAdditionalProcess(final Arguments arguments, final boolean processOnlyElementNodes) {
        // Nothing to be done here
    }
    

}

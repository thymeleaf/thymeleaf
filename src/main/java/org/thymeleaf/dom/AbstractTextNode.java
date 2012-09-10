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
 * <p>
 *   Base abstract class for text-based node types in Thymeleaf DOM trees:
 *   {@link Text}s and {@link CDATASection}s.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractTextNode extends Node {

    private static final long serialVersionUID = -4406245492696671750L;

    char[] content;

    
    AbstractTextNode(final String content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        this((content == null? null : content.toCharArray()), escapeXml, documentName, lineNumber);
    }

    
    AbstractTextNode(final char[] content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Content cannot be null");
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
    

    /**
     * <p>
     *   Returns the textual content of this node, as a String.
     * </p>
     * 
     * @return the textual content of this node.
     */
    public String getContent() {
        return new String(this.content);
    }

    
    /**
     * <p>
     *   Returns the unsafe inner char[] with the textual content of this
     *   code.
     * </p>
     * <p>
     *   Calling this method avoids the need to create a new <tt>String</tt>
     *   object (like {@link #getContent()} does, but requires to be extremely
     *   careful with the result, as any modification to the returned char array
     *   will actually modify the node's contents.
     * </p>
     * 
     * @return the textual content of this node.
     */
    public char[] unsafeGetContentCharArray() {
        return this.content;
    }
    

    /**
     * <p>
     *   Modify the textual content of this node.
     * </p>
     * 
     * @param content the new content
     */
    public void setContent(final String content) {
        this.content = content.toCharArray();
    }
    
    
    /**
     * <p>
     *   Modify the textual content of this node. This method
     *   is considered <i>unsafe</i> because it does not copy the
     *   specified array (instead, it is merely assigned to an internal variable).
     * </p>
     * 
     * @param newContent the new content
     */
    public void unsafeSetContent(final char[] newContent) {
        this.content = newContent;
    }
    

    
    @Override
    final void doAdditionalSkippableComputing(final boolean skippable) {
        // Nothing to be done here!
    }

    
    
    @Override
    final void doAdditionalProcessableComputing(final boolean processable) {
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

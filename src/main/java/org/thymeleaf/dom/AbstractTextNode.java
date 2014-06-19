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
package org.thymeleaf.dom;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;


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

    boolean contentIsEscaped;
    String content;


    /**
     * @deprecated Deprecated in 2.1.3. Will be removed in 3.0. Text and CDATA nodes do not perform XML-escaping anymore.
     */
    @Deprecated
    AbstractTextNode(final String content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Content cannot be null");
        if (escapeXml) {
            this.content = HtmlEscape.escapeHtml4Xml(content);
            this.contentIsEscaped = true;
        } else {
            this.content = content;
            this.contentIsEscaped = false;
        }
    }


    /**
     * @since 2.1.3
     */
    AbstractTextNode(final String content, final String documentName, final Integer lineNumber, final boolean contentIsEscaped) {
        super(documentName, lineNumber);
        Validate.notNull(content, "Content cannot be null");
        this.content = content;
        this.contentIsEscaped = contentIsEscaped;
    }


    /**
     * <p>
     *   Returns the textual content of this node, as a String.
     * </p>
     * 
     * @return the textual content of this node.
     */
    public String getContent() {
        if (this.contentIsEscaped) {
            return HtmlEscape.unescapeHtml(this.content);
        }
        return this.content;
    }


    /**
     * <p>
     *   Returns the textual and escaped content of this node, as a String.
     * </p>
     *
     * @return the textual content of this node.
     * @since 2.1.3
     */
    public String getEscapedContent() {
        if (this.contentIsEscaped) {
            return this.content;
        }
        return HtmlEscape.escapeHtml4Xml(this.content);
    }


    /**
     * <p>
     *   Returns the original content of this node (escaped or not), as a String.
     * </p>
     *
     * @return the textual content of this node.
     * @since 2.1.3
     */
    public String getOriginalContent() {
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
        this.content = content;
        this.contentIsEscaped = false;
    }


    /**
     * <p>
     *   Modify the textual content of this node.
     * </p>
     *
     * @param content the new content
     * @param contentIsEscaped whether the specified content is already escaped or not
     * @since 2.1.3
     */
    public void setContent(final String content, final boolean contentIsEscaped) {
        this.content = content;
        this.contentIsEscaped = contentIsEscaped;
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
    final void doAdditionalProcess(final Arguments arguments) {
        // Nothing to be done here
    }
    

}

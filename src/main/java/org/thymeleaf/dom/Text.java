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
package org.thymeleaf.dom;




/**
 * <p>
 *   A Text node in a Thymeleaf DOM tree.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Text extends AbstractTextNode {


    private static final long serialVersionUID = 8715604048893435570L;



    public Text(final String content) {
        this(content, null, null, false);
    }


    /**
     *
     * @param content content
     * @param documentName documentName
     * @since 2.1.3
     */
    public Text(final String content, final String documentName) {
        this(content, documentName, null, false);
    }


    /**
     *
     * @param content content
     * @param documentName documentName
     * @param lineNumber lineNumber
     * @since 2.1.3
     */
    public Text(final String content, final String documentName, final Integer lineNumber) {
        this(content, documentName, lineNumber, false);
    }


    /**
     *
     * @param content content
     * @param documentName documentName
     * @param lineNumber lineNumber
     * @param contentIsEscaped contentIsEscaped
     * @since 2.1.3
     */
    public Text(final String content, final String documentName, final Integer lineNumber, final boolean contentIsEscaped) {
        super(content, documentName, lineNumber, contentIsEscaped);
    }




    /**
     *
     * @param content content
     * @param escapeXml escapeXml
     * @deprecated Deprecated in 2.1.3. Will be removed in 3.0. Text and CDATA nodes do not perform XML-escaping anymore.
     */
    @Deprecated
    public Text(final String content, final boolean escapeXml) {
        this(content, escapeXml, null, null);
    }


    /**
     *
     * @param content content
     * @param escapeXml escapeXml
     * @param documentName documentName
     * @deprecated Deprecated in 2.1.3. Will be removed in 3.0. Text and CDATA nodes do not perform XML-escaping anymore.
     */
    @Deprecated
    public Text(final String content, final boolean escapeXml, final String documentName) {
        this(content, escapeXml, documentName, null);
    }


    /**
     *
     * @param content content
     * @param escapeXml escapeXml
     * @param documentName documentName
     * @param lineNumber lineNumber
     * @deprecated Deprecated in 2.1.3. Will be removed in 3.0. Text and CDATA nodes do not perform XML-escaping anymore.
     */
    @Deprecated
    public Text(final String content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        super(content, escapeXml, documentName, lineNumber);
    }

    

    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new Text(this.content, this.getDocumentName(), this.getLineNumber(), this.contentIsEscaped);
    }
    

    
    
    @Override
    void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }



    @Override
    public void visit(final DOMVisitor visitor) {
        visitor.visit(this);
    }

    
    
}

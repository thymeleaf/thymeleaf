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




/**
 * <p>
 *   A CDATA Section node in a Thymeleaf DOM tree.
 * </p>
 * <p>
 *   When building an instance of this class, setting the <tt>escapeXml</tt> flag to true
 *   will apply an escaping operation to the specified contents by means of calling
 *   {@link org.thymeleaf.util.DOMUtils#escapeXml(char[], boolean)}.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class CDATASection extends AbstractTextNode {
    
    
    private static final long serialVersionUID = -3389597836166184694L;


    public CDATASection(final String content) {
        this(content, true, null, null);
    }
    
    
    public CDATASection(final char[] content) {
        this(content, true, null, null);
    }
    
    
    public CDATASection(final String content, final boolean escapeXml) {
        this(content, escapeXml, null, null);
    }
    
    
    public CDATASection(final char[] content, final boolean escapeXml) {
        this(content, escapeXml, null, null);
    }
    
    
    public CDATASection(final String content, final boolean escapeXml, final String documentName) {
        this(content, escapeXml, documentName, null);
    }
    
    
    public CDATASection(final char[] content, final boolean escapeXml, final String documentName) {
        this(content, escapeXml, documentName, null);
    }
    
    
    public CDATASection(final String content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        super(content, escapeXml, documentName, lineNumber);
    }
    
    public CDATASection(final char[] content, final boolean escapeXml, final String documentName, final Integer lineNumber) {
        super(content, escapeXml, documentName, lineNumber);
    }
    


    @Override
    public void visit(final DOMVisitor visitor) {
        visitor.visit(this);
    }

    
    
    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        // escapeXML is false because there's no reason to escape content again (if it already has been)
        return new CDATASection(this.content, false);
    }
    

    
    
    @Override
    void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }
    
    
}

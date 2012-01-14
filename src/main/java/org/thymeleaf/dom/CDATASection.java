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
package org.thymeleaf.dom;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class CDATASection extends AbstractTextNode {
    
    
    
    
    public CDATASection(final String content) {
        this(content, null, null);
    }
    
    
    public CDATASection(final char[] content) {
        this(content, null, null);
    }
    
    
    public CDATASection(final String content, final String documentName) {
        this(content, documentName, null);
    }
    
    
    public CDATASection(final char[] content, final String documentName) {
        this(content, documentName, null);
    }
    
    
    public CDATASection(final String content, final String documentName, final Integer lineNumber) {
        super(content, documentName, lineNumber);
    }
    
    public CDATASection(final char[] content, final String documentName, final Integer lineNumber) {
        super(content, documentName, lineNumber);
    }
    


    @Override
    public final void visit(final DOMVisitor visitor) {
        visitor.visit(this);
    }

    
    
    @Override
    Node createClonedInstance(final NestableNode newParent, final boolean cloneProcessors) {
        return new CDATASection(this.content);
    }
    

    
    
    @Override
    void doCloneNodeInternals(final Node node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }



    
    
    public static final CDATASection translateDOMCDATASection(final org.w3c.dom.CDATASection domNode, final NestableNode parentNode) {
        final CDATASection cdata = new CDATASection(domNode.getData());
        cdata.parent = parentNode;
        return cdata;
    }
    
    
}

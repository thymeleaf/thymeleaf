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
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class Document extends NestableNode {


    private static final long serialVersionUID = 8647371304942210133L;

    
    private DocType docType;
    
    
    
    public Document() {
        this(null, null);
    }
    
    
    public Document(final DocType docType) {
        this(null, docType);
    }
    
    public Document(final String documentName) {
        this(documentName, null);
    }
    
    
    public Document(final String documentName, final DocType docType) {
        super(documentName, null);
        this.docType = docType;
    }
    

    public DocType getDocType() {
        return this.docType;
    }

    public boolean hasDocType() {
        return this.docType != null;
    }

    public void setDocType(DocType docType) {
    	this.docType = docType;
    }


    @Override
    void doAdditionalPrecomputeNestableNode(final Configuration configuration) {
        if (this.docType != null) {
            this.docType.process(configuration);
        }
    }

    
    public void precompute(final Configuration configuration) {
        Validate.notNull(configuration, "Configuration cannot be null");
        precomputeNode(configuration);
    }
    
    public void process(final Arguments arguments) {
        Validate.notNull(arguments, "Arguments cannot be null");
        processNode(arguments, arguments.getProcessTextNodes(), arguments.getProcessCommentNodes());
    }
    
    
    
    
    public Document clone(final boolean cloneProcessors) {
        return (Document) cloneNode(null, cloneProcessors);
    }


    
    @Override
    Node createClonedInstance(final NestableNode newParent, boolean cloneProcessors) {
        return new Document(this.docType);
    }
    

    
    @Override
    void doCloneNestableNodeInternals(final NestableNode node, final NestableNode newParent, final boolean cloneProcessors) {
        // Nothing to be done here
    }
    
    

}

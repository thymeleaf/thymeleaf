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

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.Standards;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.2
 *
 */
public final class Document {

    private DocType docType;
    private DocType translatedDocType;
    
    private final Root root;
    
    
    
    public Document(final Root root) {
        this(null, root);
    }
    
    public Document(final DocType docType, final Root root) {
        super();
        Validate.notNull(root, "Root elements cannot be null");
        this.docType = docType;
        this.translatedDocType = null;
        this.root = root;
    }
    
    
    private boolean hasTranslatedDocType() {
        return this.translatedDocType != null;
    }
    
    
    public boolean hasDocType() {
        return this.docType != null;
    }
    
    
    public void setDocType(final DocType docType) {
        this.docType = docType;
        this.translatedDocType = null;
    }
    
    
    public Root getRoot() {
        return this.root;
    }
    


    public final void precompute(final Configuration configuration) {
        Validate.notNull(configuration, "Configuration cannot be null");
        this.docType.process(configuration);
        this.root.precompute(configuration);
    }

    
    
    
    public final void process(final Arguments arguments) {
        
        Validate.notNull(arguments, "Arguments cannot be null");
        
        this.precompute(arguments.getConfiguration());
        this.root.process(arguments);
    }
    
    
    
    
    
    public Document clone(final boolean cloneProcessors) {
        final Root newRoot = (Root) this.root.cloneNode(null, cloneProcessors);
        return new Document(this.docType, newRoot);
    }
    
    
    
    public void write(final Arguments arguments, final Writer writer) throws IOException {
        if (arguments.getTemplateResolution().getTemplateMode().isXML()) {
            writer.write(Standards.XML_DECLARATION);
            writer.write('\n');
        }
        if (hasTranslatedDocType()) {
            this.translatedDocType.write(writer);
            writer.write('\n');
        } else if (hasDocType()) {
            this.docType.write(writer);
            writer.write('\n');
        }
        this.root.write(arguments, writer);
    }
    
    
    
    
    public static final Document translateDOMDocument(final org.w3c.dom.Document domDocument) {

        final org.w3c.dom.DocumentType domDocumentType = domDocument.getDoctype();
        
        final Root root = new Root();
        
        final org.w3c.dom.NodeList children = domDocument.getChildNodes();
        final int childrenLen = children.getLength();
        for (int i = 0; i < childrenLen; i++) {
            final org.w3c.dom.Node child = children.item(i);
            if (!(child instanceof org.w3c.dom.DocumentType)) {
                root.addChild(Node.translateDOMNode(child, root));
            }
        }
        
        return new Document(DocType.translateDOMDocumentType(domDocumentType), root);

    }
    
    
}

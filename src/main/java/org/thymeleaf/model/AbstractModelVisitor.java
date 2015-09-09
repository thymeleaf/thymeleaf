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
package org.thymeleaf.model;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AbstractModelVisitor implements IModelVisitor {


    public void visitTemplateStart(final ITemplateStart templateStart) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitTemplateEnd(final ITemplateEnd templateEnd) {
        // Nothing to be done here - just an empty default implementation
    }


    public void visitXMLDeclaration(final IXMLDeclaration xmlDeclaration) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitDocType(final IDocType docType) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitCDATASection(final ICDATASection cdataSection) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitComment(final IComment comment) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitText(final IText text) {
        // Nothing to be done here - just an empty default implementation
    }


    public void visitStandaloneElement(final IStandaloneElementTag standaloneElementTag) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitOpenElement(final IOpenElementTag openElementTag) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visitCloseElement(final ICloseElementTag closeElementTag) {
        // Nothing to be done here - just an empty default implementation
    }


    public void visitProcessingInstruction(final IProcessingInstruction processingInstruction) {
        // Nothing to be done here - just an empty default implementation
    }


}

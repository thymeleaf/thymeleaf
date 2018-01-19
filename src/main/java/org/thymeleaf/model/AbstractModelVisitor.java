/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
 * <p>
 *   Abstract base implementation for the {@link IModelVisitor} interface.
 * </p>
 * <p>
 *   This class provides empty implementations for all methods in the interface, so
 *   that users can define only the ones they are interested on.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AbstractModelVisitor implements IModelVisitor {


    public void visit(final ITemplateStart templateStart) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final ITemplateEnd templateEnd) {
        // Nothing to be done here - just an empty default implementation
    }


    public void visit(final IXMLDeclaration xmlDeclaration) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final IDocType docType) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final ICDATASection cdataSection) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final IComment comment) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final IText text) {
        // Nothing to be done here - just an empty default implementation
    }


    public void visit(final IStandaloneElementTag standaloneElementTag) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final IOpenElementTag openElementTag) {
        // Nothing to be done here - just an empty default implementation
    }

    public void visit(final ICloseElementTag closeElementTag) {
        // Nothing to be done here - just an empty default implementation
    }


    public void visit(final IProcessingInstruction processingInstruction) {
        // Nothing to be done here - just an empty default implementation
    }


}

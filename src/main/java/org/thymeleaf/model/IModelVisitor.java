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
 *   Interface to be implemented by all classes modeling actions to be performed on
 *   an event or sequence of events according to the <em>Visitor</em> pattern.
 * </p>
 * <p>
 *   These objects are usually applied by means of the {@link IModel#accept(IModelVisitor)}
 *   method.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @see IModel
 * @see AbstractModelVisitor
 * @since 3.0.0
 *
 */
public interface IModelVisitor {


    void visit(final ITemplateStart templateStart);

    void visit(final ITemplateEnd templateEnd);


    void visit(final IXMLDeclaration xmlDeclaration);

    void visit(final IDocType docType);

    void visit(final ICDATASection cdataSection);

    void visit(final IComment comment);

    void visit(final IText text);


    void visit(final IStandaloneElementTag standaloneElementTag);

    void visit(final IOpenElementTag openElementTag);

    void visit(final ICloseElementTag closeElementTag);


    void visit(final IProcessingInstruction processingInstruction);

}

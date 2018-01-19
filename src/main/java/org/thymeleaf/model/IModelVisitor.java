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


    public void visit(final ITemplateStart templateStart);

    public void visit(final ITemplateEnd templateEnd);


    public void visit(final IXMLDeclaration xmlDeclaration);

    public void visit(final IDocType docType);

    public void visit(final ICDATASection cdataSection);

    public void visit(final IComment comment);

    public void visit(final IText text);


    public void visit(final IStandaloneElementTag standaloneElementTag);

    public void visit(final IOpenElementTag openElementTag);

    public void visit(final ICloseElementTag closeElementTag);


    public void visit(final IProcessingInstruction processingInstruction);

}

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
public interface IModelVisitor {


    public void visitTemplateStart(final ITemplateStart templateStart);

    public void visitTemplateEnd(final ITemplateEnd templateEnd);


    public void visitXMLDeclaration(final IXMLDeclaration xmlDeclaration);

    public void visitDocType(final IDocType docType);

    public void visitCDATASection(final ICDATASection cdataSection);

    public void visitComment(final IComment comment);

    public void visitText(final IText text);


    public void visitStandaloneElement(final IStandaloneElementTag standaloneElementTag);

    public void visitOpenElement(final IOpenElementTag openElementTag);

    public void visitCloseElement(final ICloseElementTag closeElementTag);


    public void visitProcessingInstruction(final IProcessingInstruction processingInstruction);

}

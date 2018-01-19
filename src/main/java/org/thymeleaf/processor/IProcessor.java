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
package org.thymeleaf.processor;

import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Basic interface to be implemented by all processors provided by processor dialects (implementations of
 *   {@link IProcessorDialect}).
 * </p>
 * <p>
 *   Note this is a base interface without much meaning of its own. Instead, processors
 *   should implement one or several of the following sub-interfaces:
 * </p>
 * <ul>
 *   <li>{@link org.thymeleaf.processor.element.IElementTagProcessor} execute on open/standalone
 *       tag events only (no processors can be applied to close tags), and have no (direct) access to the element
 *       body.</li>
 *   <li>{@link org.thymeleaf.processor.element.IElementModelProcessor} execute on complete
 *       elements, including their bodies, in the form of {@link org.thymeleaf.model.IModel} objects.</li>
 *   <li>{@link org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor}
 *       execute on template start ({@link org.thymeleaf.model.ITemplateStart}) and/or template end
 *       ({@link org.thymeleaf.model.ITemplateEnd}) events.</li>
 *   <li>{@link org.thymeleaf.processor.text.ITextProcessor} execute on {@link org.thymeleaf.model.IText} events.</li>
 *   <li>{@link org.thymeleaf.processor.comment.ICommentProcessor} execute on {@link org.thymeleaf.model.IComment}
 *       events.</li>
 *   <li>{@link org.thymeleaf.processor.cdatasection.ICDATASectionProcessor} execute on
 *       {@link org.thymeleaf.model.ICDATASection} events.</li>
 *   <li>{@link org.thymeleaf.processor.doctype.IDocTypeProcessor} execute on {@link org.thymeleaf.model.IDocType}
 *       events.</li>
 *   <li>{@link org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor} execute on
 *       {@link org.thymeleaf.model.IXMLDeclaration} events.</li>
 *   <li>{@link org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor} execute on
 *       {@link org.thymeleaf.model.IProcessingInstruction} events.</li>
 * </ul>
 * <p>
 *   Note a class with this name existed since 2.0.0, but it was completely reimplemented
 *   in Thymeleaf 3.0
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see org.thymeleaf.processor.element.IElementProcessor
 * @see org.thymeleaf.processor.element.IElementTagProcessor
 * @see org.thymeleaf.processor.element.IElementModelProcessor
 * @see org.thymeleaf.processor.cdatasection.ICDATASectionProcessor
 * @see org.thymeleaf.processor.comment.ICommentProcessor
 * @see org.thymeleaf.processor.doctype.IDocTypeProcessor
 * @see org.thymeleaf.processor.processinginstruction.IProcessingInstructionProcessor
 * @see org.thymeleaf.processor.templateboundaries.ITemplateBoundariesProcessor
 * @see org.thymeleaf.processor.text.ITextProcessor
 * @see org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor
 *
 * @since 3.0.0
 * 
 */
public interface IProcessor {

    public TemplateMode getTemplateMode();
    public int getPrecedence();

}

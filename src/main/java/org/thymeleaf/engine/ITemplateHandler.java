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
package org.thymeleaf.engine;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface ITemplateHandler {


    public void setNext(final ITemplateHandler next);

    public void setContext(final ITemplateContext context);


    public void handleTemplateStart(final ITemplateStart templateStart);

    public void handleTemplateEnd(final ITemplateEnd templateEnd);


    public void handleXMLDeclaration(final IXMLDeclaration xmlDeclaration);

    public void handleDocType(final IDocType docType);

    public void handleCDATASection(final ICDATASection cdataSection);

    public void handleComment(final IComment comment);

    public void handleText(final IText text);


    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag);

    public void handleOpenElement(final IOpenElementTag openElementTag);

    public void handleCloseElement(final ICloseElementTag closeElementTag);



    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction);


}

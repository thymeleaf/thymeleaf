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
package org.thymeleaf.aurora.engine;

import org.thymeleaf.standard.expression.IStandardExpression;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface ITemplateHandler {


    public void setNext(final ITemplateHandler next);


    public void handleDocumentStart(final long startTimeNanos, final int line, final int col);

    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col);


    public void handleXmlDeclaration(final IXMLDeclaration xmlDeclaration);

    public void handleDocType(final IDocType docType);

    public void handleCDATASection(final ICDATASection cdataSection);

    public void handleComment(final IComment comment);

    public void handleText(final IText text);


    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag);

    public void handleOpenElement(final IOpenElementTag openElementTag);

    public void handleAutoOpenElement(final IOpenElementTag openElementTag);

    public void handleCloseElement(final ICloseElementTag closeElementTag);

    public void handleAutoCloseElement(final ICloseElementTag closeElementTag);

    public void handleUnmatchedCloseElement(final ICloseElementTag closeElementTag);



    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction);



}

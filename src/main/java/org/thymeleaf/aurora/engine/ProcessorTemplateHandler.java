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

import org.thymeleaf.aurora.context.ITemplateProcessingContext;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class ProcessorTemplateHandler extends AbstractTemplateHandler {


    private final ITemplateProcessingContext processingContext;



    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     * @param processingContext the template processing context
     */
    public ProcessorTemplateHandler(final ITemplateProcessingContext processingContext) {
        super();
        this.processingContext = processingContext;
    }





    @Override
    public void handleText(final Text text) {

        super.handleText(text);

    }



    @Override
    public void handleComment(final Comment comment) {
        
        super.handleComment(comment);

    }

    
    @Override
    public void handleCDATASection(final CDATASection cdataSection) {
        
        super.handleCDATASection(cdataSection);

    }




    @Override
    public void handleStandaloneElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final boolean minimized,
            final int line, final int col) {

        super.handleStandaloneElement(elementDefinition, elementName, elementAttributes, minimized, line, col);

    }


    @Override
    public void handleOpenElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line, final int col) {

        super.handleOpenElement(elementDefinition, elementName, elementAttributes, line, col);

    }


    @Override
    public void handleAutoOpenElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line, final int col) {

        super.handleAutoOpenElement(elementDefinition, elementName, elementAttributes, line, col);

    }


    @Override
    public void handleCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        super.handleCloseElement(elementDefinition, elementName, line, col);

    }


    @Override
    public void handleAutoCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        super.handleAutoCloseElement(elementDefinition, elementName, line, col);

    }


    @Override
    public void handleUnmatchedCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        super.handleUnmatchedCloseElement(elementDefinition, elementName, line, col);

    }




    @Override
    public void handleDocType(final DocType docType) {
        
        super.handleDocType(docType);

    }

    
    
    
    @Override
    public void handleXmlDeclaration(final XMLDeclaration xmlDeclaration) {

        super.handleXmlDeclaration(xmlDeclaration);

    }






    @Override
    public void handleProcessingInstruction(final ProcessingInstruction processingInstruction) {
        
        super.handleProcessingInstruction(processingInstruction);

    }


    
}
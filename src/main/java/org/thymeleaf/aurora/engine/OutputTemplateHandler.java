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

import java.io.Writer;

import org.thymeleaf.exceptions.TemplateOutputException;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class OutputTemplateHandler extends AbstractTemplateHandler {


    private final String templateName;
    private final Writer writer;



    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     * @param templateName the name of the template being processed
     * @param writer the writer to which output will be written.
     */
    public OutputTemplateHandler(final String templateName, final Writer writer) {
        super();
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.writer = writer;
        this.templateName = templateName;
    }





    @Override
    public void handleText(final IText text) {
        
        try {
            text.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, text.getLine(), text.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleText(text);

    }



    @Override
    public void handleComment(final IComment comment) {
        
        try {
            comment.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, comment.getLine(), comment.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleComment(comment);

    }

    
    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        
        try {
            cdataSection.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, cdataSection.getLine(), cdataSection.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleCDATASection(cdataSection);

    }




    @Override
    public void handleStandaloneElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final boolean minimized,
            final int line, final int col) {

        try {
            MarkupOutput.writeStandaloneElement(this.writer, elementName, elementAttributes, minimized);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

        // Just in case someone set us a 'next'
        super.handleStandaloneElement(elementDefinition, elementName, elementAttributes, minimized, line, col);

    }


    @Override
    public void handleOpenElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line, final int col) {

        try {
            MarkupOutput.writeOpenElement(this.writer, elementName, elementAttributes);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

        // Just in case someone set us a 'next'
        super.handleOpenElement(elementDefinition, elementName, elementAttributes, line, col);

    }


    @Override
    public void handleAutoOpenElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final ElementAttributes elementAttributes,
            final int line, final int col) {

        try {
            MarkupOutput.writeAutoOpenElement(this.writer, elementName, elementAttributes);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

        // Just in case someone set us a 'next'
        super.handleAutoOpenElement(elementDefinition, elementName, elementAttributes, line, col);

    }


    @Override
    public void handleCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        try {
            MarkupOutput.writeCloseElement(this.writer, elementName);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

        // Just in case someone set us a 'next'
        super.handleCloseElement(elementDefinition, elementName, line, col);

    }


    @Override
    public void handleAutoCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        try {
            MarkupOutput.writeAutoCloseElement(this.writer, elementName);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

        // Just in case someone set us a 'next'
        super.handleAutoCloseElement(elementDefinition, elementName, line, col);

    }


    @Override
    public void handleUnmatchedCloseElement(
            final ElementDefinition elementDefinition,
            final String elementName,
            final int line, final int col) {

        // We will write exactly the same as for non-unmatched close elements, because that does not matter from the markup point
        try {
            MarkupOutput.writeUnmatchedCloseElement(this.writer, elementName);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

        // Just in case someone set us a 'next'
        super.handleUnmatchedCloseElement(elementDefinition, elementName, line, col);

    }




    @Override
    public void handleDocType(final IDocType docType) {
        
        try {
            docType.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, docType.getLine(), docType.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleDocType(docType);

    }

    
    
    
    @Override
    public void handleXmlDeclaration(final IXMLDeclaration xmlDeclaration) {

        try {
            xmlDeclaration.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, xmlDeclaration.getLine(), xmlDeclaration.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleXmlDeclaration(xmlDeclaration);

    }






    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        
        try {
            processingInstruction.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, processingInstruction.getLine(), processingInstruction.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleProcessingInstruction(processingInstruction);

    }


    
    
}
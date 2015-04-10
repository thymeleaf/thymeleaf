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

import org.thymeleaf.aurora.model.ICDATASection;
import org.thymeleaf.aurora.model.ICloseElementTag;
import org.thymeleaf.aurora.model.IComment;
import org.thymeleaf.aurora.model.IDocType;
import org.thymeleaf.aurora.model.IOpenElementTag;
import org.thymeleaf.aurora.model.IProcessingInstruction;
import org.thymeleaf.aurora.model.IStandaloneElementTag;
import org.thymeleaf.aurora.model.IText;
import org.thymeleaf.aurora.model.IXMLDeclaration;
import org.thymeleaf.exceptions.TemplateOutputException;


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public final class OutputTemplateHandler extends AbstractTemplateHandler {


    private final Writer writer;



    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
     * @param writer the writer to which output will be written.
     */
    public OutputTemplateHandler(final Writer writer) {
        super();
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null");
        }
        this.writer = writer;
    }





    @Override
    public void handleText(final IText text) {
        
        try {
            text.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    text.getLine(), text.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleText(text);

    }



    @Override
    public void handleComment(final IComment comment) {
        
        try {
            comment.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    comment.getLine(), comment.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleComment(comment);

    }

    
    @Override
    public void handleCDATASection(final ICDATASection cdataSection) {
        
        try {
            cdataSection.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    cdataSection.getLine(), cdataSection.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleCDATASection(cdataSection);

    }




    @Override
    public void handleStandaloneElement(final IStandaloneElementTag standaloneElementTag) {

        try {
            standaloneElementTag.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    standaloneElementTag.getLine(), standaloneElementTag.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleStandaloneElement(standaloneElementTag);

    }


    @Override
    public void handleOpenElement(final IOpenElementTag openElementTag) {

        try {
            openElementTag.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    openElementTag.getLine(), openElementTag.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleOpenElement(openElementTag);

    }


    @Override
    public void handleAutoOpenElement(final IOpenElementTag openElementTag) {

        // Nothing to be written... balanced elements were not present at the original template!

        // Just in case someone set us a 'next'
        super.handleAutoOpenElement(openElementTag);

    }


    @Override
    public void handleCloseElement(final ICloseElementTag closeElementTag) {

        try {
            closeElementTag.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    closeElementTag.getLine(), closeElementTag.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleCloseElement(closeElementTag);

    }


    @Override
    public void handleAutoCloseElement(final ICloseElementTag closeElementTag) {

        // Nothing to be written... balanced elements were not present at the original template!

        // Just in case someone set us a 'next'
        super.handleAutoCloseElement(closeElementTag);

    }


    @Override
    public void handleUnmatchedCloseElement(final ICloseElementTag closeElementTag) {

        // We will write exactly the same as for non-unmatched close elements, because that does not matter from the markup point
        try {
            closeElementTag.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    closeElementTag.getLine(), closeElementTag.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleUnmatchedCloseElement(closeElementTag);

    }




    @Override
    public void handleDocType(final IDocType docType) {
        
        try {
            docType.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    docType.getLine(), docType.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleDocType(docType);

    }

    
    
    
    @Override
    public void handleXmlDeclaration(final IXMLDeclaration xmlDeclaration) {

        try {
            xmlDeclaration.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    xmlDeclaration.getLine(), xmlDeclaration.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleXmlDeclaration(xmlDeclaration);

    }






    @Override
    public void handleProcessingInstruction(final IProcessingInstruction processingInstruction) {
        
        try {
            processingInstruction.write(this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException(
                    "An error happened during template rendering", getTemplateProcessingContext().getTemplateName(),
                    processingInstruction.getLine(), processingInstruction.getCol(), e);
        }

        // Just in case someone set us a 'next'
        super.handleProcessingInstruction(processingInstruction);

    }


    
    
}
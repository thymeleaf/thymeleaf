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


    // TODO writing methods for elements and attributes here should directly delegate on these structures' 'writeTo' methods.

    /**
     * <p>
     *   Creates a new instance of this handler.
     * </p>
     *
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
    public void handleText(final char[] buffer, final int offset, final int len, final int line, final int col) {
        
        try {
            this.writer.write(buffer, offset, len);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }



    @Override
    public void handleComment(
            final char[] buffer, 
            final int contentOffset, final int contentLen, 
            final int outerOffset, final int outerLen, 
            final int line, final int col) {
        
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }

    
    @Override
    public void handleCDATASection(
            final char[] buffer, 
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleStandaloneElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final boolean minimized,
            final int line, final int col) {
        
        try {
            this.writer.write('<');
            this.writer.write(name);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleStandaloneElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final boolean minimized,
            final int line, final int col) {
        
        try {
            if (minimized) {
                this.writer.write('/');
            }
            this.writer.write('>');
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleOpenElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {

        try {
            this.writer.write('<');
            this.writer.write(name);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleOpenElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {

        try {
            this.writer.write('>');
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleAutoOpenElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }




    @Override
    public void handleAutoOpenElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }




    @Override
    public void handleCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        
        try {
            this.writer.write("</");
            this.writer.write(name);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        
        try {
            this.writer.write('>');
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleAutoCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }





    @Override
    public void handleAutoCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }




    @Override
    public void handleUnmatchedCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // They were present at the original template, so simply output them.
        handleCloseElementStart(elementDefinition, name, line, col);
    }




    @Override
    public void handleUnmatchedCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // They were present at the original template, so simply output them.
        handleCloseElementEnd(elementDefinition, name, line, col);
    }




    @Override
    public void handleAttribute(
            final AttributeDefinition attributeDefinition,
            final String name,
            final String operator,
            final String value,
            final AttributeValueQuotes quotes,
            final int line, final int col) {
        
        try {
            Attribute.writeAttribute(name, operator, value, quotes, this.writer);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleInnerWhiteSpace(
            final String whiteSpace,
            final int line, final int col) {
        
        try {
            this.writer.write(whiteSpace);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleDocType(
            final String docType,
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset,
            final int line, final int col) {
        
        try {
            this.writer.write(docType);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }

    
    
    
    @Override
    public void handleXmlDeclaration(
            final String xmlDeclaration,
            final String keyword,
            final String version,
            final String encoding,
            final String standalone,
            final int line, final int col) {

        try {
            this.writer.write(xmlDeclaration);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }






    @Override
    public void handleProcessingInstruction(
            final String processingInstruction,
            final String target,
            final String content,
            final int line, final int col) {
        
        try {
            this.writer.write(processingInstruction);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }


    
    
}
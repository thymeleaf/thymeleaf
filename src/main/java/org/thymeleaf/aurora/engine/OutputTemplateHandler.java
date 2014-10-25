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
            final char[] buffer, final int offset, final int len,
            final boolean minimized, final int line, final int col) {
        
        try {
            this.writer.write('<');
            this.writer.write(buffer, offset, len);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleStandaloneElementEnd(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
            final boolean minimized, final int line, final int col) {
        
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
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {

        try {
            this.writer.write('<');
            this.writer.write(buffer, offset, len);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleOpenElementEnd(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
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
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }




    @Override
    public void handleAutoOpenElementEnd(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }




    @Override
    public void handleCloseElementStart(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        
        try {
            this.writer.write("</");
            this.writer.write(buffer, offset, len);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleCloseElementEnd(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
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
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }





    @Override
    public void handleAutoCloseElementEnd(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        // Nothing to be done... balanced elements were not present at the original template!
    }




    @Override
    public void handleUnmatchedCloseElementStart(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        // They were present at the original template, so simply output them.
        handleCloseElementStart(elementDefinition, buffer, offset, len, line, col);
    }




    @Override
    public void handleUnmatchedCloseElementEnd(
            final ElementDefinition elementDefinition,
            final char[] buffer, final int offset, final int len,
            final int line, final int col) {
        // They were present at the original template, so simply output them.
        handleCloseElementEnd(elementDefinition, buffer, offset, len, line, col);
    }




    @Override
    public void handleAttribute(
            final AttributeDefinition attributeDefinition,
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol, final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol, final int valueContentOffset,
            final int valueContentLen, final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol) {
        
        try {
            this.writer.write(buffer, nameOffset, nameLen);
            this.writer.write(buffer, operatorOffset, operatorLen);
            this.writer.write(buffer, valueOuterOffset, valueOuterLen);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, nameLine, nameCol, e);
        }

    }




    @Override
    public void handleInnerWhiteSpace(
            final char[] buffer, 
            final int offset, final int len, 
            final int line, final int col) {
        
        try {
            this.writer.write(buffer, offset, len);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }




    @Override
    public void handleDocType(
            final char[] buffer, 
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol, 
            final int elementNameOffset, final int elementNameLen, 
            final int elementNameLine, final int elementNameCol,
            final int typeOffset, final int typeLen, 
            final int typeLine, final int typeCol,
            final int publicIdOffset, final int publicIdLen, 
            final int publicIdLine, final int publicIdCol, 
            final int systemIdOffset, final int systemIdLen,
            final int systemIdLine, final int systemIdCol, 
            final int internalSubsetOffset, final int internalSubsetLen,
            final int internalSubsetLine, final int internalSubsetCol,
            final int outerOffset, final int outerLen,
            final int outerLine, final int outerCol) {
        
        try {
            this.writer.write(buffer, outerOffset, outerLen);
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, outerLine, outerCol, e);
        }

    }

    
    
    
    @Override
    public void handleXmlDeclaration(
            final char[] buffer, 
            final int keywordOffset, final int keywordLen,
            final int keywordLine, final int keywordCol,
            final int versionOffset, final int versionLen,
            final int versionLine, final int versionCol,
            final int encodingOffset, final int encodingLen,
            final int encodingLine, final int encodingCol,
            final int standaloneOffset, final int standaloneLen,
            final int standaloneLine, final int standaloneCol,
            final int outerOffset, final int outerLen,
            final int line,final int col) {
        
        try {

            final int outerContentEnd = (outerOffset  + outerLen) - 2;
            
            this.writer.write('<');
            this.writer.write('?');
            this.writer.write(buffer, keywordOffset, keywordLen);

            /*
             * VERSION (required) 
             */
            int lastStructureEnd = keywordOffset + keywordLen;
            int thisStructureOffset = versionOffset;
            int thisStructureLen = versionLen;
            int thisStructureEnd = thisStructureOffset + thisStructureLen;
            
            this.writer.write(buffer, lastStructureEnd, thisStructureOffset - lastStructureEnd);
            this.writer.write(buffer, thisStructureOffset, thisStructureLen);

            /*
             * ENCODING (optional)
             */
            if (encodingLen > 0)  {
                
                lastStructureEnd = thisStructureEnd;
                thisStructureOffset = encodingOffset;
                thisStructureLen = encodingLen;
                thisStructureEnd = thisStructureOffset + thisStructureLen;
            
                this.writer.write(buffer, lastStructureEnd, thisStructureOffset - lastStructureEnd);
                this.writer.write(buffer, thisStructureOffset, thisStructureLen);

            }

            /*
             * STANDALONE (optional)
             */
            
            if (standaloneLen > 0) {
                
                lastStructureEnd = thisStructureEnd;
                thisStructureOffset = standaloneOffset;
                thisStructureLen = standaloneLen;
                thisStructureEnd = thisStructureOffset + thisStructureLen;
            
                this.writer.write(buffer, lastStructureEnd, thisStructureOffset - lastStructureEnd);
                this.writer.write(buffer, thisStructureOffset, thisStructureLen);
                
            }
            
            this.writer.write(buffer, thisStructureEnd, (outerContentEnd - thisStructureEnd));
            
            this.writer.write('?');
            this.writer.write('>');
            
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }






    @Override
    public void handleProcessingInstruction(
            final char[] buffer, 
            final int targetOffset, final int targetLen, 
            final int targetLine, final int targetCol,
            final int contentOffset, final int contentLen,
            final int contentLine, final int contentCol,
            final int outerOffset, final int outerLen, 
            final int line, final int col) {
        
        try {

            this.writer.write('<');
            this.writer.write('?');
            this.writer.write(buffer, targetOffset, targetLen);
            if (contentLen > 0)  {
                this.writer.write(buffer, (targetOffset + targetLen), contentOffset - (targetOffset + targetLen));
                this.writer.write(buffer, contentOffset, contentLen);
            } else {
                this.writer.write(buffer, (targetOffset + targetLen), ((outerOffset  + outerLen) - 2) - (targetOffset + targetLen));
            }
            this.writer.write('?');
            this.writer.write('>');
            
        } catch (final Exception e) {
            throw new TemplateOutputException("An error happened during template rendering", this.templateName, line, col, e);
        }

    }


    
    
}
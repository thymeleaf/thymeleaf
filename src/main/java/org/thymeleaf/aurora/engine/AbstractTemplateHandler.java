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


/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public abstract class AbstractTemplateHandler implements ITemplateHandler {




    protected AbstractTemplateHandler() {
        super();
    }



    public void handleDocumentStart(
            final long startTimeNanos, final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleXmlDeclaration(
            final String xmlDeclaration,
            final String keyword,
            final String version,
            final String encoding,
            final String standalone,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleDocType(
            final String docType,
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleCDATASection(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


    public void handleStandaloneElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final boolean minimized, final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleStandaloneElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final boolean minimized, final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }

    

    public void handleOpenElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleOpenElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleAutoOpenElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleAutoOpenElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


    
    public void handleAutoCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }

    public void handleAutoCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }
    

    
    public void handleUnmatchedCloseElementStart(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


    public void handleUnmatchedCloseElementEnd(
            final ElementDefinition elementDefinition,
            final String name,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


    
    public void handleAttribute(
            final AttributeDefinition attributeDefinition,
            final String name,
            final String operator,
            final String value,
            final AttributeValueQuotes quotes,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


    
    public void handleInnerWhiteSpace(
            final String whiteSpace,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }



    public void handleProcessingInstruction(
            final String processingInstruction,
            final String target,
            final String content,
            final int line, final int col) {
        // Nothing to be done here, meant to be overridden if required
    }


}
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
package org.thymeleaf.engine.markup.handler;

import org.thymeleaf.engine.markup.MarkupEngineConfiguration;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class StandardMarkupHandler extends AbstractMarkupHandler {


    private final MarkupEngineConfiguration config;



    public StandardMarkupHandler(final MarkupEngineConfiguration config) {
        super();
        this.config = config;
    }





    /*
     * ---------------
     * Document events
     * ---------------
     */


    @Override
    public void onDocumentStart(
            final long startTimeNanos, final String documentName) {



    }



    @Override
    public void onDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final String documentName) {



    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    @Override
    public void onXmlDeclaration(
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone,
            final String documentName, final int line, final int col) {



    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    @Override
    public void onDocTypeClause(
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId,
            final String documentName, final int line, final int col) {



    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    @Override
    public void onCDATASection(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {



    }





    /*
     * -----------
     * Text events
     * -----------
     */

    @Override
    public void onText(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {



    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    @Override
    public void onComment(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {



    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */

    @Override
    public void onAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol, final String documentName) {


}



    @Override
    public void onStandaloneElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized, final String documentName, final int line, final int col) {


    }



    @Override
    public void onStandaloneElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized, final String documentName, final int line, final int col) {


    }



    @Override
    public void onOpenElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onOpenElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onAutoCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onAutoCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onUnmatchedCloseElementStart(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onUnmatchedCloseElementEnd(
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {


    }



    @Override
    public void onElementInnerWhiteSpace(
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col) {



    }





    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    @Override
    public void onProcessingInstruction(
            final String processingInstruction,
            final String target, final String content,
            final String documentName, final int line, final int col) {


    }




}

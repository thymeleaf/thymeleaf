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
package org.thymeleaf.engine2;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class StandardMarkupEngine implements IMarkupEngine {




    public StandardMarkupEngine() {

    }





    /*
     * ---------------
     * Document events
     * ---------------
     */


    public void onDocumentStart(
            final long startTimeNanos) {

    }



    public void onDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos) {

    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    public void onXmlDeclaration (
            final String xmlDeclaration,
            final String xmlVersion, final String xmlEncoding, final boolean xmlStandalone,
            final int line, final int col) {

    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    public void onDocTypeClause (
            final String docTypeClause,
            final String docTypeRootElementName, final String docTypePublicId, final boolean docTypeSystemId,
            final int line, final int col) {

    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    public void onCDATASection (
            final String content,
            final int line, final int col) {


    }





    /*
     * -----------
     * Text events
     * -----------
     */

    public void onText (
            final String content,
            final int line, final int col) {


    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    public void onComment (
            final String content,
            final int line, final int col) {


    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */

    public void onAttribute (
            final String name, final String operator, final String value, final String quotedValue,
            final int line, final int col) {


    }


    public void onStandaloneElementStart (
            final String name, final String normalizedName,
            final int line, final int col) {


    }


    public void onStandaloneElementEnd (
            final String name, final String normalizedName,
            final boolean minimized,
            final int line, final int col) {


    }


    public void onOpenElementStart (
            final String name, final String normalizedName,
            final int line, final int col) {


    }


    public void onOpenElementEnd (
            final String name, final String normalizedName,
            final int line, final int col) {


    }


    public void onCloseElementStart (
            final String name, final String normalizedName,
            final int line, final int col) {


    }


    public void onCloseElementEnd (
            final String name, final String normalizedName,
            final int line, final int col) {


    }


    public void onElementInnerWhiteSpace (
            final String innerWhiteSpace,
            final int line, final int col) {


    }





    /*
     * -------------------------------
     * Processing Instruction handling
     * -------------------------------
     */

    public void onProcessingInstruction (
            final String processingInstruction,
            final String target, final String content,
            final int line, final int col) {


    }




}

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
package org.thymeleaf.engine.markup;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.engine.markup.dom.AttributeDefinition;
import org.thymeleaf.engine.markup.dom.ElementDefinition;
import org.thymeleaf.exceptions.TemplateOutputException;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public class DirectOutputMarkupHandler implements IMarkupHandler {


    private final String documentName;
    private final Writer writer;


    public DirectOutputMarkupHandler(final String documentName, final Writer writer) {
        super();
        this.documentName = documentName;
        this.writer = writer;
    }





    /*
     * ---------------
     * Document events
     * ---------------
     */


    public void onDocumentStart(
            final long startTimeNanos) {

        // Nothing to be done here

    }



    public void onDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos) {

        // Nothing to be done here

    }





    /*
     * ------------------------
     * XML Declaration events
     * ------------------------
     */

    public void onXmlDeclaration (
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone,
            final int line, final int col) {

        try {
            this.writer.write(xmlDeclaration);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }





    /*
     * ---------------------
     * DOCTYPE Clause events
     * ---------------------
     */

    public void onDocTypeClause (
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId,
            final int line, final int col) {

        try {
            this.writer.write(docTypeClause);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }





    /*
     * --------------------
     * CDATA Section events
     * --------------------
     */

    public void onCDATASection (
            final String content,
            final int line, final int col) {

        try {
            this.writer.write("<![CDATA[");
            this.writer.write(content);
            this.writer.write("]]>");
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }





    /*
     * -----------
     * Text events
     * -----------
     */

    public void onText (
            final String content,
            final int line, final int col) {

        try {
            this.writer.write(content);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }





    /*
     * --------------
     * Comment events
     * --------------
     */

    public void onComment (
            final String content,
            final int line, final int col) {

        try {
            this.writer.write("<!--");
            this.writer.write(content);
            this.writer.write("-->");
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }





    /*
     * ----------------
     * Element handling
     * ----------------
     */

    public void onAttribute (
            final AttributeDefinition attributeDefinition,
            final String name, final String operator, final String value, final String quotedValue,
            final int line, final int col) {

        try {
            this.writer.write(name);
            this.writer.write(operator);
            this.writer.write(quotedValue);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onStandaloneElementStart (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col) {

        try {
            this.writer.write("<");
            this.writer.write(name);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onStandaloneElementEnd (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col) {

        try {
            this.writer.write("/>");
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onOpenElementStart (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col) {

        try {
            this.writer.write("<");
            this.writer.write(name);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onOpenElementEnd (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col) {

        try {
            this.writer.write(">");
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onCloseElementStart (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col) {

        try {
            this.writer.write("</");
            this.writer.write(name);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onCloseElementEnd (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col) {

        try {
            this.writer.write(">");
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }


    public void onElementInnerWhiteSpace (
            final String innerWhiteSpace,
            final int line, final int col) {

        try {
            this.writer.write(innerWhiteSpace);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

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

        try {
            this.writer.write(processingInstruction);
        } catch (final IOException e) {
            throw new TemplateOutputException(
                    String.format("Error trying to write output for template \"{}\"", documentName), e);
        }

    }




}

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

import org.thymeleaf.engine.markup.dom.AttributeDefinition;
import org.thymeleaf.engine.markup.dom.ElementDefinition;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IMarkupHandler {



    public void onDocumentStart(final long startTimeNanos);
    public void onDocumentEnd(final long endTimeNanos, final long totalTimeNanos);

    public void onXmlDeclaration (
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone,
            final int line, final int col);

    public void onDocTypeClause (
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId,
            final int line, final int col);

    public void onCDATASection (
            final String content,
            final int line, final int col);

    public void onText (
            final String content,
            final int line, final int col);

    public void onComment (
            final String content,
            final int line, final int col);

    public void onAttribute (
            final AttributeDefinition attributeDefinition,
            final String name, final String operator, final String value, final String quotedValue,
            final int line, final int col);

    public void onStandaloneElementStart (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col);


    public void onStandaloneElementEnd (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col);


    public void onOpenElementStart (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col);


    public void onOpenElementEnd (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col);


    public void onCloseElementStart (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col);


    public void onCloseElementEnd (
            final ElementDefinition elementDefinition, final String name,
            final int line, final int col);


    public void onElementInnerWhiteSpace (
            final String innerWhiteSpace,
            final int line, final int col);

    public void onProcessingInstruction (
            final String processingInstruction,
            final String target, final String content,
            final int line, final int col);



}

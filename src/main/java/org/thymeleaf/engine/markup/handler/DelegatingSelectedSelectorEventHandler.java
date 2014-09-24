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

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 *
 */
public class DelegatingSelectedSelectorEventHandler implements ISelectedSelectorEventHandler {


    public DelegatingSelectedSelectorEventHandler() {
        super();
    }


    public void onSelectedXmlDeclaration(
            final String[] selectors, final boolean[] selectorMatches,
            final String xmlDeclaration,
            final String version, final String encoding, final boolean standalone,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onXmlDeclaration(xmlDeclaration, version, encoding, standalone, documentName, line, col);
    }

    public void onSelectedDocTypeClause(
            final String[] selectors, final boolean[] selectorMatches,
            final String docTypeClause,
            final String rootElementName, final String publicId, final String systemId,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onDocTypeClause(docTypeClause, rootElementName, publicId, systemId, documentName, line, col);
    }

    public void onSelectedCDATASection(
            final String[] selectors, final boolean[] selectorMatches,
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onCDATASection(buffer, offset, len, documentName, line, col);
    }

    public void onSelectedText(
            final String[] selectors, final boolean[] selectorMatches,
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onText(buffer, offset, len, documentName, line, col);
    }

    public void onSelectedComment(
            final String[] selectors, final boolean[] selectorMatches,
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onComment(buffer, offset, len, documentName, line, col);
    }

    public void onSelectedAttribute(
            final String[] selectors, final boolean[] selectorMatches,
            final char[] buffer,
            final int nameOffset, final int nameLen, final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen, final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen, final int valueOuterOffset, final int valueOuterLen, final int valueLine, final int valueCol,
            final String documentName,
            final IMarkupHandler handler) {
        handler.onAttribute(
                buffer,
                nameOffset, nameLen, nameLine, nameCol,
                operatorOffset, operatorLen, operatorLine, operatorCol,
                valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol,
                documentName);
    }

    public void onSelectedStandaloneElementStart(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onStandaloneElementStart(normalizedName, buffer, offset, len, minimized, documentName, line, col);
    }

    public void onSelectedStandaloneElementEnd(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final boolean minimized,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onStandaloneElementEnd(normalizedName, buffer, offset, len, minimized, documentName, line, col);
    }

    public void onSelectedOpenElementStart(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onOpenElementStart(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedOpenElementEnd(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onOpenElementEnd(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedCloseElementStart(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedCloseElementEnd(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedAutoCloseElementStart(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onAutoCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedAutoCloseElementEnd(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onAutoCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedUnmatchedCloseElementStart(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onUnmatchedCloseElementStart(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedUnmatchedCloseElementEnd(
            final String[] selectors, final boolean[] selectorMatches,
            final String normalizedName, final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onUnmatchedCloseElementEnd(normalizedName, buffer, offset, len, documentName, line, col);
    }

    public void onSelectedElementInnerWhiteSpace(
            final String[] selectors, final boolean[] selectorMatches,
            final char[] buffer, final int offset, final int len,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onElementInnerWhiteSpace(buffer, offset, len, documentName, line, col);
    }

    public void onSelectedProcessingInstruction(
            final String[] selectors, final boolean[] selectorMatches,
            final String processingInstruction, final String target, final String content,
            final String documentName, final int line, final int col,
            final IMarkupHandler handler) {
        handler.onProcessingInstruction(processingInstruction, target, content, documentName, line, col);
    }


}

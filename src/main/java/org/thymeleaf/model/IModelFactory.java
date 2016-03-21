/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.model;

import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IModelFactory {


    public IModel createModel();
    public IModel createModel(final ITemplateEvent event);

    public IModel parse(final TemplateData ownerTemplate, final String template);

    public ICDATASection createCDATASection(final CharSequence content);

    public IComment createComment(final CharSequence content);

    public IDocType createHTML5DocType();
    public IDocType createDocType(final String publicId, final String systemId);
    public IDocType createDocType(
            final String keyword,
            final String elementName,
            final String type,
            final String publicId,
            final String systemId,
            final String internalSubset);

    public IProcessingInstruction createProcessingInstruction(final String target, final String content);

    public IText createText(final CharSequence text);

    public IXMLDeclaration createXMLDeclaration(final String version, final String encoding, final String standalone);


    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final boolean synthetic, final boolean minimized);
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic, final boolean minimized);
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final Map<String,String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic, final boolean minimized);

    public IStandaloneElementTag setAttribute(final IStandaloneElementTag standaloneElementTag, final String attributeName, final String attributeValue);
    public IStandaloneElementTag setAttribute(final IStandaloneElementTag standaloneElementTag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);
    public IStandaloneElementTag replaceAttribute(final IStandaloneElementTag standaloneElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue);
    public IStandaloneElementTag replaceAttribute(final IStandaloneElementTag standaloneElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);
    public IStandaloneElementTag removeAttribute(final IStandaloneElementTag standaloneElementTag, final String attributeName);
    public IStandaloneElementTag removeAttribute(final IStandaloneElementTag standaloneElementTag, final String prefix, final String name);
    public IStandaloneElementTag removeAttribute(final IStandaloneElementTag standaloneElementTag, final AttributeName attributeName);


    public IOpenElementTag createOpenElementTag(final String elementName, final boolean synthetic);
    public IOpenElementTag createOpenElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic);
    public IOpenElementTag createOpenElementTag(final String elementName, final Map<String,String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic);

    public IOpenElementTag setAttribute(final IOpenElementTag openElementTag, final String attributeName, final String attributeValue);
    public IOpenElementTag setAttribute(final IOpenElementTag openElementTag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);
    public IOpenElementTag replaceAttribute(final IOpenElementTag openElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue);
    public IOpenElementTag replaceAttribute(final IOpenElementTag openElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);
    public IOpenElementTag removeAttribute(final IOpenElementTag openElementTag, final String attributeName);
    public IOpenElementTag removeAttribute(final IOpenElementTag openElementTag, final String prefix, final String name);
    public IOpenElementTag removeAttribute(final IOpenElementTag openElementTag, final AttributeName attributeName);


    public ICloseElementTag createCloseElementTag(final String elementName, final boolean synthetic, final boolean unmatched);


}

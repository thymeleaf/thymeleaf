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

    public IOpenElementTag createOpenElementTag(final String elementName, final boolean synthetic);
    public IOpenElementTag createOpenElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic);
    public IOpenElementTag createOpenElementTag(final String elementName, final Map<String,String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic);

    public ICloseElementTag createCloseElementTag(final String elementName, final boolean synthetic, final boolean unmatched);


    public <T extends IProcessableElementTag> T setAttribute(final T tag, final String attributeName, final String attributeValue);
    public <T extends IProcessableElementTag> T setAttribute(final T tag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);
    public <T extends IProcessableElementTag> T replaceAttribute(final T tag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue);
    public <T extends IProcessableElementTag> T replaceAttribute(final T tag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final String attributeName);
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final String prefix, final String name);
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final AttributeName attributeName);


}

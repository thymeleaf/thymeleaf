/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class StandardModelFactory implements IModelFactory {

    private static final String[][] SYNTHETIC_INNER_WHITESPACES = new String[][] {
            new String[0],
            Attributes.DEFAULT_WHITE_SPACE_ARRAY,
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE },
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE },
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE },
            new String[] { Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE, Attributes.DEFAULT_WHITE_SPACE }
    };


    private final IEngineConfiguration configuration;
    private final AttributeDefinitions attributeDefinitions;
    private final ElementDefinitions elementDefinitions;
    private final TemplateMode templateMode;



    public StandardModelFactory(final IEngineConfiguration configuration, final TemplateMode templateMode) {

        super();

        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(configuration.getAttributeDefinitions(), "Attribute Definitions returned by Engine Configuration cannot be null");
        Validate.notNull(configuration.getElementDefinitions(), "Element Definitions returned by Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");

        this.configuration = configuration;
        this.attributeDefinitions = this.configuration.getAttributeDefinitions();
        this.elementDefinitions = this.configuration.getElementDefinitions();
        this.templateMode = templateMode;

    }



    private void checkRestrictedEventForTextTemplateMode(final String eventClass) {
        if (this.templateMode.isText()) {
            throw new TemplateProcessingException(
                    "Events of class " + eventClass + " cannot be created in a text-type template " +
                    "mode (" + this.templateMode + ")");
        }
    }



    public IModel createModel() {
        return new Model(this.configuration, this.templateMode);
    }


    public IModel createModel(final ITemplateEvent event) {
        final Model model = new Model(this.configuration, this.templateMode);
        model.add(event);
        return model;
    }




    public IModel parse(final TemplateData ownerTemplate, final String template) {
        // We will be setting useCache to false because we don't want to pollute the cache with mere String
        // parsing done from here. Also, we are 'artificially' specifying it as nested even if we don't really
        // know if this fragment is exactly a nested text inside the template, but that's not really important...
        return this.configuration.getTemplateManager().parseString(ownerTemplate, template, 0, 0, this.templateMode, false);
    }




    public ICDATASection createCDATASection(final CharSequence content) {
        checkRestrictedEventForTextTemplateMode("CDATASection");
        return new CDATASection(content);
    }




    public IComment createComment(final CharSequence content) {
        checkRestrictedEventForTextTemplateMode("Comment");
        return new Comment(content);
    }




    public IDocType createHTML5DocType() {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(null, null);
    }


    public IDocType createDocType(final String publicId, final String systemId) {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(publicId, systemId);
    }


    public IDocType createDocType(
            final String keyword,
            final String elementName,
            final String publicId,
            final String systemId,
            final String internalSubset) {

        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(keyword, elementName, publicId, systemId, internalSubset);
    }




    public IProcessingInstruction createProcessingInstruction(final String target, final String content) {
        checkRestrictedEventForTextTemplateMode("ProcessingInstruction");
        return new ProcessingInstruction(target, content);
    }




    public IText createText(final CharSequence text) {
        return new Text(text);
    }




    public IXMLDeclaration createXMLDeclaration(final String version, final String encoding, final String standalone) {
        checkRestrictedEventForTextTemplateMode("XMLDeclaration");
        return new XMLDeclaration(XMLDeclaration.DEFAULT_KEYWORD, version, encoding, standalone);
    }




    @Override
    public IStandaloneElementTag createStandaloneElementTag(final String elementName) {
        return createStandaloneElementTag(elementName, false, true);
    }


    @Override
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final String attributeName, final String attributeValue) {
        return createStandaloneElementTag(elementName, attributeName, attributeValue, false, true);
    }


    @Override
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final boolean synthetic, final boolean minimized) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        return new StandaloneElementTag(this.templateMode, elementDefinition, elementName, null, synthetic, minimized);
    }


    @Override
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic, final boolean minimized) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        final Attributes attributes = buildAttributes(new Attribute[] { buildAttribute(attributeName, attributeValue, null) });
        return new StandaloneElementTag(this.templateMode, elementDefinition, elementName, attributes, synthetic, minimized);
    }


    @Override
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final Map<String, String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic, final boolean minimized) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        final Attributes attributesObj = buildAttributes(buildAttributeArray(attributes, attributeValueQuotes));
        return new StandaloneElementTag(this.templateMode, elementDefinition, elementName, attributesObj, synthetic, minimized);
    }




    @Override
    public IOpenElementTag createOpenElementTag(final String elementName) {
        return createOpenElementTag(elementName, false);
    }


    @Override
    public IOpenElementTag createOpenElementTag(final String elementName, final String attributeName, final String attributeValue) {
        return createOpenElementTag(elementName, attributeName, attributeValue, false);
    }


    @Override
    public IOpenElementTag createOpenElementTag(final String elementName, final boolean synthetic) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        return new OpenElementTag(this.templateMode, elementDefinition, elementName, null, synthetic);
    }


    @Override
    public IOpenElementTag createOpenElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        final Attributes attributes = buildAttributes(new Attribute[] { buildAttribute(attributeName, attributeValue, null) });
        return new OpenElementTag(this.templateMode, elementDefinition, elementName, attributes, synthetic);
    }


    @Override
    public IOpenElementTag createOpenElementTag(final String elementName, final Map<String, String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        final Attributes attributesObj = buildAttributes(buildAttributeArray(attributes, attributeValueQuotes));
        return new OpenElementTag(this.templateMode, elementDefinition, elementName, attributesObj, synthetic);
    }




    @Override
    public <T extends IProcessableElementTag> T setAttribute(final T tag, final String attributeName, final String attributeValue) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) setAttribute((IOpenElementTag)tag, attributeName, attributeValue);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) setAttribute((IStandaloneElementTag)tag, attributeName, attributeValue);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }


    @Override
    public <T extends IProcessableElementTag> T setAttribute(final T tag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) setAttribute((IOpenElementTag)tag, attributeName, attributeValue, attributeValueQuotes);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) setAttribute((IStandaloneElementTag)tag, attributeName, attributeValue, attributeValueQuotes);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }


    @Override
    public <T extends IProcessableElementTag> T replaceAttribute(final T tag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) replaceAttribute((IOpenElementTag)tag, oldAttributeName, attributeName, attributeValue);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) replaceAttribute((IStandaloneElementTag)tag, oldAttributeName, attributeName, attributeValue);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }


    @Override
    public <T extends IProcessableElementTag> T replaceAttribute(final T tag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) replaceAttribute((IOpenElementTag)tag, oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) replaceAttribute((IStandaloneElementTag)tag, oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }


    @Override
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final String attributeName) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) removeAttribute((IOpenElementTag)tag, attributeName);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) removeAttribute((IStandaloneElementTag)tag, attributeName);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }


    @Override
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final String prefix, final String name) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) removeAttribute((IOpenElementTag)tag, prefix, name);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) removeAttribute((IStandaloneElementTag)tag, prefix, name);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }


    @Override
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final AttributeName attributeName) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return (T) removeAttribute((IOpenElementTag)tag, attributeName);
        }
        if (tag instanceof IStandaloneElementTag) {
            return (T) removeAttribute((IStandaloneElementTag)tag, attributeName);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }




    private IStandaloneElementTag setAttribute(final IStandaloneElementTag standaloneElementTag, final String attributeName, final String attributeValue) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return setAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName, attributeValue);
        }
        return ((StandaloneElementTag) standaloneElementTag).setAttribute(this.attributeDefinitions, null, attributeName, attributeValue, null);
    }


    private IStandaloneElementTag setAttribute(final IStandaloneElementTag standaloneElementTag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return setAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName, attributeValue, attributeValueQuotes);
        }
        return ((StandaloneElementTag) standaloneElementTag).setAttribute(this.attributeDefinitions, null, attributeName, attributeValue, attributeValueQuotes);
    }


    private IStandaloneElementTag replaceAttribute(final IStandaloneElementTag standaloneElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return replaceAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), oldAttributeName, attributeName, attributeValue);
        }
        return ((StandaloneElementTag) standaloneElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, null, attributeName, attributeValue, null);
    }


    private IStandaloneElementTag replaceAttribute(final IStandaloneElementTag standaloneElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return replaceAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        return ((StandaloneElementTag) standaloneElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, null, attributeName, attributeValue, attributeValueQuotes);
    }


    private IStandaloneElementTag removeAttribute(final IStandaloneElementTag standaloneElementTag, final String attributeName) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return removeAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName);
        }
        return ((StandaloneElementTag) standaloneElementTag).removeAttribute(attributeName);
    }


    private IStandaloneElementTag removeAttribute(final IStandaloneElementTag standaloneElementTag, final String prefix, final String name) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return removeAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), prefix, name);
        }
        return ((StandaloneElementTag) standaloneElementTag).removeAttribute(prefix, name);
    }


    private IStandaloneElementTag removeAttribute(final IStandaloneElementTag standaloneElementTag, final AttributeName attributeName) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return removeAttribute(StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName);
        }
        return ((StandaloneElementTag) standaloneElementTag).removeAttribute(attributeName);
    }




    private IOpenElementTag setAttribute(final IOpenElementTag openElementTag, final String attributeName, final String attributeValue) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return setAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName, attributeValue);
        }
        return ((OpenElementTag) openElementTag).setAttribute(this.attributeDefinitions, null, attributeName, attributeValue, null);
    }


    private IOpenElementTag setAttribute(final IOpenElementTag openElementTag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return setAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName, attributeValue, attributeValueQuotes);
        }
        return ((OpenElementTag) openElementTag).setAttribute(this.attributeDefinitions, null, attributeName, attributeValue, attributeValueQuotes);
    }


    private IOpenElementTag replaceAttribute(final IOpenElementTag openElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return replaceAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), oldAttributeName, attributeName, attributeValue);
        }
        return ((OpenElementTag) openElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, null, attributeName, attributeValue, null);
    }


    private IOpenElementTag replaceAttribute(final IOpenElementTag openElementTag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return replaceAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        return ((OpenElementTag) openElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, null, attributeName, attributeValue, attributeValueQuotes);
    }


    private IOpenElementTag removeAttribute(final IOpenElementTag openElementTag, final String attributeName) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return removeAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName);
        }
        return ((OpenElementTag) openElementTag).removeAttribute(attributeName);
    }


    private IOpenElementTag removeAttribute(final IOpenElementTag openElementTag, final String prefix, final String name) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return removeAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), prefix, name);
        }
        return ((OpenElementTag) openElementTag).removeAttribute(prefix, name);
    }


    private IOpenElementTag removeAttribute(final IOpenElementTag openElementTag, final AttributeName attributeName) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return removeAttribute(OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName);
        }
        return ((OpenElementTag) openElementTag).removeAttribute(attributeName);
    }




    @Override
    public ICloseElementTag createCloseElementTag(final String elementName) {
        return createCloseElementTag(elementName, false, false);
    }


    @Override
    public ICloseElementTag createCloseElementTag(final String elementName, final boolean synthetic, final boolean unmatched) {
        final ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        return new CloseElementTag(this.templateMode, elementDefinition, elementName, null, synthetic, unmatched);
    }




    private Attribute buildAttribute(final String name, final String value, final AttributeValueQuotes quotes) {
        final AttributeDefinition attributeDefinition = this.attributeDefinitions.forName(this.templateMode, name);
        return new Attribute(attributeDefinition, name, Attribute.DEFAULT_OPERATOR, value, quotes, null, -1, -1);
    }


    private Attribute[] buildAttributeArray(final Map<String,String> attributes, final AttributeValueQuotes quotes) {
        if (attributes == null || attributes.size() == 0) {
            return Attributes.EMPTY_ATTRIBUTE_ARRAY;
        }
        int i = 0;
        final Attribute[] newAttributes = new Attribute[attributes.size()];
        for (final Map.Entry<String,String> attributesEntry : attributes.entrySet()) {
            newAttributes[i++] = buildAttribute(attributesEntry.getKey(), attributesEntry.getValue(), quotes);
        }
        return newAttributes;
    }


    private Attributes buildAttributes(final Attribute[] attributeArray) {
        if (attributeArray == null || attributeArray.length == 0) {
            return Attributes.EMPTY_ATTRIBUTES;
        }
        final String[] innerWhiteSpaces;
        if (attributeArray.length < SYNTHETIC_INNER_WHITESPACES.length) {
            innerWhiteSpaces = SYNTHETIC_INNER_WHITESPACES[attributeArray.length];
        } else {
            innerWhiteSpaces = new String[attributeArray.length];
            Arrays.fill(innerWhiteSpaces, Attributes.DEFAULT_WHITE_SPACE);
        }
        return new Attributes(attributeArray, innerWhiteSpaces);
    }


}

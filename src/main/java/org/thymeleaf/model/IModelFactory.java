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
package org.thymeleaf.model;

import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;

/**
 * <p>
 *   Interface defining model factories.
 * </p>
 * <p>
 *   A Model Factory can be obtained at custom processor artifacts by means of
 *   {@link ITemplateContext#getModelFactory()}, and then used for creating and modifying models
 *   ({@link IModel}) and events ({@link ITemplateEvent}.
 * </p>
 * <p>
 *   The {@link org.thymeleaf.engine.StandardModelFactory} implementation will be used by default.
 * </p>
 *
 * @see IModel
 * @see ITemplateEvent
 * @see org.thymeleaf.engine.StandardModelFactory
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IModelFactory {


    /**
     * <p>
     *   Creates a new, empty model.
     * </p>
     *
     * @return the new model.
     */
    public IModel createModel();

    /**
     * <p>
     *   Creates a new model containing only one event (initially).
     * </p>
     *
     * @param event the event to be put into the newly created model.
     * @return the new model.
     */
    public IModel createModel(final ITemplateEvent event);


    /**
     * <p>
     *   Parse the template specified as String and return the result as a model.
     * </p>
     * <p>
     *   Note the {@code template} argument specified here is the template content itself, not a name to be resolved
     *   by a template resolver. The {@code ownerTemplate} argument is mandatory, and specifies the template that
     *   is being processed and as a part of which process the String template is being parsed.
     * </p>
     *
     * @param ownerTemplate the template being processed, for which the String template is being parsed
     *                      into a model object.
     * @param template the String containing the contents of the template to be parsed.
     * @return the {@link IModel} representing the parsed template.
     */
    public IModel parse(final TemplateData ownerTemplate, final String template);

    /**
     * <p>
     *   Create a new CDATA Section event, containing the specified contents.
     * </p>
     *
     * @param content the content (i.e. without prefix or suffix) of the new CDATA Section.
     * @return the new CDATA Section.
     */
    public ICDATASection createCDATASection(final CharSequence content);

    /**
     * <p>
     *   Create a new Comment event, containing the specified contents.
     * </p>
     *
     * @param content the content (i.e. without prefix or suffix) of the new Comment.
     * @return the new Comment.
     */
    public IComment createComment(final CharSequence content);

    /**
     * <p>
     *   Create a DOCTYPE clause event for HTML5 (no type, no public or system id).
     * </p>
     *
     * @return the new DOCTYPE, corresponding with an HTML5 DOCTYPE clause.
     */
    public IDocType createHTML5DocType();

    /**
     * <p>
     *   Create a DOCTYPE clause event with the specified public ID and system ID.
     * </p>
     *
     * @param publicId the public ID to be applied (might be null).
     * @param systemId the system ID to be applied (might be null if public ID is also null).
     * @return the new DOCTPYE.
     */
    public IDocType createDocType(final String publicId, final String systemId);

    /**
     * <p>
     *  Create a DOCTYPE clause event, specifying all its components.
     * </p>
     *
     * @param keyword the keyword value (should be {@code DOCTYPE}, but case might vary).
     * @param elementName the root element name.
     * @param publicId the public ID (might be null).
     * @param systemId the system ID (might be null).
     * @param internalSubset the internal subset (might be null).
     * @return the new DOCTYPE.
     */
    public IDocType createDocType(
            final String keyword,
            final String elementName,
            final String publicId,
            final String systemId,
            final String internalSubset);

    /**
     * <p>
     *   Create a new Processing Instruction event, specifying its target and content.
     * </p>
     *
     * @param target the target value.
     * @param content the content value.
     * @return the new Processing Instruction.
     */
    public IProcessingInstruction createProcessingInstruction(final String target, final String content);

    /**
     * <p>
     *   Create a new Text event, specifying its contents.
     * </p>
     *
     * @param text the text contents.
     * @return the new Text.
     */
    public IText createText(final CharSequence text);

    /**
     * <p>
     *   Create a new XML Declaration event, specifying values for all its attributes.
     * </p>
     *
     * @param version the version value (might be null).
     * @param encoding the encoding value (might be null).
     * @param standalone the standalone value (might be null).
     * @return the new XML Declaration.
     */
    public IXMLDeclaration createXMLDeclaration(final String version, final String encoding, final String standalone);


    /**
     * <p>
     *   Create a new standalone element tag, non synthetic and minimized.
     * </p>
     * <p>
     *   This is equivalent to calling {@link #createStandaloneElementTag(String, boolean, boolean)} with
     *   {@code false} as a value for {@code synthetic} and {@code true} as a value for {@code minimized}.
     * </p>
     *
     * @param elementName the element name.
     * @return the standalone tag.
     */
    public IStandaloneElementTag createStandaloneElementTag(final String elementName);

    /**
     * <p>
     *   Create a new standalone element tag, non synthetic and minimized, specifying one attribute.
     * </p>
     * <p>
     *   This is equivalent to calling {@link #createStandaloneElementTag(String, String, String, boolean, boolean)} with
     *   {@code false} as a value for {@code synthetic} and {@code true} as a value for {@code minimized}.
     * </p>
     *
     * @param elementName the element name.
     * @param attributeName the name of the attribute to be added to the tag.
     * @param attributeValue the value of the attribute to be added to the tag.
     * @return the standalone tag.
     */
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final String attributeName, final String attributeValue);

    /**
     * <p>
     *   Create a new standalone element tag.
     * </p>
     *
     * @param elementName the element name.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @param minimized whether the tag is minimized or not, i.e. whether it ends in {@code />} or simply
     *                  {@code >}.
     * @return the standalone tag.
     */
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final boolean synthetic, final boolean minimized);

    /**
     * <p>
     *   Create a new standalone element tag, specifying one attribute.
     * </p>
     *
     * @param elementName the element name.
     * @param attributeName the name of the attribute to be added to the tag.
     * @param attributeValue the value of the attribute to be added to the tag.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @param minimized whether the tag is minimized or not, i.e. whether it ends in {@code />} or simply
     *                  {@code >}.
     * @return the standalone tag.
     */
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic, final boolean minimized);

    /**
     * <p>
     *   Create a new standalone element tag, specifying several attributes.
     * </p>
     *
     * @param elementName the element name.
     * @param attributes the map of attribute names and values.
     * @param attributeValueQuotes the type of quotes to be used for representing the attribute values.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @param minimized whether the tag is minimized or not, i.e. whether it ends in {@code />} or simply
     *                  {@code >}.
     * @return the standalone tag.
     */
    public IStandaloneElementTag createStandaloneElementTag(final String elementName, final Map<String,String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic, final boolean minimized);


    /**
     * <p>
     *   Create a new open element tag, non-synthetic.
     * </p>
     * <p>
     *   This is equivalent to calling {@link #createOpenElementTag(String, boolean)} with
     *   {@code false} as a value for {@code synthetic}.
     * </p>
     *
     * @param elementName the element name.
     * @return the open tag.
     */
    public IOpenElementTag createOpenElementTag(final String elementName);

    /**
     * <p>
     *   Create a new open element tag, non-synthetic, specifying one attribute.
     * </p>
     * <p>
     *   This is equivalent to calling {@link #createOpenElementTag(String, String, String, boolean)} with
     *   {@code false} as a value for {@code synthetic}.
     * </p>
     *
     * @param elementName the element name.
     * @param attributeName the name of the attribute to be added to the tag.
     * @param attributeValue the value of the attribute to be added to the tag.
     * @return the open tag.
     */
    public IOpenElementTag createOpenElementTag(final String elementName, final String attributeName, final String attributeValue);

    /**
     * <p>
     *   Create a new open element tag.
     * </p>
     *
     * @param elementName the element name.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @return the open tag.
     */
    public IOpenElementTag createOpenElementTag(final String elementName, final boolean synthetic);

    /**
     * <p>
     *   Create a new open element tag, specifying one attribute.
     * </p>
     *
     * @param elementName the element name.
     * @param attributeName the name of the attribute to be added to the tag.
     * @param attributeValue the value of the attribute to be added to the tag.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @return the open tag.
     */
    public IOpenElementTag createOpenElementTag(final String elementName, final String attributeName, final String attributeValue, final boolean synthetic);

    /**
     * <p>
     *   Create a new open element tag, specifying several attributes.
     * </p>
     *
     * @param elementName the element name.
     * @param attributes the map of attribute names and values.
     * @param attributeValueQuotes the type of quotes to be used for representing the attribute values.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @return the open tag.
     */
    public IOpenElementTag createOpenElementTag(final String elementName, final Map<String,String> attributes, final AttributeValueQuotes attributeValueQuotes, final boolean synthetic);


    /**
     * <p>
     *   Create a new close tag, non-synthetic and non-unmatched.
     * </p>
     * <p>
     *   This is equivalent to calling {@link #createCloseElementTag(String, boolean, boolean)} with
     *   {@code false} as a value for {@code synthetic} and also {@code false} as a value for {@code unmatched}.
     * </p>
     *
     * @param elementName the element name.
     * @return the close tag.
     */
    public ICloseElementTag createCloseElementTag(final String elementName);


    /**
     * <p>
     *   Create a new close tag.
     * </p>
     *
     * @param elementName the element name.
     * @param synthetic whether the tag is synthetic or not. Synthetic tags are used for balancing of markup and
     *                  will not appear on output.
     * @param unmatched whether this tag should be considered <em>unmatched</em>, i.e. there is no corresponding
     *                  previous open tag for it.
     * @return the close tag.
     */
    public ICloseElementTag createCloseElementTag(final String elementName, final boolean synthetic, final boolean unmatched);


    /**
     * <p>
     *   Create a new tag object that adds a new attribute to the existing ones in a specified tag.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     *
     * @param tag the original tag.
     * @param attributeName the name of the attribute to be added.
     * @param attributeValue the value of the attribute to be added.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T setAttribute(final T tag, final String attributeName, final String attributeValue);

    /**
     * <p>
     *   Create a new tag object that adds a new attribute to the existing ones in a specified tag, also specifying the
     *   type of quotes to be used for representing the attribute value.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     *
     * @param tag the original tag.
     * @param attributeName the name of the attribute to be added.
     * @param attributeValue the value of the attribute to be added.
     * @param attributeValueQuotes the type of quotes to be used for representing the attribute value.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T setAttribute(final T tag, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);

    /**
     * <p>
     *   Create a new tag object replacing an attribute in the original tag with another one.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     * <p>
     *   If the attribute being replaced does not exist, the new one will be created as if
     *   {@link #setAttribute(IProcessableElementTag, String, String)} was called instead. If the old attribute does
     *   exist, its position in the tag as well as its quote type will be used.
     * </p>
     *
     * @param tag the original tag.
     * @param oldAttributeName the name of the attribute to be replaced.
     * @param attributeName the name of the attribute to be added.
     * @param attributeValue the value of the attribute to be added.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T replaceAttribute(final T tag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue);

    /**
     * <p>
     *   Create a new tag object replacing an attribute in the original tag with another one, also specifying the
     *   type of quotes to be used for representing the attribute value.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     * <p>
     *   If the attribute being replaced does not exist, the new one will be created as if
     *   {@link #setAttribute(IProcessableElementTag, String, String)} was called instead. If the old attribute does
     *   exist, its position in the tag will be used.
     * </p>
     *
     * @param tag the original tag.
     * @param oldAttributeName the name of the attribute to be replaced.
     * @param attributeName the name of the attribute to be added.
     * @param attributeValue the value of the attribute to be added.
     * @param attributeValueQuotes the type of quotes to be used for representing the attribute value.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T replaceAttribute(final T tag, final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);

    /**
     * <p>
     *   Create a new tag removing an existing attribute.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     * <p>
     *   If the attribute being removed does not exist, nothing will be done and the same tag object will be returned.
     * </p>
     *
     * @param tag the original tag.
     * @param attributeName the name of the attribute to be removed.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final String attributeName);

    /**
     * <p>
     *   Create a new tag removing an existing attribute.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     * <p>
     *   If the attribute being removed does not exist, nothing will be done and the same tag object will be returned.
     * </p>
     *
     * @param tag the original tag.
     * @param prefix the prefix of the attribute (might be null).
     * @param name the name of the attribute.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final String prefix, final String name);

    /**
     * <p>
     *   Create a new tag removing an existing attribute.
     * </p>
     * <p>
     *   Note that this method should create a new object because {@link ITemplateEvent} implementations are immutable.
     *   Also, the created tag will be of the same type (i.e. standalone or open) as the specified as argument.
     * </p>
     * <p>
     *   If the attribute being removed does not exist, nothing will be done and the same tag object will be returned.
     * </p>
     *
     * @param tag the original tag.
     * @param attributeName the attribute name.
     * @param <T> the type of the original and new tags.
     * @return the new tag.
     */
    public <T extends IProcessableElementTag> T removeAttribute(final T tag, final AttributeName attributeName);


}

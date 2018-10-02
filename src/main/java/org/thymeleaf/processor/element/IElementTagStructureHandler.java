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
package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;

/**
 * <p>
 *   Structure handler class meant to be used by {@link IElementTagProcessor} implementations.
 * </p>
 * <p>
 *   Structure handlers allow processors to instruct the engine to perform a series of actions that cannot
 *   be done directly from the processors themselves, usually because these actions are applied or have effects
 *   on scopes broader than the processed events themselves.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IElementTagStructureHandler {


    /**
     * <p>
     *   Resets all actions specified so far for the current processor execution.
     * </p>
     */
    public void reset();

    /**
     * <p>
     *   Instructs the engine to set a new local variable.
     * </p>
     *
     * @param name the name of the variable.
     * @param value the value of the variable.
     */
    public void setLocalVariable(final String name, final Object value);

    /**
     * <p>
     *   Instructs the engine to remove a local variable.
     * </p>
     *
     * @param name the name of the variable.
     */
    public void removeLocalVariable(final String name);

    /**
     * <p>
     *   Instructs the engine to set an attribute (existing or not) in the current tag being
     *   processed.
     * </p>
     *
     * @param attributeName the name of the attribute.
     * @param attributeValue the value of the attribute.
     */
    public void setAttribute(final String attributeName, final String attributeValue);

    /**
     * <p>
     *   Instructs the engine to set an attribute (existing or not) in the current tag being
     *   processed.
     * </p>
     *
     * @param attributeName the name of the attribute.
     * @param attributeValue the value of the attribute.
     * @param attributeValueQuotes the type of quotes to be set for the attribute.
     */
    public void setAttribute(final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);

    /**
     * <p>
     *   Instructs the engine to replace an existing attribute for a new one (which can also exist)
     *   in the current tag being processed.
     * </p>
     *
     * @param oldAttributeName the name of the attribute to be replaced.
     * @param attributeName the name of the new attribute.
     * @param attributeValue the value of the new attribute.
     */
    public void replaceAttribute(final AttributeName oldAttributeName, final String attributeName, final String attributeValue);

    /**
     * <p>
     *   Instructs the engine to replace an existing attribute for a new one (which can also exist)
     *   in the current tag being processed.
     * </p>
     *
     * @param oldAttributeName the name of the attribute to be replaced.
     * @param attributeName the name of the new attribute.
     * @param attributeValue the value of the new attribute.
     * @param attributeValueQuotes the type of quotes to be set for the attribute.
     */
    public void replaceAttribute(final AttributeName oldAttributeName, final String attributeName, final String attributeValue, final AttributeValueQuotes attributeValueQuotes);

    /**
     * <p>
     *   Instructs the engine to remove an attribute from the tag being processed.
     * </p>
     *
     * @param attributeName the complete name of the attribute to be removed.
     */
    public void removeAttribute(final String attributeName);

    /**
     * <p>
     *   Instructs the engine to remove an attribute from the tag being processed.
     * </p>
     *
     * @param prefix the prefix of the attribute to be removed.
     * @param name the name of the attribute to be removed.
     */
    public void removeAttribute(final String prefix, final String name);

    /**
     * <p>
     *   Instructs the engine to remove an attribute from the tag being processed.
     * </p>
     *
     * @param attributeName the name of the attribute to be removed.
     */
    public void removeAttribute(final AttributeName attributeName);


    /**
     * <p>
     *   Instructs the engine to set a new selection target.
     * </p>
     * <p>
     *   The <em>selection target</em> is the object on which selection expressions {@code (*{...})} are executed.
     *   In the Standard Dialect, this selection target is usually modified by means of the {@code th:object}
     *   attribute, but custom processors can do it too. Note the selection target has the same scope as a local
     *   variable, and will therefore be accessible only inside the body of the element being processed.
     * </p>
     * <p>
     *   See {@link ITemplateContext#getSelectionTarget()}
     * </p>
     *
     * @param selectionTarget the selection target to be set.
     */
    public void setSelectionTarget(final Object selectionTarget);

    /**
     * <p>
     *   Instructs the engine to set a new inliner.
     * </p>
     * <p>
     *   Inliners are used for processing all text nodes ({@link org.thymeleaf.model.IText} events) appearing
     *   in the body of the element being processed. This is the mechanism used by the th:inline attribute to
     *   enable inlining in any of the specified modes (text, javascript, etc).
     * </p>
     * <p>
     *   See {@link ITemplateContext#getInliner()}
     * </p>
     *
     * @param inliner the inliner.
     */
    public void setInliner(final IInliner inliner);

    /**
     * <p>
     *   Instructs the engine to set a new template data.
     * </p>
     * <p>
     *   This method modifies the metadata about the template that is actually being processed. When
     *   inserting fragments, this allows the engine to know data about the specific fragment being
     *   processed, and also the complete stack of fragments being nested.
     * </p>
     * <p>
     *   See {@link ITemplateContext#getTemplateData()}
     * </p>
     *
     * @param templateData the template data.
     */
    public void setTemplateData(final TemplateData templateData);

    /**
     * <p>
     *   Instructs the engine to set a new body for the current element, in the form of a {@code CharSequence}.
     * </p>
     * <p>
     *   This is the way a processor can change what is shown inside an element during processing. For example,
     *   it is this way how {@code th:text} changes the contents of its containing element.
     * </p>
     *
     * @param text the text to be used as the new body.
     * @param processable whether the text being set should be considered <em>processable</em>, and therefore
     *                    any {@link org.thymeleaf.processor.text.ITextProcessor} processors might be applied on it,
     *                    or not.
     */
    public void setBody(final CharSequence text, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to set a new body for the current element, in the form of an {@link IModel}.
     * </p>
     * <p>
     *   This is the way a processor can change what is shown inside an element during processing. For example,
     *   it is this way how {@code th:utext} changes the contents of its containing element.
     * </p>
     *
     * @param model the model to be used as the new body.
     * @param processable whether the model being set should be considered <em>processable</em>, and therefore
     *                    any processors might be applied on its events, or not.
     */
    public void setBody(final IModel model, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to insert the specified model just before the element being processed.
     * </p>
     * <p>
     *   Processors can use this method to insert content before the tag they are processing, but with the
     *   limitation that such content (in the form of an {@link IModel}) cannot be <em>processable</em>, i.e.
     *   no processors will be executed on their elements or texts.
     * </p>
     *
     * @param model the model to be inserted
     */
    public void insertBefore(final IModel model);

    /**
     * <p>
     *   Instructs the engine to insert the specified model just after the element being processed.
     * </p>
     * <p>
     *   Processors can use this method to insert content just after the tag they are processing. Note that such
     *   content will be inserted after the <em>tag</em>, not the <em>element</em>, which in practice means that
     *   if the tag is an <em>open tag</em>, the new content will be inserted as the first part of the element's
     *   body.
     * </p>
     *
     * @param model the model to be inserted.
     * @param processable whether the inserted model should be considered <em>processable</em> or not (i.e. whether
     *                    processors should be executed on it).
     */
    public void insertImmediatelyAfter(final IModel model, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to replace the current element with the specified text (a {@code CharSequence}).
     * </p>
     * <p>
     *   Note it is the <em>complete element</em> that will be replaced with the specified text, i.e. the
     *   open tag, the body and the close tag.
     * </p>
     *
     * @param text the text to be used as a replacement.
     * @param processable whether the text should be considered <em>processable</em> or not.
     */
    public void replaceWith(final CharSequence text, final boolean processable);

    /**
     * <p>
     *   Instructs the engine to replace the current element with the specified model (a {@link IModel}).
     * </p>
     * <p>
     *   Note it is the <em>complete element</em> that will be replaced with the specified model, i.e. the
     *   open tag, the body and the close tag.
     * </p>
     *
     * @param model the model to be used as a replacement.
     * @param processable whether the model should be considered <em>processable</em> or not.
     */
    public void replaceWith(final IModel model, final boolean processable);


    /**
     * <p>
     *   Instructs the engine to remove the entire element that is being processed (open tag, body, close tag).
     * </p>
     */
    public void removeElement();

    /**
     * <p>
     *   Instructs the engine to remove the tags delimiting the element being processed (open and close tag), but
     *   keep the body.
     * </p>
     */
    public void removeTags();

    /**
     * <p>
     *   Instructs the engine to remove the body of the element being processed, but keep the open and close tags.
     * </p>
     */
    public void removeBody();

    /**
     * <p>
     *   Instructs the engine to remove all the children of the element being processed, except the first one (the
     *   first <em>element</em>, not text or others).
     * </p>
     */
    public void removeAllButFirstChild();

    /**
     * <p>
     *   Instructs the engine to iterate the current element, applying a specific iteration configuration.
     * </p>
     * <p>
     *   This method specifies the name of both the iteration variable name and the <em>iterStatus</em> variable
     *   name, and also the object that should be iterated (usually a {@code Collection}, {@code Iterable} or
     *   similar).
     * </p>
     *
     * @param iterVariableName the name of the iteration variable.
     * @param iterStatusVariableName the name of the iterations status variable.
     * @param iteratedObject the object to be iterated.
     */
    public void iterateElement(final String iterVariableName, final String iterStatusVariableName, final Object iteratedObject);

}


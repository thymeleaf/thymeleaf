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
package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;

/**
 * <p>
 *   Interface to be implemented by all <em>element tag processors</em>.
 * </p>
 * <p>
 *   This kind of processors, the most common in most scenarios, are executed on the single
 *   <em>open element</em> or <em>standalone element</em> tag that matches their <em>matching configuration</em>
 *   (see {@link IElementProcessor}.
 * </p>
 * <p>
 *   <b>Reading and modifying attributes</b>
 * </p>
 * <p>
 *   The way an element tag processor should read or modify the attributes of the tag (the event) it is
 *   being executed on is by directly acting on the {@link org.thymeleaf.model.IElementAttributes} object
 *   it contains, which can be obtained by simply calling {@link IProcessableElementTag#getAttributes()}.
 *   A quick example:
 * </p>
 * <code>
 *   // First obtain the attribute value, then unescape it (attributes will come escaped!),<br>
 *   // then remove the attribute<br>
 *   final String attributeValue =<br>
 *           EscapedAttributeUtils.unescapeAttribute(<br>
 *               context.getTemplateMode(),<br>
 *               tag.getAttributes().getValue(attributeName));<br>
 *   <br>
 *   tag.getAttributes().removeAttribute(attributeName);<br>
 *   <br>
 *    ... // do something with that attributeValue<br>
 * </code>
 * <p>
 *   <b>Using the <tt>structureHandler</tt></b>
 * </p>
 * <p>
 *   For any other action involving more than mere attribute access and/or modification, processors will
 *   use the {@link IElementTagStructureHandler} objects passed as argument to the
 *   {@link #process(ITemplateContext, IProcessableElementTag, IElementTagStructureHandler)} method.
 * </p>
 * <p>
 *   See the documentation for {@link IElementTagStructureHandler} for more info.
 * </p>
 * <p>
 *   <b>Abstract implementations</b>
 * </p>
 * <p>
 *   Two basic abstract implementations of this interface are offered:
 * </p>
 * <ul>
 *   <li>{@link AbstractElementTagProcessor}, meant for processors that match element events by their element
 *       name (i.e. without looking at any attributes).</li>
 *   <li>{@link AbstractAttributeTagProcessor}, meant for processors that match element events by one of their
 *       attributes (and optionally also the element name).</li>
 * </ul>
 *
 * @author Daniel Fern&aacute;ndez
 * @see AbstractElementTagProcessor
 * @see AbstractAttributeTagProcessor
 * @see IElementTagStructureHandler
 * @since 3.0.0
 * 
 */
public interface IElementTagProcessor extends IElementProcessor {


    public void process(
            final ITemplateContext context,
            final IProcessableElementTag tag, final IElementTagStructureHandler structureHandler);


}

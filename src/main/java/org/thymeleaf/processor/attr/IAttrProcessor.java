/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.processor.attr;

import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 *   Common interface for all <i>Attribute Processors</i>.
 * </p>
 * <p>
 *   Every attribute processor must declare:
 * </p>
 * <ul>
 *   <li>Its <b>attribute applicabilities</b>: These <i>applicabilities</i>
 *       (a set of {@link AttrApplicability} objects) specify the cases
 *       in which this attribute processor would be executed. This must at least include an
 *       attribute name (<i>execute if attribute name is A</i>) and can also include tag names
 *       (<i>execute if attribute name is A and it is set in a tag with name T</i>), companion
 *       attributes with values (<i>execute if attribute name is A and it is set in a tag with
 *       name T which has an attribute A2 with value V</i>), etc.</li>
 *   <li>Its <b>precedence</b>: When a tag includes several attributes that have an <i>attribute
 *       processor</i> associated, the precedences of these processors establish the order in which
 *       they will be executed (lowest precedence value is first). This is important 
 *       because each attribute processor will be able to see the results of the previously 
 *       executed ones.</li> 
 * </ul>
 * <p>
 *   When developing new processors, in order to save code, it is recommended to extend an abstract class like
 *   {@link AbstractAttrProcessor} (or any of its subclasses) instead of directly implementing
 *   this interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface IAttrProcessor extends Comparable<IAttrProcessor> {

    /**
     * <p>
     *   Return the attribute applicabilities.
     * </p>
     * <p>
     *   These <i>applicabilities</i>
     *   (a set of {@link AttrApplicability} objects) specify the cases
     *   in which this attribute processor would be executed. This must at least include an
     *   attribute name (<i>execute if attribute name is A</i>) and can also include tag names
     *   (<i>execute if attribute name is A and it is set in a tag with name T</i>), companion
     *   attributes with values (<i>execute if attribute name is A and it is set in a tag with
     *   name T which has an attribute A2 with value V</i>), etc.
     * </p>
     * <p>
     *   Refer to the {@link AttrApplicability} API for more details.
     * </p>
     * 
     * @return the attribute applicabilities.
     */
    public Set<AttrApplicability> getAttributeApplicabilities();
    
    
    /**
     * <p>
     *   Return this processor's <i>precedence</i>.
     * </p>
     * <p>
     *   When a tag includes several attributes that have an <i>attribute
     *   processor</i> associated, the precedences of these processors establish the order in which
     *   they will be executed (lowest precedence value is first). This is important 
     *   because each attribute processor will be able to see the results of the previously 
     *   executed ones. 
     * </p>
     * 
     * @return the processor's precedence (lowest will be executed first).
     */
    public Integer getPrecedence();
    
    
    /**
     * <p>
     *   Execute this attribute processor.
     * </p>
     * <p>
     *   This execution logic will receive as parameters the <i>execution argument</i>
     *   (an {@link Arguments} object), the result of resolving the processed template
     *   by the Template Resolver (a {@link TemplateResolution} object), the complete DOM node tree
     *   of the template being processed (the {@link Document}), the tag in which 
     *   the currently executed attribute was found (the {@link Element}) and the
     *   attribute itself ({@link Attr}).
     * </p>
     * 
     * @param arguments the execution arguments, containing Template Engine configuration and
     *                  execution context.
     * @param templateResolution the result of resolving the template by the Template Resolvers.
     * @param document the DOM node tree (document) of the template being executed.
     * @param element the tag containing the currently executed attribute.
     * @param attribute the attribute being executed.
     * @return an {@link AttrProcessResult} with the result of the execution.
     */
    public AttrProcessResult process(final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, final Attr attribute);
    
}

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
package org.thymeleaf.processor.tag;

import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.TagApplicability;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 *   Common interface for all <i>Tag Processors</i>.
 * </p>
 * <p>
 *   Every tag processor must declare:
 * </p>
 * <ul>
 *   <li>Its <b>tag applicabilities</b>: These <i>applicabilities</i>
 *       (a set of {@link TagApplicability} objects) specify the cases
 *       in which this tag processor would be executed. This must at least include a
 *       tag name (<i>execute if tag name is T</i>) and can also include 
 *       attributes with values (<i>execute if tag name is T has an attribute A with value V</i>), etc.</li>
 * </ul>
 * <p>
 *   When developing new processors, in order to save code, it is recommended to extend an abstract class like
 *   {@link AbstractTagProcessor} (or any of its subclasses) instead of directly implementing
 *   this interface.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public interface ITagProcessor {
    
    /**
     * <p>
     *   Return the tag applicabilities.
     * </p>
     * <p>
     *   These <i>applicabilities</i>
     *   (a set of {@link TagApplicability} objects) specify the cases
     *   in which this tag processor would be executed. This must at least include a
     *   tag name (<i>execute if tag name is T</i>) and can also include 
     *   attributes with values (<i>execute if tag name is T has an attribute A with value V</i>), etc.
     * </p>
     * <p>
     *   Refer to the {@link TagApplicability} API for more details.
     * </p>
     * 
     * @return the tag applicabilities.
     */
    public Set<TagApplicability> getTagApplicabilities();

    
    /**
     * <p>
     *   Execute this attribute processor.
     * </p>
     * <p>
     *   This execution logic will receive as parameters the <i>execution argument</i>
     *   (an {@link Arguments} object), the result of resolving the processed template
     *   by the Template Resolver (a {@link TemplateResolution} object), the complete DOM node tree
     *   of the template being processed (the {@link Document}) and the DOM element representing
     *   the tag currently being processed ({@link Element} object).
     * </p>
     * 
     * @param arguments the execution arguments, containing Template Engine configuration and
     *                  execution context.
     * @param templateResolution the result of resolving the template by the Template Resolvers.
     * @param document the DOM node tree (document) of the template being executed.
     * @param element the DOM element representing the tag currently being processed.
     * @return a {@link TagProcessResult} with the result of the execution.
     */
    public TagProcessResult process(final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element);
    
}

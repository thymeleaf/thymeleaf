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

import org.thymeleaf.engine.ElementDefinition;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * <p>
 *   Event interface defining an element tag (open, close or standalone).
 * </p>
 * <p>
 *   Note that any implementations of this interface should be <strong>immutable</strong>.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public interface IElementTag extends ITemplateEvent {

    /**
     * <p>
     *   Returns the template mode to which this element tag is linked.
     * </p>
     * <p>
     *   Element tags are created for a specific template mode and cannot be added to {@link IModel}
     *   objects that do not match this template mode.
     * </p>
     *
     * @return the template mode.
     */
    public TemplateMode getTemplateMode();

    /**
     * <p>
     *   Returns the complete name of the element (including prefix) as a String.
     * </p>
     * <p>
     *   If this event models a tag that was actually parsed from a template, this value will represent
     *   the exact way in which the element name was written in the template.
     * </p>
     *
     * @return the element complete name.
     */
    public String getElementCompleteName();

    /**
     * <p>
     *   Returns the {@link ElementDefinition} corresponding to this tag.
     * </p>
     * <p>
     *   The element definition contains several metadata related to the element. For example, if the
     *   template mode is {@link TemplateMode#HTML}, an element definition could specify whether the
     *   element is void or not (i.e. should be expected to have a body).
     * </p>
     *
     * @return the element definition.
     */
    public ElementDefinition getElementDefinition();

    /**
     * <p>
     *   Returns whether the tag is synthetic (i.e. not originally present in a template, but rather a
     *   tag balancing artifact).
     * </p>
     *
     * @return whether the tag is synthetic or not.
     */
    public boolean isSynthetic();

}

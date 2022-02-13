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

import org.thymeleaf.processor.IProcessor;

/**
 * <p>
 *   Base interface for all processors that execute on elements (<em>tags</em>).
 * </p>
 * <p>
 *   The elements these processors execute on are defined by the <em>matching element</em> and
 *   <em>matching attribute</em>, defined by {@link #getMatchingElementName()} and {@link #getMatchingAttributeName()}.
 * </p>
 * <p>
 *   Element processors can match an element based on an element name, an attribute name, or both. But it is
 *   required that at least one option (element or attribute) be specified.
 * </p>
 * <p>
 *   Element processors should not directly implement this interface, but instead implement one of
 *   {@link IElementTagProcessor} or {@link IElementModelProcessor}.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @see IElementTagProcessor
 * @see IElementModelProcessor
 * @see MatchingElementName
 * @see MatchingAttributeName
 *
 * @since 3.0.0
 * 
 */
public interface IElementProcessor extends IProcessor {

    /**
     * <p>
     *   Returns the element name that would make this processor match (if any).
     * </p>
     *
     * @return the element name.
     */
    public MatchingElementName getMatchingElementName();

    /**
     * <p>
     *   Returns the attribute name that would make this processor match (if any).
     * </p>
     *
     * @return the attribute name.
     */
    public MatchingAttributeName getMatchingAttributeName();

}

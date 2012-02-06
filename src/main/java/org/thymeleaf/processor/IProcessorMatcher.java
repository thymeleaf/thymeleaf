/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.processor;

import org.thymeleaf.dom.Node;





/**
 * <p>
 *   Common interface for objects that specify when a processor can be applied
 *   to a node.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public interface IProcessorMatcher<T extends Node> {
    
    /**
     * <p>
     *   Try to match the node, using the specified matching context.
     * </p>
     * 
     * @param node the node to be matched
     * @param context the matching context
     * @return true if the node matches, false if not.
     */
    public boolean matches(final Node node, final ProcessorMatchingContext context);
    
    
    /**
     * <p>
     *   Returns the type of Node this matcher applies to (and therefore the type
     *   of Node that processors with this matcher will apply to).
     * </p>
     * 
     * @return the type of node (subclass of Node) this matcher applies to.
     */
    public Class<? extends T> appliesTo();
    
}

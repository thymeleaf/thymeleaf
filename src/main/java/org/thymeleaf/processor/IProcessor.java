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
package org.thymeleaf.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;

/**
 * <p>
 *   Common interface for all processors to be applied on Thymeleaf
 *   DOM trees.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public interface IProcessor extends Comparable<IProcessor> {

    /**
     * <p>
     *   Returns the matcher ({@link IProcessorMatcher}) that 
     *   defines the applicability of this processor.
     * </p>
     * 
     * @return the matcher.
     */
    public IProcessorMatcher<? extends Node> getMatcher();

    
    /**
     * <p>
     *   Processes a node. This node is supposed to have already
     *   positively matched this processor's applicability.
     * </p>
     * 
     * @param arguments the Arguments object to be applied.
     * @param processorMatchingContext the matching context.
     * @param node the node to be processed.
     * @return the processor result.
     */
    public ProcessorResult process(final Arguments arguments, final ProcessorMatchingContext processorMatchingContext, final Node node);
    
}

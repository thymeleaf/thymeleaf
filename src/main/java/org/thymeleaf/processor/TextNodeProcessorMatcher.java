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

import org.thymeleaf.dom.AbstractTextNode;
import org.thymeleaf.dom.Node;





/**
 * <p>
 *   Implementation of {@link ITextNodeProcessorMatcher} that matches every node extending
 *   from {@link AbstractTextNode}, this is, {@link org.thymeleaf.dom.Text} and 
 *   {@link org.thymeleaf.dom.CDATASection} nodes.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public final class TextNodeProcessorMatcher implements ITextNodeProcessorMatcher {
    
    
    
    public TextNodeProcessorMatcher() {
        super();
    }
    

    public boolean matches(final Node node, final ProcessorMatchingContext context) {
        return (node instanceof AbstractTextNode);
    }


    
    public Class<? extends AbstractTextNode> appliesTo() {
        return AbstractTextNode.class;
    }
    
    
}

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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractSelectionTargetAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardSelectionAttrProcessor 
        extends AbstractSelectionTargetAttrProcessor {

    
    
    

    
    public AbstractStandardSelectionAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    public AbstractStandardSelectionAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    
    
    

    @Override
    protected final Object getNewSelectionTarget(
            final Arguments arguments, final Tag tag, final String attributeName) {

        final String attributeValue = tag.getAttributeValue(attributeName);
        
        final Expression expression = 
            StandardExpressionProcessor.parseExpression(arguments, attributeValue);
        
        validateSelectionValue(arguments, tag, attributeName, attributeValue, expression);
        
        return StandardExpressionProcessor.executeExpression(arguments, expression);
        
    }

    
    
    
    @SuppressWarnings("unused")
    protected void validateSelectionValue(
            final Arguments arguments,  final Tag tag, 
            final String attributeName, final String attributeValue,
            final Expression expression) {
        // Meant for being overridden. Nothing to be done in default implementation.
    }
    
}

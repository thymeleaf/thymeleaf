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
import org.thymeleaf.processor.attr.AbstractIterationAttrProcessor;
import org.thymeleaf.standard.expression.Each;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardIterationAttrProcessor 
        extends AbstractIterationAttrProcessor {

    
    

    
    public AbstractStandardIterationAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    public AbstractStandardIterationAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    



    @Override
    protected final IterationSpec getIterationSpec(
            final Arguments arguments, final Tag tag, final String attributeName) {

        final String attributeValue = tag.getAttributeValue(attributeName);
        
        final Each each = StandardExpressionProcessor.parseEach(arguments, attributeValue);

        final Expression iterableExpression = each.getIterable();
        
        final Object iteratedObject = StandardExpressionProcessor.executeExpression(arguments, iterableExpression);
        
        if (each.hasStatusVar()) {
            return new IterationSpec(each.getIterVar().getValue(), each.getStatusVar().getValue(), iteratedObject);
        }
        return new IterationSpec(each.getIterVar().getValue(), null, iteratedObject);
        
    }



    
    
}

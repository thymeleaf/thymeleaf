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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Tag;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {
    
    
    

    
    public AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    public AbstractStandardSingleValueMultipleAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    




    @Override
    protected final Map<String, String> getModifiedAttributeValues(
            final Arguments arguments, final Tag tag, final String attributeName) {

        final String attributeValue = tag.getAttributeValue(attributeName);
        
        final Expression expression =
            StandardExpressionProcessor.parseExpression(arguments, attributeValue);
        
        final Set<String> newAttributeNames = 
                getModifiedAttributeNames(arguments, tag, attributeName, attributeValue, expression);

        final Object valueForAttributes = 
            StandardExpressionProcessor.executeExpression(arguments, expression);
        
        final Map<String,String> result = new LinkedHashMap<String,String>();
        for (final String newAttributeName : newAttributeNames) {
            result.put(newAttributeName, (valueForAttributes == null? "" : valueForAttributes.toString()));
        }
        
        return result;
        
    }


    protected abstract Set<String> getModifiedAttributeNames(final Arguments arguments,
            final Tag tag, final String attributeName, final String attributeValue, final Expression expression);





    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Tag tag, final String attributeName) {
        return false;
    }


    
}

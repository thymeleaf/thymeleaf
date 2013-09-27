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
package org.thymeleaf.standard.processor.attr;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractConditionalVisibilityAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.ObjectUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardConditionalVisibilityAttrProcessor 
        extends AbstractConditionalVisibilityAttrProcessor {




    protected AbstractStandardConditionalVisibilityAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardConditionalVisibilityAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    


    @Override
    protected final boolean isVisible(
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        
        final Object value = StandardExpressions.processExpression(arguments, attributeValue);

        final boolean visible = ObjectUtils.evaluateAsBoolean(value);
        
        if (inverse()) {
            return !visible;
        }

        return visible;
        
    }
    
    
    protected abstract boolean inverse();
    

    
}

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
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractSelectionTargetAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

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
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);
        
        validateSelectionValue(arguments, element, attributeName, attributeValue, expression);
        
        return expression.execute(configuration, arguments);
        
    }

    
    
    
    @SuppressWarnings("unused")
    protected void validateSelectionValue(
            final Arguments arguments,  final Element element, 
            final String attributeName, final String attributeValue,
            final IStandardExpression expression) {
        // Meant for being overridden. Nothing to be done in default implementation.
    }
    
}

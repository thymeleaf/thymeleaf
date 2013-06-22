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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.Standards;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.ObjectUtils;
import org.thymeleaf.util.TemplateModeUtils;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public abstract class AbstractStandardAttributeModifierAttrProcessor 
        extends AbstractAttributeModifierAttrProcessor {
    

    
    public AbstractStandardAttributeModifierAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    
    
    @Override
    protected final Map<String,String> getModifiedAttributeValues(
            final Arguments arguments, final Element element, final String attributeName) {
        
        
        final String attributeValue = element.getAttributeValue(attributeName);
        final AssignationSequence assignations = 
            StandardExpressionProcessor.parseAssignationSequence(
                    arguments, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }
        
        final Map<String,String> newAttributeValues = new HashMap<String,String>(assignations.size() + 1, 1.0f);
        
        for (final Assignation assignation : assignations) {
            
            final String newAttributeName = assignation.getLeft().getValue();
            final Expression expression = assignation.getRight();
            
            final Object result = StandardExpressionProcessor.executeExpression(arguments, expression);
            
            if (TemplateModeUtils.isHtml(arguments.getTemplateResolution().getTemplateMode()) &&
                    ArrayUtils.contains(Standards.HTML_CONDITIONAL_FIXED_VALUE_ATTR_NAMES, newAttributeName)) {
                // Attribute is a fixed-value conditional one, like "selected", which can only
                // appear as selected="selected" or not appear at all.
                
                if (ObjectUtils.evaluateAsBoolean(result)) {
                    newAttributeValues.put(newAttributeName, newAttributeName);
                } else {
                    newAttributeValues.put(newAttributeName, null);
                }
                
            } else {
                // Attribute is a "normal" attribute, not a fixed-value conditional one
                
                newAttributeValues.put(newAttributeName, (result == null? "" : result.toString()));
                
            }

        }
        
        return Collections.unmodifiableMap(newAttributeValues);
        
    }

    

    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Element element, final String attributeName) {
        return false;
    }

    
}

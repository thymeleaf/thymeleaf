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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractConditionalVisibilityAttrProcessor;
import org.thymeleaf.standard.expression.EqualsExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardConversionUtil;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.attr.AbstractStandardSwitchStructureAttrProcessor.SwitchStructure;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractStandardCaseAttrProcessor 
        extends AbstractConditionalVisibilityAttrProcessor {
    
    public static final String CASE_DEFAULT_ATTRIBUTE_VALUE = "*";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    protected AbstractStandardCaseAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AbstractStandardCaseAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    

    

    @Override
    protected boolean isVisible(final Arguments arguments, final Element element, final String attributeName) {
        
        if (!arguments.hasLocalVariable(AbstractStandardSwitchStructureAttrProcessor.SWITCH_VARIABLE_NAME)) {
            throw new TemplateProcessingException(
                    "Cannot specify a \"" + attributeName + "\" attribute in an environment where no " +
                    "switch operator has been defined before.");
        }
        
        final SwitchStructure switchStructure = 
                (SwitchStructure) arguments.getLocalVariable(AbstractStandardSwitchStructureAttrProcessor.SWITCH_VARIABLE_NAME);

        if (switchStructure.isExecuted()) {
            return false;
        }
        

        final String attributeValue = element.getAttributeValue(attributeName);
        
        if (attributeValue != null && attributeValue.trim().equals(CASE_DEFAULT_ATTRIBUTE_VALUE)) {
            
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"",
                        new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(), attributeValue, attributeName, attributeValue, Boolean.TRUE});
            }
            
            switchStructure.setExecuted(true);
            return true;
            
        }

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression caseExpression = expressionParser.parseExpression(configuration, arguments, attributeValue);
        
        final EqualsExpression equalsExpression = new EqualsExpression(switchStructure.getExpression(), caseExpression); 

        final Object value = equalsExpression.execute(configuration, arguments);

        final boolean visible = StandardConversionUtil.convert(configuration, value, boolean.class);
        
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(), attributeValue, attributeName, attributeValue, Boolean.valueOf(visible)});
        }

        if (visible) {
            switchStructure.setExecuted(true);
        }
        
        return visible;
        
    }
    

    
    
}

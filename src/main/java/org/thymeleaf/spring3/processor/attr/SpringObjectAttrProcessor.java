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
package org.thymeleaf.spring3.processor.attr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.processor.attr.AbstractStandardSelectionAttrProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class SpringObjectAttrProcessor 
        extends AbstractStandardSelectionAttrProcessor {

    
    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(500);
    public static final String ATTR_NAME = "object";

    
    
    
    public SpringObjectAttrProcessor() {
        super();
    }

    
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName(ATTR_NAME);
    }

    public Integer getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    
    

    @Override
    protected void validateSelectionValue(final Arguments arguments,
            final TemplateResolution templateResolution, final Document document,
            final Element element, final Attr attribute, final String attributeName,
            final String attributeValue, final Expression expression) {
        
        if ("FORM".equals(element.getTagName().toUpperCase())) {
            
            if (expression != null && expression instanceof VariableExpression) {

                final String varExp = ((VariableExpression)expression).getExpression();
                if (varExp.indexOf('.') >= 0 || varExp.indexOf('[') >= 0 || varExp.indexOf(']') >= 0) {
                    throw new AttrProcessorException(
                            "The expression used as a form " +
                            "model is " + expression + ", which is not allowed by " +
                            "Spring MVC. Target selection expressions used for forms in Spring have to be " +
                            "just context variable names. Nested calls (like \"user.name\") and " +
                            "indexed accesses (like \"user['name']\") are forbidden.");
                }
                return;
                
            }
            
            throw new TemplateProcessingException(
                    "The expression used as a form " +
                    "model is " + expression + ", which is not valid: " +
                    "only variable expressions are allowed as Spring " +
                    "MVC form model objects.");
            
        }

        /*
         * Check we are not already inside a form (no th:object attrs allowed there!)
         */
        final VariableExpression formCommandValue = 
            (VariableExpression) arguments.getLocalVariable(SpringContextVariableNames.SPRING_FORM_COMMAND_VALUE);
        
        if (formCommandValue != null) {
            throw new TemplateProcessingException(
                    "A selection expression " + expression + " has been specified inside a Spring MVC " +
                    "form, but this is not allowed. Selection expressions cannot be nested inside forms.");
        }
        
    }

    
    
    

    @Override
    protected Map<String, Object> getAdditionalLocalVariables(
            final Arguments arguments, final TemplateResolution templateResolution,
            final Document document, final Element element, final Attr attribute,
            final String attributeName, final String attributeValue) {

        final Map<String,Object> additionalLocalVariables = new HashMap<String, Object>();
        additionalLocalVariables.putAll(
                super.getAdditionalLocalVariables(
                        arguments, templateResolution, document, element, attribute, attributeName, attributeValue));
        
        if ("FORM".equals(element.getTagName().toUpperCase())) {

            final VariableExpression varExpression =
                (VariableExpression) StandardExpressionProcessor.parseExpression(arguments, templateResolution, attribute.getValue());
            additionalLocalVariables.put(SpringContextVariableNames.SPRING_FORM_COMMAND_VALUE, varExpression);
            
        }
        
        return additionalLocalVariables;
        
    }


    

}

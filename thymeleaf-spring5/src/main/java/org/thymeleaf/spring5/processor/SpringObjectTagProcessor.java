/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring5.processor;

import java.util.Collections;
import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.processor.AbstractStandardTargetSelectionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;


/**
 * Specifies an object to use on a &lt;form&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.3
 */
public final class SpringObjectTagProcessor extends AbstractStandardTargetSelectionTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 500;
    public static final String ATTR_NAME = "object";
    
    
    
    public SpringObjectTagProcessor(final String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, ATTR_PRECEDENCE);
    }


    


    

    @Override
    protected void validateSelectionValue(final ITemplateContext context,
                                          final IProcessableElementTag tag,
                                          final AttributeName attributeName, final String attributeValue,
                                          final IStandardExpression expression) {

        if (expression == null || !(expression instanceof VariableExpression)) {

            throw new TemplateProcessingException(
                    "The expression used for object selection is " + expression + ", which is not valid: " +
                    "only variable expressions (${...}) are allowed in '" +  attributeName + "' attributes in " +
                    "Spring-enabled environments.");

        }


    }

    
    
    

    @Override
    protected Map<String, Object> computeAdditionalLocalVariables(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IStandardExpression expression) {

        // We set the (parsed) expression itself as a local variable because we might use it at the expression evaluator
        return Collections.singletonMap(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION, (Object)expression);
        
    }


    

}

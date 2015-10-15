/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.spring4.processor;

import java.util.Collections;
import java.util.Map;

<<<<<<< HEAD
=======
import org.thymeleaf.IEngineConfiguration;
>>>>>>> 1b08adb4a3731da6645541808b99ed79cda36c40
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.processor.AbstractStandardTargetSelectionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;


/**
 * Specifies an object to use on a &lt;form&gt;
 * 
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 */
public final class SpringObjectTagProcessor extends AbstractStandardTargetSelectionTagProcessor {

    
    public static final int ATTR_PRECEDENCE = 500;
    public static final String ATTR_NAME = "object";
    
    
    
    public SpringObjectTagProcessor(final IProcessorDialect dialect, final String dialectPrefix) {
        super(dialect, TemplateMode.HTML, dialectPrefix, ATTR_NAME, ATTR_PRECEDENCE);
    }


    


    

    @Override
<<<<<<< HEAD
    protected void validateSelectionValue(final ITemplateContext context,
=======
    protected void validateSelectionValue(final IEngineConfiguration configuration,
                                          final ITemplateContext context,
>>>>>>> 1b08adb4a3731da6645541808b99ed79cda36c40
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

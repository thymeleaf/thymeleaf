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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import ognl.Ognl;
import ognl.OgnlException;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.expression.ExpressionEvaluatorObjects;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.util.OrderedProperties;



public class DefaultContextStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    
    public static final DefaultContextStandardTestFieldEvaluator INSTANCE = 
            new DefaultContextStandardTestFieldEvaluator();
    
    public static final String LOCALE_PROPERTY_NAME = "locale";
    
    
    
    private DefaultContextStandardTestFieldEvaluator() {
        super(IContext.class);
    }



    @Override
    protected StandardTestEvaluatedField getValue(final String executionId, final String documentName, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {
        
        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forDefaultValue(new Context());
        }

        final Properties valueAsProperties = new OrderedProperties();

        try {
            
            /*
             * This String -> byte[] conversion is needed because java.util.Properties 
             * did not allow using a java.io.Reader for loading properties until Java 6.
             */
            final byte[] valueAsBytes = fieldValue.getBytes("ISO-8859-1");
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(valueAsBytes);

            valueAsProperties.load(inputStream);
            
        } catch (final Throwable t) {
            throw new TestEngineExecutionException( 
                    "Error while reading context specification", t);
        }
        
        final Locale locale = 
                (valueAsProperties.containsKey(LOCALE_PROPERTY_NAME)? 
                        new Locale(valueAsProperties.getProperty(LOCALE_PROPERTY_NAME)) : Locale.US);
        
        
        final Map<String,Object> contextVariables = new HashMap<String, Object>();
        final Map<String,Object> expressionUtilityObjects =
                ExpressionEvaluatorObjects.getExpressionEvaluationUtilityObjectsForLocale(locale);
        if (expressionUtilityObjects != null) {
            contextVariables.putAll(expressionUtilityObjects);
        }
        
        
        final Context ctx = new Context(locale);

        for (final Map.Entry<?,?> entry : valueAsProperties.entrySet()) {
            
            final String varName = (String)entry.getKey();
            final String varValue = (String)entry.getValue();
            
            final Object varObjectValue;
            if (varValue != null && varValue.trim().startsWith("${") && varValue.trim().endsWith("}")) {
                // value is an expression
                varObjectValue = evaluateAsOgnlExpression(varValue, contextVariables, ctx.getVariables());
            } else {
                varObjectValue = varValue;
            }
            
            ctx.setVariable(varName, varObjectValue);
            
        }

        return StandardTestEvaluatedField.forSpecifiedValue(ctx);
        
    }
    
    
    

    
    private static final Object evaluateAsOgnlExpression(final String varValue, 
            final Map<String,Object> contextVariables, final Object evaluationRoot) {
        
        final String varExpressionStr = varValue.trim().substring(2, varValue.length() - 1);

        try {
            
            final Object varExpression = Ognl.parseExpression(varExpressionStr);
            
            return Ognl.getValue(varExpression, contextVariables, evaluationRoot);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + varExpressionStr + "\"", e);
        }
        
    }
    
   
    
}

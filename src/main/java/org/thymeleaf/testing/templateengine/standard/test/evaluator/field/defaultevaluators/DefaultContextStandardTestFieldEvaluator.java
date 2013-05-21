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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.context.ITestContextExpression;
import org.thymeleaf.testing.templateengine.context.OgnlTestContextExpression;
import org.thymeleaf.testing.templateengine.context.TestContext;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceResolver;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;
import org.thymeleaf.testing.templateengine.util.MultiValueProperties;


public class DefaultContextStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    private static final String VAR_NAME_PREFIX_PARAM = "param.";
    private static final String VAR_NAME_PREFIX_REQUEST = "request.";
    private static final String VAR_NAME_PREFIX_SESSION = "session.";
    private static final String VAR_NAME_PREFIX_APPLICATION = "application.";
    
    
    
    public static final DefaultContextStandardTestFieldEvaluator INSTANCE = 
            new DefaultContextStandardTestFieldEvaluator();
    
    public static final String LOCALE_PROPERTY_NAME = "locale";
    
    
    
    private DefaultContextStandardTestFieldEvaluator() {
        super(ITestContext.class);
    }



    @Override
    protected StandardTestEvaluatedField getValue(final String executionId, final ITestResource resource, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {
        
        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forDefaultValue(new TestContext());
        }

        final String resourceName = (resource != null? resource.getName() : null);
        
        final MultiValueProperties properties = new MultiValueProperties();

        try {
            
            /*
             * This String -> byte[] conversion is needed because java.util.Properties 
             * did not allow using a java.io.Reader for loading properties until Java 6.
             */
            final byte[] valueAsBytes = fieldValue.getBytes("ISO-8859-1");
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(valueAsBytes);

            properties.load(inputStream);
            
        } catch (final Throwable t) {
            throw new TestEngineExecutionException( 
                    "Error while reading context specification", t);
        }
        
        
        final TestContext testContext = new TestContext();
        
        for (final Map.Entry<String,List<String>> entry : properties.entrySet()) {
            
            final String varName = entry.getKey();
            final List<String> varValue = entry.getValue();

            if (varName.equalsIgnoreCase(LOCALE_PROPERTY_NAME)) {
                checkForbiddenMultiValue(resourceName, varName, varValue);
                testContext.setLocale(new Locale(varValue.get(0)));
                continue;
            }
            
            if (varName.startsWith(VAR_NAME_PREFIX_PARAM)) {
                final int valueLen = varValue.size();
                final ITestContextExpression[] expressions = new ITestContextExpression[valueLen];
                for (int i = 0; i < valueLen; i++) {
                    expressions[i] = new OgnlTestContextExpression(varValue.get(i));
                }
                testContext.getRequestParameters().put(varName.substring(VAR_NAME_PREFIX_PARAM.length()), expressions);
                continue;
            }
            
            if (varName.startsWith(VAR_NAME_PREFIX_REQUEST)) {
                checkForbiddenMultiValue(resourceName, varName, varValue);
                testContext.getRequestAttributes().put(varName.substring(VAR_NAME_PREFIX_REQUEST.length()), new OgnlTestContextExpression(varValue.get(0)));
                continue;
            }
            
            if (varName.startsWith(VAR_NAME_PREFIX_SESSION)) {
                checkForbiddenMultiValue(resourceName, varName, varValue);
                testContext.getSessionAttributes().put(varName.substring(VAR_NAME_PREFIX_SESSION.length()), new OgnlTestContextExpression(varValue.get(0)));
                continue;
            }
            
            if (varName.startsWith(VAR_NAME_PREFIX_APPLICATION)) {
                checkForbiddenMultiValue(resourceName, varName, varValue);
                testContext.getServletContextAttributes().put(varName.substring(VAR_NAME_PREFIX_APPLICATION.length()), new OgnlTestContextExpression(varValue.get(0)));
                continue;
            }

            checkForbiddenMultiValue(resourceName, varName, varValue);
            testContext.getVariables().put(varName, new OgnlTestContextExpression(varValue.get(0)));
            
        }

        
        return StandardTestEvaluatedField.forSpecifiedValue(testContext);
        
    }
    
    
    
    private static void checkForbiddenMultiValue(final String resourceName, final String varName, final List<String> varValue) {
        if (varValue.size() > 1) {
            throw new TestEngineExecutionException(
                    "Variable \"" + varName + "\" in context for test \"" + resourceName + "\" " +
            		"cannot be multi-valued");
        }
    }
    
    
    
    
    
    
}

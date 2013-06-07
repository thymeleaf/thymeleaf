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
package org.thymeleaf.tests.springintegration.context;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.testable.ITest;




public class ErrorsSpringIntegrationWebProcessingContextBuilder 
        extends SpringIntegrationWebProcessingContextBuilder {

    public static String BINDING_ERRORS_CONTEXT_VARIABLE_NAME = "bindingErrors";
    public static String BINDING_ERRORS_OBJECT_BINDING_NAME = "binding";
    public static String BINDING_ERRORS_OBJECT_BINDING_FIELD_NAME = "field";
    public static String BINDING_ERRORS_OBJECT_BINDING_MESSAGE_NAME = "message";

    
    public ErrorsSpringIntegrationWebProcessingContextBuilder() {
        super();
    }

    
    
    @Override
    protected void initSpring(final ApplicationContext applicationContext,
            final ITest test, final HttpServletRequest request,
            final HttpServletResponse response, final ServletContext servletContext,
            final Locale locale, final Map<String, Object> variables) {

        super.initSpring(applicationContext, test, request, response, servletContext,
                locale, variables);

        final List<String> bindingVariableNames = 
                getBindingVariableNames(test, request, response, servletContext, locale, variables);
        for (final String bindingVariableName : bindingVariableNames) {
                
            final String bindingResultName = BindingResult.MODEL_KEY_PREFIX + bindingVariableName;
            final BindingResult result = (BindingResult) variables.get(bindingResultName);

            @SuppressWarnings("unchecked")
            final List<Map<String,Object>> bindingErrorsList = 
                    (List<Map<String,Object>>) variables.get(BINDING_ERRORS_CONTEXT_VARIABLE_NAME);

            if (bindingErrorsList != null) {
                
                for (final Map<String,Object> bindingErrors : bindingErrorsList) {
                    
                    final Object bindingObj = bindingErrors.get(BINDING_ERRORS_OBJECT_BINDING_NAME);
                    if (bindingObj != null) {
                        if (bindingObj.toString().equals(bindingVariableName)) {
                            // This error map applies to this binding variable

                            final Object fieldObj = bindingErrors.get(BINDING_ERRORS_OBJECT_BINDING_FIELD_NAME); 
                            if (fieldObj == null) {
                                throw new TestEngineExecutionException(
                                        "Error specification does not include property 'field', which is mandatory");
                            }
                            
                            final Object messageObj = bindingErrors.get(BINDING_ERRORS_OBJECT_BINDING_MESSAGE_NAME); 
                            if (messageObj == null) {
                                throw new TestEngineExecutionException(
                                        "Error specification does not include property 'message', which is mandatory");
                            }
                            
                            final FieldError fieldError =
                                    new FieldError(bindingVariableName, fieldObj.toString(), messageObj.toString());
                            
                            result.addError(fieldError);
                            
                        }
                    }
                    
                }

            }
            
        }
        
    }

    
    
    
}

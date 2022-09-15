/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateengine.springintegration.context;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.validation.BindingResult;
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
    protected void initBindingResult(final String bindingVariableName,
            final Object bindingObject, final ITest test, final BindingResult bindingResult,
            final Locale locale, final Map<String,Object> variables) {

        super.initBindingResult(bindingVariableName, bindingObject, test,
                bindingResult, locale, variables);
        
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

                        final Object messageObj = bindingErrors.get(BINDING_ERRORS_OBJECT_BINDING_MESSAGE_NAME); 
                        if (messageObj == null) {
                            throw new TestEngineExecutionException(
                                    "Error specification does not include property 'message', which is mandatory");
                        }

                        if (fieldObj != null) {
                            // Field error
                            bindingResult.rejectValue(fieldObj.toString(), "no_code", messageObj.toString());
                        } else {
                            // Global error
                            bindingResult.reject("no_code",messageObj.toString());
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
    }

    
}

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
package org.thymeleaf.testing.templateengine.standard.test.evaluator.field.defaultevaluators;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.messages.ITestMessagesForLocale;
import org.thymeleaf.testing.templateengine.messages.TestMessagesForLocale;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.resource.ITestResourceResolver;
import org.thymeleaf.testing.templateengine.standard.test.data.StandardTestEvaluatedField;


public class DefaultMessagesStandardTestFieldEvaluator extends AbstractStandardTestFieldEvaluator {

    
    public static final DefaultMessagesStandardTestFieldEvaluator INSTANCE = 
            new DefaultMessagesStandardTestFieldEvaluator();
    
    
    
    private DefaultMessagesStandardTestFieldEvaluator() {
        super(ITestMessagesForLocale.class);
    }



    @Override
    protected StandardTestEvaluatedField getValue(final String executionId, final ITestResource resource, 
            final ITestResourceResolver testResourceResolver, 
            final String fieldName, final String fieldQualifier, final String fieldValue) {
        
        if (fieldValue == null || fieldValue.trim().equals("")) {
            return StandardTestEvaluatedField.forDefaultValue(new TestMessagesForLocale());
        }

        final Properties properties = new Properties();

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
        
        
        final Map<String,String> readMessages = new HashMap<String,String>();
        for (final Map.Entry<?,?> entry : properties.entrySet()) {
            final String messageKey = (String) entry.getKey();
            final String messageValue = (String) entry.getValue();
            readMessages.put(messageKey, messageValue);
        }

        final TestMessagesForLocale testMessagesForLocale = new TestMessagesForLocale();
        testMessagesForLocale.setMessagesForLocale(readMessages);
        
        return StandardTestEvaluatedField.forSpecifiedValue(testMessagesForLocale);
        
    }
    
    
    
    
}

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
package org.thymeleaf.testing.templateengine.standard.config.test;

import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;



public class DefaultContextStandardDirectiveResolver extends AbstractStandardDirectiveResolver<IContext> {

    
    public static final DefaultContextStandardDirectiveResolver INSTANCE = new DefaultContextStandardDirectiveResolver();
    
    public static final String LOCALE_PROPERTY_NAME = "locale";
    
    
    
    private DefaultContextStandardDirectiveResolver() {
        super(IContext.class);
    }



    @Override
    protected IContext getValue(final String executionId, final String documentName, 
            final String directiveName, final String directiveQualifier, final String directiveValue) {
        
        if (directiveValue == null || directiveValue.trim().equals("")) {
            return new Context();
        }

        final Properties valueAsProperties = new Properties();

        try {
            
            /*
             * This String -> byte[] conversion is needed because java.util.Properties 
             * did not allow using a java.io.Reader for loading properties until Java 6.
             */
            final byte[] valueAsBytes = directiveValue.getBytes("ISO-8859-1");
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(valueAsBytes);

            valueAsProperties.load(inputStream);
            
        } catch (final Throwable t) {
            throw new TestEngineExecutionException(executionId, 
                    "Error while reading context specification", t);
        }
        
        final Locale locale = 
                (valueAsProperties.containsKey(LOCALE_PROPERTY_NAME)? 
                        new Locale(valueAsProperties.getProperty(LOCALE_PROPERTY_NAME)) : Locale.US);
        
        final Context ctx = new Context(locale);
        
        for (final Map.Entry<?,?> entry : valueAsProperties.entrySet()) {
            ctx.setVariable((String)entry.getKey(), (String)entry.getValue());
        }

        return ctx;
        
    }
    
   
    
}

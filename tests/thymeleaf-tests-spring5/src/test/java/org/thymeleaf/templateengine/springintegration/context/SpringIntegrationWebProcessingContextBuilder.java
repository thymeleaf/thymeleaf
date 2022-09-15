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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.DataBinder;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;
import org.thymeleaf.testing.templateengine.spring5.context.web.SpringMVCWebProcessingContextBuilder;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.Validate;


public class SpringIntegrationWebProcessingContextBuilder extends SpringMVCWebProcessingContextBuilder {


    
    public SpringIntegrationWebProcessingContextBuilder() {
        this("classpath:templateengine/springintegration/applicationContext.xml");
    }


    public SpringIntegrationWebProcessingContextBuilder(final String applicationContextLocation) {
        super();
        Validate.notNull(applicationContextLocation, "Application context location cannot be null");
        setApplicationContextConfigLocation(applicationContextLocation);
    }

    
    @Override
    protected void initBinder(
            final String bindingVariableName, final Object bindingObject,
            final ITest test, final DataBinder dataBinder, final Locale locale, 
            final Map<String,Object> variables) {
        
        final ITestMessages messages = test.getMessages();
        if (messages == null) {
            throw new TestEngineExecutionException(
                    "Test \"" + test.getName() + "\" returns no messages object.");
        }
        
        final String dateformat = messages.computeMessage(locale, "date.format", null);
        final SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        sdf.setLenient(false);
        dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, false));
        
    }
    
    
    
}

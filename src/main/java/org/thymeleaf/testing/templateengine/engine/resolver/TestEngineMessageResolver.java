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
package org.thymeleaf.testing.templateengine.engine.resolver;

import java.util.Locale;

import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.MessageResolutionUtils;
import org.thymeleaf.util.Validate;







public class TestEngineMessageResolver extends AbstractMessageResolver {
    
                   
    public TestEngineMessageResolver() {
        super();
    }

    
    
    public MessageResolution resolveMessage(
            final Arguments arguments, final String key, final Object[] messageParameters) {

        checkInitialized();
        
        Validate.notNull(arguments, "Arguments cannot be null");
        Validate.notNull(key, "Key cannot be null");

        final ITest test = TestExecutor.getThreadTest(); 

        final Locale locale = arguments.getContext().getLocale();
        final ITestMessages messages = test.getMessages();
        if (messages == null) {
            throw new TestEngineExecutionException(
                    "Cannot resolve messages for test \"" + TestExecutor.getThreadTestName() + "\", test " +
                    "messages object is null.");
        }

        String message = messages.computeMessage(locale, key, messageParameters);
        
        if (message == null) {
            message = MessageResolutionUtils.getAbsentMessageRepresentation(key, locale);
        }
        
        return new MessageResolution(message);
        
    }
    
    
    
}

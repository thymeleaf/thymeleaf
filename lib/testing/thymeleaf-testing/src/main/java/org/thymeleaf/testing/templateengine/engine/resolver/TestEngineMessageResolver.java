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
package org.thymeleaf.testing.templateengine.engine.resolver;

import java.util.Locale;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.testing.templateengine.messages.ITestMessages;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.util.Validate;


public class TestEngineMessageResolver extends AbstractMessageResolver {

    private final StandardMessageResolver standardMessageResolver;


    public TestEngineMessageResolver() {
        super();
        this.standardMessageResolver = new StandardMessageResolver();
    }



    public String resolveMessage(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {

        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");

        final Locale locale = context.getLocale();

        String message = null;

        /*
         * FIRST STEP: Look for the message using template-based resolution
         */
        final ITest test = TestExecutor.getThreadTest();
        final ITestMessages messages = test.getMessages();
        if (messages == null) {
            throw new TestEngineExecutionException(
                    "Cannot resolve messages for test \"" + TestExecutor.getThreadTestName() + "\", test " +
                            "messages object is null.");
        }

        message = messages.computeMessage(locale, key, messageParameters);

        if (message != null) {
            return message;
        }

        /*
         * SECOND STEP: Look for the message using origin-based resolution, delegated to the StandardMessageResolver
         */
        if (origin != null) {
            // We will be using context == null when delegating so that only origin-based resolution is performed
            message = this.standardMessageResolver.resolveMessage(null, origin, key, messageParameters);
            if (message != null) {
                return message;
            }
        }


        /*
         * NOT FOUND, return null
         */
        return null;

    }





    public String createAbsentMessageRepresentation(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        Validate.notNull(key, "Message key cannot be null");
        if (context.getLocale() != null) {
            return "??"+key+"_" + context.getLocale().toString() + "??";
        }
        return "??"+key+"_" + "??";
    }

}

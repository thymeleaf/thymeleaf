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
package org.thymeleaf.standard.expression;

import java.text.MessageFormat;
import java.util.Properties;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1
 *
 */
public class TestMessageResolver implements IMessageResolver {

    private final Properties properties;
    
    
    public TestMessageResolver(final Properties properties) {
        super();
        this.properties = properties;
    }
    
    
    public String getName() {
        return "TEST MESSAGE RESOLVER";
    }

    public Integer getOrder() {
        return Integer.valueOf(1);
    }



    public String resolveMessage(final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {

        final String messageValue = this.properties.getProperty(key);
        if (messageValue == null) {
            return null;
        }
        if (messageParameters == null || messageParameters.length == 0) {
            return messageValue;
        }

        final MessageFormat messageFormat = new MessageFormat(messageValue, context.getLocale());
        return messageFormat.format(messageParameters);

    }



    public String createAbsentMessageRepresentation(final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        return "??" + key + "_" + context.getLocale() +"??";
    }


    
    
}

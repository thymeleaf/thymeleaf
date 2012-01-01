/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.messageresolver;

import java.text.MessageFormat;
import java.util.Properties;

import org.thymeleaf.Arguments;


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

    
    public MessageResolution resolveMessage(Arguments arguments, String key, Object[] messageParameters) {

        
        final String messageValue = this.properties.getProperty(key);
        if (messageValue == null) {
            return null;
        }
        if (messageParameters == null || messageParameters.length == 0) {
            return new MessageResolution(messageValue);
        }
        
        final MessageFormat messageFormat = new MessageFormat(messageValue, arguments.getContext().getLocale());
        return new MessageResolution(messageFormat.format(messageParameters));
        
    }
    

    public void initialize() {
        // Nothing to initialize
    }
    

    
    
}

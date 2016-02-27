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
package org.thymeleaf.testing.templateengine.messages;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.util.Validate;


public class TestMessagesForLocale implements ITestMessagesForLocale {

    private final Map<String,String> messagesForLocale = new HashMap<String,String>();
    
    
    public TestMessagesForLocale() {
        super();
    }
    
    
    public void setMessagesForLocale(final Map<String,String> messages) {
        Validate.notNull(messages, "Messages cannot be null");
        this.messagesForLocale.clear();
        this.messagesForLocale.putAll(messages);
    }

    
    
    public String getMessageForKey(final String key) {
        return this.messagesForLocale.get(key);
    }

    
    
    
    public ITestMessagesForLocale aggregate(final ITestMessagesForLocale messages) {

        final TestMessagesForLocale newMessages = new TestMessagesForLocale();
        newMessages.setMessagesForLocale(this.messagesForLocale);

        if (messages == null) {
            return newMessages;
        }

        
        if (!(messages instanceof TestMessagesForLocale)) {
            throw new IllegalArgumentException(
                    "Can only aggregate " + TestMessagesForLocale.class.getName() + " objects, but " +
                    "specified messagesForLocale object is of class " + messages.getClass().getName());
        }
        
        newMessages.messagesForLocale.putAll(((TestMessagesForLocale)messages).messagesForLocale);
        
        return newMessages;
        
    }


    
    
}

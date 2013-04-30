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
package org.thymeleaf.testing.templateengine.messages;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.util.Validate;


public class TestMessages implements ITestMessages {

    private final Map<Locale,ITestMessagesForLocale> messagesByLocale = new HashMap<Locale,ITestMessagesForLocale>();
    
    
    public TestMessages() {
        super();
    }
    
    


    public Set<Locale> getLocales() {
        return this.messagesByLocale.keySet();
    }


    public ITestMessagesForLocale getMessagesForLocale(final Locale locale) {
        return this.messagesByLocale.get(locale);
    }
    
    
    public void setMessagesByLocale(final Map<Locale,ITestMessagesForLocale> messages) {
        Validate.notNull(messages, "Messages cannot be null");
        this.messagesByLocale.clear();
        this.messagesByLocale.putAll(messages);
    }

    
    public void setMessagesForLocale(final Locale locale, final ITestMessagesForLocale messagesForLocale) {
        Validate.notNull(messagesForLocale, "Messages for locale cannot be null");
        this.messagesByLocale.put(locale, messagesForLocale);
    }

    
    
    
    
    public String computeMessage(final Locale locale, final String key, final Object[] messageParameters) {
        
        Validate.notNull(key, "Message key cannot be null");
        
        final ITestMessagesForLocale messagesForLocale = this.messagesByLocale.get(locale);
        if (messagesForLocale == null) {
            return null;
        }
        
        final String messageValue = messagesForLocale.getMessageForKey(key);

        if (messageValue == null) {
            return null;
        }
        
        if (messageParameters == null || messageParameters.length == 0) {
            return messageValue;
        }

        final MessageFormat messageFormat = new MessageFormat(messageValue, locale);
        return messageFormat.format(messageParameters);
        
    }
    
    
    
    
    
    
    public ITestMessages aggregate(final ITestMessages messages) {
        
        final TestMessages newMessages = new TestMessages();
        
        newMessages.setMessagesByLocale(this.messagesByLocale);
     
        if (messages != null) {
            
            final Set<Locale> locales = messages.getLocales();
            for (final Locale locale : locales) {
                final ITestMessagesForLocale messagesForLocale = messages.getMessagesForLocale(locale);
                if (messagesForLocale != null) {
                    ITestMessagesForLocale originalMessagesForLocale = newMessages.messagesByLocale.get(locale);
                    if (originalMessagesForLocale == null) {
                        originalMessagesForLocale = new TestMessagesForLocale();
                        newMessages.messagesByLocale.put(locale,originalMessagesForLocale);
                    }
                    newMessages.messagesByLocale.put(locale, originalMessagesForLocale.aggregate(messagesForLocale));
                }
            }
            
        }
        
        return newMessages;
        
    }


    
    
}

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
package org.thymeleaf.testing.templateengine.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.testing.templateengine.util.UnmodifiableProperties;
import org.thymeleaf.util.Validate;




public class TestSuite implements ITestSuite {

    public static final List<IDialect> DEFAULT_DIALECTS = 
            Collections.singletonList((IDialect)new StandardDialect());
    public static final Map<Locale,Properties> DEFAULT_MESSAGES = 
            Collections.singletonMap((Locale)null, (Properties)new UnmodifiableProperties());
    
    
    private List<IDialect> dialects = DEFAULT_DIALECTS;
    private Map<Locale,Properties> messages = DEFAULT_MESSAGES;
    private ITestReporter reporter = null;
    
    private final String name;
    private final ITestSequence sequence;
    

    
    
    
    
    public TestSuite(final String name, final ITestSequence sequence) {
        super();
        Validate.notNull(name, "Name cannot be null");
        Validate.notNull(sequence, "Sequence cannot be null");
        this.name = name;
        this.sequence = sequence;
    }
    

    public TestSuite(final String name, final ITestable... testables) {
        
        super();
        
        Validate.notNull(name, "Name cannot be null");
        Validate.notNull(testables, "Testable object assignation cannot be null");
        
        this.name = name;
        this.sequence = new TestSequence();
        
        for (int i = 0; i< testables.length; i++) {
            ((TestSequence)this.sequence).addElement(testables[i]);
        }
        
    }

    
    
    
    
    public String getName() {
        return this.name;
    }

    
    
    
    public void setDialects(final List<? extends IDialect> dialects) {
        this.dialects = new ArrayList<IDialect>();
        this.dialects.addAll(dialects);
        this.dialects = Collections.unmodifiableList(dialects);
    }
    
    public List<IDialect> getDialects() {
        return this.dialects;
    }

    
    
    
    public void setMessages(final Map<Locale,Properties> messages) {
        
        Validate.notNull(messages, "Messages cannot be null");
        
        this.messages = new HashMap<Locale, Properties>();
        this.messages.putAll(messages);
        
    }
    
    public Map<Locale,Properties> getMessages() {
        return Collections.unmodifiableMap(this.messages);
    }
    
    
    public void setMessagesForLocale(final Locale locale, final Properties messagesForLocale) {
        
        Validate.notNull(locale, "Locale cannot be null");
        Validate.notNull(messagesForLocale, "Messages for locale cannot be null");
        
        if (this.messages == DEFAULT_MESSAGES) {
            // the default messages map is immutable, so we should change it
            final Map<Locale,Properties> newMessages = new HashMap<Locale, Properties>();
            newMessages.putAll(this.messages);
            this.messages = newMessages;
        }
        
        final Properties newMessagesForLocale = new Properties();
        newMessagesForLocale.putAll(messagesForLocale);
        this.messages.put(locale, new UnmodifiableProperties(newMessagesForLocale));
        
    }
    
    public Properties getMessagesForLocale(final Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        return this.messages.get(locale);
    }

    
    
    
    public ITestSequence getSequence() {
        return this.sequence;
    }

    
    
    
    public void setReporter(final ITestReporter reporter) {
        Validate.notNull(reporter, "Reporter cannot be null");
        this.reporter = reporter;
    }
    
    public ITestReporter getReporter() {
        if (this.reporter == null) {
            return new ConsoleTestReporter(this.name);
        }
        return this.reporter;
    }
    
}

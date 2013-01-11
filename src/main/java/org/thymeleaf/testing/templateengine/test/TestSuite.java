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
    public static final ITestReporter DEFAULT_REPORTER = new ConsoleTestReporter();
    
    
    private String name = null;
    private List<IDialect> dialects = DEFAULT_DIALECTS;
    private Map<Locale,Properties> messages = DEFAULT_MESSAGES;
    private ITestReporter reporter = DEFAULT_REPORTER;
    
    private final ITestSequence sequence;
    

    
    
    
    
    public TestSuite(final ITestSequence sequence) {
        super();
        Validate.notNull(sequence, "Sequence cannot be null");
        this.sequence = sequence;
    }
    
    
    
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean hasName() {
        return this.name != null;
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

    
    
    public void setMessages(final Locale locale, final Properties messages) {
        final Properties newMessages = new Properties();
        newMessages.putAll(messages);
        this.messages.put(locale, new UnmodifiableProperties(newMessages));
    }
    
    public Properties getMessages(final Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        return this.messages.get(locale);
    }

    
    
    
    public ITestSequence getSequence() {
        return this.sequence;
    }

    public ITestReporter getReporter() {
        return this.reporter;
    }
    
}

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
package org.thymeleaf.testing.templateengine.standard.builder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.testing.templateengine.builder.ITestSequenceBuilder;
import org.thymeleaf.testing.templateengine.builder.ITestSuiteBuilder;
import org.thymeleaf.testing.templateengine.test.ITestSequence;
import org.thymeleaf.testing.templateengine.test.ITestSuite;
import org.thymeleaf.testing.templateengine.test.TestSuite;
import org.thymeleaf.testing.templateengine.test.report.ITestReporter;
import org.thymeleaf.util.Validate;





public abstract class AbstractStandardTestSuiteBuilder implements ITestSuiteBuilder {
    
    private final String suiteName;
    
    private List<? extends IDialect> dialects = null;
    private ITestReporter reporter = null;
    private Map<Locale,Properties> messages = null;
    
    
    protected AbstractStandardTestSuiteBuilder(final String suiteName) {
        super();
        this.suiteName = suiteName;
    }
    

    public String getSuiteName() {
        return this.suiteName;
    }
    

    
    public List<? extends IDialect> getDialects() {
        return this.dialects;
    }

    public void setDialects(final List<? extends IDialect> dialects) {
        this.dialects = dialects;
    }

    public ITestReporter getReporter() {
        return this.reporter;
    }

    public void setReporter(final ITestReporter reporter) {
        this.reporter = reporter;
    }

    public Map<Locale, Properties> getMessages() {
        return this.messages;
    }

    public void setMessages(final Map<Locale, Properties> messages) {
        this.messages = messages;
    }





    protected abstract ITestSequenceBuilder getSequenceBuilder(final String executionId);
    
    
    public final ITestSuite build(final String executionId) {
        
        Validate.notNull(executionId, "Execution ID cannot be null");
        final ITestSequenceBuilder sequenceBuilder = getSequenceBuilder(executionId);
        final ITestSequence sequence = sequenceBuilder.build(executionId);
        
        final TestSuite suite = new TestSuite(this.suiteName, sequence);
        if (this.dialects != null) {
            suite.setDialects(this.dialects);
        }
        if (this.reporter != null) {
            suite.setReporter(this.reporter);
        }
        if (this.messages != null) {
            suite.setMessages(this.messages);
        }
        
        return suite;
        
    }
    
}

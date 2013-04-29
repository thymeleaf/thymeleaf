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
package org.thymeleaf.testing.templateengine.engine;

import java.util.Random;

import org.thymeleaf.TemplateEngine;





final class TestExecutionContext {

    private static final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static Random RANDOM = new Random();
    private static final int ID_SIZE = 6;
    

    private final String executionId;
    
    private int nestingLevel = 0;
    private TemplateEngine templateEngine = null;
    
    private final TestNamer testNamer = new TestNamer();

    

    TestExecutionContext() {
        super();
        this.executionId = randomAlphanumeric(ID_SIZE);
    }

    
    private TestExecutionContext(final String executionId, final TemplateEngine templateEngine, final int nestingLevel) {
        super();
        this.executionId = executionId;
        this.nestingLevel = nestingLevel;
        this.templateEngine = templateEngine; 
    }
    
    

    
    public TestNamer getTestNamer() {
        return this.testNamer;
    }

    
    
    public String getExecutionId() {
        return this.executionId;
    }
    
    public int getNestingLevel() {
        return this.nestingLevel;
    }
    
    
    
    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public void setTemplateEngine(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    
    

    public TestExecutionContext nest() {
        return new TestExecutionContext(this.executionId, this.templateEngine, this.nestingLevel + 1);
    }
    
    
    
    private static String randomAlphanumeric(final int count) {
        final StringBuilder strBuilder = new StringBuilder(count);
        final int anLen = ALPHA_NUMERIC.length();
        synchronized(RANDOM) {
            for(int i = 0; i < count; i++) { 
                strBuilder.append(ALPHA_NUMERIC.charAt(RANDOM.nextInt(anLen))) ;
            }
        }
        return strBuilder.toString();
    }
    
    
}

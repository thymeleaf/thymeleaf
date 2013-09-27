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
package org.thymeleaf.standard.processor.attr;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.0
 *
 */
public abstract class AbstractStandardSwitchStructureAttrProcessor 
        extends AbstractLocalVariableDefinitionAttrProcessor {
    
    public static final String SWITCH_VARIABLE_NAME = "[%SWITCH_EXPR%]";
    

    
    public AbstractStandardSwitchStructureAttrProcessor(final IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    public AbstractStandardSwitchStructureAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    

    

    @Override
    protected final Map<String, Object> getNewLocalVariables(
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        
        final Expression switchExpression =
                StandardExpressions.parseExpression(arguments.getConfiguration(), arguments, attributeValue);

        final Map<String,Object> newVariables = new HashMap<String, Object>(2, 1.0f);
        newVariables.put(SWITCH_VARIABLE_NAME, new SwitchStructure(switchExpression));
        
        return newVariables;
        
    }



    
    public static final class SwitchStructure {
        
        private final Expression expression;
        private boolean executed;
        
        public SwitchStructure(final Expression expression) {
            super();
            this.expression = expression;
            this.executed = false;
        }

        public Expression getExpression() {
            return this.expression;
        }

        public boolean isExecuted() {
            return this.executed;
        }

        public void setExecuted(final boolean executed) {
            this.executed = executed;
        }
                
    }
    

    
}

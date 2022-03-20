/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardSwitchTagProcessor extends AbstractAttributeTagProcessor {

    public static final int PRECEDENCE = 250;
    public static final String ATTR_NAME = "switch";

    public static final String SWITCH_VARIABLE_NAME = "%%SWITCH_EXPR%%";


    public StandardSwitchTagProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }


    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final IStandardExpression switchExpression =
                expressionParser.parseExpression(context, attributeValue);

        structureHandler.setLocalVariable(SWITCH_VARIABLE_NAME, new SwitchStructure(switchExpression));

    }




    public static final class SwitchStructure {

        private final IStandardExpression expression;
        private boolean executed;

        public SwitchStructure(final IStandardExpression expression) {
            super();
            this.expression = expression;
            this.executed = false;
        }

        public IStandardExpression getExpression() {
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


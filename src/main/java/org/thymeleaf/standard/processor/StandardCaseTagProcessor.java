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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.EqualsExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.LoggingUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardCaseTagProcessor extends AbstractStandardConditionalVisibilityTagProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public static final int PRECEDENCE = 275;
    public static final String ATTR_NAME = "case";


    public static final String CASE_DEFAULT_ATTRIBUTE_VALUE = "*";



    public StandardCaseTagProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE);
    }


    @Override
    protected boolean isVisible(
            final ITemplateContext context,
            final IProcessableElementTag tag, final AttributeName attributeName, final String attributeValue) {

        /*
         * Note the th:case processors must admit the concept of SHORTCUT inside the enclosing th:switch, which means
         * that once one th:case has evaluated to true, no other th:case should be evaluated at all. It is because
         * of this that this class should not extend from any other that evaluates the attributeValue before calling
         * this code.
         */

        final StandardSwitchTagProcessor.SwitchStructure switchStructure =
                (StandardSwitchTagProcessor.SwitchStructure) context.getVariable(StandardSwitchTagProcessor.SWITCH_VARIABLE_NAME);

        if (switchStructure == null) {
            throw new TemplateProcessingException(
                    "Cannot specify a \"" + attributeName + "\" attribute in an environment where no " +
                    "switch operator has been defined before.");
        }

        if (switchStructure.isExecuted()) {
            return false;
        }

        if (attributeValue != null && attributeValue.trim().equals(CASE_DEFAULT_ATTRIBUTE_VALUE)) {

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"",
                        new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(context.getTemplateData().getTemplate()), attributeValue, attributeName, attributeValue, Boolean.TRUE});
            }

            switchStructure.setExecuted(true);
            return true;

        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final IStandardExpression caseExpression =
                expressionParser.parseExpression(context, attributeValue);

        final EqualsExpression equalsExpression = new EqualsExpression(switchStructure.getExpression(), caseExpression);

        final Object value = equalsExpression.execute(context);

        final boolean visible = EvaluationUtils.evaluateAsBoolean(value);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(context.getTemplateData().getTemplate()), attributeValue, attributeName, attributeValue, Boolean.valueOf(visible)});
        }

        if (visible) {
            switchStructure.setExecuted(true);
        }

        return visible;

    }


}


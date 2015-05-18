/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.IVariablesMap;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.EqualsExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.EvaluationUtil;

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



    public StandardCaseTagProcessor() {
        super(ATTR_NAME, PRECEDENCE);
    }


    @Override
    protected boolean isVisible(
            final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue) {

        final IVariablesMap variablesMap = processingContext.getVariablesMap();

        final StandardSwitchTagProcessor.SwitchStructure switchStructure =
                (StandardSwitchTagProcessor.SwitchStructure) variablesMap.getVariable(StandardSwitchTagProcessor.SWITCH_VARIABLE_NAME);

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
                        new Object[] {TemplateEngine.threadIndex(), processingContext.getTemplateResolution().getTemplateName(), attributeValue, attributeName, attributeValue, Boolean.TRUE});
            }

            switchStructure.setExecuted(true);
            return true;

        }

        final IStandardExpressionParser expressionParser =
                StandardExpressions.getExpressionParser(processingContext.getConfiguration());

        final IStandardExpression caseExpression = expressionParser.parseExpression(processingContext, attributeValue);

        final EqualsExpression equalsExpression = new EqualsExpression(switchStructure.getExpression(), caseExpression);

        final Object value = equalsExpression.execute(processingContext);

        final boolean visible = EvaluationUtil.evaluateAsBoolean(value);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Case expression \"{}\" in attribute \"{}\" has been evaluated as: \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), processingContext.getTemplateResolution().getTemplateName(), attributeValue, attributeName, attributeValue, Boolean.valueOf(visible)});
        }

        if (visible) {
            switchStructure.setExecuted(true);
        }

        return visible;

    }


}


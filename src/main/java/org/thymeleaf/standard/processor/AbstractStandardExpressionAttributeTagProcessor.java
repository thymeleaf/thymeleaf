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
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardExpressionAttributeTagProcessor extends AbstractAttributeTagProcessor {


    private final StandardExpressionExecutionContext expressionExecutionContext;
    private final boolean removeIfNoop;


    /**
     * <p>
     *   Build a new instance of this tag processor.
     * </p>
     *
     * @param templateMode the template mode
     * @param dialectPrefix the dialect prefox
     * @param attrName the attribute name to be matched
     * @param precedence the precedence to be applied
     * @param removeAttribute whether the attribute should be removed after execution
     *
     */
    protected AbstractStandardExpressionAttributeTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final int precedence, final boolean removeAttribute) {
        this(templateMode, dialectPrefix, attrName, precedence, removeAttribute, StandardExpressionExecutionContext.NORMAL);
    }

    /**
     * <p>
     *   Build a new instance of this tag processor.
     * </p>
     *
     * @param templateMode the template mode
     * @param dialectPrefix the dialect prefox
     * @param attrName the attribute name to be matched
     * @param precedence the precedence to be applied
     * @param removeAttribute whether the attribute should be removed after execution
     * @param restrictedExpressionExecution whether the expression to be executed (value of the attribute) should
     *                                      be executed in restricted mode (no parameter access) or not (default: false).
     *
     * @since 3.0.9
     */
    protected AbstractStandardExpressionAttributeTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final int precedence, final boolean removeAttribute,
            final boolean restrictedExpressionExecution) {
        this(templateMode, dialectPrefix, attrName, precedence, removeAttribute,
                (restrictedExpressionExecution? StandardExpressionExecutionContext.RESTRICTED :  StandardExpressionExecutionContext.NORMAL));
    }

    /**
     * <p>
     *   Build a new instance of this tag processor.
     * </p>
     *
     * @param templateMode the template mode
     * @param dialectPrefix the dialect prefox
     * @param attrName the attribute name to be matched
     * @param precedence the precedence to be applied
     * @param removeAttribute whether the attribute should be removed after execution
     * @param expressionExecutionContext the expression execution context to be applied
     *
     * @since 3.0.10
     */
    protected AbstractStandardExpressionAttributeTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final int precedence, final boolean removeAttribute,
            final StandardExpressionExecutionContext expressionExecutionContext) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, removeAttribute);
        this.removeIfNoop = !removeAttribute;
        this.expressionExecutionContext = expressionExecutionContext;
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final Object expressionResult;
        if (attributeValue != null) {

            final IStandardExpression expression = EngineEventUtils.computeAttributeExpression(context, tag, attributeName, attributeValue);

            if (expression != null && expression instanceof FragmentExpression) {
                // This is merely a FragmentExpression (not complex, not combined with anything), so we can apply a shortcut
                // so that we don't require a "null" result for this expression if the template does not exist. That will
                // save a call to resource.exists() which might be costly.

                final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                        FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression);

                expressionResult =
                        FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

            } else {

                /*
                 * Some attributes will require the execution of the expressions contained in them in RESTRICTED
                 * mode, so that e.g. access to request parameters is forbidden.
                 */
                expressionResult = expression.execute(context, this.expressionExecutionContext);

            }

        } else {
            expressionResult = null;
        }

        // If the result of this expression is NO-OP, there is nothing to execute
        if (expressionResult == NoOpToken.VALUE) {
            if (this.removeIfNoop) {
                structureHandler.removeAttribute(attributeName);
            }
            return;
        }

        doProcess(
                context, tag,
                attributeName, attributeValue,
                expressionResult, structureHandler);

    }


    protected abstract void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler);


}

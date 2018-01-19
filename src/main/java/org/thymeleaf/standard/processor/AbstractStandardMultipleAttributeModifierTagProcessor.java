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

import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.AssignationUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardMultipleAttributeModifierTagProcessor extends AbstractAttributeTagProcessor {


    protected enum ModificationType { SUBSTITUTION, APPEND, PREPEND, APPEND_WITH_SPACE, PREPEND_WITH_SPACE }

    private final ModificationType modificationType;
    private final boolean restrictedExpressionExecution;


    /**
     * <p>
     *   Build a new instance of this tag processor.
     * </p>
     *
     * @param templateMode the template mode
     * @param dialectPrefix the dialect prefox
     * @param attrName the attribute name to be matched
     * @param precedence the precedence to be applied
     * @param modificationType type of modification to be performed on the attribute (replacement, append, prepend)
     *
     */
    protected AbstractStandardMultipleAttributeModifierTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final int precedence,
            final ModificationType modificationType) {
        this(templateMode, dialectPrefix, attrName, precedence, modificationType, false);
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
     * @param modificationType type of modification to be performed on the attribute (replacement, append, prepend)
     * @param restrictedExpressionExecution whether the expression to be executed (value of the attribute) should
     *                                      be executed in restricted mode (no parameter acess) or not.
     *
     * @since 3.0.9
     */
    protected AbstractStandardMultipleAttributeModifierTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final int precedence,
            final ModificationType modificationType,
            final boolean restrictedExpressionExecution) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
        this.modificationType = modificationType;
        this.restrictedExpressionExecution = restrictedExpressionExecution;
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {


        final AssignationSequence assignations =
                AssignationUtils.parseAssignationSequence(
                        context, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        // Compute the required execution context depending on whether execution should be restricted or not
        final StandardExpressionExecutionContext expCtx =
                (this.restrictedExpressionExecution?
                        StandardExpressionExecutionContext.RESTRICTED : StandardExpressionExecutionContext.NORMAL);

        final List<Assignation> assignationValues = assignations.getAssignations();
        final int assignationValuesLen = assignationValues.size();

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignation = assignationValues.get(i);

            final IStandardExpression leftExpr = assignation.getLeft();
            final Object leftValue = leftExpr.execute(context, expCtx);

            final IStandardExpression rightExpr = assignation.getRight();
            final Object rightValue = rightExpr.execute(context, expCtx);

            if (rightValue == NoOpToken.VALUE) {
                // No changes to be done for this attribute
                continue;
            }

            final String newAttributeName = (leftValue == null? null : leftValue.toString());
            if (StringUtils.isEmptyOrWhitespace(newAttributeName)) {
                throw new TemplateProcessingException(
                        "Attribute name expression evaluated as null or empty: \"" + leftExpr + "\"");
            }

            if (getTemplateMode() == TemplateMode.HTML &&
                    this.modificationType == ModificationType.SUBSTITUTION &&
                    ArrayUtils.contains(StandardConditionalFixedValueTagProcessor.ATTR_NAMES, newAttributeName)) {
                // Attribute is a fixed-value conditional one, like "selected", which can only
                // appear as selected="selected" or not appear at all.

                if (EvaluationUtils.evaluateAsBoolean(rightValue)) {
                    structureHandler.setAttribute(newAttributeName, newAttributeName);
                } else {
                    structureHandler.removeAttribute(newAttributeName);
                }

            } else {
                // Attribute is a "normal" attribute, not a fixed-value conditional one - or we are not just replacing

                final String newAttributeValue =
                        EscapedAttributeUtils.escapeAttribute(getTemplateMode(), rightValue == null ? null : rightValue.toString());
                if (newAttributeValue == null || newAttributeValue.length() == 0) {

                    if (this.modificationType == ModificationType.SUBSTITUTION) {
                        // Substituting by a no-value will be equivalent to simply removing
                        structureHandler.removeAttribute(newAttributeName);
                    }
                    // Prepend and append simply ignored in this case

                } else {

                    if (this.modificationType == ModificationType.SUBSTITUTION ||
                            !tag.hasAttribute(newAttributeName) ||
                            tag.getAttributeValue(newAttributeName).length() == 0) {
                        // Normal value replace
                        structureHandler.setAttribute(newAttributeName, newAttributeValue);
                    } else {
                        String currentValue = tag.getAttributeValue(newAttributeName);
                        if (this.modificationType == ModificationType.APPEND) {
                            structureHandler.setAttribute(newAttributeName, currentValue + newAttributeValue);
                        } else if (this.modificationType == ModificationType.APPEND_WITH_SPACE) {
                            structureHandler.setAttribute(newAttributeName, currentValue + ' ' + newAttributeValue);
                        } else if (this.modificationType == ModificationType.PREPEND) {
                            structureHandler.setAttribute(newAttributeName, newAttributeValue + currentValue);
                        } else { // modification type is PREPEND_WITH_SPACE
                            structureHandler.setAttribute(newAttributeName, newAttributeValue + ' ' + currentValue);
                        }
                    }

                }

            }

        }

    }


}

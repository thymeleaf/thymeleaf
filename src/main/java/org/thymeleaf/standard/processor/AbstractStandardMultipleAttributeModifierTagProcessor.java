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

import java.util.List;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.AssignationUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.EvaluationUtil;
import org.thymeleaf.util.StringUtils;
import org.unbescape.html.HtmlEscape;

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



    protected AbstractStandardMultipleAttributeModifierTagProcessor(
            final String attrName, final int precedence, final ModificationType modificationType) {
        super(TemplateMode.HTML, null, false, attrName, true, precedence);
        this.modificationType = modificationType;
    }




    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {


        final AssignationSequence assignations =
                AssignationUtils.parseAssignationSequence(
                        processingContext, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        final List<Assignation> assignationValues = assignations.getAssignations();
        final int assignationValuesLen = assignationValues.size();

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignation = assignationValues.get(i);

            final IStandardExpression leftExpr = assignation.getLeft();
            final Object leftValue = leftExpr.execute(processingContext);

            final IStandardExpression rightExpr = assignation.getRight();
            final Object rightValue = rightExpr.execute(processingContext);

            final String newAttributeName = (leftValue == null? null : leftValue.toString());
            if (StringUtils.isEmptyOrWhitespace(newAttributeName)) {
                throw new TemplateProcessingException(
                        "Attribute name expression evaluated as null or empty: \"" + leftExpr + "\"");
            }

            if (this.modificationType == ModificationType.SUBSTITUTION &&
                    ArrayUtils.contains(StandardConditionalFixedValueTagProcessor.ATTR_NAMES, newAttributeName)) {
                // Attribute is a fixed-value conditional one, like "selected", which can only
                // appear as selected="selected" or not appear at all.

                if (EvaluationUtil.evaluateAsBoolean(rightValue)) {
                    tag.getAttributes().setAttribute(newAttributeName, newAttributeName);
                } else {
                    tag.getAttributes().removeAttribute(newAttributeName);
                }

            } else {
                // Attribute is a "normal" attribute, not a fixed-value conditional one - or we are not just replacing

                final String newAttributeValue = HtmlEscape.escapeHtml4Xml(rightValue == null ? null : rightValue.toString());
                if (newAttributeValue == null || newAttributeValue.length() == 0) {

                    if (this.modificationType == ModificationType.SUBSTITUTION) {
                        // Substituting by a no-value will be equivalent to simply removing
                        tag.getAttributes().removeAttribute(newAttributeName);
                    }
                    // Prepend and append simply ignored in this case

                } else {

                    if (this.modificationType == ModificationType.SUBSTITUTION ||
                            !tag.getAttributes().hasAttribute(newAttributeName) ||
                            tag.getAttributes().getValue(newAttributeName).length() == 0) {
                        // Normal value replace
                        tag.getAttributes().setAttribute(newAttributeName, newAttributeValue);
                    } else {
                        String currentValue = tag.getAttributes().getValue(newAttributeName);
                        if (this.modificationType == ModificationType.APPEND) {
                            tag.getAttributes().setAttribute(newAttributeName, currentValue + newAttributeValue);
                        } else if (this.modificationType == ModificationType.APPEND_WITH_SPACE) {
                            tag.getAttributes().setAttribute(newAttributeName, currentValue + ' ' + newAttributeValue);
                        } else if (this.modificationType == ModificationType.PREPEND) {
                            tag.getAttributes().setAttribute(newAttributeName, newAttributeValue + currentValue);
                        } else { // modification type is PREPEND_WITH_SPACE
                            tag.getAttributes().setAttribute(newAttributeName, newAttributeValue + ' ' + currentValue);
                        }
                    }

                }

            }

        }

        tag.getAttributes().removeAttribute(attributeName);

    }


}

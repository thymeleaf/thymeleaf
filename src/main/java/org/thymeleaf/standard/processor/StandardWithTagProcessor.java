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

import org.thymeleaf.context.ILocalVariableAwareVariablesMap;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.context.IVariablesMap;
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
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardWithTagProcessor extends AbstractAttributeTagProcessor {

    public static final int PRECEDENCE = 600;
    public static final String ATTR_NAME = "with";

    public StandardWithTagProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }



    @Override
    protected void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol,
            final IElementStructureHandler structureHandler) {

        final AssignationSequence assignations =
                AssignationUtils.parseAssignationSequence(
                        processingContext, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        // Normally we would just allow the structure handler to be in charge of declaring the local variables
        // by using structureHandler.setLocalVariable(...) but in this case we want each variable defined at an
        // expression to be available for the next expressions, and that forces us to cast our Variables Map into
        // a more specific interface --which shouldn't be used directly except in this specific, special case-- and
        // put the local variables directly into it.
        final IVariablesMap variablesMap = processingContext.getVariables();
        ILocalVariableAwareVariablesMap localVariableAwareVariablesMap = null;
        if (variablesMap instanceof ILocalVariableAwareVariablesMap) {
            localVariableAwareVariablesMap = (ILocalVariableAwareVariablesMap) variablesMap;
        }

        final List<Assignation> assignationValues = assignations.getAssignations();
        final int assignationValuesLen = assignationValues.size();

        for (int i = 0; i < assignationValuesLen; i++) {

            final Assignation assignation = assignationValues.get(i);

            final IStandardExpression leftExpr = assignation.getLeft();
            final Object leftValue = leftExpr.execute(processingContext);

            final IStandardExpression rightExpr = assignation.getRight();
            final Object rightValue = rightExpr.execute(processingContext);

            final String newVariableName = (leftValue == null? null : leftValue.toString());
            if (StringUtils.isEmptyOrWhitespace(newVariableName)) {
                throw new TemplateProcessingException(
                        "Variable name expression evaluated as null or empty: \"" + leftExpr + "\"");
            }

            if (localVariableAwareVariablesMap != null) {
                localVariableAwareVariablesMap.put(newVariableName, rightValue);
            } else {
                // The problem is, these won't be available until we execute the next processor
                structureHandler.setLocalVariable(newVariableName, rightValue);
            }

        }

    }


}

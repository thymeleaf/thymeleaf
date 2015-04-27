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

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.engine.ParsedFragmentMarkup;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.IStandardExpression;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardFragmentInsertionTagProcessor extends AbstractStandardAttributeTagProcessor {


    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";


    private final boolean replaceHost;
    private final boolean insertOnlyContents;



    protected AbstractStandardFragmentInsertionTagProcessor(
            final String attrName, final int precedence, final boolean replaceHost, final boolean insertOnlyContents) {
        super(attrName, precedence);
        this.replaceHost = replaceHost;
        this.insertOnlyContents = insertOnlyContents;
    }



    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {

        final FragmentSelection fragmentSelection =
                FragmentSelectionUtils.parseFragmentSelection(processingContext, attributeValue);

        final IStandardExpression templateNameExpression = fragmentSelection.getTemplateName();
        final String templateName;
        if (templateNameExpression != null) {
            final Object templateNameObject = templateNameExpression.execute(processingContext);
            if (templateNameObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of template name from spec \"" + attributeValue + "\" returned null.");
            }
            final String evaluatedTemplateName = templateNameObject.toString();
            if (TEMPLATE_NAME_CURRENT_TEMPLATE.equals(evaluatedTemplateName)) {
                // Template name is "this" and therefore we are including a fragment from the same template.
                templateName = null;
            } else {
                templateName = templateNameObject.toString();
            }
        } else {
            // If template name expression is null, we will execute the fragment on the "current" template
            templateName = null;
        }

        // Resolve fragment parameters, if specified (null if not)
        final Map<String,Object> fragmentParameters =
                resolveFragmentParameters(processingContext, fragmentSelection.getParameters());

        final ParsedFragmentMarkup parsedFragment;
        if (fragmentSelection.hasFragmentSelector()) {

            final Object fragmentSelectorObject =
                    fragmentSelection.getFragmentSelector().execute(processingContext);
            if (fragmentSelectorObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of fragment selector from spec \"" + attributeValue + "\" " +
                                "returned null.");
            }

            String fragmentSelector = fragmentSelectorObject.toString();

            if (fragmentSelector.length() > 3 &&
                    fragmentSelector.charAt(0) == '[' && fragmentSelector.charAt(fragmentSelector.length() - 1) == ']' &&
                    fragmentSelector.charAt(fragmentSelector.length() - 2) != '\'') {
                // For legacy compatibility reasons, we allow fragment DOM Selector expressions to be specified
                // between brackets. Just remove them.
                fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1);
            }

            parsedFragment =
                    processingContext.getTemplateProcessor().parseTemplateFragment(
                            processingContext, templateName, new String[] { fragmentSelector }, this.insertOnlyContents);

        } else {

            parsedFragment =
                    processingContext.getTemplateProcessor().parseTemplateFragment(
                            processingContext, templateName, null); // insertOnlyContents would make no sense here

        }

        if (this.replaceHost) {
            structureHandler.replaceWith(parsedFragment, true);
        } else {
            structureHandler.setBody(parsedFragment, true);
        }

    }





    private static Map<String,Object> resolveFragmentParameters(
            final IProcessingContext processingContext,
            final AssignationSequence parameters) {

        if (parameters == null) {
            return null;
        }

        final Map<String,Object> parameterValues = new HashMap<String, Object>(parameters.size() + 2);
        for (final Assignation assignation : parameters.getAssignations()) {

            final IStandardExpression parameterNameExpr = assignation.getLeft();
            final Object parameterNameValue = parameterNameExpr.execute(processingContext);

            final String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

            final IStandardExpression parameterValueExpr = assignation.getRight();
            final Object parameterValueValue = parameterValueExpr.execute(processingContext);

            parameterValues.put(parameterName, parameterValueValue);

        }

        return parameterValues;

    }


}

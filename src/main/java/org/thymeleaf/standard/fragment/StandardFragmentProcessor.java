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
package org.thymeleaf.standard.fragment;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Configuration;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.Validate;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class StandardFragmentProcessor {

    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";

    

    /**
     * @since 2.1.0
     */
    public static StandardFragment computeStandardFragmentSpec(
            final Configuration configuration, final IProcessingContext processingContext, 
            final String standardFragmentSpec, final String dialectPrefix, final String fragmentSignatureAttributeName) {
        
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notEmpty(standardFragmentSpec, "Fragment Spec cannot be null");
        // Target element and attribute names can be null

        final StandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final FragmentSelection fragmentSelection =
                expressionParser.parseFragmentSelection(configuration, processingContext, standardFragmentSpec);

        final Expression templateNameExpression = fragmentSelection.getTemplateName();
        final String templateName;
        if (templateNameExpression != null) {
            final Object templateNameObject =
                    expressionExecutor.executeExpression(configuration, processingContext, templateNameExpression);
            if (templateNameObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of template name from spec \"" + standardFragmentSpec + "\" " +
                                "returned null.");
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
                resolveFragmentParameters(configuration,processingContext,fragmentSelection.getParameters());

        if (fragmentSelection.hasFragmentSelector()) {

            final Object fragmentSelectorObject =
                    expressionExecutor.executeExpression(configuration, processingContext, fragmentSelection.getFragmentSelector());
            if (fragmentSelectorObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of fragment selector from spec \"" + standardFragmentSpec + "\" " + 
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

            final DOMSelector.INodeReferenceChecker nodeReferenceChecker =
                    new StandardFragmentSignatureNodeReferenceChecker(configuration, dialectPrefix, fragmentSignatureAttributeName);

            final IFragmentSpec fragmentSpec = new DOMSelectorFragmentSpec(fragmentSelector, nodeReferenceChecker);

            return new StandardFragment(templateName, fragmentSpec, fragmentParameters, dialectPrefix, fragmentSignatureAttributeName);
            
        }
        
        return new StandardFragment(templateName, WholeFragmentSpec.INSTANCE, fragmentParameters);
        
    }





    private static Map<String,Object> resolveFragmentParameters(
            final Configuration configuration, final IProcessingContext processingContext,
            final AssignationSequence parameters) {

        if (parameters == null) {
            return null;
        }

        final StandardExpressionExecutor expressionExecutor = StandardExpressions.getExpressionExecutor(configuration);

        final Map<String,Object> parameterValues = new HashMap<String, Object>(parameters.size() + 2);
        for (final Assignation assignation : parameters.getAssignations()) {

            final Expression parameterNameExpr = assignation.getLeft();
            final Object parameterNameValue =
                    expressionExecutor.executeExpression(configuration, processingContext, parameterNameExpr);

            final String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

            final Expression parameterValueExpr = assignation.getRight();
            final Object parameterValueValue =
                    expressionExecutor.executeExpression(configuration, processingContext, parameterValueExpr);

            parameterValues.put(parameterName, parameterValueValue);

        }

        return parameterValues;

    }

    
    
    
    private StandardFragmentProcessor() {
        super();
    }

    
}


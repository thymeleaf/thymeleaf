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
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.ElementAndAttributeNameFragmentSpec;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.fragment.FragmentSignatureAttributeFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
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
    private static final char FRAGMENT_SELECTOR_DOM_SELECTOR_START = '[';
    private static final char FRAGMENT_SELECTOR_DOM_SELECTOR_END = ']';

    

    /**
     * @since 2.0.12
     */
    public static FragmentAndTarget computeStandardFragmentSpec(
            final Configuration configuration, final IProcessingContext processingContext, 
            final String standardFragmentSpec, final String targetElementName, final String targetAttributeName,
            final boolean returnOnlyChildrenIfContainingElement) {
        
        Validate.notNull(processingContext, "Evaluation Context cannot be null");
        Validate.notEmpty(standardFragmentSpec, "Fragment Spec cannot be null");
        // Target element and attribute names can be null
        
        final FragmentSelection fragmentSelection =
            StandardExpressionProcessor.parseFragmentSelection(configuration, processingContext, standardFragmentSpec);

        final Expression templateNameExpression = fragmentSelection.getTemplateName();
        final String templateName;
        if (templateNameExpression != null) {
            final Object templateNameObject =
                    StandardExpressionProcessor.executeExpression(configuration, processingContext, templateNameExpression);
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
                StandardExpressionProcessor.executeExpression(configuration, processingContext, fragmentSelection.getFragmentSelector());
            if (fragmentSelectorObject == null) {
                throw new TemplateProcessingException(
                        "Evaluation of fragment selector from spec \"" + standardFragmentSpec + "\" " + 
                        "returned null.");
            }

            final String fragmentSelector = fragmentSelectorObject.toString();
            
            if (fragmentSelector.charAt(0) == FRAGMENT_SELECTOR_DOM_SELECTOR_START &&
                    fragmentSelector.charAt(fragmentSelector.length() - 1) == FRAGMENT_SELECTOR_DOM_SELECTOR_END) {
                // This selector has the form [...], therefore we will consider it a DOM Selector-based selector.

                final IFragmentSpec fragmentSpec =
                        new DOMSelectorFragmentSpec(
                                fragmentSelector.substring(1,fragmentSelector.length() - 1),
                                fragmentParameters, returnOnlyChildrenIfContainingElement);

                return new FragmentAndTarget(templateName, fragmentSpec);
                
            }

            if (targetElementName == null) {
                // If we are not looking for a specific element name, we will consider we are looking for a fragment
                // signature, which might specify fragment parameters and has no limitations on the eleent on which
                // they can appear.

                final IFragmentSpec fragmentSpec =
                        new FragmentSignatureAttributeFragmentSpec(
                                targetAttributeName, fragmentSelector, fragmentParameters, returnOnlyChildrenIfContainingElement);
                return new FragmentAndTarget(templateName, fragmentSpec);

            }
            
            final IFragmentSpec fragmentSpec = 
                    new ElementAndAttributeNameFragmentSpec(
                            targetElementName, targetAttributeName, fragmentSelector,
                            fragmentParameters, returnOnlyChildrenIfContainingElement);
            return new FragmentAndTarget(templateName, fragmentSpec);
            
        }
        
        final IFragmentSpec fragmentSpec =
                (fragmentParameters == null? WholeFragmentSpec.INSTANCE : new WholeFragmentSpec(fragmentParameters));
        return new FragmentAndTarget(templateName, fragmentSpec);
        
    }





    private static Map<String,Object> resolveFragmentParameters(
            final Configuration configuration, final IProcessingContext processingContext,
            final AssignationSequence parameters) {

        if (parameters == null) {
            return null;
        }

        final Map<String,Object> parameterValues = new HashMap<String, Object>(parameters.size() + 2);
        for (final Assignation assignation : parameters.getAssignations()) {

            final Expression parameterNameExpr = assignation.getLeft();
            final Object parameterNameValue =
                    StandardExpressionProcessor.executeExpression(configuration, processingContext, parameterNameExpr);

            final String parameterName = (parameterNameValue == null? null : parameterNameValue.toString());

            final Expression parameterValueExpr = assignation.getRight();
            final Object parameterValueValue =
                    StandardExpressionProcessor.executeExpression(configuration, processingContext, parameterValueExpr);

            parameterValues.put(parameterName, parameterValueValue);

        }

        return parameterValues;

    }

    
    
    
    private StandardFragmentProcessor() {
        super();
    }

    
}


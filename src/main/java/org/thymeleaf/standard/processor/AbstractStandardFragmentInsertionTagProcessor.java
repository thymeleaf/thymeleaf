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
import org.thymeleaf.engine.ITemplateHandlerEvent;
import org.thymeleaf.engine.ImmutableMarkup;
import org.thymeleaf.engine.Markup;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.standard.expression.FragmentSignatureUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.util.StringUtils;
import org.unbescape.html.HtmlEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardFragmentInsertionTagProcessor extends AbstractStandardAttributeTagProcessor {


    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";
    private static final String FRAGMENT_ATTR_NAME = "fragment";


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

        /*
         * PARSE THE FRAGMENT SELECTION SPEC and resolve each of its components
         */
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


        /*
         * RESOLVE FRAGMENT PARAMETERS if specified (null if not)
         */
        Map<String,Object> fragmentParameters =
                resolveFragmentParameters(processingContext, fragmentSelection.getParameters());


        /*
         * OBTAIN THE FRAGMENT MARKUP from the TemplateManager. This means the fragment will be parsed and maybe
         * cached, and we will be returned an immutable markup object (specifically a ParsedFragmentMarkup)
         */
        ImmutableMarkup parsedFragment;
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
                    processingContext.getTemplateManager().parseTemplateFragment(
                            processingContext, templateName, new String[] { fragmentSelector });

        } else {

            parsedFragment =
                    processingContext.getTemplateManager().parseTemplateFragment(
                            processingContext, templateName, null); // insertOnlyContents would make no sense here

        }


        /*
         * ONCE WE HAVE THE FRAGMENT MARKUP (its events, in fact), CHECK THE FRAGMENT SIGNATURE which might
         * affect the way we apply the parameters to the fragment
         */
        final int parsedFragmentLen = parsedFragment.size();
        final ITemplateHandlerEvent firstEvent = (parsedFragmentLen >= 1? parsedFragment.get(0) : null);

        // We need to examine the first event just in case it contains a th:fragment matching the one we were looking
        if (firstEvent instanceof IProcessableElementTag) {

            final IElementAttributes elementAttributes = ((IProcessableElementTag)firstEvent).getAttributes();
            if (elementAttributes.hasAttribute(getDialectPrefix(), FRAGMENT_ATTR_NAME)) {
                // The selected fragment actually has a "th:fragment" attribute, so we should process its signature

                final String fragmentSignatureSpec = HtmlEscape.unescapeHtml(elementAttributes.getValue(getDialectPrefix(), FRAGMENT_ATTR_NAME));
                if (!StringUtils.isEmptyOrWhitespace(fragmentSignatureSpec)) {

                    final FragmentSignature fragmentSignature =
                            FragmentSignatureUtils.parseFragmentSignature(processingContext.getConfiguration(), fragmentSignatureSpec);
                    if (fragmentSignature != null) {

                        // Reshape the fragment parameters into the ones that we will actually use, according to the signature
                        fragmentParameters = FragmentSignatureUtils.processParameters(fragmentSignature, fragmentParameters);

                    }

                }

            }

        }


        /*
         * APPLY THE FRAGMENT PARAMETERS AS LOCAL VARIABLES, perhaps after reshaping it according to the fragment signature
         */
        if (fragmentParameters != null && fragmentParameters.size() > 0) {
            for (final Map.Entry<String,Object> fragmentParameterEntry : fragmentParameters.entrySet()) {
                structureHandler.setLocalVariable(fragmentParameterEntry.getKey(), fragmentParameterEntry.getValue());
            }
        }


        /*
         * IF WE ARE ASKING ONLY FOR CONTENTS (th:include), THEN REMOVE THE CONTAINER BLOCK
         */
        if (this.insertOnlyContents && fragmentSelection.hasFragmentSelector()) {

            final ITemplateHandlerEvent lastEvent = (parsedFragmentLen >= 2? parsedFragment.get(parsedFragmentLen - 1) : null);

            if (firstEvent != null && lastEvent != null && firstEvent instanceof IOpenElementTag && lastEvent instanceof ICloseElementTag) {

                // We will now remove the first and last events. Note we have to clone to a mutable markup object, and
                // therefore we will be performing a bit worse (because of the node cloning) than if we just inserted
                // the whole fragment without any modifications
                final Markup mutableMarkup = parsedFragment.asMutable();
                mutableMarkup.remove(parsedFragmentLen - 1);
                mutableMarkup.remove(0);

                parsedFragment = mutableMarkup.asImmutable();

            }


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

        if (parameters == null || parameters.size() == 0) {
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

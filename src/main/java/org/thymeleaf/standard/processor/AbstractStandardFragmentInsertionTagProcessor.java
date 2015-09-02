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

import java.util.Map;

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.engine.ITemplateHandlerEvent;
import org.thymeleaf.engine.ImmutableMarkup;
import org.thymeleaf.engine.MutableMarkup;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAutoCloseElementTag;
import org.thymeleaf.model.IAutoOpenElementTag;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.standard.expression.FragmentSignatureUtils;
import org.thymeleaf.standard.expression.ParsedFragmentSelection;
import org.thymeleaf.standard.expression.ProcessedFragmentSelection;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardFragmentInsertionTagProcessor extends AbstractAttributeTagProcessor {


    private static final String FRAGMENT_ATTR_NAME = "fragment";
    private static final String TEMPLATE_NAME_CURRENT_TEMPLATE = "this";


    private final boolean replaceHost;
    private final boolean insertOnlyContents;



    protected AbstractStandardFragmentInsertionTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence,
            final boolean replaceHost, final boolean insertOnlyContents) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence);
        this.replaceHost = replaceHost;
        this.insertOnlyContents = insertOnlyContents;
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementStructureHandler structureHandler) {

        /*
         * PARSE THE FRAGMENT SELECTION SPEC and resolve each of its components
         */
        final ParsedFragmentSelection parsedFragmentSelection =
                FragmentSelectionUtils.parseFragmentSelection(processingContext, attributeValue);
        if (parsedFragmentSelection == null) {
            throw new TemplateProcessingException("Could not parse as fragment selection: \"" + attributeValue + "\"");
        }


        /*
         * PROCESS THE PARSED FRAGMENT SELECTION
         */
        final ProcessedFragmentSelection processedFragmentSelection =
                FragmentSelectionUtils.processFragmentSelection(processingContext, parsedFragmentSelection);

        String templateName = processedFragmentSelection.getTemplateName();
        Map<String,Object> fragmentParameters = processedFragmentSelection.getFragmentParameters();


        if (StringUtils.isEmptyOrWhitespace(templateName) || TEMPLATE_NAME_CURRENT_TEMPLATE.equals(templateName)) {
            // We will use the same templateName that the host tag comes from
            templateName = tag.getTemplateName();
        }


        /*
         * OBTAIN THE FRAGMENT MARKUP from the TemplateManager. This means the fragment will be parsed and maybe
         * cached, and we will be returned an immutable markup object (specifically a ParsedFragmentMarkup)
         */
        final String[] markupFragments =
                (processedFragmentSelection.hasFragmentSelector()? new String[] { processedFragmentSelection.getFragmentSelector() } : null);
        ImmutableMarkup parsedFragment =
                    processingContext.getTemplateManager().parseStandaloneFragment(
                            processingContext.getConfiguration(),
                            templateName, markupFragments,
                            // we actually 'force' the template mode of the inserted fragment to be the same. This
                            // gives us a little bit more flexibility including e.g. text templates inside HTML ones,
                            // with the possibility of later applying inlining (text inlining in this case) to process
                            // them.
                            processingContext.getTemplateMode(),
                            processingContext.getVariables(), true);


        /*
         * ONCE WE HAVE THE FRAGMENT MARKUP (its events, in fact), CHECK THE FRAGMENT SIGNATURE which might
         * affect the way we apply the parameters to the fragment
         */
        final int parsedFragmentLen = parsedFragment.size();
        final ITemplateHandlerEvent firstEvent = (parsedFragmentLen >= 1? parsedFragment.get(0) : null);

        // We need to examine the first event just in case it contains a th:fragment matching the one we were looking
        if (firstEvent instanceof IProcessableElementTag) {

            final String dialectPrefix = attributeName.getPrefix();

            final IElementAttributes elementAttributes = ((IProcessableElementTag)firstEvent).getAttributes();
            if (elementAttributes.hasAttribute(dialectPrefix, FRAGMENT_ATTR_NAME)) {
                // The selected fragment actually has a "th:fragment" attribute, so we should process its signature

                final String fragmentSignatureSpec =
                        EscapedAttributeUtils.unescapeAttribute(processingContext.getTemplateMode(), elementAttributes.getValue(dialectPrefix, FRAGMENT_ATTR_NAME));
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
        if (this.insertOnlyContents && parsedFragmentSelection.hasFragmentSelector()) {

            /*
             * In the case of th:include, things get a bit complicated because we need to remove the "element envelopes"
             * that contain what we really want to include (these envelopes' contents). So we will need to traverse
             * the entire returned markup detecting those envelopes (open+close tags at markup level == 0) and remove
             * them, along with anything else that is also at that level 0.
             */

            final MutableMarkup mutableMarkup = parsedFragment.cloneAsMutable();
            int markupLevel = 0;
            int n = mutableMarkup.size();
            while (n-- != 0) { // We traverse backwards so that we can modify at the same time

                final ITemplateHandlerEvent event = mutableMarkup.get(n);

                if (event instanceof ICloseElementTag || event instanceof IAutoCloseElementTag) {
                    if (markupLevel <= 0) {
                        mutableMarkup.remove(n);
                    }
                    markupLevel++;
                    continue;
                }
                if (event instanceof IOpenElementTag || event instanceof IAutoOpenElementTag) {
                    markupLevel--;
                    if (markupLevel <= 0) {
                        mutableMarkup.remove(n);
                    }
                    continue;
                }
                if (markupLevel <= 0) {
                    mutableMarkup.remove(n);
                }

            }

            // Once processed, we convert it to immutable (this way, it won't be cloned anymore)
            parsedFragment = mutableMarkup.cloneAsImmutable();

        }


        if (this.replaceHost) {
            structureHandler.replaceWith(parsedFragment, true);
        } else {
            structureHandler.setBody(parsedFragment, true);
        }

        tag.getAttributes().removeAttribute(attributeName);

    }




}

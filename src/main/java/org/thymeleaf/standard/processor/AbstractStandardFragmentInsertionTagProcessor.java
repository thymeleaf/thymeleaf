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

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.standard.expression.FragmentSignatureUtils;
import org.thymeleaf.standard.expression.ParsedFragmentSelection;
import org.thymeleaf.standard.expression.ProcessedFragmentSelection;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.FastStringWriter;
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
    // This flag should probably be removed once th:include is removed in 3.2 (deprecated in 3.0)
    private final boolean insertOnlyContents;



    protected AbstractStandardFragmentInsertionTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence,
            final boolean replaceHost) {
        this(templateMode, dialectPrefix, attrName, precedence, replaceHost, false);
    }

    /*
     * This constructor has package visibility in order to avoid user-created subclasses to use it. This should
     * only be used by the th:include processor, which was deprecated as of 3.0, and will be removed in 3.2.
     */
    AbstractStandardFragmentInsertionTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence,
            final boolean replaceHost, final boolean insertOnlyContents) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
        this.replaceHost = replaceHost;
        this.insertOnlyContents = insertOnlyContents;
    }



    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {


        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * PARSE THE FRAGMENT SELECTION SPEC and resolve each of its components
         */
        final ParsedFragmentSelection parsedFragmentSelection =
                FragmentSelectionUtils.parseFragmentSelection(context, attributeValue);
        if (parsedFragmentSelection == null) {
            throw new TemplateProcessingException("Could not parse as fragment selection: \"" + attributeValue + "\"");
        }


        /*
         * PROCESS THE PARSED FRAGMENT SELECTION
         */
        final ProcessedFragmentSelection processedFragmentSelection =
                FragmentSelectionUtils.processFragmentSelection(context, parsedFragmentSelection);

        String templateName = processedFragmentSelection.getTemplateName();
        Map<String,Object> fragmentParameters = processedFragmentSelection.getFragmentParameters();
        final Set<String> fragments =
                (processedFragmentSelection.hasFragmentSelector()? Collections.singleton(processedFragmentSelection.getFragmentSelector()) : null);

        /*
         * OBTAIN THE FRAGMENT MODEL from the TemplateManager. This means the fragment will be parsed and maybe
         * cached, and we will be returned an immutable model object (specifically a ParsedFragmentModel)
         */

        List<String> templateNameStack = null;
        // scan the template stack if template name is 'this' or an empty name is being used
        if (StringUtils.isEmptyOrWhitespace(templateName) || TEMPLATE_NAME_CURRENT_TEMPLATE.equals(templateName)) {
            templateNameStack = new ArrayList<String>(3);
            for (int i = context.getTemplateStack().size() - 1; i >= 0; i--) {
                templateNameStack.add(context.getTemplateStack().get(i).getTemplate());
            }
            templateName = templateNameStack.get(0);
        }

        TemplateModel fragmentModel;
        String parsedTemplate = templateName;
        int i = 0;
        do {
            fragmentModel =
                    configuration.getTemplateManager().parseStandalone(
                            context, parsedTemplate, fragments,
                            null,   // we will not force the template mode
                            true);  // use the cache if possible, fragments are from template files
            i++;
        } while (fragmentModel.size() <= 2 &&
                 templateNameStack != null &&
                 i < templateNameStack.size() &&
                 (parsedTemplate = templateNameStack.get(i)) != null);  //post test -- need to parse at least 1x

        /*
         * ONCE WE HAVE THE FRAGMENT MODEL (its events, in fact), CHECK THE FRAGMENT SIGNATURE which might
         * affect the way we apply the parameters to the fragment.
         *
         * Note this works whatever the template mode of the inserted fragment, given we are looking for an
         * element containing a "th:fragment/data-th-fragment" in a generic, non-template-dependent way.
         */
        final int parsedFragmentLen = fragmentModel.size();
        ITemplateEvent fragmentHolderEvent = (parsedFragmentLen >= 1? fragmentModel.get(0) : null);
        if (fragmentHolderEvent != null && fragmentHolderEvent instanceof ITemplateStart) {
            fragmentHolderEvent = (parsedFragmentLen >= 3? fragmentModel.get(1) : null);
        }

        // We need to examine the first event just in case it contains a th:fragment matching the one we were looking
        if (fragmentHolderEvent instanceof IProcessableElementTag) {

            final String dialectPrefix = attributeName.getPrefix();

            final IElementAttributes elementAttributes = ((IProcessableElementTag)fragmentHolderEvent).getAttributes();
            if (elementAttributes.hasAttribute(dialectPrefix, FRAGMENT_ATTR_NAME)) {
                // The selected fragment actually has a "th:fragment" attribute, so we should process its signature

                final String fragmentSignatureSpec =
                        EscapedAttributeUtils.unescapeAttribute(fragmentModel.getTemplateMode(), elementAttributes.getValue(dialectPrefix, FRAGMENT_ATTR_NAME));
                if (!StringUtils.isEmptyOrWhitespace(fragmentSignatureSpec)) {

                    final FragmentSignature fragmentSignature =
                            FragmentSignatureUtils.parseFragmentSignature(configuration, fragmentSignatureSpec);
                    if (fragmentSignature != null) {

                        // Reshape the fragment parameters into the ones that we will actually use, according to the signature
                        fragmentParameters = FragmentSignatureUtils.processParameters(fragmentSignature, fragmentParameters);

                    }

                }

            }

        }


        /*
         * CHECK WHETHER THIS IS A CROSS-TEMPLATE-MODE INSERTION. Only TemplateModels for the same template mode
         * can be safely inserted into the template being executed and processed just like any other sequences of
         * events. If the inserted template has a different template mode, we will need to process it aside and
         * obtain a String result for it, then insert such String as mere text.
         *
         * Note inserting large templates with a different template mode could therefore have a negative effect
         * on performance and memory usage, as their result needs to be completely stored in memory at some point
         * before being handled to the following phases of template processing. It is therefore recommended that
         * cross-template-mode fragment insertion is done only for small fragments, in which case it will work
         * almost the same as inlining (with the exception that the content to be inlined will be retrieved from
         * somewhere else by means of template resolution).
         */
        if (context.getTemplateMode() != fragmentModel.getTemplateMode()) {

            // Check if this is a th:include. If so, just don't allow
            if (this.insertOnlyContents) {
                throw new TemplateProcessingException(
                        "Template being processed uses template mode " + context.getTemplateMode() + ", " +
                        "inserted fragment \"" + attributeValue + "\" uses template mode " +
                        fragmentModel.getTemplateMode() + ". Cross-template-mode fragment insertion is not " +
                        "allowed using the " + attributeName + " attribute, which is considered deprecated as " +
                        "of Thymeleaf 3.0. Use {th:insert,data-th-insert} or {th:replace,data-th-replace} " +
                        "instead, which do not remove the container element from the fragment being inserted.");
            }

            // If there are parameters specified, we will need to add them directly to the variables map instead of
            // doing it through the structure handler (we are going to perform a nested template processing operation)
            if (fragmentParameters != null && fragmentParameters.size() > 0) {

                if (!(context instanceof IEngineContext)) {
                    throw new TemplateProcessingException(
                            "Parameterized fragment insertion is not supported because local variable support is DISABLED. This is due to " +
                            "the use of an implementation of the " + ITemplateContext.class.getName() + " interface that does " +
                            "not provide local-variable support. In order to have local-variable support, the variables map " +
                            "implementation should also implement the " + IEngineContext.class.getName() +
                            " interface");
                }

                // NOTE this IEngineContext interface is internal and should not be used in users' code
                ((IEngineContext) context).setVariables(fragmentParameters);

            }

            // Once parameters are in order, just process the template in a nested template engine execution
            final Writer stringWriter = new FastStringWriter(200);
            configuration.getTemplateManager().process(fragmentModel, context, stringWriter);

            // We will insert the result as NON-PROCESSABLE text (it's already been processed!)
            if (this.replaceHost) {
                structureHandler.replaceWith(stringWriter.toString(), false);
            } else {
                structureHandler.setBody(stringWriter.toString(), false);
            }

            return;

        }

        /*
         * APPLY THE FRAGMENT'S TEMPLATE RESOLUTION so that all code inside the fragment is executed with its own
         * template resolution info (working as if it were a local variable)
         */
        structureHandler.setTemplateData(fragmentModel.getTemplateData());


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
             * the entire returned model detecting those envelopes (open+close tags at model level == 0) and remove
             * them, along with anything else that is also at that level 0.
             */

            final IModel model = fragmentModel.cloneModel();
            int modelLevel = 0;
            int n = model.size();
            while (n-- != 0) { // We traverse backwards so that we can modify at the same time

                final ITemplateEvent event = model.get(n);

                if (event instanceof ICloseElementTag) {
                    if (((ICloseElementTag) event).isUnmatched()) {
                        // This is an unmatched close tag (no corresponding open), therefore should not affect our count
                        continue;
                    }
                    if (modelLevel <= 0) {
                        model.remove(n);
                    }
                    modelLevel++;
                    continue;
                }
                if (event instanceof IOpenElementTag) {
                    modelLevel--;
                    if (modelLevel <= 0) {
                        model.remove(n);
                    }
                    continue;
                }
                if (modelLevel <= 0) {
                    model.remove(n);
                }

            }

            if (this.replaceHost) {
                structureHandler.replaceWith(model, true);
            } else {
                structureHandler.setBody(model, true);
            }

            return;

        }


        if (this.replaceHost) {
            structureHandler.replaceWith(fragmentModel, true);
        } else {
            structureHandler.setBody(fragmentModel, true);
        }

    }




}

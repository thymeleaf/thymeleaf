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

import java.io.Writer;
import java.util.Map;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Fragment;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.standard.expression.FragmentSignatureUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressions;
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


    private final boolean replaceHost;
    // This flag should probably be removed once th:include is removed in 3.2 (not recommended in 3.0, deprecated in 3.1)
    private final boolean insertOnlyContents;



    protected AbstractStandardFragmentInsertionTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final int precedence,
            final boolean replaceHost) {
        this(templateMode, dialectPrefix, attrName, precedence, replaceHost, false);
    }

    /*
     * This constructor has package visibility in order to avoid user-created subclasses to use it. This should
     * only be used by the th:include processor, which was not recommended as of 3.0, (deprecated as of 3.1,
     * and will be removed in 3.2).
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


        if (StringUtils.isEmptyOrWhitespace(attributeValue)) {
            throw new TemplateProcessingException("Fragment specifications cannot be empty");
        }

        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * PARSE AND PROCESS THE FRAGMENT
         */
        final Object fragmentObj = computeFragment(context, attributeValue);
        if (fragmentObj == null) {

            // If the Fragment result is null, this is an error. Note a NULL result is not the same as the
            // result being the empty fragment (~{})

            throw new TemplateInputException(
                    "Error resolving fragment: \"" + attributeValue + "\": " +
                    "template or fragment could not be resolved");

        } else if (fragmentObj == NoOpToken.VALUE) {

            // If the Fragment result is NO-OP, we will just do nothing (apart from deleting the th:* attribute)
            return;

        } else if (fragmentObj == Fragment.EMPTY_FRAGMENT) {

            // The result is the empty fragment, which means we simply have to either remove the body of this
            // tag (th:insert) or remove it completely, tag included (th:replace)
            if (this.replaceHost) {
                structureHandler.removeElement();
            } else {
                structureHandler.removeBody();
            }
            return;

        }


        final Fragment fragment = (Fragment) fragmentObj;


        final TemplateModel fragmentModel = fragment.getTemplateModel();
        Map<String, Object> fragmentParameters = fragment.getParameters();

        /*
         * ONCE WE HAVE THE FRAGMENT MODEL (its events, in fact), CHECK THE FRAGMENT SIGNATURE
         * Fragment signature is important because it might affect the way we apply the parameters to the fragment.
         *
         * Note this works whatever the template mode of the inserted fragment, given we are looking for an
         * element containing a "th:fragment/data-th-fragment" in a generic, non-template-dependent way.
         */

        // We will check types first instead of events in order to (many times) avoid creating an immutably-wrapped
        // event object when calling "model.get(pos)"

        boolean signatureApplied = false;
        final ITemplateEvent firstEvent = (fragmentModel.size() > 2 ? fragmentModel.get(1) : null);
        if (firstEvent != null && IProcessableElementTag.class.isAssignableFrom(firstEvent.getClass())) {

            final String dialectPrefix = attributeName.getPrefix();
            final IProcessableElementTag fragmentHolderEvent = (IProcessableElementTag) firstEvent;

            if (fragmentHolderEvent.hasAttribute(dialectPrefix, FRAGMENT_ATTR_NAME)) {
                // The selected fragment actually has a "th:fragment" attribute, so we should process its signature

                final String fragmentSignatureSpec =
                        EscapedAttributeUtils.unescapeAttribute(fragmentModel.getTemplateMode(), fragmentHolderEvent.getAttributeValue(dialectPrefix, FRAGMENT_ATTR_NAME));
                if (!StringUtils.isEmptyOrWhitespace(fragmentSignatureSpec)) {

                    final FragmentSignature fragmentSignature =
                            FragmentSignatureUtils.parseFragmentSignature(configuration, fragmentSignatureSpec);
                    if (fragmentSignature != null) {

                        // Reshape the fragment parameters into the ones that we will actually use, according to the signature
                        fragmentParameters = FragmentSignatureUtils.processParameters(fragmentSignature, fragmentParameters, fragment.hasSyntheticParameters());
                        signatureApplied = true;

                    }

                }

            }

        }

        // If no signature applied, we must check if the parameters map contains synthetic parameters. If so,
        // we should raise an exception because not doing so could provoke confusion in users who would see parameters
        // not being applied, maybe not realising there was no signature assignation involved.
        if (!signatureApplied && fragment.hasSyntheticParameters()) {
            throw new TemplateProcessingException(
                    "Fragment '" + attributeValue + "' specifies synthetic (unnamed) parameters, but the resolved fragment " +
                    "does not match a fragment signature (th:fragment,data-th-fragment) which could apply names to " +
                    "the specified parameters.");
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
                        "allowed using the " + attributeName + " attribute, which is no longer recommended for use as " +
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
        final TemplateData fragmentTemplateData = fragmentModel.getTemplateData();
        structureHandler.setTemplateData(fragmentTemplateData);


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
        if (this.insertOnlyContents && fragmentTemplateData.hasTemplateSelectors()) {

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




    /*
     * This can return a Fragment, NoOpToken (if nothing should be done) or null
     */
    private static Object computeFragment(final ITemplateContext context, final String input) {

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());

        final String trimmedInput = input.trim();

        if (shouldBeWrappedAsFragmentExpression(trimmedInput)) {
            // We do not know for sure that this is a complete standard expression, so we will consider it the
            // content of a FragmentExpression for legacy compatibility reasons.
            // We will only reach this point if the expression does not contain any Fragment Expressions expressed
            // as ~{...} (excluding parameters), nor the "::" fragment selector separator.
            // NOTE we are using the generic parseExpression() and not directly calling a parse method in the
            // FragmentExpression class because we want to take advantage of the expression cache.
            final FragmentExpression fragmentExpression =
                    (FragmentExpression) expressionParser.parseExpression(context, "~{" + trimmedInput + "}");

            final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                    FragmentExpression.createExecutedFragmentExpression(context, fragmentExpression);

            if (executedFragmentExpression.getFragmentSelectorExpressionResult() == null && executedFragmentExpression.getFragmentParameters() == null) {
                // We might be in the scenario that what we thought was a template name in fact was instead an expression
                // returning a Fragment itself, so we should simply return it
                final Object templateNameExpressionResult = executedFragmentExpression.getTemplateNameExpressionResult();
                if (templateNameExpressionResult != null) {
                    if (templateNameExpressionResult instanceof Fragment) {
                        return templateNameExpressionResult;
                    }
                    if (templateNameExpressionResult == NoOpToken.VALUE) {
                        return NoOpToken.VALUE;
                    }
                }
            }

            // Given this is a simple (originally unwrapped) fragment expression, we will consider the non-existence
            // of the fragment a failure. The reason we do this here instead of just waiting and seeing if we receive
            // a null and then failing is that, in order to receive such "null", the underlying resolution system would
            // have to execute a (potentially costly) resource.exists() call on the resolved resource.
            return FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

        }

        // If we reached this point, we know for sure this is a complete fragment expression, so we just parse it
        // as such and execute it

        final IStandardExpression fragmentExpression = expressionParser.parseExpression(context, trimmedInput);

        final Object fragmentExpressionResult;

        if (fragmentExpression != null && fragmentExpression instanceof FragmentExpression) {
            // This is not a complex expression but merely a FragmentExpression, so we can apply a shortcut
            // so that we don't require a "null" result for this expression if the template does not exist. That will
            // save a call to resource.exists() which might be costly.

            final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                    FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) fragmentExpression);

            fragmentExpressionResult =
                    FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

        } else {

            fragmentExpressionResult = fragmentExpression.execute(context);

        }

        if (fragmentExpressionResult == null || fragmentExpressionResult == NoOpToken.VALUE) {
            return fragmentExpressionResult;
        }

        if (!(fragmentExpressionResult instanceof Fragment)) {
            throw new TemplateProcessingException(
                    "Invalid fragment specification: \"" + input + "\": " +
                    "expression does not return a Fragment object");
        }

        return fragmentExpressionResult;

    }



    static boolean shouldBeWrappedAsFragmentExpression(final String input) {

        final int inputLen = input.length();
        if (inputLen > 2 && input.charAt(0) == FragmentExpression.SELECTOR && input.charAt(1) == '{') {
            // This input already starts as a fragment expression, so we are sure there is no need to wrap
            return false;
        }
        char c;
        int bracketLevel = 0;
        int paramLevel = 0;
        boolean inLiteral = false;
        int n = inputLen;
        int i = 0;
        while (n-- != 0) {

            c = input.charAt(i);

            if ((c >= 'a' && c <= 'z') || c == ' ') {
                // Fail fast - most characters will fall here
                i++;
                continue;
            }

            if (c == '\'') {
                inLiteral = !inLiteral;
            } else if (!inLiteral) {
                if (c == '{') {
                    bracketLevel++;
                } else if (c == '}') {
                    bracketLevel--;
                } else if (bracketLevel == 0) {
                    if (c == '(') {
                        paramLevel++;
                    } else if (c == ')') {
                        paramLevel--;
                    } else if (c == '=' && paramLevel == 1) {
                        // In exactly this disposition (paramLevel == 1, bracketLevel == 0), we know we are looking at
                        // a named parameter in a FragmentExpression content, so this has to be wrapped
                        return true;
                    } else if (c == FragmentExpression.SELECTOR && n != 0 && input.charAt(i + 1) == '{') {
                        // A fragment expression appears at level 0, so this should not be wrapped
                        return false;
                    } else if (c == ':' && n != 0 && input.charAt(i + 1) == ':') {
                        // A fragment selector ("::") has been found, so this is the content of a fragment expression and
                        // it should be wrapped
                        return true;
                    }
                }
            }

            i++;

        }

        // We haven't been able to determine any useful information about the type of expression this might be so,
        // in practice, this will mean we will consider it a fragment expression content for legacy compatibility
        // reasons (afterwards we will rectify if what we consider a "template name" happens to be a Fragment).
        return true;

    }



}

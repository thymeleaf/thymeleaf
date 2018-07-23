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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.engine.TemplateManager;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardDOMEventAttributeTagProcessor
        extends AbstractAttributeTagProcessor
        implements IAttributeDefinitionsAware {

    public static final int PRECEDENCE = 1000;

    // These attributes should be removed even if their value evaluates to null or empty string.
    // The reason why we don't let all these attributes to be processed by the default processor is that some other attribute
    // processors executing afterwards (e.g. th:field) might need attribute values already processed by these.
    public static final String[] ATTR_NAMES =
            new String[] {
                    "onabort",
                    "onafterprint",
                    "onbeforeprint",
                    "onbeforeunload",
                    "onblur",
                    "oncanplay",
                    "oncanplaythrough",
                    "onchange",
                    "onclick",
                    "oncontextmenu",
                    "ondblclick",
                    "ondrag",
                    "ondragend",
                    "ondragenter",
                    "ondragleave",
                    "ondragover",
                    "ondragstart",
                    "ondrop",
                    "ondurationchange",
                    "onemptied",
                    "onended",
                    "onerror",
                    "onfocus",
                    "onformchange",
                    "onforminput",
                    "onhashchange",
                    "oninput",
                    "oninvalid",
                    "onkeydown",
                    "onkeypress",
                    "onkeyup",
                    "onload",
                    "onloadeddata",
                    "onloadedmetadata",
                    "onloadstart",
                    "onmessage",
                    "onmousedown",
                    "onmousemove",
                    "onmouseout",
                    "onmouseover",
                    "onmouseup",
                    "onmousewheel",
                    "onoffline",
                    "ononline",
                    "onpause",
                    "onplay",
                    "onplaying",
                    "onpopstate",
                    "onprogress",
                    "onratechange",
                    "onreadystatechange",
                    "onredo",
                    "onreset",
                    "onresize",
                    "onscroll",
                    "onseeked",
                    "onseeking",
                    "onselect",
                    "onshow",
                    "onstalled",
                    "onstorage",
                    "onsubmit",
                    "onsuspend",
                    "ontimeupdate",
                    "onundo",
                    "onunload",
                    "onvolumechange",
                    "onwaiting"
            };


    private final String targetAttrCompleteName;

    private AttributeDefinition targetAttributeDefinition;




    public StandardDOMEventAttributeTagProcessor(final String dialectPrefix, final String attrName) {

        super(TemplateMode.HTML, dialectPrefix, null, false, attrName, true, PRECEDENCE, false);

        Validate.notNull(attrName, "Complete name of target attribute cannot be null");

        this.targetAttrCompleteName = attrName;

    }


    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), this.targetAttrCompleteName);
    }



    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final String newAttributeValue =
                EscapedAttributeUtils.escapeAttribute(getTemplateMode(), expressionResult == null ? null : expressionResult.toString());

        // These attributes are "removable if empty", so we simply remove the target attribute...
        if (newAttributeValue == null || newAttributeValue.length() == 0) {

            // We are removing the equivalent attribute name, without the prefix...
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);

        } else {

            // We are setting the equivalent attribute name, without the prefix...
            StandardProcessorUtils.replaceAttribute(
                    structureHandler, attributeName, this.targetAttributeDefinition, this.targetAttrCompleteName, (newAttributeValue == null ? "" : newAttributeValue));

        }

    }


    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final Object expressionResult;
        if (attributeValue != null) {

            IStandardExpression expression = null;
            try {
                expression = EngineEventUtils.computeAttributeExpression(context, tag, attributeName, attributeValue);
            } catch (final TemplateProcessingException e) {
                // The attribute value seems not to be a Thymeleaf Standard Expression. This is something that could
                // be perfectly OK, as these event handler attributes are allowed to contain a fragment of
                // JavaScript as their value, which will be processed as fragments in JAVASCRIPT template mode.
            }

            if (expression != null) {

                // This expression will be evaluated using restricted mode including the prohibition to evaluate
                // variable expressions that result in a String or in any objects that could be translated to
                // untrustable text literals.
                expressionResult = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED_FORBID_UNSAFE_EXP_RESULTS);

            } else {
                // The attribute value is not parseable as a Thymeleaf Standard Expression, so we will process it
                // as a JavaScript fragment, applying the same logic used in AbstractStandardInliner

                final IAttribute attribute = tag.getAttribute(attributeName);
                final TemplateManager templateManager = context.getConfiguration().getTemplateManager();

                final TemplateModel templateModel =
                        templateManager.parseString(
                                context.getTemplateData(), attributeValue,
                                attribute.getLine(), attribute.getCol(),
                                TemplateMode.JAVASCRIPT, true);

                final Writer stringWriter = new FastStringWriter(50);
                templateManager.process(templateModel, context, stringWriter);

                expressionResult = stringWriter.toString();

            }

        } else {
            expressionResult = null;
        }

        // If the result of this expression is NO-OP, there is nothing to execute
        if (expressionResult == NoOpToken.VALUE) {
            structureHandler.removeAttribute(attributeName);
            return;
        }

        doProcess(
                context, tag,
                attributeName, attributeValue,
                expressionResult, structureHandler);

    }

}
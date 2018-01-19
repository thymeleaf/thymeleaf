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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardConditionalFixedValueTagProcessor
            extends AbstractStandardExpressionAttributeTagProcessor
            implements IAttributeDefinitionsAware {


    public static final int PRECEDENCE = 1000;

    public static final String[] ATTR_NAMES =
            new String[] {
                    "async", "autofocus", "autoplay", "checked", "controls",
                    "declare", "default", "defer", "disabled", "formnovalidate",
                    "hidden", "ismap", "loop", "multiple", "novalidate",
                    "nowrap", "open", "pubdate", "readonly", "required",
                    "reversed", "selected", "scoped", "seamless"
            };

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private final String targetAttributeCompleteName;

    private AttributeDefinition targetAttributeDefinition;




    public StandardConditionalFixedValueTagProcessor(final String dialectPrefix, final String attrName) {

        super(TEMPLATE_MODE, dialectPrefix, attrName, PRECEDENCE, true, false);

        // We are discarding the prefix because that is exactly what we want: th:async -> async
        this.targetAttributeCompleteName = attrName;

    }




    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, this.targetAttributeCompleteName);
    }




    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        if (EvaluationUtils.evaluateAsBoolean(expressionResult)) {
            StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, this.targetAttributeCompleteName, this.targetAttributeCompleteName);
        } else {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
        }

    }



}

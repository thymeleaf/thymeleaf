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

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardConditionalFixedValueTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {


    public static final int PRECEDENCE = 1000;

    public static final String[] ATTR_NAMES =
            new String[] {
                    "async", "autofocus", "autoplay", "checked", "controls",
                    "declare", "default", "defer", "disabled", "formnovalidate",
                    "hidden", "ismap", "loop", "multiple", "novalidate",
                    "nowrap", "open", "pubdate", "readonly", "required",
                    "reversed", "selected", "scoped", "seamless"
            };




    public StandardConditionalFixedValueTagProcessor(final String dialectPrefix, final String attrName) {
        super(TemplateMode.HTML, dialectPrefix, attrName, PRECEDENCE, true);
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final String targetAttributeName = attributeName.getAttributeName(); // th:async -> async
        if (EvaluationUtils.evaluateAsBoolean(expressionResult)) {
            tag.getAttributes().setAttribute(targetAttributeName, targetAttributeName);
        } else {
            tag.getAttributes().removeAttribute(targetAttributeName);
        }

    }



}

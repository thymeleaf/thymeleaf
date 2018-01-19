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
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardConditionalVisibilityTagProcessor extends AbstractAttributeTagProcessor {

    /*
     * It is IMPORTANT THAT THIS CLASS DOES NOT EXTEND FROM AbstractStandardExpressionAttributeTagProcessor because
     * such thing would mean that the expression would be evaluated in the parent class, and this would affect
     * the th:case attribute processor, because no shortcut would be possible: once one "th:case" evaluates to true,
     * the rest of th:case in the same th:switch should not be evaluated AT ALL.
     */


    protected AbstractStandardConditionalVisibilityTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final int precedence) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
    }



    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final boolean visible = isVisible(context, tag, attributeName, attributeValue);

        if (!visible) {
            structureHandler.removeElement();
        }

    }


    protected abstract boolean isVisible(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue);


}

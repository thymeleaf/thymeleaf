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

import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IElementStructureHandler;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public abstract class AbstractStandardAttributeModifierTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {


    private final boolean removeIfEmpty;
    private final String targetAttrName;


    protected AbstractStandardAttributeModifierTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName,
            final int precedence, final boolean removeIfEmpty) {
        this(templateMode, dialectPrefix, attrName, null, precedence, removeIfEmpty);
    }

    protected AbstractStandardAttributeModifierTagProcessor(
            final TemplateMode templateMode, final String dialectPrefix, final String attrName, final String targetAttrName,
            final int precedence, final boolean removeIfEmpty) {
        super(templateMode, dialectPrefix, attrName, precedence);
        this.targetAttrName = targetAttrName;
        this.removeIfEmpty = removeIfEmpty;
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue, final Object expressionResult,
            final IElementStructureHandler structureHandler) {

        final String newAttributeName =
                (this.targetAttrName == null? attributeName.getAttributeName() : this.targetAttrName);
        final String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

        // These attributes might be "removable if empty", in which case we would simply remove the target attribute...
        if (this.removeIfEmpty && (newAttributeValue == null || newAttributeValue.length() == 0)) {
            // We are removing the equivalent attribute name, without the prefix...
            tag.getAttributes().removeAttribute(newAttributeName);
        } else {
            // We are setting the equivalent attribute name, without the prefix...
            tag.getAttributes().setAttribute(newAttributeName, (newAttributeValue == null? "" : newAttributeValue));
        }

        tag.getAttributes().removeAttribute(attributeName);

    }


}

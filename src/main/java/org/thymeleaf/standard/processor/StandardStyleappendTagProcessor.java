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
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class StandardStyleappendTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    public static final int PRECEDENCE = 1100;
    public static final String ATTR_NAME = "styleappend";
    public static final String TARGET_ATTR_NAME = "style";



    public StandardStyleappendTagProcessor(final IProcessorDialect dialect, final String dialectPrefix) {
        super(dialect, TemplateMode.HTML, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
    }



    @Override
    protected final void doProcess(
            final ITemplateProcessingContext processingContext,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final String attributeTemplateName, final int attributeLine, final int attributeCol,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

        // If we are not adding anything, we'll just leave it untouched
        if (newAttributeValue != null && newAttributeValue.length() > 0) {

            if (!tag.getAttributes().hasAttribute(TARGET_ATTR_NAME) ||
                    tag.getAttributes().getValue(TARGET_ATTR_NAME).length() == 0) {
                // No previous value, so it's just a replacement
                tag.getAttributes().setAttribute(TARGET_ATTR_NAME, newAttributeValue);
            } else {
                final String currentValue = tag.getAttributes().getValue(TARGET_ATTR_NAME);
                tag.getAttributes().setAttribute(TARGET_ATTR_NAME, currentValue + ' ' + newAttributeValue);
            }

        }

    }


}

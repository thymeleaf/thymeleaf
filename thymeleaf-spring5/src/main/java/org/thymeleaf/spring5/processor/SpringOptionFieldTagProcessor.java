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
package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.spring5.util.SpringSelectedValueComparator;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.3
 *
 */
public final class SpringOptionFieldTagProcessor extends AbstractSpringFieldTagProcessor {




    public SpringOptionFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, OPTION_TAG_NAME, null, null, true);
    }



    @Override
    protected void doProcess(final ITemplateContext context,
                             final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final IThymeleafBindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String value = tag.getAttributeValue(this.valueAttributeDefinition.getAttributeName());
        if (value == null) {
            throw new TemplateProcessingException(
                    "Attribute \"value\" is required in \"option\" tags");
        }

        final boolean selected =
                SpringSelectedValueComparator.isSelected(bindStatus, HtmlEscape.unescapeHtml(value));

        StandardProcessorUtils.setAttribute(
                structureHandler,
                this.valueAttributeDefinition, VALUE_ATTR_NAME,
                RequestDataValueProcessorUtils.processFormFieldValue(context, name, value, "option"));

        if (selected) {
            StandardProcessorUtils.setAttribute(structureHandler, this.selectedAttributeDefinition, SELECTED_ATTR_NAME, SELECTED_ATTR_NAME);
        } else {
            structureHandler.removeAttribute(this.selectedAttributeDefinition.getAttributeName());
        }

    }


}

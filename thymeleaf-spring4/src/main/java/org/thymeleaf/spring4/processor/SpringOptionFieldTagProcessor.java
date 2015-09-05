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
package org.thymeleaf.spring4.processor;

import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.SelectedValueComparatorWrapper;
import org.thymeleaf.context.ITemplateProcessingContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.unbescape.html.HtmlEscape;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringOptionFieldTagProcessor extends AbstractSpringFieldTagProcessor {




    public SpringOptionFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, OPTION_TAG_NAME, null, null, true);
    }



    @Override
    protected void doProcess(final ITemplateProcessingContext processingContext, final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final String attributeTemplateName, final int attributeLine, final int attributeCol,
                             final BindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String value = tag.getAttributes().getValue("value");
        if (value == null) {
            throw new TemplateProcessingException(
                    "Attribute \"value\" is required in \"option\" tags");
        }

        final boolean selected =
                SelectedValueComparatorWrapper.isSelected(bindStatus, HtmlEscape.unescapeHtml(value));

        tag.getAttributes().setAttribute(
                "value",
                RequestDataValueProcessorUtils.processFormFieldValue(processingContext, name, value, "option"));

        if (selected) {
            tag.getAttributes().setAttribute("selected", "selected");
        } else {
            tag.getAttributes().removeAttribute("selected");
        }

    }


}

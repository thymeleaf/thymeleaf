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
import org.springframework.web.servlet.tags.form.ValueFormatterWrapper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IElementAttributes;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.util.StandardProcessorUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public final class SpringTextareaFieldTagProcessor extends AbstractSpringFieldTagProcessor {



    public SpringTextareaFieldTagProcessor(final String dialectPrefix) {
        super(dialectPrefix, TEXTAREA_TAG_NAME, null, null, true);
    }



    @Override
    protected void doProcess(final ITemplateContext context,
                             final IProcessableElementTag tag,
                             final AttributeName attributeName, final String attributeValue,
                             final BindStatus bindStatus, final IElementTagStructureHandler structureHandler) {

        String name = bindStatus.getExpression();
        name = (name == null? "" : name);

        final String id = computeId(context, tag, name, false);

        final String value = ValueFormatterWrapper.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), true);

        final String processedValue =
                RequestDataValueProcessorUtils.processFormFieldValue(context, name, value, "textarea");

        final IElementAttributes attributes = tag.getAttributes();

        StandardProcessorUtils.setAttribute(attributes, this.idAttributeDefinition, ID_ATTR_NAME, id); // No need to escape: this comes from an existing 'id' or from a token
        StandardProcessorUtils.setAttribute(attributes, this.nameAttributeDefinition, NAME_ATTR_NAME, name); // No need to escape: this is a java-valid token

        structureHandler.setBody((processedValue == null? "" : processedValue), false);

    }


}

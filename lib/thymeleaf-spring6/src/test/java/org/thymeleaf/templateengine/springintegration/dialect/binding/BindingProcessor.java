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
package org.thymeleaf.templateengine.springintegration.dialect.binding;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring6.context.IThymeleafBindStatus;
import org.thymeleaf.spring6.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

public class BindingProcessor extends AbstractAttributeTagProcessor {

    private static final int PRECEDENCE = 1000;


    public BindingProcessor(final String prefix) {
        super(TemplateMode.HTML, prefix, null, false, "haserror", true, PRECEDENCE, true);
    }


    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName,
            final String attributeValue, final IElementTagStructureHandler structureHandler) {


        final IThymeleafBindStatus bindStatus =
                FieldUtils.getBindStatus(context, true, attributeValue);

        if (bindStatus.isError()) {
            structureHandler.setAttribute("haserror", "true");
        } else {
            structureHandler.setAttribute("haserror", "false");
        }

    }

}
